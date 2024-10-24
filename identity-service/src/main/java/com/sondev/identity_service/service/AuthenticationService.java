package com.sondev.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sondev.identity_service.dto.request.AuthenticationRequest;
import com.sondev.identity_service.dto.request.IntrospectRequest;
import com.sondev.identity_service.dto.request.LogoutRequest;
import com.sondev.identity_service.dto.request.RefreshTokenRequest;
import com.sondev.identity_service.dto.response.AuthenticationResponse;
import com.sondev.identity_service.dto.response.InstrospectResponse;
import com.sondev.identity_service.dto.response.RefreshTokenResponse;
import com.sondev.identity_service.entity.InvalidatedToken;
import com.sondev.identity_service.entity.User;
import com.sondev.identity_service.exception.AppException;
import com.sondev.identity_service.exception.ErrorCode;
import com.sondev.identity_service.repository.InvalidatedTokenRepository;
import com.sondev.identity_service.repository.UserRepository;

import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}") // Đọc biến từ file application.yaml
    protected String signerKey;

    @Value("${jwt.valid-duration}")
    protected long validDuration;

    @Value("${jwt.refreshable-duration}")
    protected long refreshDuration;

    public AuthenticationService(UserRepository userRepository, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.userRepository = userRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("SignerKey: {}", signerKey);

        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);
        // spotless:off
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
        // spotless:on
    }

    public InstrospectResponse instrospect(IntrospectRequest request) throws JOSEException, ParseException {
        // chuẩn bị: get token từ trong request
        String token = request.getToken();
        boolean isValid = true;

        /**
         *  tách ra hàm xử lý riêng để dùng chung.
         *  Nếu quá trình decode mà token không hợp lệ, hết token hết hạn thì có
         *  1 Exception xảy ra.
         * */
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        // spotless:off
        return InstrospectResponse.builder()
                .valid(isValid)
                .build();
        // spotless:on
    }

    /**
     * @General: Create Token
     * @Description: Tạo ra thông tin token gồm 3 thành phần:
     * @- header: chứa thông tin thuật toán: SHA512
     * @- payload: thông tin User gồm: subject, jssuer, issueTime, expirationTime, jwtID và custome claim (scope)
     * @- signature: chứ thông tin chữ ký.
     **/
    private String generateToken(User user) {
        // 1. Header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        // 2.1 Claims Set
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("IdentityService.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString()) // jwtID: khi User chủ động logout thì lưu jwtID này vào DB
                .claim("scope", buildScope(user))
                .build();

        // 2. Payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // 3. jwsObject = header + payload
        JWSObject jwsObject = new JWSObject(header, payload);
        // 4. sign = (jwsObject + SIGNER_KEY) ==> hash SHA512
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize(); // ->> to String
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @General: Xử lý khi Client logout.
     * @Description: Từ token trong request gửi lên, tiến hành decode token để lấy thông tin User trong token. Từ đó lấy ra jit và expiryTime.
     * Sau đó tạo ra đối tượng {@code InvalidatedToken} với jit, và expiryTime.
     * Cuối cùng lưu nó vào database.
     * @Exception: {@code ParseException}, {@code JOSEException}
     */
    public void logout(LogoutRequest request) throws ParseException, JOSEException {

        try {
            String token = request.getToken();
            SignedJWT signToken = verifyToken(token, true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException ae) {
            log.info("Token already expired");
        }
    }

    /**
     * @General: Tạo token mới với thông tin từ token cũ.
     * @Description: Các bước tiến hành:
     * @- verify token.
     * @- Logout bằng cách lưu token vào bảng InvalidatedToken.
     * @- Lấy ra User bằng userName có trong token.
     * @- Tạo ra token mới với thông tin User vừa lấy ra.
     * @Exception: {@code ParseException}, {@code JOSEException}
     */
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        SignedJWT signJWT = verifyToken(token, true);

        // Tiến hành logout bằng cách lưu token vào tbl_invalidatedToken
        String jit = signJWT.getJWTClaimsSet().getJWTID();
        var expirityTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expirityTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        // get userName từ signJWT sau đó get User trong db bằng userName.
        String userName = signJWT.getJWTClaimsSet().getSubject();
        User user =
                userRepository.findByUsername(userName).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        // Tạo token mới từ user
        String newToken = generateToken(user);

        return RefreshTokenResponse.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }

    /**
     * @General: Giả mã token thành thông tin chi tiết của token.
     * @Description: Nếu token hết hạn, hoặc không hợp lệ thì throw {@code AppException} với {@code ErrorCode.UNAUTHENTICATED}
     * @Exception: {@code AppException}
     */
    private SignedJWT verifyToken(String token, boolean isRefesh) throws JOSEException, ParseException {
        // chuẩn bị: băm chuỗi mã hóa và hash nó với MACVerifier
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        // 1. Parse token sang chuỗi mã hóa
        SignedJWT signedJWT = SignedJWT.parse(token);

        // 2. Tiến hành verify: so sánh 2 chuỗi mã hóa có trùng khớp hay không.
        boolean verified = signedJWT.verify(verifier);

        // 3. lấy ra thời gian hết hạn
        /**
         * isRefresh là biến cờ
         * isRefresh = false thì expiryTime được lấy trong token
         * isRefresh = true thì expiryTime đươc tính bằng cách lấy IssueTime + REFRESH_DURATION
         * */
        Date expiryTime = (isRefesh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(refreshDuration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        // 4. Kiểm tra xem token có hợp lệ và còn thời gian hay không.
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        /** 5. Bổ sung thêm 1 bước kiểm trả token có nằm trong table_invalidated_token hay không.
         * Bằng cách lấy ra jti và kiểm trả nó có tồn tại hay không.
         */
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        if (invalidatedTokenRepository.existsById(jti)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // 6. Trả về thông tin của User sau khi được decode từ token.
        return signedJWT;
    }

    /**
     * @General: thêm Role và Permission và Scope.
     * @Description: Nếu là {@code Role} thì thêm prefix "ROLE_" ==> ex: ROLE_ADMIN.
     * Nếu là {@code Permission} thì giữ nguyên ==> ex: CREATE_POST, APPROVE_POST
     * @Note: Tất cả Role và Permission đều được đặt trong payload của token. Mà token được gắn vào trong header của mỗi request
     * . Header giới hạn 4kb, cho nên cần phải cân nhắc khi sử dung.
     */
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                }
            });
        }
        return stringJoiner.toString();
    }
}

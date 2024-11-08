package org.sondev.identity.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
/** cần có để có thể kích hoạt Authentication Method. **/
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(
                        HttpMethod.POST,
                        "/users/registration",
                        "/auth/log-in",
                        "/auth/introspect",
                        "/auth/logout",
                        "/auth/refresh")
                .permitAll()

                // .requestMatchers(HttpMethod.GET, "/users")
                /** Ngoài sử dụng hasAnyAuthority có thể sử dụng hasRole
                 * hasAnyAuthority("ROLE_ADMIN")
                 * hasRole(Role.ADMIN.name()) ==> không cần cung cấp prefix (ROLE_)
                 **/
                // .hasRole(Role.ADMIN.name())

                .anyRequest()
                .authenticated());

        httpSecurity.oauth2ResourceServer(oAuth2 -> oAuth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        /** decode jwt **/
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                /** Custom behavior **/

                /** Catch Exception hoặc điều hướng người dùng đi đâu nếu authentication thất bại */
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        httpSecurity.csrf(AbstractHttpConfigurer::disable); // sort hand of lambda method
        /* httpSecurity.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable()); */
        return httpSecurity.build();
    }

    /**
     * @General: Chịu trách nhiệm cho việc verify token
     * @Description: Mỗi khi client gửi request lên, jwtDecoder sẽ tiến hành giải mã token.
     */
    /*
    	@Bean
    	JwtDecoder jwtDecoder() {
    		SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");

    		return NimbusJwtDecoder
    				.withSecretKey(secretKeySpec)
    				.macAlgorithm(MacAlgorithm.HS512)
    				.build();
    	}
    */

    /**
     * @Genaral: Custome behavior
     * @Description: mặc định SCOPE_ADMIN ==> ROLE_ADMIN
     * Khi có sự xuất hiện của permission, để phân biệt thì role có prefix "ROLE_" được xử lý ở hàm {@code buildScope()} nên ở đây prefix = "".
     **/
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix(""); // Sửa lại từ "ROLE_" ==> "".

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}

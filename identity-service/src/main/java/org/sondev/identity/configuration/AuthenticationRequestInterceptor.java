package org.sondev.identity.configuration;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
/**
 * Nếu được đánh dấu là 1 bean  bằng @Component thì bất kì request của feignClient nào cũng apply Interceptor này.
 * Tuy nhiên trong thực tế chúng ta k chỉ request tới 1 service mà có thể có nhiều server internal khác nhau.
 * Hoặc request ra những service bên ngoài.
 * */
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    /**
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        /* Lấy ra header từ trong request */
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
        log.info("Header: {}", authHeader);

        if (StringUtils.hasText(authHeader)) {
            requestTemplate.header("Authorization", authHeader);
        }

        /*  Truyền header đi kèm với request */
    }
}

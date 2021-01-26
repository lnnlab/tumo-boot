package cn.tycoding.boot.common.auth.filter;

import cn.tycoding.boot.common.auth.constant.ApiConstant;
import cn.tycoding.boot.common.auth.utils.SecurityUtil;
import cn.tycoding.boot.common.core.constant.CacheConstant;
import cn.tycoding.boot.common.log.exception.ServiceException;
import cn.tycoding.boot.common.redis.config.TumoRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码过滤器
 *
 * @author tycoding
 * @since 2021/1/24
 */
@Component
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter {

    private final TumoRedis tumoRedis;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (ApiConstant.API_OAUTH_TOKEN.equals(request.getRequestURI())) {
            String headerKey = request.getHeader(SecurityUtil.CAPTCHA_HEADER_KEY);
            String code = ServletRequestUtils.getStringParameter(request, SecurityUtil.CAPTCHA_FORM_KEY);
            String redisCode = (String) tumoRedis.get(CacheConstant.CAPTCHA_REDIS_KEY + headerKey);
            if (code == null || !code.toLowerCase().equals(redisCode)) {
                throw new ServiceException(SecurityUtil.CAPTCHA_ERROR_INFO);
            }
        }
        chain.doFilter(request, response);
    }
}

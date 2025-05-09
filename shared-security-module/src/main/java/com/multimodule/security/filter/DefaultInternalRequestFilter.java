package com.multimodule.security.filter;

import com.multimodule.security.context.GatewayAuthenticationContext;
import com.multimodule.security.dto.user.AuthenticatedUserDto;
import com.multimodule.security.exceptions.convertObject.InvalidConvertRoleException;
import com.multimodule.security.exceptions.gateway.HeaderDoesntContainsCredentialsException;
import com.multimodule.security.roles.Roles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default security filter that extracts authentication details from HTTP headers
 * (typically added by API Gateway) and populates the Spring Security context.
 *
 * <p>This filter checks for internal headers like user ID, username, and roles.
 * If all required headers are present and valid, an authenticated security context is created.
 * If all headers are missing, the request is treated as anonymous and passed through.
 * If some headers are missing, an exception is thrown.
 *
 * <p>Intended for use in internal microservice communication where security headers are
 * propagated by a gateway or external authentication layer.
 *
 * <p>This bean is conditional and will only load if no other bean of the same type is present.
 *
 * @author Ihor Murashko
 */
@Slf4j
public class DefaultInternalRequestFilter extends BasicOnePerRequestFilter {

    /**
     * Attempts to extract authentication data from the incoming request and set the
     * Spring Security context accordingly.
     *
     * <p>Expected headers:
     * <ul>
     *     <li>X-User-Id – unique user identifier</li>
     *     <li>X-Username – user's login/username</li>
     *     <li>X-Roles – comma-separated list of roles (as strings)</li>
     * </ul>
     *
     * <p>If no headers are found, the request is treated as anonymous.
     * If some required headers are missing, throws {@link HeaderDoesntContainsCredentialsException}.
     * If an invalid role is encountered, throws {@link InvalidConvertRoleException}.
     *
     * @param request     incoming HTTP request
     * @param response    outgoing HTTP response
     * @param filterChain the filter chain
     * @throws ServletException in case of general servlet failure
     * @throws IOException      in case of I/O error
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            GatewayAuthenticationContext gatewayAuthenticationContext = new GatewayAuthenticationContext(request);

            Optional<String> userIdOpt = gatewayAuthenticationContext.getUserId();
            Optional<String> usernameOpt = gatewayAuthenticationContext.getUsername();
            List<String> roles = gatewayAuthenticationContext.getRoles();

            // Если все заголовки отсутствуют — значит, пользователь анонимный
            if (userIdOpt.isEmpty() && usernameOpt.isEmpty() && roles.isEmpty()) {
                filterChain.doFilter(request, response); // Пропускаем, не устанавливая аутентификацию
                return;
            }

            // Если какие-то заголовки есть, а какие-то нет — ошибка
            if (userIdOpt.isEmpty() || usernameOpt.isEmpty()) {
                throw new HeaderDoesntContainsCredentialsException("Required auth headers are missing");
            }

            Set<GrantedAuthority> userRoles = roles.stream()
                    .map(role -> {
                        try {
                            return Roles.valueOf(role);
                        } catch (IllegalArgumentException e) {
                            log.error("Invalid role: {}", role);
                            throw new InvalidConvertRoleException(role);
                        }
                    })
                    .collect(Collectors.toSet());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    new AuthenticatedUserDto(userIdOpt.get(), usernameOpt.get(), userRoles),
                    null,
                    userRoles
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            log.info("Authenticated user {}", usernameOpt.get());


            filterChain.doFilter(request, response);

        } finally {
            SecurityContextHolder.clearContext();
        }
    }

}

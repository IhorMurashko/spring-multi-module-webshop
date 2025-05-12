package com.multimodule.security.config;

import com.multimodule.security.filter.DefaultInternalRequestFilter;
import com.multimodule.security.filter.DefaultJwtTokenFilter;
import com.multimodule.security.jwt.access.JwtAccessDeniedHandler;
import com.multimodule.security.jwt.access.JwtAuthEntryPoint;
import com.multimodule.security.jwt.tokenManagement.managment.DefaultJwtTokenManager;
import com.multimodule.security.jwt.tokenManagement.provider.BasicJwtTokenProvider;
import com.multimodule.security.jwt.tokenManagement.provider.DefaultJwtTokenProvider;
import com.multimodule.security.revokedTokensService.BasicRevokedTokenService;
import com.multimodule.security.revokedTokensService.DelegationTokenRevokedService;
import com.multimodule.security.revokedTokensService.TokenCacheService;
import com.multimodule.security.revokedTokensService.grpcRevokedTokenService.DefaultGrpcRevokedTokenService;
import com.multimodule.security.revokedTokensService.grpcRevokedTokenService.DelegationGrpcRevokedTokenService;
import com.multimodule.security.revokedTokensService.redisCache.DefaultRedisRevokedTokenService;
import com.multimodule.security.userDetails.JwtUserPrincipal;
import com.multimodule.security.userDetailsService.DelegatingUserDetailsService;
import com.multimodule.security.userDetailsService.TypedUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @ConditionalOnMissingBean
    public DefaultInternalRequestFilter defaultInternalRequestFilter() {
        return new DefaultInternalRequestFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultJwtTokenFilter defaultJwtTokenFilter(
            BasicJwtTokenProvider jwtTokenProvider,
            BasicRevokedTokenService basicRevokedTokenService
    ) {
        return new DefaultJwtTokenFilter(jwtTokenProvider, basicRevokedTokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(TypedUserDetailsService.class)
    @ConditionalOnBean(TypedUserDetailsService.class)
    public DefaultJwtTokenManager defaultJwtTokenManager(BasicJwtTokenProvider jwtTokenProvider,
                                                         TypedUserDetailsService<? extends UserDetailsService> typedUserDetailsService) {
        return new DefaultJwtTokenManager(jwtTokenProvider, typedUserDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultJwtTokenProvider jwtTokenProvider() {
        return new DefaultJwtTokenProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public BasicRevokedTokenService basicRevokedTokenService(TokenCacheService tokenCacheService) {
        return new DelegationTokenRevokedService(tokenCacheService);
    }

    @Bean
    //Создаёт бин только если уже существует
    // бин StringRedisTemplate (например,
    // созданный где-то в RedisConfig)
    @ConditionalOnBean(StringRedisTemplate.class)
//    Активирует бин только если класс StringRedisTemplate
//    есть в classpath (например, если spring-boot-starter-data-redis
//    подключен)
    @ConditionalOnClass(StringRedisTemplate.class)
//    Позволяет переопределить бин извне, если необходимо
    @ConditionalOnMissingBean
    public TokenCacheService revokedTokenService(
            StringRedisTemplate stringRedisTemplate,
            BasicJwtTokenProvider jwtTokenProvider
    ) {
        return new DefaultRedisRevokedTokenService(stringRedisTemplate, jwtTokenProvider);
    }

    @Bean
    @ConditionalOnBean(TypedUserDetailsService.class)
    @ConditionalOnMissingBean
    public DelegatingUserDetailsService userDetailsService(TypedUserDetailsService<JwtUserPrincipal> typedUserDetailsService) {
        return new DelegatingUserDetailsService(typedUserDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DelegationGrpcRevokedTokenService.class)
    public DefaultGrpcRevokedTokenService defaultGrpcRevokedTokenService(DelegationGrpcRevokedTokenService delegationGrpcRevokedTokenService) {

        return new DefaultGrpcRevokedTokenService(delegationGrpcRevokedTokenService);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthEntryPoint jwtAuthEntryPoint() {
        return new JwtAuthEntryPoint();
    }


}
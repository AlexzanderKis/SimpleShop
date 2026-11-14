package org.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
         Конфигурация Spring Security
         Бизнес-логики не содержит - только правила безопасности
         <p>
         Определяет правила доступа к веб-страницам и REST-эндпоинтам:
         - какие URL публичные (каталог, логин, Swagger),
         - какие требуют аутентификации (корзина, заказы, профиль),
         - какие доступны только администратору (создание/удаление товаров, смена статуса заказа)

         настраивает:
         - form-login для HTML-страниц,
         - HTTP Basic для REST-клиентов и Swagger,
         - BCrypt для хеширования паролей,
         - отключение CSRF для /api/** (чтобы можно было тестировать через Postman/Swagger).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /**
             Кодировщик паролей
             Используется BCrypt потому что:
             - автоматически генерирует последовательность символов для каждого пароля,
             - устойчив к brute-force (можно настраивать "strength/cost"),
             - рекомендован Spring Security как безопасный дефолтный
             <p>
             При смене алгоритма существующие пароли перестанут валидироваться
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(7); // пароли будут храниться в зашифрованном виде через BCrypt
    }

    // бин для программной аутентификации (если понадобится входить через код)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    /**
             Основная цепочка фильтров безопасности.
             Определяет:
             - публичные URL (каталог, логин, статика, Swagger),
             - URL только для ADMIN (создание/удаление товаров, смена статуса заказа),
             - URL только для аутентифицированных (корзина, заказы, профиль)
             Настраивает form-login для веб Thymeleaf-страниц и HTTP Basic для REST/Swagger
             <p>
             CSRF отключён для /api/** - решение для удобства тестирования REST
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(csrf -> csrf
                        // CSRF отключён для /api/**, чтобы можно было тестировать REST через Postman/Swagger без токена
                        .ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth

                        // Публичные статик ресурсы
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // Публичные swagger & openAPI docs
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Публичные веб страницы
                        .requestMatchers("/", "/catalog/**", "/products/**", "/login", "/register").permitAll()

                        // Публичные REST эндпоинты
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Админские эндпоинты
                        .requestMatchers(HttpMethod.POST,"/api/v1/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/orders/{orderId}/status").hasRole("ADMIN")

                        // Только для аутентифицированных пользователей (Cart, Order checkout, User Profile)
                        .requestMatchers("/cart/**", "/orders/**", "/checkout/**").authenticated()
                        .requestMatchers("/api/v1/cart/**", "/api/v1/orders/**", "/api/v1/users/**").authenticated()

                        .anyRequest().authenticated())

                // настройка входа через веб HTML-форму (для Thymeleaf-страниц)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/catalog",true)
                        .failureUrl("/login?error=true")
                        .permitAll())

                // включает HTTP basic аутентификацию для REST-клиентов и Swagger
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }
}

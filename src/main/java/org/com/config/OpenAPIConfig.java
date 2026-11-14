package org.com.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
         Класс конфигурации OpenAPI/Swagger
         Отвечает только за формирование метаданных для интерактивной документации API
         Бизнес-логики не содержит. Доступна по адресу /swagger-ui.html
         <p>
         Здесь задаются: заголовок, версия, описание API, список серверов и схема аутентификации
 */
@Configuration
public class OpenAPIConfig {
    /**
             Формирует метаданные OpenAPI-документации
             Схема аутентификации basicAuth соответствует SecurityConfig.httpBasic()
             через кнопку Authorize в Swagger можно ввести email/пароль и тестировать защищённые эндпоинты
     */
    @Bean
    public OpenAPI custimerOpenAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("SimpleShop REST API")
                        .version("1.0.0")
                        .description("""
                                Полноценный REST API интернет-магазина: аутентификация, каталог товаров, корзина покупателя, управление заказами и оформление.
                                Реализовано на Spring Boot 4.1.1, PostgreSQL-17, JPA, Flyway, Spring Security.""")
                        .contact(new Contact()
                                .name("SimpleShop DevTeam")
                                .email("alexzanderkis@gmail.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io/"))).servers(List.of(
                        new Server().url("http://localhost:8080").description("Локальный сервер разработки"),
                        new Server().url("http://localhost:8080/api/v1").description("API префикс v1")
                )).addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Аутентификация через HTTP Basic (email и пароль пользователя)")));
    }
}
package com.mewebstudio.javaspringbootboilerplate.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.TimeZone;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.SECURITY_SCHEME_NAME;

@Configuration
public class AppConfig {
    
    /**
     * This function creates a LocaleResolver bean. It sets the default locale and timezone for the application. 
     * The default locale is obtained from the app.default-locale configuration property, 
     * and the default timezone is obtained from app.default-timezone configuration property.
     * @param defaultLocale String
     * @return LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver(@Value("${app.default-locale:en}") final String defaultLocale,
                                         @Value("${app.default-timezone:UTC}") final String defaultTimezone) {
        AcceptHeaderLocaleResolver localResolver = new AcceptHeaderLocaleResolver();
        localResolver.setDefaultLocale(new Locale.Builder().setLanguage(defaultLocale).build());
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimezone));

        return localResolver;
    }

    /**
     * This function creates a ModelResolver bean for Swagger (OpenAPI) documentation. 
     * It configures the ObjectMapper used by Swagger for JSON serialization and deserialization to use lower camel case for property names.
     * @param objectMapper ObjectMapper
     * @return ModelResolver
     */
    @Bean
    public ModelResolver modelResolver(final ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE));
    }

    /**
     *  This function creates a PasswordEncoder bean that uses BCryptPasswordEncoder. 
     * This is useful for password encoding in security contexts, such as for user authentication.
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder delegatingPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This function creates an OpenAPI bean. 
     * It sets up the OpenAPI documentation with custom information like the application's name, description, version, 
     * terms of service URL, and license. It also configures the security scheme used for API authentication and authorization.
     *
     * @param title       String
     * @param description String
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name}") final String title,
                                 @Value("${spring.application.description}") final String description) {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                )
            )
            .info(new Info().title(title).version("1.0").description(description)
                .termsOfService("https://www.mewebstudio.com")
                .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }

    /**
     * This function creates an ApplicationEventMulticaster bean.
     * It allows for asynchronous handling of application events using SimpleAsyncTaskExecutor
     *
     * @return ApplicationEventMulticaster
     */
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return eventMulticaster;
    }

    /**
     *This function creates a SpringTemplateEngine bean for processing templates, particularly HTML templates. 
     * It configures the template engine with a template resolver for resolving templates
     *
     * @return SpringTemplateEngine
     */
    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());

        return templateEngine;
    }

    /**
     *This function creates a SpringResourceTemplateResolver bean.
     *It sets up the template resolver with properties like prefix, suffix, template mode, and character encoding for resolving HTML templates
     *
     * @return SpringResourceTemplateResolver
     */
    @Bean
    public SpringResourceTemplateResolver htmlTemplateResolver() {
        SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
        emailTemplateResolver.setPrefix("classpath:/templates/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return emailTemplateResolver;
    }

    /**
     * This function creates an ObjectMapper bean. It configures Jackson's ObjectMapper for JSON processing.
     * This includes settings like failing on unknown properties, using lower camel case for property names, 
     * registering the JavaTimeModule for date-time types, and setting the serialization inclusion policy
     *
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }
}

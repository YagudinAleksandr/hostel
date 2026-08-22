package uz.backend.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Worker не публикует наружу API — только actuator внутри контура.
 * Стартер oauth2-client тянет за собой spring-security, который иначе
 * закрыл бы actuator basic-аутентификацией со сгенерированным паролем.
 *
 * @author Aleksandr Yagudin
 */
@Configuration
@EnableWebSecurity
public class WorkerSecurityConfig {

    @Bean
    public SecurityFilterChain workerFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}

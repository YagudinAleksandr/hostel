package uz.backend.manager;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Точка входа manager'а — сервиса, который отвечает за внешний REST
 * и хранит состояние процессов, задач и попыток их выполнения.
 */
@SpringBootApplication
public class ManagerApplication {

    public static final String APP_NAME = "manager";

    public static void main(String[] args) {
        new SpringApplicationBuilder(ManagerApplication.class)
                .properties("spring.config.name=" + APP_NAME)
                .run(args);
    }
}

package uz.backend.worker;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Точка входа worker-а — сервиса, который разбирает external tasks Camunda.
 */
@SpringBootApplication
public class WorkerApplication {

    public static final String APP_NAME = "worker";

    public static void main(String[] args) {
        new SpringApplicationBuilder(WorkerApplication.class)
                .properties("spring.config.name=" + APP_NAME)
                .run(args);
    }
}

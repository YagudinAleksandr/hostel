package uz.backend.database;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Подключает сущности и репозитории к приложению-потребителю
 * @author Aleksandr Yagudin
 */
@AutoConfiguration
@EntityScan(basePackages = "uz.backend.database.entity")
@EnableJpaRepositories(basePackages = "uz.backend.database.repositories")
public class DatabaseAutoConfiguration {
}

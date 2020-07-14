package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.persistence.ConfigRepositories;
import de.echsecutables.rollandbuild.persistence.GamePlayRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RollAndBuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(RollAndBuildApplication.class, args);
    }

    @Bean
    public GamePlayRepositories getRepositories() {
        return new GamePlayRepositories();
    }

    @Bean
    public ConfigRepositories getConfigRepositories() {
        return new ConfigRepositories();
    }
}

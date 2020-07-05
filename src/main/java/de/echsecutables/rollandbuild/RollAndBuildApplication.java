package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.persistence.Repositories;
import de.echsecutables.rollandbuild.persistence.RepositoryWrapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RollAndBuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(RollAndBuildApplication.class, args);
    }

    @Bean
    public RepositoryWrapper getRepositories() {
        return new Repositories();
    }
}

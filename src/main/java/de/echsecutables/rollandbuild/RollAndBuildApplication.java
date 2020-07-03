package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.persistence.Repositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RollAndBuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(RollAndBuildApplication.class, args);
    }

    @Bean
    public Repositories getRepositories() {
        return new Repositories();
    }
}

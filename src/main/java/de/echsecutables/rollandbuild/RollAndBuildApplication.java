package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.persistence.GamePlayRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RollAndBuildApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollAndBuildApplication.class);

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(RollAndBuildApplication.class, args);
        Integer port = context.getEnvironment().getProperty("server.port", Integer.class, 8080);
        String contextPath = context.getEnvironment().getProperty("server.servlet.context-path", String.class, "");

        LOGGER.info("Application running. Local access URL: http://localhost:{}{}", port, contextPath);
        LOGGER.info("Swagger local access URL: http://localhost:{}{}/swagger-ui.html", port, contextPath);
    }

    @Bean
    public GamePlayRepositories getRepositories() {
        return new GamePlayRepositories();
    }

}

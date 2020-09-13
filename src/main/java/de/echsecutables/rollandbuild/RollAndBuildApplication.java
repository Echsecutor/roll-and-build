package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.persistence.PlayerRepositoryImplementation;
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
        String h2url = context.getEnvironment().getProperty("spring.datasource.url", String.class, "");
        String h2user = context.getEnvironment().getProperty("spring.datasource.username", String.class, "");

        //LOGGER.info("Application running. Local access URL: http://localhost:{}{}", port, contextPath);
        LOGGER.info("Swagger local access URL: http://localhost:{}{}/swagger-ui.html", port, contextPath);
        LOGGER.info("H2 Console: http://localhost:{}{}/h2-console", port, contextPath);
        LOGGER.info("H2 Url: {} User: {}", h2url, h2user);

    }

    @Bean
    public PlayerRepositoryImplementation playerRepositoryImplementation(){
        return new PlayerRepositoryImplementation();
    }

}

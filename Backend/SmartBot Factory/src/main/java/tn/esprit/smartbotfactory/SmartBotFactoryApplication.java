package tn.esprit.smartbotfactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import tn.esprit.smartbotfactory.rag.RagProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({ RagProperties.class})
public class SmartBotFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartBotFactoryApplication.class, args);
    }

}



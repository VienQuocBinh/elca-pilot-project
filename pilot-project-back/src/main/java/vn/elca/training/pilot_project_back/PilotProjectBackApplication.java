package vn.elca.training.pilot_project_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class PilotProjectBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PilotProjectBackApplication.class, args);
    }

}

package org._1mg.tt_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TtBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtBackendApplication.class, args);
    }

}

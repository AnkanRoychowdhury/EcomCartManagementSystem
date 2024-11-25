package tech.ankanroychowdhury.ecomcartmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EcomCartManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomCartManagementSystemApplication.class, args);
    }

}

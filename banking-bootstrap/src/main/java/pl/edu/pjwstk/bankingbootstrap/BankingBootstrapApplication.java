package pl.edu.pjwstk.bankingbootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pl.edu.pjwstk")
@EnableJpaRepositories(basePackages = "pl.edu.pjwstk.bankingdomain.repository")
@EntityScan(basePackages = "pl.edu.pjwstk.bankingdomain.model")
public class BankingBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingBootstrapApplication.class, args);
    }

}

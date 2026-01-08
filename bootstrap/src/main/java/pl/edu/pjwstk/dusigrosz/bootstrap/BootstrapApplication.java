package pl.edu.pjwstk.dusigrosz.bootstrap;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pl.edu.pjwstk.dusigrosz")
@EnableJpaRepositories("pl.edu.pjwstk.dusigrosz.domain.repository")
@EntityScan("pl.edu.pjwstk.dusigrosz.domain.model")
public class BootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapApplication.class, args);
    }

}

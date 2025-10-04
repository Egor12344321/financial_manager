package spring_boot;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"spring_boot", "org.example"})
@EnableJpaRepositories(basePackages = {"spring_boot", "org.example"})
@EntityScan(basePackages = {"spring_boot", "org.example"})
public class FinanceApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(FinanceApplication.class, args);
    }

}



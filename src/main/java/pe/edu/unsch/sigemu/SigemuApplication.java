package pe.edu.unsch.sigemu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SigemuApplication {

    public static void main(String[] args) {
        SpringApplication.run(SigemuApplication.class, args);
    }
}

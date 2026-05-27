package {{packageRoot}}.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "{{packageRoot}}")
public class {{applicationClass}} {

    public static void main(String[] args) {
        SpringApplication.run({{applicationClass}}.class, args);
    }
}

package morse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    private static Class<App> applicationClass = App.class;

    public static void main(String[] args) {
        SpringApplication.run(applicationClass, args);
    }
}

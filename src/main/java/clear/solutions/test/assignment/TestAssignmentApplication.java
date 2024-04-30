package clear.solutions.test.assignment;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(UserConfigurationProperties.class)
@SpringBootApplication
public class TestAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestAssignmentApplication.class, args);
	}

}

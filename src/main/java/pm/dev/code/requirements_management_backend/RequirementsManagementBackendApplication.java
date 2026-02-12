package pm.dev.code.requirements_management_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RequirementsManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequirementsManagementBackendApplication.class, args);

//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//		String rawPassword = "123456";
//		String hash = encoder.encode(rawPassword);
//
//		System.out.println("Raw password  : " + rawPassword);
	}

}

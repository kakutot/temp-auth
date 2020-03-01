package src;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
@Slf4j
public class App {

    public static void main(String args[]) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder,
                                               UserRoleRepo userRoleRepo,
                                               UserSecuredRepository userSecuredRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
               UserRole userRole = new UserRole();
                userRole.setName(UserRole.USER_ROLE.USER);

                UserRole adminRole = new UserRole();
                adminRole.setName(UserRole.USER_ROLE.ADMIN);

                userRole = userRoleRepo.save(userRole);
                adminRole = userRoleRepo.save(adminRole);

                UserSecured user1 = new UserSecured();
                user1.setUsername("Roma");
                user1.setPassword(passwordEncoder.encode("a"));
                user1.setUserRoles(new HashSet<UserRole>(Arrays.asList(userRole)));
                userSecuredRepository.save(user1);

                log.error("sdsdadasdsa"+userSecuredRepository.findByUsername("Roma").get(0).toString());
            }
        };
    }
}

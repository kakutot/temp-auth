package src;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import src.model.UserRole;
import src.model.UserSecured;
import src.repository.UserRoleRepository;
import src.repository.UserSecuredRepository;

import java.util.Arrays;
import java.util.HashSet;

@EnableJpaRepositories(basePackages = {"src.repository"})
@SpringBootApplication(scanBasePackages = {
        "src.controller", "src.filter",
        "src.model",
        "src.security", "src.service"})
public class App {

    public static void main(String args[]) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder,
                                               UserRoleRepository userRoleRepo,
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
                user1.setPassword(passwordEncoder.encode("ab"));
                user1.setUserRoles(new HashSet<UserRole>(Arrays.asList(userRole, adminRole)));
                userSecuredRepository.save(user1);
            }
        };
    }
}

package src.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import src.repository.UserSecuredRepository;

@Slf4j
@Component
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserSecuredRepository userSecuredRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails;
        try {
             userDetails = new CustomUserDetails(userSecuredRepository.findFirstByUsername(username).get());
        } catch (Exception no) {
                throw new UsernameNotFoundException(String.format("No such user : %s ", username));
        }

        return userDetails;
    }
}

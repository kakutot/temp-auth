package src;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

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
             userDetails = new CustomUserDetails(userSecuredRepository.findByUsername(username).get(0));
             log.error("Userrrrrr" + userDetails);
        } catch (Exception no) {
                throw new UsernameNotFoundException(String.format("No such user : %s ", username));
        }

        return userDetails;
    }
}

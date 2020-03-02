package src.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import src.model.UserRole;
import src.model.UserSecured;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

    public CustomUserDetails(final UserSecured user) {
        username = user.getUsername();
        password = user.getPassword();
        grantedAuthorities.addAll(translate(user.getUserRoles()));
    }

    private Collection<? extends GrantedAuthority> translate(Set<UserRole> roles) {
        return roles.stream()
                    .map(role -> "ROLE_" + role.getName())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
    }

    public UserSecured getUserSecured() {
        return new UserSecured(username, password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

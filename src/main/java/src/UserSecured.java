package src;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name ="user_secured")
@Table(name = "user_secured")
public class UserSecured {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Column
    private String username;

    @NotBlank
    @Column
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns =
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns =
            @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Set<UserRole> userRoles = new HashSet<UserRole>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(UserSecured.class.isAssignableFrom(o.getClass()))) return false;
        return id != null && id.equals(((UserSecured) o).getId());
    }

    //что-бы попали в один и тот же бакет
    @Override
    public int hashCode() {
        return 31;
    }
}
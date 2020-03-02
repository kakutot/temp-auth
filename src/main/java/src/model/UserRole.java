package src.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity(name = "user_role")
@Table(name = "user_role")
public class UserRole {

    @Id
    @GeneratedValue
    @Column(name = "role_id",
            unique = true, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    private USER_ROLE name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(id, userRole.id) &&
                name == userRole.name;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public enum USER_ROLE {
        USER, ADMIN
    }
}
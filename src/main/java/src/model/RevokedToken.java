package src.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "revoked_token")
public class RevokedToken {

    @Id
    @Column(name = "jwt_token_digest", precision = 256)
    private String jwtTokenDigest;

    @Column(name = "revocation_date")
    private LocalDateTime revocationDate;
}

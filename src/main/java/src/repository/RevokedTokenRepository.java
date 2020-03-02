package src.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import src.model.RevokedToken;

@Repository
public interface RevokedTokenRepository extends CrudRepository<RevokedToken, String> {
}

package src.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import src.model.UserSecured;

import java.util.Optional;

@Repository
public interface UserSecuredRepository extends CrudRepository<UserSecured, Long> {

   Optional<UserSecured> findFirstByUsername(String username);
}

package src;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSecuredRepository extends CrudRepository<UserSecured, Long> {

    List<UserSecured> findByUsername(String username);
}

package pm.dev.code.requirements_management_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pm.dev.code.requirements_management_backend.entities.User;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

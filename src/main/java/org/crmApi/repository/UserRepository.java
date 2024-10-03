package org.crmApi.repository;
import org.crmApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    default void deleteById(Long id) {

    }


}


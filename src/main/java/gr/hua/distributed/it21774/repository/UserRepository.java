package gr.hua.distributed.it21774.repository;

import gr.hua.distributed.it21774.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByAfm(String afm);

    Optional<User> findByAmka(String amka);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByAfm(String afm);

    Boolean existsByAmka(String amka);
}

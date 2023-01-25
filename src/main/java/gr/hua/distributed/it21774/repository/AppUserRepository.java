package gr.hua.distributed.it21774.repository;

import gr.hua.distributed.it21774.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByAfm(String afm);

    Optional<AppUser> findByAmka(String amka);

    Optional<AppUser> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByAfm(String afm);

    Boolean existsByAmka(String amka);
}

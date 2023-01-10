package gr.hua.distributed.it21774.repository;

import gr.hua.distributed.it21774.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByAfm(Integer afm);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByAfm(Integer afm);

    Boolean existsByAmka(Integer amka);
}

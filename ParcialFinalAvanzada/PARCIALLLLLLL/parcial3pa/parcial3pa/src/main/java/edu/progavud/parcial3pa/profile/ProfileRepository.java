package edu.progavud.parcial3pa.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
        // ✅ Buscar perfil por username del usuario
    @Query("SELECT p FROM Profile p WHERE p.user.username = :username")
    Optional<Profile> findByUsername(@Param("username") String username);
}

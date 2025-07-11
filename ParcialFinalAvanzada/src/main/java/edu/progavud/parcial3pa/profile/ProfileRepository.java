package edu.progavud.parcial3pa.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder y manejar los perfiles de usuario en la base de datos.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Busca un perfil por el nombre de usuario asociado.
     *
     * @param username nombre de usuario
     * @return perfil encontrado, si existe
     */
    @Query("SELECT p FROM Profile p WHERE p.user.username = :username")
    Optional<Profile> findByUsername(@Param("username") String username);
}

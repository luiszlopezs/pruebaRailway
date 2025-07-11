package edu.progavud.parcial3pa.follow;

import edu.progavud.parcial3pa.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio para manejar las relaciones de seguimiento entre usuarios.
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    /**
     * Busca una relación de seguimiento entre dos usuarios.
     *
     * @param follower el usuario que sigue
     * @param followed el usuario que es seguido
     * @return la relación si existe
     */
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);
    
    /**
     * Verifica si existe una relación de seguimiento entre dos usuarios.
     *
     * @param follower el usuario que sigue
     * @param followed el usuario que es seguido
     * @return true si existe la relación, false si no
     */
    boolean existsByFollowerAndFollowed(User follower, User followed);
    
    /**
     * Elimina una relación de seguimiento entre dos usuarios.
     *
     * @param follower el usuario que sigue
     * @param followed el usuario que es seguido
     */
    @Modifying
    @Transactional
    void deleteByFollowerAndFollowed(User follower, User followed);
    
    /**
     * Elimina todas las relaciones en las que el usuario es seguidor o seguido.
     * Se usa al eliminar una cuenta de usuario.
     *
     * @param userId ID del usuario
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.follower.id = :userId OR f.followed.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
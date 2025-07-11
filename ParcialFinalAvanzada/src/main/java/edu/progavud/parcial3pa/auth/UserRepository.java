package edu.progavud.parcial3pa.auth;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder y manejar los datos de los usuarios.
 * Proporciona métodos para buscar, verificar existencia y sugerir usuarios.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return usuario encontrado, si existe
     */
    Optional<User> findByUsername(String username);

        /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico
     * @return usuario encontrado, si existe
     */
    Optional<User> findByEmail(String email);

       /**
     * Busca un usuario por su nombre de usuario o correo electrónico.
     *
     * @param usernameOrEmail nombre de usuario o correo
     * @return usuario encontrado, si existe
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

       /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     *
     * @param username nombre de usuario
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);

        /**
     * Verifica si existe un usuario con el correo electrónico dado.
     *
     * @param email correo electrónico
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

        /**
     * Verifica si existe otro usuario (distinto al indicado) con el mismo nombre de usuario o correo electrónico.
     *
     * @param username nombre de usuario
     * @param email correo electrónico
     * @param userId ID del usuario que se quiere excluir
     * @return true si existe otro usuario con esos datos, false si no
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE (u.username = :username OR u.email = :email) AND u.id != :userId")
    boolean existsByUsernameOrEmailAndIdNot(@Param("username") String username,
            @Param("email") String email,
            @Param("userId") Long userId);

        /**
     * Busca usuarios cuyo nombre de usuario comience con el texto dado (sugerencias).
     *
     * @param query texto a buscar
     * @return lista de usuarios cuyo nombre empieza por el texto
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT(:query, '%'))") //Para mostrar sugerencias a la hora de buscar
    List<User> searchByUsernameStartsWith(@Param("query") String query);

}

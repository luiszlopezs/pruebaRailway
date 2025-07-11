package edu.progavud.parcial3pa.profile;

import edu.progavud.parcial3pa.auth.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Entidad que representa el perfil de un usuario.
 * Contiene información adicional como biografía y contadores de seguidores y seguidos.
 */
@Entity
@Table(name = "profiles")
public class Profile {

            /** Identificador único del perfil. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

        /** Biografía del usuario. */
    @Column(length = 500)
    private String bio;

        /** URL o ruta de la imagen de perfil del usuario. */
    @Column(name = "profilePicture", length = 255)
    private String profilePicture;

        /** Número de seguidores del usuario. */
    @Column(nullable = false)
    private int followers;

        /** Número de usuarios que el usuario sigue. */
    @Column(nullable = false)
    private int following;

        /** Número total de publicaciones realizadas por el usuario. */
    @Column(name = "num_posts", nullable = false)
    private int numPosts;
    

            /** Usuario al que pertenece este perfil. */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    
        /**
     * Constructor vacío requerido por JPA.
     */
    public Profile() {
    }

        /**
     * Crea un perfil con los datos especificados.
     *
     * @param bio biografía del usuario
     * @param profilePicture imagen de perfil del usuario
     * @param followers cantidad de seguidores
     * @param following cantidad de seguidos
     * @param numPosts cantidad de publicaciones
     * @param user usuario asociado al perfil
     */
    public Profile(String bio, String profilePicture, int followers, int following, int numPosts, User user) {
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.followers = followers;
        this.following = following;
        this.numPosts = numPosts;
        this.user = user;
    }

            /**
     * Obtiene el ID del perfil.
     *
     * @return ID del perfil
     */
    // Getters y setters
    public Long getId() {
        return id;
    }

        /**
     * Establece el ID del perfil.
     *
     * @param id nuevo ID del perfil
     */
    public void setId(Long id) {
        this.id = id;
    }

       /**
     * Obtiene la biografía del usuario.
     *
     * @return biografía del perfil
     */
    public String getBio() {
        return bio;
    }

        /**
     * Establece la biografía del usuario.
     *
     * @param bio nueva biografía
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

        /**
     * Obtiene la imagen de perfil del usuario.
     *
     * @return imagen de perfil
     */
    public String getProfilePicture() {
        return profilePicture;
    }
    /**
     * Establece la imagen de perfil del usuario.
     *
     * @param profilePicture nueva imagen de perfil
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    /**
     * Obtiene la cantidad de seguidores del usuario.
     *
     * @return número de seguidores
     */
    public int getFollowers() {
        return followers;
    }
   /**
     * Establece la cantidad de seguidores del usuario.
     *
     * @param followers nuevo número de seguidores
     */
    public void setFollowers(int followers) {
        this.followers = followers;
    }
    /**
     * Obtiene la cantidad de usuarios que sigue el usuario.
     *
     * @return número de seguidos
     */
    public int getFollowing() {
        return following;
    }
    /**
     * Establece la cantidad de usuarios que sigue el usuario.
     *
     * @param following nuevo número de seguidos
     */
    public void setFollowing(int following) {
        this.following = following;
    }
    /**
     * Obtiene el número de publicaciones del usuario.
     *
     * @return número de publicaciones
     */
    public int getNumPosts() {
        return numPosts;
    }
    /**
     * Establece el número de publicaciones del usuario.
     *
     * @param numPosts nuevo número de publicaciones
     */
    public void setNumPosts(int numPosts) {
        this.numPosts = numPosts;
    }
    /**
     * Obtiene el usuario asociado a este perfil.
     *
     * @return usuario del perfil
     */
    public User getUser() {
        return user;
    }
    /**
     * Establece el usuario asociado a este perfil.
     *
     * @param user nuevo usuario del perfil
     */
    public void setUser(User user) {
        this.user = user;
    }

     /**
     * Devuelve una representación en texto del perfil.
     *
     * @return cadena con los datos del perfil
     */
    @Override
    public String toString() {
        return "Profile{"
                + "id=" + id
                + ", bio='" + bio + '\''
                + ", profilePicture='" + profilePicture + '\''
                + ", followers=" + followers
                + ", following=" + following
                + ", numPosts=" + numPosts
                + ", user=" + user.getUsername()
                + // o user.getId()
                '}';
    }
    /**
     * Compara si dos perfiles son iguales según su ID.
     *
     * @param o objeto a comparar
     * @return true si los perfiles son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile profile)) {
            return false;
        }
        return Objects.equals(id, profile.id);
    }
    /**
     * Genera el código hash del perfil usando su ID.
     *
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

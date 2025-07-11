package edu.progavud.parcial3pa.follow;

import edu.progavud.parcial3pa.auth.User;
import jakarta.persistence.*;
/**
 * Entidad que representa una relación de seguimiento entre usuarios.
 * Un usuario puede seguir a otro.
 */
@Entity
@Table(name = "follows")
public class Follow {
            /** Identificador único de la relación de seguimiento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User follower; // el que sigue

    @ManyToOne
    private User followed; // al que sigue

    // Constructor vacío y getters/setters
      /**
     * Constructor vacío requerido por JPA.
     */
    public Follow() {
    }
    /**
     * Crea una nueva relación de seguimiento entre dos usuarios.
     *
     * @param follower el usuario que sigue
     * @param followed el usuario que es seguido
     */
    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }
    /**
     * Obtiene el ID de la relación de seguimiento.
     *
     * @return ID de la relación
     */
    public Long getId() {
        return id;
    }
    /**
     * Obtiene el usuario que sigue.
     *
     * @return usuario seguidor
     */
    public User getFollower() {
        return follower;
    }
    /**
     * Establece el usuario que sigue.
     *
     * @param follower usuario seguidor
     */
    public void setFollower(User follower) {
        this.follower = follower;
    }
    /**
     * Obtiene el usuario que es seguido.
     *
     * @return usuario seguido
     */
    public User getFollowed() {
        return followed;
    }
    /**
     * Establece el usuario que es seguido.
     *
     * @param followed usuario seguido
     */
    public void setFollowed(User followed) {
        this.followed = followed;
    }
}

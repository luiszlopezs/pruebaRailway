package edu.progavud.parcial3pa.auth;

import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un usuario en la aplicación.
 *
 * <p>
 * Contiene información como el nombre de usuario, correo electrónico, y fechas
 * de creación y actualización.</p>
 */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
public class User {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Correo electrónico del usuario. Debe ser único y no nulo.
     */
    @Column(unique = true, nullable = false, length = 100)
//    @Email(message = "Email debe tener un formato válido")
//    @NotBlank(message = "Email es obligatorio")
    private String email;
    /**
     * Nombre completo del usuario. No puede ser nulo.
     */
    @Column(name = "full_name", nullable = false, length = 100)
//    @NotBlank(message = "Nombre completo es obligatorio")
//    @Size(min = 2, max = 100, message = "Nombre debe tener entre 2 y 100 caracteres")
    private String fullName;
    /**
     * Nombre de usuario único. No puede ser nulo.
     */
    @Column(unique = true, nullable = false, length = 30)
//    @NotBlank(message = "Usuario es obligatorio")
//    @Size(min = 3, max = 30, message = "Usuario debe tener entre 3 y 30 caracteres")
    private String username;
    /**
     * Contraseña del usuario. No puede ser nula.
     */
    @Column(nullable = false, length = 255)
//    @NotBlank(message = "Contraseña es obligatoria")
//    @Size(min = 6, message = "Contraseña debe tener al menos 6 caracteres")
    private String password;

    /**
     * Fecha y hora en que se creó el usuario. Se asigna automáticamente.
     */
//    @Column(name = "is_active", nullable = false)
//    private Boolean isActive = true;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    /**
     * Fecha y hora de la última actualización del usuario. Se actualiza
     * automáticamente.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructores
    public User() {
        //this.isActive = true;
    }

    /**
     * Crea un nuevo usuario con los datos proporcionados.
     *
     * @param email correo electrónico del usuario
     * @param fullName nombre completo del usuario
     * @param username nombre de usuario
     * @param password contraseña del usuario
     */
    public User(String email, String fullName, String username, String password) {
        this();
        this.email = email;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
    }

    // Getters y Setters
    /**
     * Obtiene el ID del usuario.
     *
     * @return ID del usuario
     */    
    public Long getId() {
        return id;
    }
    /**
     * Establece el ID del usuario.
     *
     * @param id nuevo ID del usuario
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return correo electrónico del usuario
     */
    public String getEmail() {
        return email;
    }
    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email nuevo correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Obtiene el nombre completo del usuario.
     *
     * @return nombre completo del usuario
     */
    public String getFullName() {
        return fullName;
    }
    /**
     * Establece el nombre completo del usuario.
     *
     * @param fullName nuevo nombre completo
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    /**
     * Obtiene el nombre de usuario.
     *
     * @return nombre de usuario
     */
    public String getUsername() {
        return username;
    }
    /**
     * Establece el nombre de usuario.
     *
     * @param username nuevo nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * Obtiene la contraseña del usuario.
     *
     * @return contraseña del usuario
     */
    public String getPassword() {
        return password;
    }
    /**
     * Establece la contraseña del usuario.
     *
     * @param password nueva contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }

//    public Boolean getIsActive() {
//        return isActive;
//    }
//    
//    public void setIsActive(Boolean isActive) {
//        this.isActive = isActive;
//    }
    
            /**
     * Obtiene la fecha de creación del usuario.
     *
     * @return fecha de creación
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    /**
     * Establece la fecha de creación del usuario.
     *
     * @param createdAt nueva fecha de creación
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    /**
     * Obtiene la fecha de la última actualización del usuario.
     *
     * @return fecha de actualización
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    /**
     * Establece la fecha de la última actualización del usuario.
     *
     * @param updatedAt nueva fecha de actualización
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
        /**
     * Devuelve una representación en texto del usuario.
     *
     * @return cadena con los datos del usuario
     */
    // toString, equals y hashCode
    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", email='" + email + '\''
                + ", fullName='" + fullName + '\''
                + ", username='" + username + '\''
                + //                ", isActive=" + isActive +
                ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
        /**
     * Compara si dos objetos User son iguales según su id, username y email.
     *
     * @param o objeto a comparar
     * @return true si los objetos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(username, user.username)
                && Objects.equals(email, user.email);
    }
        /**
     * Genera el código hash del usuario usando id, username y email.
     *
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}

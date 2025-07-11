/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;


import edu.progavud.parcial3pa.auth.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una publicación realizada por un usuario.
 */
@Entity
public class Post {

            /** Identificador único de la publicación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

        /** URL de la imagen asociada a la publicación. */
    private String imageUrl;

        /** Descripción escrita por el usuario. */
    private String description;

        /** Número de "me gusta" que tiene la publicación. */
    private int likes;

        /** Fecha y hora en que se creó la publicación. */
    private LocalDateTime createdAt;

        /** Usuario que creó la publicación. */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

        /**
     * Constructor vacío requerido por JPA.
     */
    public Post() {}

            /**
     * Obtiene el ID de la publicación.
     *
     * @return ID de la publicación
     */
    public Long getId() {
        return id;
    }

        /**
     * Obtiene la URL de la imagen de la publicación.
     *
     * @return URL de la imagen
     */
    public String getImageUrl() {
        return imageUrl;
    }

        /**
     * Establece la URL de la imagen de la publicación.
     *
     * @param imageUrl nueva URL de la imagen
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

        /**
     * Obtiene la descripción de la publicación.
     *
     * @return descripción de la publicación
     */
    public String getDescription() {
        return description;
    }

        /**
     * Establece la descripción de la publicación.
     *
     * @param description nueva descripción
     */
    public void setDescription(String description) {
        this.description = description;
    }

        /**
     * Obtiene la cantidad de "me gusta" de la publicación.
     *
     * @return número de likes
     */
    public int getLikes() {
        return likes;
    }

        /**
     * Establece la cantidad de "me gusta" de la publicación.
     *
     * @param likes nuevo número de likes
     */
    public void setLikes(int likes) {
        this.likes = likes;
    }
    /**
     * Obtiene la fecha de creación de la publicación.
     *
     * @return fecha de creación
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    /**
     * Establece la fecha de creación de la publicación.
     *
     * @param createdAt nueva fecha de creación
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    /**
     * Obtiene el usuario que creó la publicación.
     *
     * @return usuario autor de la publicación
     */
    public User getUser() {
        return user;
    }
    /**
     * Establece el usuario que creó la publicación.
     *
     * @param user nuevo usuario autor de la publicación
     */
    public void setUser(User user) {
        this.user = user;
    }
}

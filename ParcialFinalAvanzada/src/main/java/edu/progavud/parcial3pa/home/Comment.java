/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;


import edu.progavud.parcial3pa.auth.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;


/**
 * Entidad que representa un comentario realizado por un usuario.
 */
@Entity
public class Comment {

            /** Identificador único del comentario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

        /** Texto del comentario. */
    private String text;

        /** Fecha y hora en que se creó el comentario. */
    private LocalDateTime createdAt;

        /** Publicación a la que pertenece el comentario. */
    @ManyToOne
    private Post post;

        /** Usuario que realizó el comentario. */
    @ManyToOne
    private User user;

        /**
     * Constructor vacío requerido por JPA.
     */
    public Comment() {}

    // Getters y setters

        /**
     * Obtiene el ID del comentario.
     *
     * @return ID del comentario
     */
    public Long getId() {
        return id;
    }

        /**
     * Obtiene el texto del comentario.
     *
     * @return texto del comentario
     */
    public String getText() {
        return text;
    }

        /**
     * Establece el texto del comentario.
     *
     * @param text nuevo texto del comentario
     */
    public void setText(String text) {
        this.text = text;
    }

        /**
     * Obtiene la fecha de creación del comentario.
     *
     * @return fecha de creación
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

        /**
     * Establece la fecha de creación del comentario.
     *
     * @param createdAt nueva fecha de creación
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

        /**
     * Obtiene la publicación a la que pertenece el comentario.
     *
     * @return publicación relacionada
     */
    public Post getPost() {
        return post;
    }

        /**
     * Establece la publicación a la que pertenece el comentario.
     *
     * @param post publicación relacionada
     */
    public void setPost(Post post) {
        this.post = post;
    }

        /**
     * Obtiene el usuario que hizo el comentario.
     *
     * @return usuario autor del comentario
     */
    public User getUser() {
        return user;
    }

        /**
     * Establece el usuario que hizo el comentario.
     *
     * @param user usuario autor del comentario
     */
    public void setUser(User user) {
        this.user = user;
    }
}

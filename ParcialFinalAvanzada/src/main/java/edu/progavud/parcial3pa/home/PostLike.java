/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;

import edu.progavud.parcial3pa.auth.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Entidad que representa un "me gusta" realizado por un usuario en una publicación.
 */
@Entity
public class PostLike {
    
        /** Identificador único del "me gusta". */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario que dio el "me gusta". */
    @ManyToOne
    private User user;

    /** Publicación que recibió el "me gusta". */
    @ManyToOne
    private Post post;

    
    

    // Getters y Setters

        /**
     * Constructor vacío requerido por JPA.
     */
    public PostLike() {
    }

    /**
     * Crea un nuevo "me gusta" con los datos especificados.
     *
     * @param id identificador del "me gusta"
     * @param user usuario que da el "me gusta"
     * @param post publicación que recibe el "me gusta"
     */
    public PostLike(Long id, User user, Post post) {
        this.id = id;
        this.user = user;
        this.post = post;
    }

    /**
     * Obtiene el ID del "me gusta".
     *
     * @return ID del "me gusta"
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del "me gusta".
     *
     * @param id nuevo ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el usuario que dio el "me gusta".
     *
     * @return usuario que dio el "me gusta"
     */
    public User getUser() {
        return user;
    }

    /**
     * Establece el usuario que dio el "me gusta".
     *
     * @param user usuario que dio el "me gusta"
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Obtiene la publicación que recibió el "me gusta".
     *
     * @return publicación relacionada
     */
    public Post getPost() {
        return post;
    }

    /**
     * Establece la publicación que recibió el "me gusta".
     *
     * @param post publicación relacionada
     */
    public void setPost(Post post) {
        this.post = post;
    }

    
}

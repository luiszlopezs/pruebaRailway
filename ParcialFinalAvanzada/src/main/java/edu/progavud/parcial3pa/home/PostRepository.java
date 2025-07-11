/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;


import edu.progavud.parcial3pa.home.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio para acceder y manejar las publicaciones en la base de datos.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Obtiene todas las publicaciones ordenadas por fecha de creación descendente.
     *
     * @return lista de publicaciones ordenadas
     */
    List<Post> findAllByOrderByCreatedAtDesc();

    /**
     * Obtiene todas las publicaciones realizadas por un usuario específico.
     *
     * @param userId ID del usuario
     * @return lista de publicaciones del usuario
     */
    List<Post> findByUserId(Long userId);
}

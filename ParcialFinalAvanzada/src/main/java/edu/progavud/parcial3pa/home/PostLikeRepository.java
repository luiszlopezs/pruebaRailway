/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio para manejar los "me gusta" de las publicaciones.
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
        /**
     * Busca un "me gusta" realizado por un usuario en una publicación específica.
     *
     * @param userId ID del usuario
     * @param postId ID de la publicación
     * @return "me gusta" encontrado, si existe
     */
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
        /**
     * Cuenta cuántos "me gusta" tiene una publicación.
     *
     * @param postId ID de la publicación
     * @return número de "me gusta"
     */
    int countByPostId(Long postId);
    
    /**
     * Elimina un "me gusta" realizado por un usuario en una publicación.
     *
     * @param userId ID del usuario
     * @param postId ID de la publicación
     */
    void deleteByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserId(Long userId);
    void deleteByPostId(Long postId);
}
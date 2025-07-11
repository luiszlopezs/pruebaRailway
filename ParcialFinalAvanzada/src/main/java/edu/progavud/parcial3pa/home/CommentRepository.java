/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;


import edu.progavud.parcial3pa.home.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
/**
 * Repositorio para acceder y manejar los comentarios en la base de datos.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
        /**
     * Busca los comentarios de una publicación por su ID, ordenados por fecha de creación ascendente.
     *
     * @param postId ID de la publicación
     * @return lista de comentarios asociados a la publicación
     */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    void deleteByUserId(Long userId);
    void deleteByPostId(Long postId);

}
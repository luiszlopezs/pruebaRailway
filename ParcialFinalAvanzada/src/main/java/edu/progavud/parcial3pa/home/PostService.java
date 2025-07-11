/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;

import edu.progavud.parcial3pa.auth.User;
import edu.progavud.parcial3pa.home.Comment;
import edu.progavud.parcial3pa.home.Post;
import edu.progavud.parcial3pa.home.CommentRepository;
import edu.progavud.parcial3pa.home.PostRepository;
import edu.progavud.parcial3pa.auth.UserRepository;
import edu.progavud.parcial3pa.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Servicio que contiene la lógica para gestionar las publicaciones, comentarios
 * y "me gusta" de los usuarios.
 */
@Service
public class PostService {

    /**
     * Carpeta donde se guardan las imágenes de las publicaciones.
     */
    private final String uploadDir = "postImages";
    
    /** Repositorio para manejar publicaciones. */
    @Autowired
    private PostRepository postRepo;
     /** Repositorio para manejar usuarios. */
    @Autowired
    private UserRepository userRepo;
     /** Repositorio para manejar "me gusta" en publicaciones. */
    @Autowired
    private PostLikeRepository postLikeRepo;
    /** Repositorio para manejar comentarios en publicaciones. */
    @Autowired
    private CommentRepository commentRepo;
/** Repositorio para acceder a los perfiles de usuario. */
    @Autowired
    private ProfileRepository profileRepo;

    
          /**
     * Crea una nueva publicación usando un enlace de imagen.
     *
     * @param userId ID del usuario que crea la publicación
     * @param imageUrl URL de la imagen que se va a publicar
     * @param description descripción de la publicación
     * @throws IllegalArgumentException si el usuario no existe
     */
    public void crearPostDesdeLink(Long userId, String imageUrl, String description) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Post post = new Post();
        post.setUser(user);
        post.setImageUrl(imageUrl);
        post.setDescription(description);
        post.setCreatedAt(LocalDateTime.now());

        postRepo.save(post);
    }


                /**
     * Obtiene todas las publicaciones ordenadas por fecha de creación descendente,
     * incluyendo información del usuario, cantidad de "me gusta", si el usuario actual ha dado "me gusta",
     * y la lista de comentarios de cada publicación.
     *
     * @param currentUserId ID del usuario actual (opcional) para verificar si ha dado "me gusta"
     * @return lista de mapas con la información detallada de cada publicación
     */
    public List<Map<String, Object>> getAllPosts(Long currentUserId) {
        List<Post> posts = postRepo.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Post post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("description", post.getDescription());
            map.put("imageUrl", post.getImageUrl());
            map.put("user", post.getUser());
            map.put("createdAt", post.getCreatedAt());

            int likes = postLikeRepo.countByPostId(post.getId());
            map.put("likes", likes);

            if (currentUserId != null) {
                boolean hasLiked = postLikeRepo.findByUserIdAndPostId(currentUserId, post.getId()).isPresent();
                map.put("hasLiked", hasLiked);
            }

            // ✅ NUEVO: Agregar los comentarios
            List<Map<String, Object>> commentsList = commentRepo.findByPostIdOrderByCreatedAtAsc(post.getId()).stream()
                    .map(comment -> {
                        Map<String, Object> commentMap = new HashMap<>();
                        commentMap.put("text", comment.getText());
                        commentMap.put("user", Map.of("username", comment.getUser().getUsername()));
                        return commentMap;
                    }).collect(Collectors.toList());

            map.put("comments", commentsList); // <-- lo agregamos al post

            resultado.add(map);
        }

        return resultado;
    }

        /**
     * Agrega un comentario a una publicación por parte de un usuario.
     *
     * @param postId ID de la publicación
     * @param userId ID del usuario que comenta
     * @param text contenido del comentario
     * @return mapa con el resultado de la operación (éxito o error con mensaje)
     */
    public Map<String, Object> commentPost(Long postId, Long userId, String text) {
        Map<String, Object> response = new HashMap<>();
        Post post = postRepo.findById(postId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);

        if (post == null || user == null) {
            response.put("success", false);
            response.put("message", "Post o usuario no encontrado");
            return response;
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepo.save(comment);

        response.put("success", true);
        return response;
    }

            /**
     * Alterna el estado de "me gusta" en una publicación para un usuario.
     * Si ya ha dado "me gusta", lo elimina; si no, lo agrega.
     *
     * @param postId ID de la publicación
     * @param userId ID del usuario
     * @return respuesta con el total actualizado de "me gusta" y el estado de la operación
     */
    public ResponseEntity<Map<String, Object>> toggleLike(Long postId, Long userId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Post> postOpt = postRepo.findById(postId);
        Optional<User> userOpt = userRepo.findById(userId);

        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Post o usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Post post = postOpt.get();
        User user = userOpt.get();

        Optional<PostLike> postLike = postLikeRepo.findByUserIdAndPostId(user.getId(), post.getId());

        if (postLike.isPresent()) {
            postLikeRepo.delete(postLike.get());
        } else {
            PostLike nuevoLike = new PostLike();
            nuevoLike.setPost(post);
            nuevoLike.setUser(user);
            postLikeRepo.save(nuevoLike);
        }

        int totalLikes = postLikeRepo.countByPostId(post.getId());

        response.put("success", true);
        response.put("likes", totalLikes);
        return ResponseEntity.ok(response);
    }
    
            /**
     * Obtiene las publicaciones realizadas por un usuario específico.
     * Si no se proporciona el ID, retorna todas las publicaciones.
     *
     * @param userId ID del usuario (opcional)
     * @return lista de publicaciones del usuario o todas si el ID es nulo
     */
    public List<Post> obtenerPostsPorUsuario(Long userId) {
        if (userId != null) {
            return postRepo.findByUserId(userId);
        } else {
            return postRepo.findAll();
        }
    }
}

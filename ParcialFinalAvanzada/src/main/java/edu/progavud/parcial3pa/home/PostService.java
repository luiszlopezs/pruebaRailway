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

@Service
public class PostService {

    private final String uploadDir = "postImages";

    @Autowired
    private PostRepository postRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PostLikeRepository postLikeRepo;
    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private ProfileRepository profileRepo;

//    public Map<String, Object> uploadPost(Long userId, MultipartFile file, String description) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            User user = userRepo.findById(userId).orElse(null);
//            if (user == null) {
//                response.put("success", false);
//                response.put("message", "Usuario no encontrado");
//                return response;
//            }
//
//            File dir = new File(uploadDir);
//            if (!dir.exists()) dir.mkdirs();
//
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            File destination = new File(dir, fileName);
//            file.transferTo(destination);
//
//            Post post = new Post();
//            post.setUser(user);
//            post.setImageUrl("/" + uploadDir + "/" + fileName);
//            post.setDescription(description);
//            post.setLikes(0);
//            post.setCreatedAt(LocalDateTime.now());
//
//            postRepo.save(post);
//
//            response.put("success", true);
//            return response;
//
//        } catch (IOException e) {
//            response.put("success", false);
//            response.put("message", "Error al guardar imagen");
//            return response;
//        }
//    }
//    public Map<String, Object> uploadPost(Long userId, MultipartFile file, String description) {
//
//        Map<String, Object> response = new HashMap<>();
//        try {
//            User user = userRepo.findById(userId).orElse(null);
//
//            if (user == null) {
//
//                response.put("success", false);
//                response.put("message", "Usuario no encontrado");
//                return response;
//            }
//
//            // Usa la misma carpeta que en updateProfile
//            String uploadDir = new File("postsImgs/").getAbsolutePath();
//
//            File dir = new File(uploadDir);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            File destination = new File(dir, fileName);
//            file.transferTo(destination);
//
//            Post post = new Post();
//            post.setUser(user);
//            // Ruta relativa para mostrar en frontend
//            post.setImageUrl("/postsImgs/" + fileName);
//            post.setDescription(description);
//            post.setLikes(0);
//            post.setCreatedAt(LocalDateTime.now());
//
//            postRepo.save(post);
//
//            response.put("success", true);
//            return response;
//
//        } catch (IOException e) {
//            response.put("success", false);
//            response.put("message", "Error al guardar imagen");
//            return response;
//        }
//    }
    
    
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

//    public List<Post> getAllPosts() {
//        return postRepo.findAllByOrderByCreatedAtDesc();
//    }
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

//    public Map<String, Object> likePost(Long postId) {
//        Map<String, Object> response = new HashMap<>();
//        Optional<Post> postOpt = postRepo.findById(postId);
//        if (postOpt.isEmpty()) {
//            response.put("success", false);
//            response.put("message", "Post no encontrado");
//            return response;
//        }
//
//        Post post = postOpt.get();
//        post.setLikes(post.getLikes() + 1);
//        postRepo.save(post);
//
//        response.put("success", true);
//        response.put("likes", post.getLikes());
//        return response;
//    }
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

    public List<Post> obtenerPostsPorUsuario(Long userId) {
        if (userId != null) {
            return postRepo.findByUserId(userId);
        } else {
            return postRepo.findAll();
        }
    }
}

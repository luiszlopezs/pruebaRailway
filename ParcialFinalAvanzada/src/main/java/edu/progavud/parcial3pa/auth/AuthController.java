package edu.progavud.parcial3pa.auth;

//import jakarta.validation.Valid;
import edu.progavud.parcial3pa.follow.FollowRepository;
import edu.progavud.parcial3pa.profile.Profile;
import edu.progavud.parcial3pa.profile.ProfileRepository;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Controlador REST encargado de gestionar las operaciones de autenticación de usuarios,
 * como registro, login y obtención de perfiles asociados.
 *
 * <p>Este controlador expone los endpoints relacionados con la autenticación bajo la ruta <code>/auth</code>.</p>
 *
 * <p>Permite el acceso desde cualquier origen gracias a la anotación {@code @CrossOrigin(origins = "*")}.</p>
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    /** Logger para mostrar mensajes en consola. */
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /** Servicio que contiene la lógica de autenticación. */
    private final AuthService authService;

    /** Repositorio para operaciones relacionadas con seguidores. */
    @Autowired
    private FollowRepository followRepository;

    /** Repositorio para operaciones con usuarios. */
    @Autowired
    private UserRepository userRepository;

    /** Repositorio para acceder a los perfiles de usuario. */
    @Autowired
    private ProfileRepository profileRepository;

    /**
     * Constructor que inyecta el servicio de autenticación.
     *
     * @param authService servicio de autenticación
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    /**
     * Registro de usuario
     */
    @PostMapping("/register")
//    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody User user) {
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            User registeredUser = authService.registerUser(user);

            // No retornamos la contraseña
            registeredUser.setPassword(null);

            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("user", registeredUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("Error al registrar usuario: {}", e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Login de usuario
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();

        String usernameOrEmail = loginData.get("usernameOrEmail");
        String password = loginData.get("password");

        if (usernameOrEmail == null || password == null) {
            response.put("success", false);
            response.put("message", "Usuario/email y contraseña son requeridos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<User> userOpt = authService.loginUser(usernameOrEmail, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(null); // No retornamos la contraseña

            response.put("success", true);
            response.put("message", "Inicio de sesión exitoso");
            response.put("user", user);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Credenciales inválidas");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = authService.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(null);

            response.put("success", true);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Verificar disponibilidad de username
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();

        boolean available = authService.isUsernameAvailable(username);

        response.put("available", available);
        response.put("message", available ? "Username disponible" : "Username no disponible");

        return ResponseEntity.ok(response);
    }

    /**
     * Verificar disponibilidad de email
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();

        boolean available = authService.isEmailAvailable(email);

        response.put("available", available);
        response.put("message", available ? "Email disponible" : "Email no disponible");

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener usuario por username
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return authService.findByUsername(username)
                .map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user); // ✅ devolvemos solo el User
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //Búsqueda por nombre de usuario
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam String query) {
        List<User> users = authService.searchUsersByUsername(query);
        List<Map<String, Object>> result = new ArrayList<>();

        for (User u : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", u.getId());
            userMap.put("username", u.getUsername());
            userMap.put("fullName", u.getFullName());

            // Obtener foto de perfil (si existe)
            Optional<Profile> profileOpt = profileRepository.findByUsername(u.getUsername());
            userMap.put("profilePicture", profileOpt.map(Profile::getProfilePicture)
                    .orElse("/postsImgs/profile_pictures/default.jpg"));

            result.add(userMap);
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Eliminar relaciones follow antes de eliminar al usuario
            
            // Eliminar usuario
            userRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Usuario y relaciones eliminadas correctamente.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

package edu.progavud.parcial3pa.auth;

import edu.progavud.parcial3pa.profile.Profile;
import edu.progavud.parcial3pa.profile.ProfileRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio encargado de manejar la lógica relacionada con la autenticación de usuarios.
 */
@Service
@Transactional
public class AuthService {

        /** Logger para mostrar mensajes en consola. */
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    /** Repositorio para operaciones con usuarios. */
    private final UserRepository userRepository;

     /** Servicio para el envío de correos electrónicos. */
    @Autowired
    private EmailService emailService;

    /** Cliente HTTP para consumir servicios externos. */
    @Autowired
    private RestTemplate restTemplate;

     /**
     * Constructor que inyecta el repositorio de usuarios.
     *
     * @param userRepository repositorio de usuarios
     */
    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registra un nuevo usuario
     */
    public User registerUser(User user) {
        log.info("Intentando registrar usuario: {}", user.getUsername());

        // Validar que el usuario no exista
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // TODO: Encriptar contraseña (por ahora guardamos en texto plano)
        // En producción usar BCryptPasswordEncoder
        User savedUser = userRepository.save(user);
        emailService.enviarConfirmacionCuenta(savedUser.getEmail(), savedUser.getUsername());
        log.info("Usuario registrado exitosamente: {}", savedUser.getUsername());
        
        //restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/profile/"+savedUser.getUsername(), Profile.class);
//        Profile nuevoPerfil = new Profile();
//        nuevoPerfil.setUser(savedUser);
//        nuevoPerfil.setBio("");
//        nuevoPerfil.setFollowers(0);
//        nuevoPerfil.setFollowing(0);
//        nuevoPerfil.setNumPosts(0);
//        nuevoPerfil.setProfilePicture("/postsImgs/profile_pictures/default.jpg"); // opcional
//        profileRepository.save(nuevoPerfil);
        return savedUser;
    }

    /**
     * Autentica un usuario
     */
    public Optional<User> loginUser(String usernameOrEmail, String password) {
        log.info("Intentando autenticar usuario: {}", usernameOrEmail);

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // TODO: Comparar contraseña encriptada
            // En producción usar BCryptPasswordEncoder.matches()
            if (user.getPassword().equals(password) /*&& user.getIsActive()*/) {
                log.info("Usuario autenticado exitosamente: {}", user.getUsername());
                return Optional.of(user);
            } else {
                log.warn("Credenciales inválidas para: {}", usernameOrEmail);
            }
        } else {
            log.warn("Usuario no encontrado: {}", usernameOrEmail);
        }

        return Optional.empty();
    }
    //Buscar usuario por nombre de usuario, accede al repository para realizar la consulta a la bd

    public List<User> searchUsersByUsername(String query) {
        return userRepository.searchByUsernameStartsWith(query);
    }

    /**
     * Busca usuario por ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Busca usuario por username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Verifica si un username está disponible
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Verifica si un email está disponible
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}

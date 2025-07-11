package edu.progavud.parcial3pa.follow;

import edu.progavud.parcial3pa.auth.User;
import edu.progavud.parcial3pa.auth.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public boolean followUser(String fromUsername, String toUsername) {
        User from = userRepository.findByUsername(fromUsername).orElse(null);
        User to = userRepository.findByUsername(toUsername).orElse(null);

        if (from == null || to == null || from.equals(to)) {
            return false;
        }

        if (!followRepository.existsByFollowerAndFollowed(from, to)) {
            followRepository.save(new Follow(from, to));

            // REST Template para actualizar contadores
            restTemplate.postForObject("http://localhost:8091/profile/increment-follow", Map.of(
                    "follower", from.getUsername(),
                    "followed", to.getUsername()
            ), Void.class);
        }
        return true;
    }

    @Transactional
    public boolean unfollowUser(String fromUsername, String toUsername) {
        User from = restTemplate.getForObject("http://localhost:8091/auth/by-username/" + fromUsername, User.class);
        
        User to = restTemplate.getForObject("http://localhost:8091/auth/by-username/" + toUsername, User.class);

        if (from == null || to == null || from.equals(to)) {
            return false;
        }

        followRepository.deleteByFollowerAndFollowed(from, to);

        restTemplate.postForObject("http://localhost:8091/profile/decrement-follow", Map.of(
                "follower", from.getUsername(),
                "followed", to.getUsername()
        ), Void.class);

        return true;
    }

    public boolean isFollowing(String fromUsername, String toUsername) {
        User from = restTemplate.getForObject("http://localhost:8091/auth/by-username/" + fromUsername, User.class);
        
        User to = restTemplate.getForObject("http://localhost:8091/auth/by-username/" + toUsername, User.class);

        if (from == null || to == null || from.equals(to)) {
            return false;
        }

        return followRepository.existsByFollowerAndFollowed(from, to);
    }
    
        public void deleteAllByUserId(Long userId) {
        followRepository.deleteAllByUserId(userId);
    }

}

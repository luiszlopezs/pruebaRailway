package edu.progavud.parcial3pa.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/follow")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/follow")
    public Map<String, Object> follow(@RequestBody Map<String, String> payload) {
        String from = payload.get("fromUsername");
        String to = payload.get("toUsername");

        boolean success = followService.followUser(from, to);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

    @PostMapping("/unfollow")
    public Map<String, Object> unfollow(@RequestBody Map<String, String> payload) {
        String from = payload.get("fromUsername");
        String to = payload.get("toUsername");

        boolean success = followService.unfollowUser(from, to);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> isFollowing(
            @RequestParam String from,
            @RequestParam String to) {

        boolean following = followService.isFollowing(from, to);
        Map<String, Object> response = new HashMap<>();
        response.put("following", following);
        return response;
    }

    @DeleteMapping("/deleteByUserId/{userId}")
    public ResponseEntity<Map<String, Object>> deleteFollowsByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            followService.deleteAllByUserId(userId);
            response.put("success", true);
            response.put("message", "Follows eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar follows: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}


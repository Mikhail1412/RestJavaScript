package habsida.spring.boot_strap.demo.controller;

import habsida.spring.boot_strap.demo.model.User;
import habsida.spring.boot_strap.demo.repositories.RoleRepository;
import habsida.spring.boot_strap.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    private static final String NAME_RE = "^[\\p{L}\\s-]+$";

    public UserRestController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest req) {

        if (req.firstName == null || !req.firstName.trim().matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Имя: только буквы / пробел / дефис");
        }
        if (req.lastName == null || !req.lastName.trim().matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Фамилия: только буквы / пробел / дефис");
        }
        if (req.age == null || req.age <= 0) {
            return ResponseEntity.badRequest().body("Возраст должен быть положительным числом");
        }
        if (req.email == null || req.email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email обязателен");
        }
        if (req.password == null || req.password.isBlank()) {
            return ResponseEntity.badRequest().body("Пароль обязателен");
        }
        if (req.roleIds == null || req.roleIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Нужно выбрать хотя бы одну роль");
        }

        User u = new User();
        u.setFirstName(req.firstName.trim());
        u.setLastName(req.lastName.trim());
        u.setAge(req.age);
        u.setEmail(req.email.trim());
        u.setPassword(req.password);
        u.setRoles(new HashSet<>(roleRepository.findAllById(req.roleIds)));

        User saved = userService.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody UpdateUserRequest req) {

        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existing = opt.get();

        String firstName = req.firstName != null ? req.firstName.trim() : "";
        String lastName  = req.lastName != null ? req.lastName.trim() : "";

        if (firstName.isEmpty() || !firstName.matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Имя: только буквы / пробел / дефис");
        }
        if (lastName.isEmpty() || !lastName.matches(NAME_RE)) {
            return ResponseEntity.badRequest().body("Фамилия: только буквы / пробел / дефис");
        }
        if (req.age == null || req.age <= 0) {
            return ResponseEntity.badRequest().body("Возраст должен быть положительным числом");
        }
        if (req.email == null || req.email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email обязателен");
        }

        existing.setFirstName(firstName);
        existing.setLastName(lastName);
        existing.setAge(req.age);
        existing.setEmail(req.email.trim());

        if (req.roleIds != null && !req.roleIds.isEmpty()) {
            existing.setRoles(new HashSet<>(roleRepository.findAllById(req.roleIds)));
        }

        User saved = userService.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateUserRequest {
        public String firstName;
        public String lastName;
        public Integer age;
        public String email;
        public String password;
        public List<Long> roleIds;
    }

    public static class UpdateUserRequest {
        public String firstName;
        public String lastName;
        public Integer age;
        public String email;
        public List<Long> roleIds;
    }
}

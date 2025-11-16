package habsida.spring.boot_strap.demo.controller;

import habsida.spring.boot_strap.demo.model.Role;
import habsida.spring.boot_strap.demo.model.User;
import habsida.spring.boot_strap.demo.repositories.RoleRepository;
import habsida.spring.boot_strap.demo.service.UserService;
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

    public UserRestController(UserService userService,
                              RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.findById(id);
        return optionalUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request) {
        if (request.getFirstName() == null || request.getLastName() == null
                || request.getEmail() == null || request.getPassword() == null
                || request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User u = new User();
        u.setFirstName(request.getFirstName().trim());
        u.setLastName(request.getLastName().trim());
        if (request.getAge() != null) {
            u.setAge(request.getAge());
        }
        u.setEmail(request.getEmail().trim());
        u.setPassword(request.getPassword());

        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        u.setRoles(new HashSet<>(roles));

        User saved = userService.save(u);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request
    ) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }
        user.setEmail(request.getEmail());

        User saved = userService.save(user);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class UserCreateRequest {
        private String firstName;
        private String lastName;
        private Integer age;
        private String email;
        private String password;
        private List<Long> roleIds;

        public UserCreateRequest() {
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<Long> getRoleIds() {
            return roleIds;
        }

        public void setRoleIds(List<Long> roleIds) {
            this.roleIds = roleIds;
        }
    }

    public static class UserUpdateRequest {
        private String firstName;
        private String lastName;
        private Integer age;
        private String email;

        public UserUpdateRequest() {
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
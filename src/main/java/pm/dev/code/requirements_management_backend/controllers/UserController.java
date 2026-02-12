package pm.dev.code.requirements_management_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pm.dev.code.requirements_management_backend.dto.users.*;
import pm.dev.code.requirements_management_backend.services.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponse updateUserByAdmin(@PathVariable Long id, @RequestBody UpdateUserByAdminRequest request) {
        return userService.updateUserByAdmin(id, request);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @PutMapping("/me")
    public UserResponse updateOwnProfile(@RequestBody UpdateOwnProfileRequest request) {
        return userService.updateOwnProfile(request);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changeOwnPassword(@RequestBody ChangePasswordRequest request) {
        userService.changeOwnPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }
}

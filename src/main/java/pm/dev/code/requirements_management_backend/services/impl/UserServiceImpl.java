package pm.dev.code.requirements_management_backend.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pm.dev.code.requirements_management_backend.dto.users.*;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.enums.Role;
import pm.dev.code.requirements_management_backend.repositories.IUserRepository;
import pm.dev.code.requirements_management_backend.services.IUserService;
import pm.dev.code.requirements_management_backend.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    @Override
    public List<UserResponse> getAllUsers() {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() == Role.ADMIN || currentUser.getRol() == Role.SUPER_ADMIN) {

            return userRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        throw new AccessDeniedException("Access denied");
    }

    @Override
    public UserResponse getUserById(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getRol() == Role.ADMIN || currentUser.getRol() == Role.SUPER_ADMIN) {
            return mapToResponse(user);
        }

        if (currentUser.getRol() == Role.USUARIO && currentUser.getId().equals(id)) {
            return mapToResponse(user);
        }

        throw new AccessDeniedException("Access denied");
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(request.password()));

        if (currentUser.getRol() == Role.ADMIN) {
            user.setRol(Role.USUARIO);
        } else if (currentUser.getRol() == Role.SUPER_ADMIN) {
            user.setRol(Role.ADMIN);
        } else {
            throw new RuntimeException("You are not allowed to create users");
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUserByAdmin(Long id, UpdateUserByAdminRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new AccessDeniedException("Only ADMIN can update users");
        }

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (targetUser.getRol() != Role.USUARIO) {
            throw new AccessDeniedException("ADMIN can only update users with role USUARIO");
        }

        targetUser.setFirstName(request.firstName());
        targetUser.setLastName(request.lastName());
        targetUser.setEmail(request.email());
        targetUser.setPhone(request.phone());
        targetUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(targetUser);

        return mapToResponse(targetUser);
    }

    @Override
    public UserResponse updateOwnProfile(UpdateOwnProfileRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (userRepository.existsByUsername(request.username())
                && !currentUser.getUsername().equals(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        currentUser.setUsername(request.username());
        currentUser.setFirstName(request.firstName());
        currentUser.setLastName(request.lastName());
        currentUser.setEmail(request.email());
        currentUser.setPhone(request.phone());
        currentUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(currentUser);

        return mapToResponse(currentUser);
    }

    @Override
    public void deleteUser(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (targetUser.getRol() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("SUPER_ADMIN cannot be deleted");
        }

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new AccessDeniedException("ADMIN can only delete USERS");
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        userRepository.delete(targetUser);
    }

    @Override
    public void changeOwnPassword(ChangePasswordRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(request.currentPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
    }

    @Override
    public void resetPassword(Long id, ResetPasswordRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new AccessDeniedException("ADMIN can only reset passwords of USERS");
        }

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            if (currentUser.getId().equals(targetUser.getId())) {
                throw new AccessDeniedException("SUPER_ADMIN cannot reset own password here");
            }
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        targetUser.setPassword(passwordEncoder.encode(request.newPassword()));
        targetUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(targetUser);
    }

    @Override
    public void enableUser(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (targetUser.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already enabled");
        }

        if (targetUser.getRol() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("SUPER_ADMIN cannot be enabled");
        }

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new AccessDeniedException("ADMIN can only enable USERS");
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        targetUser.setActive(true);
        targetUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(targetUser);
    }

    @Override
    public void disableUser(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!targetUser.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already disabled");
        }

        if (targetUser.getRol() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("SUPER_ADMIN cannot be disabled");
        }

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new AccessDeniedException("ADMIN can only disable USERS");
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        targetUser.setActive(false);
        targetUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(targetUser);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRol().name(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

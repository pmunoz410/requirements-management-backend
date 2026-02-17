package pm.dev.code.requirements_management_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.users.*;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.enums.Role;
import pm.dev.code.requirements_management_backend.exceptions.business.users.*;
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

//        if (currentUser.getRol() == Role.ADMIN || currentUser.getRol() == Role.SUPER_ADMIN) {
//
//            return userRepository.findAll()
//                    .stream()
//                    .map(this::mapToResponse)
//                    .toList();
//        }
//
//        throw new UserOperationNotAllowedException(UserErrorMessage.LIST_NOT_ALLOWED);
        if (currentUser.getRol() != Role.ADMIN &&
                currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.LIST_NOT_ALLOWED);
        }

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User currentUser = securityUtils.getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (currentUser.getRol() == Role.ADMIN || currentUser.getRol() == Role.SUPER_ADMIN) {
            return mapToResponse(user);
        }

        if (currentUser.getRol() == Role.USUARIO && currentUser.getId().equals(id)) {
            return mapToResponse(user);
        }

        throw new UserOperationNotAllowedException(UserErrorMessage.VIEW_NOT_ALLOWED);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (userRepository.existsByUsername(request.username())) {
            throw new UserConflictException(UserErrorMessage.USERNAME_ALREADY_EXISTS);
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
            throw new UserOperationNotAllowedException(UserErrorMessage.CREATE_NOT_ALLOWED);
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUserByAdmin(Long id, UpdateUserByAdminRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRol() != Role.ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.UPDATE_NOT_ALLOWED);
        }

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (targetUser.getRol() != Role.USUARIO) {
            throw new UserOperationNotAllowedException(UserErrorMessage.UPDATE_NOT_ALLOWED);
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
            throw new UserConflictException(UserErrorMessage.USERNAME_ALREADY_EXISTS);
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
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (targetUser.getRol() == Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.DELETE_NOT_ALLOWED);
        }

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new UserOperationNotAllowedException(UserErrorMessage.DELETE_NOT_ALLOWED);
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.ACCESS_DENIED);
        }

        userRepository.delete(targetUser);
    }

    @Override
    public void changeOwnPassword(ChangePasswordRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(request.currentPassword(), currentUser.getPassword())) {
            throw new UserValidationException(UserErrorMessage.CURRENT_PASSWORD_INCORRECT);
        }

        currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
    }

    @Override
    public void resetPassword(Long id, ResetPasswordRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new UserOperationNotAllowedException(UserErrorMessage.RESET_PASSWORD_NOT_ALLOWED);
        }

        if (currentUser.getRol() == Role.SUPER_ADMIN) {
            if (currentUser.getId().equals(targetUser.getId())) {
                throw new UserOperationNotAllowedException(UserErrorMessage.RESET_PASSWORD_NOT_ALLOWED);
            }
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.ACCESS_DENIED);
        }

        targetUser.setPassword(passwordEncoder.encode(request.newPassword()));
        targetUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(targetUser);
    }

    @Override
    public void enableUser(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (targetUser.isActive()) {
            throw new UserConflictException(UserErrorMessage.USER_ALREADY_ENABLED);
        }

        if (targetUser.getRol() == Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.ENABLE_NOT_ALLOWED);
        }

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new UserOperationNotAllowedException(UserErrorMessage.ENABLE_NOT_ALLOWED);
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.ACCESS_DENIED);
        }

        targetUser.setActive(true);
        targetUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(targetUser);
    }

    @Override
    public void disableUser(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(UserErrorMessage.USER_NOT_FOUND));

        if (!targetUser.isActive()) {
            throw new UserConflictException(UserErrorMessage.USER_ALREADY_DISABLED);
        }

        if (targetUser.getRol() == Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.DISABLE_NOT_ALLOWED);
        }

        if (currentUser.getRol() == Role.ADMIN && targetUser.getRol() != Role.USUARIO) {
            throw new UserOperationNotAllowedException(UserErrorMessage.DISABLE_NOT_ALLOWED);
        }

        if (currentUser.getRol() != Role.ADMIN && currentUser.getRol() != Role.SUPER_ADMIN) {
            throw new UserOperationNotAllowedException(UserErrorMessage.ACCESS_DENIED);
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

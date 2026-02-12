package pm.dev.code.requirements_management_backend.services;

import pm.dev.code.requirements_management_backend.dto.users.*;

import java.util.List;

public interface IUserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUserByAdmin(Long id, UpdateUserByAdminRequest request);
    UserResponse updateOwnProfile(UpdateOwnProfileRequest  request);
    void deleteUser(Long id);

    void changeOwnPassword(ChangePasswordRequest request);
    void resetPassword(Long id, ResetPasswordRequest request);
    void enableUser(Long id);
    void disableUser(Long id);
}

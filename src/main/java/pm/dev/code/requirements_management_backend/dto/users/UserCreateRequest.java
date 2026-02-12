package pm.dev.code.requirements_management_backend.dto.users;

public record UserCreateRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        String phone
) {
}

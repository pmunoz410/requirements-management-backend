package pm.dev.code.requirements_management_backend.dto.areas;

public record ChangeAdminAreaRequest(
        Long adminId,
        Long fromAreaId,
        Long toAreaId
) {
}

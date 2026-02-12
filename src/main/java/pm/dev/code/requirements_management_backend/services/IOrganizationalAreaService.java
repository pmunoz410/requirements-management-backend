package pm.dev.code.requirements_management_backend.services;

import pm.dev.code.requirements_management_backend.dto.areas.CreateOrganizationalAreaRequest;
import pm.dev.code.requirements_management_backend.dto.areas.OrganizationalAreaResponse;
import pm.dev.code.requirements_management_backend.dto.areas.UpdateOrganizationalAreaRequest;

import java.util.List;

public interface IOrganizationalAreaService {
    List<OrganizationalAreaResponse> getAllAreas();
    OrganizationalAreaResponse getAreaById(Long id);
    OrganizationalAreaResponse createArea(CreateOrganizationalAreaRequest request);
    OrganizationalAreaResponse updateArea(Long id, UpdateOrganizationalAreaRequest request);
    void deleteArea(Long id);

    void assignAdmins(Long areaId, List<Long> adminIds);
    void removeAdmin(Long areaId, Long adminId);
    void changeAdminArea(Long adminId, Long fromAreaId, Long toAreaId);
}

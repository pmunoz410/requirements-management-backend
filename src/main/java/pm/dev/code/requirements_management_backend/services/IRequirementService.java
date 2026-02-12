package pm.dev.code.requirements_management_backend.services;

import pm.dev.code.requirements_management_backend.dto.requirements.*;

import java.util.List;

public interface IRequirementService {
    List<RequirementResponse> getAllRequirements();
    RequirementResponse getRequirementById(Long id);
    RequirementResponse createRequirement(Long workflowId,CreateRequirementRequest request);
    List<RequirementResponse> getRequirementsByWorkflow(Long workflowId);
    RequirementResponse updateRequirementDetails(Long id, UpdateRequirementDetailsRequest request);
    RequirementResponse updateRequirementStatusByAdmin(Long id, UpdateRequirementStatusByAdminRequest request);
    RequirementResponse updateRequirementStatusByUser(Long id, UpdateRequirementStatusRequest request);
    void deleteRequirement(Long id);

    RequirementResponse assignRequirement(Long requirementId, Long userId);
    RequirementResponse unassignRequirement(Long requirementId);
    void enableRequirement(Long id);
    void disableRequirement(Long id);
}

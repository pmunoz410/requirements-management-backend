package pm.dev.code.requirements_management_backend.services;

import pm.dev.code.requirements_management_backend.dto.workflows.CreateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.UpdateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.WorkflowResponse;

import java.util.List;

public interface IWorkflowService {
    List<WorkflowResponse> getAllWorkflows();
    WorkflowResponse getWorkflowById(Long id);
    WorkflowResponse createWorkflow(CreateWorkflowRequest request);
    WorkflowResponse updateWorkflow(Long id, UpdateWorkflowRequest request);
    void deleteWorkflow(Long id);

    void assignUsersToWorkflow(Long workflowId, List<Long> userIds);
    void removeUserFromWorkflow(Long workflowId, Long userId);
}

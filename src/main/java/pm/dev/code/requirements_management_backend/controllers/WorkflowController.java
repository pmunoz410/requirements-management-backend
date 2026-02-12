package pm.dev.code.requirements_management_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pm.dev.code.requirements_management_backend.dto.requirements.CreateRequirementRequest;
import pm.dev.code.requirements_management_backend.dto.requirements.RequirementResponse;
import pm.dev.code.requirements_management_backend.dto.workflows.AssignUsersToWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.CreateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.UpdateWorkflowRequest;
import pm.dev.code.requirements_management_backend.dto.workflows.WorkflowResponse;
import pm.dev.code.requirements_management_backend.services.IRequirementService;
import pm.dev.code.requirements_management_backend.services.IWorkflowService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final IWorkflowService workflowService;
    private final IRequirementService requirementService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @GetMapping
    public List<WorkflowResponse> getAllWorkflows() {
        return workflowService.getAllWorkflows();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @GetMapping("/{id}")
    public WorkflowResponse getWorkflow(@PathVariable Long id) {
        return workflowService.getWorkflowById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@RequestBody CreateWorkflowRequest request) {
        WorkflowResponse created = workflowService.createWorkflow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public WorkflowResponse updateWorkflow(@PathVariable Long id, @RequestBody UpdateWorkflowRequest request) {
        return workflowService.updateWorkflow(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{workflowId}/users")
    public ResponseEntity<Void> assignUsers(@PathVariable Long workflowId, @RequestBody AssignUsersToWorkflowRequest request) {
        workflowService.assignUsersToWorkflow(workflowId, request.userIds());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{workflowId}/users/{userId}")
    public ResponseEntity<Void> removeUser( @PathVariable Long workflowId, @PathVariable Long userId) {
        workflowService.removeUserFromWorkflow(workflowId, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @GetMapping("/{workflowId}/requirements")
    public List<RequirementResponse> getRequirementsByWorkflow(@PathVariable Long workflowId) {
        return requirementService.getRequirementsByWorkflow(workflowId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{workflowId}/requirements")
    public ResponseEntity<RequirementResponse> createRequirement(@PathVariable Long workflowId, @RequestBody CreateRequirementRequest request) {
        RequirementResponse created = requirementService.createRequirement(workflowId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

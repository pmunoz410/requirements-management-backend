package pm.dev.code.requirements_management_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pm.dev.code.requirements_management_backend.dto.requirements.*;
import pm.dev.code.requirements_management_backend.services.IRequirementService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final IRequirementService requirementService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @GetMapping
    public List<RequirementResponse> getAllRequirements() {
        return requirementService.getAllRequirements();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USUARIO')")
    @GetMapping("/{id}")
    public RequirementResponse getRequirement(@PathVariable Long id) {
        return requirementService.getRequirementById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/details")
    public RequirementResponse updateDetails(@PathVariable Long id, @RequestBody UpdateRequirementDetailsRequest request) {
        return requirementService.updateRequirementDetails(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status/admin")
    public RequirementResponse updateStatusAdmin(@PathVariable Long id, @RequestBody UpdateRequirementStatusByAdminRequest request) {
        return requirementService.updateRequirementStatusByAdmin(id, request);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PatchMapping("/{id}/status/user")
    public RequirementResponse updateStatusUser(@PathVariable Long id, @RequestBody UpdateRequirementStatusRequest request) {
        return requirementService.updateRequirementStatusByUser(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequirement(@PathVariable Long id) {
        requirementService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<RequirementResponse> assign(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(requirementService.assignRequirement(id, userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/unassign")
    public ResponseEntity<RequirementResponse> unassign(@PathVariable Long id) {
        return ResponseEntity.ok(requirementService.unassignRequirement(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enable(@PathVariable Long id) {
        requirementService.enableRequirement(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        requirementService.disableRequirement(id);
        return ResponseEntity.noContent().build();
    }
}

package pm.dev.code.requirements_management_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pm.dev.code.requirements_management_backend.dto.areas.*;
import pm.dev.code.requirements_management_backend.services.IOrganizationalAreaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class OrganizationalAreaController {

    private final IOrganizationalAreaService organizationalAreaService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public List<OrganizationalAreaResponse> getAllAreas() {
        return organizationalAreaService.getAllAreas();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/{id}")
    public OrganizationalAreaResponse getArea(@PathVariable Long id) {
        return organizationalAreaService.getAreaById(id);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<OrganizationalAreaResponse> createArea(@RequestBody CreateOrganizationalAreaRequest request) {
        OrganizationalAreaResponse created = organizationalAreaService.createArea(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public OrganizationalAreaResponse updateArea(@PathVariable Long id, @RequestBody UpdateOrganizationalAreaRequest request) {
        return organizationalAreaService.updateArea(id, request);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        organizationalAreaService.deleteArea(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{areaId}/administrators")
    public ResponseEntity<Void> assignAdmins(@PathVariable Long areaId, @RequestBody AssignAdminsRequest request) {
        organizationalAreaService.assignAdmins(areaId, request.adminIds());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{areaId}/administrators/{adminId}")
    public ResponseEntity<Void> removeAdmin(@PathVariable Long areaId, @PathVariable Long adminId) {
        organizationalAreaService.removeAdmin(areaId, adminId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/change-admin-area")
    public ResponseEntity<Void> changeAdminArea(@RequestBody ChangeAdminAreaRequest request) {
        organizationalAreaService.changeAdminArea(request.adminId(), request.fromAreaId(), request.toAreaId());
        return ResponseEntity.ok().build();
    }
}

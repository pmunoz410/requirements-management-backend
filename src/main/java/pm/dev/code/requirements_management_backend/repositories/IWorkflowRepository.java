package pm.dev.code.requirements_management_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.entities.Workflow;

import java.util.List;

public interface IWorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByAdministrator(User administrator);
    List<Workflow> findByUsersContaining(User user);
}

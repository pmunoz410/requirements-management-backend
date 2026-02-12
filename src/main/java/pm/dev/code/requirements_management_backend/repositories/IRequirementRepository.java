package pm.dev.code.requirements_management_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pm.dev.code.requirements_management_backend.entities.Requirement;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.entities.Workflow;

import java.util.List;

public interface IRequirementRepository extends JpaRepository<Requirement, Long> {
    @Query("SELECT r FROM Requirement r WHERE r.workflow.administrator = :admin")
    List<Requirement> findByWorkflowAdministrator(@Param("admin") User admin);
    List<Requirement> findByAssignee(User assignee);

    List<Requirement> findByWorkflow(Workflow workflow);
    List<Requirement> findByWorkflowAndAssignee(Workflow workflow, User assignee);
}

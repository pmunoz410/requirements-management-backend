package pm.dev.code.requirements_management_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pm.dev.code.requirements_management_backend.entities.Comment;
import pm.dev.code.requirements_management_backend.entities.Requirement;
import pm.dev.code.requirements_management_backend.enums.CommentType;

import java.util.List;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRequirement(Requirement requirement);
    List<Comment> findByRequirementAndType(Requirement requirement, CommentType type);
}

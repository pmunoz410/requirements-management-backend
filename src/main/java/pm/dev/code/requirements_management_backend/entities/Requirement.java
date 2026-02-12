package pm.dev.code.requirements_management_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pm.dev.code.requirements_management_backend.enums.Priority;
import pm.dev.code.requirements_management_backend.enums.RequirementClassification;
import pm.dev.code.requirements_management_backend.enums.RequirementStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementClassification classification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementStatus status = RequirementStatus.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}

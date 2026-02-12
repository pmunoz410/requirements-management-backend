package pm.dev.code.requirements_management_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Organizational area
    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private OrganizationalArea area;

    // Responsible administrator
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User administrator;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    // Users assigned to workflow (N:M)
    @ManyToMany
    @JoinTable(
            name = "workflow_users",
            joinColumns = @JoinColumn(name = "workflow_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    // Requirements under this workflow (1:N)
    @OneToMany(mappedBy = "workflow")
    private List<Requirement> requirements;
}

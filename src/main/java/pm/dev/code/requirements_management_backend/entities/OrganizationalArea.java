package pm.dev.code.requirements_management_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "organizational_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationalArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // Area administrators (N:M)
    @ManyToMany
    @JoinTable(
            name = "area_administrators",
            joinColumns = @JoinColumn(name = "area_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> administrators;

    // Workflows under this area (1:N)
    @JsonIgnore
    @OneToMany(mappedBy = "area")
    private List<Workflow> workflows;
}

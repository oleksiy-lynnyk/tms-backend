package org.example.tmsstriker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

/**
 * Зв'язок між користувачем та проектом з роллю.
 * Визначає, які користувачі мають доступ до проекту та з якими правами.
 */
@Data
@Entity
@Table(name = "project_member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "user_id"})
})
public class ProjectMember {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectRole role;

    /**
     * Перевірка, чи має користувач роль адміністратора в проекті.
     */
    public boolean isAdmin() {
        return role == ProjectRole.ADMIN;
    }

    /**
     * Перевірка, чи має користувач роль менеджера або вище.
     */
    public boolean isManagerOrHigher() {
        return role == ProjectRole.ADMIN || role == ProjectRole.MANAGER;
    }

    /**
     * Перевірка, чи може користувач редагувати в проекті.
     */
    public boolean canEdit() {
        return role != ProjectRole.VIEWER;
    }
}
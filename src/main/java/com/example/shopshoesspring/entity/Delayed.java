package com.example.shopshoesspring.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "_delayed")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Delayed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delayed_id", nullable = false)
    private Long id;

    @Column(name = "subnet", nullable = false, length = 800)
    private String subnet;

    @Column(name = "_path", nullable = false, length = 800)
    private String path;

    @Column(name = "_login", nullable = false, length = 800)
    private String login;

    @Column(name = "_password", nullable = false, length = 800)
    private String password;

    @Column(name = "_scan_date", nullable = false)
    private Instant scanDate;

    @Column(name = "ip_SMB", nullable = false, length = 800)
    private String ipSmb;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Delayed delayed = (Delayed) o;
        return getId() != null && Objects.equals(getId(), delayed.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
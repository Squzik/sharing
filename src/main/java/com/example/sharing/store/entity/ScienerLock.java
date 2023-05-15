package org.example.sharing.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sciener_lock")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScienerLock {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "lock_name", nullable = false)
    private String lockName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sciener_user_id", nullable = false)
    private ScienerUser scienerUser;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "scienerLock")
    private Flat flat;

    @OneToMany(mappedBy = "lock")
    private Set<ScienerCode> codes;

    @Column(name = "is_linked")
    private Boolean isLinked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScienerLock that = (ScienerLock) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

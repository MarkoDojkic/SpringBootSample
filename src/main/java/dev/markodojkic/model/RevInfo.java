package dev.markodojkic.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo")
@RevisionEntity
public class RevInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo-seq")
    @SequenceGenerator(
            name = "revinfo-seq",
            sequenceName = "revinfo_seq",
            allocationSize = 1)
    @RevisionNumber
    private Long rev;

    @RevisionTimestamp
    private long revtstmp;

    public Long getRev() {
        return rev;
    }

    public long getRevtstmp() {
        return revtstmp;
    }
}

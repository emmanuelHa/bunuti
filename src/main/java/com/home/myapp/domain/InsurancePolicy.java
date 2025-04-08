package com.home.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "insurancepolicy")
public class InsurancePolicy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Integer id;

    @Column(name = "policy_name")
    @NotEmpty(message = "PolicyName can not be blank")
    private String policyName;

    @Column(nullable = false)
    private StatusInsurancePolicy status;

    @Basic
    @Temporal(TemporalType.TIME)
    @Column(nullable = false, name = "coverage_start_date")
    private Date coverageStartDate;

    @Basic
    @Temporal(TemporalType.TIME)
    @Column(nullable = false, name = "coverage_end_date")
    private Date coverageEndDate;

    @Column(nullable = false, name = "creation_date")
    @CreationTimestamp
    // TODO not a Date
    private Instant creationDate;

    @Column(nullable = false, name = "update_date")
    @UpdateTimestamp
    // TODO not a Date
    private Instant updateDate;

    // TODO Bad type
    public Integer getId() {
        return this.id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public StatusInsurancePolicy getStatus() {
        return status;
    }

    public void setStatus(StatusInsurancePolicy status) {
        this.status = status;
    }

    public Date getCoverageStartDate() {
        return coverageStartDate;
    }

    public void setCoverageStartDate(Date coverageStartDate) {
        this.coverageStartDate = coverageStartDate;
    }

    public Date getCoverageEndDate() {
        return coverageEndDate;
    }

    public void setCoverageEndDate(Date coverageEndDate) {
        this.coverageEndDate = coverageEndDate;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InsurancePolicy)) {
            return false;
        }
        return getId() != null && getId().equals(((InsurancePolicy) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "InsurancePolicy{" +
                "id=" + getId() +
                ", policyName='" + getPolicyName() + "'" +
                ", status=" + getStatus() +
                ", coverageStartDate=" + getCoverageStartDate() +
                ", coverageEndDate=" + getCoverageEndDate() +
                ", coverageEndDate=" + getCoverageEndDate() +
                ", creationDate=" + getCreationDate() +
                ", updateDate=" + getUpdateDate() +
                "}";
    }
}

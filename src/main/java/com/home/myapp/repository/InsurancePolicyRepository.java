package com.home.myapp.repository;

import com.home.myapp.domain.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Integer> {
    // Add custom request defined by uses case
}

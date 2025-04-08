package com.home.myapp.web.rest;

import com.home.myapp.domain.InsurancePolicy;
import com.home.myapp.repository.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link InsurancePolicy}.
 */
@RestController
@RequestMapping("/api/insurancepolicies")
@Transactional
public class InsurancePolicyResource {
    private static final Logger LOG = LoggerFactory.getLogger(InsurancePolicyResource.class);

    private static final String ENTITY_NAME = "insurancepolicy";

    private final InsurancePolicyRepository insurancePolicyRepository;

    public InsurancePolicyResource(InsurancePolicyRepository InsurancePolicyRepository) {
        this.insurancePolicyRepository = InsurancePolicyRepository;
    }

    /**
     * {@code POST  /InsurancePolicies} : Create a new InsurancePolicy.
     *
     * @param insurancePolicy the InsurancePolicy to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new InsurancePolicy, or with status {@code 400 (Bad Request)} if the InsurancePolicy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InsurancePolicy> createInsurancePolicy(@RequestBody InsurancePolicy insurancePolicy) throws URISyntaxException {
        LOG.debug("REST request to save InsurancePolicy : {}", insurancePolicy);
        if (insurancePolicy.getId() != null) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new Error("A new job cannot already have an ID"));
        }
        insurancePolicy = insurancePolicyRepository.save(insurancePolicy);
        return ResponseEntity.created(new URI("/api/insurancePolicies/" + insurancePolicy.getId()))
                .body(insurancePolicy);
    }

    /**
     * {@code PUT  /InsurancePolicys/:id} : Updates an existing InsurancePolicy.
     *
     * @param id the id of the InsurancePolicy to save.
     * @param insurancePolicy the insurancePolicy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated InsurancePolicy,
     * or with status {@code 400 (Bad Request)} if the InsurancePolicy is not valid,
     * or with status {@code 500 (Internal Server Error)} if the InsurancePolicy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InsurancePolicy> updateInsurancePolicy(@PathVariable(value = "id", required = false) final Long id, @RequestBody InsurancePolicy insurancePolicy)
            throws URISyntaxException {
        LOG.debug("REST request to update InsurancePolicy : {}, {}", id, insurancePolicy);
        if (insurancePolicy.getId() == null) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new Error("Invalid id"));
        }
        if (!Objects.equals(id, Long.valueOf(insurancePolicy.getId()))) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new Error("Invalid id"));
        }

        if (!insurancePolicyRepository.existsById(id.intValue())) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new Error("Entity not found"));
        }

        insurancePolicy = insurancePolicyRepository.save(insurancePolicy);
        return ResponseEntity.ok().body(insurancePolicy);
    }

    /**
     * {@code PATCH  /InsurancePolicys/:id} : Partial updates given fields of an existing InsurancePolicy, field will ignore if it is null
     *
     * @param id the id of the InsurancePolicy to save.
     * @param insurancePolicy the InsurancePolicy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated InsurancePolicy,
     * or with status {@code 400 (Bad Request)} if the InsurancePolicy is not valid,
     * or with status {@code 404 (Not Found)} if the InsurancePolicy is not found,
     * or with status {@code 500 (Internal Server Error)} if the InsurancePolicy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InsurancePolicy> partialUpdateInsurancePolicy(@PathVariable(value = "id", required = false) final Long id, @RequestBody InsurancePolicy insurancePolicy)
            throws URISyntaxException {
        LOG.debug("REST request to partial update InsurancePolicy partially : {}, {}", id, insurancePolicy);
        if (insurancePolicy.getId() == null) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new Error("Invalid id"));
        }
        if (!Objects.equals(id, Long.valueOf(insurancePolicy.getId()))) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new Error("Invalid id"));
        }

        if (!insurancePolicyRepository.existsById(id.intValue())) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new Error("Entity not found"));
        }

        Optional<InsurancePolicy> result = insurancePolicyRepository
                .findById(insurancePolicy.getId())
                .map(existingInsurancePolicy -> {
                    if (insurancePolicy.getPolicyName() != null) {
                        existingInsurancePolicy.setPolicyName(insurancePolicy.getPolicyName());
                    }
                    if (insurancePolicy.getStatus() != null) {
                        existingInsurancePolicy.setStatus(insurancePolicy.getStatus());
                    }
                    if (insurancePolicy.getCoverageStartDate() != null) {
                        existingInsurancePolicy.setCoverageStartDate(insurancePolicy.getCoverageStartDate());
                    }
                    if (insurancePolicy.getCoverageEndDate() != null) {
                        existingInsurancePolicy.setCoverageEndDate(insurancePolicy.getCoverageEndDate());
                    }

                    return existingInsurancePolicy;
                })
                .map(insurancePolicyRepository::save);

        // TODO deal with headers
        return result.map((insurance) -> ResponseEntity.ok().body(insurance))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@code GET  /insurancePolicies} : get all the InsurancePolicies.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of InsurancePolicies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<InsurancePolicy>> getAllInsurancePolicies(
            Pageable pageable,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {

        LOG.debug("REST request to get a page of insurancePolicies");
        Page<InsurancePolicy> page;
        if (eagerload) {
            //page = insurancePolicyRepository.findAllWithEagerRelationships(pageable);
            page = insurancePolicyRepository.findAll(pageable);
        } else {
            page = insurancePolicyRepository.findAll(pageable);
        }
        // TODO deal with headers
        return ResponseEntity.ok().body(page.getContent());
    }

    /**
     * {@code GET  /InsurancePolicies/:id} : get the "id" InsurancePolicy.
     *
     * @param id the id of the InsurancePolicy to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the InsurancePolicy, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InsurancePolicy> getInsurancePolicy(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InsurancePolicy : {}", id);
        //Optional<InsurancePolicy> insurancePolicy = insurancePolicyRepository.findOneWithEagerRelationships(id.intValue());
        Optional<InsurancePolicy> insurancePolicy = insurancePolicyRepository.findById(id.intValue());
        // TODO deal with headers
        return insurancePolicy.map((insurance) -> ResponseEntity.ok().body(insurance))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@code DELETE  /insurancePolicy/:id} : delete the "id" InsurancePolicy.
     *
     * @param id the id of the InsurancePolicy to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurancePolicy(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete insurancePolicy : {}", id);
        insurancePolicyRepository.deleteById(id.intValue());
        // TODO deal with headers
        return ResponseEntity.noContent().build();
    }

}

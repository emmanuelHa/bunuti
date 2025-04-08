package com.home.myapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.myapp.config.JacksonConfiguration;
import com.home.myapp.domain.InsurancePolicy;
import com.home.myapp.domain.StatusInsurancePolicy;
import com.home.myapp.repository.InsurancePolicyRepository;
import com.home.myapp.web.rest.InsurancePolicyResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Integration tests for the {@link InsurancePolicyResource} REST controller.
 */

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
@SpringBootTest(classes = { MyappApplication.class,
        JacksonConfiguration.class})
public class InsurancePolicyResourceIT {

    public static final LocalDateTime COVERAGE_START_DATE = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final String DEFAULT_INSURANCE_TITLE = "AAAAAAAAAA";

    private static final String ENTITY_API_URL = "/api/insurancepolicies";
    public static final LocalDateTime COVERAGE_END_DATE = LocalDateTime.of(2025, 10, 1, 10, 0);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;

    @Autowired
    private MockMvc restJobMockMvc;

    private InsurancePolicy insurancePolicy;

    private InsurancePolicy insertedInsurancePolicy;

    public static InsurancePolicy createEntity() {
        InsurancePolicy insurancePolicy = new InsurancePolicy();
        insurancePolicy.setPolicyName(DEFAULT_INSURANCE_TITLE);
        insurancePolicy.setStatus(StatusInsurancePolicy.ACTIVE);
        Date date = Date.from(COVERAGE_START_DATE.atZone(ZoneId.systemDefault()).toInstant());
        insurancePolicy.setCoverageStartDate(date);
        Date endDate = Date.from(COVERAGE_END_DATE.atZone(ZoneId.systemDefault()).toInstant());
        insurancePolicy.setCoverageEndDate(endDate);
        return insurancePolicy;
    }

    @BeforeEach
    void initTest() {
        insurancePolicy = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInsurancePolicy != null) {
            insurancePolicyRepository.delete(insertedInsurancePolicy);
            insertedInsurancePolicy = null;
        }
    }

    @Test
    @Transactional
    void createJob() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Insurance policy in DB
        om.readValue(
                restJobMockMvc
                        .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsBytes(insurancePolicy)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(),
                InsurancePolicy.class
        );

        // Validate the Insurance policy in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
    }

    protected long getRepositoryCount() {
        return insurancePolicyRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

}

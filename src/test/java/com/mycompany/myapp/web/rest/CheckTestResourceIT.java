package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.CheckTest;
import com.mycompany.myapp.repository.CheckTestRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CheckTestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CheckTestResourceIT {

    private static final String DEFAULT_CHECK = "AAAAAAAAAA";
    private static final String UPDATED_CHECK = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/check-tests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CheckTestRepository checkTestRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCheckTestMockMvc;

    private CheckTest checkTest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckTest createEntity(EntityManager em) {
        CheckTest checkTest = new CheckTest().check(DEFAULT_CHECK).name(DEFAULT_NAME);
        return checkTest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CheckTest createUpdatedEntity(EntityManager em) {
        CheckTest checkTest = new CheckTest().check(UPDATED_CHECK).name(UPDATED_NAME);
        return checkTest;
    }

    @BeforeEach
    public void initTest() {
        checkTest = createEntity(em);
    }

    @Test
    @Transactional
    void createCheckTest() throws Exception {
        int databaseSizeBeforeCreate = checkTestRepository.findAll().size();
        // Create the CheckTest
        restCheckTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(checkTest)))
            .andExpect(status().isCreated());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeCreate + 1);
        CheckTest testCheckTest = checkTestList.get(checkTestList.size() - 1);
        assertThat(testCheckTest.getCheck()).isEqualTo(DEFAULT_CHECK);
        assertThat(testCheckTest.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createCheckTestWithExistingId() throws Exception {
        // Create the CheckTest with an existing ID
        checkTest.setId(1L);

        int databaseSizeBeforeCreate = checkTestRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCheckTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(checkTest)))
            .andExpect(status().isBadRequest());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCheckTests() throws Exception {
        // Initialize the database
        checkTestRepository.saveAndFlush(checkTest);

        // Get all the checkTestList
        restCheckTestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkTest.getId().intValue())))
            .andExpect(jsonPath("$.[*].check").value(hasItem(DEFAULT_CHECK)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getCheckTest() throws Exception {
        // Initialize the database
        checkTestRepository.saveAndFlush(checkTest);

        // Get the checkTest
        restCheckTestMockMvc
            .perform(get(ENTITY_API_URL_ID, checkTest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(checkTest.getId().intValue()))
            .andExpect(jsonPath("$.check").value(DEFAULT_CHECK))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingCheckTest() throws Exception {
        // Get the checkTest
        restCheckTestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCheckTest() throws Exception {
        // Initialize the database
        checkTestRepository.saveAndFlush(checkTest);

        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();

        // Update the checkTest
        CheckTest updatedCheckTest = checkTestRepository.findById(checkTest.getId()).get();
        // Disconnect from session so that the updates on updatedCheckTest are not directly saved in db
        em.detach(updatedCheckTest);
        updatedCheckTest.check(UPDATED_CHECK).name(UPDATED_NAME);

        restCheckTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCheckTest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCheckTest))
            )
            .andExpect(status().isOk());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
        CheckTest testCheckTest = checkTestList.get(checkTestList.size() - 1);
        assertThat(testCheckTest.getCheck()).isEqualTo(UPDATED_CHECK);
        assertThat(testCheckTest.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingCheckTest() throws Exception {
        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();
        checkTest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkTest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(checkTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCheckTest() throws Exception {
        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();
        checkTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(checkTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCheckTest() throws Exception {
        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();
        checkTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckTestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(checkTest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCheckTestWithPatch() throws Exception {
        // Initialize the database
        checkTestRepository.saveAndFlush(checkTest);

        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();

        // Update the checkTest using partial update
        CheckTest partialUpdatedCheckTest = new CheckTest();
        partialUpdatedCheckTest.setId(checkTest.getId());

        partialUpdatedCheckTest.check(UPDATED_CHECK).name(UPDATED_NAME);

        restCheckTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCheckTest))
            )
            .andExpect(status().isOk());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
        CheckTest testCheckTest = checkTestList.get(checkTestList.size() - 1);
        assertThat(testCheckTest.getCheck()).isEqualTo(UPDATED_CHECK);
        assertThat(testCheckTest.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateCheckTestWithPatch() throws Exception {
        // Initialize the database
        checkTestRepository.saveAndFlush(checkTest);

        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();

        // Update the checkTest using partial update
        CheckTest partialUpdatedCheckTest = new CheckTest();
        partialUpdatedCheckTest.setId(checkTest.getId());

        partialUpdatedCheckTest.check(UPDATED_CHECK).name(UPDATED_NAME);

        restCheckTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCheckTest))
            )
            .andExpect(status().isOk());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
        CheckTest testCheckTest = checkTestList.get(checkTestList.size() - 1);
        assertThat(testCheckTest.getCheck()).isEqualTo(UPDATED_CHECK);
        assertThat(testCheckTest.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingCheckTest() throws Exception {
        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();
        checkTest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, checkTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(checkTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCheckTest() throws Exception {
        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();
        checkTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(checkTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCheckTest() throws Exception {
        int databaseSizeBeforeUpdate = checkTestRepository.findAll().size();
        checkTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckTestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(checkTest))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CheckTest in the database
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCheckTest() throws Exception {
        // Initialize the database
        checkTestRepository.saveAndFlush(checkTest);

        int databaseSizeBeforeDelete = checkTestRepository.findAll().size();

        // Delete the checkTest
        restCheckTestMockMvc
            .perform(delete(ENTITY_API_URL_ID, checkTest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CheckTest> checkTestList = checkTestRepository.findAll();
        assertThat(checkTestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

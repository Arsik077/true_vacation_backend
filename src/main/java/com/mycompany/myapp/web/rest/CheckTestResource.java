package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.CheckTest;
import com.mycompany.myapp.repository.CheckTestRepository;
import com.mycompany.myapp.service.CheckTestService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.CheckTest}.
 */
@RestController
@RequestMapping("/api")
public class CheckTestResource {

    private final Logger log = LoggerFactory.getLogger(CheckTestResource.class);

    private static final String ENTITY_NAME = "checkTest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CheckTestService checkTestService;

    private final CheckTestRepository checkTestRepository;

    public CheckTestResource(CheckTestService checkTestService, CheckTestRepository checkTestRepository) {
        this.checkTestService = checkTestService;
        this.checkTestRepository = checkTestRepository;
    }

    /**
     * {@code POST  /check-tests} : Create a new checkTest.
     *
     * @param checkTest the checkTest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new checkTest, or with status {@code 400 (Bad Request)} if the checkTest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/check-tests")
    public ResponseEntity<CheckTest> createCheckTest(@RequestBody CheckTest checkTest) throws URISyntaxException {
        log.debug("REST request to save CheckTest : {}", checkTest);
        if (checkTest.getId() != null) {
            throw new BadRequestAlertException("A new checkTest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CheckTest result = checkTestService.save(checkTest);
        return ResponseEntity
            .created(new URI("/api/check-tests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /check-tests/:id} : Updates an existing checkTest.
     *
     * @param id the id of the checkTest to save.
     * @param checkTest the checkTest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkTest,
     * or with status {@code 400 (Bad Request)} if the checkTest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the checkTest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/check-tests/{id}")
    public ResponseEntity<CheckTest> updateCheckTest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CheckTest checkTest
    ) throws URISyntaxException {
        log.debug("REST request to update CheckTest : {}, {}", id, checkTest);
        if (checkTest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkTest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkTestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CheckTest result = checkTestService.save(checkTest);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, checkTest.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /check-tests/:id} : Partial updates given fields of an existing checkTest, field will ignore if it is null
     *
     * @param id the id of the checkTest to save.
     * @param checkTest the checkTest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkTest,
     * or with status {@code 400 (Bad Request)} if the checkTest is not valid,
     * or with status {@code 404 (Not Found)} if the checkTest is not found,
     * or with status {@code 500 (Internal Server Error)} if the checkTest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/check-tests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CheckTest> partialUpdateCheckTest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CheckTest checkTest
    ) throws URISyntaxException {
        log.debug("REST request to partial update CheckTest partially : {}, {}", id, checkTest);
        if (checkTest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkTest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkTestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CheckTest> result = checkTestService.partialUpdate(checkTest);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, checkTest.getId().toString())
        );
    }

    /**
     * {@code GET  /check-tests} : get all the checkTests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of checkTests in body.
     */
    @GetMapping("/check-tests")
    public List<CheckTest> getAllCheckTests() {
        log.debug("REST request to get all CheckTests");
        return checkTestService.findAll();
    }

    /**
     * {@code GET  /check-tests/:id} : get the "id" checkTest.
     *
     * @param id the id of the checkTest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the checkTest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/check-tests/{id}")
    public ResponseEntity<CheckTest> getCheckTest(@PathVariable Long id) {
        log.debug("REST request to get CheckTest : {}", id);
        Optional<CheckTest> checkTest = checkTestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(checkTest);
    }

    /**
     * {@code DELETE  /check-tests/:id} : delete the "id" checkTest.
     *
     * @param id the id of the checkTest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/check-tests/{id}")
    public ResponseEntity<Void> deleteCheckTest(@PathVariable Long id) {
        log.debug("REST request to delete CheckTest : {}", id);
        checkTestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.CheckTest;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link CheckTest}.
 */
public interface CheckTestService {
    /**
     * Save a checkTest.
     *
     * @param checkTest the entity to save.
     * @return the persisted entity.
     */
    CheckTest save(CheckTest checkTest);

    /**
     * Partially updates a checkTest.
     *
     * @param checkTest the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CheckTest> partialUpdate(CheckTest checkTest);

    /**
     * Get all the checkTests.
     *
     * @return the list of entities.
     */
    List<CheckTest> findAll();

    /**
     * Get the "id" checkTest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CheckTest> findOne(Long id);

    /**
     * Delete the "id" checkTest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

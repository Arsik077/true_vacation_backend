package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.CheckTest;
import com.mycompany.myapp.repository.CheckTestRepository;
import com.mycompany.myapp.service.CheckTestService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CheckTest}.
 */
@Service
@Transactional
public class CheckTestServiceImpl implements CheckTestService {

    private final Logger log = LoggerFactory.getLogger(CheckTestServiceImpl.class);

    private final CheckTestRepository checkTestRepository;

    public CheckTestServiceImpl(CheckTestRepository checkTestRepository) {
        this.checkTestRepository = checkTestRepository;
    }

    @Override
    public CheckTest save(CheckTest checkTest) {
        log.debug("Request to save CheckTest : {}", checkTest);
        return checkTestRepository.save(checkTest);
    }

    @Override
    public Optional<CheckTest> partialUpdate(CheckTest checkTest) {
        log.debug("Request to partially update CheckTest : {}", checkTest);

        return checkTestRepository
            .findById(checkTest.getId())
            .map(existingCheckTest -> {
                if (checkTest.getCheck() != null) {
                    existingCheckTest.setCheck(checkTest.getCheck());
                }
                if (checkTest.getName() != null) {
                    existingCheckTest.setName(checkTest.getName());
                }

                return existingCheckTest;
            })
            .map(checkTestRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckTest> findAll() {
        log.debug("Request to get all CheckTests");
        return checkTestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CheckTest> findOne(Long id) {
        log.debug("Request to get CheckTest : {}", id);
        return checkTestRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CheckTest : {}", id);
        checkTestRepository.deleteById(id);
    }
}

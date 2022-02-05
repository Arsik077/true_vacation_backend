package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CheckTest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CheckTest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CheckTestRepository extends JpaRepository<CheckTest, Long> {}

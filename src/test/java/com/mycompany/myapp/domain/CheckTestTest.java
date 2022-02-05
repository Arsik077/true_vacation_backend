package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheckTestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CheckTest.class);
        CheckTest checkTest1 = new CheckTest();
        checkTest1.setId(1L);
        CheckTest checkTest2 = new CheckTest();
        checkTest2.setId(checkTest1.getId());
        assertThat(checkTest1).isEqualTo(checkTest2);
        checkTest2.setId(2L);
        assertThat(checkTest1).isNotEqualTo(checkTest2);
        checkTest1.setId(null);
        assertThat(checkTest1).isNotEqualTo(checkTest2);
    }
}

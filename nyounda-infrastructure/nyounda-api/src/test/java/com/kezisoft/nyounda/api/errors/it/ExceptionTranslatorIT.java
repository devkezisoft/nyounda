package com.kezisoft.nyounda.api.errors.it;

import com.kezisoft.nyounda.api.it.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class ExceptionTranslatorIT extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Context loads for ExceptionTranslator")
    void contextLoads() throws Exception {
        // Intentionally blank: ensures Spring can create the context with this controller.
    }

    @Test
    @DisplayName("No explicit mappings found; manual test placeholder")
    void placeholder() {
    }

}

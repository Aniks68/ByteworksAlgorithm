package com.aniks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankTransactionProcessTest {

    String path;
    BankTransactionProcess processor;
    @BeforeEach
    void setUp() {
        path = "src/com/aniks/test-request.txt";
        processor = new BankTransactionProcess();
    }

    @Test
    @DisplayName("To test if method accurately returns transaction status")
    void processClientRequest() throws IOException {
        List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add("NO");
        expectedOutput.add("YES");
        expectedOutput.add("YES");
        expectedOutput.add("NO");
        List<String> actualOutput = processor.processClientRequest(path);

        assertEquals(expectedOutput, actualOutput);
    }
}
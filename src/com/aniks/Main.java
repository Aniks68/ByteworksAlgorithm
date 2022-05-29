package com.aniks;

import java.io.IOException;

import static com.aniks.BankTransactionProcess.processClientRequest;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println(processClientRequest("src/com/aniks/client-request.txt"));
    }
}

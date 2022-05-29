package com.aniks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionResponse {
    private Map<String, Double> childProbabilities;
    private List<ArrayList<String>> bankLinks;

    public TransactionResponse(Map<String, Double> childProbabilities, List<ArrayList<String>> bankLinks) {
        this.childProbabilities = childProbabilities;
        this.bankLinks = bankLinks;
    }

    public Map<String, Double> getChildProbabilities() {
        return childProbabilities;
    }

    public void setChildProbabilities(Map<String, Double> childProbabilities) {
        this.childProbabilities = childProbabilities;
    }

    public List<ArrayList<String>> getBankLinks() {
        return bankLinks;
    }

    public void setBankLinks(List<ArrayList<String>> bankLinks) {
        this.bankLinks = bankLinks;
    }
}

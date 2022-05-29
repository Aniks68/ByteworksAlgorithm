package com.aniks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BankTransactionProcess {

    public static List<String> processClientRequest(String path) throws IOException {
        String fileLine;
        List<String> finalResponse = new ArrayList<>();

        try(BufferedReader fileReader = new BufferedReader(new FileReader(path))) {
            TransactionResponse transactionResponse = bankNodesAnalysis();
            List<ArrayList<String>> bankLinks = transactionResponse.getBankLinks();
            Map<String, Double> childProbabilities = transactionResponse.getChildProbabilities();

            while((fileLine = fileReader.readLine()) != null) {
                if(fileLine.equals("END")) return finalResponse;

                String[] requestArray = fileLine.split(",");
                double successStandard = Double.parseDouble(requestArray[2]);
                List<String> source = new ArrayList<>();
                List<String> destination = new ArrayList<>();
                List<String> singlePath = new ArrayList<>();

                int singlePathIndex = sortingRequestSources(bankLinks, requestArray, source, destination, singlePath);

                Double probability = getActualProbability(childProbabilities, requestArray, source, destination, singlePath, singlePathIndex);

                finalResponse.add(probability >= successStandard ? "YES" : "NO");
            }
        }

        return finalResponse;
    }

    private static TransactionResponse bankNodesAnalysis() throws IOException {
        List<ArrayList<String>> collect = new ArrayList<>();
        String fileLine;
        Set<String> parentNodes = new HashSet<>();
        Map<String, Double> childNodes = new HashMap<>();
        ArrayList<ArrayList<String>> setupCollection = new ArrayList<>();

        try(BufferedReader fileReader = new BufferedReader(new FileReader("src/com/aniks/teradata.txt"))) {
            int banks = Integer.parseInt(fileReader.readLine());

            while ((fileLine = fileReader.readLine()) != null) {
                String[] lineArray = fileLine.split(",");
                parentNodes.add(lineArray[0]);
                childNodes.put(lineArray[1], Double.parseDouble(lineArray[2])/100);

                for(String el : parentNodes) {
                    ArrayList<String> test = new ArrayList<>();
                    if(childNodes.containsKey(el)) {
                        test.add(el);
                        if(!setupCollection.contains(test))
                            setupCollection.add(test);
                    }
                }

                for (ArrayList el : setupCollection) {
                    if(el.contains(lineArray[0])) {
                        el.add(lineArray[1]);
                    }
                }
            }

            collect = setupCollection.stream().filter(innerList -> innerList.size() == 3).collect(Collectors.toList());
        }
        TransactionResponse response = new TransactionResponse(childNodes, collect);
        return response;
    }

    private static int sortingRequestSources(List<ArrayList<String>> bankLinks, String[] requestArray, List<String> source, List<String> destination, List<String> singlePath) {
        boolean singlePathCheck = Arrays.stream(requestArray).noneMatch(x -> x.equals("1"));
        int singlePathIndex = 0;

        for (ArrayList list : bankLinks) {
            if(list.contains(requestArray[0]) && list.contains(requestArray[1])) {
                source.addAll(list);
                destination.addAll(list);
            } else if (singlePathCheck && list.contains(requestArray[0])) {
                source.addAll(list);
            } else if (singlePathCheck && list.contains(requestArray[1])) {
                destination.addAll(list);
            } else if (!singlePathCheck && list.contains(requestArray[1])) {
                singlePath.addAll(list);
                singlePathIndex = getSingleIndex(requestArray, singlePath, 1);
            } else if (!singlePathCheck && list.contains(requestArray[0])) {
                singlePath.addAll(list);
                singlePathIndex = getSingleIndex(requestArray, singlePath, 0);
            }

        }
        return singlePathIndex;
    }

    private static int getSingleIndex(String[] requestArray, List<String> singlePath, int position) {
        int singlePathIndex = 0;
        for (int i = 0; i < singlePath.size(); i++) {
            if (singlePath.get(i).equals(requestArray[position])) {
                singlePathIndex= i;
            }
        }
        return singlePathIndex;
    }

    private static Double getActualProbability(Map<String, Double> childProbabilities, String[] requestArray, List<String> source, List<String> destination, List<String> singlePath, int singlePathIndex) {
        double prob = 0;

        boolean lengthCheck = source.size() > 0 && destination.size() > 0;

        if(lengthCheck && !source.equals(destination)) {
            double sourceLineProbability = source.get(0).equals(requestArray[0]) ? childProbabilities.get(requestArray[0]) : Double.valueOf(childProbabilities.get(requestArray[0]) * childProbabilities.get(source.get(0)));

            double endLineProbability;
            if(!destination.get(0).equals(requestArray[1])) {
                endLineProbability = childProbabilities.get(requestArray[1]) * childProbabilities.get(destination.get(0));
            } else {
                endLineProbability = childProbabilities.get(destination.get(0));
            }
            prob = sourceLineProbability * endLineProbability;
        } else if(lengthCheck && source.equals(destination)) {
            if(Arrays.stream(requestArray).anyMatch(x-> x.equals(source.get(0)))) {
                int index = 0;
                for (int i = 0; i < requestArray.length-1; i++) {
                    if (!requestArray[i].equals(source.get(0))) {
                        index = i;
                    }
                }
                int singleIndex = getSingleIndex(requestArray, source, index);
                return childProbabilities.get(source.get(singleIndex));
            }
            prob = childProbabilities.get(requestArray[0]) * childProbabilities.get(requestArray[1]);
        } else if (singlePath.size() > 0) {
            prob = singlePath.get(0).equals(requestArray[0]) ?
                    childProbabilities.get(requestArray[0]) :
                    childProbabilities.get(singlePath.get(singlePathIndex)) * childProbabilities.get(singlePath.get(0));
        }
        return prob;
    }
}

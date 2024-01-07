/*
 * Copyright (c) 2023. [tomrut] https://github.com/tomrut
 */
package com.tomrut.streams.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NestedCollectionsLookup {

    @FunctionalInterface
    public interface FindByParentAndChildKeyFunction {
        int  findByParentAndChildKey(String pKey, String cKey, List<ParentItem> parentItems);
    }

    private static final Map<String, List<Long>> functionCallTimes = new HashMap<>();

    public static void main(String[] args) {

        int iterations = 10000;
        boolean byFunction = true;

        List<ParentItem> parentItems = new ArrayList<>();
        int childCount = 10;
        int parentCount = 10;
        for (int i = 0; i < parentCount; i++) {
            List<ChildItem> childItemList = new ArrayList<>();
            parentItems.add(new ParentItem("pKey" + i, childItemList));
            for (int j = 0; j < childCount; j++) {
                childItemList.add(new ChildItem("childKey" + j, (j + i) * (i + 1)));
            }
        }

        String searchPKey = "pKey" + (parentCount / 2);
        String searchCKey = "childKey" + (childCount / 2);
        for (int i = 0; i < 10; i++) {
            if (byFunction)
                runAllMethodsByFunction(iterations, parentItems, searchPKey, searchCKey);
            else
                runAllReal(iterations, parentItems, searchPKey, searchCKey);
        }

        for (String functionName : functionCallTimes.keySet()) {
            List<Long> runTimes = functionCallTimes.get(functionName);
            Double average = runTimes.stream().collect(Collectors.averagingLong(Long::longValue));
            Long max = runTimes.stream().max(Long::compare).orElse(null);
            Long min = runTimes.stream().min(Long::compare).orElse(null);
            System.out.printf("%-50s min: %7d max: %7d, avg: %10.2f [microseconds]\n", functionName, min, max, average);
        }

        int byParentAndChildKeyFlatMapFindAny = findByParentAndChildKeyFlatMapFindAny(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyFlatMapFindAny = " + byParentAndChildKeyFlatMapFindAny);

        int byParentAndChildKeyForLoops = findByParentAndChildKeyForLoops(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyForLoops = " + byParentAndChildKeyForLoops);

        int byParentAndChildKeyFlatMapFindAnyParallel = findByParentAndChildKeyFlatMapFindAnyParallel(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyFlatMapFindAnyParallel = " + byParentAndChildKeyFlatMapFindAnyParallel);

        int byParentAndChildKeyFlatMapFindFirst = findByParentAndChildKeyFlatMapFindFirst(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyFlatMapFindFirst = " + byParentAndChildKeyFlatMapFindFirst);

        int byParentAndChildKeyMapStreamFindAny = findByParentAndChildKeyMapStreamFindAny(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyMapStreamFindAny = " + byParentAndChildKeyMapStreamFindAny);

        int byParentAndChildKeyMapStreamFindFirst = findByParentAndChildKeyMapStreamFindFirst(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyMapStreamFindFirst = " + byParentAndChildKeyMapStreamFindFirst);

        int byParentAndChildKeyTwoStreams = findByParentAndChildKeyTwoStreams(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyTwoStreams = " + byParentAndChildKeyTwoStreams);

        int byParentAndChildKeyMapStreamFindAnyParallel = findByParentAndChildKeyMapStreamFindAnyParallel(searchPKey, searchCKey, parentItems);
        System.out.println("byParentAndChildKeyMapStreamFindAnyParallel = " + byParentAndChildKeyMapStreamFindAnyParallel);

    }

    private static void runAllReal(int iterations, List<ParentItem> parentItems, String searchPKey, String searchCKey) {
        runFindByParentAndChildKeyForLoops(parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChildKeyFlatMapFindAny(parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChildKeyFlatMapFindFirst(parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChildKeyFlatMapFindAnyParallel(parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChildKeyMapStreamFindFirst(parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChildKeyMapStreamFindAny(parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChildKeyMapStreamFindAnyParallel(parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChildKeyTwoStreams(parentItems, iterations, searchPKey, searchCKey);
    }



    private static void runAllMethodsByFunction(int iterations, List<ParentItem> parentItems, String searchPKey, String searchCKey) {
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyForLoops, "findByParentAndChildKeyForLoops",
                parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyFlatMapFindAny, "findByParentAndChildKeyFlatMapFindAny",
                parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyFlatMapFindFirst, "findByParentAndChildKeyFlatMapFindFirst",
                parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyFlatMapFindAnyParallel, "findByParentAndChildKeyFlatMapFindAnyParallel",
                parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyMapStreamFindFirst, "findByParentAndChildKeyMapStreamFindFirst",
                parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyMapStreamFindAny, "findByParentAndChildKeyMapStreamFindAny",
                parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyMapStreamFindAnyParallel, "findByParentAndChildKeyMapStreamFindAnyParallel",
                parentItems, iterations, searchPKey, searchCKey);
        runFindByParentAndChild(NestedCollectionsLookup::findByParentAndChildKeyTwoStreams,  "findByParentAndChildKeyTwoStreams",
                parentItems,iterations, searchPKey, searchCKey);
    }

    private static void runFindByParentAndChild(FindByParentAndChildKeyFunction function, String methodName, List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            function.findByParentAndChildKey(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, methodName);
    }

    private static void runFindByParentAndChildKeyForLoops(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start;
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyForLoops(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, "findByParentAndChildKeyForLoops");
    }

    private static void registerDiff(long start, String functionName) {
        long diff = (System.nanoTime() - start) / 1000;
        List<Long> runs = functionCallTimes.computeIfAbsent(functionName, k -> new ArrayList<>());
        runs.add(diff);

    }

    private static void runFindByParentAndChildKeyFlatMapFindAny(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyFlatMapFindAny(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, "findByParentAndChildKeyFlatMapFindAny");
    }

    private static void runFindByParentAndChildKeyFlatMapFindAnyParallel(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyFlatMapFindAnyParallel(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, "findByParentAndChildKeyFlatMapFindAnyParallel");
    }

    private static void runFindByParentAndChildKeyFlatMapFindFirst(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start;
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyFlatMapFindFirst(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, "findByParentAndChildKeyFlatMapFindFirst");
    }

    private static void runFindByParentAndChildKeyMapStreamFindAny(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyMapStreamFindAny(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, "findByParentAndChildKeyMapStreamFindAny");
    }

    private static void runFindByParentAndChildKeyMapStreamFindAnyParallel(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyMapStreamFindAnyParallel(searchPKey, searchCKey, parentItems);
        }

        registerDiff(start, "findByParentAndChildKeyMapStreamFindAnyParallel");
    }

    private static void runFindByParentAndChildKeyMapStreamFindFirst(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyMapStreamFindFirst(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, "findByParentAndChildKeyMapStreamFindFirst");
    }

    private static void runFindByParentAndChildKeyTwoStreams(List<ParentItem> parentItems, int iterations, String searchPKey, String searchCKey) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findByParentAndChildKeyTwoStreams(searchPKey, searchCKey, parentItems);
        }
        registerDiff(start, "findByParentAndChildKeyTwoStreams");
    }

    public static int findByParentAndChildKeyFlatMapFindAny(String pKey, String cKey, List<ParentItem> parentItems) {
        return parentItems.stream().filter(pItem -> pItem.getKey().equals(pKey)).
                flatMap(p -> p.getChildItemList().stream().filter(c -> c.getChildKey().equals(cKey))).findAny().map(ChildItem::getValue).orElse(0);
    }

    public static int findByParentAndChildKeyFlatMapFindAnyParallel(String pKey, String cKey, List<ParentItem> parentItems) {
        return parentItems.parallelStream().filter(pItem -> pItem.getKey().equals(pKey)).
                flatMap(p -> p.getChildItemList().stream().filter(c -> c.getChildKey().equals(cKey))).findAny().map(ChildItem::getValue).orElse(0);
    }

    public static int findByParentAndChildKeyFlatMapFindFirst(String pKey, String cKey, List<ParentItem> parentItems) {
        return parentItems.stream().filter(pItem -> pItem.getKey().equals(pKey)).
                flatMap(p -> p.getChildItemList().stream().filter(c -> c.getChildKey().equals(cKey))).findFirst().map(ChildItem::getValue).orElse(0);
    }

    public static int findByParentAndChildKeyMapStreamFindAny(String pKey, String cKey, List<ParentItem> parentItems) {
        return parentItems.stream().filter(pItem -> pItem.getKey().equals(pKey)).
                map(p -> p.getChildItemList().stream().filter(c -> c.getChildKey().equals(cKey)).findAny().map(ChildItem::getValue).orElse(0)).findAny().orElse(0);
    }

    public static int findByParentAndChildKeyMapStreamFindAnyParallel(String pKey, String cKey, List<ParentItem> parentItems) {
        return parentItems.parallelStream().filter(pItem -> pItem.getKey().equals(pKey)).
                map(p -> p.getChildItemList().parallelStream().filter(c -> c.getChildKey().equals(cKey)).findAny().map(ChildItem::getValue).orElse(0)).findAny().orElse(0);
    }



    public static int findByParentAndChildKeyMapStreamFindFirst(String pKey, String cKey, List<ParentItem> parentItems) {
        return parentItems.stream().filter(pItem -> pItem.getKey().equals(pKey)).
                map(p -> p.getChildItemList().stream().filter(c -> c.getChildKey().equals(cKey)).findFirst().map(ChildItem::getValue).orElse(0)).findFirst().orElse(0);
    }

    public static int findByParentAndChildKeyTwoStreams(String pKey, String cKey, List<ParentItem> parentItems) {
        ParentItem parentItem = parentItems.stream().filter(pItem -> pItem.getKey().equals(pKey)).findAny().orElse(null);
        if (parentItem != null) {
            return parentItem.getChildItemList().stream().filter(it -> it.getChildKey().equals(cKey)).findAny().map(ChildItem::getValue).orElse(0);
        }

        return 0;
    }

    public static int findByParentAndChildKeyForLoops(String pKey, String cKey, List<ParentItem> parentItems) {
        for (ParentItem parentItem : parentItems) {
            if (parentItem.getKey().equals(pKey)) {
                List<ChildItem> childItemList = parentItem.getChildItemList();
                for (ChildItem childItem : childItemList) {
                    if (childItem.getChildKey().equals(cKey)) {
                        return childItem.getValue();
                    }
                }
            }
        }
        return 0;
    }

}
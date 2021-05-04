package com.dependency.analysis.tool;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyAnalysis {
    Map<String, Set<String>> initialDependencyMap = new HashMap<>();
    Map<String, Set<String>> finalDependencyMap = new HashMap<>();
    Map<String, Set<String>> inverseDependencyMap = new HashMap<>();

    public static void main(String[] args) {
        boolean exit = false;
        StringBuilder sb = new StringBuilder();
        sb.append("************************************************** \n ");
        sb.append("GOOD DAY, PLEASE ENTER Dependencies \n ");
        sb.append("Press Enter in the end (to terminate the Application) \n ");
        sb.append("************************************************** \n");
        System.out.println(sb);
        DependencyAnalysis da = new DependencyAnalysis();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            exit = da.calculateDependency(scanner.nextLine());
            if (exit) {
                da.printDependencies();
                da.printInverseDependencies();
                break;
            }
        }
        System.out.println("\nBYE HAVE A GREAT DAY !!!");
        scanner.close();
    }

    private boolean calculateDependency(String inputLine) {
        if (StringUtils.isNotBlank(inputLine)) {
            List<String> dependencies = Arrays.asList(inputLine.split(" "));
            dependencies = dependencies.stream().distinct().collect(Collectors.toList());
            initialDependencyMap.put(dependencies.get(0), new HashSet<>(dependencies.subList(1, dependencies.size())));
            return false;
        }
        return true;
    }

    private void printDependencies() {
        initialDependencyMap.forEach((k, v) -> {
            HashSet<String> dependencySet = new HashSet<>(v);
            resolveInterDependency(k, k, v, dependencySet);
            finalDependencyMap.put(k, dependencySet);
        });
        System.out.println("*********************** full set of transitive dependencies********************");
        finalDependencyMap.forEach((k, v) -> System.out.println(k + ' ' + v));
    }

    private HashSet<String> resolveInterDependency(String key, String currentKey, Set<String> dependency, HashSet<String> dependencySet) {
        dependency.forEach(dependencyValue -> {
            HashSet<String> setData = dependencySet;
            if (initialDependencyMap.containsKey(dependencyValue)) {
                Set<String> tmpSet = initialDependencyMap.get(dependencyValue).stream().filter(val -> !val.equals(key) && !val.equals(currentKey)).collect(Collectors.toSet());
                setData.addAll(tmpSet);
                setData = resolveInterDependency(key, dependencyValue, tmpSet, dependencySet);
            }
            dependencySet.addAll(setData);
        });

        return dependencySet;
    }

    private void printInverseDependencies() {
        finalDependencyMap.forEach((k, v) -> {
            HashSet<String> inverseDependencySet = new HashSet<>();
            finalDependencyMap.forEach((key, value) -> {
                if (value.contains(k)) {
                    inverseDependencySet.add(key);
                }
            });
            inverseDependencyMap.put(k, inverseDependencySet);
        });
        System.out.println("\n***********************inverse dependencies********************");
        inverseDependencyMap.forEach((k, v) -> System.out.println(k + ' ' + v));
    }
}


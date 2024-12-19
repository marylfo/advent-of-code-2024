package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day19 {

    static class PatternDesign {

        private final HashMap<Integer, List<String>> possiblePatterns = new HashMap<>();
        private final List<String> designs;

        PatternDesign(String filename) {
            var line = getLines(filename);
            Arrays.stream(line.getFirst().split(", ")).forEach(this::addPatternToPossiblePatterns);

            designs = line.subList(2, line.size());
        }

        private void addPatternToPossiblePatterns(String pattern) {
            var patterns = possiblePatterns.getOrDefault(pattern.length(), new ArrayList<>());
            patterns.add(pattern);
            possiblePatterns.put(pattern.length(), patterns);
        }

        boolean isPossible(String pattern) {
            var subPatterns = new ArrayList<>(List.of(pattern));
            while (!subPatterns.isEmpty()) {
                var currentPattern = subPatterns.removeFirst();
                for (var patternLength : possiblePatterns.keySet()){

                    if (patternLength > currentPattern.length()) {
                        continue;
                    }

                    if (possiblePatterns.get(patternLength)
                            .contains(currentPattern.substring(currentPattern.length() - patternLength))) {

                        if (patternLength == currentPattern.length()) {
                            addPatternToPossiblePatterns(pattern);
                            return true;
                        } else {
                            var remainingPatterns = currentPattern.substring(0, currentPattern.length() - patternLength);

                            if (!subPatterns.contains(remainingPatterns)) {
                                subPatterns.add(remainingPatterns);
                            }
                        }
                    }
                }
            }
            return false;
        }

        long getNumOfPossibleDesigns() {
            return designs.stream().map(this::isPossible).filter(value -> value).count();
        }

    }

    public static void main(String[] args) {
        var patternDesign = new PatternDesign("19.txt");
        System.out.println("Part one answer: "+ patternDesign.getNumOfPossibleDesigns());
    }

}

package dev.marylfo.challenges;

import java.util.ArrayList;

import static dev.marylfo.services.FileService.getLines;

public class Day1 {

    public static void main(String[] args) {
        var lines = getLines("1.txt");

        if (lines == null) {
            return;
        }

        var listOne = new ArrayList<Integer>();
        var listTwo = new ArrayList<Integer>();

        for (var line : lines) {
            var numbers = line.split(" +");
            listOne.add(Integer.valueOf(numbers[0]));
            listTwo.add(Integer.valueOf(numbers[1]));
        }

        listOne.sort(Integer::compareTo);
        listTwo.sort(Integer::compareTo);

        var totalDiff = 0;
        for (int i = 0; i < listOne.size(); i++) {
            totalDiff += Math.abs(listOne.get(i) - listTwo.get(i));
        }

        System.out.println("Part one answer: " + totalDiff);

        var similarityScore = 0;

        for (int currentValue : listOne) {
            if (listTwo.contains(currentValue)) {
                var timeOfOccurrence = (int) listTwo.stream().filter(value -> value.equals(currentValue)).count();
                similarityScore += (currentValue * timeOfOccurrence);
            }
        }

        System.out.println("Part two answer: " + similarityScore);
    }

}

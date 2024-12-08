package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day2 {

    private static List<Integer> getNumbers(String line) {
        return Arrays.stream(line.split(" ")).map(String::trim).map(Integer::valueOf).toList();
    }

    private static boolean isSafe(List<Integer> numbers) {
        boolean isIncrease = numbers.get(1) > numbers.get(0);

        for (int i = 0; i < numbers.size() - 1; i++) {
            boolean currIsIncrease = numbers.get(i+1) > numbers.get(i);
            if (currIsIncrease != isIncrease) {
                return false;
            }

            var diff = Math.abs(numbers.get(i+1) - numbers.get(i));
            if (diff == 0 || diff > 3) {
                return false;
            }
        }

        return true;
    }

    private static boolean isRemoveOneSafe(List<Integer> numbers) {
        for (int i = 0; i < numbers.size(); i++) {

            if (i == 0 ) {
                var newNumbers = numbers.subList(1, numbers.size());

                if (isSafe(newNumbers)) {
                    return true;
                }

            } else if (i == numbers.size() - 1) {
                var newNumbers = numbers.subList(0, numbers.size() - 1);

                if (isSafe(newNumbers)) {
                    return true;
                }

            } else {
                var result = new ArrayList<>(numbers.subList(0, i));
                result.addAll(numbers.subList(i+1, numbers.size()));

                if (isSafe(result)) {
                    return true;
                }
            }

        }

        return false;
    }

    public static void main(String[] args) {
        var lines = getLines("2.txt");

        var sum = lines.stream().map(Day2::getNumbers).filter(Day2::isSafe).count();

        System.out.println("Part one answer: " + sum);

        var newSum = lines.stream()
                .map(Day2::getNumbers)
                .filter(value -> !isSafe(value))
                .map(Day2::isRemoveOneSafe)
                .filter(value -> value)
                .count() + sum;

        System.out.println("Part two answer: " + newSum);
    }

}

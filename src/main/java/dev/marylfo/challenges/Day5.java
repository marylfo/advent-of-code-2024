package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day5 {

    private static final List<String> orderingRules;
    private static final List<String> pagesToProduce;

    static {
        var allLines = getLines("5.txt");
        var separateLineIndex = allLines.indexOf("");

        orderingRules = allLines.subList(0, separateLineIndex);
        pagesToProduce = allLines.subList(separateLineIndex+1, allLines.size());
    }

    private static boolean isInputAfterOther(String input, List<String> otherNumbers) {
        for (String otherNum : otherNumbers) {
            var violateRule = "%s|%s".formatted(otherNum, input);
            if (orderingRules.contains(violateRule)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRightOrder(String pageNumbers) {
        var numbers = List.of(pageNumbers.split(","));
        var numbersLength = numbers.size();
        for (int i = 0; i < numbersLength - 1; i++) {
            if (isInputAfterOther(numbers.get(i), numbers.subList(i+1, numbersLength))) {
                return false;
            }
        }
        return true;
    }

    private static int getMiddleNumber(String pageNumbers) {
        var numbers = pageNumbers.split(",");
        var middleIndex = (numbers.length+1)/2 - 1;
        return Integer.parseInt(numbers[middleIndex]);
    }

    private static String getInvalidNumber(String input, List<String> otherNumbers) {
        for (String otherNum : otherNumbers) {
            var violateRule = "%s|%s".formatted(otherNum, input);
            if (orderingRules.contains(violateRule)) {
                return otherNum;
            }
        }
        return null;
    }

    private static String getCorrectOrder(String pageNumbers) {
        var numbers = new ArrayList<>(List.of(pageNumbers.split(",")));
        var numbersLength = numbers.size();
        for (int i = 0; i < numbersLength - 1; i++) {
            if (isInputAfterOther(numbers.get(i), numbers.subList(i+1, numbersLength))) {
                var invalidNumber = getInvalidNumber(numbers.get(i), numbers.subList(i+1, numbersLength));
                var loc = numbers.indexOf(invalidNumber);
                numbers.set(loc, numbers.get(i));
                numbers.set(i, invalidNumber);
            }
        }
        return String.join(",", numbers);
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        var sum = pagesToProduce.stream()
                .filter(Day5::isRightOrder)
                .map(Day5::getMiddleNumber)
                .reduce(0, Integer::sum);

        System.out.println("Part one answer: " + sum);
    }

    private static void partTwo() {
        var notCorrectOrderMiddleSum = pagesToProduce.stream()
                .filter(line -> !isRightOrder(line))
                .map(line -> {
                    var correctOrder = getCorrectOrder(line);
                    var confirmCorrectOrder = getCorrectOrder(correctOrder);

                    while (!confirmCorrectOrder.equals(correctOrder)) {
                        correctOrder = confirmCorrectOrder;
                        confirmCorrectOrder = getCorrectOrder(correctOrder);
                    }

                    return confirmCorrectOrder;
                })
                .map(Day5::getMiddleNumber)
                .reduce(0, Integer::sum);

        System.out.println("Part two answer: " + notCorrectOrderMiddleSum);
    }

}

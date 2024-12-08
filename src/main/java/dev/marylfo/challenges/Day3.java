package dev.marylfo.challenges;

import dev.marylfo.services.FileService;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 {

    private static final String input;

    static {
        input = String.join("\n", Objects.requireNonNull(FileService.getLines("3.txt")));
    }

    private static Integer extractMulValues(String input) {
        int sum = 0;
        Pattern pattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            var first = Integer.valueOf(matcher.group(1));
            var second = Integer.valueOf(matcher.group(2));

            sum += first * second;
        }
        return sum;
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        var sum = extractMulValues(input);
        System.out.println("Part one answer: " + sum);
    }

    private static void partTwo() {
        var texts = input.split("don't\\(\\)");

        var sum = extractMulValues(texts[0]);

        for (int i=1; i<texts.length; i++){
            var validText = texts[i].split("do\\(\\)");
            for (int j=1; j<validText.length; j++){
                sum += extractMulValues(validText[j]);
            }
        }

        System.out.println("Part two answer: " + sum);
    }
}

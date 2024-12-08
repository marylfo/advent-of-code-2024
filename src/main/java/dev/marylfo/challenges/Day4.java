package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.marylfo.services.FileService.getLines;

public class Day4 {

    private static final String XMAS = "XMAS";
    private static final Pattern XMAS_PATTERN = Pattern.compile(XMAS);
    private static final String XMAS_REVERSE = "SAMX";
    private static final Pattern XMAS_REVERSE_PATTERN = Pattern.compile(XMAS_REVERSE);

    private static List<String> getVerticalLines(List<String> lines) {
        int length = lines.get(0).length();
        var verticalLines = new ArrayList<String>(length);

        for (int i = 0; i < length; i++) {
            StringBuilder line = new StringBuilder();
            for (String s : lines) {
                line.append(s.charAt(i));
            }
            verticalLines.add(line.toString());
        }

        return verticalLines;
    }

    private static List<String> getLeftDiagonalLines(List<String> lines) {
        int numOfDiagonalLine = lines.get(0).length();
        var leftDiagonalLines = new ArrayList<String>(numOfDiagonalLine);

        for (int i = 0; i < numOfDiagonalLine; i++) {
            StringBuilder line = new StringBuilder();
            var k = 0;
            for (int j = i; j >= 0; j--) {
                if ( k < numOfDiagonalLine) {
                    line.append(lines.get(k).charAt(j));
                    k++;
                }
            }
            leftDiagonalLines.add(line.toString());
        }

        for (int i = 1; i < lines.size(); i++) {
            StringBuilder line = new StringBuilder();
            var k = lines.get(0).length() - 1;
            for (int j = i; j < lines.size(); j++) {
                if ( k >= 0) {
                    line.append(lines.get(j).charAt(k));
                    k--;
                }
            }
            leftDiagonalLines.add(line.toString());
        }

        return leftDiagonalLines;
    }


    private static List<String> getRightDiagonalLines(List<String> lines) {
        int numOfDiagonalLine = lines.get(0).length();
        var rightDiagonalLines = new ArrayList<String>(numOfDiagonalLine);

        for (int i = 0; i < numOfDiagonalLine; i++) {
            StringBuilder line = new StringBuilder();
            var k = 0;
            for (int j = i; j < numOfDiagonalLine; j++) {
                if ( k < numOfDiagonalLine) {
                    line.append(lines.get(k).charAt(j));
                    k++;
                }
            }
            rightDiagonalLines.add(line.toString());
        }

        for (int i = 1; i < lines.size(); i++) {
            StringBuilder line = new StringBuilder();
            var k = 0;
            for (int j = i; j < lines.size(); j++) {
                if ( k < numOfDiagonalLine) {
                    line.append(lines.get(j).charAt(k));
                    k++;
                }
            }
            rightDiagonalLines.add(line.toString());
        }

        return rightDiagonalLines;
    }

    private static int getSearchCount(List<String> lines) {
        int count = 0;

        for (String line : lines) {
            Matcher xmas_matcher = XMAS_PATTERN.matcher(line);
            Matcher xmas_reverse_matcher = XMAS_REVERSE_PATTERN.matcher(line);

            while (xmas_matcher.find()) {
                count++;
            }

            while (xmas_reverse_matcher.find()) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        List<String> horizontalLines = getLines("4.txt");
        List<String> verticalLines = getVerticalLines(horizontalLines);
        List<String> leftDiagonalLines = getLeftDiagonalLines(horizontalLines);
        List<String> rightDiagonalLines = getRightDiagonalLines(horizontalLines);

        var totalTime = getSearchCount(horizontalLines) + getSearchCount(verticalLines)
                + getSearchCount(leftDiagonalLines) + getSearchCount(rightDiagonalLines);

        System.out.println("Part one answer: " + totalTime);
    }

    private static void partTwo() {
        getXmasAppear(getLines("4.txt"));
    }

    private static void getXmasAppear(List<String> lines) {
        var count = 0;
        for (int i = 1; i < lines.size() - 1; i++) {
            for (int j = 1; j < lines.get(0).length() - 1; j++) {
                if (lines.get(i).charAt(j) == 'A') {
                    var diagonalOne = List.of(
                            lines.get(i-1).charAt(j-1),
                            lines.get(i+1).charAt(j+1)
                    );

                    var diagonalTwo = List.of(
                            lines.get(i-1).charAt(j+1),
                            lines.get(i+1).charAt(j-1)
                    );

                    count += isMAS(diagonalOne) && isMAS(diagonalTwo)? 1 : 0;
                }
            }
        }

        System.out.println("Part two answer: " + count);
    }

    private static boolean isMAS(List<Character> neighbours) {
        return neighbours.contains('M') && neighbours.contains('S');
    }

}

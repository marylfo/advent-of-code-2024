package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.marylfo.services.FileService.getLines;

public class Day21 {

    record Coordinate(int row, int col) {}
    static HashMap<Character, Coordinate> numericKeys = new HashMap<>();
    static HashMap<Direction, Coordinate> directionalKeys = new HashMap<>();

    enum Direction {
        UP, DOWN, LEFT, RIGHT, PRESS
    }

    static {
        numericKeys.put('7', new Coordinate(0,0));
        numericKeys.put('8', new Coordinate(0, 1));
        numericKeys.put('9', new Coordinate(0, 2));
        numericKeys.put('4', new Coordinate(1, 0));
        numericKeys.put('5', new Coordinate(1, 1));
        numericKeys.put('6', new Coordinate(1, 2));
        numericKeys.put('1', new Coordinate(2, 0));
        numericKeys.put('2', new Coordinate(2, 1));
        numericKeys.put('3', new Coordinate(2, 2));
        numericKeys.put('0', new Coordinate(3, 1));
        numericKeys.put('A', new Coordinate(3, 2));

        directionalKeys.put(Direction.UP, new Coordinate(0, 1));
        directionalKeys.put(Direction.PRESS, new Coordinate(0, 2));
        directionalKeys.put(Direction.LEFT, new Coordinate(1, 0));
        directionalKeys.put(Direction.DOWN, new Coordinate(1, 1));
        directionalKeys.put(Direction.RIGHT, new Coordinate(1, 2));
    }

    private static List<Direction> getDirection(boolean isVertical, int delta) {
        Direction direction;
        if (isVertical) {
            direction = (delta > 0) ? Direction.DOWN : Direction.UP;
        } else {
            direction = (delta > 0) ? Direction.RIGHT : Direction.LEFT;
        }

        return IntStream.range(0, Math.abs(delta)).mapToObj(_ -> direction).toList();
    }

    private static Coordinate getNextCoordinate(Coordinate curr, Direction dir) {
        return switch (dir) {
            case UP -> new Coordinate(curr.row - 1, curr.col);
            case DOWN -> new Coordinate(curr.row + 1, curr.col);
            case LEFT -> new Coordinate(curr.row, curr.col - 1);
            default -> new Coordinate(curr.row, curr.col + 1);
        };
    }

    private static boolean isValidSequence(Character c, List<Direction> sequence) {
        Coordinate currentCoordinate = numericKeys.get(c);
        for (var step : sequence) {
            currentCoordinate = getNextCoordinate(currentCoordinate, step);
            if (currentCoordinate.row == 3 && currentCoordinate.col == 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidSequence(Direction d, List<Direction> sequence) {
        Coordinate currentCoordinate = directionalKeys.get(d);
        for (var step : sequence) {
            currentCoordinate = getNextCoordinate(currentCoordinate, step);
            if (currentCoordinate.row == 0 && currentCoordinate.col == 0) {
                return false;
            }
        }
        return true;
    }

    private static List<List<Direction>> filterInvalidSequences(Character c, List<List<Direction>> sequences) {
        return sequences.parallelStream().filter(s -> isValidSequence(c, s)).toList();
    }

    private static List<List<Direction>> filterInvalidSequences(Direction d, List<List<Direction>> sequences) {
        return sequences.parallelStream().filter(s -> isValidSequence(d, s)).toList();
    }

    private static List<List<Direction>> getButtonPressSequences(Character c1, Character c2) {
        if (c1.equals(c2)) {
            return List.of(List.of(Direction.PRESS));
        }

        Coordinate coordinate1 = numericKeys.get(c1);
        Coordinate coordinate2 = numericKeys.get(c2);

        var deltaRow = coordinate2.row - coordinate1.row;
        var deltaCol = coordinate2.col - coordinate1.col;

        List<Direction> verticalMove = (deltaRow != 0) ? getDirection(true, deltaRow) : null;
        List<Direction> horizontalMove =  (deltaCol != 0) ? getDirection(false, deltaCol) : null;

        if (verticalMove == null && horizontalMove == null) {
            System.out.println("Something error happen!");
        } else if (verticalMove == null) {
            return List.of(Stream.concat(horizontalMove.stream(), Stream.of(Direction.PRESS)).toList());
        } else if (horizontalMove == null) {
            return List.of(Stream.concat(verticalMove.stream(), Stream.of(Direction.PRESS)).toList());
        }

        var combo1 = Stream.concat(verticalMove.stream(), Stream.concat(horizontalMove.stream(), Stream.of(Direction.PRESS))).toList();
        var combo2 = Stream.concat(horizontalMove.stream(), Stream.concat(verticalMove.stream(), Stream.of(Direction.PRESS))).toList();
        return filterInvalidSequences(c1, List.of(combo1, combo2));
    }

    private static List<List<Direction>> getButtonPressSequences(Direction d1, Direction d2) {
        if (d1.equals(d2)) {
            return List.of(List.of(Direction.PRESS));
        }

        Coordinate coordinate1 = directionalKeys.get(d1);
        Coordinate coordinate2 = directionalKeys.get(d2);

        var deltaRow = coordinate2.row - coordinate1.row;
        var deltaCol = coordinate2.col - coordinate1.col;

        List<Direction> verticalMove = (deltaRow != 0) ? getDirection(true, deltaRow) : null;
        List<Direction> horizontalMove =  (deltaCol != 0) ? getDirection(false, deltaCol) : null;

        if (verticalMove == null && horizontalMove == null) {
            System.out.println("Something error happen!");
        } else if (verticalMove == null) {
            return List.of(Stream.concat(horizontalMove.stream(), Stream.of(Direction.PRESS)).toList());
        } else if (horizontalMove == null) {
            return List.of(Stream.concat(verticalMove.stream(), Stream.of(Direction.PRESS)).toList());
        }

        var combo1 = Stream.concat(verticalMove.stream(), Stream.concat(horizontalMove.stream(), Stream.of(Direction.PRESS))).toList();
        var combo2 = Stream.concat(horizontalMove.stream(), Stream.concat(verticalMove.stream(), Stream.of(Direction.PRESS))).toList();
        return filterInvalidSequences(d1, List.of(combo1, combo2));
    }

    private static List<List<Direction>> calculateAllCombo(List<List<Direction>> currSeq, List<List<Direction>> nextSeq) {
        if (currSeq.isEmpty()) {
            return nextSeq;
        } else {
            return currSeq.parallelStream()
                    .flatMap(curr -> nextSeq.stream().map(next -> Stream.concat(curr.stream(), next.stream()).toList()))
                    .toList();
        }
    }

    private static List<List<Direction>> getNumericKeypadButtonPressSequences(String input) {
        var path = "A" + input;
        List<List<Direction>> sequences = new ArrayList<>();
        for (int i = 0; i < path.length()-1; i++) {
            sequences = calculateAllCombo(sequences, getButtonPressSequences(path.charAt(i), path.charAt(i+1)));
        }
        return sequences;
    }

    private static List<List<Direction>> getDirectionalKeypadButtonPressSequences(List<Direction> input) {
        var path = Stream.concat(Stream.of(Direction.PRESS), input.stream()).toList();
        List<List<Direction>> sequences = new ArrayList<>();
        for (int i = 0; i < path.size()-1; i++) {
            sequences = calculateAllCombo(sequences, getButtonPressSequences(path.get(i), path.get(i+1)));
        }
        return sequences;
    }

    private static List<List<Direction>> getDirectionalKeyPadButtonPressSequences(List<List<Direction>> numericPaths) {
        return numericPaths.parallelStream().map(Day21::getDirectionalKeypadButtonPressSequences)
                .flatMap(List::stream)
                .distinct().toList();
    }


    private static int getShortestSeq(String input) {
        var numericPaths = getNumericKeypadButtonPressSequences(input);
        var directionPaths1 = getDirectionalKeyPadButtonPressSequences(numericPaths);
        var directionPaths2 = getDirectionalKeyPadButtonPressSequences(directionPaths1);

        return directionPaths2.parallelStream().mapToInt(List::size).min().orElse(-1);
    }

    public static void main(String[] args) {
        var sum = getLines("21.txt").parallelStream()
                .mapToInt(line -> Integer.parseInt(line.substring(0, 3)) * getShortestSeq(line))
                .sum();

        System.out.println("Part one answer: " + sum);
    }

}


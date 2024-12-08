package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day6 {

    enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public Direction getNext() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }

    }

    private static char[][] map;
    private static int numOfVerticalLines;
    private static int numOfHorizontalLines;

    private static Coordinate start;

    private static int steps = 0;

    static {
        readMap("6.txt");
    }

    // verticalIndex: map.get(i), horizontalIndex: string.charAt(i)
    record Coordinate(int verticalIndex, int horizontalIndex, Direction dir) {}

    private static char getValue(Coordinate coordinate) {
        return map[coordinate.verticalIndex][coordinate.horizontalIndex];
    }

    private static void readMap(String fileName) {
        var lines = getLines(fileName);
        if (lines == null || lines.isEmpty()) { return; }

        numOfVerticalLines = lines.size();
        numOfHorizontalLines = lines.get(0).length();

        map = new char[numOfVerticalLines][numOfHorizontalLines];
        for (int i = 0; i < numOfVerticalLines; i++) {
            String line = lines.get(i);
            for (int j = 0; j < lines.get(i).length(); j++) {
                map[i][j] = line.charAt(j);

                if (map[i][j] == '^') {
                    start = new Coordinate(i, j, Direction.UP);
                }
            }
        }
    }

    private static Coordinate getNextCoordinate(Coordinate currentCoordinate) {
        var nextVerticalLine = currentCoordinate.verticalIndex;
        var nextHorizontalLine = currentCoordinate.horizontalIndex;

        switch (currentCoordinate.dir) {
            case UP:
                nextVerticalLine--;
                break;
            case DOWN:
                nextVerticalLine++;
                break;
            case LEFT:
                nextHorizontalLine--;
                break;
            case RIGHT:
                nextHorizontalLine++;
                break;
        }

        if (getValue(new Coordinate(nextVerticalLine, nextHorizontalLine, Direction.UP)) == '#') {
            return new Coordinate(
                    currentCoordinate.verticalIndex,
                    currentCoordinate.horizontalIndex(),
                    currentCoordinate.dir.getNext());
        } else {
            return new Coordinate(nextVerticalLine, nextHorizontalLine, currentCoordinate.dir);
        }
    }

    private static void markStep(Coordinate currentCoordinate) {
        steps++;
        map[currentCoordinate.verticalIndex][currentCoordinate.horizontalIndex] = 'x';
    }

    private static boolean canMove(Coordinate currentCoordinate) {
        if (currentCoordinate.verticalIndex == 0 && currentCoordinate.dir == Direction.UP) {
            return false;
        } else if (currentCoordinate.horizontalIndex == 0 && currentCoordinate.dir == Direction.LEFT) {
            return false;
        } else if (currentCoordinate.horizontalIndex == (numOfHorizontalLines-1) && currentCoordinate.dir == Direction.RIGHT) {
            return false;
        } else if  (currentCoordinate.verticalIndex == (numOfVerticalLines-1) && currentCoordinate.dir == Direction.DOWN) {
            return false;
        } else {
            return true;
        }
    }

    private static void walk() {
        var currentCoordinate = start;
        while (getValue(currentCoordinate) == '^' || getValue(currentCoordinate) == '.' || getValue(currentCoordinate) == 'x') {
            if (getValue(currentCoordinate) != 'x') {
                markStep(currentCoordinate);
            }

            if (!canMove(currentCoordinate)) {
                return;
            }

            currentCoordinate = getNextCoordinate(currentCoordinate);
        }
    }

    private static boolean isWalkRepeated(HashMap<Coordinate, Integer> steps) {
        return steps.entrySet().stream().anyMatch(entry -> entry.getValue() == 2);
    }

    private static boolean isALoop(int verticalIndex, int horizontalIndex) {
        // set value
        map[verticalIndex][horizontalIndex] = '#';
        var steps = new HashMap<Coordinate, Integer>();

        var currentCoordinate = start;
        steps.put(currentCoordinate, 1);

        while (getValue(currentCoordinate) == '^' || getValue(currentCoordinate) == '.' || getValue(currentCoordinate) == 'x') {
            if (!canMove(currentCoordinate)) {
                return false;
            }

            currentCoordinate = getNextCoordinate(currentCoordinate);
            steps.put(currentCoordinate, steps.getOrDefault(currentCoordinate, 0) + 1);

            if (isWalkRepeated(steps)) {
                return true;
            }
        }

        return false;
    }

    private static List<Coordinate> getPossibleObstructionCoordinates() {
        var coordinates = new ArrayList<Coordinate>();
        for (int i = 0; i < numOfVerticalLines; i++) {
            for (int j = 0; j < numOfHorizontalLines; j++) {
                if (map[i][j] == '.') {
                    coordinates.add(new Coordinate(i, j, Direction.UP));
                }
            }
        }
        return coordinates;
    }

    public static void main(String[] args) {
		partOne();
        partTwo();
    }

    private static void partOne() {
        walk();
        System.out.println("Part one answer: " + steps);
    }

    private static void partTwo() {
        readMap("6.txt");

        var possibleCoordinates = getPossibleObstructionCoordinates();
//        System.out.println("possibleCoordinates: " + possibleCoordinates.size());
//        int progress = 0;
        int count = 0;

        for (var coordinate : possibleCoordinates) {
//            progress++;
//            System.out.println(progress);
            var isLoop = isALoop(coordinate.verticalIndex, coordinate.horizontalIndex);
            if (isLoop) {
                count++;
            }
            map[coordinate.verticalIndex][coordinate.horizontalIndex] = '.';
        }

        System.out.println("Part two answer: " + count);
    }

    private static void printMap() {
        for (int i = 0; i < numOfVerticalLines; i++) {
            for (int j = 0; j < numOfHorizontalLines; j++) {
                System.out.print(map[i][j]);
            }
            System.out.print("\n");
        }
    }
}

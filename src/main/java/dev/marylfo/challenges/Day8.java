package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day8 {

    record Coordinate(int x, int y) {}

    private static final List<String> map;
    private static final int maxX, maxY;
    private static final HashMap<Character, List<Coordinate>> antennas = new HashMap<>();
    private static final HashSet<Coordinate> antinodes = new HashSet<>();
    private static final HashSet<Coordinate> manyAntinodes = new HashSet<>();

    private static final boolean isTest = false;

    static {
        map = getLines(isTest ? "8-test.txt" : "8.txt");
        maxX = map.get(0).length() - 1;
        maxY = map.size() - 1;
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).length(); j++) {
                if (map.get(i).charAt(j) != '.') {
                    var temp = antennas.getOrDefault(map.get(i).charAt(j), new ArrayList<>());
                    temp.add(new Coordinate(j, i));
                    antennas.put(map.get(i).charAt(j), temp);
                }
            }
        }
    }

    private static List<Coordinate> getValidCoordinates(List<Coordinate> coordinates) {
        return coordinates.stream()
                .filter(coordinate -> coordinate.x >= 0 && coordinate.x <= maxX &&
                        coordinate.y >= 0 && coordinate.y <= maxY)
                .toList();
    }

    private static List<Coordinate> getAntinodes(Coordinate a, Coordinate b) {
        // vertical line
        if (a.x == b.x) {
            var deltaY = Math.abs(b.y - a.y);
            var up = (a.y < b.y) ? a : b;
            var down = up.equals(a) ? b : a;

            return getValidCoordinates(List.of(
                    new Coordinate(a.x, up.y - deltaY),
                    new Coordinate(a.x, down.y + deltaY)));
        }

        // horizontal line
        var left = (a.x - b.x < 0) ? a : b;
        var right = left.equals(a) ? b : a;
        var deltaX = Math.abs(a.x - b.x);

        if (a.y == b.y) {
            return getValidCoordinates(List.of(
                    new Coordinate(left.x - deltaX, a.y),
                    new Coordinate(right.x + deltaX, a.y)));
        }

        float slope = (float) (left.y - right.y) / (float) (left.x - right.x);
        var deltaY = Math.abs(a.y - b.y);

        if (slope > 0) {
            return getValidCoordinates(List.of(
                    new Coordinate(left.x - deltaX, left.y - deltaY),
                    new Coordinate(right.x + deltaX, right.y + deltaY)));
        } else {
            return getValidCoordinates(List.of(
                    new Coordinate(left.x - deltaX, left.y + deltaY),
                    new Coordinate(right.x + deltaX, right.y - deltaY)));
        }
    }

    private static boolean isValidCoordinate(Coordinate coordinate) {
        return coordinate.x >= 0 && coordinate.x <= maxX && coordinate.y >= 0 && coordinate.y <= maxY;
    }

    private static List<Coordinate> getManyAntinodes(Coordinate a, Coordinate b) {
        // vertical line
        var antinodes = new ArrayList<Coordinate>();
        antinodes.add(a);
        antinodes.add(b);
        var count = 1;

        if (a.x == b.x) {
            var deltaY = Math.abs(b.y - a.y);
            var up = (a.y < b.y) ? a : b;
            var down = up.equals(a) ? b : a;

            while (isValidCoordinate(new Coordinate(a.x, up.y - count * deltaY))) {
                antinodes.add(new Coordinate(a.x, up.y - count * deltaY));
                count++;
            }

            count = 1;
            while (isValidCoordinate(new Coordinate(a.x, down.y + count * deltaY))) {
                antinodes.add(new Coordinate(a.x, down.y + count * deltaY));
                count++;
            }

            return antinodes;
        }

        // horizontal line
        var left = (a.x - b.x < 0) ? a : b;
        var right = left.equals(a) ? b : a;
        var deltaX = Math.abs(a.x - b.x);

        if (a.y == b.y) {
            while (isValidCoordinate(new Coordinate(left.x - count * deltaX, left.y))) {
                antinodes.add(new Coordinate(left.x - count * deltaX, left.y));
                count++;
            }

            count = 1;
            while (isValidCoordinate(new Coordinate(right.x + count * deltaX, left.y))) {
                antinodes.add(new Coordinate(right.x + count * deltaX, left.y));
                count++;
            }

            return antinodes;
        }

        float slope = (float) (left.y - right.y) / (float) (left.x - right.x);
        var deltaY = Math.abs(a.y - b.y);

        if (slope > 0) {
            while (isValidCoordinate(new Coordinate(left.x - count * deltaX, left.y - count * deltaY))) {
                antinodes.add(new Coordinate(left.x - count * deltaX, left.y - count * deltaY));
                count++;
            }

            count = 1;
            while (isValidCoordinate(new Coordinate(right.x + count * deltaX, right.y + count * deltaY))) {
                antinodes.add(new Coordinate(right.x + count * deltaX, right.y + count * deltaY));
                count++;
            }
            return antinodes;
        } else {
            while (isValidCoordinate(new Coordinate(left.x - count * deltaX, left.y + count * deltaY))) {
                antinodes.add(new Coordinate(left.x - count * deltaX, left.y + count * deltaY));
                count++;
            }

            count = 1;
            while (isValidCoordinate(new Coordinate(right.x + count * deltaX, right.y - count * deltaY))) {
                antinodes.add(new Coordinate(right.x + count * deltaX, right.y - count * deltaY));
                count++;
            }
            return antinodes;
        }
    }

    private static void partOne() {
        for (var frequency : antennas.keySet()) {
            var coordinates = antennas.get(frequency);
            for (int i = 0; i < coordinates.size() - 1; i++) {
                for (int j = i + 1; j < coordinates.size(); j++) {
                    antinodes.addAll(getAntinodes(coordinates.get(i), coordinates.get(j)));
                }
            }
        }

        System.out.println("Part one answer: " + antinodes.size());
    }

    private static void partTwo() {
        for (var frequency : antennas.keySet()) {
            var coordinates = antennas.get(frequency);
            for (int i = 0; i < coordinates.size() - 1; i++) {
                for (int j = i + 1; j < coordinates.size(); j++) {
                    manyAntinodes.addAll(getManyAntinodes(coordinates.get(i), coordinates.get(j)));
                }
            }
        }

        System.out.println("Part two answer: " + manyAntinodes.size());
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }
}

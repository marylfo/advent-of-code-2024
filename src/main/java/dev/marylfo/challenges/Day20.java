package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static dev.marylfo.services.FileService.getLines;

public class Day20 {

    private static final char[][] map;
    private static final int size;
    private static Coordinate start, end;
    private static final List<Movement> directions = List.of(
            new Movement(0, 1), new Movement(0, -1),
            new Movement(1, 0), new Movement(-1, 0));
    private static final List<Step> path = new ArrayList<>();
    private static final HashMap<Integer, Integer> savedSeconds = new HashMap<>();

    record Coordinate(int row, int col) {}
    record Movement(int deltaRow, int deltaCol) {}

    record Step(int picoseconds, Coordinate coordinate, Movement direction) {}

    static {

        var lines = getLines("20.txt");
        size = lines.size();
        map = new char[size][size];

        var currRow = 0;
        for (var line : lines) {
            for (int i = 0; i<line.length(); i++) {
                map[currRow][i] = line.charAt(i);
                if (line.charAt(i) == 'S') {
                    start = new Coordinate(currRow, i);
                } else if (line.charAt(i) == 'E') {
                    end = new Coordinate(currRow, i);
                }
            }
            currRow++;
        }

        setFullPath();
    }

    static void setFullPath() {
        var picoseconds = 0;
        var currCoorindate = start;
        while (currCoorindate != end) {
            for (var dir : directions) {
                if (map[currCoorindate.row + dir.deltaRow][currCoorindate.col + dir.deltaCol] == 'E') {
                    path.add(new Step(picoseconds++, currCoorindate, dir));
                    path.add(new Step(picoseconds, end, null));
                    currCoorindate = end;
                    break;
                } else if (map[currCoorindate.row + dir.deltaRow][currCoorindate.col + dir.deltaCol] == '.') {
                    if (!path.isEmpty() && path.getLast().coordinate.equals(
                            new Coordinate(currCoorindate.row + dir.deltaRow, currCoorindate.col + dir.deltaCol))) {
                        continue;
                    }
                    path.add(new Step(picoseconds++, currCoorindate, dir));
                    currCoorindate = new Coordinate(currCoorindate.row + dir.deltaRow, currCoorindate.col + dir.deltaCol);
                    break;
                }
            }
        }
    }

    static int picosecondsInPath(int row, int col) {
        try {
            return path.stream().filter(step -> {
                var coor = step.coordinate;
                return coor.row == row && coor.col == col;
            }).map(step -> step.picoseconds).toList().getFirst();
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    static int getSavedSecondsCheats(int picoseconds) {
        for (var step : path) {
            var currCoordinate = step.coordinate;
            for (var dir: directions) {
                if (map[currCoordinate.row + dir.deltaRow][currCoordinate.col + dir.deltaCol] == '#') {
                    var sec = picosecondsInPath(currCoordinate.row + dir.deltaRow *2, currCoordinate.col + dir.deltaCol*2);
                    if (sec > 0 && sec > step.picoseconds) {
                        var saveSec = sec - step.picoseconds - 2;
                        savedSeconds.put(saveSec, savedSeconds.getOrDefault(saveSec, 0) + 1);
                    }
                }
            }
        }

        return savedSeconds.entrySet().stream()
                .filter(entry -> entry.getKey() >= picoseconds)
                .map(Map.Entry::getValue)
                .reduce(0, Integer::sum);
    }

    public static void main(String[] args) {
        var cheatsSum = getSavedSecondsCheats(100);
        System.out.println("Part one answer: " + cheatsSum);
    }

}

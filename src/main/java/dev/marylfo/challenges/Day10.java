package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static dev.marylfo.services.FileService.getLines;

public class Day10 {

    static class Position {
        final int height;
        List<Position> neighbours = new ArrayList<>();

        Position(int height) {
            this.height = height;
        }

        void setNeighbours(Position up, Position down, Position left, Position right) {
            neighbours = Stream.of(up, down, left, right).filter(Objects::nonNull).toList();
        }
    }

    private static final List<List<Position>> map;
    private static final List<Position> trailheads = new ArrayList<>();

    static {
        map = getLines("10.txt").stream()
                .map(line -> Arrays.stream(line.split(""))
                        .map(pos -> new Position(Integer.parseInt(pos)))
                        .toList())
                .toList();

        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                var currPos = map.get(i).get(j);

                var up = (i-1 >= 0) ? map.get(i-1).get(j) : null;
                var down = (i+1 < map.size()) ? map.get(i+1).get(j) : null;
                var left = (j-1 >= 0) ? map.get(i).get(j-1) : null;
                var right = (j+1 < map.getFirst().size()) ? map.get(i).get(j+1) : null;

                currPos.setNeighbours(up, down, left, right);

                if (currPos.height == 0) {
                    trailheads.add(currPos);
                }
            }
        }
    }

    private static List<Position> getLinkedTrailTails(Position trailhead) {
        var currPositions = List.of(trailhead);
        var nextHeight = 1;

        do {
            int finalNextHeight = nextHeight;
            currPositions = currPositions.stream()
                    .map(pos -> pos.neighbours.stream()
                            .filter(neighbour -> neighbour.height == finalNextHeight)
                            .toList())
                    .flatMap(List::stream)
                    .distinct()
                    .toList();

            nextHeight++;
        } while (nextHeight <= 9 && !currPositions.isEmpty());

        return currPositions;
    }

    private static List<Position> getRatingLinkedTrailTails(Position trailhead) {
        var currPositions = List.of(trailhead);
        var nextHeight = 1;

        do {
            int finalNextHeight = nextHeight;
            currPositions = currPositions.stream()
                    .map(pos -> pos.neighbours.stream()
                            .filter(neighbour -> neighbour.height == finalNextHeight)
                            .toList())
                    .flatMap(List::stream)
                    .toList();

            nextHeight++;
        } while (nextHeight <= 9 && !currPositions.isEmpty());

        return currPositions;
    }

    public static void main(String[] args) {
        var sum = trailheads.stream().map(Day10::getLinkedTrailTails).mapToInt(List::size).sum();
        System.out.println("Part one answer: " + sum);

        var ratings = trailheads.stream().map(Day10::getRatingLinkedTrailTails).mapToInt(List::size).sum();
        System.out.println("Part two answer: " + ratings);
    }

}

package dev.marylfo.challenges;

import java.util.Comparator;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day14 {

    private static List<Robot> robots;
    private static final int numOfRobots;
    private static final int xTileSize = 101;
    private static final int yTileSize = 103;
    private static final int xBoundary = xTileSize / 2;
    private static final int yBoundary = yTileSize / 2;

    static class Robot {

        private int x;
        private int y;
        private final int deltaX;
        private final int deltaY;

        public Robot(String input) {
            var values = input.replace("p=", "").replace(" v=", ",").split(",");
            x = Integer.parseInt(values[0]);
            y = Integer.parseInt(values[1]);
            deltaX = Integer.parseInt(values[2]);
            deltaY = Integer.parseInt(values[3]);
        }

        public void move(int time) {
            for (int i = 0; i < time; i++) {
                var newX = x + deltaX;
                if (newX < 0) { newX += xTileSize; }
                x = newX % xTileSize;

                var newY = y + deltaY;
                if (newY < 0) { newY += yTileSize; }
                y = newY % yTileSize;
            }
        }

        public int getQuadrantNum() {
            if (x < xBoundary && y < yBoundary) {
                return 4;
            } else if (x > xBoundary && y < yBoundary) {
                return 1;
            } else if (x < xBoundary && y > yBoundary) {
                return 3;
            } else if (x > xBoundary && y > yBoundary) {
                return 2;
            }
            return 0;
        }

        public Location getLocation() {
            return new Location(x, y);
        }
    }

    record Location(int x, int y) implements Comparator<Location> {
        @Override
        public int compare(Location o1, Location o2) {
            return  (o1.x == o2.x && o1.y == o2.y) ? 0 : (o1.x < o2.x || o1.y < o2.y) ? -1 : 1;
        }
    }

    static {
        robots = getLines("14.txt").stream().map(Robot::new).toList();
        numOfRobots = robots.size();
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        var quadrants = robots.stream().map(robot -> {
            robot.move(100);
            return robot.getQuadrantNum();
        }).toList();

        var q1 = quadrants.stream().filter(ele -> ele == 1).count();
        var q2 = quadrants.stream().filter(ele -> ele == 2).count();
        var q3 = quadrants.stream().filter(ele -> ele == 3).count();
        var q4 = quadrants.stream().filter(ele -> ele == 4).count();

        var answer = q1 * q2 * q3 * q4;

        System.out.println("Part one answer: " + answer);
    }

    private static void partTwo() {
        robots = getLines("14.txt").stream().map(Robot::new).toList();

        int seconds = 0;
        boolean hasArrange = false;
        while (!hasArrange) {
            seconds++;
            robots.forEach(robot -> robot.move(1));
            if (numOfRobots == robots.stream().map(Robot::getLocation).distinct().count()) {
                hasArrange = true;
            }
        }

        System.out.println("Part two answer: " + seconds);
    }

}

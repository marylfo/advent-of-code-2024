package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day18 {

    enum TileType {
        SAFE('.'),
        CORRUPTED('#');

        final char value;
        TileType(Character value) {
            this.value = value;
        }

        public static TileType from(Character input) {
            for (TileType type: TileType.values()) {
                if (type.value == input) {
                    return type;
                }
            }
            return null;
        }
    }

    static class Tile {
        private final TileType type;
        private Tile up;
        private Tile down;
        private Tile left;
        private Tile right;

        Tile(String input) {
            type = TileType.from(input.charAt(0));
            up = null;
            down = null;
            left = null;
            right = null;
        }

        public void setUp(Tile up) {
            this.up = up;
        }

        public void setDown(Tile down) {
            this.down = down;
        }

        public void setLeft(Tile left) {
            this.left = left;
        }

        public void setRight(Tile right) {
            this.right = right;
        }

        boolean isAvailable() {
            return type == TileType.SAFE ;
        }

        List<Tile> getAvailableTiles() {
            List<Tile> tiles = new ArrayList<>();
            if (up != null && up.isAvailable()) {
                tiles.add(up);
            }

            if (down != null && down.isAvailable()) {
                tiles.add(down);
            }

            if (left != null && left.isAvailable()) {
                tiles.add(left);
            }

            if (right != null && right.isAvailable()) {
                tiles.add(right);
            }

            return tiles;
        }
    }

    static class Map {

        private final List<List<Tile>> map = new ArrayList<>();
        private final int mapSize;
        final Tile endTile;
        private final List<String> fallingByte;

        Map(String fileName, int numOfCorruptedLoc, int mapSize) {
            this.mapSize = mapSize;

            for (int i = 0; i < mapSize; i++) {
                var line = new ArrayList<Tile>();
                for (int j = 0; j < mapSize; j++) {
                    line.add(new Tile("."));
                }
                map.add(line);
            }

            List<String> inputLines = getLines(fileName);
            inputLines.subList(0, numOfCorruptedLoc).forEach(this::setFallingByte);
            setNeighbour();
            endTile = map.getLast().getLast();

            fallingByte = inputLines.subList(numOfCorruptedLoc, inputLines.size());
        }

        private void setFallingByte(String input) {
            var coordinate = input.split(",");
            var x = Integer.parseInt(coordinate[0]);
            var y = Integer.parseInt(coordinate[1]);
            var newLine = map.get(y);
            newLine.set(x, new Tile("#"));
            map.set(y, newLine);
        }

        private Tile getItemAt(int row, int column) {
            return map.get(row).get(column);
        }

        private void setNeighbour() {
            for (var i = 0; i < mapSize; i++) {
                for (var j = 0; j < mapSize; j++) {
                    var curr = getItemAt(i, j);

                    if (i != 0) {
                        curr.setUp(getItemAt(i-1, j));
                    }
                    if (i != (mapSize-1)) {
                        curr.setDown(getItemAt(i+1, j));
                    }
                    if (j != 0) {
                        curr.setLeft(getItemAt(i, j-1));
                    }
                    if (j != (mapSize-1)) {
                        curr.setRight(getItemAt(i, j+1));
                    }
                }
            }
        }

        List<List<Tile>> getReachableTiles(int step) {
            List<List<Tile>> reachedTiles = new ArrayList<>();

            if (step > 0) {
                List<Tile> currentLocations;

                for (var i = 0; i < step; i++) {
                    if (i == 0) {
                        currentLocations = new ArrayList<>();
                        currentLocations.add(getItemAt(0, 0));
                    } else {
                        currentLocations = reachedTiles.get(i - 1);
                    }

                    var uniquePlots = new HashSet<>(currentLocations.stream()
                            .map(Tile::getAvailableTiles)
                            .flatMap(List::stream)
                            .toList());

                    reachedTiles.add(uniquePlots.stream().toList());
                }
            }

            return reachedTiles;
        }

        String afterFallingByte() {
            var fallingByteStr = fallingByte.removeFirst();
            setFallingByte(fallingByteStr);
            setNeighbour();

            return fallingByteStr;
        }
    }

    private static void partOne() {
        var map = new Map("18.txt", 1024, 71);
        var reachableTiles = map.getReachableTiles(500);
        var minOfStep = -1;
        for (int i = 0; i < reachableTiles.size(); i++) {
            if (reachableTiles.get(i).contains(map.endTile) && minOfStep < 0) {
                minOfStep = i+1;
            }
        }

        System.out.println("Part one answer: " + minOfStep);
    }

    private static void partTwo() {
        var map = new Map("18.txt", 1024, 71);
        while (!map.fallingByte.isEmpty()) {
            var str = map.afterFallingByte();
            if (map.getReachableTiles(700).stream().noneMatch(tile -> tile.contains(map.endTile))) {
                System.out.println("Part two answer: " + str);
                break;
            }
        }
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

}




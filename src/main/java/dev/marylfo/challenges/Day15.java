package dev.marylfo.challenges;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day15 {

    private static final List<List<String>> map = new ArrayList<>();
    private static final int verticalSize, horizontalSize;
    private static final List<String> moves;

    private static int robotX, robotY;

    static List<String> getItemsAtX(int xLoc) {
        return map.stream().map(line -> line.get(xLoc)).toList();
    }

    static List<String> getItemsAtY(int yLoc) {
        return map.get(yLoc);
    }

    static void setItemAtX(int xLoc, List<String> item) {
        for (int i = 0; i < verticalSize; i++) {
            var updated = new ArrayList<>(getItemsAtY(i));
            updated.set(xLoc, item.get(i));
            map.set(i, updated);
        }
    }

    static void setItemsAtY(int yLoc, List<String> items) {
        map.set(yLoc, items);
    }

    static {
        var input = getLines("15.txt");
        boolean isMap = true;
        int index = 0;
        while (isMap) {
            map.add(List.of(input.get(index++).split("")));
            if (input.get(index).equals("")) {
                isMap = false;
            }
        }

        verticalSize = map.size();
        horizontalSize = map.getFirst().size();

        moves = List.of(String.join("", input.subList(index, input.size())).split(""));

        locateRobot();
    }

    private static void locateRobot() {
        for (int i = 0; i < verticalSize; i++) {
            for (int j = 0; j < horizontalSize; j++) {
                if (map.get(i).get(j).equals("@")) {
                    robotX = j;
                    robotY = i;
                }
            }
        }
    }

    static List<String> moveLeft(List<String> input) {
        var robotIndex = input.indexOf("@");
        if (input.get(robotIndex-1).equals("#")) { // wall
            return input;
        }
        var emptyIndex = input.subList(0, robotIndex).lastIndexOf(".");
        var wallIndex = input.subList(0, robotIndex).lastIndexOf("#");
        if (emptyIndex == -1 || (wallIndex != -1 && wallIndex > emptyIndex)) {
            return input;
        } else {
            var list = new ArrayList<>(input.subList(0, emptyIndex));
            list.addAll(input.subList(emptyIndex+1, robotIndex+1));
            list.add(".");
            list.addAll(input.subList(robotIndex+1, input.size()));
            return list;
        }
    }

    static List<String> moveRight(List<String> input) {
        var robotIndex = input.indexOf("@");
        if (input.get(robotIndex+1).equals("#")) { // wall
            return input;
        }
        var emptyIndex = input.subList(robotIndex+1, input.size()).indexOf(".");
        var wallIndex = input.subList(robotIndex+1, input.size()).indexOf("#");
        if (emptyIndex == -1 || (wallIndex != -1 && wallIndex < emptyIndex)) {
            return input;
        } else {
            emptyIndex = robotIndex + input.subList(robotIndex+1, input.size()).indexOf(".");
            var list = new ArrayList<>(input.subList(0, robotIndex));
            list.add(".");
            list.addAll(input.subList(robotIndex, emptyIndex+1));
            list.addAll(input.subList(emptyIndex+2, input.size()));
            return list;
        }
    }

    static void partOne() {
        for (var move : moves) {
            switch (move) {
                case "<":
                    setItemsAtY(robotY, moveLeft(getItemsAtY(robotY)));
                    robotX = getItemsAtY(robotY).indexOf("@");
                    break;
                case "^":
                    setItemAtX(robotX, moveLeft(getItemsAtX(robotX)));
                    robotY = getItemsAtX(robotX).indexOf("@");
                    break;
                case ">":
                    setItemsAtY(robotY, moveRight(getItemsAtY(robotY)));
                    robotX = getItemsAtY(robotY).indexOf("@");
                    break;
                case "v":
                    setItemAtX(robotX, moveRight(getItemsAtX(robotX)));
                    robotY = getItemsAtX(robotX).indexOf("@");
                    break;
            }
        }

        var sum = BigInteger.ZERO;

        for (int i = 0; i < verticalSize; i++) {
            for (int j = 0; j < horizontalSize; j++) {
                if (map.get(i).get(j).equals("O")) {
                    sum = sum.add(BigInteger.valueOf(i).multiply(BigInteger.valueOf(100)).add(BigInteger.valueOf(j)));
                }
            }
        }

        System.out.println("Part one answer: " + sum);
    }

    public static void main(String[] args) {
        partOne();
    }

}

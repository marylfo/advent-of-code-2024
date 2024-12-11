package dev.marylfo.challenges;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.marylfo.services.FileService.getLines;

public class Day11 {

    private static final HashMap<BigInteger, Stone> stones = new HashMap<>();
    private static final HashMap<BigInteger, List<Stone>> nextStonesCache = new HashMap<>(
            Map.of(BigInteger.ZERO, List.of(new Stone(BigInteger.ONE)))
    );

    static class Stone {

        BigInteger number;

        Stone(BigInteger number) {
            this.number = number;
        }

        static Stone of(String number) {
            var key = new BigInteger(number);

            if (!stones.containsKey(key)) {
                stones.put(key, new Stone(key));
            }

            return stones.get(key);
        }

        List<Stone> change() {
            calculateStones();
            return nextStonesCache.get(number);
        }

        private void calculateStones() {
            if (nextStonesCache.containsKey(number)) {
                return;
            }

            if (number.toString().length() % 2 == 0) {
                var numberStr = number.toString();
                var nextStone = List.of(
                        Stone.of(numberStr.substring(0, numberStr.length()/2)),
                        Stone.of(numberStr.substring(numberStr.length()/2)));
                nextStonesCache.put(number, nextStone);
            } else {
                var nextStone = List.of(new Stone(number.multiply(BigInteger.valueOf(2024))));
                nextStonesCache.put(number, nextStone);
            }
        }
    }

    private static final List<Stone> initialArrangement = Arrays.stream(getLines("11.txt").getFirst().split(" "))
            .map(Stone::of)
            .toList();

    private static List<Stone> afterOneBlink(List<Stone> input) {
        return input.stream().map(Stone::change).flatMap(List::stream).toList();
    }

    private static List<Stone> multipleBlink(Stone s, int time) {
        var stones = List.of(s);

        for (int blink = 0; blink < time; blink++) {
            stones = afterOneBlink(stones);
        }

        return stones;
    }

    private static void partOne() {
        var numOfStone = initialArrangement.stream()
                .map(stone -> multipleBlink(stone, 25).size())
                .reduce(0, Integer::sum);
        System.out.println("Part one answer: " + numOfStone);
    }

    public static void main(String[] args) {
        partOne();
    }

}

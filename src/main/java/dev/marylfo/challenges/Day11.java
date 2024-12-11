package dev.marylfo.challenges;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.marylfo.services.FileService.getLines;

public class Day11 {

    private record StoneBlink(BigInteger number, BigInteger blink) {}

    private static final HashMap<BigInteger, Stone> stones = new HashMap<>();
    private static final HashMap<BigInteger, List<Stone>> nextStonesCache = new HashMap<>(Map.of(BigInteger.ZERO, List.of(new Stone(BigInteger.ONE))));
    private static final HashMap<StoneBlink, BigInteger> countCache = new HashMap<>();

    private static final List<Stone> initialArrangement = Arrays.stream(getLines("11.txt").getFirst().split(" "))
            .map(Stone::of)
            .toList();

    static class Stone {

        BigInteger number;

        Stone(BigInteger number) {
            this.number = number;
        }

        static Stone of(String number) {
            return of(new BigInteger(number));
        }

        static Stone of(BigInteger number) {
            if (!stones.containsKey(number)) {
                stones.put(number, new Stone(number));
            }
            return stones.get(number);
        }

        List<Stone> afterOneBlink() {
            calculateStones();
            return nextStonesCache.get(number);
        }

        private void calculateStones() {
            if (nextStonesCache.containsKey(number)) {
                return;
            }

            if (number.toString().length() % 2 == 0) {
                var numberStr = number.toString();
                nextStonesCache.put(number, List.of(
                        Stone.of(numberStr.substring(0, numberStr.length()/2)),
                        Stone.of(numberStr.substring(numberStr.length()/2))));
            } else {
                nextStonesCache.put(number, List.of(new Stone(number.multiply(BigInteger.valueOf(2024)))));
            }
        }

    }

    private static List<Stone> multipleBlink(Stone s, int time) {
        var stones = List.of(s);

        for (int blink = 0; blink < time; blink++) {
            stones = stones.stream().map(Stone::afterOneBlink).flatMap(List::stream).toList();
        }

        return stones;
    }

    private static void partOne() {
        var numOfStone = initialArrangement.stream()
                .map(stone -> multipleBlink(stone, 25).size())
                .reduce(0, Integer::sum);
        System.out.println("Part one answer: " + numOfStone);
    }

    private static BigInteger getCount(StoneBlink stoneBlink) {
        if (countCache.containsKey(stoneBlink)) {
            return countCache.get(stoneBlink);
        }

        if (stoneBlink.blink.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        } else {
            var count = Stone.of(stoneBlink.number).afterOneBlink()
                    .stream().map(num -> getCount(new StoneBlink(num.number, stoneBlink.blink.subtract(BigInteger.ONE))))
                    .reduce(BigInteger.ZERO, BigInteger::add);
            countCache.put(stoneBlink, count);
            return count;
        }
    }

    private static void partTwo() {
        var numOfStone = initialArrangement.stream()
                .map(stone -> getCount(new StoneBlink(stone.number, new BigInteger("75"))))
                .reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println("Part two answer: " + numOfStone);
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

}

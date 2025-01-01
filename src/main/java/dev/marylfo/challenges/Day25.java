package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.marylfo.services.FileService.getLines;

public class Day25 {

    private static final String LOCK_START = "#".repeat(5);

    private static final List<List<Integer>> locks = new ArrayList<>();
    private static final List<List<Integer>> keys = new ArrayList<>();

    private static boolean isLock(String firstLine) {
        return LOCK_START.equals(firstLine);
    }

    private static List<String> getTranspose(List<String> input) {
        return IntStream.range(0, input.getFirst().length())
                .mapToObj(i -> input.stream()
                        .map(s -> String.valueOf(s.charAt(i)))
                        .collect(Collectors.joining()))
                .toList();
    }

    private static void saveLockOrKey(List<String> pattern) {
        var pinToHeight = getTranspose(pattern.subList(1, pattern.size()-1)).stream()
                .map(line -> Math.toIntExact(Arrays.stream(line.split(""))
                        .filter(c -> c.equals("#"))
                        .count()))
                .toList();

        if (isLock(pattern.getFirst())) {
            locks.add(pinToHeight);
        } else {
            keys.add(pinToHeight);
        }
    }

    static {
        var lines = getLines("25.txt");
        int numOfLockAndKey = lines.size()/8;

        IntStream.range(0, numOfLockAndKey+1)
                .mapToObj(i -> lines.subList(7*i+i, 7*(i+1)+i))
                .forEach(Day25::saveLockOrKey);
    }

    private static int isValid(List<Integer> lock, List<Integer> key) {
        for (int i = 0; i < lock.size(); i++) {
            if (lock.get(i) + key.get(i) > 5) {
                return 0;
            }
        }
        return 1;
    }

    public static void main(String[] args) {
        var count = 0;
        for (var lock : locks) {
            for (var key: keys) {
                count += isValid(lock, key);
            }
        }
        System.out.println("Part one answer: " + count);
    }

}

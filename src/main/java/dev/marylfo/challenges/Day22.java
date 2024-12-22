package dev.marylfo.challenges;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static dev.marylfo.services.FileService.getLines;

public class Day22 {

    public static final BigInteger PRUNE_DIVISOR = BigInteger.valueOf(16777216);
    public static final BigInteger MIX_MULTI_ONE = BigInteger.valueOf(64);
    public static final BigInteger MIN_MULTI_TWO = BigInteger.valueOf(2048);
    public static final BigInteger MIX_DIVISOR = BigInteger.valueOf(32);

    private static BigInteger mix(BigInteger value, BigInteger secretNum) {
        return value.xor(secretNum);
    }

    private static BigInteger prune(BigInteger value) {
        return value.mod(PRUNE_DIVISOR);
    }

    private static BigInteger getNextSecretNum(BigInteger secret) {
        secret = mix(secret.multiply(MIX_MULTI_ONE), secret);
        secret = prune(secret);
        secret = mix(secret.divide(MIX_DIVISOR), secret);
        secret = prune(secret);
        secret = mix(secret.multiply(MIN_MULTI_TWO), secret);
        return prune(secret);
    }

    private static BigInteger getSecretAfterSteps(String input, int step) {
        var secret = new BigInteger(input);
        for (int i = 0; i < step; i++ ){
            secret = getNextSecretNum(secret);
        }
        return secret;
    }

    private static List<Integer> getSecretDigitAfterSteps(String input, int step) {
        var secret = new BigInteger(input);
        var secretDigits = new ArrayList<Integer>();
        secretDigits.add(getLastDigit(secret));
        for (int i = 0; i < step; i++ ){
            secret = getNextSecretNum(secret);
            secretDigits.add(getLastDigit(secret));
        }
        return secretDigits;
    }

    private static int getLastDigit(BigInteger value) {
        return value.mod(BigInteger.valueOf(10)).intValue();
    }

    private static void partOne() {
        var sum = getLines("22.txt").parallelStream().map(input -> getSecretAfterSteps(input, 2000))
                .reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println("Part one answer: " + sum);
    }

    private static List<Integer> getChangesInDigit(List<Integer> list) {
        var changes = new ArrayList<Integer>();
        for (int i = 1; i < list.size(); i++) {
            changes.add(list.get(i) - list.get(i-1));
        }
        return changes;
    }

    private static List<Integer> getChangesAtStep(List<Integer> changes, int loc) {
        return changes.subList(loc-4, loc);
    }

    private static HashMap<List<Integer>, Integer> getBananaHashMap(List<Integer> secretDigits, List<Integer> changes) {
        HashMap<List<Integer>, Integer> changeBanana = new HashMap<>();


        for (int i = 4; i < secretDigits.size(); i++) {
            var changeAtStep = getChangesAtStep(changes, i);
            var bananaAtStep = secretDigits.get(i);

            if (!changeBanana.containsKey(changeAtStep)) {
                changeBanana.put(changeAtStep, bananaAtStep);
            }

        }
        return changeBanana;
    }

    private static void partTwo() {
        var lineInput = getLines("22.txt");
        var secretDigits = lineInput.parallelStream().map(input -> getSecretDigitAfterSteps(input, 2000))
                .toList();

        var changes = secretDigits.parallelStream().map(Day22::getChangesInDigit).toList();

        List<List<Map.Entry<List<Integer>, Integer>>> allChangesBanana = new ArrayList<>();

        HashSet<List<Integer>> allAvailableChanges = new HashSet<>();

        for (int i = 0; i < lineInput.size(); i++) {
            var hashMap = getBananaHashMap(secretDigits.get(i), changes.get(i));
            allAvailableChanges.addAll(hashMap.keySet());
            var sortHashMap = hashMap.entrySet().stream().sorted((a,b) -> Integer.compare(b.getValue(), a.getValue())).toList();
            allChangesBanana.add(sortHashMap);
        }

        var comboChangesBanana = allChangesBanana.stream().flatMap(List::stream).toList();

        var maxBananaCount = allAvailableChanges.parallelStream()
                .mapToInt(change -> comboChangesBanana.parallelStream()
                        .filter(entry -> entry.getKey().equals(change))
                        .map(Map.Entry::getValue).reduce(0, Integer::sum))
                .max().orElse(-1);

        System.out.println("Part two answer: " + maxBananaCount);
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

}

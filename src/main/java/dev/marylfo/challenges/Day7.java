package dev.marylfo.challenges;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day7 {

    record Equation(BigInteger testValue, List<BigInteger> numbers) {}
    private static final List<Equation> equations;

    private static final boolean isTest = false;
    private static boolean hasConcatOperator = false;

    static {
        equations = getLines(isTest ? "7-test.txt" : "7.txt")
                .stream()
                .map(line -> {
                    var numbers = line.split(": ");
                    return new Equation(
                            new BigInteger(numbers[0]),
                            Arrays.stream((numbers[1]).split(" ")).map(BigInteger::new).toList()
                    );
                }).toList();
    }

    private static List<BigInteger> getOperationResult(List<BigInteger> inputs, BigInteger next) {
        var result = new ArrayList<BigInteger>();
        for (var input : inputs) {
            result.add(input.add(next));
            result.add(input.multiply(next));

            if (hasConcatOperator) {
                result.add(new BigInteger(input.toString().concat(next.toString())));
            }
        }
        return result;
    }

    private static boolean isTrueOperation(List<BigInteger> inputs, BigInteger lastInput, BigInteger testValue) {
        for (var input : inputs) {
            if (testValue.equals(input.add(lastInput)) || testValue.equals(input.multiply(lastInput))) {
                return true;
            }

            if (hasConcatOperator) {
                var concatValue = new BigInteger(input.toString().concat(lastInput.toString()));
                if (testValue.equals(concatValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void partOne() {
        performCoreLogic("Part one answer: ");
    }

    public static void partTwo() {
        hasConcatOperator = true;
        performCoreLogic("Part two answer: ");
    }

    private static void performCoreLogic(String str) {
        BigInteger sum = new BigInteger("0");

        for (var equation : equations) {
            var input = List.of(equation.numbers.get(0));
            for (int j = 1; j < equation.numbers.size() - 1; j++) {
                input = getOperationResult(input, equation.numbers.get(j));
            }

            if (isTrueOperation(input, equation.numbers.get(equation.numbers.size()-1), equation.testValue)) {
                sum = sum.add(equation.testValue);
            }
        }

        System.out.println(str + sum);
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

}

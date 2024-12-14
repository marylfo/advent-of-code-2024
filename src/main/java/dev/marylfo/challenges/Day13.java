package dev.marylfo.challenges;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static dev.marylfo.services.FileService.getLines;

public class Day13 {

    private static final List<Machine> machines = new ArrayList<>();

    static {
        var oneLine = String.join(" ", getLines("13.txt"));
        var pattern = Pattern.compile("Button A: X\\+(\\d+), Y\\+(\\d+) Button B: X\\+(\\d+), Y\\+(\\d+) Prize: X=(\\d+), Y=(\\d+)");
        var matcher = pattern.matcher(oneLine);

        while (matcher.find()) {
            machines.add(Machine.of(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3),
                    matcher.group(4),
                    matcher.group(5),
                    matcher.group(6)));
        }
    }

    record Machine (int xA, int yA, int xB, int yB, int xPrize, int yPrize) {
        static Machine of(String xA, String yA, String xB, String yB, String xPrize, String yPrize) {
            return new Machine(Integer.parseInt(xA), Integer.parseInt(yA), Integer.parseInt(xB), Integer.parseInt(yB), Integer.parseInt(xPrize), Integer.parseInt(yPrize));
        }

        private PressCount getPressCount() {
            var determinant = xA * yB - xB * yA;

            float x = (float) (yB * xPrize - xB * yPrize);
            float y = (float) (-yA * xPrize + xA * yPrize);

            if (x%determinant != 0 || y%determinant != 0) {
                return null;
            }

            return new PressCount((int) x/determinant, (int) y/determinant);
        }

        Integer getToken() {
            if (getPressCount() == null) {
                return null;
            }
            var pressCount = getPressCount();

            if (pressCount.a > 100 || pressCount.b > 100) {
                return null;
            } else {
                return pressCount.a * 3 + pressCount.b;
            }
        }

        private BigIntegerPressCount getPartTwoPressCount() {
            var determinant = xA * yB - xB * yA;
            var newXPrize = new BigDecimal("10000000000000").add(BigDecimal.valueOf(xPrize));
            var newYPrize = new BigDecimal("10000000000000").add(BigDecimal.valueOf(yPrize));

            var x = newXPrize.multiply(BigDecimal.valueOf(yB)).subtract(newYPrize.multiply(BigDecimal.valueOf(xB)));
            var y = newYPrize.multiply(BigDecimal.valueOf(xA)).subtract(newXPrize.multiply(BigDecimal.valueOf(yA)));

            if (!x.remainder(BigDecimal.valueOf(determinant)).equals(BigDecimal.ZERO)||
                    !y.remainder(BigDecimal.valueOf(determinant)).equals(BigDecimal.ZERO)) {
                return null;
            }

            return new BigIntegerPressCount(x.divide(BigDecimal.valueOf(determinant)), y.divide(BigDecimal.valueOf(determinant)));
        }

        BigInteger getPartTwoToken() {
            if (getPartTwoPressCount() == null) {
                return null;
            }
            var pressCount = getPartTwoPressCount();

            return new BigInteger(String.valueOf(pressCount.a.multiply(BigInteger.valueOf(3)).add(pressCount.b)));
        }
    }

    record PressCount(int a, int b) {}

    record BigIntegerPressCount(BigInteger a, BigInteger b) {
        public BigIntegerPressCount(BigDecimal a, BigDecimal b) {
            this(new BigInteger(a.toString()), new BigInteger(b.toString()));
        }
    }

    public static void main(String[] args) {
        var sum = machines.stream().map(Machine::getToken).filter(Objects::nonNull).reduce(0, Integer::sum);
        System.out.println("Part one answer: " + sum);

        var partTwoSum = machines.stream().map(Machine::getPartTwoToken).filter(Objects::nonNull).reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println("Part two answer: " + partTwoSum);
    }

}

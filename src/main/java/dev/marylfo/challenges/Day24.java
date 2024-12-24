package dev.marylfo.challenges;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.marylfo.services.FileService.getLines;

enum Operation { OR, AND, XOR }

public class Day24 {

    private static final HashMap<String, Wire> wires;

    record Gate(Operation type, List<Wire> wires) {
        boolean canConnect() {
            return wires.stream().map(wire -> wire.value).noneMatch(value -> value == -1);
        }

        int getResult() {
            return switch (type) {
                case OR -> wires.stream().map(Wire::getValue).anyMatch(value -> value == 1) ? 1 : 0;
                case AND -> wires.stream().map(Wire::getValue).allMatch(value -> value == 1) ? 1 : 0;
                case XOR -> wires.stream().map(Wire::getValue).distinct().count() == 2 ? 1 : 0;
            };
        }
    }

    static class Wire {

        String name;
        int value = -1;
        Gate gate;

        public Wire(String name) {
            this.name = name;
        }

        static public Wire of(String input) {
            String[] split = input.split(": ");
            var wire = new Wire(split[0]);
            wire.setValue(Integer.parseInt(split[1]));
            return wire;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public Wire getThis() {
            return this;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void setGate(Gate gate) {
            this.gate = gate;
        }

    }

    private static Operation getOperation(String o) {
        return switch (o) {
            case "OR" -> Operation.OR;
            case "AND" -> Operation.AND;
            case "XOR" -> Operation.XOR;
            default -> throw new IllegalStateException("Unexpected value: " + o);
        };
    }

    static {
        var lines = getLines("24.txt");
        var separateIndex = lines.indexOf("");

        wires = new HashMap<>(lines.subList(0, separateIndex).stream()
                .map(Wire::of)
                .collect(Collectors.toMap(Wire::getName, Wire::getThis)));

        var gateDetails = lines.subList(separateIndex + 1, lines.size());

        wires.putAll(gateDetails.stream()
                .map(line -> List.of(line.split(" ")))
                .flatMap(List::stream)
                .distinct()
                .filter(wireName -> !List.of("OR", "AND", "XOR", "->").contains(wireName) &&
                        !wires.containsKey(wireName))
                .map(Wire::new)
                .collect(Collectors.toMap(Wire::getName, Wire::getThis)));

        gateDetails.forEach(detail -> {
            // ntg XOR fgs -> mjb
            var split = detail.split(" ");
            var wire = wires.get(split[4]);
            var connectedWires = List.of(wires.get(split[0]), wires.get(split[2]));
            wire.setGate(new Gate(getOperation(split[1]), connectedWires));
        });
    }

    static boolean isAllWireHasValue() {
        return wires.values().stream().map(Wire::getValue).noneMatch(value -> value == -1);
    }

    private static void simulateGates() {
        while (!isAllWireHasValue()) {
            wires.values().stream().filter(wire -> wire.value == -1).forEach(wire -> {
                if (wire.gate.canConnect()) {
                    wire.value = wire.gate.getResult();
                }
            });
        }
    }

    private static void partOne() {
        simulateGates();

        var num = wires.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("z"))
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(Map.Entry::getValue)
                .peek(w -> System.out.println(w.getName() + "\t" + w.getValue()))
                .map(w -> w.getValue() == 1 ? BigInteger.valueOf(2).pow(Integer.parseInt(w.getName().substring(1,3))) : BigInteger.ZERO)
                .reduce(BigInteger.ZERO, BigInteger::add);

        System.out.println("Part one answer: " + num);
    }

    public static void main(String[] args) {
        partOne();
    }

}

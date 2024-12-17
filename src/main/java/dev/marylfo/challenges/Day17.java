package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.marylfo.services.FileService.getLines;

class ComputerProgram {

    private int registerA, registerB, registerC;
    private final List<Integer> instruction;
    private int instructionPointer = 0;
    private boolean isPointerChange = false;

    public ComputerProgram(String fileName) {
        var line = getLines(fileName);

        registerA = Integer.parseInt(line.get(0).split(": ")[1]);
        registerB = Integer.parseInt(line.get(1).split(": ")[1]);
        registerC = Integer.parseInt(line.get(2).split(": ")[1]);

        instruction = Stream.of(line.get(4).split(": ")[1].split(",")).map(Integer::parseInt).toList();
    }

    private int getCombo(int value) {
        return switch (value) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> registerA;
            case 5 -> registerB;
            case 6 -> registerC;
            default -> -1;
        };
    }

    private Integer runProgram(int opcodeIndex, int operandIndex) {
        switch (instruction.get(opcodeIndex)) {
            case 0:
                registerA = Double.valueOf(registerA / Math.pow(2, getCombo(instruction.get(operandIndex)))).intValue();
                break;
            case 1:
                registerB = registerB ^ instruction.get(operandIndex);
                break;
            case 2:
                registerB = getCombo(instruction.get(operandIndex)) % 8;
                break;
            case 3:
                if (registerA != 0) {
                    isPointerChange = true;
                    instructionPointer = instruction.get(operandIndex);
                }
                break;
            case 4:
                registerB = registerB ^ registerC;
                break;
            case 5:
                return getCombo(instruction.get(operandIndex)) % 8;
            case 6:
                registerB = Double.valueOf(registerA / Math.pow(2, getCombo(instruction.get(operandIndex)))).intValue();
                break;
            case 7:
                registerC = Double.valueOf(registerA / Math.pow(2, getCombo(instruction.get(operandIndex)))).intValue();
                break;
        }

        return null;
    }

    public String runAndGetOutput() {
        List<Integer> output = new ArrayList<>();
        var currentInstructionPointer = instructionPointer;
        while (currentInstructionPointer < instruction.size() - 1) {
            var tempOutput = runProgram(currentInstructionPointer, currentInstructionPointer + 1);

            if (tempOutput != null) {
                output.add(tempOutput);
            }

            if (isPointerChange) {
                currentInstructionPointer = instructionPointer;
                isPointerChange = false;
            } else {
                currentInstructionPointer += 2;
            }
        }

        return output.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

}

public class Day17 {

    public static void main(String[] args) {
        var program = new ComputerProgram("17.txt");
        System.out.println("Part one answer: " + program.runAndGetOutput());
    }

}

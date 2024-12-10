package dev.marylfo.challenges;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static dev.marylfo.services.FileService.getLines;

public class Day9 {

    private static final List<BigInteger> blocks = new ArrayList<>();
    private static final int firstEmptyIndex;
    private static int lastBlockIndex;

    record FileBlock(int fileId, int startIndex, int size) {}
    record EmptySpace(int index, int size) {}

    private static final List<BigInteger> partTwoBlocks = new ArrayList<>();
    private static final List<FileBlock> fileBlocks = new ArrayList<>();
    private static final List<EmptySpace> emptySpaces = new ArrayList<>();

    static {
        boolean isTest = false;
        var values = getLines(isTest ? "9-test.txt" : "9.txt").get(0).split("");
        var idNum = 0;
        for (int i = 0; i < values.length; i++) {
            if (i%2 == 0) {
                if (Integer.parseInt(values[i]) > 0) {
                    fileBlocks.add(new FileBlock(idNum, blocks.size(), Integer.parseInt(values[i])));
                }
                var count = Integer.parseInt(values[i]);
                for (int blockLen = 0; blockLen < count; blockLen++) {
                    blocks.add(BigInteger.valueOf(idNum));
                    partTwoBlocks.add(BigInteger.valueOf(idNum));
                }
                idNum++;
            } else {
                if (Integer.parseInt(values[i]) > 0) {
                    emptySpaces.add(new EmptySpace(blocks.size(), Integer.parseInt(values[i])));
                }
                for (int blockLen = 0; blockLen < Integer.parseInt(values[i]); blockLen++) {
                    blocks.add(null);
                    partTwoBlocks.add(null);
                }
            }
        }

        // first file id should never move
        fileBlocks.removeFirst();

        firstEmptyIndex = Integer.parseInt(values[0]);
        lastBlockIndex = blocks.size() - 1;

        while (blocks.get(lastBlockIndex) == null) {
            lastBlockIndex--;
        }
    }

    private static BigInteger popBlock(int index) {
        var lastBlockId = blocks.get(index);
        blocks.set(index, null);
        return lastBlockId;
    }

    private static int getNextInsertIndex(int currentIndex) {
        var nextIndex = currentIndex + 1;
        while (blocks.get(nextIndex) != null) {
            nextIndex++;
        }
        return nextIndex;
    }

    private static int getNextPopBlockIndex(int currentIndex) {
        var nextIndex = currentIndex - 1;
        while (blocks.get(nextIndex) == null) {
            nextIndex--;
        }
        return nextIndex;
    }

    private static void moveFileBlock() {
        var insertIndex = firstEmptyIndex;
        var popBlockIndex = lastBlockIndex;

        while (insertIndex < popBlockIndex) {
            blocks.set(insertIndex, popBlock(popBlockIndex));
            insertIndex = getNextInsertIndex(insertIndex);
            popBlockIndex = getNextPopBlockIndex(popBlockIndex);
        }
    }

    private static void removeEndingNull() {
        var lastIndex = blocks.size() - 1;
        while (blocks.get(lastIndex) == null) {
            blocks.remove(lastIndex);
            lastIndex--;
        }

        lastIndex = partTwoBlocks.size() - 1;
        while (partTwoBlocks.get(lastIndex) == null) {
            partTwoBlocks.remove(lastIndex);
            lastIndex--;
        }
    }

    private static BigInteger getChecksum(List<BigInteger> blocks) {
        var checksum = BigInteger.ZERO;
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i) != null) {
                checksum = checksum.add(blocks.get(i).multiply(BigInteger.valueOf(i)));
            }
        }
        return checksum;
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        moveFileBlock();
        removeEndingNull();
        System.out.println("Part one answer: " + getChecksum(blocks));
    }

    private static void partTwo() {
        moveWholeFileBlock();
        removeEndingNull();
        System.out.println("Part two answer: " + getChecksum(partTwoBlocks));
    }

    private static void moveWholeFileBlock() {
        while (!fileBlocks.isEmpty()) {
            var currentFileBlock = fileBlocks.removeLast();
            var isNotMove = true;
            var emptySpaceIndex = 0;

            while (emptySpaceIndex < emptySpaces.size() &&
                    isNotMove &&
                    currentFileBlock.startIndex > emptySpaces.get(emptySpaceIndex).index) {
                var emptySpace = emptySpaces.get(emptySpaceIndex);

                if (emptySpace.size > currentFileBlock.size) {
                    for (int i = 0; i < currentFileBlock.size(); i++) {
                        partTwoBlocks.set(emptySpace.index + i, BigInteger.valueOf(currentFileBlock.fileId));
                        partTwoBlocks.set(currentFileBlock.startIndex + i, null);
                    }
                    emptySpaces.set(emptySpaceIndex, new EmptySpace(emptySpace.index + currentFileBlock.size, emptySpace.size - currentFileBlock.size));
                    isNotMove = false;
                } else if (emptySpace.size == currentFileBlock.size) {
                    for (int i = 0; i < emptySpace.size(); i++) {
                        partTwoBlocks.set(emptySpace.index + i, BigInteger.valueOf(currentFileBlock.fileId));
                        partTwoBlocks.set(currentFileBlock.startIndex + i, null);
                    }
                    emptySpaces.remove(emptySpace);
                    isNotMove = false;
                }

                emptySpaceIndex++;
            }
        }
    }

}

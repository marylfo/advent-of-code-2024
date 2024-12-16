package dev.marylfo.challenges;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import static dev.marylfo.services.FileService.getLines;

public class Day16 {

    record Step(int currRow, int currCol, int deltaRow, int deltaCol) {
        public boolean isEnd() {
            return map.get(currRow).get(currCol).equals("E");
        }

        public boolean isNotSameDirection(Direction dir) {
            return !(this.deltaRow == dir.deltaRow && this.deltaCol == dir.deltaCol);
        }

        public boolean isNotOppositeDirection(Direction dir) {
            return !(this.deltaRow == -dir.deltaRow && this.deltaCol == -dir.deltaCol);
        }
    }

    record Direction(int deltaRow, int deltaCol) {}

    record StepState(int score, Step step) implements Comparable<StepState> {
        @Override
        public int compareTo(StepState other) {
            return Integer.compare(this.score, other.score);
        }
    }

    static List<Direction> directions = new ArrayList<>(List.of(
            new Direction(-1, 0),  // UP
            new Direction(1, 0),   // DOWN
            new Direction(0, -1),  // LEFT
            new Direction(0, 1)    // RIGHT
    ));

    private static final List<List<String>> map;
    private static final int rowSize;
    private static final int colSize;

    private final static HashSet<Step> visitedSteps = new HashSet<>();
    private final static PriorityQueue<StepState> pq = new PriorityQueue<>();

    static {
        map = getLines("16.txt").stream().map(line -> List.of(line.split(""))).toList();
        rowSize = map.size();
        colSize = map.getFirst().size();
    }

    private static boolean isValidLoc(int nextRow, int nextCol) {
        return nextRow > 0 && nextRow < rowSize - 1 && nextCol > 0 && nextCol < colSize -1
                && !map.get(nextRow).get(nextCol).equals("#");
    }

    private static void resetQueueAndSet() {
        visitedSteps.clear();
        pq.clear();
        pq.add(new StepState(0, new Step(rowSize-2,1,0,1)));
    }

    private static void moveInSameDirection(StepState stepState) {
        var currentScore = stepState.score;
        Step currentStep = stepState.step();
        int nextRow = currentStep.currRow() + currentStep.deltaRow();
        int nextCol = currentStep.currCol() + currentStep.deltaCol();
        if (isValidLoc(nextRow, nextCol)) {
            pq.add(
                    new StepState(currentScore + 1,
                            new Step(
                                    nextRow,
                                    nextCol,
                                    currentStep.deltaRow(),
                                    currentStep.deltaCol()
                            )
                    )
            );
        }
    }

    private static void moveInRotateDirection(StepState stepState, Direction nextDirection) {
        var currentScore = stepState.score;
        Step currentStep = stepState.step();
        int nextRow = currentStep.currRow() + nextDirection.deltaRow();
        int nextCol = currentStep.currCol() + nextDirection.deltaCol();
        if (isValidLoc(nextRow, nextCol)) {
            pq.add(
                    new StepState(currentScore + 1001,
                            new Step(
                                    nextRow,
                                    nextCol,
                                    nextDirection.deltaRow(),
                                    nextDirection.deltaCol()
                            )
                    )
            );
        }
    }

    public static int getLowestScore() {
        resetQueueAndSet();
        while (!pq.isEmpty()) {
            var currentQueue = pq.remove();
            var currentScore = currentQueue.score;
            var currentStep = currentQueue.step;
            if (currentStep.isEnd()) {
                return currentScore;
            }
            if (visitedSteps.contains(currentStep)) {
                continue;
            }
            visitedSteps.add(currentStep);
            for (var nextDirection : directions) {
                if (currentStep.isNotSameDirection(nextDirection) && currentStep.isNotOppositeDirection(nextDirection)) {
                    moveInRotateDirection(currentQueue, nextDirection);
                } else {
                    moveInSameDirection(currentQueue);
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println("Part one answer: " + getLowestScore());
    }

}

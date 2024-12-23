package dev.marylfo.challenges;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.marylfo.services.FileService.getLines;

public class Day23 {

    private static final Map<String, Computer> computers;
    private static final HashSet<HashSet<Computer>> threeInterConnectedComputers = new HashSet<>();

    static class Computer {

        String name;
        Set<Computer> neighbours = new HashSet<>();

        public Computer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Computer getThis() {
            return this;
        }

        public Set<Computer> getNeighbours() {
            return neighbours;
        }

        public void setNeighbours(Set<Computer> input) {
            this.neighbours = input;
        }

    }

    private static Computer getComputerByName(String name) {
        return computers.get(name);
    }

    static {
        var connections = getLines("23.txt");
        computers = connections.stream()
                .map(connection -> List.of(connection.split("-")))
                .flatMap(List::stream)
                .distinct()
                .map(Computer::new)
                .collect(Collectors.toMap(Computer::getName, Computer::getThis));

        for (var comp: computers.entrySet()) {
            var name = comp.getKey();
            var computer = comp.getValue();

            var neighbours = connections.stream().filter(line -> line.contains(name))
                    .map(connection -> List.of(connection.split("-")))
                    .flatMap(List::stream)
                    .filter(compName -> !compName.equals(name))
                    .map(Day23::getComputerByName)
                    .collect(Collectors.toSet());

            computer.setNeighbours(neighbours);
        }
    }

    private static void searchForThreeInterConnectedComputers() {
        for (var startComp: computers.values()) {
            for (var middleComp: startComp.getNeighbours()) {
                for (var lastComp: computers.get(middleComp.name).getNeighbours()) {
                    if (computers.get(lastComp.name).getNeighbours().contains(startComp)) {
                        threeInterConnectedComputers.add(new HashSet<>(Set.of(startComp, middleComp, lastComp)));
                    }
                }
            }
        }
    }

    private static void partOne() {
        searchForThreeInterConnectedComputers();

        var value = threeInterConnectedComputers.stream()
                .filter(connected -> connected.stream().anyMatch(comp -> comp.getName().charAt(0) == 't'))
                .count();

        System.out.println("Part one answer: " + value);
    }

    private static HashSet<HashSet<Computer>> getNextDegreeInterConnectedComputer(HashSet<HashSet<Computer>> currInterConnectedComputers) {
        var nextDegreeInterConnectedComputers = new HashSet<HashSet<Computer>>();

        for(var set: currInterConnectedComputers) {
            var neighbours = set.stream().map(Computer::getNeighbours).toList();
            var commonNeighbours = new HashSet<>(neighbours.getFirst());
            for (int i = 1; i < neighbours.size(); i++) {
                commonNeighbours.retainAll(neighbours.get(i));
            }

            if (!commonNeighbours.isEmpty()) {
                for (var neighbour: commonNeighbours) {
                    if (neighbour.getNeighbours().containsAll(set)) {
                        var nextDegreeSet = new HashSet<>(set);
                        nextDegreeSet.add(neighbour);
                        nextDegreeInterConnectedComputers.add((nextDegreeSet));
                    }
                }
            }
        }
        return nextDegreeInterConnectedComputers;
    }

    private static void partTwo() {
        var nextDegCluster = getNextDegreeInterConnectedComputer(threeInterConnectedComputers);

        while (nextDegCluster.size() > 1) {
            nextDegCluster = getNextDegreeInterConnectedComputer(nextDegCluster);
        }

        var largestClusterComputerNames = nextDegCluster.stream()
                    .flatMap(set -> set.stream().map(Computer::getName))
                    .sorted()
                    .collect(Collectors.joining(","));

        System.out.println("Part two answer: " + largestClusterComputerNames);
    }

    public static void main(String[] args) {
        partOne();
        partTwo();
    }

}

package simulation;

import simulation.states.*;
import java.util.*;

public class Simulation {
    private static final double STEP_DURATION = 1.0 / 25.0;
    private static final Random random = new Random();

    private double areaWidth;
    private double areaHeight;
    private int initialPopulation;
    private double entryFrequency;
    private double immunityPercentage;
    private boolean immunityEnabled;

    private List<Person> people;
    private double simulationTime;
    private int stepsCount;
    private double timeSinceLastEntry;

    public Simulation(double width, double height, int initPop,
                      double entryFreq, boolean immunity, double immunePct) {
        this.areaWidth = width;
        this.areaHeight = height;
        this.initialPopulation = initPop;
        this.entryFrequency = entryFreq;
        this.immunityEnabled = immunity;
        this.immunityPercentage = immunePct;

        this.people = new ArrayList<>();
        this.simulationTime = 0;
        this.stepsCount = 0;
        this.timeSinceLastEntry = 0;

        initializePopulation();
    }

    private void initializePopulation() {
        for (int i = 0; i < initialPopulation; i++) {
            double x = random.nextDouble() * areaWidth;
            double y = random.nextDouble() * areaHeight;
            boolean immune = immunityEnabled && random.nextDouble() * 100 < immunityPercentage;

            Person p = new Person(x, y, immune);

            if (!immune && random.nextDouble() < 0.1) {
                p.setState(new InfectedState());
            }

            people.add(p);
        }
    }

    public void step() {
        stepsCount++;
        simulationTime += STEP_DURATION;
        timeSinceLastEntry += STEP_DURATION;

        if (timeSinceLastEntry >= 1.0 / entryFrequency) {
            addNewPerson();
            timeSinceLastEntry = 0;
        }

        for (Person p : people) {
            p.update(STEP_DURATION, areaWidth, areaHeight);
        }

        for (int i = 0; i < people.size(); i++) {
            for (int j = i + 1; j < people.size(); j++) {
                people.get(i).checkInfection(people.get(j), STEP_DURATION);
                people.get(j).checkInfection(people.get(i), STEP_DURATION);
            }
        }

        people.removeIf(p -> p.isOutOfBounds(areaWidth, areaHeight));
    }

    private void addNewPerson() {
        int side = random.nextInt(4);
        double x = 0, y = 0;

        switch(side) {
            case 0: x = 0; y = random.nextDouble() * areaHeight; break;
            case 1: x = areaWidth; y = random.nextDouble() * areaHeight; break;
            case 2: x = random.nextDouble() * areaWidth; y = 0; break;
            case 3: x = random.nextDouble() * areaWidth; y = areaHeight; break;
        }

        boolean immune = immunityEnabled && random.nextDouble() * 100 < immunityPercentage;
        Person p = new Person(x, y, immune);

        if (!immune && random.nextDouble() < 0.1) {
            p.setState(new InfectedState());
        }

        people.add(p);
    }

    public SimulationMemento saveState() {
        return new SimulationMemento(people, simulationTime, stepsCount);
    }

    public void restoreState(SimulationMemento memento) {
        people.clear();
        simulationTime = memento.getSimulationTime();
        stepsCount = memento.getStepsCount();

        for (SimulationMemento.PersonData pd : memento.getPeople()) {
            Person p = new Person(pd.x, pd.y, false);

            switch(pd.stateName) {
                case "Healthy": p.setState(new HealthyState()); break;
                case "Immune": p.setState(new ImmuneState()); break;
                default: p.setState(new InfectedState()); break;
            }
            people.add(p);
        }
    }

    public List<Person> getPeople() { return people; }
    public double getSimulationTime() { return simulationTime; }

    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Healthy", 0);
        stats.put("Infected", 0);
        stats.put("Asymptomatic", 0);
        stats.put("Immune", 0);

        for (Person p : people) {
            String state = p.getState().getStateName();
            if (state.equals("Healthy")) stats.put("Healthy", stats.get("Healthy") + 1);
            else if (state.equals("Immune")) stats.put("Immune", stats.get("Immune") + 1);
            else if (state.equals("Infected-Symptomatic")) stats.put("Infected", stats.get("Infected") + 1);
            else if (state.equals("Infected-Asymptomatic")) stats.put("Asymptomatic", stats.get("Asymptomatic") + 1);
        }

        return stats;
    }
}
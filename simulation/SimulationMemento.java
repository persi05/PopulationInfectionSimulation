package simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimulationMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<PersonData> people;
    private final double simulationTime;
    private final int stepsCount;

    public static class PersonData implements Serializable {
        public double x, y;
        public double vx, vy;
        public String stateName;

        public PersonData(Person p) {
            this.x = p.getPosition().getX();
            this.y = p.getPosition().getY();
            this.vx = p.getSpeed().getX();
            this.vy = p.getSpeed().getY();
            this.stateName = p.getState().getStateName();
        }
    }

    public SimulationMemento(List<Person> people, double time, int steps) {
        this.people = new ArrayList<>();
        for (Person p : people) {
            this.people.add(new PersonData(p));
        }
        this.simulationTime = time;
        this.stepsCount = steps;
    }

    public List<PersonData> getPeople() { return people; }
    public double getSimulationTime() { return simulationTime; }
    public int getStepsCount() { return stepsCount; }
}
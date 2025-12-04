package simulation.states;

import simulation.Person;
import java.util.Random;

public class InfectedState implements PersonState {
    private static final Random random = new Random();
    private double infectionDuration;
    private double elapsedTime;
    private boolean symptomatic;

    public InfectedState() {
        this.infectionDuration = 20 + random.nextDouble() * 10;
        this.elapsedTime = 0;
        this.symptomatic = random.nextBoolean(); // 50% szans na objawy
    }

    @Override
    public void update(Person person, double deltaTime) {
        elapsedTime += deltaTime;
        if (elapsedTime >= infectionDuration) {
            person.setState(new ImmuneState());
        }
    }

    @Override
    public String getStateName() {
        return symptomatic ? "Infected-Symptomatic" : "Infected-Asymptomatic";
    }

    @Override
    public boolean canInfect() {
        return true;
    }

    @Override
    public boolean canBeInfected() {
        return false;
    }

    public boolean isSymptomatic() {
        return symptomatic;
    }

    public double getInfectionProbability() {
        return symptomatic ? 1.0 : 0.5;
    }
}
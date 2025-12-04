package simulation.states;

import simulation.Person;

public class HealthyState implements PersonState {
    @Override
    public void update(Person person, double deltaTime) {
        // zdrowy osobnik nie zmienia stanu automatycznie
    }

    @Override
    public String getStateName() {
        return "Healthy";
    }

    @Override
    public boolean canInfect() {
        return false;
    }

    @Override
    public boolean canBeInfected() {
        return true;
    }
}
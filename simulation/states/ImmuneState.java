package simulation.states;

import simulation.Person;

public class ImmuneState implements PersonState {
    @Override
    public void update(Person person, double deltaTime) {
    }

    @Override
    public String getStateName() {
        return "Immune";
    }

    @Override
    public boolean canInfect() {
        return false;
    }

    @Override
    public boolean canBeInfected() {
        return false;
    }
}
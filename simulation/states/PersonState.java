package simulation.states;

import simulation.Person;

public interface PersonState {
    void update(Person person, double deltaTime);
    String getStateName();
    boolean canInfect();
    boolean canBeInfected();
}
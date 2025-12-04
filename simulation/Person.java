package simulation;

import simulation.states.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Person {
    private static final Random random = new Random();
    private static final double MAX_SPEED = 2.5;

    private Vector2D position;
    private Vector2D speed;
    private PersonState state;

    private Map<Person, Double> contactTimes = new HashMap<>();

    public Person(double x, double y, boolean immune) {
        this.position = new Vector2D(x, y);
        this.speed = randomSpeed();
        this.state = immune ? new ImmuneState() : new HealthyState();
    }

    private Vector2D randomSpeed() {
        double angle = random.nextDouble() * 2 * Math.PI;
        double speed = random.nextDouble() * MAX_SPEED;
        return new Vector2D(Math.cos(angle) * speed, Math.sin(angle) * speed);
    }

    public void update(double deltaTime, double areaWidth, double areaHeight) {
        if (random.nextDouble() < 0.02) {
            speed = randomSpeed();
        }

        position = position.add(speed.multiply(deltaTime));
        handleBoundaries(areaWidth, areaHeight);

        state.update(this, deltaTime);
    }

    private void handleBoundaries(double width, double height) {
        if (position.getX() <= 0 || position.getX() >= width) {
            if (random.nextDouble() < 0.5) speed = new Vector2D(-speed.getX(), speed.getY());
            position.setX(Math.max(0, Math.min(position.getX(), width)));
        }
        if (position.getY() <= 0 || position.getY() >= height) {
            if (random.nextDouble() < 0.5) speed = new Vector2D(speed.getX(), -speed.getY());
            position.setY(Math.max(0, Math.min(position.getY(), height)));
        }
    }

    public void checkInfection(Person other, double deltaTime) {
        if (!state.canBeInfected() || !other.getState().canInfect()) return;

        double distance = position.distance(other.getPosition());

        if (distance <= 2.0) {
            contactTimes.put(other, contactTimes.getOrDefault(other, 0.0) + deltaTime);

            if (contactTimes.get(other) >= 3.0) {
                InfectedState infectedState = (InfectedState) other.getState();
                if (Math.random() < infectedState.getInfectionProbability()) {
                    setState(new InfectedState());
                }
                contactTimes.put(other, 0.0);
            }
        } else {
            contactTimes.put(other, 0.0);
        }
    }

    public boolean isOutOfBounds(double width, double height) {
        return position.getX() < 0 || position.getX() > width ||
                position.getY() < 0 || position.getY() > height;
    }

    public Vector2D getPosition() { return position; }
    public Vector2D getSpeed() { return speed; }
    public PersonState getState() { return state; }
    public void setState(PersonState state) { this.state = state; }
}

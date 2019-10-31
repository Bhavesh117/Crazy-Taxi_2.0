import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Passenger {

    Graphics2D g2;
    GameContainer gc;
    private List<People> passengers;

    public Passenger (GameContainer gc) {
        this.gc = gc;
        g2 = gc.getG();

        passengers = new ArrayList<>();

    }

    public void addPassengers(){
        Random rand = new Random();
        String filename;

        int type = rand.nextInt(3);
        int x = rand.nextInt((gc.getDimension().width - 100) + 1) + 50;
        int y = rand.nextInt((gc.getDimension().height - 100) + 1) + 50;
        if (type == 0)
            filename = "assets/images/passenger_one.png";
        else if (type == 2)
            filename = "assets/images/passenger_two.png";
        else
            filename = "assets/images/passenger_three.png";

        passengers.add(new People(gc, x, y, 0, 0, 0,0, filename));
    }

    public void draw() {
        for(People passenger: passengers){
            passenger.draw(gc.getG());
        }
    }

//    Increases the amount of passengers that could be spawned based on the maxPassengers variable. Determines if player has collided with passengers and drop offs and
//    performs necessary updates.
    public void update() {
        People p = null;

        if (passengers.size() < gc.getMaxPassengers()) addPassengers();

        for(People passenger: passengers){
            if(gc.getPlayer().isPassenger(passenger) && !passenger.isPassengerCollected()){
                gc.getPlayer().pickUpPassenger(passenger);
                passenger.setPassengerCollected(true);
            }

            if(gc.getPlayer().isDropOff(passenger)){
                gc.getPlayer().dropOffPassenger(passenger);
                passenger.setDroppedOff(true);
                p = passenger;
            }
        }

        passengers.remove(p);
    }

}

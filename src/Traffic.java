import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Traffic {

    private int speed = 2;

    private List<Vehicle> vehicles;
    private GameContainer gc;
    private boolean isEven = true;

    public Traffic (GameContainer gc){

        this.gc = gc;
        vehicles = new ArrayList<>();

        addVehicle();
        addVehicle();
    }

//    Adds a vehicle to the amount that are on screen and switches between adding a new horizontal and vertical vehicle. Direction of the vehicles are randomized.
    public void addVehicle(){
        if(vehicles.size() >= 20) return;

        int direction = new Random().nextInt(2);
        int type = new Random().nextInt(8);
        String filename;

        if (direction == 0) direction = -1;

        int x = 0, y = 0;
        int xSize, ySize;
        double dx, dy;

        if (type == 0){
            filename = "assets/images/Car.png";
        }
        else if (type == 1){
            filename = "assets/images/Audi.png";
        }
        else if (type == 2){
            filename = "assets/images/Ambulance.png";
        }
        else if (type == 3){
            filename = "assets/images/Police.png";
        }
        else if (type == 4){
            filename = "assets/images/Mini_truck.png";
        }
        else if (type == 5){
            filename = "assets/images/Mini_van.png";
        }
        else if (type == 6){
            filename = "assets/images/truck.png";
        }
        else{
            filename = "assets/images/Black_viper.png";
        }

        if(isEven){
            dx = speed * direction;
            dy = 0;
            isEven = false;
            ySize = new Random().nextInt((40 - 30) + 1) + 30;
            xSize = new Random().nextInt((80 - 60) + 1) + 60;
        }
        else{
            dy = speed * direction;
            dx = 0;
            isEven = true;
            xSize = new Random().nextInt((40 - 30) + 1) + 30;
            ySize = new Random().nextInt((80 - 60) + 1) + 60;
        }

//        Determine spawn location off screen based on vehicle direction.
        if (dx > 0){
            x = xSize * -1;
            y = new Random().nextInt((gc.getDimension().height - 10) + 1) + 10;
        }
        else if (dx < 0){
            x = xSize + gc.getDimension().width;
            y = new Random().nextInt((gc.getDimension().height - 10) + 1) + 10;
        }
        else if (dy > 0){
            x = new Random().nextInt((gc.getDimension().width - 10) + 1) + 10;
            y = ySize * -1;
        }
        else if (dy < 0){
            x = new Random().nextInt((gc.getDimension().width - 10) + 1) + 10;
            y = ySize + gc.getDimension().height;
        }

        vehicles.add(new Vehicle(gc, x, y, dx, dy, xSize, ySize, filename));
    }

    public  void update(){
        for (Vehicle vehicle: vehicles){
            vehicle.update();
            if (gc.getPlayer().isAccident(vehicle) && gc.isPlaying()){
                gc.getPlayer().damage();
            }

//            Setting of focus power up to all vehicles
            if(gc.getPowerUp() != null){
                if(gc.getPowerUp().isFocusActive() && !vehicle.isInFocus()){
                    vehicle.setFocus(0.25);
                }

                if (!gc.getPowerUp().isFocusActive() && vehicle.isInFocus()){
                    vehicle.setFocus(4);
                }
            }
        }
    }

    public void draw(){
        for (Vehicle vehicle: vehicles){
            vehicle.draw(gc.getG());
        }
    }

//    Increases speed for new vehicles.
    public void increaseSpeed(){
        if (speed >= 10) return;

        speed = speed + 1;
    }

    public int trafficSize(){
        return vehicles.size();
    }

    public int getSpeed() {
        return speed;
    }
}

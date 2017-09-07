package de.materna.ar.model;

public class VehicleBuilder {
    private final Vehicle vehicle;

    private VehicleBuilder() {
        vehicle = new Vehicle();
    }

    public static VehicleBuilder create() {
        return new VehicleBuilder();
    }

    public Vehicle build() {
        return vehicle;
    }


    public VehicleBuilder introShort(String intro) {
        vehicle.setIntroShort(intro);
        return this;
    }

    public VehicleBuilder name(String name) {
        vehicle.setName(name);
        return this;
    }

    public VehicleBuilder topSpeed(int topSpeed) {
        vehicle.setTopSpeed(topSpeed);
        return this;
    }

    public VehicleBuilder power(int power) {
        vehicle.setPower(power);
        return this;
    }

    public VehicleBuilder financing(int financing) {
        vehicle.setFinancing(financing);
        return this;
    }

    public VehicleBuilder acceleration(float acceleration) {
        vehicle.setAcceleration(acceleration);
        return this;
    }

    public VehicleBuilder x(String x) {
        vehicle.setX(x);
        return this;
    }
    public VehicleBuilder y(String y) {
        vehicle.setY(y);
        return this;
    }
    public VehicleBuilder z(String z) {
        vehicle.setZ(z);
        return this;
    }


}
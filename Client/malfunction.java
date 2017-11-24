package project350client;

import java.util.TimerTask;

//Generates malfunction every 10 seconds
public class malfunction extends TimerTask{
    private final Appliance appliance;
    
    public malfunction(Appliance appliance){
        this.appliance = appliance;

    }
    
    @Override
    public void run() {
                appliance.generateMalfunction();
    }
}
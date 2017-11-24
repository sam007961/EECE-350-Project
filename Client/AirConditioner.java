package project350client;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AirConditioner extends Appliance {
    public AirConditioner() {
        super();
        file = User.AirConditionerFile;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if(ClientUI.acPower)
                        out.write(("ORDER Electricity 5\n").getBytes());
                    } catch (IOException ex) {
                        System.out.println("IO Exception while sending requests");
                    }
            }
        }, 5000, 5000);//Consume electricity every 5 seconds
    }

    @Override
    protected void handleUserInput() {//Read Files and send requests
            if(file.getStatus().equals("BAD")){//Status check
                if(!User.Orders.checkItem("MaintA")){
                    try {
                        out.write(("MAINT Air_Conditioner\n").getBytes());
                        User.Orders.append("MaintA");
                    } catch (IOException ex) {
                        System.out.println("IO Exception while sending requests");
                    }
                }
            }
        try {
            Thread.sleep(1);//Do not overload with synchronized calls
        } catch (InterruptedException ex) {
            System.out.println("Client Interrupted");
        }
    }
    
}

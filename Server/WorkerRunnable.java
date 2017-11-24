package javaproject350.JavaProject350;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class WorkerRunnable implements Runnable {
    protected Socket clientSocket = null;
    protected String serverText = null;
    protected final FileHandler Bill, Prices = MultithreadedServer.Prices;
    protected String UserID;
    protected BufferedReader input;
    protected OutputStream output;
    
    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        
        try {
        input = new BufferedReader(new InputStreamReader
                                                (clientSocket.getInputStream()));
        output = clientSocket.getOutputStream();
        
            UserID = input.readLine();//Wait for User to submit ID
        } catch (IOException ex) {
            Thread.currentThread().interrupt();
        }
        if(MultithreadedServer.Bills.get(UserID) == null){//Synchronize to one FileHandler
            Bill = new FileHandler(UserID + "/Bill");
            MultithreadedServer.Bills.put(UserID, Bill);
        } else {
            Bill = MultithreadedServer.Bills.get(UserID);
        }
        System.out.println("Request processed: " + UserID);
    }
    
    
    @Override
    public void run() {
        try {
            long timeStamp;
            while(true) {
                if(input.ready()){//Check if buffer is readable
                    String text = input.readLine();
                    String[] parse = text.split(" ");
                    switch (parse[0]) {
                        case "MAINT":
                            //    0              1
                            //MAINT appliance_name
                            System.out.println("Request for maint accepted");
                            Bill.setStatus(System.currentTimeMillis());//Update Time Stamp
                            System.out.println("Billing " + parse[1]);
                            Bill.append(parse[1] + "_Maint. 1 "
                                    + Prices.getValue(parse[1]+"Maint"));;
                            break;
                        case "ORDER":
                            //    0       1       2
                            //Order    Item     Qty
                            Bill.setStatus(System.currentTimeMillis());//Update Time Stamp
                            synchronized(Bill) {
                                if(parse[1].equals("Electricity")) {
                                    if(Bill.getValue("Electricity") == 0)//No previous order (QTY = 0)
                                        Bill.append(parse[1] + " " + parse[2] + " "//Add new entry
                                        + Integer.parseInt(parse[2])
                                                *Prices.getValue(parse[1]));
                                    else {//Update previous order

                                            Bill.setQuantity("Electricity", 
                                                Bill.getValue("Electricity")//Add to current QTY
                                                        + Integer.parseInt(parse[2]));
                                            Bill.setValue("Electricity",//Recalculate Price
                                                Bill.getTPrice("Electricity")
                                                        + Integer.parseInt(parse[2])
                                                            *Prices.getValue(parse[1]));
                                    }
                                    break;
                                }
                            }
                            Bill.append(parse[1] + " " + parse[2] + " "
                                    + Integer.parseInt(parse[2])
                                            *Prices.getValue(parse[1]));
                            break;
                        case "VER"://Client requesting bill verification
                            timeStamp = Long.parseLong(parse[1]);
                            if(timeStamp != Bill.getStatus())
                                output.write(("OLD\n").getBytes());
                            else output.write(("OK\n").getBytes());
                            break;
                        case "UPDATE"://Client requesting bill update
                            output.write(("BILL\n").getBytes());
                            synchronized(Bill) {
                                BufferedReader billFile = new BufferedReader(
                                    new FileReader(Bill.getFile()));
                                for(String line = billFile.readLine(); line != null;
                                        line = billFile.readLine()) {
                                    if(line.equals("")) break;
                                    output.write((line + "\n").getBytes());
                                }
                            }
                            output.write(("END\n").getBytes());

                            break;
                        case "VERP"://Client requesting verification for payment
                            timeStamp = Long.parseLong(parse[1]);
                            if(timeStamp != Bill.getStatus())
                                output.write(("OLDP\n").getBytes());
                            else output.write(("OKP\n").getBytes());
                            break;
                        case "PAY"://Client requesting payment
                            System.out.println("payment accepted");
                            output.write(("PURCH\n").getBytes());
                            BufferedReader inFile = new BufferedReader(
                                    new FileReader(Bill.getFile()));
                            synchronized(Bill) {
                                inFile.readLine();//Skip Status Line
                                for(String line = inFile.readLine(); line != null;
                                        line = inFile.readLine()) {
                                    if(line.equals("")) break;
                                    String[] item = line.split(" ");
                                    output.write(("ADD " + item[0] + " " + item[1] 
                                            + "\n").getBytes());
                                }
                            }
                            output.write(("END\n").getBytes());
                            Bill.setStatus(System.currentTimeMillis());
                            Bill.clear();
                            output.write((Bill.getStatus() + "\n").getBytes());
                            break;
                        case "EXIT":
                            Thread.currentThread().interrupt();
                            break;
                        default:
                            break;
                    }
                }
                Thread.sleep(1);
                output.write(("TEST\n").getBytes());//Test if connection is alive
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }  
}

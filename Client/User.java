package project350client;

import java.io.IOException;
import javax.swing.JOptionPane;

public class User extends Client {
    public static String UserID;
    public static FileHandler Bill = new FileHandler(null);
    public static FileHandler Orders = new FileHandler(null);
    public static FileHandler RefrigeratorFile = new FileHandler(null);
    public static FileHandler StoveFile = new FileHandler(null);
    public static FileHandler WashingMachineFile = new FileHandler(null);
    public static FileHandler AirConditionerFile = new FileHandler(null);
    public static boolean flagPayment = false, flagUpdate = false;//pay and update bill flags
    
    @Override
    protected void handleUserInput() {
        if(flagUpdate) {
            try {
                //Verify Bill
                String timeStamp = Bill.getStatus();
                out.write(("VER " + timeStamp + "\n").getBytes());
                flagUpdate = false;
            } catch (IOException ex) {
                System.out.println("IO  Exception while verifying bill");
            }
        } else if(flagPayment) {
            try {
                //Verify Bill
                String timeStamp = Bill.getStatus();
                out.write(("VERP " + timeStamp + "\n").getBytes());
                flagPayment = false;
            } catch (IOException ex) {
                System.out.println("IO  Exception while verifying bill");
            }
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            System.out.println("User Thread Interrupted");
        }
    }
    
    @Override
    protected void init() {
        try {
            //Request ID
            UserID = (String)JOptionPane.showInputDialog(
                    null,
                    "Enter your ID:",
                    "Identification",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
            if(UserID == null) System.exit(1);//Cancel
            Bill.setFile(UserID + "/Bill");//Setup Bill File
            Orders.setFile(UserID + "/Orders");//Setup Orders File
            RefrigeratorFile.setFile(UserID + "/Refrigerator");//Setup Refrigerator File
            StoveFile.setFile(UserID + "/Stove");//Setup Stove File
            WashingMachineFile.setFile(UserID + "/Washing_Machine");//Setup Washing Machine File
            AirConditionerFile.setFile(UserID + "/Air_Conditioner");//Setup Washing Machine File
            out.write((UserID + "\n").getBytes());//Send ID to Server
            //Create Reader
            Reader = new clientReader(serverSocket) {
                @Override
                public void handleServerInput(String data) {
                    String[] parse = data.split(" ");
                    switch (parse[0]) {
                        case "OLD"://Bill out of date
                            try {
                                ClientUI.jTextArea1.setText("Updating...");
                                out.write(("UPDATE\n").getBytes());
                            } catch (IOException ex) {
                                System.out.println("IO Exception while requesting update");
                            }   
                            break;
                        case "OK"://Bill up to date
                            JOptionPane.showMessageDialog(null, "Bill Up to Date.");
                            ClientUI.jButton4.setEnabled(true);
                            ClientUI.jButton2.setEnabled(true);
                            break;
                        case "BILL"://Server Sending Bill
                            try {
                                User.Bill.clear();
                                User.Bill.setStatus(Long.parseLong(in.readLine()));//Update time stamp
                                for(String text = in.readLine(); !text.equals("END");
                                        text = in.readLine())
                                    User.Bill.append(text);
                                ClientUI.jTextArea1.setText("");
                                ClientUI.jButton4.setEnabled(true);
                                ClientUI.jButton2.setEnabled(true);
                            } catch (IOException ex) {
                                System.out.println("IO Exception while reading bill");
                            }   
                            break;
                            case "OLDP"://Payment for outdated bill
                                JOptionPane.showMessageDialog(null,
                                        "Bill is out of date! Please Update",
                                        "Bill Out of Date",
                                        JOptionPane.WARNING_MESSAGE);
                                ClientUI.jButton4.setEnabled(true);
                                ClientUI.jButton2.setEnabled(true);
                                break;
                            case "OKP"://Payment accepted
                                try {
                                    out.write(("PAY\n").getBytes());
                                    System.out.println("Requested payment");
                                } catch (IOException ex) {
                                    System.out.println("IO Exception while paying bill.");
                                }
                                break;
                            case "PURCH"://Server sending items
                                try {
                                User.Bill.clear();
                                for(String text = in.readLine(); !text.equals("END");
                                        text = in.readLine()) {
                                    String[] item = text.split(" ");
                                    switch(item[1]) {
                                        case "Refrigerator_Maint.":
                                            User.RefrigeratorFile.setStatus(true);
                                            break;
                                        case "Washing_Machine_Maint.":
                                            User.WashingMachineFile.setStatus(true);
                                            break;
                                        case "Stove_Maint.":
                                            User.StoveFile.setStatus(true);
                                            break;
                                        case "Air_Conditioner_Maint.":
                                            User.AirConditionerFile.setStatus(true);
                                            break;
                                        case "Gas_Tank":
                                            User.StoveFile.setQuantity("Gas", 100);
                                            break;
                                        default:
                                            RefrigeratorFile.setQuantity(item[1],
                                                    RefrigeratorFile.
                                                            getQuantity(item[1])
                                                            + Integer.parseInt(item[2]));
                                            WashingMachineFile.setQuantity(item[1],
                                                    WashingMachineFile.
                                                            getQuantity(item[1])
                                                            + Integer.parseInt(item[2]));
                                            StoveFile.setQuantity(item[1],
                                                    StoveFile.
                                                            getQuantity(item[1])
                                                            + Integer.parseInt(item[2]));
                                            AirConditionerFile.setQuantity(item[1],
                                                    AirConditionerFile.
                                                            getQuantity(item[1])
                                                            + Integer.parseInt(item[2]));
                                            break;    
                                    }
                                }
                                Bill.setStatus(Long.parseLong(in.readLine()));//Update Bill time stamp
                                Orders.clear();
                                ClientUI.jTextArea1.setText("");
                                ClientUI.jButton4.setEnabled(true);
                                ClientUI.jButton2.setEnabled(true);
                                ClientUI.jTextField1.setText(
                                Integer.toString(ClientUI.file.getQuantity(
                                        ClientUI.jList1.getSelectedValue())));
                                } catch (IOException ex) {
                                    System.out.println("IO Exception while reading bill");
                                } 
                                break;
                            default:
                                break;
                    }
                }
            };
            //Start Threads
            new Thread(new GUIThread())     .start();//Start GUI
            new Thread(new Refrigerator())  .start();//Start Refrigerator
            new Thread(new WashingMachine()).start();//Start Washing Machime
            new Thread(new Stove())         .start();//Start Stove
            new Thread(new AirConditioner()).start();//Start Air Conditioner
        } catch (IOException ex) {
            System.out.println("IO Exception while initializing user");
        }
    }
}

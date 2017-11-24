//Server Version of FileHandler
package javaproject350.JavaProject350;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileHandler {

    private String FileName;
    
    public FileHandler(String FileName) {
        setFile(FileName);
    }
    
    public synchronized long getStatus() {//Get Status or Time Stamp
        try {
            BufferedReader inFile;
            inFile = new BufferedReader(new FileReader(FileName));
            long Status = Long.parseLong(inFile.readLine());//Read first line
            inFile.close();
            return Status;

        } catch (IOException ex) {
            return 0;
        }
    }
    
    public synchronized int getValue(String itemName) {//Get Price in Price file,
//                                                       Get Quantity in Bill file
        try {
            BufferedReader inFile;
            inFile = new BufferedReader(new FileReader(FileName));
            inFile.readLine();//Skip Status Line
            for(String line = inFile.readLine(); line != null;
                                                    line = inFile.readLine()) {
                if(line.equals("")) break;
                String[] parse = line.split(" ");
                if (parse[0].equals(itemName)) {
                    inFile.close();
                    return Integer.parseInt(parse[1]);
                }
            }
            
            inFile.close();
            return 0;//Item not found
        } catch (IOException ex) {
            return 0;//File not found
        }
    }
    
    public synchronized int getTPrice(String itemName) {
        try {
            BufferedReader inFile;
            inFile = new BufferedReader(new FileReader(FileName));
            inFile.readLine();//Skip Status Line
            for(String line = inFile.readLine(); line != null;
                                                    line = inFile.readLine()) {
                if(line.equals("")) break;
                String[] parse = line.split(" ");
                if (parse[0].equals(itemName)) {
                    inFile.close();
                    return Integer.parseInt(parse[2]);
                }
            }
            
            inFile.close();
            return 0;//Item not found
        } catch (IOException ex) {
            return 0;//File not found
        }
    }
    
    public synchronized void setQuantity(String item, int t) {
        HashMap<String, int[]> data = getData();
        if(data.get(item) == null) return;
        int[] temp = {t, data.get(item)[1]};
        data.put(item, temp);
        writeData(data);
        
    }
    
    
    public synchronized void setValue(String item, int t) {
        HashMap<String, int[]> data = getData();
        if(data.get(item) == null) return;
        int[] temp = {data.get(item)[0], t};
        data.put(item, temp);
        writeData(data);
        
    }
    
    public synchronized String[] listItems() {
        BufferedReader inFile;
        try {
            inFile = new BufferedReader(new FileReader(FileName));
            inFile.readLine();//Skip Status Line
            List<String> items = new ArrayList<>();
            for(String line = inFile.readLine(); line != null;
                                                    line = inFile.readLine()) {
                if(line.equals("")) break;
                String[] parse = line.split(" ");
                
                items.add(parse[0]);

            }
            inFile.close();
            String[] temp = new String[items.size()];
            items.toArray(temp);
            return temp;
        } catch (FileNotFoundException ex) {
            return new String[0];
        } catch (IOException ex) {
            return new String[0];
        } 
    }

    public synchronized String getText() {
        try {
            File file = new File(FileName);
            byte[] data;
            try (FileInputStream inFile = new FileInputStream(file)) {
                data = new byte[(int) file.length()];
                inFile.read(data);
            }
            return new String(data, "UTF-8");
            
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
    
    public synchronized String getTextFormat() {
        String temp = "";
        try {
            BufferedReader inFile;
            inFile = new BufferedReader(new FileReader(FileName));
            inFile.readLine();//Skip Status Line
            for(String line = inFile.readLine(); line != null;
                    line = inFile.readLine()) {
                if(line.equals("")) break;
                String[] parse = line.split(" ");
                temp += String.format("%-20s%-5s%-5s%n",
                        parse[0],parse[1],parse[2]);
            }
            inFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
        } catch (IOException ex) {
            System.out.println("IO Exception while getting formatted text.");
        }
        return temp;
    }
    
    public synchronized void setStatus(long time) {
        try {
            BufferedReader inFile;
            inFile = new BufferedReader(new FileReader(FileName));
            inFile.readLine();//Skip Status Line
            String text = "";
            for(String line = inFile.readLine(); line != null;
                    line = inFile.readLine()) {
                if(line.equals("")) break;
                text += line + "\n";
            }
            inFile.close();
            PrintWriter outFile; 
            outFile = new PrintWriter(new File(FileName));
            outFile.println(Long.toString(time));
            outFile.print(text);
            outFile.close();
            
        } catch (FileNotFoundException ex) {
           System.out.println("File Not Found");
        } catch (IOException ex) {
            System.out.println("IO Exception while setting status.");
        }
    }
    
    private HashMap<String, int[]> getData() {
        HashMap<String, int[]> temp = new HashMap<>();
        try {
            BufferedReader inFile;
            inFile = new BufferedReader(new FileReader(FileName));
            inFile.readLine();//Skip Status Line
            for(String line = inFile.readLine(); line != null;
                    line = inFile.readLine()) {
                if(line.equals("")) break;
                String[] parse = line.split(" ");
                int[] vals = {Integer.parseInt(parse[1]), 
                    Integer.parseInt(parse[2])};
                temp.put(parse[0], vals);
            }
            inFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
        } catch (IOException ex) {
            System.out.println("IO Exception while getting data.");
        }
        
        return temp;
    }
    
    
    private void writeData(HashMap<String, int[]> data) {
        try {
            String[] itemList = listItems();
            PrintWriter outFile; 
            long Status = getStatus();
            outFile = new PrintWriter(new File(FileName));
            outFile.println(Status);
            for (String item : itemList) {
                outFile.println(item + " " + data.get(item)[0] + " " 
                        + data.get(item)[1]);
            }
            outFile.close();
        } catch (IOException ex) {
            System.out.println("IO Exception while writing data.");
        }
        
    }
    public synchronized boolean checkItem(String itemName) {
        String[] temp = listItems();
        for(String item : temp) {
            if(item.equals(itemName))
                return true;
        }
        return false;
    }
    
    public synchronized void append(String data) {
        try {
            PrintWriter outFile;
            outFile = new PrintWriter(new BufferedWriter(
                    new FileWriter(FileName, true)));
            System.out.println("Now appending to bill " + data);
            outFile.println(data);
            System.out.println("Done appending to bill " + data);
            outFile.close();
        } catch (IOException ex) {
            System.out.println("IO Exception while appending.");
        }
    }
    
    public synchronized void clear() {
        PrintWriter outFile;
        try {
            long Status = getStatus();
            outFile = new PrintWriter(new File(FileName));
            outFile.println(Long.toString(Status));
            outFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        } 
    }
    public synchronized final void setFile(String FileName) {
        this.FileName = FileName;
    } 
    
    public synchronized final String getFile() {
        return FileName;
    } 
}
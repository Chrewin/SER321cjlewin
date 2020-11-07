package Assignment3Starter;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version August 2020
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 */
public class SockServer {
  public static void main (String args[]) {
    Socket sock;
    int guesses = 0;
    int tiles = 0;
    int count = 0;
    int lives = 3;
    
    List<String> pineapple = new ArrayList<String>();
    pineapple.add("img/Pineapple-Upside-down-cake_0_0.jpg");
    pineapple.add("img/Pineapple-Upside-down-cake_0_1.jpg");
    pineapple.add("img/Pineapple-Upside-down-cake_1_0.jpg");
    pineapple.add("img/Pineapple-Upside-down-cake_1_1.jpg");
    
    List<String> funny = new ArrayList<String>();
    funny.add("img/To-Funny-For-Words1_0_0.jpg");
    funny.add("img/To-Funny-For-Words1_0_1.jpg");
    funny.add("img/To-Funny-For-Words1_1_0.jpg");
    funny.add("img/To-Funny-For-Words1_1_1.jpg");
    funny.add("img/To-Funny-For-Words1_2_0.jpg");
    funny.add("img/To-Funny-For-Words1_0_2.jpg");
    funny.add("img/To-Funny-For-Words1_2_1.jpg");
    funny.add("img/To-Funny-For-Words1_1_2.jpg");
    funny.add("img/To-Funny-For-Words1_2_2.jpg");
    
    HashMap<Integer, List<String>> questions = new HashMap<Integer, List<String>>();
    
    List<String> a = new ArrayList<String>();
    a.add("What is 5+5?");
    a.add("10");
    
    List<String> b = new ArrayList<String>();
    b.add("What is 2+4?");
    b.add("6");
    
    List<String> c = new ArrayList<String>();
    c.add("What is 5+3?");
    c.add("8");
    
    List<String> d = new ArrayList<String>();
    d.add("What is 1+5?");
    d.add("6");
    
    questions.put(0, a);
    questions.put(1, b);
    questions.put(2, c);
    questions.put(3, d);
    
    
    try {

        System.out.println("Starting while block.");
        //open socket
        ServerSocket serv = new ServerSocket(8888); // create server socket on port 8888
        System.out.println("Server ready for 1 connection");
        // only does three connections then closes
        // NOTE: SINGLE-THREADED, only one connection at a time

        System.out.println("Server waiting for a connection");

        while (true) {
            
            sock = serv.accept(); // blocking wait
            System.out.println("New client connected...");
            // setup the object reading channel
            ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
            
            // generate an output
            // get output channel
            System.out.println("Setting up output.");
            OutputStream out = sock.getOutputStream();
            // create an object output writer (Java only)
            ObjectOutputStream os = new ObjectOutputStream(out);
            System.out.println("Finished Setting up output.");
            
            // read in the number, we know it's an integer because that's the second thing sent by the client.
            Integer i = (Integer) in.readObject();
            System.out.println("Received the Integer "+ i);
            
            
            
            while (count == 0) {
                if (i == 2) {

                    System.out.println("Server returning: " + pineapple.get(count));
                    os.writeObject(pineapple.get(count));
                    break;
                } else if (i == 3) {

                    os.writeObject(funny.get(count));
                    break;
                } else {

                    os.writeObject("Invalid.");

                }
                
                i = (Integer) in.readObject();
                System.out.println("Received the Integer "+ i);
            }
            // write the whole message
            // os.writeObject(i);
            // make sure it wrote and doesn't get cached in a buffer
            // os.flush();
            System.out.println("Wrote object and flushed");
            
            do {
                
                os.writeObject(questions.get(count).get(0));
                count++;
                // read in the number, we know it's an integer because that's the second thing sent by the client.
                i = (Integer) in.readObject();
                System.out.println("Received the Integer "+ i);
                
                while (i != Integer.parseInt(questions.get(count).get(1))) {
                    
                    if (lives >= 0) {
                        
                        sock.close();
                    }
                    
                    lives--;
                    os.writeObject("Incorrect. 1 life lost. Current lives:" + lives);
                }
                // write the whole message
                os.writeObject(pineapple.get(count));
                // make sure it wrote and doesn't get cached in a buffer
                // os.flush();
                System.out.println("Wrote object and flushed");
            } while (i != 0);
        }
    } catch(Exception e) {e.printStackTrace();}
  }
}

package Assignment3Starter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.Socket;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.*;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input text box,
 * a button, and a text area for status. 
 * 
 * Methods of Interest
 * ----------------------
 * show(boolean modal) - Shows the GUI frame with the current state
 *     -> modal means that it opens the GUI and suspends background processes. Processing 
 *        still happens in the GUI. If it is desired to continue processing in the 
 *        background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x dimension size
 * insertImage(String filename, int row, int col) - Inserts an image into the grid
 * appendOutput(String message) - Appends text to the output panel
 * submitClicked() - Button handler for the submit button in the output panel
 * 
 * Notes
 * -----------
 * > Does not show when created. show() must be called to show he GUI.
 * 
 */
public class ClientGui implements Assignment3Starter.OutputPanel.EventHandlers {
  JDialog frame;
  PicturePanel picturePanel;
  OutputPanel outputPanel;
  
  // setup class variables
  boolean ready;
  Socket          sock = null;
  OutputStream    out = null;
  int port = 9099; // default port
  ObjectOutputStream os;
  ObjectInputStream in;
  int count = -1;
  int guess = 3;
  int numTiles = 0;
  
  HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
  List<Integer> a = new ArrayList<Integer>();
  List<Integer> b = new ArrayList<Integer>();
  List<Integer> z = new ArrayList<Integer>();
  List<Integer> f = new ArrayList<Integer>();
  List<Integer> g = new ArrayList<Integer>();
  List<Integer> h = new ArrayList<Integer>();
  List<Integer> i = new ArrayList<Integer>();
  List<Integer> j = new ArrayList<Integer>();
  List<Integer> k = new ArrayList<Integer>();


  /**
   * Construct dialog
   */
  public ClientGui() {
    frame = new JDialog();
    frame.setLayout(new GridBagLayout());
    frame.setMinimumSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // setup the top picture frame
    picturePanel = new PicturePanel();
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0.25;
    frame.add(picturePanel, c);

    // setup the input, button, and output area
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weighty = 0.75;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    outputPanel = new OutputPanel();
    outputPanel.addEventHandlers(this);
    frame.add(outputPanel, c);
    
    a.add(0);
    a.add(0);
    
    b.add(0);
    b.add(1);
    
    z.add(1);
    z.add(0);
    
    f.add(1);
    f.add(1);
    
    g.add(0);
    g.add(2);
    
    h.add(1);
    h.add(2);
    
    k.add(2);
    k.add(2);
    
    i.add(2);
    i.add(1);
    
    j.add(2);
    j.add(0);
    
    map.put(0, a);
    map.put(1, b);
    map.put(2, z);
    map.put(3, f);
    map.put(4, g);
    map.put(5, h);
    map.put(6, k);
    map.put(6, i);
    map.put(6, j);
  }

  /**
   * Shows the current state in the GUI
   * @param makeModal - true to make a modal window, false disables modal behavior
   */
  public void show(boolean makeModal) {
    frame.pack();
    frame.setModal(makeModal);
    frame.setVisible(true);
  }

  /**
   * Creates a new game and set the size of the grid 
   * @param dimension - the size of the grid will be dimension x dimension
   */
  public void newGame(int dimension) {
    picturePanel.newGame(dimension);
    outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");
  }

  /**
   * Insert an image into the grid at position (col, row)
   * 
   * @param filename - filename relative to the root directory
   * @param row - the row to insert into
   * @param col - the column to insert into
   * @return true if successful, false if an invalid coordinate was provided
   * @throws IOException An error occured with your image file
   */
  public boolean insertImage(String filename, int row, int col) throws IOException {
    String error = "";
    try {
      // insert the image
      if (picturePanel.insertImage(filename, row, col)) {
      // put status in output
        outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")");
        return true;
      }
      error = "File(\"" + filename + "\") not found.";
    } catch(PicturePanel.InvalidCoordinateException e) {
      // put error in output
      error = e.toString();
    }
    outputPanel.appendOutput(error);
    return false;
  }

  /**
   * Submit button handling
   * 
   * Change this to whatever you need
   */
  @Override
  public void submitClicked() {
    // Pulls the input box text
    String input = outputPanel.getInputText();
    // if has input
    if (input.length() > 0) {
      // append input to the output panel
      outputPanel.appendOutput("You have chosen: " + input);
      // clear input text box
      outputPanel.setInputText("");
      
      System.out.println("Button clicked.");
      
      try {
              
          System.out.println("Into try block");
          
          if (count == -1) {
              
              int choice = Integer.parseInt(input);
              os.writeObject(Integer.parseInt(input));
              os.flush();
              System.out.println("past flush");
              
              System.out.println("Attempting to read object.");
              String e = (String) in.readObject();
              System.out.println("Server response: " + e);

              if (e.equals("Invalid.")) {
                  
                  outputPanel.appendOutput("Please select either 2 or 3 as play options.");
                  show(true);
                  return;
              }
              
              numTiles = choice;
              count++;
              
              newGame(numTiles);
              
              insertImage(e, map.get(count).get(0), map.get(count).get(1));
              String q = (String) in.readObject();
              outputPanel.appendOutput(q);
              show(true);
              
          } else {
              
              os.writeObject(Integer.parseInt(input));
              
              String e = (String) in.readObject();
              System.out.println("Server response: " + e);
              insertImage(e, map.get(count).get(0), map.get(count).get(1));
              String q = (String) in.readObject();
              outputPanel.appendOutput(q);
              show(true);
          }
      } catch (Exception e) {
              
          System.out.println("Couldn't parse int.");
          System.out.println(e.getMessage());
          System.out.println(e.getStackTrace());
      }
    }
  }
  
  /**
   * Key listener for the input text box
   * 
   * Change the behavior to whatever you need
   */
  @Override
  public void inputUpdated(String input) {
    if (input.equals("surprise")) {
      outputPanel.appendOutput("You found me!");
    }
  }

  public static void main (String args[]) throws Exception {

      if (args.length != 2) {
          System.out.println("Expected arguments: <host(String)> <port(int)>");
          System.exit(1);
      }      
      ClientGui main = new ClientGui();

      String host = args[0];
      try {
          main.port = Integer.parseInt(args[1]);
      } catch (NumberFormatException nfe) {
          System.out.println("[Port] must be an integer");
          System.exit(2);
      }
      try {
          // open the connection
          main.sock = new Socket(host, 8888); // connect to host and socket on port 8888
          // get output channel
          OutputStream out = main.sock.getOutputStream();
          // create an object output writer (Java only)
          main.os = new ObjectOutputStream(out);
          System.out.println("Opening input channels");
          main.in = new ObjectInputStream(main.sock.getInputStream());
          // write the whole message
          // make sure it wrote and doesn't get cached in a buffer

          main.outputPanel.appendOutput("Please enter the number of tile number to play with: ");
          main.show(true);
          
          main.sock.close(); // close socked after sending
            } catch (Exception e) {e.printStackTrace();}
  }
}

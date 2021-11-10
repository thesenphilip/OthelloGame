package othello;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This program makes a socket connection 
 * time that the server sends.
 *
 */
public class OthelloNetworkController extends Thread
{
   OthelloViewController ovc;
   private Socket connection;
   private String userName;
   InputStream inStream;
   OutputStream outStream;
   PrintWriter out;
   boolean connected = false;
   String address;
   int port;
   boolean loop = true;
   Scanner input;

   OthelloNetworkController(String address, int port, String name, OthelloViewController frame){
      ovc = frame;
      connection = null;
      userName = name;
      address = address;
      port = port;
      try
      {
            connection = new Socket(address, port);
            //connection.setSoTimeout(10000); // FUCK THIS SHIT
         try
         {
            inStream = connection.getInputStream();
            input = new Scanner(inStream);
            outStream = connection.getOutputStream();
            out = new PrintWriter(outStream, true /* autoFlush */);
         }
         catch (EOFException ee) 
         { 
            System.out.println(ee); 
            ovc.rightCenter.append("\nEnd of file exception");
            return;
         }
         
      }
      catch (UnknownHostException uhe) 
      { 
         System.out.println(uhe);
         ovc.rightCenter.append("\nUnknown host excatpion");
         return;
      }
      catch (ConnectException ce)
      {
         ovc.rightCenter.append("\nerror:SERVER NOT RUNNING???(error caugh by OthelloNetworkController");  
         return; 
      }
      catch (SocketTimeoutException ste)
      {
         //rare, but if the timer on the connection runs out
         ste.printStackTrace();
         ovc.rightCenter.append("\nSocet Timeout exception");
         return;
      }
      catch (IOException io)
      {
         io.printStackTrace();
         ovc.rightCenter.append("\nIO excatpion");
         return;
      }
      connected = true;
      ovc.connect.setEnabled(false);
   }

   public boolean connected(){
      return connected;
   }

   public void toServer(String message){
      out.println(message);
   }

   public void disconnect(){
      loop = false;

      connected = false;
      ovc.disconnect.setEnabled(false);
      ovc.submitBtn.setEnabled(false);
      ovc.connect.setEnabled(true);
      ovc.rightCenter.append("\nDisconnected from server.");

      try{
         inStream.close();
         outStream.close();
         connection.close();
      }
      catch (IOException ioe)
      {
         System.out.println(ioe);
         ovc.rightCenter.append("\nIO exception");
      }
   }

   public void run(){   
      String line;
      int checks = 0;
      toServer(userName);
      while (loop)
      {     
         out.print("");

         try{
            line = input.nextLine();
         }
         catch(NoSuchElementException e){
            if (checks < 10){
               checks++;
               continue;
            }
            else{
               ovc.rightCenter.append("\nServer no longer reachable");
               disconnect();
               break;
            }      
         }
         ovc.rightCenter.append("\n" + line);
      }
   }
}


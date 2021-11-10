package othello;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This program makes a socket connection 
 * time that the server sends.
 *
 */
public class OthelloNetworkController
{

  OthelloNetworkController(String address, int port, String name){

  }



   public static void main(String[] args)
   {
      Socket s = null;
      int port = 8189;
      String host = "localhost"; //"127.0.0.1"
      try
      {
 //         s = new Socket("localhost", 8189);
            s = new Socket();
            s.connect(new InetSocketAddress(InetAddress.getByName(host),port),10000);
            s.setSoTimeout(10000);
         try
         {
            InputStream inStream = s.getInputStream();
            Scanner in = new Scanner(inStream);

            while (in.hasNextLine())
            {
               String line = in.nextLine();
               System.out.println(line);
            }
         }
         finally
         {
            s.close();
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}

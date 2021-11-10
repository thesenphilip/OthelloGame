package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
   This program implements a multithreaded server
*/
public class OthelloServer extends Thread
{  
   Vector<OthelloServerThread> clientList = new Vector<>();
   int port;

   public OthelloServer(String[] args){
      int tempPort;
      if (args.length == 0){
         System.out.println("Using defualt port: 62000");
         tempPort = 62000;
      }
      else{
         try{
            tempPort = Integer.parseInt(args[0]);
            System.out.println("Using port number: " + Integer.toString(tempPort));
         }
         catch(NumberFormatException e){
            System.out.println("Error: Invalid port number: " + args[0]);
            System.out.println("Using defualt port: 62000");
            tempPort = 62000;
         }
      }
      port = tempPort;
   }

   public static void main(String[] args )
   {  
      OthelloServer othelloServer = new OthelloServer(args);
      othelloServer.start();
      
   }

   public void run(){
      try
      {  
         int i = 1;
         ServerSocket s = new ServerSocket(port);

         while (true)
         {  
            Socket incoming = s.accept();
            System.out.println("Inbound connection #" + i);
            OthelloServerThread client = new OthelloServerThread(incoming, this);
            client.start();
            clientList.add(client);
            i++;
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }

   synchronized public void print(String message){
      System.out.println(message);
   }

   synchronized public void broadcast(String message){
      for(OthelloServerThread thread: clientList){
         thread.display(message);
      }
   }

   synchronized public void printEveryoneElse(String message, OthelloServerThread temp){
      for(OthelloServerThread thread: clientList){
         if(thread != temp){
            thread.display(message);
         }
      }
   }

   synchronized public String[] who(OthelloServerThread thread){
      String[] names = new String[clientList.size() + 1];
      
      names[0] = "Currently on the server:";

      for(int i = 0; i < clientList.size(); i++){
         String name = clientList.get(i).getUsername();
         names[i + 1] = name;
      }

      return names;
   }

   synchronized public void removeClient(OthelloServerThread client){
      String tempName = "";
      for(int i = 0; i < clientList.size(); i++){
         if (client == clientList.get(i)){
            tempName = clientList.get(i).getUsername();
            clientList.get(i).disconnect();
            clientList.remove(i);
            break;
         }
      }
   }
}

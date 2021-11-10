package server;

import java.io.*;
import java.net.*;
import java.util.*;

class OthelloServerThread extends Thread
{ 

    Socket sock;
    String userName;
    OthelloServer server;
    PrintWriter out;
    Scanner in;

    /**
      Constructs a handler.
      @param i the sock socke
    */
    public OthelloServerThread(Socket i, OthelloServer serv)
    { 
        sock = i; 
        server = serv;

        try{
            InputStream inStream = sock.getInputStream();
            OutputStream outStream = sock.getOutputStream();
            
            in = new Scanner(inStream);         
            out = new PrintWriter(outStream, true /* autoFlush */);
            try{
                userName = in.nextLine();
            }
            catch(NoSuchElementException e){
                e.printStackTrace();
                System.out.println("Error assigning userName");            
            }
            
        }
        catch (IOException e)
        {  
            e.printStackTrace();
            System.out.println(e);
        }
        server.print(userName + " has connected");
    }

    public void display(String message){
        out.println(message);
    }

    public String getUsername(){
        return userName;
    }

    public void disconnect(){        
        try{
            in.close();
            out.close();
            sock.close();
        }
        catch (IOException ioe)
        {
            System.out.println(ioe);
        }

        server.print(userName + " has disconnected");
    }

    public void run()
    {         
        display("Hello! You are connected to the server.");
        server.printEveryoneElse("SERVER: " + userName + " has joined the server", this);
        
        // echo client input
        boolean done = false;
        String line;
        int checks = 0;
        while (!done)
        {  
            out.print("");
            try{
                line = in.nextLine();
            }
            catch(NoSuchElementException e){
                if (checks < 10){
                    checks++;
                    continue;
                }
                else{
                    done = true;
                    server.removeClient(this);
                    server.broadcast("SERVER: " + userName + " has disconnected");
                    break;
                }           
            }

            if(line.length() > 1){
                if("/".equals(line.substring(0,1))){
                    if ("/bye".equalsIgnoreCase(line)){
                        done = true;
                        server.removeClient(this);
                        server.broadcast("SERVER: " + userName + " has disconnected");
                    }
                    else if("/who".equalsIgnoreCase(line)){
                        String[] names = server.who(this);
                        for(String s: names){
                            display(s);
                        }
                    }
                    else if("/help". equalsIgnoreCase(line)){
                        display("HELP:");
                        display("/help: this message.");
                        display("/bye : disconnect");
                        display("/who : Shows the names of all connected players.");
                        display("/name (name): rename yourself");
                    }
                    else if(line.length() > 6 && "/name".equalsIgnoreCase(line.substring(0,5))){
                        String tempName = userName;
                        userName = line.substring(6, line.length());
                        server.broadcast("SERVER: " + tempName + " Changed name to " + userName);
                    }
                    else{
                        server.broadcast(userName + ": " + line);
                    }
                }   
                else{
                        server.broadcast(userName + ": " + line);  
                    }        
            } 
        }
    }
}

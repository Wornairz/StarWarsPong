package starwarspong;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable     //Serben
{   
    private ServerSocket server = null;                // indirizzo server locale  
    public static int portaServer = 6789;      
    private Socket client = null;
    private BufferedReader   inDalClient; 
    private DataOutputStream outVersoClient;
    private String last, indirizzoClient;
    private Barra g1; //barra del server (questa istanza)
    private Barra g2; //barra del client
    private Palla p; //palla 
       
    @Override
    public void run() {
        //Thread
        while(true){
            try {
                //Leggiamo
                last = inDalClient.readLine();
                g2.setY(Integer.parseInt(last));
                //mandiamo
                outVersoClient.writeBytes(g1.getY()+"s"+p.getY()+"s"+p.getX() + "\n");
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
   public Server(Palla p, Barra g1, Barra g2) throws SocketException{
        System.out.println("SERVER partito in esecuzione ...");
        
        this.p=p;
        this.g1=g1;
        this.g2=g2;
        try {
            //Datagramsocket è un socket che usa UDP
            DatagramSocket socket = new DatagramSocket(portaServer, InetAddress.getByName("0.0.0.0")); 
            socket.setBroadcast(true); //abilitiamo a mandare o ricevere broadcast
            byte[] buffer = new byte[100]; //prepariamo il buffer per ricevere il broadcast dal client
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length); 
            socket.receive(packet); //blocca esecuzione
            //Pacchetto ricevuto
            System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
            System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));
            //Controlliamo se i pacchetti sono uguali
            String message = new String(packet.getData()).trim();
            //System.out.println("MESSAGGIO:"+message);
            if (message.equals(MainClass.messaggio)) {
                byte[] sendData = MainClass.conferma.getBytes();
                //Facciamo sapere il nostro indirizzo al Client
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                socket.send(sendPacket);
                indirizzoClient=packet.getAddress().getHostAddress();
            }
            socket.close();
        } 
        catch (IOException ex) {}
        try //dopo aver scoperto il Client
        {
            //Socket che fa da server e che usa il TCP
           server = new ServerSocket(portaServer);
           // rimane in attesa di un client 
           System.out.println(InetAddress.getByName("localhost"));
           client = server.accept(); //blocca esecuzione
           client.setTcpNoDelay(true);
           // chiudo il server per inibire altri client
           server.close();
           //associo due oggetti al socket del client per effettuare la scrittura e la lettura 
           inDalClient = new BufferedReader(new InputStreamReader (client.getInputStream()));
           outVersoClient = new DataOutputStream(client.getOutputStream());
           last= ((MainClass.schermo.height-(MainClass.schermo.height/100*20)))/2+"";

        }
        catch (Exception e) 
        {
           System.out.println("Errore durante l'istanza del server !");
        }
   } 

   /*public void manda(String string) {
     try
     {
        //la modifico e la rispedisco al client  
         //System.out.println(string);
        outVersoClient.writeBytes(string + "\n");
        //termina elaborazione sul server : chiudo la connessione del client 
      }  
      catch (Exception e) 
      {
          System.out.println("Eccezione " + e.getClass());
        System.out.println("Errore nel mandare la stringa");
      }
   }
   public String ricevi(){
       try{
           
            // rimango in attesa della riga trasnmessa dal client
          if (inDalClient.ready()){
              last = inDalClient.readLine();
          }
       }catch(IOException e){
            System.out.println("Errore nel ricevere la stringa");
       }
       return last;
   }*/
   public void chiudi(){
       try {
           client.close();
       } catch (IOException ex) {}
    }

}

  

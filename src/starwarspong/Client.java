package starwarspong;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Client implements Runnable{
    
  private String nomeServer ="localhost"; // indirizzo server locale  
  private Socket miosocket;         //socket per la connessione
  private DataOutputStream outVersoServer;                 // stream di output
  private BufferedReader inDalServer;                      // stream di input 
  private String last;  //variabile d'appoggio
  private Barra g1; //barra del client (questa istanza)
  private Barra g2; //barra del server
  private Palla p; //palla 
  
  @Override
  public void run(){
      //Thread
      
        while(true){
                try {
                    //mandiamo
                    outVersoServer.writeBytes(g1.getY() + "\n");
                    //riceviamo
                    last = inDalServer.readLine();
                    //splittiamo perché mandiamo le coordinate divise da una s
                    String ricevuta[]=last.split("s");
                    //settiamo in base all'ordine in cui abbiamo mandato
                    g2.setY(Integer.parseInt(ricevuta[0]));
                    p.setY(Integer.parseInt(ricevuta[1]));
                    p.setX(MainClass.schermo.width-Integer.parseInt(ricevuta[2])); //La sottrazione è per specchiare la palla
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
    /*public void manda(String string){
        try                      
        {
            //la spedisco al server 
            outVersoServer.writeBytes(string + "\n"); //potrebbe volerci \n
        } 
        catch (Exception e) 
        {

            System.out.println("Eccezione " + e.getClass());
            System.out.println("Errore nel mandare la stringa");
        }
    }
    public String ricevi(){
          try {
              //leggo la risposta dal server
              if(inDalServer.ready())
              {
                  last = inDalServer.readLine();
              }
          } catch (IOException ex) {
            System.out.println("Errore nel ricevere la stringa");
          }
          return last;
      }*/

      public Client(Palla p, Barra g1, Barra g2) throws ConnectException, UnknownHostException, SocketTimeoutException, SocketException{
            System.out.println("CLIENT partito in esecuzione ...");
            
            //Ci prendiamo la palla e le barre per poter mandare e settare i loro attributi 
            this.p=p;
            this.g1=g1;
            this.g2=g2;
            DatagramSocket c = null;
            // Alla ricerca del Server
            try {
                //Socket che usa l'UDP
                //Mandiamo un broadcast
                c = new DatagramSocket(Server.portaServer);
                c.setBroadcast(true);
                byte[] sendData = MainClass.messaggio.getBytes();
                //255.255.255.255 Indirizzo di Local Broadcast
                /*try {
                  DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), Server.portaServer);
                  c.send(sendPacket);
                  System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
                } catch (Exception e) {
                    System.out.println("Eccezione nel mandare al Local Broadcast");
                }*/
                // (Probabile ma non sicuro) Nel caso fallisse nel mandare al Local Broadcast 
                // (alcuni router potrobbero bloccarlo per ragioni di sicurezza)
                // prova a mandare un Broadcast sulle interfacce di rete disponibili
                // in realtà cerchiamo il nostro IP dalla giusta interfaccia per poi mandare un Directed Broadcast
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); //ritorna una Enumeration
                //Ci facciamo tornare tutte le interfacce di rete (Logiche e fisiche) e le scorriamo una a una
                while (interfaces.hasMoreElements()) { //finché sono rimaste interfacce da controllare
                  NetworkInterface networkInterface = interfaces.nextElement(); //passiamo alla prossima
                  if (networkInterface.isLoopback() || !networkInterface.isUp()) { //skippiamo le loopback e le interf. non in utilizzabili (o in uso)
                    //Vengono skippate la maggior parte delle interfacce logiche che sono assai
                      continue; 
                  }
                  //Arrivati qui dovrebbe rimanere solo la NIC in uso
                  //Nel caso trovi una o più interfacce disponibile si scorre l'IPv4 e IPv6 
                  for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    //ora cerchiamo il broadcast diretto
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) { //Skippa l'IPv6 che non ha un broadcast (in generale)
                      continue;
                    }
                    // Manda finalmente il broadcast (diretto)
                    try {
                        /* NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
                            InetAddress broadcast=networkInterface.getInterfaceAddresses().get(0).getBroadcast();
                        */
                      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Server.portaServer);
                      c.send(sendPacket);
                    }
                    catch (Exception e) {}
                    System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                  }
                }
                System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");
                //Aspettiamo la risposta
                
                //System.out.println();
                c.setSoTimeout(2000);
                //DatagramPacket receivePacket;
                long tempo=0;
                while(true){
                    byte[] recvBuf = new byte[100];
                    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                    tempo=System.currentTimeMillis();
                    c.receive(receivePacket); //blocca l'esecuzione
                    System.out.println(System.currentTimeMillis()-tempo);
                    //Risposta ricevuta
                    System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
                    //Controlliamo il messaggio
                    String message = new String(receivePacket.getData()).trim();
                    System.out.println("MESSAGGIO CLIENT:"+message);
                    if (message.equals(MainClass.conferma)) {
                        nomeServer = receivePacket.getAddress().getHostAddress();
                        break;
                    }
                }
                //Chiudiamo
                c.close();
                }
                catch (IOException ex) {
                    c.close();
                    if(ex.getClass().toString().equals("class java.net.SocketTimeoutException")) throw new SocketTimeoutException();
                }
            
            try 
            {
              // Creiamo il Socket  
              miosocket = new Socket(nomeServer, Server.portaServer); //creiamo un nuovo socket ad (Indirizzo, Porta)
              miosocket.setTcpNoDelay(true);
              // miosocket = new Socket(InetAddress.getByName("   "), 6789);
              // associo due oggetti al socket per effettuare la scrittura e la lettura 
              outVersoServer = new DataOutputStream(miosocket.getOutputStream());
              inDalServer    = new BufferedReader(new InputStreamReader (miosocket.getInputStream()));
              //Coordinate iniziali
              last= ((MainClass.schermo.height-(MainClass.schermo.height/100*20)))/2+"s"+MainClass.schermo.height/2+"s"+MainClass.schermo.width/2;
            }
            catch (IOException e) 
            {
                if(e.getClass().toString().equals("class java.net.ConnectException")) throw new ConnectException();
            }
        }
    public void chiudi(){
          try { 
              miosocket.close();
          } catch (IOException ex) {
          }
    }
}




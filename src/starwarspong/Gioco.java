package starwarspong;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import com.leapmotion.leap.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Gioco extends JPanel implements KeyListener, ActionListener{
    Controller controllo;
    Coll listener;
    private Barra g1; //barra sx
    private Barra g2; //barra dx
    private Palla p; //la palla
    private String nemico; //nemico è per stampare l'avversario contro cui si sta giocando
    private boolean sopra,sotto,W,S, pause=true, iniziale=true;
    private boolean IA, menù=false,enter=true,n=false,d=false,m=false,utili=false,multiplayer=false;
    private boolean trid=false,tridpalla=false;
    private int spostSpade,last,contorovescia=3;
    private static int scoreG1,scoreG2;
    private Client client=null;
    private Server server=null;
    private final int distx=15;
    private Timer timer;
    private Dimension schermo;
    private Suono AccensioneSpada;
    private java.awt.Image sfondo,logo;
    private Random rand;
    private Thread TridPalla;
    private static boolean reset=false;
    private int tempo=0;
    Gioco() throws UnsupportedAudioFileException {
        schermo=MainClass.schermo; //ci prendiamo dall mainclass la dimensione dello schermo
        addKeyListener(this); //aggiungiamo il keylistener al panel
        setPreferredSize(schermo); //facciamo il panel della dimensione dello schermo  
        g1 = new Barra(distx, schermo.height); // ( distanza dal bordo di sinistra, distanza dal bordo di sopra, percorso)
        g2 = new Barra(schermo.width-g1.getLarg()-distx, schermo.height); // come sopra
        g1.setY((schermo.height-g1.getLung())/2); //settiamo la barra di sx a metà schermo Y
        g2.setY((schermo.height-g1.getLung())/2); //come sopra ma per barra dx
        g1.setLung(schermo.height/100*20, ".\\res\\immagini\\spadablu.png"); //settiamo la lunghezza della barra a sx del 20% della lunghezza dello schermo + img
        g2.setLung(schermo.height/100*20, ".\\res\\immagini\\spadarossa.png"); //come sopra ma per barra dx + img
        sfondo= new ImageIcon(".\\res\\immagini\\sfondogame1.jpg").getImage().getScaledInstance(schermo.width, schermo.height, WIDTH); //sfondo che si estende per tutta la dimensione
        logo= new ImageIcon(".\\res\\immagini\\logo.png").getImage().getScaledInstance(schermo.width/100*70,schermo.height/100*45, WIDTH); // logo iniziale
        p = new Palla(schermo.width/2, schermo.height/2,".\\res\\immagini\\sfera.png",g1,g2); //settiamo la palla a metà schermo esatta e diamo l'img
        AccensioneSpada = new Suono(".\\res\\Suoni\\AccensioneSpadaLaser.mp3"); //suono quando parte il gioco
        //aggiungiamo tutti i suoni che potranno verificarsi a ogni colpo
        /*Colpo = new Suono(".\\res\\Suoni\\Colpo.mp3");
        Colpo1 = new Suono(".\\res\\Suoni\\Colpo1.mp3");
        Colpo2 = new Suono(".\\res\\Suoni\\Colpo2.mp3");
        Colpo3 = new Suono(".\\res\\Suoni\\Colpo3.mp3");
        Colpo4 = new Suono(".\\res\\Suoni\\Colpo4.mp3");
        Colpo5 = new Suono(".\\res\\Suoni\\Colpo5.mp3");*/
        rand=new Random();
        timer=new Timer(30,this); //ogni 20 millisecondi esegue ciò che c'è nell'actionperformed
        timer.start(); //parte il timer
        setFocusable(true);
         //ogni quante volte ha impattato la palla con le barre (a 2 aumenta la velocità)
        scoreG1=0; //la score del player sx
        scoreG2=0; // player dx
        spostSpade=10; //velocità delle barre
        TridPalla=new Thread(p);
        controllo = new Controller(); //leap
        listener=new Coll(); //leap
        controllo.addListener(listener); //leap
    }
    public class Coll extends Listener {
    @Override
    public void onConnect(Controller controller) 
    {
        System.out.println("Collegata");
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE,true);
    }
    @Override
    public void onDisconnect(Controller cntrlr){
        
        System.out.println("Non collegata");
        W=false;
        S=false;
    }
    @Override
    
      public void onFrame(Controller controller) 
      {   
          com.leapmotion.leap.Frame frame= controller.frame();
          HandList hands=frame.hands();
          GestureList gesti=frame.gestures();
          Gesture gesto= gesti.get(0);
          Hand mano= hands.get(0); 
          if(mano.isValid())
          {
              System.out.println((int)mano.stabilizedPalmPosition().getY());
              if(!utili)
              {
              last=(int)mano.stabilizedPalmPosition().getY();
              utili=true;
              }
              
              if((int)mano.stabilizedPalmPosition().getY()>5 && (int)mano.stabilizedPalmPosition().getY()<80)
              {
                  S=true;
                  W=false;
                  last=(int)mano.stabilizedPalmPosition().getY();
              }
              else
                  if((int)mano.stabilizedPalmPosition().getY()>330)
                  {
                  S=false;
                  W=true;
                  last=(int)mano.stabilizedPalmPosition().getY(); 
                  }
              else
              if((int)mano.stabilizedPalmPosition().getY()>last)
              {
                  W=true;
                  S=false;
                  last=(int)mano.stabilizedPalmPosition().getY();
              }
              else
              if((int)mano.stabilizedPalmPosition().getY()<last) 
              {
                  S=true;
                  W=false;
                  last=(int)mano.stabilizedPalmPosition().getY();
              }
              else
              {
                  W=false;
                  S=false;
              }
          }
          else
             {
                  W=false;
                  S=false;
              }
          if(!controller.frame().isValid())
          {
              W=false;
              S=false;
          }
      }
    }
    @Override
    public void actionPerformed(ActionEvent ae)
    {   
        if(reset) tempo+=timer.getDelay(); //Se si è fatto punto o si è all'inizio della partita, viene iniziato il conteggio dei secondi per il 3,2,1,GO!
        
   
        //Questo metodo viene eseguito ogni X ms dove X è il primo parametro del Timer
        if(W) g1.setY(g1.getY()-spostSpade); //se tasto premuto è la W allora va sopra di spostSpade
        if(S) g1.setY(g1.getY()+spostSpade); //come sopra ma S e va sotto
        
        if(!IA){ //se la variabile IA è falsa, quindi due giocatori, dobbiamo controllare gli input delle freccette per il secondo giocatore
            
            /*if(multiplayer){
                if(client==null){
                    g2.setY(Integer.parseInt(server.ricevi()));
                    server.manda(g1.getY()+"s"+p.getY()+"s"+p.getX());
                    
                }
                else{
                    client.manda(g1.getY()+ "");
                    String ricevuta[]=client.ricevi().split("s");
                    g2.setY(Integer.parseInt(ricevuta[0]));
                    p.setY(Integer.parseInt(ricevuta[1]));
                    p.setX(schermo.width-Integer.parseInt(ricevuta[2]));
                }          
            }*/
            //else{
                    if(sopra) g2.setY(g2.getY()-spostSpade); //come W
                    if(sotto) g2.setY(g2.getY()+spostSpade); //come S
                //}
        }
        //Altrimenti gestiamo l'IA
        else if(p.getX()>schermo.width/2){ //l'IA comincia a seguire la palla quando la palla supera la metà dello schermo. 
            if(p.getY()>g2.getY()+g2.getLung()/2-10) //punto dove la barra colpisce la palla. La barra scende
                    g2.setY(g2.getY()+(spostSpade*2)); //la barra si sposta verso sotto di spostSpade
            else if(p.getY()<g2.getY()+g2.getLung()/2-10) //punto dove la barra colpisce la palla. La barra sale
                    g2.setY(g2.getY()-(spostSpade*2)); //la barra si sposta verso sopra di spostSpade
        } 
        
        else if(p.getX()<schermo.width/2){ //Quando la palla è nella metà campo avversaria la barra cerca di tornare al punto centrale
            if(g2.getY()+g2.getLung()/2<schermo.height/2-spostSpade*2) //se il punto centrale della barra è nella prima parte di schermo (Verticale)
            g2.setY(g2.getY()+spostSpade); //scende 
            else if(g2.getY()+g2.getLung()/2>schermo.height/2+spostSpade*2) //Se è nella seconda 
            g2.setY(g2.getY()-spostSpade); //sale
        } 
        repaint(); //richiamiamo il paintcomponent
    }
    
    private void ResettaggioGame(Graphics g) throws InterruptedException{
            if(!tridpalla) 
            {
                TridPalla.start();
                tridpalla=true;
            }
            //Viene eseguito ogni volta che si starta una partita o si segna un punto
            g.setColor(Color.black); //Settiamo il colore del "pennello" a nero
            // e disegnamo un piccolo rettangolo
            g.fillRect((schermo.width-schermo.width/100*50)/2, (schermo.height-schermo.height/100*24)/2, schermo.width/100*50,schermo.height/100*24); 
            g.setColor(Color.yellow); //settiamo a giallo il colore
            //settiamo il font del "pennarello" e la dimensione
            g.setFont(new Font("starwars", Font.BOLD, (int) ((schermo.width+schermo.height)/100*6)));
            //facciamo un conto alla rovescia (3 - 2 - 1 - GO!)
            if(contorovescia>0){ //se il contatore è >0
                if(multiplayer && !trid){ //nel caso di multiplayer startiamo il thread per cominciare la comunicazione tra Server e Client in background
                    trid=true; //Lo dobbiamo fare solo una volta all'inizio della partita non ogni volta che si segna un punto quindi usiamo una variabile boolean
                    if(client==null) new Thread(server).start(); //startiamo il Server perché non abbiamo istanziato il Client
                    else new Thread(client).start(); //Startiamo il Client
                }
                //stampa il valore di contorovescia
                g.drawString("" + contorovescia, (schermo.width-schermo.width/100*50+schermo.width/100*50-g.getFontMetrics().stringWidth("3"))/2, (schermo.height-schermo.height/100*24)/2+g.getFontMetrics().getHeight());
            }
            else{ //se il contatore è ==0
                //e stampiamo GO!
                g.drawString("GO!", (schermo.width-schermo.width/100*50+schermo.width/100*50-g.getFontMetrics().stringWidth("GO!"))/2, (schermo.height-schermo.height/100*24)/2+g.getFontMetrics().getHeight());  
            }    
    }
    public static void punteggio(int scoreg1, int scoreg2)
    {
        scoreG1+=scoreg1;
        scoreG2+=scoreg2;
        reset=true;  
    }
    @Override
    public void keyTyped(KeyEvent ke){}
    @Override
    public void keyPressed(KeyEvent ke)
    {
        //Quando premiamo un tasto la barra comincia ad andare verso una direzione si ferma solo al rilascio (Key released)
        if(ke.getKeyCode()==VK_W) W=true; 
        if(ke.getKeyCode()==VK_S) S=true;  
        if(ke.getKeyCode()==VK_UP) sopra=true;
        if(ke.getKeyCode()==VK_DOWN) sotto=true;
        if(ke.getKeyCode()==VK_N){          
            if(n){ //se siamo nel menù
                //Parte un match singleplayer
                //settiamo tutto ai valori iniziali
                scoreG1=0; scoreG2=0;    
                reset=true; //fa in modo che venga eseguito il Resettaggio Game
                iniziale=false; //non deve più stampare il cielo stellato (Stampainiz)
                //enter - n-d-m false in modo tale da non dare la possibilità di premere questi tasti
                enter=false; 
                n=false;
                d=false;
                m=false;
                //Suono di Start delle spade figo
                AccensioneSpada.setMediaTimer();
                AccensioneSpada.Start();
                menù=false; // non deve più stampare il menù
                IA=true;   //attiviamo l'IA
                nemico="COMPUTER"; //per il nome dell'avversario in alto
                timer.restart(); //riprendiamo il timer
            }
        }
        if(ke.getKeyCode()==VK_D) {          
             if(d){ //se siamo nel menù
                 //Parte un match dual player
                //STESSI CONTROLLI DI N
                scoreG1=0; scoreG2=0;                
                reset=true;
                d=false;
                n=false;
                m=false;           
                iniziale=false;
                enter=false;
                AccensioneSpada.setMediaTimer();
                AccensioneSpada.Start();
                menù=false;
                IA=false;
                nemico="PLAYER 2";
                timer.restart();
             }
        }
        if(ke.getKeyCode()==VK_M) {         
             if(m){ //se siamo nel menù
                    //Parte un match multiplayer
                    try{ //Proviamo a startare un client
                        client = new Client(p, g1, g2);
                    }catch(SocketTimeoutException e){
                        System.out.println("SocketTiemout");
                        //Se si solleva l'eccezione vuol dire che non ha trovato nessun server a cui connettersi
                        //Dunque parte come Server
                        client = null; //Serve a sapere nel resto del programma se siamo un client o un server
                        try {
                            server = new Server(p, g1, g2);
                        } catch (SocketException ex) {
                            Logger.getLogger(Gioco.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    catch(UnknownHostException e){
                        System.out.println("Unknown host");
                    } catch (ConnectException ex) {
                        System.out.println("ConnectException");
                    } catch (SocketException ex) {
                     Logger.getLogger(Gioco.class.getName()).log(Level.SEVERE, null, ex);
                 }
                multiplayer=true; //per sapere se siamo in multiplayer negli altri metodi
                //Stessi controlli di N e D
                reset=true;
                d=false;
                n=false;
                m=false;
                scoreG1=0; scoreG2=0;            
                iniziale=false;
                enter=false;
                AccensioneSpada.setMediaTimer();
                AccensioneSpada.Start();
                menù=false;
                IA=false;
                nemico="PLAYER 2";
                timer.restart();
             }
        }                               
             
        
        if(ke.getKeyCode()==VK_ENTER){
            if(enter){ //Se siamo o all'inizio del gioco o alla fine di un match ci porta al menù
                enter=false;
                menù=true;
                d=true;
                n=true;
                m=true;
                timer.start();
            }
        }
        if(ke.getKeyCode()==VK_P && !multiplayer) timerControl(); //Non esiste la pausa in multiplayer
        if(ke.getKeyCode()==VK_ESCAPE){
            if(menù) System.exit(0); //Esce dal gioco solo se siamo nel menù
            //Se non siamo nel menù ci porta nel menù
            menù=true;
            d=true;
            n=true;
            m=true;
        }
         
    }
    private void timerControl()
    {
        if(pause)
        {
            p.stop();
            pause=false;
        }
        else
        {
            pause=true;
            p.resume();
            timer.restart();
        }
    }
    @Override
    public void keyReleased(KeyEvent ke)
    {
        //Ferma le barre dal movimento innescato dal KeyTyped
        if(ke.getKeyCode()==VK_W) W=false;
        if(ke.getKeyCode()==VK_S) S=false;
        if(ke.getKeyCode()==VK_UP) sopra=false;
        if(ke.getKeyCode()==VK_DOWN) sotto=false;
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //Cancella ciò che è stato disegnato in precedenza (INDISPENSABILE SE NO VEDREMMO I FOTOGRAMMI SOVRAPPOSTI)
        if(!iniziale && !menù) //Se siamo durante il gioco
             settaggiGrafici(g);
        if(reset) try { //Se dobbiamo stampare il conto alla rovescia
            ResettaggioGame(g);
            if(tempo>1000) //Se è passato un secondo
            {
                contorovescia--;//Decrementa di uno il numero del conto alla rovescia
                tempo=0; //Riazzera il tempo in modo tale da permettere il conteggio di un altro secondo
                if(contorovescia==-1) //Se si è stampato il GO!
                {
                    reset=false; //Si riavvia il movimento delle barre(vedi settaggiGrafici(g))
                    p.resume(); //Si riavvia il movimento della palla
                    contorovescia=3; //Si resetta il valore del conto alla rovescia
                }
            } 
        } catch (InterruptedException ex) {}
        else if(menù) //stampiamo il menù
            Menù(g);
        else if(iniziale) //Stampiamo l'iniziale
           try {
                Stampainiz(g);
            } catch (InterruptedException ex) {}
        if(scoreG1>=5){ //Se ha vinto a sx
           String vincitore="PLAYER 1"; 
            stampafinale(g,vincitore); //facciamo la stampa finale
        } 
        else if(scoreG2>=5){ //Se ha vinto a dx
           String vincitore=nemico;
            stampafinale(g,vincitore); //facciamo stampa finale
        }
        if (!pause)
            Pause(g);      
    }
    private void settaggiGrafici(Graphics g){ //Metodo che viene eseguito durante il gioco ogni tot ms (Guardare Timer)
            //Stampiamo lo sfondo
            g.drawImage(sfondo,0,0,this); //this è l'imageObserver attende il caricamento dell'immagine e la stampa solo quando è effettivamente pronta
            //Settiamo il font
            g.setFont(new Font("starwars", Font.PLAIN, (int) ((schermo.width+schermo.height)/100*1.8)));
            //Settiamo il colore del "pennarello"
            g.setColor(Color.YELLOW);
            //Stampiamo il punteggio per ogni player in alto
            g.drawString("PLAYER 1", 0+schermo.width/100*20, (int) (0+schermo.height/100*4.5));  // 0
            //Tramite getFontMetrics e stringWidth riusciamo a sapere la lunghezza della Stringa in pixel dunque la adattiamo allo schermo dando una % di quest'ultimo
            g.drawString(nemico, schermo.width-g.getFontMetrics().stringWidth(nemico)-schermo.width/100*20, (int) (0+schermo.height/100*4.5));
            g.drawString(scoreG1+" - "+scoreG2, (schermo.width-g.getFontMetrics().stringWidth(scoreG1+" - "+scoreG2))/2, (int) (0+schermo.height/100*4.5));
            if(reset){ //Se uno dei due ha segnato o è stato appena startato il match
                //Operazioni di reset del game
                p.reset();//Viene settata la posizione di palla e barra a quelle di default
                p.stop(); //Viene fermato il movimento della palla
                utili=false; //leap
            }
            g1.paintComponent(g); //Stampiamo barra sx
            g2.paintComponent(g); //Stampiamo barra dx
            p.paintComponent(g); //Stampiamo palla
            System.out.println("SETTAGGI GRAFICI");
    }
    private void Pause(Graphics g){ //Stampiamo il menù pausa
            //Settiamo il colore del "pennarello" a nero
            g.setColor(Color.black);
            //Settiamo il font
            g.setFont(new Font("starwars", Font.BOLD, (int) ((schermo.width+schermo.height)/100*1.4)));
            //Disegnamo un rettangolino
            g.fillRect((schermo.width-schermo.width/100*13)/2,(schermo.height-schermo.height/100*7)/2, schermo.width/100*13,schermo.height/100*7);
            //Settiamo il colore del "pennarello" a giallo
            g.setColor(Color.yellow);
            //Stampiamo la scritta pausa (Guardare settaggi grafici per sapere di più su fontmetrics)
            g.drawString("PAUSE", (schermo.width-g.getFontMetrics().stringWidth("PAUSE"))/2,(schermo.height-schermo.height/100*7)/2+g.getFontMetrics().getHeight());
            timer.stop();
    }
    private void stampafinale(Graphics g,String vincitore){ //La stampa quando uno dei due player ha vinto
            //Settiamo colore pennarello a nero
            g.setColor(Color.black);
            //Stampiamo rettangolo
            g.fillRect((schermo.width-schermo.width/100*50)/2, (schermo.height-schermo.height/100*24)/2, schermo.width/100*50,schermo.height/100*24);
            //Settiamo colore pennarello a giallo
            g.setColor(Color.yellow);
            //Settiamo font
            g.setFont(new Font("starwars", Font.BOLD, (int) ((schermo.width+schermo.height)/100*1.4)));
            //Stampiamo la stringa del vincitore passata dal paintcomponent
            g.drawString(vincitore+" wins!", (schermo.width-schermo.width/100*50+schermo.width/100*50-g.getFontMetrics().stringWidth(vincitore+" wins!"))/2, (schermo.height-schermo.height/100*24)/2+g.getFontMetrics().getHeight());
            //E se l'utente vuole tornare al menù
            g.drawString("Press ENTER to return to the menù",(schermo.width-schermo.width/100*50+schermo.width/100*50-g.getFontMetrics().stringWidth("Press ENTER to return to the menù"))/2,(schermo.height-schermo.height/100*24)/2+4*g.getFontMetrics().getHeight());
            //L'utente DEVE tornare al menù
            enter=true; //sblocchiamo il tasto enter
            trid=false; //Stoppiamo il thread per comunicazione client-server nel caso di multiplayer
            timer.stop();
    }
    private void Stampainiz(Graphics g) throws InterruptedException{
            CieloStellato(g); //metodo per fare i puntini bianchi sullo schermo
            //Stampiamo il logo di starwars
            g.drawImage(logo,schermo.width/2-schermo.width/100*35,schermo.height/100*10,this);
            //Settiamo il font
            g.setFont(new Font("starwars", Font.BOLD, (((schermo.width+schermo.height)/100)*3)));
            //Colore pennarello
            g.setColor(Color.yellow);
            //Stampiamo le scritte
            g.drawString("BATTLEPONG", schermo.width/2-(g.getFontMetrics().stringWidth("BATTLEPONG"))/2,(schermo.height/100*10)+(logo.getHeight(null)));
            //Diminuiamo un po' la dimensione delle altre due stringhe
            g.setFont(new Font("starwars", Font.BOLD, (int) ((schermo.width+schermo.height)/100*1.5)));
            //E le stampiamo
            g.drawString("Press ENTER to get into the menù",schermo.width/2-(g.getFontMetrics().stringWidth("Press ENTER to get into the menù"))/2, (schermo.height/100*10)+(logo.getHeight(null)+g.getFontMetrics().getHeight()*2*3));
            g.drawString("Press ESC to exit the game", schermo.width/2-(g.getFontMetrics().stringWidth("Press ENTER to get into the menù"))/2 , (schermo.height/100*10)+(logo.getHeight(null)+g.getFontMetrics().getHeight()*2*3+g.getFontMetrics().getHeight()));
            timer.stop();
    }                                                                                                                                                                                                                    
    private void Menù(Graphics g){
            CieloStellato(g); //Metodo per creare i puntini bianchi sullo schermo
            //Stampiamo il logo di starwars
            g.drawImage(logo, (schermo.width-schermo.width/100*30)/2, schermo.height/100*5, schermo.width/100*30, schermo.height/100*20, null);
            //Settiamo il font
            g.setFont(new Font("starwars", Font.BOLD, (int) ((schermo.width+schermo.height)/100*1.5)));
            //Settiamo il colore del pennarello
            g.setColor(Color.yellow);
            //Stampiamo le scritte
            g.drawString("BATTLEPONG",schermo.width/2-(g.getFontMetrics().stringWidth("BATTLEPONG"))/2,(schermo.height/100*5)+(schermo.height/100*20));
            //mettiamo il font a CENTER_BASELINE --> The baseline used in ideographic scripts like Chinese, Japanese, and Korean when laying out text.
            g.setFont(new Font("starwars", Font.CENTER_BASELINE, (int) ((schermo.width+schermo.height)/100*1.5)));
            //Stampiamo i bordini che conterranno le stringhe (E quello esterno grande che le comprende tutte)
            g.drawRoundRect((schermo.width-schermo.width/100*70)/2,(schermo.height/100*20+g.getFontMetrics().getHeight()*3), schermo.width/100*70, schermo.height/100*40, 60, 60); //Divido il "contenitore" in 7 parti
            g.drawRoundRect((schermo.width-g.getFontMetrics().stringWidth("Press N to single player game"))/2,(schermo.height/100*20+g.getFontMetrics().getHeight()*3)+(schermo.height/100*40)/7, g.getFontMetrics().stringWidth("Press N to single player game")+g.getFontMetrics().stringWidth("Press N to single player game")/100*5, (schermo.height/100*40)/7, 20, 20);
            g.drawRoundRect((schermo.width-g.getFontMetrics().stringWidth("Press N to single player game"))/2,(schermo.height/100*20+g.getFontMetrics().getHeight()*3)+(schermo.height/100*40)/7*3, g.getFontMetrics().stringWidth("Press N to single player game")+g.getFontMetrics().stringWidth("Press N to single player game")/100*5, (schermo.height/100*40)/7, 20, 20);
            g.drawRoundRect((schermo.width-g.getFontMetrics().stringWidth("Press N to single player game"))/2,(schermo.height/100*20+g.getFontMetrics().getHeight()*3)+(schermo.height/100*40)/7*5, g.getFontMetrics().stringWidth("Press N to single player game")+g.getFontMetrics().stringWidth("Press N to single player game")/100*5, (schermo.height/100*40)/7, 20, 20);
            //Stampiamo le stringhe
            g.drawString("Press N to single player game",(schermo.width-g.getFontMetrics().stringWidth("Press N to single player game")+g.getFontMetrics().stringWidth("Press N to single player game")/100*5)/2,((schermo.height/100*20+g.getFontMetrics().getHeight()*3)+(schermo.height/100*40)/7+g.getFontMetrics().getHeight()));
            g.drawString("Press D to dual player game",(schermo.width-g.getFontMetrics().stringWidth("Press D to dual player game")+g.getFontMetrics().stringWidth("Press D to dual player game")/100*5)/2,(schermo.height/100*20+g.getFontMetrics().getHeight()*3)+(schermo.height/100*40)/7*3+g.getFontMetrics().getHeight());
            g.drawString("Press ESC to exit the game",(schermo.width-g.getFontMetrics().stringWidth("Press ESC to exit the game")+g.getFontMetrics().stringWidth("Press ESC to exit the game")/100*5)/2,(schermo.height/100*20+g.getFontMetrics().getHeight()*3)+(schermo.height/100*40)/7*5+g.getFontMetrics().getHeight());
            timer.stop();
    }
    private void CieloStellato(Graphics g){
        int i,x,y,conteggio=0;
        //Settiamo il colore del pennarello a nero
        g.setColor(Color.black);
        //Stampiamo un Rettangolo nero grande quanto tutto lo schermo
        g.fillRect(0, 0, schermo.width, schermo.height);
        //Ora impostiamo il pennarello a bianco per fare i pallini
        g.setColor(Color.white);    
        for(i=0;i<450;i++) //Dobbiamo stampare 450 pallini
        {        
           x=rand.nextInt(schermo.width-1); //X del pallino a random che non superi la larg dello schermo
           y=rand.nextInt(schermo.height-1); //Y del pallino a random che non superi la lung dello schermo      
           if(conteggio<20) g.fillOval(x, y, 3, 3); //Per ogni 20 pallini di raggio 3 (piccoli)
           else 
            {   
                //Ne stampiamo uno più di raggio 7 (grosso)
                g.fillOval(x, y, 7, 7); 
                conteggio=0; //e resettiamo il contatore
            }                 
                conteggio++;
         }
    }
    public void riprendi_timer(){
    timer.restart();
    timerControl();
    }
    public void stop_timer(){
    timer.stop();
    timerControl();
    }

 
}

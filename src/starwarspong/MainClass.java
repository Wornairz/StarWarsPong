
package starwarspong;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class MainClass {
    private Gioco gioco; //la classe che gestisce tutto
    private static JFrame finestra; //La finestra
    private Thread cs; //thread per la colonna sonora in background
    public static Dimension schermo; // Per passare la dimensione dello schermo a tutte le classi che lo richiedono
    public static String messaggio="StarWarsPong Broadcast check!"; //Messaggio di check per il Server nel caso di Multiplayer
    public static String conferma="StarWarsPong Broadcast Acknowledgement";
    MainClass() throws UnsupportedAudioFileException {
        finestra=new JFrame(); //Creiamo la finestra
        cs= new Thread(new ColonnaSonora()); //thread per la colonna sonora in background
        cs.start();// avvia thread
        schermo=Toolkit.getDefaultToolkit().getScreenSize(); //Ci prendiamo le dimensioni dello schermo
        finestra.setSize(schermo); //Settiamo la grandezza della finestra a fullscreen
        finestra.setResizable(false); //Non è permesso il ridimensionamento (È UN GIOCO FULLSCREN!!)
        finestra.setBackground(Color.WHITE); //Lo sfondo del CONTENT PANE (implicito) della finestra
        finestra.setIconImage(new ImageIcon(".\\res\\Immagini\\Icona02.png").getImage()); //mette l'icona alla finestra
        finestra.setTitle("Star Wars: BattlePong"); //Settiamo il titolo
        finestra.setUndecorated(false); //Togliamo la barra del titolo
        gioco = new Gioco();
       /* gioco.addFocusListener(new FocusListener(){ //listener di fuoco, ovvero selezionata
            @Override
            public void focusGained(FocusEvent e) { //fuoco acquisito 
                gioco.riprendi_timer();
                gioco.repaint();
            }
            @Override
            public void focusLost(FocusEvent e) { //fuoco perso
                gioco.stop_timer();
                gioco.repaint();
            }
        });*/
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Stoppiamo l'esecuzione di tutto il programma alla chiusura 
        finestra.add(gioco); //Aggiungiamo il JPanel al content pane della finestra
        finestra.setVisible(true); // La rendiamo visibile
        
    }
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, MalformedURLException, LineUnavailableException, URISyntaxException {    
        new MainClass();
    } 
}


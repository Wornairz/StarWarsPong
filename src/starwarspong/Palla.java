package starwarspong;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Dani
 * Non è vero Danilo fa schifo
 */
import java.awt.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
public class Palla implements Runnable{

    private int x, y;
    private final int r=25;
    private Image palla;
    private final int spostPalla=10;
    private int slip=50;
    private Barra g1,g2;
    private Suono Colpo,Colpo1,Colpo2,Colpo3,Colpo4,Colpo5;
    private Random rand;
    private boolean diry=false, dirx=false;
    private int cont=0;
    private Client client=null;
    private boolean stop=false;
    
    @Override
    public void run() {
       while(true)
       {
            System.out.print(""); //Non rimuovere, DIFFONTAMENTALE importanza
         if(!stop)
         {
             System.out.println("RAN");
             ControllaCollisioni();
             try {
                 Thread.sleep(slip);
             } catch (InterruptedException ex){
                 Logger.getLogger(Palla.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
        
        
       }
 
    }
    Palla(int x, int y, String getPalla,Barra g1, Barra g2){
        this.g1=g1;
        this.g2=g2;
        this.x=x;
        this.y=y;
        rand=new Random();
        Colpo = new Suono(".\\res\\Suoni\\Colpo.mp3");
        Colpo1 = new Suono(".\\res\\Suoni\\Colpo1.mp3");
        Colpo2 = new Suono(".\\res\\Suoni\\Colpo2.mp3");
        Colpo3 = new Suono(".\\res\\Suoni\\Colpo3.mp3");
        Colpo4 = new Suono(".\\res\\Suoni\\Colpo4.mp3");
        Colpo5 = new Suono(".\\res\\Suoni\\Colpo5.mp3");
        palla=new ImageIcon(getPalla).getImage().getScaledInstance(r, r, 1); // 1 perché è bello
    }
    
    public int getR() {
        return r;
    }
    
    public void setX(int x){
        this.x=x;
    }
    public void setY(int y){
        this.y=y;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    
    public void paintComponent(Graphics g){
         g.drawImage(palla,x, y,null);        
    }
    
    private void ControllaCollisioni(){
        if(client==null){
            if(y+spostPalla>0 && !diry) //finché y palla > 0
            { // la variabile diry serve per il cambio di direzione, in modo da continuare lo spostamento verso la direzione (sopra o sotto)
                y-=spostPalla; //si sposta verso sopra
            }
            else
            {
                y+=spostPalla; // altrimenti verso sotto
                if(y<MainClass.schermo.height-r) diry=true; //fino a quando non tocca il bordo inferiore, in questo caso cambia direzione
                else diry=false; //quanto tocca il bordo inferiore settiamo la variabile diry a false
            } 
            //i controlli della X funzionano in modo analogo a quelli della Y
            if(x+spostPalla>0 && !dirx)
            {
                x-=spostPalla;
            }
            else
            {
                x+=spostPalla;
                if(x<MainClass.schermo.width) dirx=true;
                else dirx=false;
            }


            //fine controlli solo del server
            //fine controlli X
            if(x<=0){
                slip=50;
                reset();
                Gioco.punteggio(0, 1);
            }
            else if(x+r>=MainClass.schermo.width){
                slip=50;
                reset();
                Gioco.punteggio(1, 0);
            }
        }
        if(y+r>g1.getY() && y+r<(g1.getY()+g1.getLung()+(r/2))){
            if( x<g1.getX()+g1.getLarg()){
                try {              
                   SuoniSpadaLaser();                  
                } catch (LineUnavailableException ex) { }
                if(client==null){
                    cont++;
                    dirx=true;
                }
            }
        }
        if(y+r>g2.getY() && y+r<(g2.getY()+g2.getLung()+(r/2))){
            if(x+r>g2.getX()){             
                try {
                    SuoniSpadaLaser();                  
                } catch (LineUnavailableException ex) {}
                
                if(client==null){
                    cont++;
                    dirx=false;
                }
              
            }
            if(cont==2 && client==null){
                if(slip>5) slip-=5;           
                cont=0;
            }                
        }      
    }
    
    public void reset()
    {
        dirx=rand.nextBoolean();
        diry=rand.nextBoolean();
        x=MainClass.schermo.width/2;
        y=MainClass.schermo.height/2;
        g1.setY((MainClass.schermo.height-g1.getLung())/2);
        g2.setY((MainClass.schermo.height-g2.getLung())/2);
    }
    
    public synchronized void stop()
    {
        stop=true;
    }
    
    public synchronized void resume()
    {
        stop=false;
    }
    
    private void SuoniSpadaLaser() throws LineUnavailableException{
        //Ogni volta che la palla tocca una barra viene richiamato questo metodo
        //Scegliamo a caso uno dei 6 Suoni
        switch(rand.nextInt(5)){
            case 0: Colpo.setMediaTimer();
                    Colpo.Start();
                    break;
            case 1: Colpo1.setMediaTimer();
                    Colpo1.Start();
                    break;
            case 2: Colpo2.setMediaTimer();
                    Colpo2.Start();
                    break;
            case 3: Colpo3.setMediaTimer();
                    Colpo3.Start();
                   break;
            case 4: Colpo4.setMediaTimer();
                    Colpo4.Start();
                    break;
            case 5: Colpo5.setMediaTimer();
                    Colpo5.Start();
                    break;  
        }
    }
    
}

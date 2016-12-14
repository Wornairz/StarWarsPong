package starwarspong;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Dani
 */
import java.awt.*;
import java.awt.event.*;
import static java.awt.event.KeyEvent.*;
import javax.swing.*;

public class Barra{
  private int x, y, Yframe;
  Image spada;
    private int lung;
    private final int larg=20;
    public int getLung(){
        return lung;
    }
    public void setLung(int lung, String getSpada){
        this.lung=lung;
        spada=new ImageIcon(getSpada).getImage().getScaledInstance(larg, lung, 1); //1 perché è bello
    }
    public int getLarg(){
        return larg;
    }
    
    public int getY(){
        return y;
    }

    public void setY(int y) {
        //Controlliamo in modo tale da non fare uscire la barra fuori dallo schermo fermandola agli estremi
        if(y>0 && y<Yframe-getLung())
        {
           this.y=y; 
        }
        else if(y<=0)
        {
            this.y=0;
        }
        else if(y>=Yframe)
        {
            this.y=Yframe;
        }
    }
    
    public int getX(){
        return x;
    }
    
    public Barra(int x,int Yframe){
        this.Yframe=Yframe;
        this.x=x;
    }
    
    public void paintComponent(Graphics g){
        g.drawImage(spada,x, y,null);
    }
}


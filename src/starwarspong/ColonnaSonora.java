package starwarspong;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author Dani
 */
public class ColonnaSonora implements ActionListener, Runnable{
    
    private Timer timer; //
    private Suono ColonnaSonora, MarciaImperiale,Cantina,KyloRenTheme;
    int r;
    long l,t;
    @Override
    public void run() {
        r=1;
       ColonnaSonora = new Suono(".\\res\\Suoni\\ColonnaSonora.mp3");
       MarciaImperiale = new Suono(".\\res\\Suoni\\MarciaImperiale.mp3");
       Cantina = new Suono(".\\res\\Suoni\\Cantina.mp3");
       KyloRenTheme = new Suono(".\\res\\Suoni\\KyloRenTheme.mp3");
       timer=new Timer(50,this); 
       timer.start();
    
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(r){
            case 1: ColonnaSonora.Start(); 
                    if(ColonnaSonora.getMediaTimer()==ColonnaSonora.getDuration()){
                        r=2; 
                        ColonnaSonora.Stop();
                        ColonnaSonora.setMediaTimer();
                            
                    }
                   
                    break;
            case 2: MarciaImperiale.Start();
                    if(MarciaImperiale.getMediaTimer()==MarciaImperiale.getDuration()){
                        r=3;
                        MarciaImperiale.Stop();
                        MarciaImperiale.setMediaTimer();
                         }
                    break;
           case 3:  KyloRenTheme.Start();
                   if(KyloRenTheme.getMediaTimer()==KyloRenTheme.getDuration()){
                        r=4;
                        KyloRenTheme.Stop();
                        KyloRenTheme.setMediaTimer();
                         }
                    break;
           case 4:  Cantina.Start();
                   if(Cantina.getMediaTimer()==Cantina.getDuration()){
                        r=1;
                        Cantina.Stop();
                        Cantina.setMediaTimer();
                         }
                    break;
                              
        }    
    }
}

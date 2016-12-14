/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package starwarspong;

import java.io.File;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.Time;
import javax.media.format.AudioFormat;

/**
 *
 * @author torrisisimone
 */
public class Suono{
    Time time; //Time is kept at nanosecond precision.
    Player player;
    public Suono(String indirizzo){
        time= new Time(0); //Usiamo i nanosecondi
        Format mp3 = new AudioFormat(AudioFormat.MPEGLAYER3);
		Format output = new AudioFormat(AudioFormat.LINEAR);
		PlugInManager.addPlugIn(
			"com.sun.media.codec.audio.mp3.JavaDecoder",
			new Format[]{mp3},
			new Format[]{output},
			PlugInManager.CODEC
		);
		try{
                    player = Manager.createPlayer(new MediaLocator(new File(indirizzo).toURI().toURL()));
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
               player.realize();
    }
    
    public void Start() {
        //Comincia il suono
        player.start();
    }
    
    public void setMediaTimer() {
        //Setta il suono ad una determinata posizione
        player.setMediaTime(time);
    }
    
    public void Stop() {
        //Stoppa il suono
        player.stop();
    }
    
    public long getMediaTimer() {
        //Ritorna dove è arrivata la tracc(h)ia
        return (long) player.getMediaTime().getSeconds();
    }
    
    public long getDuration() {
        //Ritorna la durata della tracc(h)ia
        return (long) player.getDuration().getSeconds();
    }
    
}

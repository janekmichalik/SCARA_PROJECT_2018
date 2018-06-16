package SCARA_2018_Borzyszkowski_Michalik;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

/**
 * Klasa mająca na celu odtwarzanie dźwięków.
 */

public class audio {
    private String filename;
    private Player player; 


    public audio(String filename) {
        this.filename = filename;
    }
    
    public void close() { if (player != null) player.close(); }

    public void play() {
        try {
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }
        catch (Exception e) {
            System.out.println("Error:  " + filename);
            System.out.println(e);
        }


        new Thread() {
            public void run() {
                try { player.play(); }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();

    }

}
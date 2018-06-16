package SCARA_2018_Borzyszkowski_Michalik;

import com.sun.j3d.utils.geometry.Sphere;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.swing.JOptionPane;


/**
 * DETEKCJA KOLIZJI SCARA 2018
 * @author Bartłomiej Borzyszkowski, Michalik Jan
 */
public class CollisionDetectorFunc extends Behavior {
    public static boolean inCollision = false;
    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;
    Sphere element;
   
  /** Konstruktor klasy CollisonDetectorFunc.
   * @param obiekt Obiekt typu Sphere. Wykrywanie zderzenia, styczności na tym obiekcie.
   * @param sphere  Obiekty typu BoundingSphere. W tym obszarze będzie sprawdzana kolizja.
   */
    
  public CollisionDetectorFunc(Sphere obiekt, BoundingSphere sphere) {
    inCollision = false;
    element = obiekt;
    element.setCollisionBounds(sphere);
  }

  /** Metoda init. */
  public void initialize() {
    wEnter = new WakeupOnCollisionEntry(element);
    wExit = new WakeupOnCollisionExit(element);
    wakeupOn(wEnter);
  }
  /** Reaguje na akcje związane z kolizją.*/
  public void processStimulus(Enumeration criteria) {
    
    inCollision = !inCollision;

    if (inCollision) {
        JOptionPane.showMessageDialog(null, "KOLIZJA!");;
        wakeupOn(wExit);  
  }
    else {
        System.out.println("Brak kolizji");
        wakeupOn(wEnter); 
    }
    
}
}
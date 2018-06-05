

package robotscara;

import com.sun.j3d.utils.geometry.Box;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.swing.JOptionPane;


/**
 * Detekcja kolizji.
 * @author Borzyszkowski Bartłomiej, Michalik Jan
 */
public class CollisionDetector extends Behavior {
    public static boolean inCollision = false;
    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;
    Box element;
   
  /** Konstruktor klasy CollisonDetector.
   * @param obiekt Obiekt typu Sphere. To na nim będzie sprawdzane czy zachodzi kolizja.
   * @param box  Obiekty typu BoundingBox. Wewnątrz tego obszaru będą sprawdzane kolizje.
   */
  public CollisionDetector(Box obiekt, BoundingBox box) {
    inCollision = false;
    element = obiekt;
    element.setCollisionBounds(box);
  }

  /** Metoda inicjalizująca. */
  public void initialize() {
    wEnter = new WakeupOnCollisionEntry(element);
    wExit = new WakeupOnCollisionExit(element);
    wakeupOn(wEnter);
  }
  /** Reaguje na pojawienie się lub zniknięcie kolizji.*/
  public void processStimulus(Enumeration criteria) {
    
    inCollision = !inCollision;

    if (inCollision) {
     //   JOptionPane.showMessageDialog(null, "KOLIZJA!");
        wakeupOn(wExit);  
  }
    else {
        System.out.println("");
        wakeupOn(wEnter); 
    }
    
}
}
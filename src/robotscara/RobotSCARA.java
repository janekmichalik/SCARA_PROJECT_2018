/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package robotscara;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import javax.swing.JButton;
import javax.swing.JTextField;


/**
 * Klasa główna
 * @author Bartłomiej Borzyszkowski, Michalik Jan
 */
public class RobotSCARA extends Applet implements ActionListener, KeyListener{
    
    private JButton poczatek = new JButton("Ustawienia początkowe");
    private JTextField info = new JTextField("");
  
    
    private TransformGroup trans_ramie1, trans_przegub1, trans_ramie2, trans_chwytak, trans_podstawa, trans_klocek;
    private TransformGroup objRotate;
    private Transform3D przesuniecie_obserwatora = new Transform3D();
    private Transform3D obrot1 = new Transform3D();
    private Transform3D obrot2 = new Transform3D();
    private Transform3D przes1 = new Transform3D();
    private Transform3D przes2 = new Transform3D();
    private Transform3D przes3 = new Transform3D();
    private Transform3D poz_klocek;
    private SimpleUniverse u;
    private BranchGroup element;
   
    /** Zmienna boolowska informująca czy jest przenoszony element. */
    public boolean przenoszenie_klocka = false;
    /** Zmienna boolowska odpowiedzialna za muzykę. Jeżeli true, to dźwięki są włączone. */
    public boolean muzyka = false;
    /** Zmienna boolowska odpowiedzialna za zapis trakterii ruchu robota. Jeżeli true, to trasa jest zapamiętywana. */
    public boolean zapis = false;
    /** Tablica kolejnych ruchów robota. Z niej odtwarzana jest trakteria ruchu robota. */
    public int iloscKrokow[] = new int[1000];
    /** Zmienna informująca ile zapisana trajektoria ma kroków. */
    public int liczbaKrokow = 0; 
    /** Zmienna informująca, który krok jest odtwarzany. */
    public int nrKroku = 0;

    public float zmiana = 0;
    
    /** Zmienna opisująca aktualne położenie naszej sfery. */
    public float pozX = 0.0f, pozY = 0.0f, pozZ = 0.0f;
    /** Zmienna opisująca położenie naszej sfery przed zapisem trajektorii. */
    public float pPozX = 0.0f, pPozY = 0.0f, pPozZ = 0.0f;
    
    /** Zmienna boolowska informująca czy na początku zapisu element był przenoszony. */
    public boolean pPrzenoszenie = false;
    
    /** Zmienna informująca o tym, jaki był ostatni wykonany ruch. Jeżeli: <br>
     * 1 - obrót w lewo ramieniem nr 1, <br>
     * 2 - obrót w prawo ramieniem nr 1, <br>
     * 3 - obrót w lewo ramieniem nr 2, <br>
     * 4 - obrót w prawo ramieniem nr 2, <br>
     * 5 - obrót w lewo ramieniem nr 2, <br>
     * 6 - podniesienie chwytaka, <br>
     * 7 - opuszczenie chwytaka. <br>
     */
    public int ostatni_ruch;

    public float kat1 = 0.0f; 
    /** Zmienna opisujaca kat obrotu ramienia nr 2 względem ramienia nr 1. */
    public float kat2 = 0.0f; 
    /** Zmienna informująca na jakiej wysokości względem ramienia nr 2 jest środek ciężkości chwytaka. */
    public float wys = 0.0f;
    /** Stała określająca na jakiej wysokości znajduje się opuszczona kulka. */
    public final float wysokosc = 0.05f;
    
    /** Zmienna opisująca odpowiedni parametr ramion robota, na początku zapisywania trajektorii. */
    public float poczKat1 = 0.0f; 
    public float poczKat2 = 0.0f;
    public float poczWych = 0.0f;
    
    /**
     * Metoda do sterowania odtwarzaczem. 
     * @param zezwolenie czy jest zezwolenie na odtwarzanie dźwięku
     * @param ktore 1 - dźwięk przesuwania elementów, 2 - puszczanie, podnoszenie elementu.
     */
    
    /** Metoda służąca do obrotu ramienia nr 1 w lewo.
    * @param krok wielkość przesunięcia.
    */
    public void obrotRamie1Lewo(float krok)
    {
            kat1 += krok; 
            obrot1.rotY(kat1);  
            przes1.setTranslation(new Vector3f(0.2f,0.25f,0.0f)); 
            obrot1.mul(przes1); 
            trans_ramie1.setTransform(obrot1);
    }
    
    /** Metoda służąca do obrotu ramienia nr 1 w prawo.
    * @param krok wielkość przesunięcia, rozdzielczość.
    */
    public void obrotRamie1Prawo(float krok)
    {
            kat1 -= krok;
            obrot1.rotY(kat1);
            przes1.setTranslation(new Vector3f(0.2f,0.25f,0.0f));
            obrot1.mul(przes1);
            trans_ramie1.setTransform(obrot1);
    }
    
    
    /** Metoda służąca do obrotu ramienia nr 2 w lewo.
    * @param krok wielkość przesunięcia.
    */
    public void obrotRamie2Lewo(float krok)
    {
        if(kat2<2.355f) {
            kat2 += krok;
            obrot2.rotY(kat2);
            przes2.setTranslation(new Vector3f(0.21f,0.1f,0.0f));
            obrot2.mul(przes2);
            trans_ramie2.setTransform(obrot2);
        }
    }
    
    /** Metoda służąca do obrotu ramienia nr 2 w prawo.
    * @param krok wielkość przesunięcia.
    */
    public void obrotRamie2Prawo(float krok)
    {
        if(kat2>-2.355f){
            kat2 -= krok;
            obrot2.rotY(kat2);
            przes2.setTranslation(new Vector3f(0.21f,0.1f,0.0f));
            obrot2.mul(przes2);
            trans_ramie2.setTransform(obrot2);
        }
    }
    
    /** Metoda służąca do podniesienia chwytaka.
    * @param krok wielkość przesunięcia.
    */
    public void podniesienieChwytak(float krok)
    {
        if(wys<0.24f){
            wys += krok;
            przes3.setTranslation(new Vector3f(0.25f,-0.1f+wys,0.0f));
            trans_chwytak.setTransform(przes3);
            }
    }
    
    /** Metoda służąca do opuszczenia chwytaka.
    * @param krok wielkość przesunięcia.
    */
    void opuszczenieChwytak(float krok)
    {
        if((wys>-0.17f && przenoszenie_klocka == false) || (przenoszenie_klocka == true && wys>=-0.03f)){
            wys -= krok;
            przes3.setTranslation(new Vector3f(0.25f,-0.1f+wys,0.0f));
            trans_chwytak.setTransform(przes3);
            }
    }

    
    /**
     * Konstruktor klasy RobotSCARA. Tutaj tworzona jest kanwa, buttony, universe i obserwator.
     */
     RobotSCARA(){
          
    
       
 
        setLayout(new BorderLayout()); // ustawienie layoutu
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        
       
        Canvas3D canvas = new Canvas3D(config);
        add(BorderLayout.CENTER, canvas);
        canvas.addKeyListener(this);
         
        
        /**
         * 
         * Stworzenie przycisków w Panelu.
         */
        Panel p = new Panel();
        p.add(poczatek); // dodanie przycisku do panelu
        add("West", p); // położenie przycisku, ma być na "północy okna"
        poczatek.addActionListener(this);
        poczatek.addKeyListener(this);


        u = new SimpleUniverse(canvas);
     
        przesuniecie_obserwatora.set(new Vector3f(0.3f,0.2f,2.6f));

        u.getViewingPlatform().getViewPlatformTransform().setTransform(przesuniecie_obserwatora);
        BranchGroup scene = utworzScene(u);
        u.addBranchGraph(scene);
        
    }
    
     
     /**
      * Metoda w której tworzona jest Scena. W niej kreowany jest cały świat.
      * @param su universum, do którego zostanie dodadana scena.
      * @return Zwraca obiekt typu BranchGroup.
      */
     public BranchGroup utworzScene(SimpleUniverse su){
         
        BranchGroup Scena = new BranchGroup();
        TransformGroup vpTrans = null;
        vpTrans = su.getViewingPlatform().getViewPlatformTransform();
        
        /**
         * Ustawienie materiału dla wszystkich elementów.
         */
        Material mat = new Material();
        mat.setAmbientColor(new Color3f(1.0f, 0.45882353f, 0.10196078f));

        Material mat_klocek = new Material();
        mat_klocek.setAmbientColor(new Color3f(0.0f, 0.6f, 0.8f));
        
        /**
         * Ustawienie wyglądu dla wszystkich elementów zawartych w Scenie.
         */
        Appearance wyglad = new Appearance();
        wyglad.setMaterial(mat);
        
        Appearance wyglad_klocek = new Appearance();
        wyglad_klocek.setMaterial(mat_klocek);
        
        Background background = new Background(new Color3f(0.90196078f, 0.90196078f, 0.90196078f));
        BoundingSphere sphere = new BoundingSphere(new Point3d(0,0,0), 100000);
        background.setApplicationBounds(sphere);
        Scena.addChild(background);
        /**
         * Stworzenie podstawy, na której znajduje się robot typu SCARA.
         */
        Cylinder podstawa = new Cylinder(0.25f,0.05f, wyglad);
        trans_podstawa = new TransformGroup();
        trans_podstawa.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_podstawa.addChild(podstawa);
         
        //WALEC
        Cylinder walec = new Cylinder(0.1f, 0.4f, wyglad);
        Transform3D poz_walec = new Transform3D();
        poz_walec.set(new Vector3f(0.0f, 0.2f, 0));
        TransformGroup przes_walec = new TransformGroup(poz_walec);
        przes_walec.addChild(walec);
        trans_podstawa.addChild(przes_walec);
         
        /**
         * Stworzenie ramienia nr 1 robota, które składa się z:
         * jednego elementu typu Box i dwóch elementów typu Cylinder.
         * Element ten jest rodzicem przegubu nr 1.
         */
        Box ramie1 = new Box(0.20f, 0.05f, 0.1f, wyglad);
        Cylinder walecram11 = new Cylinder(0.1f, 0.1f, wyglad);
        Cylinder walecram12 = new Cylinder(0.1f, 0.1f, wyglad);
         
        Transform3D  poz_ramie1   = new Transform3D();
        poz_ramie1.set(new Vector3f(0.2f,0.25f,0.0f));
         
        Transform3D poz_walecram11 = new Transform3D();
        poz_walecram11.set(new Vector3f(-0.2f, 0.0f, 0));
        TransformGroup trans_walecram11 = new TransformGroup(poz_walecram11);
        trans_walecram11.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_walecram11.addChild(walecram11);
         
        Transform3D poz_walecram12 = new Transform3D();
        poz_walecram12.set(new Vector3f(0.2f, 0.0f, 0));
        TransformGroup trans_walecram12 = new TransformGroup(poz_walecram12);
        trans_walecram12.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_walecram12.addChild(walecram12);
         
        trans_ramie1 = new TransformGroup(poz_ramie1);
        trans_ramie1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_ramie1.addChild(ramie1);
        trans_ramie1.addChild(trans_walecram11);
        trans_ramie1.addChild(trans_walecram12);
         
        /**
         * Stworzenie przegubu nr 1 robota.
         * Jest on rodzicecem ramienia nr 2.
         */
        Cylinder przegub1 = new Cylinder(0.05f, 0.01f, wyglad);
        Transform3D  poz_przegub1   = new Transform3D();   
        poz_przegub1.set(new Vector3f(0.21f,0.0f,0.0f));
        trans_przegub1 = new TransformGroup(poz_przegub1);
        trans_przegub1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_przegub1.addChild(przegub1);
         
         /**
         * Stworzenie ramienia nr 2 robota, które składa się z:
         * jednego elementu typu Box i dwóch elementów typu Cylinder.
         * Element ten jest rodzicem przegubu chwytaka.
         */
        Box ramie2 = new Box(0.2f, 0.05f, 0.1f, wyglad);
        Cylinder walecram21 = new Cylinder(0.1f, 0.1f, wyglad);
        Cylinder walecram22 = new Cylinder(0.1f, 0.1f, wyglad);
         
        Transform3D  poz_ramie2 = new Transform3D();
        poz_ramie2.set(new Vector3f(0.21f,0.1f,0.0f));
        
        Transform3D poz_walecram21 = new Transform3D();
        poz_walecram21.set(new Vector3f(-0.2f, 0.0f, 0));
        
        Transform3D poz_walecram22 = new Transform3D();
        poz_walecram22.set(new Vector3f(0.2f, 0.0f, 0));
        
        TransformGroup trans_walecram21 = new TransformGroup(poz_walecram21);
        trans_walecram21.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_walecram21.addChild(walecram21);
        
        TransformGroup trans_walecram22 = new TransformGroup(poz_walecram22);
        trans_walecram22.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_walecram22.addChild(walecram22);
         
        trans_ramie2 = new TransformGroup(poz_ramie2);
        trans_ramie2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_ramie2.addChild(ramie2);
        trans_ramie2.addChild(trans_walecram21);
        trans_ramie2.addChild(trans_walecram22);

        /**
        * Stworzenie chwytaka, odpowiedzialnego za podnoszenie elementów.
        */
        Cylinder chwytak = new Cylinder(0.02f, 0.6f, wyglad);
        Transform3D  poz_chwytak = new Transform3D();
        poz_chwytak.set(new Vector3f(0.25f,-0.1f,0.0f));
        trans_chwytak = new TransformGroup(poz_chwytak);
        trans_chwytak.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        trans_chwytak.addChild(chwytak);
        
        //USTAWIENIE DZIECI
        trans_ramie1.addChild(trans_przegub1);
        trans_przegub1.addChild(trans_ramie2);
        trans_ramie2.addChild(trans_chwytak);
        przes_walec.addChild(trans_ramie1);
       
        //KLOCEK
        pozX = 0.86f;  pozY = 0.065f;
        Box klocek = new Box(0.08f, 0.08f ,0.08f, wyglad_klocek);
        poz_klocek = new Transform3D();
        poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
        trans_klocek = new TransformGroup(poz_klocek);
        trans_klocek.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        trans_klocek.addChild(klocek);
         
        
        //ŚWIATŁO KIERUNKOWE
        BoundingSphere bounds = new BoundingSphere (new Point3d(0.0,0.0,0.0),100.0);
        Color3f light1Color = new Color3f(1.0f, 0.63921569f, 0.30196078f);
        Vector3f light1Direction = new Vector3f(4.0f,-7.0f,-12.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        Scena.addChild(light1);
    
    
        //ŚWIATŁO PUNKTOWE
        Color3f ambientColor = new Color3f(1.0f,1.0f,1.0f);
        AmbientLight ambientLightNode= new AmbientLight(ambientColor);
        ambientLightNode.setInfluencingBounds(bounds);
        Scena.addChild(ambientLightNode);
       

        
    
    
        //MYSZKA - obsługa
        objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        Scena.addChild(objRotate);
        objRotate.addChild(trans_podstawa);
    
        element = new BranchGroup();
        element.setCapability(element.ALLOW_DETACH);
        element.setCapability(element.ALLOW_CHILDREN_WRITE);
        element.setCapability(element.ALLOW_CHILDREN_READ);
        element.addChild(trans_klocek);

        objRotate.addChild(element);

         
        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(objRotate);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        Scena.addChild(myMouseRotate);

        MouseWheelZoom myMouseZoom = new MouseWheelZoom();
        myMouseZoom.setTransformGroup(vpTrans);
        myMouseZoom.setSchedulingBounds(new BoundingSphere());
        Scena.addChild(myMouseZoom);
   
        //WŁĄCZENIE KOLIZJI

        
     //   CollisionDetector detect = new CollisionDetector(klocek, new BoundingBox());
       // detect.setSchedulingBounds(bounds);
      //  Scena.addChild(detect);
        Scena.compile();
        return Scena;

    }
 
   // @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == poczatek){ 
            kat1 = 0.0f; 
            kat2 = 0.0f; 
            wys = 0.0f;
            
            //USTAWIENIE WSZYSTKICH SKŁADOWYCH ROBOTA W POZYCJI STARTOWEJ
            obrot1.rotY(kat1);  
            przes1.setTranslation(new Vector3f(0.2f,0.25f,0.0f)); 
            obrot1.mul(przes1); 
            trans_ramie1.setTransform(obrot1);
            obrot2.rotY(kat2);  
            przes2.setTranslation(new Vector3f(0.21f,0.1f,0.0f)); 
            obrot2.mul(przes2); 
            trans_ramie2.setTransform(obrot2);
            przes3.setTranslation(new Vector3f(0.25f,-0.1f,0.0f)); 
            trans_chwytak.setTransform(przes3);
            
            //WYZEROWANIE USTAWIEŃ KAMERY
            objRotate.setTransform( new Transform3D());
            
            
            //jeżeli klocek był trzymany, to następuje opuszczenie go
            if(przenoszenie_klocka == true){
                trans_chwytak.removeChild(element);
                objRotate.addChild(element);
                System.err.println("jestem tutaj");
                przenoszenie_klocka = false;  
            }
            
            //ustawienie klocka w pozycji startowej
            poz_klocek = new Transform3D();
            pozX = 0.86f; pozY = 0.07f; pozZ = 0.0f;
            poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
            trans_klocek.setTransform(poz_klocek);
                
                System.out.println(pozX +" "+ pozY+"" +pozZ);

        }
       
        
    }
   @Override
            public void keyPressed(KeyEvent e){
               
                switch(e.getKeyCode()){
                    
                    case KeyEvent.VK_LEFT:
                        if(((CollisionDetector.inCollision && ostatni_ruch != 1) || przenoszenie_klocka == true || CollisionDetector.inCollision == false))
                        {
                            
                        if(!CollisionDetector.inCollision) ostatni_ruch = 1;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 1;
                            liczbaKrokow++;
                        }
                        obrotRamie1Lewo(0.01f);

                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(((CollisionDetector.inCollision && ostatni_ruch != 2) || przenoszenie_klocka == true || CollisionDetector.inCollision == false))
                        {
                        if(!CollisionDetector.inCollision) ostatni_ruch = 2;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 2;
                            
                            liczbaKrokow++;
                        }
                        obrotRamie1Prawo(0.01f);
                        }
                        break;
                    case KeyEvent.VK_A:
                        if(((CollisionDetector.inCollision && ostatni_ruch != 3) || przenoszenie_klocka == true || CollisionDetector.inCollision == false))
                        {
                        if(!CollisionDetector.inCollision) ostatni_ruch = 3;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 3;
                            liczbaKrokow++;
                        }
                        obrotRamie2Lewo(0.01f);
                        }
                        break;
                    case KeyEvent.VK_D:
                        if(((CollisionDetector.inCollision && ostatni_ruch != 4) || przenoszenie_klocka == true || CollisionDetector.inCollision == false))
                        {
                        if(!CollisionDetector.inCollision) ostatni_ruch = 4;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 4;
                            liczbaKrokow++;
                        }
                        obrotRamie2Prawo(0.01f); 
                        }
                        break;
                    case KeyEvent.VK_UP:
                        ostatni_ruch = 5;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 5;
                            liczbaKrokow++;
                        }
                        podniesienieChwytak(0.01f);
                        break;
                    case KeyEvent.VK_DOWN:
                        if(((CollisionDetector.inCollision && ostatni_ruch != 6) || przenoszenie_klocka == true || CollisionDetector.inCollision == false))
                        {
                            System.out.println("blabla");
                        if(!CollisionDetector.inCollision) ostatni_ruch = 6;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 6;
                            liczbaKrokow++;
                        }
                        opuszczenieChwytak(0.01f);
                        }
                        break;    
                    case KeyEvent.VK_SPACE: 
                         System.err.println(wys);
                          if(CollisionDetector.inCollision == true && wys > -0.05f && wys < -0.01f){
                              przenoszenie_klocka = !przenoszenie_klocka;
                            if(przenoszenie_klocka == true){
                                if(zapis == true)
                                {
                                    iloscKrokow[liczbaKrokow] = 7;
                                    liczbaKrokow++;
                                }
                                System.err.println("przenoszenie");
                                objRotate.removeChild(element);
                                Transform3D temp = new Transform3D();
                                poz_klocek.set(new Vector3f(0.0f, -0.36f, 0.0f));
                                temp.mul(poz_klocek);
                                trans_klocek.setTransform(temp);
                                trans_chwytak.addChild(element);
                                
                                
                            }
                            else {
                                
                                
                                Transform3D temp = new Transform3D();
                                 if(zapis == true)
                                {
                                    iloscKrokow[liczbaKrokow] = 8;
                                    liczbaKrokow++;
                                }
                               System.err.println("puszczamoe");
                                trans_chwytak.removeChild(element);
                                pozX = (float) (0.425f*Math.cos(kat1)+0.425f*Math.cos(kat1+kat2));
                                pozY = (float) wysokosc;
                                pozZ = (float) (-(0.435f*Math.sin(kat1)+0.435f*Math.sin(kat1+kat2)));
                                System.out.println(pozX +" "+ pozY+"" +pozZ);
                                poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
                                temp.mul(poz_klocek);
                                trans_klocek.setTransform(temp);
                                objRotate.addChild(element);
                                
                                 
                            }
                          }
                          else if(CollisionDetector.inCollision == false && przenoszenie_klocka == true && wys > -0.04f && wys < -0.01f)
                          {
                              
                                if(zapis == true)
                                {
                                    iloscKrokow[liczbaKrokow] = 7;
                                    liczbaKrokow++;
                                }
                                trans_chwytak.removeChild(element);
                                pozX = (float) (0.425f*Math.cos(kat1)+0.425f*Math.cos(kat1+kat2));
                                pozY = (float) wysokosc;
                                pozZ = (float) (-(0.435f*Math.sin(kat1)+0.435f*Math.sin(kat1+kat2)));
                                System.out.println(pozX +" "+ pozY+"" +pozZ);
                                poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
                                Transform3D temp = new Transform3D();
                                temp.mul(poz_klocek);
                                trans_klocek.setTransform(temp);
                                objRotate.addChild(element);
                                
                                
                            przenoszenie_klocka =!przenoszenie_klocka;
                          }
                          break;
               }
            }

            public void keyReleased(KeyEvent e){
              
            }

            public void keyTyped(KeyEvent e){
         
            }
   
    /**
     * Główna metoda klasy. W niej tworzony jest robot i dodawany jest KeyListener;
     * @param args 
     */         
    public static void main(String[] args) {
     
        RobotSCARA okno = new RobotSCARA();
        okno.addKeyListener(okno);
        MainFrame mf = new MainFrame(okno, 900, 600); 
        
        // TODO code application logic here
    }
}


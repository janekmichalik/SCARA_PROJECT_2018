package SCARA_2018_Borzyszkowski_Michalik;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;


/**
 * Klasa główna SCARA 2018
 * @author Bartłomiej Borzyszkowski, Michalik Jan
 */
public class SCARA_2018 extends Applet implements ActionListener, KeyListener{
    
    
    private JButton reset = new JButton("RESET");
    private JButton nauka_start = new JButton("NAUKA - START");
    private JButton nauka_koniec = new JButton("NAUKA - KONIEC");
    private JButton zademonstruj = new JButton("ZADEMONSTRUJ");
    private JButton audio = new JButton("AUDIO");
    
    private TransformGroup t_ramie_1, t_przegub_1, t_ramie_2, t_chwytak, t_podstawa, t_klocek, t_chwytak_lewo, t_chwytak_prawo;
    private TransformGroup objRotate;
    private Transform3D przesuniecie_obserwatora = new Transform3D();
    private Transform3D obrot1 = new Transform3D();
    private Transform3D obrot3 = new Transform3D();
    private Transform3D obrot2 = new Transform3D();
    private Transform3D przes1 = new Transform3D();
    private Transform3D przes2 = new Transform3D();
    private Transform3D przes3 = new Transform3D();
    private Transform3D przes4 = new Transform3D();
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
    
    private audio thePlayer;
    /** Zmienna opisujaca kat obrotu ramienia nr 1 względem podstawy robota. */
    public float kat1 = 0.0f; 
    /** Zmienna opisujaca kat obrotu ramienia nr 2 względem ramienia nr 1. */
    public float kat2 = 0.0f; 
    /** Zmienna opisujaca kat obrotu chwytaka. */
    public float kat3 = 0.0f; 
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
    public void audio_play(boolean zezwolenie, int ktore)
    {
        if(zezwolenie == true){
            if(ktore == 1) thePlayer = new audio("audio/robot.mp3");
            else if (ktore == 2) thePlayer = new audio("audio/dock.mp3");
            thePlayer.play();
            }
    }
    
    /** Metoda służąca do obrotu ramienia nr 1 w lewo.
    * @param krok wielkość przesunięcia.
    */
    public void obrotRamie1Lewo(float krok)
    {
            kat1 += krok; 
            obrot1.rotY(kat1);  
            przes1.setTranslation(new Vector3f(0.2f,0.25f,0.0f)); 
            obrot1.mul(przes1); 
            t_ramie_1.setTransform(obrot1);
    }
    
    public void obrotChwytakLewo()
    {
            obrot3.rotX(Math.PI/25);   
            t_chwytak_lewo.setTransform(obrot3);
    }
    
    public void obrotChwytakPrawo()
    {
            obrot3.rotX(-Math.PI/25); 
            t_chwytak_prawo.setTransform(obrot3);
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
            t_ramie_1.setTransform(obrot1);
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
            t_ramie_2.setTransform(obrot2);
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
            t_ramie_2.setTransform(obrot2);
        }
    }
    
    /** Metoda służąca do podniesienia chwytaka.
    * @param krok wielkość przesunięcia.
    */
    public void podniesienie_chwytaka(float krok)
    {
        if(wys<0.24f){
            wys += krok;
            przes3.setTranslation(new Vector3f(0.25f,-0.1f+wys,0.0f));
            t_chwytak.setTransform(przes3);
            }
    }
    
    /** Metoda służąca do opuszczenia chwytaka.
    * @param krok wielkość przesunięcia.
    */
    void opuszczenie_chwytaka(float krok)
    {
        if((wys>-0.17f && przenoszenie_klocka == false) || (przenoszenie_klocka == true && wys>=-0.03f)){
            wys -= krok;
            przes3.setTranslation(new Vector3f(0.25f,-0.1f+wys,0.0f));
            t_chwytak.setTransform(przes3);
            }
    }

    
    /**
     * Konstruktor klasy RobotSCARA. Tutaj tworzona jest kanwa, buttony, universe i obserwator.
     */
     SCARA_2018(){
          
    
       
 
        setLayout(new BorderLayout()); // ustawienie layoutu
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        
       
        Canvas3D canvas = new Canvas3D(config);
        add(BorderLayout.CENTER, canvas);
        canvas.addKeyListener(this);
         
        
        /**
         * 
         * Stworzenie przycisków w Panelu.
         */
        JPanel p = new JPanel();
        p.add(reset); // dodanie przycisku do panelu
        add("North", p); // położenie przycisku, ma być na "północy okna"
        reset.addActionListener(this);
        reset.addKeyListener(this);
        
        p.add(audio);
        add("North",p);
        audio.addActionListener(this);
        audio.addKeyListener(this);
       
        
        p.add(nauka_start);
        add("North",p);
        nauka_start.addActionListener(this);
        nauka_start.addKeyListener(this);
        
        p.add(nauka_koniec);
        add("North",p);
        nauka_koniec.addActionListener(this);
        nauka_koniec.addKeyListener(this);
        
        p.add(zademonstruj);
        add("North",p);
        zademonstruj.addActionListener(this);
        zademonstruj.addKeyListener(this);
        

        u = new SimpleUniverse(canvas);
     
        przesuniecie_obserwatora.set(new Vector3f(0.0f,0.2f,3.0f));

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
        t_podstawa = new TransformGroup();
        t_podstawa.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_podstawa.addChild(podstawa);
         
         
        //WALEC
        Cylinder walec = new Cylinder(0.1f, 0.4f, wyglad);
        Transform3D poz_walec = new Transform3D();
        poz_walec.set(new Vector3f(0.0f, 0.2f, 0));
        TransformGroup przes_walec = new TransformGroup(poz_walec);
        przes_walec.addChild(walec);
        t_podstawa.addChild(przes_walec);
         
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
         
        t_ramie_1 = new TransformGroup(poz_ramie1);
        t_ramie_1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_ramie_1.addChild(ramie1);
        t_ramie_1.addChild(trans_walecram11);
        t_ramie_1.addChild(trans_walecram12);
         
        /**
         * Stworzenie przegubu nr 1 robota.
         * Jest on rodzicecem ramienia nr 2.
         */
        Cylinder przegub1 = new Cylinder(0.05f, 0.01f, wyglad);
        Transform3D  poz_przegub1   = new Transform3D();   
        poz_przegub1.set(new Vector3f(0.21f,0.0f,0.0f));
        t_przegub_1 = new TransformGroup(poz_przegub1);
        t_przegub_1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_przegub_1.addChild(przegub1);
         
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
         
        t_ramie_2 = new TransformGroup(poz_ramie2);
        t_ramie_2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_ramie_2.addChild(ramie2);
        t_ramie_2.addChild(trans_walecram21);
        t_ramie_2.addChild(trans_walecram22);

        /**
        * Stworzenie chwytaka, odpowiedzialnego za podnoszenie elementów.
        */
        Cylinder chwytak = new Cylinder(0.02f, 0.6f, wyglad);
        Transform3D  poz_chwytak = new Transform3D();
        poz_chwytak.set(new Vector3f(0.25f,-0.1f,0.0f));
        t_chwytak = new TransformGroup(poz_chwytak);
        t_chwytak.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_chwytak.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_chwytak.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        t_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        t_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        t_chwytak.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        t_chwytak.addChild(chwytak);
       
        
        //USTAWIENIE DZIECI
        t_ramie_1.addChild(t_przegub_1);
        t_przegub_1.addChild(t_ramie_2);
        t_ramie_2.addChild(t_chwytak);
        przes_walec.addChild(t_ramie_1);
       
        //KLOCEK
        pozX = 0.86f;  pozY = 0.065f;
        Sphere klocek = new Sphere(0.07f, Sphere.GENERATE_NORMALS,80, wyglad_klocek);
        poz_klocek = new Transform3D();
        poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
        t_klocek = new TransformGroup(poz_klocek);
        t_klocek.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        t_klocek.addChild(klocek);
         
        
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
        objRotate.addChild(t_podstawa);
    
        element = new BranchGroup();
        element.setCapability(element.ALLOW_DETACH);
        element.setCapability(element.ALLOW_CHILDREN_WRITE);
        element.setCapability(element.ALLOW_CHILDREN_READ);
        element.addChild(t_klocek);

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
        CollisionDetectorFunc detect = new CollisionDetectorFunc(klocek, new BoundingSphere(new Point3d(), 0.065d));
        detect.setSchedulingBounds(bounds);
        Scena.addChild(detect);
        Scena.compile();
        return Scena;

    }
 
   // @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == reset){ 
            kat1 = 0.0f; 
            kat2 = 0.0f; 
            wys = 0.0f;
            
            //USTAWIENIE WSZYSTKICH SKŁADOWYCH ROBOTA W POZYCJI STARTOWEJ
            obrot1.rotY(kat1);  
            przes1.setTranslation(new Vector3f(0.2f,0.25f,0.0f)); 
            obrot1.mul(przes1); 
            t_ramie_1.setTransform(obrot1);
            obrot2.rotY(kat2);  
            przes2.setTranslation(new Vector3f(0.21f,0.1f,0.0f)); 
            obrot2.mul(przes2); 
            t_ramie_2.setTransform(obrot2);
            przes3.setTranslation(new Vector3f(0.25f,-0.1f,0.0f)); 
            t_chwytak.setTransform(przes3);
            
            //jeżeli klocek był trzymany, to następuje opuszczenie go
            if(przenoszenie_klocka == true){
                t_chwytak.removeChild(element);
                objRotate.addChild(element);
                System.err.println("jestem tutaj");
                przenoszenie_klocka = false;  
            }
            
            //WYZEROWANIE USTAWIEŃ KAMERY
            objRotate.setTransform( new Transform3D());
            
            //ustawienie klocka w pozycji startowej
            poz_klocek = new Transform3D();
            pozX = 0.86f; pozY = 0.07f; pozZ = 0.0f;
            poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
            t_klocek.setTransform(poz_klocek);
                
                System.out.println(pozX +" "+ pozY+"" +pozZ);

        }
        else if(e.getSource() == nauka_start)
        {
            /*
            Początek nauki.
            Tworzona jest czysta tablica (czyszczona jest stara zawartość.
            Zapisywane jest akutalne położenie klocka i ustawienie ramion robota.
            */
            iloscKrokow = new int[1000];
            pPrzenoszenie = przenoszenie_klocka;
            System.out.println("pPrzen" + pPrzenoszenie);
            zapis = true;
            poczKat1 = kat1;
            poczKat2 = kat2;
            poczWych = wys;
            pPozX = pozX;
            pPozY = pozY;
            pPozZ = pozZ;
        }
        else if(e.getSource() == audio)
        {
            muzyka = !muzyka;
        }
        
        else if(e.getSource() == nauka_koniec)
        {
            liczbaKrokow = 0;
            zapis = false;
            //System.out.println("Przen: " + przenoszenie_klocka);
        }
        else if(e.getSource() == zademonstruj)
        {
            kat1 = poczKat1;
            kat2 = poczKat2;
            wys = poczWych;
            pozX = pPozX;
            pozY = pPozY;
            pozZ = pPozZ;

            if(pPrzenoszenie == false && przenoszenie_klocka == false){
                //System.out.println("1");
                poz_klocek = new Transform3D();
                poz_klocek.set(new Vector3f(pPozX, pPozY, pPozZ));
                t_klocek.setTransform(poz_klocek);
            }
            
            else if(pPrzenoszenie == true && przenoszenie_klocka == false){
                //System.out.println("2");
                objRotate.removeChild(element);
                poz_klocek = new Transform3D();
                poz_klocek.set(new Vector3f(0.0f, -0.37f, 0.0f));
                t_klocek.setTransform(poz_klocek);
                t_chwytak.addChild(element);
                przenoszenie_klocka = true;
            }
            
            else if(pPrzenoszenie == false && przenoszenie_klocka == true){
                System.out.println("3");
                poz_klocek = new Transform3D();
                poz_klocek.set(new Vector3f(pPozX, pPozY, pPozZ));
                t_klocek.setTransform(poz_klocek);
                t_chwytak.removeChild(element);
                objRotate.addChild(element);  
                przenoszenie_klocka = false;               
            }
            
            //System.out.println("4");
            
            obrot1.rotY(kat1);  
            przes1.setTranslation(new Vector3f(0.2f,0.25f,0.0f)); 
            obrot1.mul(przes1); 
            t_ramie_1.setTransform(obrot1);
            obrot2.rotY(kat2);  
            przes2.setTranslation(new Vector3f(0.21f,0.1f,0.0f)); 
            obrot2.mul(przes2); 
            t_ramie_2.setTransform(obrot2);
            przes3.setTranslation(new Vector3f(0.25f,-0.1f,0.0f)); 
            t_chwytak.setTransform(przes3);
            
            
           while(iloscKrokow[nrKroku]!=0)
           {
               //System.out.println("Nr kroku: " + nrKroku + " rodzaj " + iloscKrokow[nrKroku]);
               if(iloscKrokow[nrKroku] == 1)
               {
                   audio_play(muzyka,1);
                   while(zmiana < 0.01f)
                   {
                   obrotRamie1Lewo(0.0000005f);
                   zmiana += 0.0000005f;
                   }
                   zmiana = 0.0f;
               }
               
               else if(iloscKrokow[nrKroku] == 2)
               {
                   audio_play(muzyka,1);
                   while(zmiana < 0.01f)
                   {
                   obrotRamie1Prawo(0.0000005f);
                   zmiana += 0.0000005f;
                   }
                   zmiana = 0.0f;
               }
               
               else if(iloscKrokow[nrKroku] == 3)
               {
                   audio_play(muzyka,1);
                   while(zmiana < 0.01f)
                   {
                   zmiana += 0.0000005f;
                   obrotRamie2Lewo(0.0000005f);   
                   }
                   zmiana = 0.0f;
               }
               
                else if(iloscKrokow[nrKroku] == 4)
               {
                   audio_play(muzyka,1);
                   while(zmiana < 0.01f)
                   {
           
                   zmiana += 0.0000005f;
                   obrotRamie2Prawo(0.0000005f);
                   }
                   zmiana = 0.0f;
               }
                
                else if(iloscKrokow[nrKroku] == 5)
               {
                   audio_play(muzyka,1);
                   while(zmiana < 0.01f)
                   {
                 
                   zmiana += 0.0000005f;
                   podniesienie_chwytaka(0.0000005f);
                   }
                   zmiana = 0.0f;
               }
                
                else if(iloscKrokow[nrKroku] == 6)
               {
                   audio_play(muzyka,1);
                   while(zmiana < 0.01f)
                   {
                   zmiana += 0.0000005f;
                   opuszczenie_chwytaka(0.0000005f);
                   }
                   zmiana = 0.0f;
               }
               else if(iloscKrokow[nrKroku] == 7)
               {
                   if(przenoszenie_klocka == false){
                    audio_play(muzyka,2);
                    objRotate.removeChild(element);
                    poz_klocek.set(new Vector3f(0.0f, -0.37f, 0.0f));
                    t_klocek.setTransform(poz_klocek);
                    t_chwytak.addChild(element);
                    
                    }
                    
                   przenoszenie_klocka = true;
                   
                   //System.out.println("lalala");
               }
               else if(iloscKrokow[nrKroku] == 8)
               {
                    //System.out.println("5");
                   audio_play(muzyka,2);
                    Transform3D temp = new Transform3D();
                    t_chwytak.removeChild(element);
                    objRotate.addChild(element);
                    //Zadanie kinematyki prostej
                    poz_klocek.set(new Vector3f((float) (0.425f*Math.cos(kat1)+0.425f*Math.cos(kat1+kat2)), wysokosc, (float) -(0.435f*Math.sin(kat1)+0.435f*Math.sin(kat1+kat2))));
                    temp.mul(poz_klocek);
                    t_klocek.setTransform(temp);
                    
                    przenoszenie_klocka = false;
                   

               }
               nrKroku++;
           }
           nrKroku = 0;
        }
    }
   @Override
            public void keyPressed(KeyEvent e){
               
                switch(e.getKeyCode()){
                    
                    case KeyEvent.VK_LEFT:
                        if(((CollisionDetectorFunc.inCollision && ostatni_ruch != 1) || przenoszenie_klocka == true || CollisionDetectorFunc.inCollision == false))
                        {
                            
                        if(!CollisionDetectorFunc.inCollision) ostatni_ruch = 1;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 1;
                            liczbaKrokow++;
                        }
                        obrotRamie1Lewo(0.01f);

                        audio_play(muzyka,1);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(((CollisionDetectorFunc.inCollision && ostatni_ruch != 2) || przenoszenie_klocka == true || CollisionDetectorFunc.inCollision == false))
                        {
                        if(!CollisionDetectorFunc.inCollision) ostatni_ruch = 2;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 2;
                            
                            liczbaKrokow++;
                        }
                        audio_play(muzyka,1);
                        obrotRamie1Prawo(0.01f);
                        }
                        break;
                    case KeyEvent.VK_1:
                        if(((CollisionDetectorFunc.inCollision && ostatni_ruch != 2) || przenoszenie_klocka == true || CollisionDetectorFunc.inCollision == false))
                        {
                        if(!CollisionDetectorFunc.inCollision) ostatni_ruch = 2;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 2;
                            
                            liczbaKrokow++;
                        }
                        audio_play(muzyka,1);
                        obrotChwytakLewo();
                        }
                        break;
                    case KeyEvent.VK_2:
                        if(((CollisionDetectorFunc.inCollision && ostatni_ruch != 2) || przenoszenie_klocka == true || CollisionDetectorFunc.inCollision == false))
                        {
                        if(!CollisionDetectorFunc.inCollision) ostatni_ruch = 2;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 2;
                            
                            liczbaKrokow++;
                        }
                        audio_play(muzyka,1);
                        obrotChwytakPrawo();
                        }
                        break;
                    case KeyEvent.VK_A:
                        if(((CollisionDetectorFunc.inCollision && ostatni_ruch != 3) || przenoszenie_klocka == true || CollisionDetectorFunc.inCollision == false))
                        {
                        if(!CollisionDetectorFunc.inCollision) ostatni_ruch = 3;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 3;
                            liczbaKrokow++;
                        }
                        audio_play(muzyka,1);
                        obrotRamie2Lewo(0.01f);
                        }
                        break;
                    case KeyEvent.VK_D:
                        if(((CollisionDetectorFunc.inCollision && ostatni_ruch != 4) || przenoszenie_klocka == true || CollisionDetectorFunc.inCollision == false))
                        {
                        if(!CollisionDetectorFunc.inCollision) ostatni_ruch = 4;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 4;
                            liczbaKrokow++;
                        }
                        audio_play(muzyka,1);
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
                        podniesienie_chwytaka(0.01f);
                        audio_play(muzyka,1);
                        break;
                    case KeyEvent.VK_DOWN:
                        if(((CollisionDetectorFunc.inCollision && ostatni_ruch != 6) || przenoszenie_klocka == true || CollisionDetectorFunc.inCollision == false))
                        {
                            System.out.println("");
                        if(!CollisionDetectorFunc.inCollision) ostatni_ruch = 6;
                        if(zapis == true)
                        {
                            iloscKrokow[liczbaKrokow] = 6;
                            liczbaKrokow++;
                        }
                        opuszczenie_chwytaka(0.01f);
                        audio_play(muzyka,1);
                        }
                        break;    
                    case KeyEvent.VK_SPACE: 
                         System.err.println(wys);
                          if(CollisionDetectorFunc.inCollision == true && wys > -0.05f && wys < -0.01f){
                              przenoszenie_klocka = !przenoszenie_klocka;
                              audio_play(muzyka,2);
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
                                t_klocek.setTransform(temp);
                                t_chwytak.addChild(element);
                                
                                
                            }
                            else {
                                
                                audio_play(muzyka,2);
                                
                                
                                Transform3D temp = new Transform3D();
                                 if(zapis == true)
                                {
                                    iloscKrokow[liczbaKrokow] = 8;
                                    liczbaKrokow++;
                                }
                               System.err.println("puszczamoe");
                                t_chwytak.removeChild(element);
                                pozX = (float) (0.425f*Math.cos(kat1)+0.425f*Math.cos(kat1+kat2));
                                pozY = (float) wysokosc;
                                pozZ = (float) (-(0.435f*Math.sin(kat1)+0.435f*Math.sin(kat1+kat2)));
                                System.out.println(pozX +" "+ pozY+"" +pozZ);
                                poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
                                temp.mul(poz_klocek);
                                t_klocek.setTransform(temp);
                                objRotate.addChild(element);
                                
                                 
                            }
                          }
                          else if(CollisionDetectorFunc.inCollision == false && przenoszenie_klocka == true && wys > -0.04f && wys < -0.01f)
                          {
                              
                                if(zapis == true)
                                {
                                    iloscKrokow[liczbaKrokow] = 7;
                                    liczbaKrokow++;
                                }
                                audio_play(muzyka,2);
                                t_chwytak.removeChild(element);
                                pozX = (float) (0.425f*Math.cos(kat1)+0.425f*Math.cos(kat1+kat2));
                                pozY = (float) wysokosc;
                                pozZ = (float) (-(0.435f*Math.sin(kat1)+0.435f*Math.sin(kat1+kat2)));
                                System.out.println(pozX +" "+ pozY+"" +pozZ);
                                poz_klocek.set(new Vector3f(pozX, pozY, pozZ));
                                Transform3D temp = new Transform3D();
                                temp.mul(poz_klocek);
                                t_klocek.setTransform(temp);
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
     * Główna metoda klasy.;
     * @param args 
     */         
    public static void main(String[] args) {
     
        SCARA_2018 okno = new SCARA_2018();
        okno.addKeyListener(okno);
        MainFrame mf = new MainFrame(okno, 900, 600); 
        
        // TODO code application logic here
    }
}


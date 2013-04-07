//Turret.java
//DusanZelembaba
//This class represents a turret in the game. A turret will locate an ant within its range and then fire bullets at it.
//A turret starts as a basic turret and can upgrade in many different ways from there. This class implements Runnabel
//so each turret will run as a seperate thread
import javax.swing.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;

public class Turret implements Runnable{
	private String name = "Basic Turret";//The name of the turret
	private double x_pos;//The x-coordinate of the turret
	private double y_pos;//The y-coordinate of the turret
	private Image base;//The image of this turrets base
	private Image gun;//The image of this turrets gun
	private java.util.List <Bullet> shots;//An arrayList holding all the bullets that have been fired by this turret
	private double fireRate;//This number of bullets fired per second
	private double range;//The range of the turret
	private double bulletSpeed;//The speed of this turret's bullets
	private double bulletPower;//The power of this turret's bullets
	private double heading = 315;//The direction this turret is facing (0 = right)
//	private boolean fired = false;//If this turret has fired (used
	private Ant target;//The current ant this turret is targetting
	private int antKills = 0;//The number of ants this turret has killed
	private boolean fireMissile;//If a missile has been fired 
	private ArrayList <MyButton> upgrades = new ArrayList <MyButton> ();//An arrayList of buttons leading to this turret's upgrades
	private CheckBox [] checkBoxes = new CheckBox [4];//An array of checkboxes that represent what this turret will target
	private MainGame game;//The MainGame object
	private AntHill antHill;//The antHill creating the ants in the game
	private String targetOrder1 = "Target1 With Cake";//What ants to target (With Cake. NoCake)
	private String targetOrder2 = "Target2 Weakest";//What ants to target 2nd (Weakest,Closest)
	private Thread th;//This turrets thread
	//Maps the turret name to a String array containing stats and upgrades
	public static Map <String,String []> turretStats = new HashMap <String,String []>  ();//1-Cost,2-fR,3-range,4-speed,5-Damage
	
	//The constructor that creates a bullet taking in values for the position,the fireRate,the range,the bulletSpeed
	//,the bulletPower and the two images as well as an antHill object and the MainGame object
    public Turret(double x,double y,double fR,double r,double bS,double bP,Image b,Image gu,AntHill a,MainGame g){
    	x_pos = x;
    	y_pos = y;
    	fireRate = fR;
    	range = r;
    	bulletSpeed = bS;
    	bulletPower = bP;
    	base = b;//20X20
    	gun = gu;//20X20
    	shots = Collections.synchronizedList(new ArrayList <Bullet> ());
    	antHill = a;
    	game = g;
    	upgrades.add (new MyButton ((int)MainGame.getTopLeft().getX()+110,(int)MainGame.getBottomRight().getY()+20,new ImageIcon ("resources/Pictures/Turrets/Double TurretGun.png").getImage(),"Double Turret"));
    	upgrades.add (new MyButton ((int)MainGame.getTopLeft().getX()+145,(int)MainGame.getBottomRight().getY()+20,new ImageIcon ("resources/Pictures/Turrets/Sniper TurretGun.png").getImage(),"Sniper Turret"));
    	upgrades.add (new MyButton ((int)MainGame.getTopLeft().getX()+110,(int)MainGame.getBottomRight().getY()+55,new ImageIcon ("resources/Pictures/Turrets/Heavy TurretGun.png").getImage(),"Heavy Turret"));
    	upgrades.add (new MyButton ((int)MainGame.getTopLeft().getX()+145,(int)MainGame.getBottomRight().getY()+55,new ImageIcon ("resources/Pictures/Turrets/Quick TurretGun.png").getImage(),"Quick Turret"));
		checkBoxes [0] = new CheckBox ((int)MainGame.getTopLeft().getX()+290,(int)MainGame.getBottomRight().getY()+20,"Target1 With Cake","With Cake");    
    	checkBoxes [1] = new CheckBox ((int)MainGame.getTopLeft().getX()+290,(int)MainGame.getBottomRight().getY()+35,"Target1 NoCake","Without Cake");
    	checkBoxes [2] = new CheckBox ((int)MainGame.getTopLeft().getX()+290,(int)MainGame.getBottomRight().getY()+55,"Target2 Weakest","Weakest");
    	checkBoxes [3] = new CheckBox ((int)MainGame.getTopLeft().getX()+290,(int)MainGame.getBottomRight().getY()+70,"Target2 Closest","Closest");
    }
    //Reads the stats of each turret from a file and stores it in turretStats
    public static void initStats ()throws IOException{
		Scanner fIn = new Scanner (new File ("resources/stats.txt"));
		while (fIn.hasNextLine ()){
			String name = fIn.nextLine ();
			String [] stats = fIn.nextLine ().split (",");
			turretStats.put (name,stats);
		}
    }
    //Returns the first target order
    public String getTargetOrder1 (){
    	return targetOrder1;
    }
    //Sets the first target order
    public void setTargetOrder1(String s){
    	targetOrder1 = s;
    }
    //Returns the 2nd target order
    public String getTargetOrder2 (){
    	return targetOrder2;
    }
    //Sets the 2nd taget order
    public void setTargetOrder2 (String s){
    	targetOrder2 = s;
    }
    //Returns an array of this turret's checkboxes
    public CheckBox [] getCheckBoxes (){
    	return checkBoxes;
    }
    //Returns a Map of all the Turret stats
    public static Map <String,String []> getStats (){
    	return turretStats;
    }
    //Returns an ArrayList of button representing this turret's upgrades
    public ArrayList <MyButton> getUpgrades (){
    	return upgrades;
    }
    //Returns the name of this turret
    public String getName (){
    	return name;
    }
    //Returns the x-coordinate of this turret
    public double getX (){
    	return x_pos;
    }
    //Returns the y-coordinate of this turret
    public double getY (){
    	return y_pos;
    }
    //Returns the range of this turret
    public double getRange (){
    	return range;
    }
    //Returns the number of ants this turret has killed
    public int getAntKills (){
    	return antKills;
    }
    //Starts this thread
    public void startTurret (){
    	if (th == null){
    		th = new Thread (this);
    		th.start ();
    	}
    }
    //Draws the turret by rotating the gun so that it corresponds with the turrets heading
	public void drawTurret (Graphics g){
		drawBullets (g);
		g.drawImage (base,(int)x_pos-10,(int)y_pos-10,game);
    	Graphics2D g2D = (Graphics2D)g;
		AffineTransform saveXform = g2D.getTransform();
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians (-heading),x_pos,y_pos);
		g2D.transform(at);
		g2D.drawImage(gun,(int)x_pos-10,(int)y_pos-10,game);
		g2D.setTransform(saveXform);
	}
	//Draws each of this turret's bullets by using the bullet's draw method
	public void drawBullets (Graphics g){
		for (int i = 0;i<shots.size ();i++){
			Bullet b = shots.get (i);
			b.drawBullet (g);
		}
	}
	//Draws this turrets stats in a box with the top left corner being at (x,y)
	public void drawStats (Graphics g,int x,int y){
		g.setColor (Color.black);
		g.drawString ("Fire Rate:  "+fireRate,x,y);
		g.drawString ("Range:       "+range,x,y+13);	   
		g.drawString ("Speed:       "+bulletSpeed,x,y+26);
		g.drawString ("Damage:   "+bulletPower,x,y+39);
		g.drawString ("Kills:           "+antKills,x,y+52);
	}
	//Fires a bullet by adding it to shots
    public void fire (){
    	if (name.contains ("Double")){//A double turret will shoot two bullets
    		shots.add (new Bullet (x_pos,y_pos,bulletSpeed,heading-10,bulletPower,Color.yellow));
    		shots.add (new Bullet (x_pos,y_pos,bulletSpeed,heading+10,bulletPower,Color.yellow));
    	}
    	else if (name.contains ("Triple")){//A double turret will shoot three bullets
    		shots.add (new Bullet (x_pos,y_pos,bulletSpeed,heading,bulletPower,Color.blue));
    		shots.add (new Bullet (x_pos,y_pos,bulletSpeed,heading-15,bulletPower,Color.blue));
    		shots.add (new Bullet (x_pos,y_pos,bulletSpeed,heading+15,bulletPower,Color.blue));
    	}
    	else if (name.contains ("Octa")){//A double turret will shoot eight bullets
    		for (int i = -60;i<61;i+=15){
    			shots.add (new Bullet (x_pos,y_pos,bulletSpeed,heading+i,bulletPower,Color.red));
    		}
    	}
    	else if (name.contains ("Laser")){//A laser turret will shoot a laserBullet
    		shots.add (new LaserBullet (x_pos,y_pos,target.getX(),target.getY(),heading,bulletPower));
    	}
    	else if (name.contains ("Missile")){//A Missile Launcher will shoot a missile
    		if (!fireMissile)
    			shots.add (new Missile (x_pos,y_pos,bulletSpeed,heading,bulletPower,target,new ImageIcon ("resources/Pictures/Missile.png").getImage(),game));
    		fireMissile = true;
    	}
    	else if (name.contains ("Lightning")){//A Lightning turet will shoot a lightingin bullet
    		shots.add (new Lightning (x_pos,y_pos,target.getX(),target.getY(),heading,bulletPower));
    	}
    	else if (name.contains ("Poison")){//A Poison turet will shoot PoisonGas
    		shots.add (new PoisonGas (x_pos,y_pos,bulletSpeed,heading));
    	}
    	else if (name.contains ("Ice")){//An Ice turet will shoot Ice Bullets
    		shots.add (new IceBullet (x_pos,y_pos,bulletSpeed,heading,bulletPower));
    	}
    	else{
    		shots.add (new Bullet (x_pos,y_pos,bulletSpeed,heading,bulletPower));
    	}
    }
    //Turns the turret so that it will be facing the target
    public void turnToTarget (){
    	double x_dist = target.getX()-x_pos;
    	double y_dist = target.getY()-y_pos;
    	
    	if (x_dist == 0){
    		heading = y_dist > 0 ? 270 : 90;
    		return;
    	}
    	if (x_dist < 0){
    		x_dist *= -1;
    		if (y_dist <0){
    			y_dist *= -1;
     			heading = 180 - Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    		else{
    			heading = 180 + Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    	}
    	else{
    	  	if (y_dist <0){
    			y_dist *= -1;
     			heading = Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    		else{
    			heading = 360 - Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    	}
    }
    //Main method for this thread.Finds an ant in range and then shoots at it.Sleeps in between shots based on fireRate
    public void run (){
    	while (true){
    		while (!game.getPaused ()){
				target = findAnt ();
				if (target != null){
					if (distance (x_pos,target.getX(),y_pos,target.getY()) > range || !target.isAlive()){
						target = null;
					}
					else{
						turnToTarget ();
						fire ();
					}
				}
		    	try{
		    		th.sleep ((int)(1000 / fireRate));
		    	}
		    	catch (InterruptedException e){
		    	}
    		}
    	}
    }
    //Finds the distance from (x1,y1) to (x2,y2)
    public double distance (double x1,double x2,double y1,double y2){
    	return Math.sqrt ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    //Checks this turrets bullet to see if any are collding with an ant.
   	public int checkCollisions (){
   		int score = 0;//The level of the ants kiled
		for (int i = antHill.getAnts ().size ()-1;i>=0;i--){
			Ant a = antHill.getAnts ().get (i);
			for (int j = shots.size ()-1;j>=0;j--){
				Bullet b = shots.get (j);
                if (b == null) {
                    System.out.println("Its Null! " + j);
                    for (int k = 0; k < shots.size(); k++) System.out.println(shots.get(k));
                }
				//Deal with special cases of bullets
				if (b.getClass ().toString().equals ("class Explosion") && b.getRadius () > 30){
					shots.remove (j);
				}
				else if (b.getClass ().toString().equals ("class LaserBullet") && b.getFadeCount() <= 0){
					shots.remove (j);
				}
				else if (b.getClass ().toString().equals ("class PoisonGas")){
					Bullet [] gas = b.getBullets ();
					for (int k = 0;k<gas.length;k++){
						Bullet b2 = gas [k];
						if (distance (b2.getX(),a.getX(),b2.getY(),a.getY()) < 10+b2.getRadius ()){
							a.poison ();
						}
					}
				}
				else if (b.getClass ().toString().equals ("class Lightning")){
					if (b.getPower() != 0)
					for (Point p : b.getPoints ()){//Iterate through each connection point on the lighting and treat it as a seperate bullet
						if (distance (p.getX(),a.getX(),p.getY(),a.getY()) < 20){
							a.looseHealth (b.getPower ());
							if (!a.isAlive()){
								antHill.removeAnt (i);
								antKills ++;
								if (a.hasCake ())
									antHill.setCakeGone (antHill.getCakeGone () - 1);
								score += (int)Math.sqrt (a.getMaxHealth ()*4);
								break;
							}
						}
					}
					if (b.getFadeCount ()<= 0)
						shots.remove (j);
					else if (i  == 0)
						b.setPower (0);//Sets power to 0 so it doesnt do any damage while it is fading
				}
				//If a collision ccurs
				else if (distance (b.getX(),a.getX(),b.getY(),a.getY()) < 10+b.getRadius()){
					a.looseHealth (b.getPower ());
					//Deal with special cases
					if (b.getClass ().toString().equals ("class LaserBullet")){
						b.setVelocity (0);
						b.setPower (0);
					}
					else if (!b.getClass ().toString().equals ("class Explosion"))
						shots.remove (j);//Remove the buller uncless it is an Explosion or LaserBullet
					if (b.getClass().toString().equals ("class Missile")){
						fireMissile = false;
						shots.add (new Explosion (b.getX(),b.getY(),1));
					}
					else if (b.getClass ().toString().equals ("class IceBullet")){
						a.freeze ();
					}
					//if the ant dies remove it and to the user's score
					if (!a.isAlive()){
						antHill.removeAnt (i);
						antKills ++;
						if (a.hasCake ())
							antHill.setCakeGone (antHill.getCakeGone () - 1);
						score += (int)Math.sqrt (a.getMaxHealth ()*4);
						break;
					}
				}
			}
		}
		return score;
	}
	//Move each bullet bu calling the bullet's move function
    public void moveBullets (){
    	for (int i = 0;i<shots.size ();i++){
    		Bullet b = shots.get (i);
    		b.move ();
    		//Deal with special cases
    		if (b.getClass().toString().equals ("class Missile")){
    			if (!b.getTarget().isAlive ()){
    				shots.remove (i);
    				fireMissile = false;
    			}
    		}
    		else if (distance (x_pos,b.getX(),y_pos,b.getY()) > range&&!b.getClass().toString().equals ("class Explosion")){
    			shots.remove (i);
    		}
    	}
    }
    //Find weakest ant in range
    public Ant findWeakest (ArrayList <Ant> ants){	
    	double weakest = Integer.MAX_VALUE;
		Ant lowest = null;
		
    	for (int i = 0;i<ants.size();i++){
    		Ant a = ants.get (i);
    		if (a.getHealth () < weakest ){
				lowest = a;
				weakest = a.getHealth ();
			}
    	}
    	return lowest;
    }
    //Find closest ant in range
    public Ant findClosest (ArrayList <Ant> ants){	
    	double closest = range;
		Ant nearest = null;
		
    	for (int i = 0;i<ants.size();i++){
    		Ant a = ants.get (i);
    		if (distance (x_pos,a.getX(),y_pos,a.getY()) < closest){
				nearest = a;
				closest = distance (x_pos,a.getX(),y_pos,a.getY());
			}
    	}
    	return nearest;
    }
    //Finds the ant to be targeted based on what this targets prioritizes
    public Ant findAnt (){
    	ArrayList <Ant> choices = new ArrayList <Ant> ();
    	ArrayList <Ant> inRange = new ArrayList <Ant> ();
		
		for (int i = 0;i<antHill.getAnts().size();i++){
			Ant a = antHill.getAnts ().get (i);
			if (distance (x_pos,a.getX(),y_pos,a.getY()) < range){
				inRange.add (a);
				if (targetOrder1.equals ("Target1 With Cake")){
					if (a.hasCake ())
						choices.add (a);
				}
				else
					if (!a.hasCake())
						choices.add (a);
			}
		}
		if (targetOrder2.equals ("Target2 Weakest"))
			if (choices.isEmpty())
				return findWeakest (inRange);
			else
				return findWeakest (choices);
		else if (targetOrder2.equals ("Target2 Closest"))
			if (choices.isEmpty())
				return findClosest (inRange);
			else	
				return findClosest (choices);
		else
			return null;
    }
    //Turens the turret right by 'deg'
    public void turnRight (int deg){
    	heading -= deg;
    	while (heading > 360){//Keeps angle under 360
    		heading -= 360;
    	}
    }
    //Upgrades a turret to 'name'
    public void upgrade (String name){
    	this.name = name;
    	//Uses turretStats to change this turrets stats
    	fireRate = Double.parseDouble (turretStats.get (name) [1]);
    	range = Double.parseDouble (turretStats.get (name) [2]);
    	bulletSpeed = Double.parseDouble (turretStats.get (name) [3]);
    	bulletPower = Double.parseDouble (turretStats.get (name) [4]);
    	base = new ImageIcon ("resources/Pictures/Turrets/"+name+"Base.png").getImage ();
    	gun = new ImageIcon ("resources/Pictures/Turrets/"+name+"Gun.png").getImage ();
    	upgrades.clear ();
    	//Adds new buttons to represent the upgraded turrets upgrades
    	for (int i = 5;i<turretStats.get (name).length;i++){
    		if (i < 7){
    			upgrades.add (new MyButton ((int)MainGame.getTopLeft().getX()+110+((i-5)*35),
	    		(int)MainGame.getBottomRight().getY()+20,new ImageIcon ("resources/Pictures/Turrets/"+turretStats.get (name) [i]+"Gun.png").getImage(),turretStats.get (name) [i]));
    		}
    		else{
    			upgrades.add (new MyButton ((int)MainGame.getTopLeft().getX()+110+((i-7)*35),
	    		(int)MainGame.getBottomRight().getY()+55,new ImageIcon ("resources/Pictures/Turrets/"+turretStats.get (name) [i]+"Gun.png").getImage(),turretStats.get (name) [i]));
    		}
    	}
    }   
}

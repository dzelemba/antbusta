//Ant.java
//DusanZelembaba
//This file has all the classes that represent the ants in the game.

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;

//This class represents an ant in the game.The ant creates a path of 10 points
//and follows it towards the cake and then back to the ant hill.
public class Ant {
	private String name;//Name of the ant (Ant)
	private double x_pos;//X position of the ant
	private double y_pos;//Y position of the ant
	private double health;//Health of the ant
	private double maxHealth;//The max health this ant can have
	private double speed;//How fast the ant moves
	private double heading = 315;//The direction in which the ant travels (0 is right)
	private boolean hasCake;//True if the ant has stolen a cake; false otherwise
	//Next 3 pictures are draw in rotation to make the ants legs move
	private Image antPic1;
	private Image antPic2;
	private Image antPic3;
	private Image cake;//The image of a single cake slice.
	private Image current;//The current image being drawn (antPic1-3)
	private Point topLeft = MainGame.getTopLeft ();//topleft corner of the game area
	private Point bottomRight = MainGame.getBottomRight ();//bottom right corner of the game area
	private AntHill antHill;//The anthill that creates this ant
	private Point target;//The point the ant is walking towards
	private Point cakeP = new Point ((int)MainGame.getBottomRight().getX() - 35,(int)MainGame.getBottomRight().getY() -35);//The (x,y) coordinates of the cake
	private Point home = new Point ((int)MainGame.getTopLeft().getX() + 15,(int)MainGame.getTopLeft().getY() +15);//The (x,y) coordinates of the ant hill
	private MainGame game;//The MainGame
	private int targetCount = 0;//index of the path array (which point we are targeting
	private Point [] path = new Point [10];//The array of points the ant will follow
	private boolean poison = false;//If the ant is poisened or not
	private int poisonCount;//The length of the poison
	private boolean frozen;//If the ant is frozen or not
	private int freezeCount;//The duration of the freeze
	
	//Constructor that creates the ant at position (x,y) a heading of h, a speed of s
	//and passes in the anthill that created the ant as well as the MainGame controlling everything
    public Ant(double x,double y,int h,double s,AntHill a,MainGame g) {
    	name = "Ant";
    	x_pos = x;
    	y_pos = y;
    	health = h;
    	maxHealth = h;
    	speed = s;
    	antPic2 = new ImageIcon ("resources/Pictures/Ants/ant.png").getImage ();//25X25
    	antPic1 = new ImageIcon ("resources/Pictures/Ants/ant2.png").getImage ();
    	antPic3 = new ImageIcon ("resources/Pictures/Ants/ant3.png").getImage ();
    	cake = new ImageIcon ("resources/Pictures/Cake/CakeSlice.png").getImage ();
    	current = antPic2;
    	antHill = a;
    	game = g;
    }
    //Draws the ant by rotating the current image at an angle that corresponds to
    //the heading
    public void drawAnt (Graphics g){
    	Graphics2D g2D = (Graphics2D)g;
    	g2D.setColor (Color.lightGray);
    	//draws the healthbar
    	g2D.fillRect ((int)x_pos-18,(int)y_pos-18,5,25);
    	g2D.setColor (Color.red);
    	g2D.fillRect ((int)x_pos-18,(int)y_pos-18,5,(int)((health/maxHealth) * 25));
		AffineTransform saveXform = g2D.getTransform();
		AffineTransform at = new AffineTransform();
		//Image starts off at an angle of -30 so add -30 to the heading
		at.rotate(Math.toRadians (-heading-30),x_pos,y_pos);
		g2D.transform(at);
		g2D.drawImage(current,(int)x_pos-13,(int)y_pos-13,game);
		if (hasCake)
			g2D.drawImage(cake,(int)x_pos-20,(int)y_pos-13,game);
		g2D.setColor (Color.white);
		g2D.setTransform(saveXform);
    }
    //Rotates between the 3 ant images
    public void changeCurrent (){
    	if (current.equals (antPic1)){
    		current = antPic2;
    	}
    	else if (current.equals (antPic2)){
    		current = antPic3;
    	}
    	else{
    		current = antPic1;
    	}
    }
    //Sets the ants name to'n'
    public void setName (String n){
    	name = n;
    }
    //Returns if the ant has stolen a cake
    public boolean hasCake (){
    	return hasCake;
    }
    //Returns the x positon of the ant
    public double getX (){
    	return x_pos;
    }
    //Returns the y position of the ant
    public double getY (){
    	return y_pos;
    }
    //Returns the current health of the ant
    public double getHealth (){
    	return health;
    }
    //Returns the ants maximum health
    public double getMaxHealth (){
    	return maxHealth;
    }
    //Return the direction in which
    public double getHeading (){
    	return heading;
    }
    //sets the direction of the ant to amt
    public void setHeading (double amt){
    	heading = amt;
    }
    //decreases the ants health by amt
    public void looseHealth (double amt){
    	health -= amt;
    }
    //Poisons the ant
    public void poison (){
    	poison = true;
    	poisonCount = 500;
    }
    //Freezes the ant
    public void freeze (){
    	frozen = true;
    	freezeCount = 250;
    }
    //Returns true if the ant is alive; false otherwise
    public boolean isAlive (){
    	if (health > 0){
    		return true;
    	}
    	return false;
    }
    //sets the targetCount to target the point at position n in the path array
    public void setTargetCount (int n){
    	targetCount = Math.max (n,0);
    }
    //returns the posisiton in the path array that the target is
    public int getTargetCount (){
    	return targetCount;
    }
    //sets the ant to target the point at position n in the path array
    public void setTarget (int i){
    	target = path [Math.max (i,0)];
    }
    //returns the distance from (x1,y1) to (x2,y2)
    public double distance (double x1,double x2,double y1,double y2){
    	return Math.sqrt ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    //draws this ants stats using (x,y) as the topleft corner
   	public void drawStats (Graphics g,int x,int y){
		g.setColor (Color.black);
		g.drawString (name,x,y-17);
		g.drawString ("Health:  "+(int)health+"/"+(int)maxHealth,x,y+7);
		g.drawString ("Speed:       "+speed,x,y+20);	   
	}
	//turns the ant so that it walks towards its target
    public void turnToTarget (){
    	double x_dist = target.getX()-x_pos;
    	double y_dist = target.getY()-y_pos;
    	//if target is directly above or below
    	 if (x_dist == 0){
    		heading = y_dist > 0 ? 270 : 90;
    		return;
    	}
    	//Determines which quadrant the angle is in on the winding function and returns a value using atan
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
    //Creates the ants path by selecting random points that are always moving towards the cake
    public void createPath (){
    	path [0] = home;
    	path [path.length - 1] = cakeP;
    	for (int i = 1;i<path.length-1;i++){
    		while (true){
    			Point next = new Point ((int)(Math.random ()*((bottomRight.getX()-topLeft.getX())/path.length*i)+topLeft.getX()+10),
    				(int)(Math.random ()*((bottomRight.getY()-topLeft.getY())/path.length*i)+topLeft.getY()+10));
    			//makes sure the points are moving toward the cake
    			if (next.getX() - path [i-1].getX() + next.getY() - path [i-1].getY() > 0){
					path [i] = next;
					break;
    			}
    		}
    	}
    	//sets the first taget based on if the ant has a cake or not.
    	target =  hasCake ? path [path.length - 1] : path [0];
    }
    //decreases the heading by amt (turns right by amt degrees)
    public void turnRight (double amt){
    	heading -= amt;
    }
    //Keeps the ant following its path by checking if it has reached its target and then advacing its target.
    public void followPath (){
    	if (hasCake&&distance (x_pos,target.getX(),y_pos,target.getY()) < 5){
    		target = path [targetCount - 1];
    		targetCount --;
    	}
    	else if (distance (x_pos,target.getX(),y_pos,target.getY()) < 5){
    		//if ant has reached cake but there was no cake available then set targetCount to path.length /2
    		targetCount = targetCount+1>path.length - 1 ? path.length / 2 : targetCount +1;
    		target = path [targetCount];
    	}
    }
    //Moves the ant forward and checks the position of the ant.Returns true if the ant dies while moving(poison)
    public boolean move (){
    	followPath();
   		turnToTarget ();
		changeCurrent ();
    	x_pos += speed * Math.cos (Math.toRadians (heading));
    	y_pos -= speed * Math.sin (Math.toRadians (heading));
    	//checks if the ant has reached the cake
    	if (distance (x_pos,MainGame.getBottomRight().getX()-35,y_pos,MainGame.getBottomRight().getY()-35) < 15){
    		if (!hasCake && antHill.getCakeGone () < 8){
    			antHill.setCakeGone (antHill.getCakeGone () + 1);
    			hasCake = true;
    			health = Math.min (health+maxHealth,maxHealth);
    			createPath ();
    			speed = 0.5;
    		}
    	}
    	//checks if the ant has a cake and has reached the ant hill
    	if (distance (x_pos,MainGame.getTopLeft ().getX()+15,y_pos,MainGame.getTopLeft().getY()+15) < 30 && hasCake){
    		antHill.setCakeStolen (antHill.getCakeStolen () + 1);
    		hasCake = false;
    		createPath ();
    		speed = 0.7;
    	}
    	//makes sure the ants stays int he game area
    	if (x_pos < topLeft.getX ()+13 || x_pos > bottomRight.getX ()-13 || y_pos < topLeft.getY()+13 || y_pos >bottomRight.getY()-13){
    		heading += 180;
    	}
    	//Checks the state of the ant
    	if (poison){
    		health -= 0.05;
    		poisonCount -= 1;
    		if (poisonCount <=0)
    			poison = false;
    		if (health <= 0)
    			return true;
    	}
    	if (frozen){
    		speed = 0.3;
    		freezeCount -= 1;
    		if (freezeCount <=0){
    			frozen = false;
    			speed = hasCake ? 0.5 : 0.7;
    		}
    	}
    	return false;
    }
}

//This class represents a ShyAnt in the game. This ant follows the same path as an ant, but it will check forward range
//pixels and with a view of 80 degrees. If a turret is found it will back up and create a new path
class ShyAnt extends Ant{
	private static ArrayList <Turret> guns;//An arraylist of all the guns on the screen
	private double range;//How far ahead the ant can look
	
	//Constructor that calls its super constructor,gets the guns arrayList from MainGame, and sets the range and name
	public ShyAnt (double x,double y,int h,double s,double r,AntHill a,MainGame g) {
		super (x,y,h,s,a,g);
		guns = g.getPlayer().getGuns();
		range = r;
		setName ("Shy Ant");
    }
    //Returns at what angle 't' is located in realtion to this ant's heading
    public double getBearing (Turret t){
    	double x_dist = t.getX()-getX();
    	double y_dist = t.getY()-getY();
    	
    	//if target is directly above or below
    	 if (x_dist == 0){
    		return y_dist > 0 ? 270 : 90 - getHeading();
    	}
    	//Determines which quadrant the angle is in on the winding function and returns a value using atan
    	if (x_dist < 0){
    		x_dist *= -1;
    		if (y_dist <0){
    			y_dist *= -1;
     			return 180 - Math.toDegrees (Math.atan (y_dist/x_dist)) - getHeading ();
    		}
    		else{
    			return 180 + Math.toDegrees (Math.atan (y_dist/x_dist))- getHeading ();
    		}
    	}
    	else{
    	  	if (y_dist <0){
    			y_dist *= -1;
     			return Math.toDegrees (Math.atan (y_dist/x_dist))- getHeading ();
    		}
    		else{
    			return 360 - Math.toDegrees (Math.atan (y_dist/x_dist))- getHeading ();
    		}
    	}
    }
    //Check if a turret is located with a range of 'range' in front of the ant with a view of 80 degrees
    public boolean checkAhead (){
    	for (int i = 0;i<guns.size();i++){
    		Turret t = guns.get (i);
    		double distance = distance (t.getX(),getX(),t.getY(),getY());
    		if (distance < range){//Checks distance first
    			if (getBearing (t) <= 40 && getBearing (t) >= -40){//Checks if its within the field of view
    				return true;
    			}
    		}
    	}
    	return false;
    }
    //Moves the ant by calling the super's move funciton and then checking if a turret is located ahead of it
    public boolean move (){
		//Checks ahead and there is a 95% chance the ant will turn around
		if (checkAhead () && Math.random () >0.95){
			createPath ();
			setTarget (getTargetCount()-1);
			setTargetCount (hasCake() ? getTargetCount()+1:getTargetCount()-1);
			turnRight (180);
		}
		if (super.move())
			return true;
		return false;
    }
}

//This class represemts a smart ant. The smart ant finds a path that encounters the least amount of turrets on its way
//to the cake
class SmartAnt extends Ant{
	//A 2-D array that holds information on the turret locations and is used to determine the path of least resisitance
	private double [][] map = new double [(int)(MainGame.getBottomRight().getY()-MainGame.getTopLeft().getY())][(int)(MainGame.getBottomRight().getX()-MainGame.getTopLeft().getX())];
	//A 2-D array that holds the directions for the path of least resistance
	private String [][] directions = new String [(int)(MainGame.getBottomRight().getY()-MainGame.getTopLeft().getY())][(int)(MainGame.getBottomRight().getX()-MainGame.getTopLeft().getX())];
	private Point [] path = new Point [10000];//A Point array that will be the path the ant follows
	private Point target;//The Point the ant is currently aiming towards
	private int targetCount = 0;//The index in the path array of target
	
	//A constructor the creates an ant and creates the smart ant's map
	public SmartAnt (double x,double y,int h,double s,AntHill a,MainGame g) {
		super (x,y,h,s,a,g);
		createMap (g);
		setName ("Smart Ant");
    } 
    //This function will use a dynamic programming mehtod to determine the best way for the ant to reacht the cake
    public void createMap (MainGame g){
   	 ArrayList <Turret> guns = g.getPlayer().getGuns();
   	 	//Set both 2-D arrays to "0" and "" values.
   	 	for (int y = 0;y<map.length;y++){
   	 		for (int x = 0;x<map [0].length;x++){
   	 			map [y][x] = 0;
   	 			directions [y][x] = "";
   	 		}
   	 	}
   	 	//goes through each turret and places a value of 1 at every position that the turret can shoot at and also
   	 	//2 pixels farther than the turrets range just to be safe
    	for (int i = 0;i<guns.size();i++){
    		Turret t = guns.get (i);
    		for (int x = (int)(t.getX()-t.getRange()-2);x<t.getX()+t.getRange()+2;x++){
    			for (int y = (int)(t.getY()-t.getRange()-2);y<t.getY()+t.getRange()+2;y++){
    				double distance = distance (x,t.getX(),y,t.getY());
    				if (distance < t.getRange ()+2){
    					if (y - 100 < map.length && x < map[0].length)
    						map [Math.max (y-100,0)][Math.max (x,0)] += 1;
    				}
    			}
    		}
    	}
    	//Stars at (15,15) (antHill) and adds values to each position on the board which represents the minimum
    	//cost of reaching the point and also adds a direction to each point which represents which point was used
    	//to reach the next point
   		for (int y = 15;y<map.length;y++){
    		for (int x = 15;x<map[0].length;x++){
    			if (y != 15 && x!= 15){
					if (map [y-1][x-1] <= map [y-1][x]){
						if (map [y-1][x-1] <= map [y][x-1]){
							map [y][x] += map [y-1][x-1];
							directions [y][x] = "TL";
						}
						else{
							map [y][x] += map [y][x-1];
							directions [y][x] = "L";
						}
					}
					else{
						if (map [y-1][x] == map [y][x-1]){
							if (Math.random () > 0.5){
								map [y][x] += map [y-1][x];
								directions [y][x] = "T";
							}	
							else{
								map [y][x] += map [y][x-1];
								directions [y][x] = "L";	
							}
						}
						else if (map [y-1][x] < map [y][x-1]){
							map [y][x] += map [y-1][x];
							directions [y][x] = "T";
						}
						else{
							map [y][x] += map [y][x-1];
							directions [y][x] = "L";
						}
					}    			
    			}
    			else if (y == 15){
    				map [y][x] += map [y][x-1];
    				directions [y][x] = "L";
    			}
    			else if (x == 15){
    				map [y][x] += map [y-1][x];
    				directions [y][x] = "T";
    			}
    		}
    	}
    }
    //Overwrite the super's turnToTarget so that this function will use the smartAnt's target instead of using the
    //ant's target. Changes the ants direction so that it is moving towards the target
    public void turnToTarget (){
    	double x_dist = target.getX()-getX();
    	double y_dist = target.getY()-getY();
    	//checks if the target is directly above or below
    	if (x_dist == 0){
    		setHeading (y_dist > 0 ? 270 : 90);
    		return;
    	}
    	//Determines which quadrant the angle is in on the winding function and returns a value using atan
    	if (x_dist < 0){
    		x_dist *= -1;
    		if (y_dist <0){
    			y_dist *= -1;
     			setHeading (180 - Math.toDegrees (Math.atan (y_dist/x_dist)));
    		}
    		else{
    			setHeading (180 + Math.toDegrees (Math.atan (y_dist/x_dist)));
    		}
    	}
    	else{
    	  	if (y_dist <0){
    			y_dist *= -1;
     			setHeading (Math.toDegrees (Math.atan (y_dist/x_dist)));
    		}
    		else{
    			setHeading (360 - Math.toDegrees (Math.atan (y_dist/x_dist)));
    		}
    	}
    }
    //This function uses the 2-D Map and Directons arrays to create a path of least resistance that the ant will follow
    public void createPath (){
    	int y = map.length -35;
    	int x = map[0].length -35;
    	//Starts at point (y,x)(cake) and works backwards following the directions and adding each point to the array
		for (int i = path.length-1;i>=0;i--){
			path [i] = new Point (x,y+100);
			if (directions [y][x].equals ("TL")){//"TopLeft
				y -= 1;
				x -= 1;
			}
			else if (directions [y][x].equals ("T")){//Top
				y -= 1;
			}
			else if (directions [y][x].equals ("L")){//Left
				x -= 1;
			}
			else{//The value of directions at the cake point will be "", so this is where we break and set the first target
				target = path [hasCake() ? path.length -1 : i];
				targetCount = hasCake() ? path.length -1 : i;
				return;
			} 	
		}
    }
    //Overwrites the super's followPath so that the functino uses the smart ant's target,path, and targetCount variables
    //instead of the ant's.Checks if the ant has reached its target and then advances the target.
    public void followPath (){
   		if (hasCake()&&distance (getX(),target.getX(),getY(),target.getY()) < 5){
    		target = path [targetCount - 1];
    		targetCount --;
    	}
    	else if (distance (getX(),target.getX(),getY(),target.getY()) < 5){
    		//if ant has reached cake but there was no cake available then set targetCount to targetCount/2
    		targetCount =targetCount+1>path.length-1 || path [targetCount+1] == null ? targetCount-150 : targetCount +1;
    		target = path [targetCount];
    	}
    }
}
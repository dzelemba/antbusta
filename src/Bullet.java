//Bullet.java
//DusanZelembaba
//This file holds all the classes that represent the various types of bullets in the game
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

//This class represents a basic bullet that moves in a straight line and injures an ant if it comes into contact with it
public class Bullet {
	private double x_pos;//The x-coordinate of the bullet
	private double y_pos;//The y-coordinate of the bullet
	private double velocity;//the speed of th bullet
	private double heading;//The direction in which the bullet is travelling (0 = right)
	private double radius = 3;//the radius of the bullet
	private double power;//How much health the bullet will take away form the ant
	private Color color = Color.white;//The color of the bullet
	
	//The constructor that creates a new bullet with a (x,y) position, a velocity, a heading, and a power
    public Bullet(double xp,double yp,double v, double h, double p) {
    	x_pos = xp;
    	y_pos = yp;
		velocity = v;
		heading = h;
    	power = p;
    }
    //The constructor that creates a new bullet with a (x,y) position, a velocity, a heading, and a power, and a bullet Color
    public Bullet(double xp,double yp,double v, double h, double p,Color c) {
    	x_pos = xp;
    	y_pos = yp;
		velocity = v;
		heading = h;
    	power = p;
    	color =c;
    }
    //Return the x-coordiante of the bullet
    public double getX (){
    	return x_pos;
    }
    //Returns the y-coordiante of the bullet
    public double getY (){
    	return y_pos;
    }
    //Returns the radius of the bullet
    public double getRadius (){
    	return radius;
    }
    //Returnds the power of the bullet
    public double getPower (){
    	return power;
    }
    //Returnd the heading of the bullet
    public double getHeading (){
    	return heading;
    }
    //Returns the fadeCount of the bullet (Overwritten in the explosion and lightning classes)
    public int getFadeCount (){
    	return 25;
    }
    //Returns a Set of Points (Overwritten in the lightning class)
    public Set <Point> getPoints (){
    	return null;
    }
    //Returns an array of Bullets (Overwritten in PoisonGas)
    public Bullet [] getBullets (){
    	return null;
    }
    //Sets the radius of the bullet to amt
    public void setRadius (double amt){
    	radius = amt;
    }
    //Sets the heading of the bullet to amt
    public void setHeading (double amt){
    	heading = amt;
    }
    //Sets the velocity of the bullet to amt
    public void setVelocity (double amt){
    	velocity = amt;
    }
    //sets the power of the bullet to amt
    public void setPower (double amt){
    	power = amt;
    }
    //Changes the color of the bullet to c
    public void setColor (Color c){
    	color = c;
    }
    //Returns the ant being targeted (Overwritten in laserBullet class)
    public Ant getTarget (){
    	return null;	
    }
 	//draws the bullet onto g.
    public void drawBullet (Graphics g){
    	g.setColor (color);
		g.fillOval (new Double(x_pos-radius).intValue(),new Double(y_pos-radius).intValue()
			,(int)radius*2,(int)radius* 2);
		g.setColor (Color.black);
		g.drawOval (new Double(x_pos-radius).intValue(),new Double(y_pos-radius).intValue()
			,(int)radius*2,(int)radius* 2);
    }
    //Moves the bullet using the velocity and the heading
    public void move (){
    	x_pos += velocity * Math.cos (Math.toRadians (heading));
    	y_pos -= velocity * Math.sin (Math.toRadians (heading));
    }
}
//This class represents the laser that a laser turret fires. It is treated as a regualr bullet the reaches the target
//ant in one frame and then its power and speed is set to 0 so that it can fade away before being removed
class LaserBullet extends Bullet{
	private double target_x;//The x-coordinate of the target
	private double target_y;//The y-coordinate of the target
	private double turretX;//The x-coordinate of the turret
	private double turretY;//The y-coordinate of the turret
	private int fadeCount = 15;//How long the laser will fade
	
	//Constructor that creates a Bullet with a speed that reached the target in one frame
	public LaserBullet (double xp,double yp,double tx,double ty,double h, double p){
		super (xp,yp,Math.sqrt ((xp-tx)*(xp-tx) + (yp-ty)*(yp-ty)),h,p);
		
		target_x = tx;
		target_y = ty;
		turretX = xp;
		turretY = yp;
	}
	//returnd the fade count
	 public int getFadeCount (){
    	return fadeCount;
    }
   	//Draws a thickline from point (x1,y1) to (x2,y2) by drawing 'thickness*2-1' lines from the two points spreading outward
    public void drawThickLine (Graphics g,double x1,double y1,double x2,double y2,int thickness){
    	double angle = getHeading();//Gets the angle between the points
		double xMod = Math.cos (Math.toRadians(angle-90));//The amount the x-coordinate will change when the lines are being drawn
		double yMod = -Math.sin (Math.toRadians(angle-90));//The amount the y-coordinate will change when the lines are being drawn
		
		//Draws all the lines modifin the x and y coordianted by xMod and yMod
		for (double i = 0;i<thickness;i++){
			g.setColor (new Color (255,(int)(255-i*50),(int)(255-i*50),(int)(100+fadeCount*10)));
			g.drawLine ((int)(x1+i*xMod),(int)(y1+i*yMod),(int)(x2+i*xMod),(int)(y2+i*yMod));
			g.drawLine ((int)(x1-i*xMod),(int)(y1-i*yMod),(int)(x2-i*xMod),(int)(y2-i*yMod));
		}
    }
    //Draws a thcikline from the turret to the target
	public void drawBullet (Graphics g){
		drawThickLine (g,(int)turretX,(int)turretY,(int)target_x,(int)target_y,5);
		fadeCount --;
	}
}
//This class represents a Missile Bullet that is fired by a missile launcher. The bullet will follow its target and
//upon impact will create an explosino
class Missile extends Bullet{
	private Ant target;//The ant being targeted
	private Image missile;//The image of the missile
	private MainGame game;//The MainGame object
	
	//The constructor that creates a Bullet and also takes an Image and a MainGame object
	public Missile (double xp,double yp,double v, double h, double p,Ant t,Image m,MainGame g){
		super (xp,yp,v,h,p);
		
		missile = m;
		target = t;
		game = g;
	}
	//Returns the ant being tageted
	public Ant getTarget (){
		return target;
	}
	//Draws the bullet by rotating the missile image to correspond with the heading. Uses an affineTransform rotation
	public void drawBullet (Graphics g){
		Graphics2D g2D = (Graphics2D)g;
		AffineTransform saveXform = g2D.getTransform();
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians (-getHeading()+135),getX(),getY());
		g2D.transform(at);
		g2D.drawImage(missile,(int)getX()-7,(int)getY()-7,game);
		g2D.setTransform(saveXform);
    }
    //Turns the bullet so that it is moving towards the target
	public void turnToTarget (){
    	double x_dist = target.getX()-getX();
    	double y_dist = target.getY()-getY();
    	//If the target is directly above or below
    	if (x_dist == 0){
    		setHeading (y_dist > 0 ? 270 : 90);
    		return;
    	}
    	//Determines which quadrant the angle is in on the winding function and uses that to find the angle
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
    //Turns toward the target and then calls the super classes move method
    public void move (){
    	turnToTarget ();
    	super.move ();
    }
}
//This class represents an explosion made when a missile hits its target. The explosion hits everything within its radius
//and increases in size until its radius exceeds 30pixels.
class Explosion extends Bullet {
	//The constructor that creates a Bullet and sets the radius to 1
	public Explosion (double xp,double yp,double p){
		super (xp,yp,0,0,p);
		
		setRadius (1);
	}
	//Draws the explosiong by drawing two intercesting ovals and then drawing two smaller but indentical ovals
	//over the first two. The color is generated randomly to be somehwere between yellow and red
	public void drawBullet (Graphics g){
		setRadius (getRadius() + 2);
		g.setColor (new Color (255,(int)(Math.random ()*75+55),0,150));	
		g.fillOval ((int)(getX()-getRadius()/2),(int)(getY()-getRadius()),(int)(getRadius()/2*2),(int)(getRadius()*2));
		g.fillOval ((int)(getX()-getRadius()),(int)(getY()-getRadius()/2),(int)(getRadius()*2),(int)(getRadius()/2*2));	
		setRadius (getRadius() - 2);
		g.setColor (new Color (255,(int)(Math.random ()*75+180),0,150));
		g.fillOval ((int)(getX()-getRadius()/2),(int)(getY()-getRadius()),(int)(getRadius()/2*2),(int)(getRadius()*2));
		g.fillOval ((int)(getX()-getRadius()),(int)(getY()-getRadius()/2),(int)(getRadius()*2),(int)(getRadius()/2*2));	
		setRadius (getRadius() + 2);
	}
}
//This class represents a lightning bullet that is drawn similar to a recursive tree and uses each intersecting point
//in the tree as a bullet that can do damage
class Lightning extends Bullet {
	private double turretX;//The x-coordinate of the turret
	private double turretY;//The y-coordinate of the turret
	private int fadeCount = 15;//The length of the fade
	//A Map the points a Point to an ArrayList of Points. This represents the branches of the lighting pointing to where
	//They will branch off
	private Map <Point,ArrayList <Point>> lightningMap = new HashMap <Point,ArrayList <Point>> ();
	
	//The constructor that creates a bullet and sets turret/target coordiantes and creates the lightningMap
	public Lightning (double xp,double yp,double tx,double ty,double h, double p){
		super (xp,yp,0,h,p);
		
		turretX = xp;
		turretY = yp;
		createLightning (turretX,turretY,getHeading (),7);
	}
	//Returns the fadeCount
    public int getFadeCount (){
    	return fadeCount;
    }
    //returns a Set containing the Keys of the lightningMap
    public Set <Point> getPoints (){
    	return lightningMap.keySet ();
    }
    //Returns the angle at which the target is in regards to the turret
    public double getAngle (double x1,double y1,double x2,double y2){
    	double x_dist = x2-x1;
    	double y_dist = y2-y1;
    	//If the target is directly above or below
    	if (x_dist == 0){
    		return y_dist > 0 ? 270 : 90;
    	}
    	if (x_dist < 0){
    		x_dist *= -1;
    		if (y_dist <0){
    			y_dist *= -1;
     			return 180 - Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    		else{
    			return 180 + Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    	}
    	else{
    	  	if (y_dist <0){
    			y_dist *= -1;
     			return Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    		else{
    			return 360 - Math.toDegrees (Math.atan (y_dist/x_dist));
    		}
    	}
    }
    //Draws a thickline from point (x1,y1) to (x2,y2) by drawing 'thickness*2-1' lines from the two points spreading outward
    public void drawThickLine (Graphics g,double x1,double y1,double x2,double y2,int thickness){
    	double angle = getAngle (x1,y1,x2,y2);//Gets the angle between the points
		double xMod = Math.cos (Math.toRadians(angle-90));//The amount the x-coordinate will change when the lines are being drawn
		double yMod = -Math.sin (Math.toRadians(angle-90));//The amount the y-coordinate will change when the lines are being drawn
		
		//Draws all the lines modifin the x and y coordianted by xMod and yMod
		for (double i = 0;i<thickness;i++){
			int alpha = fadeCount >= -10 ? 100 + fadeCount * 10 : 0;
			g.setColor (new Color (255,255,(int)(255-i*50), alpha));
			g.drawLine ((int)(x1+i*xMod),(int)(y1+i*yMod),(int)(x2+i*xMod),(int)(y2+i*yMod));
			g.drawLine ((int)(x1-i*xMod),(int)(y1-i*yMod),(int)(x2-i*xMod),(int)(y2-i*yMod));
		}
    }
    //Creates the lighting recursively by randomly determining the number of branches and the angle of each branch coming
    //from each prevoius branch
    public void createLightning (double x,double y,double angle,int stage){	
    	int numBranches = (int)(Math.random()*2+1);//randomly choose number of branches between (1-2)
       	ArrayList <Point> branches = new ArrayList <Point> ();//An arrayList of all branches extending from this bracnh
		if (stage != 0){//If we haven't reached our final stage of extending the lightning
			for (int i = 0;i<numBranches;i++){//Create a new random bracnh 'numBranches' times
				double angleMod = Math.random ()*100 -50 + angle;//random angle
				double length = 15;//Constant length
				//add the connecting branch to the ArrayList
				branches.add (new Point ((int)(x+length*Math.cos (Math.toRadians (angleMod))),(int)(y-length*Math.sin (Math.toRadians (angleMod)))));
				//Recursice call to extend each branch
				createLightning (x+length*Math.cos (Math.toRadians (angleMod)),y-length*Math.sin (Math.toRadians (angleMod)),angleMod,stage-1);
			}	
		}
		//Once all branches from this point have been made add it to the Map
		lightningMap.put (new Point ((int)x,(int)y),branches);
    }
    //Draws the bullet using the lightingMap
	public void drawBullet (Graphics g){
		for (Point p : lightningMap.keySet ()){//Iterate through each point
			ArrayList <Point> connections = lightningMap.get (p); //Get all the connections
			
			for (int i= 0;i<connections.size();i++){
				Point c = connections.get (i);
				drawThickLine (g,p.getX(),p.getY(),c.getX(),c.getY(),2);//draw a line to each connection
			}
		}
		fadeCount-=2;//decrease the fadeCount
	}
}
//This class represents poison gas.It treated as 8 bullets that have 0 power but poison ants when they collide
class PoisonGas extends Bullet{
	private Bullet [] gas = new Bullet [8];
	//The constructor that creates a bullet with poer of 0
	public PoisonGas (double xp,double yp,double bS,double h){
		super (xp,yp,bS,h,0);
		
		//Initialize the 8 bullets and their headings
		for (int i = 0;i<gas.length;i++){
			gas [i] = new Bullet (xp,yp,bS,h+((i-4)*10),0,Color.green);
		}
	}
	//Returns an array of the Bullets
	public Bullet [] getBullets (){
		return gas;
	}
	//Draws the poison gas
	public void drawBullet (Graphics g){
		g.setColor (new Color (0,255,0,200));
		g.drawArc ((int)getX()-35,(int)getY()-35,70,70,(int)getHeading()-45,90);
	}
	//Moves the curretn Bullet and the 8 other bullets
	public void move (){
		super.move ();
		for (int i = 0;i<gas.length;i++){
			gas [i].move ();
		}
	}
	
}
//This class represents an ice bullet.It acts identical to a reqular bullet except it slows enemies down upon impact
class IceBullet extends Bullet{
	//The constructor that creates a new bullet with a (x,y) position, a velocity, a heading, and a power
    public IceBullet(double xp,double yp,double v, double h, double p) {
 		super (xp,yp,v,h,p);
    }
    public void drawBullet (Graphics g){
    	g.setColor (Color.blue);
		g.fillOval (new Double(getX()-getRadius()).intValue(),new Double(getY()-getRadius()).intValue()
			,(int)getRadius()*2,(int)getRadius()* 2);
		g.setColor (Color.black);
		g.drawOval (new Double(getX()-getRadius()).intValue(),new Double(getY()-getRadius()).intValue()
			,(int)getRadius()*2,(int)getRadius()* 2);
    }
}
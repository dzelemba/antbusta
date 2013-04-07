//Player.java
//DusanZelembaba
//This class represents the user who is playing the game. It implements MouseListener,and MouseMotionLIstener to read
//input from the user.
import java.util.*;
import java.awt.event.*;
import java.awt.*;

class Player implements MouseListener,MouseMotionListener{
	private int score;//The player's curretn score
	private int money;//The player's curretn money
	private ArrayList <Turret> guns;//An arrayList of turrets this player has created
	private AntHill antHill;//The antHill object that creates the ants in the game
	private MainGame game;//The MainGame object
	private boolean addingTurret;//True if player is adding a turret;false otherwise
	private boolean goodSpot = false;//True if this spot is suitable for a turret;false otherwise
	private int placementX;//the x-coordinate of the turret being palced
	private int placementY;//the y-coordiante of the turret being placed
	private Image base;//Image of the basic turret base
	private Image gun;//Image of the basic turret gun
	private Turret selected;//The turret that is selected
	private Ant selected2;//Tha ant that is being selected
	//The cost of a basic turret (increases as more turrets are introduced)
	private int basicTurretCost = Integer.parseInt (Turret.getStats().get ("Basic Turret") [0]);
	
	//The constructor that creates a player at level 1 staring with money 'm'.
	public Player (int m,AntHill a,MainGame g){
		score = 0;
		money = m;
		guns = new ArrayList <Turret> ();
		antHill = a;
		game = g;
		base = Toolkit.getDefaultToolkit ().getImage ("resources/Pictures/Turrets/turretBase.png");//20X20
    	gun = Toolkit.getDefaultToolkit ().getImage ("resources/Pictures/Turrets/turretGun.png");//20X20
	}
	//Returns an ArratList of this player's turrets
	public ArrayList <Turret> getGuns (){
		return guns;
	}
	//Return the players  curretn score
	public int getScore (){
		return score;
	}
	//Return the amount of money this player has
	public int getMoney (){
		return money;
	}
	//Returns the cost of a basic turret
	public int getBasicTurretCost (){
		return basicTurretCost;
	}
	//Is called if the mouse if moved
	public void mouseMoved (MouseEvent e){
		//If we are adding a turret then check if the position is on the game area and far enough away from the cake and
		//the antHill and not overlapping any other turret
		if (addingTurret){
			placementX = e.getX ();
			placementY = e.getY ();
			if (e.getX()>MainGame.getBottomRight().getX()-125&&e.getX()<MainGame.getBottomRight().getX()&&
				e.getY()>MainGame.getBottomRight().getY()-125&&e.getY()<MainGame.getBottomRight().getY())
					goodSpot = false;
			else if	(e.getX()>MainGame.getTopLeft().getX()&&e.getX()<MainGame.getTopLeft().getX()+125&&
				e.getY()>MainGame.getTopLeft().getY()&&e.getY()<MainGame.getTopLeft().getY()+125)
					goodSpot = false;
			else if (e.getX()<MainGame.getTopLeft().getX()||e.getX()>MainGame.getBottomRight().getX()||
					e.getY()<MainGame.getTopLeft().getY()||e.getY()>MainGame.getBottomRight().getY())
						goodSpot = false;
			else
				goodSpot = true;
			for (int i = 0;i<guns.size();i++){
				Turret t = guns.get (i);
				if (distance (e.getX(),t.getX(),e.getY(),t.getY()) < 40)
					goodSpot = false;
			}
		}
		//Check if the mouse is on a button and set the rollover to true
		else{
			MyButton b = checkButtons (e.getX(),e.getY());
			if (b != null){
				b.setRollOver (true);
			}
		}
	}
	//Is called when the mouse is pressed
	public void mousePressed(MouseEvent e){
		//If we are adding a turret then cancel if the right button is clicked, or add the turret if its a goodSpot
		if (addingTurret){
			if (e.getButton() == MouseEvent.BUTTON3){
				addingTurret = false;
				goodSpot = false;
			}
			else if (goodSpot){
				selected = addTurret (e.getX(),e.getY());
				addingTurret = false;
				goodSpot = false;
				money -= basicTurretCost;
				basicTurretCost = guns.size()*guns.size()*25;
			}
		}
		//Check if a button was pressed
		else{
			MyButton b = checkButtons (e.getX(),e.getY());//Gets the pressed button
			if (b != null){
				if (b.getTask().equals ("Basic Turret")){
					if (basicTurretCost<= money)
						addingTurret = true;//Begin adding a turret
				}
				//If the button is one of checkBoxes
				else if (b.getTask().contains ("Target1")){
					if (!b.getChecked())
						selected.getCheckBoxes () [0].setChecked (false);
						selected.getCheckBoxes () [1].setChecked (false);
						b.setChecked (true);
						selected.setTargetOrder1 (b.getTask());
				}
				else if (b.getTask().contains ("Target2")){
					if (!b.getChecked())
						selected.getCheckBoxes () [2].setChecked (false);
						selected.getCheckBoxes () [3].setChecked (false);
						//selected.getCheckBoxes () [4].setChecked (false);
						b.setChecked (true);
						selected.setTargetOrder2 (b.getTask());
				}
				//Pause button
				else if (b.getTask().contains ("Pause")){
					game.setPause (!game.getPaused ());
				}
				//The rest of the buttons are for upgrading
				else if (Integer.parseInt (Turret.getStats().get (b.getTask()) [0]) <= money){
					selected.upgrade (b.getTask ());
					money -= Integer.parseInt (Turret.getStats().get (b.getTask()) [0]);
				}
			}
			//Check to see if something (turret or ant) is being selected
			else{
				selected = null;
				selected2 = null;
				for (int i = 0;i<guns.size();i++){
					Turret t = guns.get (i);
					if (distance (e.getX(),t.getX(),e.getY(),t.getY()) < 10)
						selected = t;
				}
				for (int i = 0;i<antHill.getAnts().size();i++){
					Ant t = antHill.getAnts().get (i);
					if (distance (e.getX(),t.getX(),e.getY(),t.getY()) < 10)
						selected2 = t;
				}
			}
		}
	}
	//Unused MouseListener funcitons
	public void mouseClicked(MouseEvent e){		
	}
	public void mouseDragged (MouseEvent e){
	}
	public void mouseReleased(MouseEvent e){
	}
	public void mouseEntered(MouseEvent e){
	}
	public void mouseExited(MouseEvent e){
	}
	//Returns the distance from (x1,y1) to (x2,y2)
	public double distance (double x1,double x2,double y1,double y2){
    	return Math.sqrt ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    //Returns what button was pressed;returns null if no button was presesd
    public MyButton checkButtons (int x,int y){
    	MyButton [] b = game.getButtons ();
    	//Checks the MainGame buttons (pause,addTurret)
    	for (int i = 0;i<b.length;i++){
    		b[i].setRollOver (false);
    		if (x > b[i].getX()&&x<b[i].getX()+b[i].getWidth()&&y>b[i].getY()&&y<b[i].getY()+b[i].getHeight()){
  		  		return b[i];
    		}
    	}
    	//Check upograde and checkBox buttons
    	if (selected != null){
    		for (int i = 0;i<selected.getUpgrades().size();i++){
    			MyButton b2 = selected.getUpgrades().get(i);
    			b2.setRollOver (false);
    			if (x > b2.getX()&&x<b2.getX()+b2.getWidth()&&y>b2.getY()&&y<b2.getY()+b2.getHeight()){
    				return b2;
    			}
    		}
   			for (int i = 0;i<selected.getCheckBoxes().length;i++){
    			CheckBox b2 = selected.getCheckBoxes() [i];
    			b2.setRollOver (false);
    			if (x > b2.getX()&&x<b2.getX()+b2.getWidth()&&y>b2.getY()&&y<b2.getY()+b2.getHeight()){
    				return b2;
    			}
    		}
    	}
    	return null;
    }
    //Adds a turret at position (x,y) by adding it to 'guns' and starts the thread.Retrurns the turret being added
	public Turret addTurret (int x, int y){
		Turret t = new Turret (x,y,3,100,2,1,base,gun,antHill,game);
		guns.add (t);
		t.startTurret ();
		return t;
	}
	//Draws where the turret is being added 
	public void drawTurretPlacement (Graphics g,int x,int y){
		g.drawImage (base,x-10,y-10,game);
		g.setColor (new Color (20,20,20,50));
		g.fillOval (x-100,y-100,200,200);
		if (goodSpot)
			g.setColor (Color.green);
		else
			g.setColor (Color.red);
		g.drawRect (x-11,y-11,22,22);
	}
	//Draws the turret placememtn if the player is adding a turet
	public void placeTurret (Graphics g){
		if (addingTurret){
			drawTurretPlacement (g,placementX,placementY);
		}
	}
	//Draws either the ant or turret that is currently selected
	public void drawSelected (Graphics g){
		if (selected != null){//If a turret is selected
			g.setColor (new Color (20,20,20,50));
			g.fillOval ((int)(selected.getX()-selected.getRange()),(int)(selected.getY()-selected.getRange()),(int)selected.getRange()*2,(int)selected.getRange()*2);
			g.setColor (Color.black);
			g.drawString (selected.getName (),(int)MainGame.getTopLeft().getX()+110,(int)MainGame.getBottomRight().getY()+13);
			//draws the upgrade buttons
			for (int i = 0;i<selected.getUpgrades().size();i++){
    			MyButton b2 = selected.getUpgrades().get(i);
				b2.drawButton (g,game,0);
    		}
    		//Draws the checkboxes
    		g.drawString ("Target:",(int)MainGame.getTopLeft().getX()+290,(int)MainGame.getBottomRight().getY()+15);
    		for (int i = 0;i<selected.getCheckBoxes().length;i++){
    			selected.getCheckBoxes () [i].drawButton (g,game);
    		}
    		g.drawLine ((int)MainGame.getTopLeft().getX()+280,(int)MainGame.getBottomRight().getY()+10,(int)MainGame.getTopLeft().getX()+280,(int)MainGame.getBottomRight().getY()+90);
    		g.drawLine ((int)MainGame.getTopLeft().getX()+290,(int)MainGame.getBottomRight().getY()+50,(int)MainGame.getTopLeft().getX()+380,(int)MainGame.getBottomRight().getY()+50);
    		//draw the turret's stats
    		selected.drawStats (g,(int)MainGame.getTopLeft().getX()+180,(int)MainGame.getBottomRight().getY()+25);
		}
		else if (selected2 != null){//If an ant is selected
			selected2.drawStats (g,(int)MainGame.getTopLeft().getX()+110,(int)MainGame.getBottomRight().getY()+35);
		}
	}
	//Iterates through each turets, moves all their bullets and checks if any collisions are occuring
	public void handleBullets (){
		for (int i = 0;i<guns.size();i++){
			Turret t = guns.get (i);
			t.moveBullets ();
			int amt = t.checkCollisions ();
			money += amt;
			score += amt;
		}
	}
}
//AntHill.java
//DusanZelembaba
//This class will control all the ants on the screen and keep a constant number of ants in the game.
import java.util.*;

class AntHill{
	private double x_pos;//the x-coordinate of the ant hill
	private double y_pos;//the y-coordinate of the ant hill
	private ArrayList <Ant> ants;//an arrayList of all the ants in the game
	private int maxAnts;//the maximum number of ants in the game at once
	private int level = 1;//The current level the player has reahced
	private int cakeGone = 0;//The number of cake slices not on the plate
	private int cakeStolen = 0;//The number of cake slices returned to the anthill
	private int antsKilled = 0;//The number of ants that have died
	private MainGame game;//The MainGame object
	private int antDelay = 50;//The delay between the introduction of each ant (cpu cycles)
	
	//The constructor that creates a new anthill at position (x,y)and sets the max number of ants at numAnts
	public AntHill (double x,double y,int numAnts,MainGame g){
		x_pos = x;
		y_pos = y;
		ants = new ArrayList <Ant> ();
		maxAnts = numAnts;
		game = g;
	}
	//returns an arrayList of all the ants on the screen
	public ArrayList <Ant> getAnts (){
		return ants;
	}
	//Returns the level the user has reached
	public int getLevel (){
		return level;
	}
	//removes and returns an ant from the arrayList at position index, also updates the level based on number of ants killed
	public Ant removeAnt (int index){
		antsKilled ++;
		level = antsKilled/6+1;
		return ants.remove (index);
	}
	//returns the variable cakeGone
	public int getCakeGone (){
		return cakeGone;
	}
	//returns the variable cakeStolen
	public int getCakeStolen (){
		return cakeStolen;
	}
	//returns the number of ants killed
	public int getAntsKilled (){
		return antsKilled;
	}
	//sets the cakeGone to amt
	public void setCakeGone (int amt){
		cakeGone = amt;
	}
	//sets the cakeStolen to amt
	public void setCakeStolen (int amt){
		cakeStolen = amt;
	}
	//adds an ant to the game
	public void addAnt (int health,double speed){
		Ant a = new Ant (x_pos,y_pos,health,speed,this,game);
		ants.add (a);
		a.createPath ();
	}
	//adds a shyAnt to the game
	public void addShyAnt (int health,double speed,int range){
		Ant a = new ShyAnt (x_pos,y_pos,health,speed,range,this,game);
		ants.add (a);
		a.createPath();
	}
	//adds a smartAnt to the game
	public void addSmartAnt (int health,double speed){
		Ant a = new SmartAnt (x_pos,y_pos,health,speed,this,game);
		ants.add (a);
		a.createPath();
	}
	//adds an ant if there is less than 'maxAnts' in the game.5%chance a smartAnt is added,65% chance for an ant and
	//a 30% a shayAnt is added
	public void addAnts (){
		if (ants.size () < maxAnts){
			if (antDelay > 50){
				double type = Math.random ();
				//the health of the ant is related to the level using the formula: health = 0.25*level*level
				if (type > 0.95){
					addSmartAnt ((int)(0.25*level*level + 1),0.7);
				}
				else{
					addAnt ((int)(0.25*level*level + 1),0.7);
				}/*
				else{
					// ShyAnts are kind of dumb... If you put a few turrents by the Ant Hill 
					// they just keep turning around
					//addShyAnt ((int)(0.25*level*level + 1),0.7,100);
				}*/
				antDelay = 0;
			}
		}
		antDelay++;
	}
	//Moves each ant by calling each ones move function.
	public void moveAnts (){
		for (int i = ants.size()-1;i>=0;i--){
			Ant a = ants.get (i);
			 if (a.move ()){
			 	ants.remove (i);
			 	antsKilled ++;
				level = antsKilled/6+1;
			 }
		}
	}
}
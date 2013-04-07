//MyButton.java
//DusanZelembaba
//This file holds the classes that represents buttons in the game they allow the user to do things such as add new
//turrets, upgrade turrets, and pause/resume the game

import java.awt.*;

//This class represents a button that performs an action when clicked on and draws something when the mouse rolls over  it
public class MyButton {
	private int x;//The top left corner x-coordinate
	private int y;//the top left corner y-coordinate
	private int width= 25;//The width of the button
	private int height = 25;//the height of the button
	private boolean visible = true;//True if the button is visible;false otherwise
	private String text ="";//The text displayed on the button
	private Image picture;//The picture drawn on the button
	private String task;//The task of this button
	private boolean rollover;//true is the mouse is currently on the button;false otherwise
	private boolean rolloverPic = true;//true if there is something to be drawn when rollover is true
	
	//The constructor that creates a blank button with a task
    public MyButton(int x,int y,String task) {
    	this.x = x;
    	this.y = y;
    	this.task = task;
    }
    //The constructor that create a button with text and a task
    public MyButton(int x,int y,String t,String task) {
    	this.x = x;
    	this.y = y;
    	text = t;
    	this.task = task;
    }
    //The constructor that creates a button with an image and a task
    public MyButton(int x,int y,Image p,String task) {
    	this.x = x;
    	this.y = y;
    	picture = p;
    	this.task = task;
    }
    //Sets the rolloverPic to true or false
    public void setRolloverPic (boolean s){
    	rolloverPic = s;
    }
    //Returns checked (Overwritten in CheckBox)
    public boolean getChecked (){
    	return true;
    }
    //sets check to s (Overwritten in CheckBox)
    public void setChecked (boolean s){
    }
    //returns x
    public int getX(){
    	return x;
    }
    //returns y
    public int getY(){
    	return y;
    }
    //returns the width
    public int getWidth (){
    	return width;
    }
    //return the height
    public int getHeight (){
    	return height;
    }
    //Returns the task
    public String getTask (){
    	return task;
    }//Sets the size of the button to width w and height h
    public void setSize (int w,int h){
    	width = w;
    	height = h;
    }
    //Sets the variable visible to s
    public void setVisible (boolean s){
    	visible = s;
    }
    //Sets this buttons task to t
    public void setTask (String t){
    	task = t;
    }
    //Sets rollover to s
    public void setRollOver (boolean s){
    	rollover = s;
    }
    //Returns rollover
    public boolean getRollOver (){
    	return rollover;
    }
    //Draws the rollover picture if there is one (Stats of a turret)
    public void drawRollOver (Graphics g,int cost){
    	g.setColor (new Color (20,20,20,100));
    	g.fillRect (x+10,(int)MainGame.getBottomRight().getY()-87,200,84);
    	g.setColor (new Color (210,210,210));
    	g.drawString ("Name:      "+task,x+10,(int)MainGame.getBottomRight().getY()-75);
    	if (task.equals ("Basic Turret"))
    		g.drawString ("Cost:      "+cost,x+10,(int)MainGame.getBottomRight().getY()-62);
    	else
    		g.drawString ("Cost:      "+Turret.turretStats.get (task) [0],x+10,(int)MainGame.getBottomRight().getY()-62);
		g.drawString ("Fire Rate:  "+Turret.getStats().get (task) [1],x+10,(int)MainGame.getBottomRight().getY()-49);
		g.drawString ("Range:       "+Turret.getStats().get (task) [2],x+10,(int)MainGame.getBottomRight().getY()-36);	   
		g.drawString ("Speed:       "+Turret.getStats().get (task) [3],x+10,(int)MainGame.getBottomRight().getY()-23);
		g.drawString ("Damage:   "+Turret.getStats().get (task) [4],x+10,(int)MainGame.getBottomRight().getY()-10);
    }
    //Draws the button onto g
    public void drawButton (Graphics g,MainGame obs,int basicTurretCost){
    	if (visible){
	    	if (rollover){
	    		g.setColor (Color.white);
	    		g.fillRect (x,y,width,height);
	    		g.setColor (Color.black);
	    		g.drawRect (x,y,width,height);
	    		if (rolloverPic)
	    			drawRollOver (g,basicTurretCost);
	    	}
	    	else{
	    		g.setColor (Color.black);
	    		g.drawRect (x,y,width,height);
	    	}
	    	if (picture == null){
	    		g.setColor (Color.black);
	    		g.drawString (text,x+5,y+height/2);
	    	}
	    	else{
	    		g.drawImage (picture,x+(width-picture.getWidth(obs))/2,y+(height-picture.getHeight (obs))/2,obs);
	    	}
    	}
    }
}
//This class represents a check box that has two states: checked and unchecked; each state doing a different function
class CheckBox extends MyButton{
	private boolean checked = false;//True if this box is checked;false otherwise
	private String name;//The label of this check bos
	
	//The constructor that creates a button and sets its size to 10x10
	public CheckBox (int x,int y,String task,String n){
		super (x,y,task);
		
		name = n;
		super.setSize (10,10);
	}
	//returns if its checked or not
	public boolean getChecked (){
		return checked;
	}
	//sets if the button is checked or not
	public void setChecked (boolean c){
		checked = c;
	}
	//draws the button based on whether it is checked or not
	public void drawButton (Graphics g,MainGame obs){
		g.setColor (Color.black);
		if (!checked){
			g.drawRect (super.getX(),super.getY(),super.getWidth(),super.getHeight());
		}
		else{
			g.setColor (new Color (150,150,150));
			g.fillRect (super.getX(),super.getY(),super.getWidth(),super.getHeight());
		}
		g.setColor (Color.black);
		g.drawString (name,super.getX()+15,super.getY()+10);
	}
}
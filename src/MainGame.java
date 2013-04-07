//MainGame.java
//DusanZelembaba
//This class will be the one frame onto which everything is drawn.It will combine
//all the other classes and put them together into a playable game.

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class MainGame extends JFrame implements Runnable{
	private final int STARTMONEY = 25000;//Money user starts with
	private int MaxWidth;//The width of the JFrame
	private int MaxHeight;//The height of the JFrame
	private Image dbImage;//Double buffer image
	private Graphics dbg;//Double buffer graphics 
	private Image background;//The image of the background
	private Image title;//The title of the game (Ant Buster 2)
	private Image gOver;//The title of the game over screen (Game Over)
	private Image [] cake = new Image [9];//An array of all the images of the cake
	private int boardWidth;//The width of the game area.
	private int boardHeight;//The height of the game area.
	private static Point topLeft;//The top left corner of the game area
	private static Point bottomRight;//The bottom right corner of the game area.
	private AntHill antHill;//The antHill in the game
	private Player user;//The person who is playing the game.
	private Thread th;//The thread that runs this class.
	private MyButton addTurret;//The button that adds a basic turret
	private MyButton pause;//The button that pauses the game.
	private boolean paused = false;//True if game is paused,False otherwise
	private boolean gameOver = false;//Ture if the game is over,False otherwise
	private Image turretPic;//The image for the addTurret button.
	
	//The constructor. Initializes the global variables, adds Mouse listeners and
	//initializes the JFrame.
	public MainGame ()throws IOException{
		super("Ant Busta 2");
		
		Turret.initStats ();
		setLayout (null);
		MaxHeight = 601;
		MaxWidth = 502;
		boardWidth = 502;
		boardHeight = 401;
		topLeft = new Point ((MaxWidth - boardWidth)/2,(MaxHeight - boardHeight)/2);
		bottomRight = new Point ((MaxWidth + boardWidth)/2,(MaxHeight + boardHeight)/2);
		antHill = new AntHill (topLeft.getX()+15,topLeft.getY()+15,6,this);
		user = new Player (STARTMONEY,antHill,this);
		addMouseListener (user);
		addMouseMotionListener (user);
	//	addKeyListener (user);
		background = new ImageIcon ("resources/Pictures/background.png").getImage ();
		title = new ImageIcon ("resources/Pictures/title.png").getImage ();
		gOver = new ImageIcon ("resources/Pictures/gameOver.png").getImage ();
		for (int i = 0;i<9;i++){
			cake [i] = new ImageIcon ("resources/Pictures/Cake/"+i+"Gone.png").getImage ();
		}
		turretPic = new ImageIcon ("resources/Pictures/Turrets/addTurret.png").getImage ();//.getScaledInstance(100,100,Image.SCALE_SMOOTH);
		addTurret = new MyButton ((int)topLeft.getX(),(int)bottomRight.getY()+1,turretPic,"Basic Turret");
		addTurret.setSize (100,100);
		pause = new MyButton ((int)bottomRight.getX()-51,(int)bottomRight.getY()+49,"Pause","Pause");
		pause.setSize (50,50);
		pause.setRolloverPic (false);
		setSize (MaxWidth,MaxHeight);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setVisible (true);
	}
	//Returns the bottomRight corner of the game area.
	public static Point getBottomRight (){
		return bottomRight;
	}
	//Returns the top left corner of the game area.
	public static Point getTopLeft (){
		return topLeft;
	}
	//Return the current player.
	public Player getPlayer (){
		return user;
	}
	//Returns the addTurret and pause buttons in an array.
	public MyButton [] getButtons (){
		MyButton [] btns = new MyButton [2];
		btns [0] = addTurret;
		btns [1] = pause;
		return btns;
	}
	//Pauses/unpauses the game
	public void setPause (boolean s){
		paused = s;
	}
	//Returns if the game is paused or not.
	public boolean getPaused (){
		return paused;
	}
	//dealys the current thread by 'len' milliseconds.
	public void delay (long len){
		try{
		    th.sleep (len);
		}
		catch (InterruptedException ex){
		    System.out.println(ex);
		}
    }
    //Paints onto the JFrame
	public void paint(Graphics g){
	  	if (dbImage == null){//make off screen image
	    	dbImage = createImage (getWidth (),getHeight());
	    	dbg = dbImage.getGraphics ();
		}
		if (!gameOver){//Game Screen
			//draw on image instead
			dbg.setColor (new Color (240,240,240));
			dbg.fillRect (0,0,MaxWidth,MaxHeight);
			dbg.drawImage (title,(MaxWidth-title.getWidth (this))/2,((int)topLeft.getY()-title.getHeight(this))-10,this);
			dbg.drawImage (background,(MaxWidth-boardWidth)/2,(MaxHeight-boardHeight)/2,this);
			dbg.drawImage (cake [antHill.getCakeGone ()],(int)bottomRight.getX()-72,(int)bottomRight.getY()-71,this);
			for (int i = 0;i<antHill.getAnts().size();i++){
				Ant a = antHill.getAnts ().get (i);
				a.drawAnt (dbg);
			}
			for (int i = 0;i<user.getGuns().size();i++){
				Turret t = user.getGuns ().get (i);
				t.drawTurret (dbg);
			}
			user.placeTurret (dbg);
			addTurret.drawButton (dbg,this,user.getBasicTurretCost());
			pause.drawButton (dbg,this,0);
			user.drawSelected (dbg);
			dbg.setColor (Color.black);
			//Stats int bottom right corner
			dbg.drawRect ((int)bottomRight.getX()-101,(int)bottomRight.getY()+1,50,48);
			dbg.drawString ("Money",(int)bottomRight.getX()-97,(int)bottomRight.getY()+14);
			dbg.drawString (""+user.getMoney(),(int)bottomRight.getX()-95,(int)bottomRight.getY()+30);
			dbg.drawRect ((int)bottomRight.getX()-51,(int)bottomRight.getY()+1,50,48);
			dbg.drawString ("Score",(int)bottomRight.getX()-47,(int)bottomRight.getY()+14);
			dbg.drawString (""+user.getScore(),(int)bottomRight.getX()-45,(int)bottomRight.getY()+30);
			dbg.drawRect ((int)bottomRight.getX()-101,(int)bottomRight.getY()+49,50,48);
			dbg.drawString ("Level",(int)bottomRight.getX()-95,(int)bottomRight.getY()+62);
			dbg.drawString (""+antHill.getLevel(),(int)bottomRight.getX()-90,(int)bottomRight.getY()+75);
			dbg.drawRect ((int)bottomRight.getX()-51,(int)bottomRight.getY()+49,50,48);
			if (paused){
				dbg.setColor (new Color (20,20,20,100));
				dbg.fillRect ((int)topLeft.getX(),(int)topLeft.getY(),(int)bottomRight.getX()-(int)topLeft.getX(),(int)bottomRight.getY()-(int)topLeft.getY());
			}
			g.drawImage (dbImage,0,0,this);
		}
		else{//Game Over screen
			dbg.setColor (new Color (240,240,240));
			dbg.fillRect (0,0,MaxWidth,MaxHeight);
			dbg.drawImage (gOver,(MaxWidth-gOver.getWidth (this))/2,((int)topLeft.getY()-gOver.getHeight(this))-10,this);
			dbg.setColor (Color.black);
			dbg.drawString ("End Game Stats: ",200,200);
			dbg.drawString ("Score: "+user.getScore(),200,220);
			dbg.drawString ("Ants Killed: "+antHill.getAntsKilled (),200,233);
			g.drawImage (dbImage,0,0,this);
		}
	}
	//Starts the thread
    public void startGame (){
    	if (th == null){
    		th = new Thread (this);
    		th.start ();
    	}
    }
    //The main game loop
	public void run (){
		while (true){
			//while the game isn't over
			while (antHill.getCakeStolen () < 8){
				if (paused){
					repaint ();
					while (paused){
						delay (100);
					}
				}
				delay (20);
				repaint ();
				user.handleBullets ();
				antHill.moveAnts ();
				antHill.addAnts ();
			}
			gameOver = true;
			repaint ();
		}
	}
	
	
	//Main method. Creates a mainGame and starts the thread
	public static void main(String[]args)throws IOException{
		MainGame main = new MainGame ();
		main.startGame ();
	}
}

package com.ankitkumar.buildler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sun.audio.AudioPlayer;


public class MainClass extends JPanel implements Runnable,KeyListener,MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BufferedImage background,playerLeft,playerRight,leftKick,rightKick;
	public static final int HEIGHT=640,WIDTH=360;
	public static MainClass builder;
	public static JFrame frame;
	public Clip clipBack,clipGOver;
	public boolean running,gameOver=false,kick=false;
	public boolean leftClick=false,rightClick=false;
	public Rectangle player,progress;
	public ArrayList<Rectangle> pillars;
	public ArrayList<Rectangle> balcony;
	public ArrayList<Color> colors;
	public int pos=265,score=0,count=0,speed=0,time=400,backPos=0;
	public Random random;
	
	public MainClass(){
		player = new Rectangle(50,50);
		progress=new Rectangle(280,30);
		frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle("Droping Box by Ankit Kumar");
        setFocusable(true);
		frame.setVisible(true);
		frame.add(this);
        addKeyListener(this);
        addMouseListener(this);
        
       
        random = new Random();
        pillars = new ArrayList<Rectangle>();
        balcony = new ArrayList<Rectangle>();
        colors = new ArrayList<Color>();
        addPillar(true);
        addBalcony(true); 
        try {
			background=ImageIO.read(getClass().getResourceAsStream("/background.png"));
			playerLeft=ImageIO.read(getClass().getResourceAsStream("/player-left.png"));
			playerRight=ImageIO.read(getClass().getResourceAsStream("/player-right.png"));
			rightKick=ImageIO.read(getClass().getResourceAsStream("/player-right-kick.png"));
			leftKick=ImageIO.read(getClass().getResourceAsStream("/player-left-kick.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
        	clipBack = AudioSystem.getClip();
			AudioInputStream is = AudioSystem.getAudioInputStream(AudioPlayer.class.getResource("/bensound-background.wav"));
			clipBack.open(is);
			clipBack.start();
			clipBack.loop(5);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        repaint();
        timer();
	}
	
	public static void main(String[] args){
		builder = new MainClass();
		Thread thread = new Thread(builder);
		thread.start();
	}
	
	public void whackSound(){
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream is = AudioSystem.getAudioInputStream(AudioPlayer.class.getResource("/whack.wav"));
			clip.open(is);
			clip.start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void GameOverSound(){
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream is = AudioSystem.getAudioInputStream(AudioPlayer.class.getResource("/game-over.wav"));
			clip.open(is);
			clip.start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void timer(){
		//Thread t1=new Thread();
		Timer t = new Timer();
		//t1.start();
		t.schedule(new TimerTask() {
		    @Override
		    public void run() {
		    	progress.width-=speed;
		    }
		}, 0, 700);
	}

	public void addBalcony(boolean start){
		int i=random.nextInt(2);
		if(start){
			balcony.add(new Rectangle(20,HEIGHT-180,80,160));
			balcony.add(new Rectangle(260,HEIGHT-180*3,80,160));
		}
		else{
			if(i==0){
				balcony.add(new Rectangle(20,balcony.get(balcony.size()-1).y-(180*2),80,160));
			}
			else{
				balcony.add(new Rectangle(260,balcony.get(balcony.size()-1).y-(180*2),80,160));
			}
		}
	}
	public void addPillar(boolean start){
		int space= 20;
		int width= 160;
		if(start){
			pillars.add(new Rectangle(100,HEIGHT-width-space,width,width));
			colors.add(new Color((int)(Math.random() * 0x1000000)));
			pillars.add(new Rectangle(100,HEIGHT-width*2-space*2,width,width));
			colors.add(new Color((int)(Math.random() * 0x1000000)));
			pillars.add(new Rectangle(100,HEIGHT-width*3-space*3,width,width));
			colors.add(new Color((int)(Math.random() * 0x1000000)));
		}
		else{
			colors.add(new Color((int)(Math.random() * 0x1000000)));
			pillars.add(new Rectangle(100,pillars.get(pillars.size()-2).y-width*2-space*2,width,width));
		}
	}
	public void removePillar(){
		pillars.remove(pillars.get(0));
		colors.remove(colors.get(0));
	}
	public void removeBalcony(){
		balcony.remove(balcony.get(0));
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		repaint(g);
	}
	
	public void paintPillar(Graphics g,int index){
		g.setColor(colors.get(index));
		g.fillRect(pillars.get(index).x, pillars.get(index).y, pillars.get(index).width, pillars.get(index).height);
		g.setColor(Color.WHITE);
		g.drawRect(pillars.get(index).x, pillars.get(index).y, pillars.get(index).width, pillars.get(index).height);
		g.setColor(colors.get(index).brighter());
		g.fillRect(pillars.get(index).x+18, pillars.get(index).y+15, pillars.get(index).width/3, pillars.get(index).height/3);
		g.fillRect(pillars.get(index).x+18, pillars.get(index).y+85, pillars.get(index).width/3, pillars.get(index).height/3);
		g.fillRect(pillars.get(index).x+90, pillars.get(index).y+15, pillars.get(index).width/3, pillars.get(index).height/3);
		g.fillRect(pillars.get(index).x+90, pillars.get(index).y+85, pillars.get(index).width/3, pillars.get(index).height/3);
	}
	
	public void paintBalcony(Graphics g,int index){
		g.setColor(Color.BLACK);
		g.fillRect(balcony.get(index).x, balcony.get(index).y, balcony.get(index).width, balcony.get(index).height);
		g.setColor(Color.WHITE);
		g.drawRect(balcony.get(index).x, balcony.get(index).y, balcony.get(index).width, balcony.get(index).height);
		g.setColor(Color.GRAY.darker().darker().darker());
		g.fillRect(balcony.get(index).x+15, balcony.get(index).y+20, balcony.get(index).width-30, balcony.get(index).height/2-30);
		g.fillRect(balcony.get(index).x+15, balcony.get(index).y+90, balcony.get(index).width-30, balcony.get(index).height/2-30);
		
	}
	
	public void  paintPlayer(Graphics g){
		g.setColor(new Color(0,0,0,0));
		g.fillRect(pos, HEIGHT-75, player.width, player.height);
		if(kick){
			kick=false;
			if(pos==45)
				g.drawImage(leftKick, pos, HEIGHT-75,player.width,player.height, null);
			else
				g.drawImage(rightKick, pos, HEIGHT-75,player.width,player.height, null);
		}else{
			if(pos==45)
				g.drawImage(playerLeft, pos, HEIGHT-75,player.width,player.height, null);
			else
				g.drawImage(playerRight, pos, HEIGHT-75,player.width,player.height, null);
		}
			
	}
	
	public void repaint(Graphics g){
		g.drawImage(background, 0, backPos,WIDTH,HEIGHT, null);
		g.drawImage(background, 0, backPos-HEIGHT, WIDTH, HEIGHT, null);
		g.setColor(Color.GREEN);g.fillRect(130, 0, 10, HEIGHT);
		g.setColor(Color.GREEN);g.fillRect(160, 0, 10, HEIGHT);
		g.setColor(Color.GREEN);g.fillRect(190, 0, 10, HEIGHT);
		g.setColor(Color.GREEN);g.fillRect(220, 0, 10, HEIGHT);
		for(int i=0;i<pillars.size();i++){
			paintPillar(g,i);
		}
		for(int i=0;i<balcony.size();i++){
			paintBalcony(g,i);
		}

		g.setColor(Color.CYAN);g.fillRect(0, 0,WIDTH,90);
		g.setColor(Color.CYAN.darker());g.drawRect(-1, -1, WIDTH, 91);
		g.setColor(Color.BLUE);
		if(!gameOver){
			g.setFont(new Font("Arial",1,15));
			g.drawString("Score: ", 155,30);
			g.drawString(String.valueOf(score), 205 , 30);
			g.setColor(Color.cyan.darker());g.drawRect(39,39, 281, 31);
			g.setColor(Color.YELLOW);g.fillRect(40,40, progress.width, progress.height);
			g.setColor(Color.BLUE);g.drawString(String.valueOf(progress.width), 170, 60);
			g.setColor(Color.BLACK);g.drawString("Speed-Meter", 135, 85);
			paintPlayer(g);
		}
		else{
			g.setFont(new Font("Arial",1,50));
			g.setColor(Color.BLACK);g.drawString("GAME OVER!", 23, 63);
			g.setColor(Color.WHITE);g.drawString("GAME OVER!", 22, 62);
			g.setFont(new Font("Arial",1,90));
			if(score/100==0){
				g.setColor(Color.BLACK);g.drawString(String.valueOf(score-1), 131 , 301);
				g.setColor(Color.WHITE);g.drawString(String.valueOf(score-1), 130, 300);
			}else{
				g.setColor(Color.BLACK);g.drawString(String.valueOf(score-1), 111 , 301);
				g.setColor(Color.WHITE);g.drawString(String.valueOf(score-1), 110, 300);
			}
			clipBack.stop();
			GameOverSound();
		}
		if(running==false){
			g.setFont(new Font("Arial",1,20));
			g.setColor(Color.WHITE);g.drawString("Use arrow keys to flick Boxes!", 35, 240);
			g.setColor(Color.BLACK);g.drawString("Use arrow keys to flick Boxes!", 34, 239);
			g.setColor(Color.WHITE);g.drawString("Use left <- key to flick box to left", 15, 270);
			g.setColor(Color.BLACK);g.drawString("Use left <- key to flick box to left", 14, 269);
			g.setColor(Color.WHITE);g.drawString("Use right -> key to flick box to right", 5, 300);
			g.setColor(Color.BLACK);g.drawString("Use right -> key to flick box to right", 4, 299);
			g.setColor(Color.WHITE);g.drawString("Dont collide with Black Boxes! ", 35, 330);
			g.setColor(Color.BLACK);g.drawString("Dont collide with Black Boxes! ", 34, 329);
			g.setColor(Color.WHITE);g.drawString("Don't let the speed run out!", 44, 360);
			g.setColor(Color.BLACK);g.drawString("Don't let the speed run out!", 43, 359);
			g.setColor(Color.WHITE);g.drawString("When u flick, the top box come down",0, 390);
			g.setColor(Color.BLACK);g.drawString("When u flick, the top box come down",1, 389);
			g.setColor(Color.WHITE);g.drawString("When u flick, u regain speed",30, 420);
			g.setColor(Color.BLACK);g.drawString("When u flick, u regain speed",31, 419);
			g.setColor(Color.WHITE);g.drawString("Fast u flick, fast u regain speed",20, 450);
			g.setColor(Color.BLACK);g.drawString("Fast u flick, fast u regain speed",21, 449);
		}
		if(!gameOver&&((progress.width<160&&progress.width>150)
				||(progress.width<140&&progress.width>130)||(progress.width<120&&progress.width>110)
				||(progress.width<100&&progress.width>90)||(progress.width<80&&progress.width>70)
				||(progress.width<60&&progress.width>50||(progress.width<40&&progress.width>30)
				||(progress.width<20&&progress.width>10)))){
			g.setFont(new Font("Arial",1,30));
			g.setColor(Color.BLACK);g.drawString("Watch the time!", 75, 105);
			g.setColor(Color.WHITE);g.drawString("Watch the time!", 74, 104);
		}
	}
	public void update(){
		if(progress.width<=0) gameOver=true;
		boolean drop=false;
		if(leftClick){
			for(int i=0;i<pillars.size();i++){
				Rectangle pillar = pillars.get(0);
				Rectangle balcon = balcony.get(0);
				balcon.x-=7;
				pillar.x-=5;
				if(pillar.x<0){
					count++;
					leftClick=false;
					drop=true;
					addPillar(false);
					addBalcony(false);
					removePillar();
					if(count%2==0)removeBalcony();
				}
			}
		}
		if(rightClick){
			for(int i=0;i<pillars.size();i++){
				Rectangle pillar = pillars.get(0);
				Rectangle balcon = balcony.get(0);
				balcon.x+=9;
				pillar.x+=7;
				if(pillar.x>WIDTH-10){
					count++;
					rightClick=false;
					drop=true;
					addPillar(false);
					addBalcony(false);
					removePillar();
					if(count%2==0)removeBalcony();
				}
			}
		}
		if(!leftClick&&!rightClick&&drop){
			drop=false;
			if(progress.width<278)progress.width+=2;
			if(score%20==0){
				speed+=2;
			}
			score++;
			for(int i=0;i<pillars.size();i++){
				Rectangle column = pillars.get(i);
				column.y+=180;
			}
			for(int i=0;i<balcony.size();i++){
				Rectangle column = balcony.get(i);
				column.y+=180;
			}
			if(backPos>=HEIGHT-180){
				backPos=0;
			}
			else{
				backPos+=180;
			}
		}
	}
	
	public void collision(){
		if((balcony.get(0).x==20&&pos==45) || (balcony.get(0).x==260&&pos==265)){ 
			gameOver=true;
			clipBack.stop();
			GameOverSound();
		}
	}
	
	@Override
	public void run() {
		while(!gameOver){
			try {
				update();
				repaint();
				collision();
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
			case KeyEvent.VK_SPACE: 
				System.out.println("space");
				break;
			case KeyEvent.VK_LEFT:
				pos=265;
				System.out.println("left");
				whackSound();
				running=true;
				kick=true;
				leftClick=true;
				break;
				
			case KeyEvent.VK_RIGHT:
				pos=45;
				System.out.println("right");
				whackSound();
				rightClick=true;
				kick=true;
				running=true;
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}

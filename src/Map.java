
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.JPanel;

/*0 = tree
 *1 = grass
 *2 = gold 
 *3 = monster
 *4 = trap
 * 5 = player start
 */

public class Map extends JComponent implements ActionListener, Runnable {
	Thread t;
	public boolean ingame = false;
	public boolean finished = false;
    public boolean dying = false;
    private static Image grass =null;
    private static Image tree = null;
    private static Image UpA =null;
    private static Image DownA = null;
    private static Image LeftA =null;
    private static Image RightA = null;
    private static Image RespawnI = null;
    private static Image Scroll = null;
	public Player Hero;
	public Dimension d;
	RoomLoad Board;
	public final Font smallfont = new Font("Helvetica", Font.BOLD, 14);
	int[][] map;
	Image MapImg;
	public boolean allowmove = true;
	public String currentevent = "";
	public Timer currente;
	public boolean infight = false;
	public int takendamage =0;
	public JFrame ref;
	public JPanel controlPanel;
	public RoomLoad R;
	public ArrayList<monsters> monlist = new ArrayList<monsters>(); //Monster list
	public HashMap<String, JButton> Buttonlist = new HashMap<String,JButton>(); //But all buttons into a list
	public Map(JFrame frame,int[][] m,RoomLoad R1) {
		R = R1;
		ref = frame;
		map = m;
		MapImg = bufferMap(map);
		JSplitPane mainPanel = new JSplitPane();
		frame.add(mainPanel);
		mainPanel.setDividerSize(0);
		ref.add(mainPanel);
		mainPanel.setRightComponent(this);
		// Put all the panels split up and in
		controlPanel = new JPanel();
		controlPanel.setLayout(null);
		controlPanel.setMinimumSize(new Dimension(155, 150));
		mainPanel.setLeftComponent(controlPanel);
		Scroll = new ImageIcon("src/scroll.png").getImage();
		
		Hero = new Player(this); //set up the player
		controlPanel.addKeyListener(new TAdapter());
		controlPanel.setFocusable(true);
		setBackground(Color.WHITE);
		setEnabled( false );
		drawControls(controlPanel);
		this.start(); //start the thread
		
	}  
	@Override
	public void run() {
		while (Thread.currentThread()==t) {
			repaint(); //repaint everything
			try {
				Thread.sleep(100);
			} catch (Exception e){}
		}
		
	}
	public void start() {
		t=new Thread(this);
		t.start();
	}
	public void stop(){
		t=null;
	}
	public void paintComponent(Graphics g) {
		//draw the map
		g.drawImage(Scroll,(map[0].length * 64)-150,-120,null);
		g.drawImage(MapImg, 0, 0, null);
		

		 Graphics2D g2d = (Graphics2D) g;
		 Hero.draw(g2d);
		 drawScore(g2d);
		 drawEvents(g2d);
		 //draw in the monsters
		 if(!monlist.isEmpty()) {
			 	for(monsters mon: monlist) {
			 		if(mon.x != Hero.x || mon.y != Hero.y) {
			 			mon.draw(g2d);
			 		} else {
			 			if(mon.type==0) {
			 				g.drawImage(mon.trollI,(map[0].length * 64) + 30,map[0].length + 300,null);			 				
			 			}else {
			 				g.drawImage(mon.dragonI,(map[0].length * 64) + 10,map[0].length + 300,null); 
			 			}
			 				String s = "Monster Health:\n" + mon.hp;
			 				drawString(g,s,(map[0].length * 64) + 10, map[0].length + 380);
			 		}
			 	}
		 }
	     Toolkit.getDefaultToolkit().sync();
	     g2d.dispose();
	}
	
    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
    private void drawScore(Graphics2D g) {
       
        String s;
        g.setFont(smallfont);
        g.setColor(Color.RED);
        s = "Gold: " + Hero.gold;
        g.drawString(s,(map[0].length * 64) + 10, map[0].length + 50);
        s = "Health: " + Hero.health;
        g.drawString(s,(map[0].length * 64) + 10, map[0].length + 70);
        s = "Player Stats";
        g.setColor(Color.BLACK);
        g.drawString(s,(map[0].length * 64) + 10, map[0].length + 30);
    } 
    private void drawEvents(Graphics2D g) {
      if(currentevent != "") {
        g.setFont(smallfont);
        g.setColor(Color.BLUE);
        drawString(g,currentevent,(map[0].length * 64) + 10, map[0].length + 90);
      }

    }
    //Load in button images
    public void loadMoveImages() {
        UpA = new ImageIcon("src/arrow-up.png").getImage();
        DownA = new ImageIcon("src/arrow-down.png").getImage();
        LeftA = new ImageIcon("src/arrow-left.png").getImage();
        RightA = new ImageIcon("src/arrow-right.png").getImage();
        RespawnI = new ImageIcon("src/respawn.png").getImage();
    }
    //Show the respawn button, only called if you die
    public void ShowRespawnButton(JPanel controlPanel) {
    	JButton RespawnB = new JButton(new ImageIcon(RespawnI)); 
    	RespawnB.setBorder(BorderFactory.createEmptyBorder());
    	RespawnB.setContentAreaFilled(false); 
    	RespawnB.setFocusable(false);
    	RespawnB.setBounds(0, 150, 155, 155);
    	RespawnB.setMnemonic(KeyEvent.VK_R);
    	RespawnB.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
    		ref.dispose();
    		new RPG();
    	} });
    	controlPanel.add(RespawnB);
 
    	controlPanel.repaint();
    	
    }
    //Show the fight and flee buttons, only called when in a fight
    public void ShowMonsterButtons(JPanel controlPanel) {
    	JButton Fight = new JButton(); 
    	Fight.setToolTipText("Fight the monster, you have a chance of giving or taking damage.");
    	Fight.setText("Fight");
    	Fight.setFocusable(false);
    	Fight.setBounds(5, 380, 145, 35);
    	Fight.setMnemonic(KeyEvent.VK_RIGHT);
    	Fight.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Hero.fight();} });
    	
    	JButton Flee = new JButton(); 
    	Flee.setToolTipText("Flee from the monster, he may hit you instead though!");
    	Flee.setText("Flee");
    	Flee.setFocusable(false);
    	Flee.setBounds(5, 420, 145, 35);
    	Flee.setMnemonic(KeyEvent.VK_RIGHT);
    	Flee.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Hero.flee();} });

    	
    	controlPanel.add(Fight);
    	controlPanel.add(Flee);
    	Buttonlist.put("Fight",Fight);
    	Buttonlist.put("Flee",Flee);   	
    	controlPanel.repaint();
    	
    	
    }
    //Draw the move arrows, save and load.
    private void drawControls(JPanel controlPanel) {
    	loadMoveImages();
    	JButton UpB = new JButton(new ImageIcon(UpA));
    	UpB.setBorder(BorderFactory.createEmptyBorder());
    	UpB.setContentAreaFilled(false); 
    	UpB.setFocusable(false);
    	UpB.setBounds(37, 26, 89, 55);
    	UpB.setMnemonic(KeyEvent.VK_UP);    	
    	UpB.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Hero.MoveUP();} });
    	
    	JButton DownB = new JButton(new ImageIcon(DownA)); 
    	DownB.setBorder(BorderFactory.createEmptyBorder());
    	DownB.setContentAreaFilled(false); 
    	DownB.setFocusable(false);
    	DownB.setBounds(37, 115, 89, 55);
		DownB.setMnemonic(KeyEvent.VK_DOWN);
		DownB.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Hero.MoveDown();} });
		
		JButton LeftB = new JButton(new ImageIcon(LeftA)); 
    	LeftB.setBorder(BorderFactory.createEmptyBorder());
    	LeftB.setContentAreaFilled(false); 
    	LeftB.setFocusable(false);
    	LeftB.setBounds(-12, 71, 89, 55);
    	LeftB.setMnemonic(KeyEvent.VK_LEFT);
    	LeftB.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Hero.MoveLeft();} });
		
    	JButton RightB = new JButton(new ImageIcon(RightA)); 
    	RightB.setBorder(BorderFactory.createEmptyBorder());
    	RightB.setContentAreaFilled(false); 
    	RightB.setFocusable(false);
    	RightB.setBounds(77, 71, 89, 55);
    	RightB.setMnemonic(KeyEvent.VK_RIGHT);
    	RightB.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Hero.MoveRight();} });
    	
    	JButton SaveGame = new JButton(); 
    	SaveGame.setToolTipText("Save your progress and the map!");
    	SaveGame.setText("Save Game");
    	SaveGame.setFocusable(false);
    	SaveGame.setBounds(5, 300, 145, 35);
    	SaveGame.setMnemonic(KeyEvent.VK_RIGHT);
    	SaveGame.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){try {
			Hero.Save();
		} catch (Exception e1) {
			e1.printStackTrace();
		}} });
    	
    	JButton LoadGame = new JButton(); 
    	LoadGame.setToolTipText("Load the latest save!");
    	LoadGame.setText("Load game");
    	LoadGame.setFocusable(false);
    	LoadGame.setBounds(5, 340, 145, 35);
    	LoadGame.setMnemonic(KeyEvent.VK_RIGHT);
    	LoadGame.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){ref.dispose();
		new RPG();} });


    	
    	
    	controlPanel.add(UpB);
    	controlPanel.add(DownB);
    	controlPanel.add(LeftB);
    	controlPanel.add(RightB);
    	controlPanel.add(SaveGame);
    	controlPanel.add(LoadGame);
    	Buttonlist.put("Upb",UpB);
    	Buttonlist.put("DownB",DownB);
    	Buttonlist.put("LeftB",LeftB);
    	Buttonlist.put("RightB",RightB);
    	Buttonlist.put("SaveGame",SaveGame);
    	Buttonlist.put("LoadGame",LoadGame);
    }
   //Load in map tiles.
    public void loadMapImages() {
        tree = new ImageIcon("src/tree.png").getImage();
        grass = new ImageIcon("src/grass.png").getImage();
    }
    //Remove the fight and flee buttons
    public void hideButtons() {
    	controlPanel.remove(Buttonlist.get("Fight"));
    	controlPanel.remove(Buttonlist.get("Flee")); 
    	controlPanel.repaint();
    	
    }
    //Draw the map, grass or tree
    public BufferedImage bufferMap(int[][] map){
    	loadMapImages();
        BufferedImage bImg = new BufferedImage(map[0].length*64, (map.length)*64, BufferedImage.TYPE_INT_BGR);
        Graphics2D g2d = bImg.createGraphics();
        g2d.setColor(Color.green);
        g2d.fillRect(0, 0, map.length*64, (map[0].length)*64);
        for(int y= 0; y<map[0].length;y++){
        	for(int x = 0; x<map.length; x++) {
        		switch(map[x][y]){
             		case 0:
             			g2d.drawImage(tree, y*64,x*64, null);
             			break;
             		default:
             			g2d.drawImage(grass, y*64,x*64, null);
             			break;
        		}
        	}
        }

        g2d.finalize();
        g2d.dispose();
        return bImg;
   }
    
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
			
	}
	//Allow move via the keyboard
	class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
        	if(allowmove) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT) {
            		Hero.MoveLeft();
                } else if (key == KeyEvent.VK_RIGHT) {
                	Hero.MoveRight();
                } else if (key == KeyEvent.VK_UP) {
                	Hero.MoveUP();
                } else if (key == KeyEvent.VK_DOWN) {
                	Hero.MoveDown();
                } 
        	}
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

}

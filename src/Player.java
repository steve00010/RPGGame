import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;

public class Player{

	public Image unit;
	public int health;
	public int gold;
	public int x,y;
	public Point spawn;
	public Map Board;
	public int MHP;
	public int MDMG;
	public int CMON;
	public Player(Map M) {


		this.Board = M;
		int[] load = Board.R.LoadHero(); //load hero stats in, health and gold.
		this.health = load[0];
		this.gold = load[1];
		spawn = find2DIndex(Board.map, 5); //check for spawn point, will throw error if none found
		this.x = spawn.x *64; //set spawn points
		this.y = spawn.y*64;
		
		loadImage();
	}
	//Used for searching in the 2Dindex, find spawn point
	private Point find2DIndex(int[][] array, int search) {

	    if (search == 0 || array == null) return null;

	    for (int rowIndex = 0; rowIndex < array.length; rowIndex++ ) {
	       int[] row = array[rowIndex];
	       if (row != null) {
	          for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
	             if (search ==(row[columnIndex])) {
	                 return new Point(columnIndex, rowIndex);
	             }	
	          }
	       }
	    }
	    return null; // value not found in array
	}
	//remove all spawns for saving
	private void removespawn(int search) {

	    for (int rowIndex = 0; rowIndex < Board.map.length; rowIndex++ ) {
	       int[] row = Board.map[rowIndex];
	       if (row != null) {
	          for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
	             if (search ==(row[columnIndex])) {
	            	 Board.map[rowIndex][columnIndex] =1; //set to grass
	             }
	          }
	       }
	    }
	}
	//Save the current state of game
	public void Save() throws Exception {
		
		removespawn(5); //remove old spawn
		Board.map[this.y/64][this.x/64] = 5; //set new spawn
		Board.R.SaveMap(); //save the map
		Board.R.SaveHero(this.health,this.gold); //save the hero
		
	}
	// Draw the hero
	public void draw(Graphics2D g2d) {
		g2d.drawImage(unit,x, y, Board);
	}
	//Load in hero icon
	private void loadImage() {
        unit = new ImageIcon("src/hero.png").getImage();
        
    }
	//Handle each tile, gold monsters etc, interaction
	public void handlerooms() {
	    	int cur = Board.map[(this.y/64)][(this.x/64)];
	    	if(cur == 2) { //GOLD/health
	    		Random rand = new Random(); 
	    		int pick = rand.nextInt(4) + 1;
	    		if(pick < 3) { // found gold
		    		this.gold += pick *10;
		    		Board.currentevent = "You found " +pick*10+" gold!";
	    		} else { //found health
	    			this.health = Math.min(this.health+20,100);
	    			Board.currentevent = "You found a\nhealth potion and\nhealed 20 health!";
	    		}
	    		Board.map[(this.y/64)][(this.x/64)]=1;
	    		Thread one=new Thread() { //spawn a thread to clear the current event
	    			public void run() {
	    				try {
	    					Thread.sleep(3000); 
	    					
	    				}
	    				catch (InterruptedException annoyingCheckedException) {}
	    				Board.currentevent = "";
	    				
	    			}
	    		};
	    		one.start();
	    	}else if(cur == 3) { //Monster
	    		Random rand = new Random(); 
	    		int pick = rand.nextInt(3);
	    		handlemonster(pick);
	    		}
	    	else if(cur == 4) { //Trap
	    		Random rand = new Random(); 
	    		int pick = rand.nextInt(3) + 1;
	    		this.health -= pick*10;
	    	
	    		if(health <=0) { //killed by a trap
	    			health = 0;
	    			handleDeath();
	    		} else {
	    			Board.currentevent = "You have stepped\non a trap and taken\n"+ pick*10+" damage!";
	    		}
    			Board.map[(this.y/64)][(this.x/64)]=1;
	    		Thread four= new Thread() { //spawn a thread to clear the current event
	    			public void run() {
	    				try {
	    					if(health==0){
	    						Thread.sleep(1000);
	    			    		Board.currentevent = "You have stepped\non a trap and died!\nRespawn and go\nagain!";
	    						Thread.sleep(1000);
	    			    		Board.currentevent = "You have stepped\non a trap and died!\nRespawn and go\nagain!";
	    						Thread.sleep(1000);
	    			    		Board.currentevent = "You have stepped\non a trap and died!\nRespawn and go\nagain!";
	    						Thread.sleep(100000);
	    						
	    					} else {
	    						Thread.sleep(3000);
	    					}
	    					
	    				}
	    				catch (InterruptedException annoyingCheckedException) {}
	    				Board.currentevent = "";
	    				
	    			}
	    		};
	    		four.start();
	    		
	    	}
	    	
	    	
	    }
	// Hero died.
	 	public void handleDeath() {
			Board.allowmove = false;
			unit = new ImageIcon("src/dead.png").getImage();
    		Board.currentevent = "You have been\nkilled!\nRespawn and go\nagain!";
    		Board.ShowRespawnButton(Board.controlPanel);
    		Board.controlPanel.remove(Board.Buttonlist.get("SaveGame"));
	 		
	 	}
	 	//Fight the monster.
	 	public void fight() {
	 		if(Board.infight) {
	 			if(!Board.monlist.isEmpty()) {
	 				monsters montofight = null;
				 	for(monsters mon: Board.monlist) {
				 		if(mon.x == this.x && mon.y == this.y) {
				 			montofight = mon;
				 		}
				 	}
				 	handlefight(montofight);
	 			}
	 		}
	 	}
	 	//Handle the fight, damage events
	 	public void handlefight(monsters mon) {
	 		Random rand = new Random(); 
	 		int pick = rand.nextInt(3) + 1; 
	 		if(pick == 0 || pick == 1) { //66% chance to hit the monster, hit it
	 				int inf = rand.nextInt(15) + 5;
	 				mon.hp = mon.hp -inf;
	 				if(mon.hp>0) { //just damage it
	 					Board.currentevent = "You damage\nthe monster\nfor "+inf+"\ndamage!";
	 				} else { //killed the monster
	 					mon.hp =0;
	 					Board.currentevent = "You have slain\nthe monster!";
	 					Board.map[this.y/64][this.x/64]=1;
	 					Iterator<monsters> iter = Board.monlist.iterator();
	 					while(iter.hasNext()){
	 						monsters mon1 = iter.next();
					 		if(mon1.x == this.x && mon1.y == this.y && mon1.hp ==0) {
					 			iter.remove();
					 		}
					 	}
	 					//Allow the player to get back to moving 
	 					Board.infight = false;
	 					Board.allowmove = true;
	 					//Hide fight buttons
	 					Board.hideButtons();
	 				}
	 				
	 		}	
	 		else { //33% chance to get hit by the monster, hit by it
	 			int inf = rand.nextInt(mon.dmg) + 1;
	 			Board.currentevent = "The monster\ndamages you\nfor "+inf+"\ndamage!";
	 			this.health = this.health -inf;
	 			if(this.health < 0) { //been killed
	 				this.health = 0;
	 				handleDeath();
	 			}
	 		}
	 				
	 		
	 		Thread one=new Thread() { //spawn a thread to clear the current event
    			public void run() {
    				try {
    					Thread.sleep(4000); 
    					
    				}
    				catch (InterruptedException annoyingCheckedException) {}
    				Board.currentevent = "";
    				
    			}
    		};
    		one.start();
	 	}
	 	//Run away from monster
	 	public void flee() {
	 		if(Board.infight) {
	 			if(!Board.monlist.isEmpty()) {
	 				monsters montofight = null;
				 	for(monsters mon: Board.monlist) {
				 		if(mon.x == this.x && mon.y == this.y) {
				 			montofight = mon;
				 		}
				 	}
				 	handleflee(montofight);
	 			}
	 		} 		
	 	}
	 	//Handle the fleeing
	 	public void handleflee(monsters mon) {
	 		Random ran = new Random();
	 		int n = ran.nextInt(2);
	 		if(n ==0) { //managed to flee from the monster
					Board.infight = false;
					Board.allowmove = true;
					Board.hideButtons();
					if(this.x != 0 && Board.map[(this.y/64)][(this.x/64)-1] != 0) {
		    			this.x = this.x -64;
		    			handlerooms();
		    		} else if(this.y != 0 && Board.map[(this.y/64)-1][(this.x/64)] != 0) {
		    			this.y = this.y -64;
						handlerooms();
		    		}else if(this.x / 64 != Board.map[0].length -1 && Board.map[(this.y/64)][(this.x/64)+1] != 0) {
		    			this.x = this.x +64;
		    			handlerooms();
		    		} else if(this.y / 64 != (Board.map.length-1) && Board.map[(this.y/64)+1][(this.x/64)] != 0) {
		    			this.y = this.y +64;
		    			handlerooms();
		    		}
					
	 			
	 		}else { //not managed to flee from the monster
	 			int inf = ran.nextInt(mon.dmg) + 1;
	 			Board.currentevent = "The monster\ndamages you\nfor "+inf+"\ndamage!";
	 			this.health = this.health -inf;
	 			if(this.health < 0) {
	 				this.health = 0;
	 				handleDeath();
	 			}
	 			
	 		}
	 		Thread one=new Thread() { //spawn a thread to clear the current event
    			public void run() {
    				try {
    					Thread.sleep(4000); 
    					
    				}
    				catch (InterruptedException annoyingCheckedException) {}
    				Board.currentevent = "";
    				
    			}
    		};
    		one.start();
	 		
	 		
	 	}
	 	//Handle the monster spawning.
	    public void handlemonster(int ran) {
	    	monsters montofight = null;
	    	for(monsters mon: Board.monlist) {
		 		if(mon.x == this.x && mon.y == this.y) {
		 			montofight = mon;
		 		}
		 	}
	    	if(montofight == null) {
	    		if(ran == 1 || ran == 2) { //found a troll
	    			this.MHP=10;
	    			this.MDMG=5;
	    			this.CMON = 0;
		    		Board.currentevent = "You have\nstumbled upon\na troll!\nYou must either\n fight or flee!";
	    		}
	    		else { //found a dragon
	    			this.MHP=15;
	    			this.MDMG=10;
	    			this.CMON =1;
	    			Board.currentevent = "You have\nstumbled upon\na dragon! You\nmust either\nfight or flee!";
	    		}
	    	

    	
	    	Board.ShowMonsterButtons(Board.controlPanel);
	    	Board.monlist.add(new monsters(this.x,this.y,this.MHP,this.MDMG,this.CMON,Board));
	    	Board.infight = true;
	    	Board.allowmove = false;	
	    	} else {
	    		switch(montofight.type) {
	    		case 0:
		    		Board.currentevent = "You have\nstumbled upon\na troll!\nYou must either\n fight or flee!";
			    	break;
	    		case 1:
			    	Board.currentevent = "You have\nstumbled upon\na dragon! You\nmust either\nfight or flee!";
			    	break;
	    		}
	    		// still in a fight
		    	Board.ShowMonsterButtons(Board.controlPanel);
		    	Board.infight = true;
		    	Board.allowmove = false;	
	    		
	    	}
	    }
	    //Move left on the map
	    public void MoveLeft() {
	    	if(Board.allowmove) {
	    		if(this.x != 0 && Board.map[(this.y/64)][(this.x/64)-1] != 0) {
	    			this.x = this.x -64;
	    			handlerooms();
	    		}
	    	}
	    }
	    //Move up on the map
	    public void MoveUP() {
	    	if(Board.allowmove) {
	    		if(this.y != 0 && Board.map[(this.y/64)-1][(this.x/64)] != 0) {
	    			this.y = this.y -64;
					handlerooms();
	    		}	
	    	}
	    }
	    //Move right on the map
	    public void MoveRight() {
	    	if(Board.allowmove) {
	    		if(this.x / 64 != Board.map[0].length -1 && Board.map[(this.y/64)][(this.x/64)+1] != 0) {
	    			this.x = this.x +64;
	    			handlerooms();
	    		}
	    	}
	    }
	    //Move down on the map
	    public void MoveDown() {
	    	if(Board.allowmove) {
	    		if(this.y / 64 != (Board.map.length-1) && Board.map[(this.y/64)+1][(this.x/64)] != 0) {
	    			this.y = this.y +64;
	    			handlerooms();
	    		}
	    	}
	    }
}

import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;


public class monsters {
	int x;
	int y;
	int hp;
	int dmg;
	int type;
    public Image dragon =null;
    public Image troll = null;
    public Image dragonI =null;
    public Image trollI = null;    
    public Map M;
    //Load the monster.
	public monsters(int x,int y, int hp, int dmg, int type, Map M) {
		this.M = M;
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.dmg = dmg;
		this.type = type;
		loadMonsterImages();
	}
	//Draw the monster on the map
	public void draw(Graphics2D g2d) {
		if(type == 0) g2d.drawImage(troll,x, y, M);
		if(type == 1) g2d.drawImage(dragon,x, y, M);
	}
	//Load the images for the monsters, on map or sidebars
    public void loadMonsterImages() {
        dragon = new ImageIcon("src/dragon.png").getImage();
        troll = new ImageIcon("src/troll.png").getImage();
        dragonI = new ImageIcon("src/Dragon_Lord.gif").getImage();
        trollI = new ImageIcon("src/Island_Troll.gif").getImage();
        
    }
}

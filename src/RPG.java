import java.awt.Image;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;


public class RPG {
	Image MapImg;
	int[][] map;
	RoomLoad R1;
	Map board;

	
	public RPG(){
		JFrame frame = new JFrame(); //Create the Frame
		
		initUI(frame); //Load the UI
		int minx = map[0].length * 64 + 310;
		int miny = Math.max(map.length * 64 + 30,500);
		frame.setTitle("Monsters and Magic");
		frame.setSize(minx, miny);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setResizable(false);
		frame.setVisible(true); //Frame set up.

	}
	private void initUI(JFrame frame) {
        R1 = new RoomLoad("save/map.txt","save/hero.txt");
        map = R1.map;
        Map board = new Map(frame,map,R1);
	}

}


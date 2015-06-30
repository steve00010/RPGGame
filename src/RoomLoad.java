import java.awt.Color;
import java.awt.Point;
import java.io.*;

import javax.swing.Timer;


public class RoomLoad {
	public int[][] map;
	public final int blocksize = 64;
    public final int nrofblocks;
    public final int scrsize;
    public final Color dotcolor = new Color(192, 192, 192);
    public Color mazecolor;
    public short[] screendata;
    public Timer timer;
	public Point spawn;
	String file;
	String herofile;
	public RoomLoad(String mapfile,String herofile){
		this.file = mapfile;
		this.herofile = herofile;
		map = LoadMap(file);
		
		nrofblocks = map[0].length;
		scrsize = nrofblocks * blocksize;
	}
	//Read the map from the text file and put it into the map int array.
	public int[][] LoadMap(String file){

		try {
			FileReader fr= new FileReader(file);
			LineNumberReader LN = new LineNumberReader(fr);
			int lines=0;
			String buff = null;
			String aa = null;
			while(( buff= LN.readLine()) !=null) {
				if(buff != System.getProperty("line.separator")) { lines ++; }
				aa = aa + buff;
			}
			LN.close();
			
			
			int x=0, y=0;
			int[][] ret = null;
			boolean first=true;
			BufferedReader bf = new BufferedReader(new FileReader(file));
			while((buff = bf.readLine()) !=null) {
				
					String[] cells = buff.split(",");
					if(first) {
						ret = new int[lines][cells.length];
						first = false;
					}
					 for (String b1 : cells) {
				            ret[x][y]=(int)Integer.parseInt(b1);
				            y=y+1;
				        }
					 y=0;
					 x=x+1;
				
			}
			bf.close();
			return ret;
			
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}			
	}
	//Save the map to the map file
	public void SaveMap() {
		String[] lines = new String[map.length];
		for(int i=0;i<this.map.length;i++) {
			for(int y=0;y<this.map[0].length;y++) {
				if(y==0) {
					lines[i] = Integer.toString(map[i][y]) + ',';
				}
				if(y==this.map[0].length-1) {
					lines[i] =  lines[i] + Integer.toString(map[i][y]);
				}
				 else { lines[i] = lines[i] + Integer.toString(map[i][y]) + ','; }
			}
		}
		try {
			FileWriter out = new FileWriter(this.file);
			int q=0;
			for(String a : lines) {
				if(q!=map.length)
				out.write(a.trim() + System.getProperty( "line.separator" ));
				else 
				out.write(a.trim());
				
				q++;
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//Save the hero to the hero file
	public void SaveHero(int health,int gold) {
		try {
			FileWriter out = new FileWriter(this.herofile);
			out.write(health+";"+gold);
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//Load the hero from the hero file
	public int[] LoadHero() {
		int[] ret = new int[2];
		try {
			BufferedReader bf = new BufferedReader(new FileReader(this.herofile));
			String[] ex = bf.readLine().split(";");
			ret[0]=Integer.parseInt(ex[0]); //health
			ret[1]=Integer.parseInt(ex[1]); //gold
			bf.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
}

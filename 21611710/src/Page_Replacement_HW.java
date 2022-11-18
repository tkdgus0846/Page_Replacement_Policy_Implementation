import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Page_Replacement_HW {
	
	private static String inputString="";
	private static int frameNum=0;
	private static Graphics g;
	private static ArrayList<String> table= new ArrayList<>();
	private static boolean fifo_State= true; 
	private static boolean lru_State= false;
	private static boolean lfu_State= false;
	private static boolean mfu_State= false;
	
	private static Color fault=new Color(153,000,000);
	private static Color hit=new Color(051,000,102);
	private static Color migrate=new Color(102,000,102);

	static class Panel1 extends JPanel{
		
		Panel2 p2;
		Panel3 p3;
		
		public Panel1(Panel2 p2, Panel3 p3) {
			this.p2=p2;
			this.p3=p3;
			setBounds(0,0,785,60);
			setLayout(null);
					
			JTextField reference_String= new JTextField();
			JTextField frame_Num= new JTextField("3");
			JLabel policy_Label=new JLabel("Policy");
			JLabel reference_String_Label=new JLabel("Reference String");
			JLabel frame_Num_Label=new JLabel("#Frame");
			
			policy_Label.setBounds(25,0,40,20);
			reference_String_Label.setBounds(210,0,100,20);
			frame_Num_Label.setBounds(453,0,50,20);
			reference_String.setBounds(80,20,350,25);
			frame_Num.setBounds(435,20,80,25);
						
			String policy_List[]= {"FIFO","LRU","LFU","MFU"};
			JComboBox policy_Sel= new JComboBox(policy_List);
			policy_Sel.setBounds(5,20,70,25);
			
			policy_Sel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JComboBox combo= (JComboBox) e.getSource();
					
					for (int i=0; i<policy_List.length; i++) {
						if (policy_List[i].equals(combo.getSelectedItem())) {
							switch(i) {
							case 0:
								fifo_State= true;
								lru_State= false;
								lfu_State= false;
								mfu_State= false;
								break;
							case 1:
								fifo_State= false;
								lru_State= true;
								lfu_State= false;
								mfu_State= false;
								break;
							case 2:
								fifo_State= false;
								lru_State= false;
								lfu_State= true;
								mfu_State= false;
								break;
							case 3:
								fifo_State= false;
								lru_State= false;
								lfu_State= false;
								mfu_State= true;
								break;
							}
						}
					}
					
				}
				
			});
			
			JButton random= new JButton("Random");
			JButton run= new JButton("Run");
			random.setBackground(Color.LIGHT_GRAY);
			run.setBackground(Color.LIGHT_GRAY);
			random.setBounds(570,10,80,35);
			run.setBounds(660,10,80,35);
			
			random.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reference_String.setText("");
					Random rand= new Random();
					String tempStr="";
					int end= rand.nextInt(40)+4;
					for (int start=0; start < end; start++) {
						Random randChar= new Random();
						char ch= (char)(65+randChar.nextInt(26));
						tempStr+=ch;
					}
					reference_String.setText(tempStr);
				}
			});
			
			run.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (reference_String.getText().equals("") || frame_Num.getText().equals("")) return;
					
					p3.isHasBeen= false;
					inputString= reference_String.getText();
					frameNum= Integer.parseInt(frame_Num.getText());
					table.clear();
					p2.repaint();
				}
			});
			
			add(policy_Sel);
			add(reference_String);
			add(frame_Num);
			add(policy_Label);
			add(reference_String_Label);
			add(frame_Num_Label);
			add(random);
			add(run);
		}
	}
	
	static class Panel2 extends JPanel{
		
		private static final int SIZE= 30;
		private int x=5,y=30;
		
		private ArrayList <String> whenUsed= new ArrayList<>();
		private Hashtable <String, Integer> howManyUsed= new Hashtable<>();
		private boolean isMigrateEnded= false;
		
		Panel3 p3= new Panel3();
		
		public Panel2(Panel3 p3) {
			this.p3= p3;
			setBackground(Color.black);
			Dimension d2= new Dimension(520*4,398*4);
			setPreferredSize(d2);
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.setColor(Color.WHITE);
			if (inputString.equals("") || frameNum==0) return;
			
			String str="";
			for (int i=0; i<inputString.length(); i++) {
								
				str= inputString.substring(i,i+1);
				g.drawString(str, x+11, 17);
				
				for (int j=0; j<frameNum; j++) { 
					
					if (fifo_State == true)
						fifo(g, table, str, j);
					if (lru_State == true)
						lru(g, table, str, j);
					if (lfu_State == true)
						lfu(g, table, str, j);
					if (mfu_State == true)
						mfu(g, table, str, j);
						
					
					g.setColor(Color.WHITE);
					g.drawRect(x,y+(SIZE*j),SIZE,SIZE);
				}
				
                for (int j=0; j<table.size(); j++) { 
					g.drawString(table.get(j), x+11, y+(SIZE*j)+17);
				}
				isMigrateEnded= false;				
				x+=(5+SIZE);
			}
			x=5;
			table.clear();
			whenUsed.clear();
			howManyUsed.clear();
			
			if (!p3.isHasBeen) {
				p3.set_Text();
				p3.isHasBeen= true;
			}
			
		}
		
		private void fifo(Graphics g, ArrayList<String> table, String str, int j) {
			
			if (table.size() == frameNum && !table.contains(str)) {			
				if (j == frameNum-1) {
					table.remove(0);
					table.add(str);
					g.setColor(migrate);
					g.fillRect(x,y+(SIZE*j),SIZE,SIZE);
					
					if (!p3.isHasBeen) {
						p3.add_Migrate(str);
					}
					
				}
				return;
			}			
			if (table.size() < frameNum && !table.contains(str)) {
				if (j == table.size()) {
					g.setColor(fault);
				    g.fillRect(x,y+(SIZE*j),SIZE,SIZE);
				    table.add(str);
				    if (!p3.isHasBeen) {
						p3.add_Fault(str);
					}
				}
				return;				
			}
			if (table.contains(str)) {
				if (j == table.indexOf(str)) {
					g.setColor(hit);
					g.fillRect(x,y+(SIZE*j),SIZE,SIZE);
					if (!p3.isHasBeen) {
						p3.add_Hit(str);
					}
				}		
				return;
			}	
			
		}
		
		private void lru(Graphics g, ArrayList<String> table, String str, int j) {
			
			if (table.size() == frameNum && !table.contains(str)) {			
				if (whenUsed.get(0).equals(table.get(j))) {
					g.setColor(migrate);
					g.fillRect(x,y+(SIZE*j),SIZE,SIZE);
					table.remove(j);
					table.add(j,str);					
					whenUsed.remove(0);
					whenUsed.add(str);
					if (!p3.isHasBeen) {
						p3.add_Migrate(str);
					}
					
				}
				return;
			}	
			if (table.size() < frameNum && !table.contains(str)) {
				if (j == table.size()) {
					g.setColor(fault);
				    g.fillRect(x,y+(SIZE*j),SIZE,SIZE);
				    table.add(str);
				    whenUsed.add(str);
				    if (!p3.isHasBeen) {
						p3.add_Fault(str);
					}
				}
				return;				
			}
			if (table.contains(str)) {
				if (j == table.indexOf(str)) {
					g.setColor(hit);
					g.fillRect(x,y+(SIZE*j),SIZE,SIZE);				
					whenUsed.remove(whenUsed.indexOf(str));
					whenUsed.add(str);
					if (!p3.isHasBeen) {
						p3.add_Hit(str);
					}
				}		
				return;
			}	
			
		}
		
        private void lfu(Graphics g, ArrayList<String> table, String str, int j) {
        	
        	if (table.size() == frameNum && !table.contains(str)) {	
        		      		
        		if (!isMigrateEnded) {
        			Hashtable <String,Integer> tempTable= new Hashtable<>();
        			
            		for (int i=0; i<whenUsed.size(); i++) {
            			if (howManyUsed.containsKey(whenUsed.get(i))) {
            				tempTable.put(whenUsed.get(i), howManyUsed.get(whenUsed.get(i)));
            			}
            		}
            		Integer min= Collections.min(tempTable.values());
            		
        			if (min.equals(howManyUsed.get(table.get(j)))){
        				int index= -1;
        				for (int i=0; i<whenUsed.size(); i++) {
        					if(min.equals(howManyUsed.get(whenUsed.get(i)))) {
        						index= i;
        						break;
        					}
        				}
    					
    					g.setColor(migrate);
    					g.fillRect(x,y+(table.indexOf(whenUsed.get(index))*SIZE),SIZE,SIZE);
    					
    					table.add(table.indexOf(whenUsed.get(index)),str);
    					table.remove(whenUsed.get(index)); 					
    					whenUsed.remove(index);
    					whenUsed.add(str);				
    					
    					if (howManyUsed.containsKey(str))
    						howManyUsed.replace(str,howManyUsed.get(str)+1);
    					else
    						howManyUsed.put(str,1);
    					
    					isMigrateEnded= true;
    					if (!p3.isHasBeen) {
    						p3.add_Migrate(str);
    					}
            		}      			
        		}
        		return;   
        	}	
        	       	
			if (table.size() < frameNum && !table.contains(str)) { 
				if (j == table.size()) {
					g.setColor(fault);
				    g.fillRect(x,y+(SIZE*j),SIZE,SIZE);
				    table.add(str);
				    whenUsed.add(str);
				    howManyUsed.put(str, 1);
				    if (!p3.isHasBeen) {
						p3.add_Fault(str);
					}
				}
				return;				
			}
			
			if (table.contains(str)) {
				if (!isMigrateEnded) {
					if (j == table.indexOf(str)) { 
						g.setColor(hit);
						g.fillRect(x,y+(SIZE*j),SIZE,SIZE);	
						int index= whenUsed.indexOf(str);
						whenUsed.remove(index);
						whenUsed.add(str);
						howManyUsed.replace(str, howManyUsed.get(str)+1);
						isMigrateEnded= true;
						if (!p3.isHasBeen) {
							p3.add_Hit(str); 
						}
					}
				}
						
				return;
			}
		}
        
        private void mfu(Graphics g, ArrayList<String> table, String str, int j) {
        	
        	if (table.size() == frameNum && !table.contains(str)) {	
        		      		
        		if (!isMigrateEnded) {
        			Hashtable <String,Integer> tempTable= new Hashtable<>();
        			
            		for (int i=0; i<whenUsed.size(); i++) {
            			if (howManyUsed.containsKey(whenUsed.get(i))) {
            				tempTable.put(whenUsed.get(i), howManyUsed.get(whenUsed.get(i)));
            			}
            		}
            		Integer max= Collections.max(tempTable.values());
            		
        			if (max.equals(howManyUsed.get(table.get(j)))){
        				int index= -1;
        				for (int i=0; i<whenUsed.size(); i++) {
        					if(max.equals(howManyUsed.get(whenUsed.get(i)))) {
        						index= i;
        						break;
        					}
        				}
    					
    					g.setColor(migrate);
    					g.fillRect(x,y+(table.indexOf(whenUsed.get(index))*SIZE),SIZE,SIZE);
    					
    					table.add(table.indexOf(whenUsed.get(index)),str);
    					table.remove(whenUsed.get(index)); 					
    					whenUsed.remove(index);
    					whenUsed.add(str);				
    					
    					if (howManyUsed.containsKey(str))
    						howManyUsed.replace(str,howManyUsed.get(str)+1);
    					else
    						howManyUsed.put(str,1);
    					
    					isMigrateEnded= true;
    					if (!p3.isHasBeen) {
    						p3.add_Migrate(str);
    					}
            		}      			
        		}
        		return;   
        	}	
        	       	
    		if (table.size() < frameNum && !table.contains(str)) { 
    			if (j == table.size()) {
    				g.setColor(fault);
    			    g.fillRect(x,y+(SIZE*j),SIZE,SIZE);
    			    table.add(str);
    			    whenUsed.add(str);
    			    howManyUsed.put(str, 1);
    			    if (!p3.isHasBeen) {
    					p3.add_Fault(str);
    				}
    			}
    			return;				
    		}
    		
    		if (table.contains(str)) {
    			if (!isMigrateEnded) {
    				if (j == table.indexOf(str)) { 
    					g.setColor(hit);
    					g.fillRect(x,y+(SIZE*j),SIZE,SIZE);	
    					int index= whenUsed.indexOf(str);
    					whenUsed.remove(index);
    					whenUsed.add(str);
    					howManyUsed.replace(str, howManyUsed.get(str)+1);
    					isMigrateEnded= true;
    					if (!p3.isHasBeen) {
    						p3.add_Hit(str); 
    					}
    				}
    			}
    					
    			return;
    		}
    	}
 
	}
	
	static class Panel3 extends JPanel{
		
		private int hit_Num=0;
		private int migrate_Num=0;
		private int fault_Num=0;
		private boolean isHasBeen= false;
		
		ChartPanel chartPanel= new ChartPanel();
		String outStr= "";
		JTextArea route= new JTextArea();
		JScrollPane js= new JScrollPane(route);
		JLabel label= new JLabel("Page Fault Rate (%) =     ");
		
		public Panel3() {
			setLayout(null);
			setBounds(528,60,253,398);
			label.setBounds(0,377,300,20);
			add(label);
			chartPanel.setBackground(Color.white);
			chartPanel.setBounds(2,135,250,235);
			add(chartPanel);
	
			route.setBackground(null);
			route.setEditable(false);
			js.setBounds(2,2,251,130);
			add(js);
		}
		
		public void add_Migrate(String str) {
			outStr+= "DATA "+str+" is Migrated\n";
			migrate_Num++;
		}
		
		public void add_Hit(String str) {
			outStr+= "DATA "+str+" is Hit\n";
			hit_Num++;
		}
		
		public void add_Fault(String str) {
			outStr+= "DATA "+str+" is Page Fault\n";
			fault_Num++;
		}
		
		public void set_Text() {			
			route.setText(outStr);
			
			chartPanel.setNum(fault_Num, migrate_Num, hit_Num);
			chartPanel.repaint();
			double percent= ((double)(fault_Num+migrate_Num)/(double)(fault_Num+migrate_Num+hit_Num));
			label.setText("Page Fault Rate (%) =     "+ String.format("%.2f", percent*100)+"%");
			outStr= "";
			migrate_Num=0;
			hit_Num=0;
			fault_Num=0;
		}
		
	}
	
	static class ChartPanel extends JPanel{
		private int fault_Num;
		private int migrate_Num;
		private int hit_Num;
		
		public void paint(Graphics g) {
			super.paint(g);
			g.clearRect(0, 0, getWidth(), getHeight());
			
			int total = fault_Num + migrate_Num + hit_Num;
			if (total == 0)
				return;
			
			int fault_Rad = (int) 360.0 * fault_Num / total;
			int migrate_Rad = (int) 360.0 * migrate_Num / total;
			int hit_Rad = 360-(fault_Rad+migrate_Rad);
			
			g.setColor(fault);
			g.fillArc(50, 10, 150, 150, 0, fault_Rad);
			g.setColor(migrate);
			g.fillArc(50, 10, 150, 150, fault_Rad, migrate_Rad);			
			g.setColor(hit);
			g.fillArc(50, 10, 150, 150, fault_Rad+migrate_Rad, hit_Rad);
			
			g.setColor(fault);
			g.fillRect(50, 184, 15, 10);
			g.setColor(migrate);
			g.fillRect(50, 202, 15, 10);
			g.setColor(hit);
			g.fillRect(50, 220, 15, 10);
			
			g.setColor(Color.black);
			g.drawString("page fault= "+fault_Num, 68, 192);
			g.drawString("migrated= "+migrate_Num, 68, 210);
			g.drawString("hit= "+hit_Num, 68, 228);
		}
		
		public void setNum(int fault_Num, int migrate_Num, int hit_Num) {
			this.fault_Num= fault_Num;
			this.migrate_Num= migrate_Num;
			this.hit_Num= hit_Num;
		}
	}
	
	public static void main(String[] args) {
		JFrame frame= new JFrame("Memory Simulator");
		
		Panel3 p3= new Panel3();
		Panel2 p2= new Panel2(p3);
		Panel1 p1= new Panel1(p2, p3);	
		
		JScrollPane scroll= new JScrollPane(p2);
		scroll.setBounds(5,60,520,398);
		
		frame.add(p1);
		frame.add(scroll);
		frame.add(p3);
		frame.setLayout(null);
		frame.setVisible(true);
		frame.pack();
		frame.setSize(800, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

	}

}

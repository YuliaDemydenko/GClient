package view;

import controller.ControlView;
import model.ParseMessage;
import org.slf4j.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GamePanel {
	protected static final Logger logger = LoggerFactory.getLogger(GamePanel.class);
	
	ControlView clientView;
	public GamePanel(ControlView clientView){
		this.clientView=clientView;
	}
	
	/** indicates angle ships  */
	private static final int HORIZONTAL = 0; 
	private static final int VERTICAL = 1; 
	private JPanel gamePanel = new JPanel(new GridBagLayout());
	private JTextArea messegeArea = new JTextArea();
	public JButton sendButton = new JButton("send");
	private JPanel [][]gameFields = new JPanel[10][10];
	private boolean [][]myShips = new boolean[12][12];
	private boolean [][]myShipsChecked = new boolean[12][12];
	private JButton surrenderButton = new JButton("Surrender");
	private JPanel myGameField = new JPanel(new GridLayout(10,10));
	private JPanel [][]myGameFields = new JPanel[10][10];
	/** position where the shot was fired last time */
	private int oldTargetX;
	private int oldTargetY;
	private Timer moveLongTimer = new Timer (10000, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("Warning, 5 seconds left!");
			messegeArea.setText("Hurry up, 5 seconds left!");
			moveQuickTimer.restart();
		}		    		
	});
	private Timer moveQuickTimer = new Timer (5000, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.debug("Time is up!");
			clientView.gameOver(false);
			clientView.repaint();
		}		    		
	});
	public JPanel getGamePanel(){
		return gamePanel;
	}
	
	{
		// timers without repeats
				moveQuickTimer.setRepeats(false);
				moveLongTimer.setRepeats(false);
				
				
JPanel gameField = new JPanel(new GridLayout(10,10));
		
		PanelsMouseListener pML = new PanelsMouseListener();
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				gameFields[i][j] = new JPanel();
				gameFields[i][j].setBackground(new Color(65, 105, 225));
				gameFields[i][j].addMouseListener(pML);
				gameFields[i][j].setPreferredSize(new Dimension(25,25) );
				gameFields[i][j].setBorder(new LineBorder(Color.BLACK,1));
				gameField.add(gameFields[i][j]);
			}
		}			
		
		
		for (int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				myGameFields[i][j] = new JPanel();
				myGameFields[i][j].setBackground(new Color(65, 105, 225));
				myGameFields[i][j].setPreferredSize(new Dimension(25,25) );
				myGameFields[i][j].setBorder(new LineBorder(Color.BLACK,1));
				myGameField.add(myGameFields[i][j]);
			}
		}
		
		GridBagConstraints c = new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0);
		
		gamePanel.add(messegeArea,c);
		c.gridwidth=1;
		c.gridy=1;		
		gamePanel.add(surrenderButton,c);
		surrenderButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				clientView.gameOver(false);
				clientView.validate();
				clientView.repaint();
			}
			
		});
		
		c.gridx=1;
		gamePanel.add(sendButton,c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridy=2;	
		c.gridx=0;
		c.gridwidth=2;
		gamePanel.add(gameField,c);
				
		
		sendButton.setPreferredSize(new Dimension(100, 25));
		sendButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!clientView.getUser().isReady()){
					fillMyShips();
					if (!validationPlacementShips()){
						logger.info("User incorrectly arranged the ships.");
						messegeArea.setText("Please, arrange the ships correctly!");
						return;
					}
					logger.info("User ready for game.");
					clientView.getUser().setReady(true);
					clientView.getController().send(ParseMessage.createXML(""+clientView.getUser().getId(), ""+clientView.getController().getOpponentId(), "ready", ""));
					messegeArea.setText("i am ready!");
					sendButton.setEnabled(false);
					refreshGameFields();
				}
				else if (sendButton.isEnabled()) {
					int row = -1;
					int col = -1;
					for (int i=0;i<10;i++){
						for (int j=0;j<10;j++){
							if (gameFields[i][j].getBackground()==Color.WHITE){
								row=i;
								col=j;
							}
						}
					}
					
					if (row==-1 || col==-1){
						messegeArea.setText("Choose one field!");
						return;
					}
					
					gameFields[row][col].setBackground(Color.CYAN);
					oldTargetX=row;
					oldTargetY=col;
					
					moveLongTimer.stop();
					moveQuickTimer.stop();					

					logger.info("User wait opponent");
					messegeArea.setText("wait opponent");
					clientView.getController().send(ParseMessage.createXML(""+clientView.getUser().getId(),""+clientView.getController().getOpponentId(),
							"fire","<row>"+row+"</row><col>"+col+"</col>"));
					sendButton.setEnabled(false);
				}
				
			}
			
		});
	}
	
	public boolean validationPlacementShips(){
		int fourDeck=0;
		int threeDeck=0;
		int twoDeck=0;
		int oneDeck=0;
		
		int counterActiveFields=0;
		int counterCountShip=0;
		
		for (int i=0;i<12;i++){
			counterCountShip=0;
			for (int j=0;j<12;j++){
				if (myShips[i][j] && !myShipsChecked[i][j]){
					counterActiveFields++;
					counterCountShip++;
				}
				else {
					if (counterCountShip>1){
						switch (counterCountShip){
							case 2: 
								twoDeck++; 
								if (twoDeck>3)
									return false;	
								break;
							case 3: 
								threeDeck++; 
								if (threeDeck>2)
									return false;
								break;
							case 4: 
								fourDeck++; 
								if (fourDeck>1)
									return false;
								break;
							default: 
								return false;
						}
							if (!checkSurroundings(HORIZONTAL,counterCountShip,i,j))
								return false;
						
					}
					counterCountShip=0;
				}
			}
		}
		
		if (counterActiveFields>20) return false;
		
		for (int j=0;j<12;j++){
			counterCountShip=0;
			for (int i=0;i<12;i++){
				if (myShips[i][j] && !myShipsChecked[i][j]){
					counterCountShip++;
				}
				else  {
					if (counterCountShip>0){
						switch (counterCountShip){
							case 1:
								oneDeck++; 
								if (oneDeck>4)
									return false;	
								break;
							case 2: 
								twoDeck++; 
								if (twoDeck>3)
									return false;	
								break;
							case 3: 
								threeDeck++; 
								if (threeDeck>2)
									return false;
								break;
							case 4: 
								fourDeck++; 
								if (fourDeck>1)
									return false;
								break;
							default: 
								return false;
						}
							if (!checkSurroundings(VERTICAL,counterCountShip,i,j))
								return false;
					}
					counterCountShip=0;
				}
			}
		}
		
		if (fourDeck!=1 || threeDeck!=2 || twoDeck!=3 || oneDeck!=4 )
			return false;
		return true;			
	}
	public void fillMyShips(){
		for (int i=0;i<10;i++)
			for (int j=0;j<10;j++){
				myShipsChecked[i+1][j+1]=false;
				if (gameFields[i][j].getBackground()==Color.WHITE) 
					myShips[i+1][j+1]=true;
				else 
					myShips[i+1][j+1]=false;
			}
	}

	public boolean checkSurroundings(int position, int counterCountShip, 
			int row, int col)throws ArrayIndexOutOfBoundsException {
		if (position==0){
			for (int j=col;j>=col-counterCountShip-1;j--){
				if (myShips[row-1][j] || myShips[row+1][j]){
					return false;
				}
				else {
					myShipsChecked[row-1][j]=true;
					myShipsChecked[row+1][j]=true;
				}
			}
			
			for (int j=col;j>=col-counterCountShip-1;j--){
				myShipsChecked[row][j]=true;
			}
			
		}
		else if (position==1){
			for (int i=row;i>=row-counterCountShip-1;i--){
				if (myShips[i][col-1] || myShips[i][col+1]){
					return false;
				}
				else {
					myShipsChecked[i][col-1]=true;
					myShipsChecked[i][col+1]=true;
				}
			}
			for (int i=row;i>=row-counterCountShip-1;i--){
				myShipsChecked[i][col]=true;
			}			
		}			
		return true;
	}
	
	public void startGame(boolean beginner){
		logger.info("Start Game.");
		myShipsChecked = new boolean[10][10];
		for (int i = 0; i<10; i++){
			for (int j = 0; j<10; j++){
				myShipsChecked[i][j]=myShips[i+1][j+1];
			}
		}			
		if (beginner){		
			messegeArea.setText("fire!");
			moveLongTimer.restart();	
			sendButton.setEnabled(true);	
			while (!sendButton.isEnabled()){
				System.out.println("faaaaaaaalse");
				System.out.println("do="+sendButton.isEnabled());
				sendButton.setEnabled(true);
				System.out.println("posle="+sendButton.isEnabled());
				
			}
		}
		else {
			clientView.getController().send(ParseMessage.createXML(""+clientView.getUser().getId(),""+clientView.getController().getOpponentId(),"start",""));
			messegeArea.setText("wait opponent!");
			sendButton.setEnabled(false);
		}
		clientView.getController().setGame(true);
		
		GridBagConstraints c = new GridBagConstraints(3,2,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0);	
		gamePanel.add(myGameField,c);
		refreshGameFields();
		refreshMyGameFields();
	}

	/** checked ship is kill or wounded */
	public boolean isKill(int row, int col, int recurs) {
		if (!isLivingFields(row+1,col,recurs,1))
			return false;
		if (!isLivingFields(row,col+1,recurs,2))
			return false;			
		if (!isLivingFields(row-1,col,recurs,3))
			return false;
		if (!isLivingFields(row,col-1,recurs,4))
			return false;
		return true;
	}
	/** check one field near hit (used in isKill )  */
	public boolean isLivingFields(int i, int j, int recurs, int rec){
		if (myShips[i+1][j+1]) 
			if (myShipsChecked[i][j]){
				return false;
			}
			else {
				if (recurs==0 || recurs==rec )
					if (!isKill(i,j,rec)){
						return false;
					}					
			}
		return true;
	}
	
	/** filling the fields around ship */
	public void shipIsKill(JPanel [][]fields, int row, int col){			
		for (int i=row-1;i<row+2;i++){
			coloringFields(fields,i, col+1);
			coloringFields(fields,i, col-1);
		}
		coloringFields(fields,row-1,col);
		coloringFields(fields,row+1,col);
	}
	/** coloring fields around ship (used in shipIsKill) */
	public void coloringFields(JPanel [][]fields, int i, int j){
		if (i>=0 && i<10 && j>=0 && j<10) {
			if (fields[i][j].getBackground()==Color.RED){
				fields[i][j].setBackground(Color.PINK);
				shipIsKill(fields,i,j);
			}
			else  if (fields[i][j].getBackground()!=Color.PINK){
				fields[i][j].setBackground(Color.CYAN);
			}
		}
	}
	public void refreshGameFields() {
		for (int i=0;i<10;i++)
			for (int j=0;j<10;j++)
				gameFields[i][j].setBackground(new Color(65, 105, 225));
	}
	public void refreshMyGameFields() {
		for (int i=0;i<10;i++)
			for (int j=0;j<10;j++)
				if (myShipsChecked[i][j]) 
					myGameFields[i][j].setBackground(new Color(255, 140, 0));
				else
					myGameFields[i][j].setBackground(new Color(65, 105, 225));
	}
	
	public class PanelsMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {	}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			((JPanel) arg0.getSource()).setBorder(new BevelBorder(1,Color.BLACK,Color.GRAY));
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			((JPanel) arg0.getSource()).setBorder(new LineBorder(Color.BLACK,1));
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			((JPanel) arg0.getSource()).setBorder(new SoftBevelBorder(1,Color.BLACK,Color.GRAY));
			
			JPanel target = (JPanel) arg0.getSource();
			
			if (target.getBackground()==Color.CYAN || target.getBackground()==Color.RED || 
					target.getBackground()==Color.PINK || (clientView.getUser().isReady() && !clientView.getController().isGame() )) {
			}
			else if (target.getBackground()==Color.WHITE) {
				target.setBackground(new Color(65, 105, 225));
			}
			else {
				if (clientView.getController().isGame()){
					for (int i=0;i<10;i++){
						for (int j=0;j<10;j++){
							if (gameFields[i][j].getBackground()==Color.WHITE){
								gameFields[i][j].setBackground(new Color(65, 105, 225));
							}
						}
					}
				}
				target.setBackground(Color.WHITE);
			}			
			((JPanel) arg0.getSource()).setBorder(new BevelBorder(1,Color.BLACK,Color.GRAY));
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
		}
		
	}
	public void addGamePanel() {
		refreshGameFields();
		messegeArea.setText("Please, Arrange your ships!");		
		sendButton.setEnabled(true);
		
	}
	public boolean checkAliveShips() {
		for (int i=0;i<10;i++)
			for (int j=0;j<10;j++)
				if (myShipsChecked[i][j])
					return true;
		return false;
	}
	public void fire(Point p){
		if (myShipsChecked[p.x][p.y]){
			myShipsChecked[p.x][p.y]=false;
			myGameFields[p.x][p.y].setBackground(Color.RED);
			if (checkAliveShips()){
				boolean isKill=false;
					isKill = isKill(p.x,p.y,0);
				
				if (isKill){
					logger.info("User's ship is kill.");
					clientView.getController().send(ParseMessage.createXML(""+clientView.getController().getUser().getId(),""+clientView.getController().getOpponentId(),"hit","kill"));
					messegeArea.setText("your ship is kill");
					shipIsKill(myGameFields,p.x,p.y);
					for (int i=0;i<10;i++)
						for (int j=0;j<10;j++)
							if (myGameFields[i][j].getBackground()==Color.PINK)
								myGameFields[i][j].setBackground(Color.RED);
				}
				else {
					logger.info("User's ship is wounded.");
					clientView.getController().send(ParseMessage.createXML(""+clientView.getController().getUser().getId(),
							""+clientView.getController().getOpponentId(),"hit","wounded"));
					messegeArea.setText("your ship is wounded");
				}							
			}
			else {
				clientView.gameOver( false);
			}							
		}
		else {
			myGameFields[p.x][p.y].setBackground(Color.CYAN);
			messegeArea.setText("fire!");
			sendButton.setEnabled(true);
			while (!sendButton.isEnabled()){
				System.out.println("faaaaaaaalse");
				System.out.println("do="+sendButton.isEnabled());
				sendButton.setEnabled(true);
				System.out.println("posle="+sendButton.isEnabled());
				
			}
			moveLongTimer.restart();
		}		
	}
	public void hit(String content){
		gameFields[oldTargetX][oldTargetY].setBackground(Color.RED);
		
		if (content.equals("kill")){
			
			shipIsKill(gameFields,oldTargetX, oldTargetY);
			
			for (int i=0;i<10;i++)
				for (int j=0;j<10;j++)
					if (gameFields[i][j].getBackground()==Color.PINK)
						gameFields[i][j].setBackground(Color.RED);
		}
		sendButton.setEnabled(true);
		while (!sendButton.isEnabled()){
			System.out.println("faaaaaaaalse");
			System.out.println("do="+sendButton.isEnabled());
			sendButton.setEnabled(true);
			System.out.println("posle="+sendButton.isEnabled());
			
		}
		messegeArea.setText("you "+content+" opponent's ship. Try again!");
		logger.info("Opponent's ship is "+content);
		moveLongTimer.restart();
	}
	public void setMessegeArea(String str){
		messegeArea.setText(str);
	}
	public void endGame(){
		gamePanel.remove(myGameField);
		myShipsChecked = new boolean[12][12];
		moveLongTimer.stop();
	}
}

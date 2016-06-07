package controller;

import model.ParseMessage;
import model.User;
import org.slf4j.*;
import view.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/* Class for create window*/
public class ControlView extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ControlView.class);
	private Client controller;
	private User user;
		
	private HeadingPanel headPanel;	
	private LeftPanel leftPanel;	
	private AnswerButtonsPanel answButPanel;
	private GamePanel gamePanelInst;
	private JPanel centralPanel  = new JPanel();
	private AdvertisementPanel advertisementPanel;
	
	private Authorization authorizationPanel;
	
	private RegistrationPanel registrationPanel;

	public ControlView(Client control){	
		super("User");
		controller=control;
		gamePanelInst = new GamePanel(this);
		headPanel = new HeadingPanel(this);
		answButPanel = new AnswerButtonsPanel(this);
		leftPanel = new LeftPanel(this);
		authorizationPanel = new Authorization(this);		
		registrationPanel = new RegistrationPanel(this);
		advertisementPanel = new AdvertisementPanel(this);
		
		setSize(350,130);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setVisible(true);			
		
		addAuthorizationPanel();	
		
		validate();					
	}		
	public GamePanel getGamePanelInst(){
		return gamePanelInst;
	}
	public LeftPanel getLeftPanel(){
		return leftPanel;
	}
	public HeadingPanel getHeadPanel(){
		return headPanel;
	}
	public User getUser() {
		return user;
	}
	public Client getController() {
		return controller;
	}
	public JPanel getCentralPanel() {
		return centralPanel;
	}
	public Authorization getAuthorizationPanel() {
		return authorizationPanel;
	}
	public RegistrationPanel getRegistrationPanel() {
		return registrationPanel;
	}
	public void authorizationSuccessful(User user){
		this.user=user;
		logger.info("Authorization successful");
		setTitle(user.getLogin());	
		remove(getAuthorizationPanel());
		setSize(900,400);
		
		add(leftPanel, BorderLayout.WEST);
		
		add(headPanel,BorderLayout.NORTH);	
		add(getCentralPanel(),BorderLayout.CENTER);	
		
		headPanel.updateHeadingPanel();
	}
	public void sendUpdateUser(){
		StringBuilder content = new StringBuilder("");
		content.append("<countGamesWon>")
			.append(getUser().getCountGamesWon())
			.append("</countGamesWon>")
			.append("<countLostGames>")
			.append(getUser().getCountLostGames())
			.append("</countLostGames><points>")
			.append(getUser().getPoints())
			.append("</points>");
		
		getController().send(ParseMessage.createXML(""+getUser().getId(),""+0,"update user",content.toString()));
	}
	
	public void addRegistrationPanel(){
		remove(getAuthorizationPanel());
		add(getRegistrationPanel(),BorderLayout.CENTER);
		repaint();
		validate();
	}
	public void addAuthorizationPanel(){
		remove(getRegistrationPanel());
		add(getAuthorizationPanel(),BorderLayout.CENTER);	
		repaint();
		validate();
	}
	
	/* create buttons for answer yes/no */
	public void addAnswerButtons(String opponentLogin){
		getController().changeFree(false);
		getCentralPanel().removeAll();
		getCentralPanel().add(answButPanel);  
		answButPanel.getQueryLabel().setText("do you want play with "+opponentLogin);
		validate();
	}
	/* create messege about waiting enemy */
	public void waitingEnemy(){		
		getController().changeFree(false);
		displayAdvertisement("waiting enemy");
		advertisementPanel.cancelRequestSetVisible(true);
		validate();
	}	
	/* create messege about failure opponent */
	public void createFailure() {
		getController().changeFree(true);		
		displayAdvertisement("opponent failure");
		validate();
	}
	public void displayAdvertisement(String str){
		advertisementPanel.setAdvertisement(str);
		getCentralPanel().removeAll();
		getCentralPanel().add(advertisementPanel);
	}		
	
	public void createGamePanel(){	
		getController().changeFree(false);
		gamePanelInst.addGamePanel();
		getCentralPanel().removeAll();
		getCentralPanel().add(gamePanelInst.getGamePanel());
		validate();
	}
	public void gameOver( boolean result){
		if (result){
			logger.info("game over. user winner.");
			displayAdvertisement("WINNER!");
			if (getController().isGame())
				getUser().setCountGamesWon(getUser().getCountGamesWon() + 1);
		}
		else {
			logger.info("game over. user loser.");
			getController().send(ParseMessage.createXML(""+getUser().getId(),""+getController().getOpponentId(),"game over",""+getUser().getPoints()));
			displayAdvertisement("LOSER!");
			getUser().setCountLostGames(getUser().getCountLostGames() + 1);
		}
		getController().changeFree(true);
		headPanel.updateHeadingPanel();
		sendUpdateUser();
		getUser().setReady(false);
		getController().setGame(false);
		gamePanelInst.endGame();
		
		validate();
		repaint();
	}
	/* Class for mouse event listener CLICK ON USER */
	public class UsersClickMouseListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel source = (JLabel) e.getSource();
			String name = source.getText();
			int id = 0;
			
			for (int i=0;i<leftPanel.activeUsers.length;i++){
				if (name.equals(leftPanel.activeUsers[i].getLogin())){
					id=leftPanel.activeUsers[i].getId();
					break;
				}
			}			
			
			if (getUser().isFree()){
				if (!name.equals(getUser().getLogin())){
					getController().send(ParseMessage.createXML(""+getUser().getId(),String.valueOf(id),"challenge",getUser().getLogin()));
					logger.info("User try play with "+getUser().getLogin());
					waitingEnemy();
				}
				else {
					logger.info("User try play with himself");
					displayAdvertisement("You do not can play himself");
				}
			}				
			validate();
		}
	}
	public class AnswerButtonsActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			
			getController().send(ParseMessage.createXML(""+getUser().getId(),""+getController().getOpponentId(),"answer",source.getText()));
			remove(answButPanel);
			
			if (source.getText().equals("Yes")){
				logger.info("User agree for game");
				createGamePanel();
			}
			else {
				logger.info("User disagree for game");
				getCentralPanel().removeAll();
				getController().changeFree(true);
			}
			validate();
			repaint();			
		}		
	}	
}

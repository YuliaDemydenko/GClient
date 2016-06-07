package view;

import controller.ControlView;

import javax.swing.*;
import java.awt.*;

public class HeadingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel headingLabel = new JLabel();
	private JTextArea countWonArea = new JTextArea();
	private JTextArea countLostArea = new JTextArea();
	private JTextArea pointArea = new JTextArea();	
	
	private ControlView clientView;
	
	
	public HeadingPanel(ControlView clientView){
		this.clientView=clientView;
		
		countWonArea.setEditable(false);
		countLostArea.setEditable(false);
		pointArea.setEditable(false);
		
		JLabel countWonLabel = new JLabel("Count won:");
		JLabel countLostLabel = new JLabel("Count lost:");
		JLabel pointLabel = new JLabel("Points:");
		
		setLayout(new FlowLayout());
		
		add(headingLabel);
		add(countWonLabel);
		add(countWonArea);
		add(countLostLabel);
		add(countLostArea);
		add(pointLabel);
		add(pointArea);
	}
	
	public void updateHeadingPanel(){
		countWonArea.setText(""+clientView.getUser().getCountGamesWon());
		countLostArea.setText(""+clientView.getUser().getCountLostGames());
		pointArea.setText(""+clientView.getUser().getPoints());
		
		clientView.validate();
	}
}

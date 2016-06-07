package view;

import controller.ControlView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = LoggerFactory.getLogger(StartPanel.class);

	protected JLabel advertisementLabelLogin = new JLabel();
	protected JLabel loginLabel = new JLabel("Login");
	protected JTextField loginField = new JTextField(20);
	protected JLabel passwordLabel = new JLabel("Password");
	protected JPasswordField passwordField = new JPasswordField(20);
	protected JButton registrationButton = new JButton("Registration");
	protected ControlView clientView;
	
	public StartPanel(ControlView clientVieew){
		this.clientView=clientVieew;
		
		setLayout(new GridLayout(5,5));
		
		add(loginLabel);
		add(loginField);
		add(passwordLabel);
		add(passwordField);		
	}
	public void setAdvertisement(String str){
		advertisementLabelLogin.setText(str);
	}
}

package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import controller.ControlView;
import model.ParseMessage;

public class RegistrationPanel extends StartPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel passwordLabelRepeat = new JLabel("Repeat password");
	private JPasswordField passwordFieldRepeat = new JPasswordField(20);
	private JButton cancelRegistrationButton = new JButton("Cancel");
	
	public RegistrationPanel(ControlView clientVieew){
		super(clientVieew);
		
		add(passwordLabelRepeat);
		add(passwordFieldRepeat);
		add(registrationButton);
		add(cancelRegistrationButton);
		add(advertisementLabelLogin);
		
		registrationButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
					if (loginField.getText().isEmpty() || passwordField.getPassword().length<8 ||
							passwordFieldRepeat.getPassword().length<8){
						advertisementLabelLogin.setText("Password (min 8 chars)");
						logger.debug("Password (min 8 chars)");
					}
					else if (equalityPasswords(passwordField.getPassword(), passwordFieldRepeat.getPassword())){
						clientView.getController().send(ParseMessage.createXML("",""+0,"registration",
								"<login>"+loginField.getText()+"</login><password>"+
								new String(passwordField.getPassword())+"</password>"));
					}
					else {
						advertisementLabelLogin.setText("Passwords is not equals");
						logger.debug("Passwords is not equals");
					}				
				repaint();
			}			
		});
		
		cancelRegistrationButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clientView.addAuthorizationPanel();
			}
			
		});
	}
	public boolean equalityPasswords(char []p1, char []p2){
		if (p1.length!=p2.length)
			return false;
		for (int i=0;i<p1.length;i++){
			if (p1[i]!=p2[i])
				return false;
		}
		return true;
	}
	public JButton getCancelRegistrationButton(){
		return cancelRegistrationButton;
	}
}

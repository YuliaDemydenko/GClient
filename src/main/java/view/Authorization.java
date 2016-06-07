package view;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import controller.ControlView;
import model.ParseMessage;

public class Authorization  extends StartPanel {
	private static final long serialVersionUID = 1L;	
	private JButton entryButton = new JButton("Log in");
	
	public Authorization(ControlView clientVieew){
		super(clientVieew);
		add(registrationButton);
		add(entryButton);
		add(advertisementLabelLogin);
		
		entryButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String pass = new String(passwordField.getPassword());
				if (pass.length()<8) {
					advertisementLabelLogin.setText("Small password");
					logger.debug("Small password.");
				}
				else {
					StringBuilder sb = new StringBuilder();
					sb.append("<login>")
						.append(loginField.getText())
						.append("</login><password>")
						.append(pass)
						.append("</password>");					
					clientView.getController().send(ParseMessage.createXML("",""+0,"login",sb.toString()));
				}
			}				
		});
		
		registrationButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {				
				clientView.addRegistrationPanel();				
				repaint();
			}			
		});
	}
}

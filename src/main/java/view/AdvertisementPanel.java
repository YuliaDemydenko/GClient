package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.ControlView;
import model.ParseMessage;

public class AdvertisementPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel advertisementLabel = new JLabel();
	private JButton cancelRequest = new JButton("Cancel");
	
	public AdvertisementPanel(final ControlView clientView){
		add(cancelRequest);
		add(advertisementLabel);
		
		cancelRequestSetVisible(false);
		
		cancelRequest.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clientView.getController().cancelRequest();
			}			
		});
	}
	public void cancelRequestSetVisible(boolean visible){
		cancelRequest.setVisible(visible);
	}
	public void setAdvertisement(String str){
		cancelRequestSetVisible(false);
		advertisementLabel.setText(str);
	}
}

package view;

import controller.ControlView;
import model.ParseMessage;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LeftPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private ArrayList<JLabel> usersList = new ArrayList<JLabel>();
	public User activeUsers[];
	
	private JPanel panelUsers = new JPanel();
	private JScrollPane scrollUsers = new JScrollPane(panelUsers);
	
	private JPanel panelBusyUsers = new JPanel();
	private JScrollPane scrollBusyUsers = new JScrollPane(panelBusyUsers);
	private ControlView clientView;
	public LeftPanel(ControlView clientView){
		this.clientView=clientView;
		setLayout(new GridLayout(2,1));
		
		panelUsers.setLayout(new BoxLayout(panelUsers, BoxLayout.Y_AXIS));
		panelBusyUsers.setLayout(new BoxLayout(panelBusyUsers, BoxLayout.Y_AXIS));
		scrollUsers.setPreferredSize(new Dimension(200, 50));
		scrollBusyUsers.setPreferredSize(new Dimension(200, 50));
		
		add(scrollBusyUsers);
		add(scrollUsers);
	}
	
	public void refreshUsersPanel(ParseMessage docXML) {
		panelUsers.removeAll();
		panelBusyUsers.removeAll();
		panelUsers.add(new JLabel("Free users:"));
		panelBusyUsers.add(new JLabel("Busy users:"));
		
		activeUsers = docXML.getUsers(false);
		for (int i=0;i<activeUsers.length;i++){
			addUser(activeUsers[i]);
		}		
		
		User usersBusy[] = docXML.getUsers(true);
		for (int i=0;i<usersBusy.length;i++){
			addBusyUser(usersBusy[i]);
		}	
	}
	public void addUser(User user) {
		usersList.add(new JLabel(user.getLogin()));
		panelUsers.add(usersList.get(usersList.size()-1));		
		
		usersList.get(usersList.size()-1).addMouseListener(clientView.new UsersClickMouseListener());
	}
	public void addBusyUser(User user) {
		panelBusyUsers.add(new JLabel(user.getLogin()));				
	}
	
}

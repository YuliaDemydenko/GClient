package controller;
import model.ParseMessage;
import model.User;
import org.slf4j.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client  {
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private User user;
	private int opponentId;
	
	private Socket clientSocket;
	private BufferedReader in;
	private PrintWriter out;
	private ControlView window;
	private boolean isGame = false;
	public static final double coefficient = 0.1;
	
	private Timer timerCheckUsers = new Timer (11000, new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			window.displayAdvertisement("Server disconnected :(");
			window.getAuthorizationPanel().setAdvertisement("Server disconnected :(");
			window.getRegistrationPanel().setAdvertisement("Server disconnected :(");
			window.validate();
			window.repaint();
		}		    		
	});
	
	public void changeFree(boolean free){
		if (free!=user.isFree()){
			user.setFree(free);
			send(ParseMessage.createXML(""+user.getId(),""+0,"setFree",""+free));
		}
	}
	
	public static void main(String[] args) {
		logger.info("Client created!");
		Client client = new Client();
		client.execute();
	}
	
	public void execute(){
		try {
			clientSocket = new Socket("localhost",3456);	
			timerCheckUsers.start();
			window = new ControlView(this);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(),true);
				
			
			String userInput;
			ParseMessage xmlDoc;
			
			while ((userInput = in.readLine()) != null) {		
				
				xmlDoc=new ParseMessage(userInput);
				String title = xmlDoc.getTitle();
				
				
				if (xmlDoc.getFrom()==0){
					if (title.equals("ban")){
						logger.info("user banned.");
						window.getAuthorizationPanel().setAdvertisement("You are banned!");
					}
					else if (title.equals("userList")){
						window.getLeftPanel().refreshUsersPanel(xmlDoc);
					}
					else if (title.equals("ping")){
						send(ParseMessage.createXML("",""+0,"ping",""));
						timerCheckUsers.restart();
					}
					else if (title.equals("busy")){
						window.createFailure();
						window.displayAdvertisement("user is busy");
					}
					else if (title.equals("login")){
						
						if (xmlDoc.getContent()!=null){
							if (xmlDoc.getContent().equals("false")){
								window.getAuthorizationPanel().setAdvertisement("input correct login and password");
							}
							else if (xmlDoc.getContent().equals("use")){
								window.getAuthorizationPanel().setAdvertisement("user active");
							}
						}
						else {
							user = xmlDoc.getUser();							
							window.authorizationSuccessful(user);
							window.getHeadPanel().updateHeadingPanel();
						}
					}
					else if (title.equals("registration")){
						if (xmlDoc.getContent().equals("true")){
							logger.info("Registration successful.");
							window.addAuthorizationPanel();
							window.getAuthorizationPanel().setAdvertisement("Registration successful");
						}
						else {
							logger.info("Registration failure.");
							window.getRegistrationPanel().setAdvertisement("login is not available");
						}
					}
				}
				else {
					logger.debug("get messege with title =\""+title+"\" from "+xmlDoc.getFrom());
					if (title.equals("challenge")){
						opponentId=xmlDoc.getFrom();
						send(ParseMessage.createXML(""+user.getId(), ""+0, "opponentId",""+opponentId ));
						send(ParseMessage.createXML(""+user.getId(), ""+opponentId, "opponentId","" ));
						window.addAnswerButtons(xmlDoc.getContent());
					}
					else if (title.equals("opponentId")){
						opponentId=xmlDoc.getFrom();
					}
					else if (title.equals("answer")){
						String answer = xmlDoc.getContent();
						if (answer.equals("Yes")){
							window.createGamePanel();						
						}
						else if (answer.equals("No")){
							window.createFailure();
						}
					}
					else if (title.equals("ready")){
						if (user.isReady()){
							window.getGamePanelInst().startGame(false);
						}
						else {
							window.getGamePanelInst().setMessegeArea("Hurry up, the opponent is waiting for you!");
						}
					}
					else if (title.equals("start")){
						window.getGamePanelInst().startGame(true);
					}
					else if (title.equals("fire")){
						Point p = xmlDoc.getPointFire();
						window.getGamePanelInst().fire(p);			
					}
					else if (title.equals("hit")){
						window.getGamePanelInst().hit(xmlDoc.getContent());						
					}
					else if (title.startsWith("game over")){						
						if (isGame()) 
							user.setPoints((int) (user.getPoints() + coefficient*Integer.parseInt(xmlDoc.getContent())));
						window.gameOver(true);
						
						if (title.equals("game over disconnected")){
							window.displayAdvertisement("opponent disconnected!");
						}						
					}
					else if (title.startsWith("Cancel request")){
						changeFree(true);
						window.displayAdvertisement("Opponent canceled request!");						
					}
				}
				window.validate();
				window.repaint();
			}			
		} catch (IOException e) {
			logger.error(String.valueOf(e));
		}
		finally {
			logger.info("Client disconnected!");
			try {
				if (clientSocket!=null)
					clientSocket.close();
				if (in!=null)
					in.close();
				if (out!=null)
					out.close();
			} catch (IOException e) {
				logger.error(String.valueOf(e));
			}
		}
	}
	
	public void send(String str){
		out.println(str);		
	}
	
	public User getUser() {
		return user;
	}
	public int getOpponentId() {
		return opponentId;
	}

	public boolean isGame() {
		return isGame;
	}
	public void setGame(boolean isGame) {
		this.isGame = isGame;
	}

	public void cancelRequest() {
		send(ParseMessage.createXML(String.valueOf(getUser().getId()),String.valueOf(getOpponentId()),"Cancel request",""));
		changeFree(true);
		window.displayAdvertisement("Request canceled!");
	}

	
}

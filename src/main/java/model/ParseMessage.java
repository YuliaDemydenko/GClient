package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ParseMessage {
	protected static final Logger logger = LoggerFactory.getLogger(ParseMessage.class);
	private Document xmlDoc=null;
	
	public ParseMessage (String xmlString){
		DocumentBuilderFactory DocFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder xmlDocBuilder = null;
		try {
			xmlDocBuilder = DocFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Parse XML failure.");	
		}
		ByteArrayInputStream theStream;
	    theStream = new ByteArrayInputStream( xmlString.getBytes() );
	    
	    try {
			xmlDoc = xmlDocBuilder.parse(theStream);
		} catch ( IOException e) {
			logger.error("Parse XML failure.");	
		} catch (SAXException e){
			logger.error("Parse XML failure.");	
		}
	}
	public String getTitle(){
	   return xmlDoc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
	}
	public int getFrom(){		
		 return Integer.parseInt(xmlDoc.getElementsByTagName("from").item(0).getFirstChild().getNodeValue());
	}
	public int getTo(){		
		 return Integer.parseInt(xmlDoc.getElementsByTagName("to").item(0).getFirstChild().getNodeValue());
	}
	public String getContent(){
		return xmlDoc.getElementsByTagName("content").item(0).getFirstChild().getNodeValue();
	}
	public User[] getUsers(Boolean busy) {
		String userType;
		if (busy)
			userType="busyUser";
		else 
			userType="user";
		
		NodeList users = xmlDoc.getElementsByTagName(userType);
	   
		int usersLength = users.getLength();	    
	    User [] newUsers = new User[usersLength];
	    
	    for (int i=0; i < usersLength; i++) {
            Element user = (Element) users.item(i); 
            NodeList loginAndPas = user.getChildNodes();
            Element userId = (Element) loginAndPas.item(0); 
            Element userLogin = (Element) loginAndPas.item(1); 

            newUsers[i] = new User( Integer.parseInt(userId.getTextContent()), userLogin.getTextContent());
	    }	    
		return newUsers;	    		
	}
	
	public User getUser(){		
		Node userNode = xmlDoc.getElementsByTagName("user").item(0);
		NodeList userChildNodes = userNode.getChildNodes();
		
		User user = new User(Integer.parseInt(userNode.getAttributes().getNamedItem("id").getTextContent()),
				userChildNodes.item(0).getTextContent(),
				userChildNodes.item(1).getTextContent(),
				Integer.parseInt(userChildNodes.item(2).getTextContent()),
				Integer.parseInt(userChildNodes.item(3).getTextContent()),
				Integer.parseInt(userChildNodes.item(4).getTextContent())
			);
		
		return user;
	}
	public Point getPointFire(){
		Point p = new Point();
		p.x=Integer.parseInt(xmlDoc.getElementsByTagName("row").item(0).getFirstChild().getNodeValue());
		p.y=Integer.parseInt(xmlDoc.getElementsByTagName("col").item(0).getFirstChild().getNodeValue());
		return p;		
	}
	public String getCountGamesWon(){
		return xmlDoc.getElementsByTagName("countGamesWon").item(0).getFirstChild().getNodeValue();
	}
	public String getCountLostGames(){
		return xmlDoc.getElementsByTagName("countLostGames").item(0).getFirstChild().getNodeValue();
	}
	public String getPoints(){
		return xmlDoc.getElementsByTagName("points").item(0).getFirstChild().getNodeValue();
	}
	public static String createXML(String from, String to, String title, String content){
		StringBuilder xmlString = new StringBuilder("<?xml version='1.0'?>");
		xmlString.append("<GameServer>")
			.append("<title>")
			.append(title)
			.append("</title>")
			.append("<from>")
			.append(from)
			.append("</from>")
			.append("<to>")
			.append(to)
			.append("</to>")
			.append("<content>")
			.append(content)
			.append("</content>")
			.append("</GameServer>");
		return xmlString.toString();			
	}
}

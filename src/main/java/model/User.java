package model;

public class User {
	private int id;
	private String login;
	private String password;
	private int countGamesWon;
	private int countLostGames;
	private int points;
	private boolean isFree=true;
	private boolean isReady=false;
	
		
	public boolean isFree(){
		return isFree;
	}
	public void setFree(boolean free){
		this.isFree=free;
	}
	public boolean isReady(){
		return isReady;
	}
	
	public void setReady(boolean ready){
		this.isReady=ready;
	}
	public User(int id, String login){
		this.id=id;
		this.login=login;
	}
			
	public User(int id, String login, String password, int countGamesWon, int countLostGames, int points ){
		this.id=id;
		this.login=login;
		this.password=password;
		this.countGamesWon=countGamesWon;
		this.countLostGames=countLostGames;
		this.points=points;
	}
	public int getId() {
		return id;
	}
	public String getLogin() {
		return login;
	}
	public String getPassword() {
		return password;
	}
	public int getCountGamesWon() {
		return countGamesWon;
	}
	public void setCountGamesWon(int countGamesWon) {
		this.countGamesWon = countGamesWon;
	}
	public int getCountLostGames() {
		return countLostGames;
	}
	public void setCountLostGames(int countLostGames) {
		this.countLostGames = countLostGames;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
}

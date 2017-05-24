package com.makeurpicks.domain;

public class PlayerWins {

	private String playerId;
	private int wins;
	private int picks;
	public String getPlayerId() {
		return playerId;
	}
	
	public int getWins() {
		return wins;
	}
	
	public int getPicks() {
		return picks;
	}
	
	
	public static PlayerWins build(String playerId, int wins, int picks)
	{
		PlayerWins pw = new PlayerWins();
		pw.playerId = playerId; 
		pw.wins = wins;
		pw.picks = picks;
		return pw;
	}
	
	public static PlayerWins build(String playerId, int wins)
	{
		PlayerWins pw = new PlayerWins();
		pw.playerId = playerId; 
		pw.wins = wins;
		return pw;
	}
}

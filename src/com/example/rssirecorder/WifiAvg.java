package com.example.rssirecorder;

public class WifiAvg {
	private int count;
	private int levelSums;
	
	

	public WifiAvg(int initialValue){
		count = 1;
		levelSums = initialValue;
	}
	
	public int giveAverageRSSI(){
		return levelSums/count;
	}
	public void addToTotal(int db){
		count++;
		levelSums += db;
	}

}

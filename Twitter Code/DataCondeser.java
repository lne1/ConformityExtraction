package com.twitter.hbc.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class DataCondeser {

	public static void main(String[] args) {
		
		
		HashMap<Long, ArrayList<String>> lebron = new HashMap<Long, ArrayList<String>>();
		HashMap<Long, ArrayList<String>> gameGate = new HashMap<Long, ArrayList<String>>();
		HashMap<Long, ArrayList<String>> nyc = new HashMap<Long, ArrayList<String>>();
		ArrayList<Long> lebronFive = new ArrayList<Long>();
		ArrayList<Long> gamerGateFive = new ArrayList<Long>();
		ArrayList<Long> nycFive = new ArrayList<Long>();
		
		for(int i=1; i<46; i++){
			try{
				Scanner inFile=new Scanner(new File("F:/Alex Files/Twitter Data/Twitter Data "+i+".txt"));	
				
				while(inFile.hasNextLine()){
					String s = inFile.nextLine();
					
					//System.out.println(s);
					
					String[] split = s.split("|");
					int indexOf = s.indexOf("|");
					split[0]= s.substring(0, indexOf-1);
					split[1]=s.substring(indexOf+2);
					
					Long id = 0L;
					try{
						id = Long.parseLong(split[1]);
					}catch(Exception e){
						continue;
					}
					
					ArrayList<String> tweet = new ArrayList<String>();
					tweet.add(split[0]);
					if(s.contains("nyc")|| s.contains("NYC") || s.contains("New York")){
						if(nyc.containsKey(id)){
							nyc.get(id).add(split[0]);
							if(nyc.get(id).size()==10){
								nycFive.add(id);								
							}
						}else{
							nyc.put(id, tweet);
						}
					}
					
					if(s.contains("Lebron")){
						if(lebron.containsKey(id)){
							lebron.get(id).add(split[0]);
							
							if(lebron.get(id).size()==5){
								lebronFive.add(id);								
							}
						}else{
							lebron.put(id, tweet);
						}
					}
					
					if(s.contains("GamerGate")){
						if(gameGate.containsKey(id)){
							gameGate.get(id).add(split[0]);
							
							if(gameGate.get(id).size()==10){
								gamerGateFive.add(id);								
							}
						}else{
							gameGate.put(id, tweet);
						}
					}
				}
				inFile.close();
				
						
						
			}catch(Exception e){
				System.out.println("error reading file: "+e);
			}
			
		}
		
		System.out.println("!!!!!!!!!!!!!!! Finished Reading !!!!!!!!!!!!!!!!!!!!!!!!");
		
		try {
			PrintWriter outFile = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/Lebron Data.txt"));
			System.out.println("Lebron users = "+lebronFive.size());
			for(Long l : lebronFive){
				ArrayList<String> arrayList = lebron.get(l);
				outFile.println(l.toString());
				for(String s : arrayList){
					outFile.println(s);
				}
				outFile.println();
			}
			outFile.close();
			
			
			
			outFile = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/GamerGate Data.txt"));
			System.out.println("GamerGate Users = "+ gamerGateFive.size());
			for(Long l : gamerGateFive){
				ArrayList<String> arrayList = gameGate.get(l);
				outFile.println(l.toString());
				for(String s : arrayList){
					outFile.println(s);
				}
				outFile.println();
			}
			outFile.close();
			
			outFile = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/NYC Data.txt"));
			System.out.println("NYC Users = "+nycFive.size());
			for(Long l : nycFive){
				ArrayList<String> arrayList = nyc.get(l);
				outFile.println(l.toString());
				for(String s : arrayList){
					outFile.println(s);
				}
				outFile.println();
			}
			outFile.close();			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TwitterUserGrab.getFollowers(nycFive);
		
	

	}

}

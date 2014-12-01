package com.twitter.hbc.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class JsonMaker {

	
	public static void main(String[] args) {
		
		try{
			Scanner lebronTweets=new Scanner(new File("F:/Alex Files/Twitter Data/Lebron Data.txt"));	
			Scanner lebronFollowing=new Scanner(new File("F:/Alex Files/Twitter Data/Lebron following Data.txt"));	
			PrintWriter lebronJson = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/Lebron Json.txt"));	
			ArrayList<Long> errorIDs = new ArrayList<Long>();
			ArrayList<user> users = new ArrayList<user>();				
			HashMap<Long, ArrayList<Long>> followData = new HashMap<Long, ArrayList<Long>>();
			
			
			while(lebronFollowing.hasNextLine()){
				String s = lebronFollowing.nextLine();
				int indexOf = s.indexOf("{");
				Long userId = Long.parseLong(s.substring(0, indexOf-2));				
				int arrayStart = s.indexOf("[");
				int arrayEnd = s.indexOf("]");
				if(arrayStart == -1 || s.contains("error")){	
					errorIDs.add(userId);
					continue;
				}
				
				s=s.substring(arrayStart+1, arrayEnd);
				String[] split = s.split(",");
				
				
				ArrayList<Long> followIDs = new ArrayList<Long>();				
				for(String l : split){
					try{
						followIDs.add(Long.parseLong(l));
					}catch(Exception e){
						//This happens when a user isn't following anyone 
						continue;
					}
				}				
				followData.put(userId, followIDs);				
			}
			lebronFollowing.close();	
			
					
			while(lebronTweets.hasNextLine()){
				String s = lebronTweets.nextLine();
				Long userId =Long.parseLong(s);				
				ArrayList<tweet> tweets = new ArrayList<tweet>();				
				s = lebronTweets.nextLine();
				while(s.length()!=0){							
					tweet t = new tweet(s);
					tweets.add(t);	
					s=lebronTweets.nextLine();
				}		
				if(!errorIDs.contains(userId)){
					user u = new user(userId, followData.get(userId), tweets);
					JSONObject json = u.getJson();				
					lebronJson.println(json);					
					users.add(u);						
				}
				
			}
			lebronJson.close();
			lebronTweets.close();
			System.out.println("All done");
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	
	
	
	public static class user{
		public Long userId;
		public ArrayList<Long> followingIDs;
		public ArrayList<tweet> tweets;
		
		public user(Long i, ArrayList<Long> l, ArrayList<tweet> t ){
			userId = i;
			followingIDs = l;
			tweets = t;
		}	
		
		@SuppressWarnings("unchecked")
		public JSONObject getJson() throws JSONException{
			JSONObject u = new JSONObject();	
			u.put("userID", userId);
			
			JSONArray followinglist = new JSONArray();			
			for(Long l : followingIDs){
				followinglist.add(l.toString());
			}			
			u.put("followingIDs", followinglist);
			
			JSONArray tweetsList = new JSONArray();
			for(tweet t : tweets){
				tweetsList.add(t.getJson());
			}
			u.put("tweets", tweetsList);			
			return u;			
		}
	}
	
	public static class tweet{
		public String text;
		public double sentimentValue;		
		
	
		public tweet(String s){
			text = s;
			sentimentValue = 2.0;
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject getJson() throws JSONException{
			JSONObject t = new JSONObject();	
			t.put("text", text);
			t.put("sentimentValue", sentimentValue);
			return t;
			
		}
	}

}

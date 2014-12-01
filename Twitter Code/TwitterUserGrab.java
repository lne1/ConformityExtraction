package com.twitter.hbc.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class TwitterUserGrab {
	
	public static void main(String[] args){
		try{
			Scanner inFile=new Scanner(new File("F:/Alex Files/Twitter Data/nyc following Data.txt"));	
			ArrayList<Long> errorIDs = new ArrayList<Long>();
			ArrayList<Long> ids = new ArrayList<Long>();
			HashMap<Long, ArrayList<Long>> followData = new HashMap<Long, ArrayList<Long>>();
			
			
			while(inFile.hasNextLine()){
				String s = inFile.nextLine();
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
				
				ids.add(userId);
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
			System.out.println("all Done");
			
			inFile.close();
			
			
			PrintWriter outFile = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/Lebron adjacancey matrix TFS.txt"));			
			Collections.sort(ids);
			int count = 0;			
			for(int i = 0; i<ids.size(); i++){	
				String row ="";
				for(int j = 0; j<ids.size(); j++){				
					ArrayList<Long> following = followData.get(ids.get(j));					
					if(following.contains(ids.get(i))){
						row+="1 ";	
						count++;
					}else{
						row+="0 ";						
					}
				}
				row+=";";
				outFile.println(row);
				//System.out.println(row);
			}
			System.out.println("There where "+count+" connections");
			
			outFile.close();
			
			
		
			outFile = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/Lebron adjacancey matrix SFT.txt"));			
			Collections.sort(ids);
			count = 0;			
			for(int i = 0; i<ids.size(); i++){	
				String row ="";
				ArrayList<Long> following = followData.get(ids.get(i));					
				for(int j = 0; j<ids.size(); j++){				
					if(following.contains(ids.get(j))){
						row+="1 ";	
						count++;
					}else{
						row+="0 ";						
					}
				}
				row+=";";
				outFile.println(row);
				//System.out.println(row);
			}
			System.out.println("There where "+count+" connections");
			
			outFile.close();
						
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void getFollowers(ArrayList<Long> userIDs){
		try{
		
		Scanner in = new Scanner(System.in);
		OAuthService service = new ServiceBuilder()
		.provider(TwitterApi.SSL.class)
		.apiKey("JyXRqWjMgEUkdcxvzmCjnVTB6")
		.apiSecret("L6HSmdxocKYsIpO3EL9JkpynkQ7ABWWZtUiNFdWSHC1bjSwLcf")
		.build();
	
		Token requestToken = service.getRequestToken();
		
		 System.out.println("Now go and authorize Scribe here:");
		    System.out.println(service.getAuthorizationUrl(requestToken));
		    System.out.println("And paste the verifier here");
		    System.out.print(">>");
		    Verifier v = new Verifier(in.nextLine());
		    System.out.println();
//		String authUrl = service.getAuthorizationUrl(requestToken);
//		Verifier v = new Verifier(authUrl);
		Token accessToken = service.getAccessToken(requestToken, v);
		
		PrintWriter outFile = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/nyc follower Data.txt"));
		OAuthRequest request;
		int count = 1;
		for(long l : userIDs){
			
			request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/friends/ids.json?cursor=-1&user_id="+l+"&count=5000");			
			service.signRequest(accessToken, request); // the access token from step 4
			Response response = request.send();
			System.out.println(l+": "+response.getBody());
			System.out.println(count);
			outFile.println(l+": "+response.getBody());
			count++;
			Thread.sleep(60000);
			
		}
		
		outFile.close();
		
		}catch(Exception e){};
	}
}

/**
 * 
 */
package addjsonvalue;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zzc
 *
 */
public class AddValue {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		String pathname = "/Users/zzc/Downloads/Lebron Json.txt";
		//String pathname = "/Users/zzc/Downloads/Web Data Mining/Project/new data/Lebron Json.txt";
		File filename = new File(pathname);
		File writename = new File("/Users/zzc/Downloads/lebronjson_final.json");
		File homophily = new File("/Users/zzc/Downloads/lebronjson_homophily.json");
		try {
			writename.createNewFile();
			homophily.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		FileWriter fw = new FileWriter(writename);
		BufferedWriter out = new BufferedWriter(fw);
		FileWriter hm = new FileWriter(homophily);
		BufferedWriter hmout = new BufferedWriter(hm);
		
		
		try {
			FileInputStream fis = new FileInputStream(filename);
			
			//这里改过
			//JSONArray users = new JSONArray();
			JSONObject users = new JSONObject();
			
			Scanner sc = new Scanner(fis, "UTF-8");
			
			/* for test
			int fortest = 0;
			*/
			//对于每一个user 对象而言
			while(sc.hasNext()){
			String inputStreamString = sc.useDelimiter("\\n").next();
			//System.out.println(inputStreamString);
			
			JSONObject user = new JSONObject(inputStreamString);
			String userID = String.valueOf(user.getLong("userID"));
			
			
			String sentimentBucket = new String();
			JSONArray tweets = new JSONArray();
			tweets = user.getJSONArray("tweets");
			double innerOpinion = 0, expressedOpinion = 0;
			
			double[] array = new double[3];
			int positiveTweetsNum = 0;
			int neutralTweetsNum = 0;
			int negativeTweetsNum = 0;
			int numberOfTweets = tweets.length();
			
			//this is to add sentimentBucket
			
			for(int i=0; i<tweets.length(); i++){
				//add sentimentBucket
				double sentimentValue = tweets.getJSONObject(i).getDouble("sentimentValue");
				if(sentimentValue>=0.0 && sentimentValue <3.5){
					sentimentBucket = "negative";
				}
				else if(sentimentValue>=3.5 && sentimentValue<6.5){
					sentimentBucket = "neutral";
				}
				else if(sentimentValue >=6.5 && sentimentValue <=9.0){
					sentimentBucket = "positive";
				}
				else{
					sentimentBucket = "invalid";
				}
					
				tweets.getJSONObject(i).put("sentiment_bucket", sentimentBucket);
				
				
				//calculate the inner opinion
				if(i<=2){
					innerOpinion = innerOpinion + tweets.getJSONObject(i).getDouble("sentimentValue");
					if(i==2){
						innerOpinion = innerOpinion/3;
					}
				}
				
				//calculate the expressed opinion
				if(i>=tweets.length()-3){
					
					if(i==tweets.length()-3){
						array[0] = tweets.getJSONObject(i).getDouble("sentimentValue");
					}
					else if(i==tweets.length()-2){
						array[1] = tweets.getJSONObject(i).getDouble("sentimentValue");
					}
					else if(i==tweets.length()-1){
						array[2] = tweets.getJSONObject(i).getDouble("sentimentValue");
						Arrays.sort(array);
						expressedOpinion = array[1];
					}
				}
				
				//calculate what portion of tweets is
				if(tweets.getJSONObject(i).getString("sentiment_bucket") == "negative"){
					negativeTweetsNum++;
				}
				else if(tweets.getJSONObject(i).getString("sentiment_bucket") == "neutral"){
					neutralTweetsNum++;
				}
				else if(tweets.getJSONObject(i).getString("sentiment_bucket") == "positive"){
					positiveTweetsNum++;
				}
			}
			
			//here is to append inner opinion to jsonobject
			//here is to append expressed opinion to jsonobject
			//here is to append num of po,ne,ne to jsonobject
			user.put("inner_opinion", innerOpinion);
			user.put("expressed_opinion", expressedOpinion);
			user.put("num_of_tweet", numberOfTweets);
			user.put("negative_tweet_num", negativeTweetsNum);
			user.put("neutral_tweet_num", neutralTweetsNum);
			user.put("positive_tweet_num", positiveTweetsNum);
			
			
			String innerBucket = new String();
			if(innerOpinion>=0.0 && innerOpinion <3.5){
				innerBucket = "negative";
			}
			else if(innerOpinion>=3.5 && innerOpinion<6.5){
				innerBucket = "neutral";
			}
			else if(innerOpinion >=6.5 && innerOpinion <=9.0){
				innerBucket = "positive";
			}
			else{
				innerBucket = "invalid";
			}
			user.put("inner_bucket", innerBucket);
			
			
			String finalBucket = new String();
			if(expressedOpinion>=0.0 && expressedOpinion <3.5){
				finalBucket = "negative";
			}
			else if(expressedOpinion>=3.5 && expressedOpinion<6.5){
				finalBucket = "neutral";
			}
			else if(expressedOpinion >=6.5 && expressedOpinion <=9.0){
				finalBucket = "positive";
			}
			else{
				finalBucket = "invalid";
			}
			user.put("final_bucket", finalBucket);
			
			
			users.put(userID,user);
			
			
			/* for test
			fortest++;
			System.out.println(users);
			if(fortest>2)
				break;
			*/
			}
			
			
			
			
			Iterator<String> i = users.keys();
			
			//float [] homophily_total = new float[users.length()];
			//开始对于每一个
			//int num_homophily = 0;
			
			
			
			////////////////////////////////////////////////////////////////////
			/////////////////////////////////////
			/////////////////////////
			////////////
			while(i.hasNext()){
				HashSet<String> result = new HashSet<String>();
		        HashSet<String> userIDset = new HashSet<String>();
		        
		        //String groundTruth, estimatedA = new String();
				//String majorityNeighborOpinion = new String(); //done
				//String finalMajorityOpinion = new String(); //done
				int total_negative = 0;
				int total_positive = 0;
				int total_neutral = 0;
				
				int final_negative = 0;
				int final_positive = 0;
				int final_neutral = 0;
		        
				String userID = i.next().toString();
				String[] followingIDs = new String[users.getJSONObject(userID).getJSONArray("followingIDs").length()];
				for(int index=0; index < users.getJSONObject(userID).getJSONArray("followingIDs").length(); index++){
					followingIDs[index] = users.getJSONObject(userID).getJSONArray("followingIDs").getString(index);
					userIDset.add(followingIDs[index]);
				}
				
				Iterator<String> inneri = users.keys();
				for(int index2 = 0; index2 < users.length() && inneri.hasNext(); index2++){
					if(!userIDset.add(inneri.next())){
						result.add(inneri.next());
					}
				}
				
				Iterator<String> resulti = result.iterator();
				while(resulti.hasNext()){
					String resultis = resulti.next();
					total_negative = total_negative + users.getJSONObject(resultis).getInt("negative_tweet_num");
					total_positive = total_positive + users.getJSONObject(resultis).getInt("negative_tweet_num");
					total_neutral = total_neutral + users.getJSONObject(resultis).getInt("negative_tweet_num");
					if(users.getJSONObject(resultis).getInt("expressed_opinion")>0.0 && users.getJSONObject(resultis).getInt("expressed_opinion")<3.5){
						final_negative++;
					}
					if(users.getJSONObject(resultis).getInt("expressed_opinion")>=3.5 && users.getJSONObject(resultis).getInt("expressed_opinion")<6.5){
						final_neutral++;
					}
					if(users.getJSONObject(resultis).getInt("expressed_opinion")>=6.5 && users.getJSONObject(resultis).getInt("expressed_opinion")<=9.0){
						final_positive++;
					}
				}
					
				int[] majority = {total_negative,total_positive,total_neutral};
				Arrays.sort(majority);
				if(majority[2]==total_negative){
					users.getJSONObject(userID).put("majority_neighbor_opinion", "negative");
				}
				else if(majority[2]==total_neutral){
					users.getJSONObject(userID).put("majority_neighbor_opinion", "neutral");
				}
				else if(majority[2]==total_positive){
					users.getJSONObject(userID).put("majority_neighbor_opinion", "positive");
				}
				
				int[] final_majority ={final_negative,final_neutral,final_positive};
				Arrays.sort(final_majority);
				if(final_majority[2]==final_negative){
					users.getJSONObject(userID).put("final_majority_opinion", "negative");
				}
				else if(final_majority[2]==final_neutral){
					users.getJSONObject(userID).put("final_majority_opinion", "neutral");
				}
				else if(final_majority[2]==final_positive){
					users.getJSONObject(userID).put("final_majority_opinion", "positive");
				}
				
				
				//decide if a user is stubborn or conforming or none
				
				float neg_fraction = users.getJSONObject(userID).getInt("negative_tweet_num")/users.getJSONObject(userID).getInt("num_of_tweet");
				float neu_fraction = users.getJSONObject(userID).getInt("neutral_tweet_num")/users.getJSONObject(userID).getInt("num_of_tweet");
				float pos_fraction = users.getJSONObject(userID).getInt("positive_tweet_num")/users.getJSONObject(userID).getInt("num_of_tweet");
				if((neg_fraction>=0.7 && (users.getJSONObject(userID).getString("majority_neighbor_opinion") == "neutral" || users.getJSONObject(userID).getString("majority_neighbor_opinion") == "positive")) 
						|| (neu_fraction>=0.7 && (users.getJSONObject(userID).getString("majority_neighbor_opinion") == "negative" || users.getJSONObject(userID).getString("majority_neighbor_opinion") == "positive")) 
						|| (pos_fraction>=0.7 && (users.getJSONObject(userID).getString("majority_neighbor_opinion") == "negative" || users.getJSONObject(userID).getString("majority_neighbor_opinion") == "neutral"))){
					users.getJSONObject(userID).put("ground_truth", "stubborn");
				}
				else if((users.getJSONObject(userID).getString("final_bucket") != users.getJSONObject(userID).getString("inner_bucket"))
						&& (  (neg_fraction>=0.3 && (users.getJSONObject(userID).getString("majority_neighbor_opinion") == "negative")) 
						|| ((neu_fraction>=0.3 && (users.getJSONObject(userID).getString("majority_neighbor_opinion") == "neutral")) ) 
						|| ((pos_fraction>=0.3 && (users.getJSONObject(userID).getString("majority_neighbor_opinion") == "positive")) ))){
					users.getJSONObject(userID).put("ground_truth", "conforming");
				}
				else {
					users.getJSONObject(userID).put("ground_truth", "neither");
				}
				
				
				
				
				//for homophily analysis:
				/*int[] neighbors_inner = new int[followingIDs.length];
				int inner_fraction = 0;
				for(int index=0; index < followingIDs.length; index++){
					
					if(users.has(userID)){
						if(users.getJSONObject(followingIDs[index]).has("inner_opinion")){
							neighbors_inner[index] = users.getJSONObject(followingIDs[index]).getInt("inner_opinion");
							if(neighbors_inner[index]-(users.getJSONObject(userID).getInt("inner_opinion")) <=1 ){
								inner_fraction++;
						}}else{
						System.out.println("no inner opinion of this guy");
						}
					}
					else
					{
						System.out.println("no this guy");
					}
					
				}
				num_homophily++;
				homophily_total[num_homophily] = inner_fraction/(users.getJSONObject(userID).getJSONArray("followingIDs").length());
				*/
				
			}
			
			
			
			out.write(users.toString());
			out.flush();
			out.close();
			sc.close();
			
			System.out.println("it's done: /Users/zzc/Downloads/lebronjson_final.json");
			
			
			
			//for homophily analysis:
			/*if(i.hasNext()){
				System.out.println("i has next");
			}
			else{
				System.out.println("i has no next");
			}
			System.out.println(i.toString());
			*/
			
			float [] homophily_total = new float[users.length()];
			//开始对于每一个
			int num_homophily = 0;
			
			
			
			
			Iterator<String> ii = users.keys();
			while(ii.hasNext()){
				
				String userID = ii.next().toString();
				System.out.println(userID);
				String[] followingIDs = new String[users.getJSONObject(userID).getJSONArray("followingIDs").length()];
			    System.out.println("followingIDs's length: "+followingIDs.length);
			int[] neighbors_inner = new int[followingIDs.length];
			
			int inner_fraction = 0;
			
			for(int index=0; index < followingIDs.length; index++){
				followingIDs[index] = users.getJSONObject(userID).getJSONArray("followingIDs").getString(index);
				if(users.has(followingIDs[index]) && (users.getJSONObject(followingIDs[index]).has("inner_opinion"))){
					//System.out.println("enter this if clause");
						neighbors_inner[index] = users.getJSONObject(followingIDs[index]).getInt("inner_opinion");
						if(neighbors_inner[index]-(users.getJSONObject(userID).getInt("inner_opinion")) <=1 ){
							inner_fraction++;
							}
				}
				else{
					
					continue;
				}
			   	
			}
			
			System.out.println("followingIDs 's length: "+(users.getJSONObject(userID).getJSONArray("followingIDs").length()));
			if(users.getJSONObject(userID).getJSONArray("followingIDs").length()!=0){
			homophily_total[num_homophily] = (float)inner_fraction/(float)(users.getJSONObject(userID).getJSONArray("followingIDs").length());
			
			System.out.println(inner_fraction +"||"+ (users.getJSONObject(userID).getJSONArray("followingIDs").length()));
			}
			else{
				homophily_total[num_homophily] = 0;
			}
			
			
			hmout.append("the fraction of differences between this user "+userID+"'s inner opinion and his neighbors' inner opinion that is less than 1: "+String.valueOf(homophily_total[num_homophily])+"\n");
			num_homophily++;
			}
			
			
			
			hmout.write(homophily_total.toString());
			hmout.flush();
			hmout.close();
			System.out.println("it's done: /Users/zzc/Downloads/lebronjson_homophily.json");
			
			
			
		} catch (FileNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
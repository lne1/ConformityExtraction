/**
 * Copyright 2013 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.twitter.hbc.example;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SampleStreamExample {

  public static void run(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {
    // Create an appropriately sized blocking queue
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
    //List<String> languages = Lists.newArrayList("en");
    // Define our endpoint: By default, delimited=length is set (we need this for our processor)
    // and stall warnings are on.
//    StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
//    endpoint.stallWarnings(false);
//    endpoint.languages(languages);
//    endpoint.addPostParameter(Constants.LANGUAGE_PARAM, "en");
	    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
	    // Optional: set up some followings and track terms
	    List<Long> followings = Lists.newArrayList(1234L, 566788L);
	    List<String> terms = Lists.newArrayList("Lebron","New York","nyc","gamergate");
	    endpoint.followings(followings);
	    endpoint.trackTerms(terms);
	    

    Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
    //Authentication auth = new com.twitter.hbc.httpclient.auth.BasicAuth(username, password);

    // Create a new BasicClient. By default gzip is enabled.
    BasicClient client = new ClientBuilder()
            .name("sampleExampleClient")
            .hosts(Constants.STREAM_HOST)
            .endpoint(endpoint)
            .authentication(auth)
            .processor(new StringDelimitedProcessor(queue))
            .build();

    // Establish a connection
    client.connect();

    int count = 0;
    for(int i=30; i<10000;i++){   
	    try{
		    PrintWriter outFile = new PrintWriter(new FileOutputStream("F:/Alex Files/Twitter Data/Twitter Data "+i+".txt"));
		    // Do whatever needs to be done with messages
		    for (int msgRead = 0; msgRead < 100000; msgRead++) {
		    	if (client.isDone()) {
		    		System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
		    		break;
		    	}
		
		    	String msg = queue.poll(5, TimeUnit.SECONDS);
		    	if (msg == null) {
		    		System.out.println("Did not receive a message in 5 seconds");
		    	} else {
		    		msg = msg.replace('"', '*');		    		
		    		//System.out.println(msg);
		    		
		    		if(msg.contains(",*lang*:*en*,")){		    		
			    		int startText = msg.indexOf("*text*:");
			    		int endText = msg.indexOf("*source*:");
			    		int startId = msg.indexOf("*id_str*:", endText);
			    		int endId = msg.indexOf("*name*:", startId);			    		
			    		String data = msg.substring(startText+8, endText-2)+" | "+msg.substring(startId+10, endId-2);			    			    		
		    			outFile.println(data);	  
		    			count++;  	
		    			
		    			
		    		}
					
		      }
		    }
		    outFile.close();
	    }catch(Exception e){
	    	System.out.println("error writing to file "+e);
	    }
	    
    }
    client.stop();

    // Print some stats
    System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());
    System.out.println("Was really "+count);
  }

  public static void main(String[] args) {
    try {
    	String[] keys = new String[4];
    	keys[0]="JyXRqWjMgEUkdcxvzmCjnVTB6";
    	keys[1]="L6HSmdxocKYsIpO3EL9JkpynkQ7ABWWZtUiNFdWSHC1bjSwLcf";
    	keys[2]="70837476-iH1AOFLFvZq3osehjSOzep18r0qipugK0On3M2BT6";
    	keys[3]="eSL6eijC3yFE385WMjKtrL1mdzZuwLthzjDcfmPcAuwtR";
      SampleStreamExample.run(keys[0], keys[1], keys[2], keys[3]);
    } catch (InterruptedException e) {
      System.out.println(e);
    }
  }
}

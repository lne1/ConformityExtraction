# EECS 393: Experimental Evaluation of a Conformity Extraction System
# author: Lucas Evans

import re
import statistics
import unirest
from textblob import *
import json

class Processor:

    tweet_dict = {}
    sentiment_dict = {}
    expr_opinion_dict = {}
    expr_opinion_vector = []

    def __init__(self):
        l = self.list_lines("lebron processed.txt")
        self.make_dict(l)
        self.tb_analyze_dict()
        self.expr_opinions()
        self.update_users()

    # Look at the processed text file
    # Look for the ID numbers.
    # Make a new user and add the tweets underneath the ID to the tweet_list.
    def list_lines(self, filename):
        with open(filename) as f:
            lines = list(f)
        for i in range(0, lines.count(r'\n')):
            lines.remove(r'\n')
        return lines

    def make_dict(self, line_list):
        current_user = 0
        for s in line_list:
            if re.match('[0-9]+\s\n', s):
                current_user = s.strip()
                self.tweet_dict[current_user] = []
            elif re.match(r'\n', s): continue
            else: self.tweet_dict[current_user].append(s.strip())
        #print self.tweet_dict

    def tp_analyze(self, content):
        # These code snippets use an open-source library. http://unirest.io/python
        response = unirest.post("https://japerk-text-processing.p.mashape.com/sentiment/",
                                headers={
                                    "X-Mashape-Key": "ps09ttwJbUmshXhIx7y35oa4LIjWp1aiqJGjsnZpa0q4YUWOnF",
                                    "Content-Type": "application/x-www-form-urlencoded"
                                },
                                params={
                                    "language": "english",
                                    "text": content
                                }
        )
        pos = response.body['probability']['pos']
        return int(round(pos*10)) # smooth into opinion bucket {0, 1, ... , 9}

    def tp_analyze_dict(self):
        pass

    def tb_analyze(self, content):
        tweet = TextBlob(content)
        sentiment = tweet.sentiment.polarity
        sentiment = (sentiment + 1)/2
        return int(round(sentiment * 10))


    def tb_analyze_dict(self):
        for user in self.tweet_dict:
            value_list = []
            tweet_list = self.tweet_dict[user]
            for t in tweet_list:
                value = self.tb_analyze(t)
                value_list.append(value)
            self.sentiment_dict[user] = value_list
        #print self.sentiment_dict

    def expr_opinions(self): # expressed opinion: median of three most recent opinions
        for user in self.sentiment_dict:  # tweets are stored in the array in the dictionary oldest->newest
            mr = self.sentiment_dict[user]  # tweets are stored in the array in the dictionary oldest->newest
            mr.reverse()  # easier to get most recent
            mr = mr[0:2]
            self.expr_opinion_dict[user] = int(statistics.median(mr))
        #print self.expr_opinion_dict

    # update single user
    def update_json(self, user_dict):
        user_dict = json.loads(user_dict)
        user_id = user_dict['userID']
        t_list = user_dict['tweets']
        sentiments = self.sentiment_dict[str(user_id)]
        for i in range(0, len(t_list)):
            t_list[i]['sentimentValue'] = sentiments[i]
        user_dict['tweets'] = t_list
        return json.dumps(user_dict)

    def update_users(self):
        with open('Lebron Json.txt') as f:
            for line in f:
                print self.update_json(line.strip())



































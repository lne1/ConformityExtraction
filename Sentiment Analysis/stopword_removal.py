# EECS 393: Experimental Evaluation of a Conformity Extraction System
# A quick and dirty script that scans our raw Twitter data from a text file and removes stopwords, URLS, and @usernames from text
# Uses the stopword list from the Natural Language Toolkit - ntlk.org
# author: Lucas Evans
import string
import re

class Remover:

    def __init__(self):
        pass

    stopwords = ['i', 'me', 'my', 'myself', 'we', 'our', 'ours', 'ourselves', 'you', 'your', 'yours',
             'yourself', 'yourselves', 'he', 'him', 'his', 'himself', 'she', 'her', 'hers',
             'herself', 'it', 'its', 'itself', 'they', 'them', 'their', 'theirs', 'themselves',
             'what', 'which', 'who', 'whom', 'this', 'that', 'these', 'those', 'am', 'is', 'are',
             'was', 'were', 'be', 'been', 'being', 'have', 'has', 'had', 'having', 'do', 'does',
             'did', 'doing', 'a', 'an', 'the', 'and', 'but', 'if', 'or', 'because', 'as', 'until',
             'while', 'of', 'at', 'by', 'for', 'with', 'about', 'against', 'between', 'into',
             'through', 'during', 'before', 'after', 'above', 'below', 'to', 'from', 'up', 'down',
             'in', 'out', 'on', 'off', 'over', 'under', 'again', 'further', 'then', 'once', 'here',
             'there', 'when', 'where', 'why', 'how', 'all', 'any', 'both', 'each', 'few', 'more',
             'most', 'other', 'some', 'such', 'no', 'nor', 'not', 'only', 'own', 'same', 'so',
             'than', 'too', 'very', 's', 't', 'can', 'will', 'just', 'don', 'should', 'now']

    def strip_string(self, s): # remove usernames, URLS, and stop words from a single tweet
        words = string.lower(s).split() # convert to lower case and split the tweet around whitespace
        # note that this also handles tweets in which people used line breaks, e.g. :
        # I
        # Hate
        # Myself
        ret_string = ''
        trash = ''
        for s in words:
            if s[0] == '@':
                trash = trash + s
            elif len(s) > 5 and s[0:5] == 'http:':
                trash = trash + s
            elif s in self.stopwords:
                trash = trash + s
            else: ret_string = ret_string + s + ' '
        return ret_string

    def scan_file(self, filename):
        # file read loop
        with open(filename) as f:
            for line in f:
                print self.strip_string(line)

# problems: emoji, other unicode, rt, #stopwords, no space between emoji rt or @name, rogue newlines
# advertising??



































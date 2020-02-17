# SearchEngine-DocumentRetrieval
Welcome to "tootle" Search Engine!

## Description

Search Engine for documents retrival, this search engine is responsible 
for parsing terms by specified rules as:

--Number parsing: each number readen is converted to units as K - for thousnads
							      M - for millions
							      B - for billions

--Price parsing: each U.S. dollar price given is parsed into one token.

--Percantage parsing: each percent unit is parsed into one token of %X - where X is a number.

--Range & Expression: each expression given as "w1-w2-...-wn" parsed into 1 token.
		      each range given as "X-Y" or "between X and Y" where X,Y is numbers parsed by Number parser into 1 token.
          
--Entities: each entity is recognized by Stanford Tagger and written into 1 token.

--UpperLower: each word that start with Upper case saved whole as Upper.

--Stemmer: each word is being stemming by Porter Stemmer.

--Dates: each date given in the text is parsed as "dd-mm-yyyy".

This search engine is responsible for indexing and creating inverted index, saving term information in posting files.

## Usage
1. Corpus path -> choose the path where your corpus is located.
2. Saving path -> choose the path where you want to save the posting file.
3. Start index button -> parse&indexing //WARNING - this process may take up to 25 min depends on your computer technical specifications//
4. Upload dictionary -> Loading the dictionary into RAM.
5. Show dictionary -> display the dictionary and all terms in it.
6. Search single query for searcing.
7. Load Searching queries from a txt file -> comparing with TREC EVAL .jar results for testing. 

## Credits
This Search Engine for documents retrieval written by:
Daniel Ben-Simon, Third year student of information & Software systems engineering
Eran Toutian, Third year student of information & Software systems engineering

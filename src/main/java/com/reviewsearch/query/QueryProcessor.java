package com.reviewsearch.query;

import java.io.File;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.reviewsearch.lucene.indexer.IndexCreater;

public class QueryProcessor {

	public static ArrayList<DocResult> processQuery(String s) throws Exception {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(IndexCreater.indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		// TopScoreDocCollector collector = TopScoreDocCollector.create(20,
		// true);

		if(SentimentAnalyzer.negWords.isEmpty() && SentimentAnalyzer.posWords.isEmpty())SentimentAnalyzer.initialize();
		
		Query q = new QueryParser(Version.LUCENE_40, "content", IndexCreater.analyzer).parse(s);
		TopDocs topDocs = searcher.search(q, 100);

		ScoreDoc[] hits = topDocs.scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		ArrayList<DocResult> drList = new ArrayList<DocResult>();

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println(d.get("filename") + " - " + (i + 1) + ". " + d.get("title") + " score=" + hits[i].score);

			String content = d.get("content");
			
			int sentiment = SentimentAnalyzer.getSentimentScore(content);
			String[] strs = content.split(" ");
			StringBuilder build = new StringBuilder();
			s = s.replaceAll("AND", "");

				for (int j = 0; j < strs.length; j++) {
					for (String term : s.split(" ")) {
					if(term.equalsIgnoreCase(strs[j])){
						if(j > 4 && j < strs.length - 4){
							build.append(""+strs[j - 4] +" "+strs[j - 3] +" "+strs[j - 2] +" "+strs[j - 1] +" " + strs[j] +" ");
							build.append(strs[j + 1] +" "+strs[j + 2] +" "+strs[j + 3] +" " + strs[j + 4] +" ");
							break;
						}else if(j < 4){
							build.append(" "+ strs[j] +" ");
							build.append(strs[j + 1] +" "+strs[j + 2] +" "+strs[j + 3] +" " + strs[j + 4] +" ");
							break;
						}else if(j > strs.length - 4){
							build.append(" "+strs[j - 4] +" "+strs[j - 3] +" "+strs[j - 2] +" "+strs[j - 1] +" " + strs[j] +" ");
							break;
						}
					}
				}
					if(build.length() > 100){
						break;
					}
			}
			DocResult dr = new DocResult();
			dr.setDocContents(build.toString());
			dr.setTitleOfDoc(d.get("title"));
			dr.setUrlOfDoc(d.get("url"));
			if(sentiment == 1)dr.setSentiment("Positive");
			else if(sentiment == -1)dr.setSentiment("Negative");
			else dr.setSentiment("Neutral");
			if(dr.getSentiment().equalsIgnoreCase("positive")){
				DocResult.posReviews++;
			}else if(dr.getSentiment().equalsIgnoreCase("negative")){
				DocResult.negReviews++;
			}else{
				DocResult.neuReviews++;
			}
			drList.add(dr);
		}
		return drList;
	}

}

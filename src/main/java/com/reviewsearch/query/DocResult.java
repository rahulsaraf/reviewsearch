/**
 * 
 */
package com.reviewsearch.query;

/**
 * @author rahulsaraf
 *
 */
public class DocResult {
	
	public static Integer posReviews = 0;
	public static Integer negReviews = 0;
	public static Integer neuReviews = 0;
	private String docContents;
	private String urlOfDoc;
	private String titleOfDoc;
	private String sentiment;
	public String getDocContents() {
		return docContents;
	}
	public void setDocContents(String docContents) {
		this.docContents = docContents;
	}
	public String getUrlOfDoc() {
		return urlOfDoc;
	}
	public void setUrlOfDoc(String urlOfDoc) {
		this.urlOfDoc = urlOfDoc;
	}
	public String getTitleOfDoc() {
		return titleOfDoc;
	}
	public void setTitleOfDoc(String titleOfDoc) {
		this.titleOfDoc = titleOfDoc;
	}
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	
	
}

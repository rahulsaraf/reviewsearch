package com.webapp.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.reviewsearch.lucene.indexer.IndexCreater;
import com.reviewsearch.query.DocResult;
import com.reviewsearch.query.QueryProcessor;


@Controller
public class WelcomePageController {
	
	
	@RequestMapping("/hello")
	public String redirectWelcomePage(ModelMap model){
		return "/WEB-INF/jsp/welcome.jsp";
	}
	
	@RequestMapping("/search")
	public String redirectDynamicWelcomePage(ModelMap model, @RequestParam("searchText") String searchText) throws Exception{
		try{
			File file = new File(IndexCreater.indexLocation);
			if(!file.exists()) throw new Exception(); 
		}catch(Exception e){
			IndexCreater index = new IndexCreater(IndexCreater.indexLocation);
			index.readFiles();
		}
		DocResult.posReviews = 0;
		String[] texts = searchText.split(" ");
		String query = "";
		for (int i = 0; i < texts.length - 1; i++) {
			query += texts[i] + " AND ";
		}
		query += texts[ texts.length - 1];
		ArrayList<DocResult> dr = QueryProcessor.processQuery(query);
		
		String[] elements = searchText.split(" ");
		StringBuilder newQuery = new StringBuilder();
		if(elements != null && elements.length>1){
			for (String word : elements) {
				newQuery.append(word);
				newQuery.append("%20");
			}
			query = newQuery.toString();
		}
		model.addAttribute("searchText", searchText);
		model.addAttribute("records", dr);
		model.addAttribute("googleRecords", googleCall(query));
		model.addAttribute("bingRecords", bingCall(query));
		model.addAttribute("posReviews", DocResult.posReviews);
		model.addAttribute("negReviews", DocResult.negReviews);
		model.addAttribute("neuReviews", DocResult.neuReviews);
		return "/WEB-INF/jsp/welcome.jsp";
	}
	
	
	public static ArrayList<DocResult> googleCall(String searchText) throws IOException{
		ArrayList<DocResult> googleResult = new ArrayList<DocResult>();
	
		String key = "AIzaSyCDpq0TH29k3c8TiiQd4YWWZxKuamwMw88"; // Browser API Key sahithi
		
		
		String cref = "001501472147766648365:ay-2hmjwumw"; // Custom Search engine ID sahithi
		
		URL url = new URL("https://www.googleapis.com/customsearch/v1?key="+key+ "&cx="+ cref +"&q="+ searchText +"&alt=json");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		final StringBuilder response = new StringBuilder();

		while ((line = br.readLine()) != null) {
			response.append(line);
		}		
		if(response != null){

			JSONObject obj = new JSONObject(response.toString());
			JSONArray items = obj.getJSONArray("items");
			for(int i=0;i<items.length();i++){
				DocResult data = new DocResult();
				data.setUrlOfDoc(items.getJSONObject(i).getString("link"));
				data.setTitleOfDoc(items.getJSONObject(i).getString("title"));
				data.setDocContents(items.getJSONObject(i).getString("snippet"));
				googleResult.add(data);
			}
		}
		return googleResult;
	}
	
	@SuppressWarnings({ "resource", "deprecation" })
	public static ArrayList<DocResult> bingCall(String searchText) throws IOException{
		ArrayList<DocResult> bingResult = new ArrayList<DocResult>();
		@SuppressWarnings("deprecation")
		HttpClient httpclient = new DefaultHttpClient();

		try {
			HttpGet httpget = new HttpGet("https://api.datamarket.azure.com/Bing/Search/Web?Query=%27"+searchText+"%27&$format=JSON&$top=10");
		
			String appId = "na4aBgkt0DUOF8G0VFTc+PS65jKgw+uid1BGNQzWpsE="; //sahithi

			byte[] accountKeyBytes = Base64.encodeBase64((":" + appId).getBytes());
			String enc_key =  new String(accountKeyBytes);
			httpget.setHeader("Authorization", "Basic "+enc_key);




			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpget, responseHandler);

			if(responseBody != null){
				JSONObject obj = new JSONObject(responseBody);

				JSONObject obj2 = obj.getJSONObject("d");

				if(obj2 != null){
					JSONArray results = obj2.getJSONArray("results");
					if(results != null && results.length()>0){
						for(int i=0;i<results.length();i++){
							DocResult data = new DocResult();
							data.setUrlOfDoc(results.getJSONObject(i).getString("Url"));
							data.setTitleOfDoc(results.getJSONObject(i).getString("Title"));
							data.setDocContents(results.getJSONObject(i).getString("Description"));
							bingResult.add(data);
						}
					}
				}
			}

			return bingResult;
			
		} finally {
			httpclient.getConnectionManager().shutdown();
		}	
	}
	
	
	
	
	
	

}

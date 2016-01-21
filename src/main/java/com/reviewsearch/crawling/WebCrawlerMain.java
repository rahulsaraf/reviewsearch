/**
 * 
 */
package com.reviewsearch.crawling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author rahulsaraf
 *
 */
public class WebCrawlerMain {

	public static final String DISALLOW = "Disallow:";
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private HashMap<String, ArrayList<String>> disallowedURLS = new HashMap<String, ArrayList<String>>();
	private HashMap<String, WebPage> webPages = new HashMap<String,WebCrawlerMain.WebPage>();
	private HashMap<Integer, LinkedList<String>> crawlWebPages = new HashMap<Integer, LinkedList<String>>();
	public static Integer countOfPages = 29605;
	public static String webPageDirectory = "E:/yelpdata/documents/";
	public static String webGraphDirectory = "E:/yelpdata/indexInfo/";

	public void crawl(String url, int count) {
			try {
				if (robotSafe(url)) {
				Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
				Document htmlDocument = connection.get();
				if (connection.response().statusCode() == 200) {
					System.out.println("\n**Visiting** Received web page at " + url);
				}
				if (!connection.response().contentType().contains("text/html")) {
					System.out.println("**Failure** Retrieved something other than HTML");
					return;
				}
				WebPage page = new WebPage();
				Elements linksOnPage = htmlDocument.select("a");
				String title = htmlDocument.select("title").first().text();
				page.url = url;
				if (!webPages.containsKey(url)) {
					page.docId = countOfPages++;
					webPages.put(url,page);
					page.title = title;
					System.out.println("Found (" + linksOnPage.size() + ") links");
					for (Element link : linksOnPage) {
						String linkHref = link.absUrl("href");
						String linkText = link.text();
						if(linkHref.isEmpty()) continue;
						WebPage outPage;
						if(webPages.containsKey(linkHref)){
							outPage = webPages.get(linkHref);
							outPage.achorTags.add(linkText);
							outPage.inLinks.put(url,1 + (outPage.inLinks.get(url) == null ? 0 : outPage.inLinks.get(url)));
						}
						page.outLinks.put(linkHref,1 + (page.outLinks.get(linkHref)  == null ?  0 :  page.outLinks.get(linkHref)));
						crawlWebPages.get(count).add(linkHref);
					}
					writeDocument(page, htmlDocument);
				}
			}
			} catch (java.lang.IllegalArgumentException e) {
				System.out.println("\n**Error -  " + url);
				return ;
			} catch (IOException ioe) {
				return ;
			}catch (Exception ioe) {
				return ;
			}
			return ;
	}

	public void crawlWeb(String URL, int count) throws Exception {
		while(true){
			String page = nextUrl(count);
			if (page == null) {
				crawl(URL,count);
			} else {
				//Thread.sleep(1000L);
				crawl(page,count);
				if(countOfPages % 40000 == 0){
					writeWebGraph(webPages);
				}
				if(countOfPages % 20 == 0){
					if(crawlWebPages.size() < 8 )
						break;
					else{
						if(count == 7) count = 0;
						else count ++;
					}
				}
			}
		}
	}

	private void writeWebGraph(HashMap<String, WebPage> webPages) throws Exception {
		File file = new File(webGraphDirectory + "webgraph.gf");
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		for (String url : webPages.keySet()) {
			WebPage page = webPages.get(url);
			bw.write(String.valueOf(page.docId));
			bw.write("\t");
			bw.write(page.url);
			bw.write("\t");
			bw.write(page.title);
			bw.write("\t");
			bw.write(":INLINKS:");		
			bw.write(String.valueOf(page.inLinks.size()));
			bw.write("\t");
			for (String inUrl : page.inLinks.keySet()) {
				bw.write(inUrl);
				bw.write("\t");
				bw.write(String.valueOf(page.inLinks.get(inUrl)));
				bw.write("\t");
			}
			bw.write(":OUTLINKS:");		
			bw.write(String.valueOf(page.outLinks.size()));
			bw.write("\t");
			for (String inUrl : page.outLinks.keySet()) {
				bw.write(inUrl);
				bw.write("\t");
				bw.write(String.valueOf(page.outLinks.get(inUrl)));
				bw.write("\t");
			}
			bw.newLine();
		}
		bw.close();
	}

	public static void main(String[] args) throws Exception {
		WebCrawlerMain crawl = new WebCrawlerMain();
		String[] urls = { "http://www.zagat.com/","http://gayot.com","http://www.Dine.com","http://fodors.com","http://www.restaurant.com",
				"http://www.menupages.com","http://www.menuism.com","http://www.Restaurantica.com"};
		int count = 0;
		for (String url : urls) {
			crawl.crawlWebPages.put(count, new LinkedList<String>());
			crawl.crawlWeb(url, count++);
		}
	}

	public boolean robotSafe(String currentUrl) throws Exception {
		System.out.println("currentUrl = " + currentUrl);
		String[] parts = currentUrl.split("/");
		String host = "";
		if(currentUrl.contains("http")){
			host = parts[2];
		}else{
			host = parts[0];
		}
		if(!host.contains("com")) return false;
		if(currentUrl.contains("youtube") || currentUrl.contains("facebook") || currentUrl.contains("twitter")
				 || currentUrl.contains("google")  || currentUrl.contains("instagram") || currentUrl.contains("studyonline") || 
				 currentUrl.contains("login") || currentUrl.contains("signup") || currentUrl.contains("register")){
			return false;
		}

		if (disallowedURLS.containsKey(host)) {
			ArrayList<String> list = disallowedURLS.get(host);
			for (int i = 1; i < parts.length; i++) {
				if (!parts[i].trim().isEmpty() && list.contains(parts[i].trim())) {
					return false;
				}
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			disallowedURLS.put(host, list);
			String strRobot = "http://" + host + "/robots.txt";
			URL urlRobot;
			try {
				urlRobot = new URL(strRobot);
			} catch (MalformedURLException e) {
				return false;
			}
			String strCommands;
			try {
				InputStream urlRobotStream = urlRobot.openStream();
				byte b[] = new byte[1000];
				int numRead = urlRobotStream.read(b);
				strCommands = new String(b, 0, numRead);
				while (numRead != -1) {
					numRead = urlRobotStream.read(b);
					if (numRead != -1) {
						String newCommands = new String(b, 0, numRead);
						strCommands += newCommands;
					}
				}
				urlRobotStream.close();
			} catch (IOException e) {
				return true;
			}
			System.out.println(strCommands);
			int index = 0;
			while ((index = strCommands.indexOf(DISALLOW, index)) != -1) {
				index += DISALLOW.length();
				String strPath = strCommands.substring(index);
				StringTokenizer st = new StringTokenizer(strPath);

				if (!st.hasMoreTokens())
					break;

				String strBadPath = st.nextToken().replace("/", "");
				list.add(strBadPath);
			}
			ArrayList<String> listUrls = disallowedURLS.get(host);
			for (int i = 1; i < parts.length; i++) {
				if (!parts[i].trim().isEmpty() && listUrls.contains(parts[i].trim())) {
					return false;
				}
			}
		}
		return true;
	}

	private String nextUrl(int count) {
		String nextUrl = null;
		if(this.crawlWebPages.get(count).isEmpty()) return nextUrl;
		do {
			nextUrl = this.crawlWebPages.get(count).remove(0);
		} while (this.crawlWebPages.get(count).contains(nextUrl));
		return nextUrl;
	}

	private void writeDocument(WebPage page, Document htmlDocument) throws IOException {
		File file = new File(webPageDirectory + page.docId);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("Title:" + page.title + "::");
		bw.newLine();
		bw.write("URL:" + page.url + "::");
		bw.newLine();
		bw.write(htmlDocument.body().text());
		bw.close();
	}

	public class WebPage {
		String url;
		String title;
		Integer docId;
		ArrayList<String> achorTags = new ArrayList<String>();
		HashMap<String, Integer> inLinks = new HashMap<String,Integer>();
		HashMap<String,Integer> outLinks = new HashMap<String,Integer>();

		public boolean equals(Object obj) {
			if (!(obj instanceof WebPage))
				return false;
			return (url.equals(((WebPage) obj).url));
		}

		public int hashCode() {
			return url.hashCode();
		}
	}
}

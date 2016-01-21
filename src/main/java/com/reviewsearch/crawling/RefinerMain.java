/**
 * 
 */
package com.reviewsearch.crawling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author rahulsaraf
 *
 */
public class RefinerMain {

	/**
	 * @param args
	 */
	static String docLocation = "E:/yelpdata/documents";
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String x = "Zagat About Business Owners Write Reviews New York City closeSelect a city Within the U.S. International Atlanta Austin Baltimore Boston Cape Cod Charleston Charlotte Chicago Connecticut Dallas Denver Hawaii Houston Las Vegas Long Island Los Angeles Miami Minneapolis Nashville National New Jersey New Orleans New York City Orlando Philadelphia Phoenix / Scottsdale Portland Providence San Antonio San Diego San Francisco Savannah Seattle Tampa Washington, DC Westchester/Hudson Valley Amsterdam Barcelona Florence London Madrid Paris Rome Toronto Vancouver Venice Ratings & Reviews Lists Buzz";
		String y = "Stay in-the-know with our";
		File[] listOfFiles = new File(docLocation).listFiles();
		int count = 0;
		boolean isYelp = true;
		for (File file : listOfFiles) {
		//File file = new File(docLocation + "/32371");
			String line = "";
			String url = "";
			String title = "";
			FileReader fr = new FileReader(file);
			StringBuilder str = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(fr);
			isYelp = true;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("Title:")) {
					title = line.replaceAll("Title:", "").replace("::", "");
					str.append(line);str.append("\n");
				}else if (line.contains("URL:")) {
					url = line.replaceAll("URL:", "").replace("::", "");
					if(!url.contains("zagat")){
						System.out.println(url);
						isYelp = false;
						break;
					}
					str.append(line);str.append("\n");
				}else{
					str.append(line);
				}
				
			}
			if(isYelp){
				String s = str.toString().replace(x, "");
				String z = "";
				if(s.indexOf(y) != -1)
					z = s.substring(s.indexOf(y));
				s = s.replace(z, "");
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(s);
				bw.close();
			}
			
		}
		}

}

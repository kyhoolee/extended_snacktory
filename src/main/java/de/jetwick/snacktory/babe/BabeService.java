package de.jetwick.snacktory.babe;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.jetwick.snacktory.model.JResult;
import de.jetwick.snacktory.util.HttpUtil;
import de.jetwick.snacktory.util.Utils;

public class BabeService {
	public static String getBabeData() {
		
		String url = "http://10.2.15.5:8983/solr/article-repo/select?q=type_i%3A0&start=2&rows=10&wt=json&indent=true";
		String data = HttpUtil.doGet(url);
		
		return data;
	}
	
	public static void checkSource() {
		
	}
	
	public static List<String> getSourceList() {
		List<String> result = new ArrayList<String>();
		
		String filePath = "/home/mainspring/tutorial/learn/content-extractor/data/source_ids.txt";
		List<String> data = Utils.readLine(filePath);
		for(String line : data) {
			//System.out.println(line);
			line = line.replace(",", "");
			result.add(line);
			System.out.println(line);
		}
		
		return result;
	} 
	
	
	public static int checkSourceArticle(String source) {
		String url = "http://10.2.15.5:8983/solr/article-repo/select?q=type_i:0+AND+source_i:" + source
				+ "&wt=json&indent=true&start=0&rows=1";
		
		String data = HttpUtil.doGet(url);
		
		JSONObject dataObject = new JSONObject(data);
		
		JSONObject response = dataObject.getJSONObject("response");
		
		int numFound = response.getInt("numFound");
		
		System.out.println(source + " -- " + numFound);
		
		return numFound;
	}
	
	public static List<String> checkBabeSource() {
		List<String> sources = getSourceList();
		
		List<String> active = new ArrayList<String>();
		for(String s : sources) {
			int num = checkSourceArticle(s);
			if(num > 0) {
				active.add(s);
			}
		}
		
		System.out.println("Total active: " + active.size());
		
		return active;
	}
	
	public static void writeActive() {
		List<String> active = checkBabeSource();
		String filePath = "/home/mainspring/tutorial/learn/content-extractor/data/active_sources.txt";
		Utils.writeLine(active, filePath);
	}
	
	public static List<String> readActive() {
		String filePath = "/home/mainspring/tutorial/learn/content-extractor/data/active_sources.txt";
		List<String> result = Utils.readLine(filePath);
		return result;
	}
	
	
	public static List<JResult> getBabeData(String source, int num) {
		String url = "http://10.2.15.5:8983/solr/article-repo/select?q=type_i:0+AND+source_i:" + source
				+ "&wt=json&indent=true&start=0&rows=" + num;
		
		String data = HttpUtil.doGet(url);
		
		List<JResult> result = Utils.json2Result(data);
		
		return result;
	}
	
	public static JSONArray getBabeJSON(int num) {
		
		
		List<String> active = readActive();
		
		JSONArray result = new JSONArray();
		
		List<JResult> full = new ArrayList<JResult>();
		//int count = 0;
		for(String source : active) {
			List<JResult> data = getBabeData(source, num);
			full.addAll(data);
			
//			count ++;
//			if(count > 10)
//				break;
		}
		
		result = Utils.result2JSON(full);
		
		return result;
		
	}
	
	public static void writeJSON(JSONObject data, String filePath) {

		try (FileWriter file = new FileWriter(filePath)) {
			file.write(data.toString());
			System.out.println("Successfully Copied JSON Object to File...");
			//System.out.println("\nJSON Object: " + data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeJSONList(JSONArray data, String filePath) {

		try (FileWriter file = new FileWriter(filePath)) {
			for(int i = 0 ; i < data.length() ; i ++) {
				file.write(data.getJSONObject(i).toString());
				file.write("\n");
			}
			
			System.out.println("Successfully Copied JSON Object to File...");
			//System.out.println("\nJSON Object: " + data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeBabeJSON() {
		JSONArray articles = getBabeJSON(1);
		
		JSONObject data = new JSONObject();
		data.put("articles", articles);
		
		System.out.println(articles.length());
		
		String filePath = "/home/mainspring/tutorial/learn/content-extractor/data/articles_json.txt";
		writeJSONList(articles, filePath);
	}
	
	
	public static List<JResult> readBabeArticleJSON(String filePath) {
		//String filePath = "/home/mainspring/tutorial/learn/content-extractor/data/articles_json.txt";
		List<String> data = Utils.readLine(filePath);
		
		List<JResult> result = new ArrayList<>();
		for(String line : data) {
			JResult r = Utils.object2Result(new JSONObject(line));
			result.add(r);
		}
		
		return result;
	}
	
	public static List<JResult> readBabeJSON() {
		String filePath = "/home/mainspring/tutorial/learn/content-extractor/data/articles_json.txt";
		List<String> data = Utils.readLine(filePath);
		
		List<JResult> result = new ArrayList<>();
		for(String line : data) {
			JResult r = Utils.object2Result(new JSONObject(line));
			result.add(r);
		}
		
		return result;
	}
	
	public static List<JResult> readBabeJSON(String filePath) {
		//String filePath = "/home/mainspring/tutorial/learn/content-extractor/data/articles_json.txt";
		List<String> data = Utils.readLine(filePath);
		
		List<JResult> result = new ArrayList<>();
		for(String line : data) {
			try {
				System.out.println(line);
				JResult r = Utils.object2Result(new JSONObject(line));
				result.add(r);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	public static List<CompareResult> readCompare(String filePath) {
		return Utils.readCSV(filePath);
	}
	
	
	public static void writeCompare(List<CompareResult> compared, String filePath) {
		Utils.writeCSV(compared, filePath);
	}
	
	public static void filterCompare() {
		String filePath = "4_2_2017_compared_result.csv";
		List<CompareResult> compList = readCompare(filePath);
		
		List<CompareResult> resList = new ArrayList<>();
		for(CompareResult comp : compList) {
			System.out.println(comp);
//			if(comp.lib_title_len > 0 && comp.lib_content_len > 0) {
//				resList.add(comp);
//			}
			if(comp.title_distance > 0) {
				resList.add(comp);
			}
		}
		
		String resPath = "4_2_2017_filtered_compared_result.csv";
		writeCompare(resList, resPath);
		
	} 
	
	public static List<String> checkFiltered() {
		String filePath = "filtered_compared_result.csv";
		List<CompareResult> compList = readCompare(filePath);
		
		List<String> resList = new ArrayList<>();
		for(CompareResult comp : compList) {
			System.out.println(comp);
			resList.add(comp.id);
		}
		
		return resList;
	}
	
	public static void writeFilteredId() {
		String filePath = "filtered_ids.txt";
		List<String> idList = checkFiltered();
		
		Utils.writeLine(idList, filePath);
	}
	
	public static void filterArticle() {
		String filePath = "filtered_ids.txt";
		
		List<String> ids = Utils.readLine(filePath);
		HashSet<String> idSet = new HashSet<>();
		idSet.addAll(ids);
		
		filePath = "articles_json.txt";
		List<JResult> articles = readBabeArticleJSON(filePath);
		
		List<JResult> filtered = new ArrayList<>();
		for(JResult r : articles) {
			if(idSet.contains(r.getId())) {
				filtered.add(r);
			}
		}
		
		JSONArray filtered_json = Utils.result2JSON(filtered);
		filePath = "filtered_articles.json";
		writeJSONList(filtered_json, filePath);
	}
	
	
	public static void main(String[] args) {
		//writeActive();
		//writeBabeJSON();
		
		filterCompare();
		//writeFilteredId();
		//filterArticle();
	}
}

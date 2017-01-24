package de.jetwick.snacktory.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import com.csvreader.CsvWriter;

import de.jetwick.snacktory.JResult;
import de.jetwick.snacktory.babe.CompareResult;

public class Utils {
	
	
	
	public static List<JResult> json2Result(String data) {
		List<JResult> result = new ArrayList<JResult>();
		
		JSONObject dataObject = new JSONObject(data);
		
		JSONObject response = dataObject.getJSONObject("response");
		JSONArray docs = response.getJSONArray("docs");
		
		for(int i = 0 ; i < docs.length() ; i ++) {
			JResult res = object2Result(docs.getJSONObject(i));
			System.out.println(res.getCanonicalUrl());
			result.add(res);
		}
		
		return result;
	}
	
	public static JResult object2Result(JSONObject o) {
		JResult result = new JResult();
		
		result.setCanonicalUrl(o.getString("url_s"));
		result.setTitle(o.getString("title_t"));

		Document doc = Jsoup.parse(o.getString("body_t"), "", Parser.xmlParser());
		Element topNode = doc;
		result.setTopNode(topNode);
		result.setText(topNode.text());
		
		
		return result;
	}
	
	public static JSONObject result2Object(JResult r) {
		JSONObject object = new JSONObject();
		
		object.put("url_s", r.getCanonicalUrl()).put("title_t", r.getTitle()).put("body_t", r.getText());
		
		return object;
	}
	
	public static JSONArray result2JSON(List<JResult> rs) {
		JSONArray json = new JSONArray();
		
		for(JResult r : rs) {
			JSONObject o = result2Object(r);
			json.put(o);
		}
		
		return json;
	}
	
	public static List<String> readLine(String filePath) {
		List<String> result = new ArrayList<String>();
		
		try {
			File fin = new File(filePath);
			FileInputStream fis = new FileInputStream(fin);
			 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		 
			String line = null;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				result.add(line);
			}
		 
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	public static void writeLine(List<String> data, String filePath){
		
		try {
			File fout = new File(filePath);
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		 
			for (String line : data) {
				bw.write(line);
				bw.newLine();
			}
		 
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void writeCSV(List<CompareResult> data, String filePath) {
		/*
		 * public String id = "";
	public String source_id = "";
	public String babe_url;
	public String lib_url;
	public String babe_title;
	public String lib_title;
	
	public int title_distance;
	public int babe_title_len;
	public int lib_title_len;
	
	public int content_distance;
	public int babe_content_len;
	public int lib_content_len;
		 */
		// before we open the file check to see if it already exists
		boolean alreadyExists = false;//new File(filePath).exists();
			
		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(filePath, true), ',');
			
			// if the file didn't already exist then we need to write out the header line
			if (!alreadyExists)
			{
				csvOutput.write("id");
				csvOutput.write("source_id");
				csvOutput.write("babe_url");
				csvOutput.write("lib_url");
				csvOutput.write("babe_title");
				csvOutput.write("lib_title");
				csvOutput.write("title_distance");
				csvOutput.write("babe_title_len");
				csvOutput.write("lib_title_len");
				csvOutput.write("content_distance");
				csvOutput.write("babe_content_len");
				csvOutput.write("lib_content_len");
				csvOutput.endRecord();
			}
			// else assume that the file already has the correct header line
			
			for(CompareResult r : data) {
				csvOutput.write(r.id);
				csvOutput.write(r.source_id);
				csvOutput.write(r.babe_url);
				csvOutput.write(r.lib_url);
				csvOutput.write(r.babe_title);
				csvOutput.write(r.lib_title);
				csvOutput.write(r.title_distance + "");
				csvOutput.write(r.babe_title_len + "");
				csvOutput.write(r.lib_title_len + "");
				csvOutput.write(r.content_distance + "");
				csvOutput.write(r.babe_content_len + "");
				csvOutput.write(r.lib_content_len + "");
				csvOutput.endRecord();
			}
			
			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	

}

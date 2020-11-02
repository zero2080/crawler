package com.crawler.util;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.halowd.saveImg.SaveImg;

public class ImageDownloader extends Thread{

	private static final Logger log = LogManager.getLogger(ImageDownloader.class);
	private static final String PATH = System.getProperty("os.name").indexOf("Window")>=0?"C:/test":"/root/item_thumbnail";
	private SaveImg saveImg;
	private String fileName;
	private String item_url;
	
	public ImageDownloader(String shop_name,String item_name,String item_url) {
		fileName = shop_name+"_"+item_name.replaceAll("[\\/:*?\"<>|]","__").replaceAll(" ","_").replaceAll("%","__");
		this.item_url=new String(item_url);
	}
	
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public void run() {
		saveImg = new SaveImg();
		try {
			saveImg.saveImgFromUrl(item_url, PATH, fileName);
		}catch(IOException e) {
			log.error("thumbnail down load error : "+e.getMessage());
		}
	}
}

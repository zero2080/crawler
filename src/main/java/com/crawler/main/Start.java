package com.crawler.main;

import com.crawler.config.Config;

public class Start {
	public static void main(String[] args) {
		
		Config.getInstance(args[0]);
		
		for(int i =0; i<Config.THREADCNT;i++) {
			
			Crawler c = new Crawler();
			c.round=i;
			c.start();
		}
	}
	
	
}
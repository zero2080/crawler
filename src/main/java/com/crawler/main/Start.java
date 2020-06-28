package com.crawler.main;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.crawler.config.Config;
import com.crawler.exception.CrawlerException;

public class Start {
	public static final Logger log = LogManager.getLogger(Start.class);
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		Config.setConfig(args[0]);
		int round = Config.TARGETCOUNT / Config.THREADUNIT;
		Crawler[] c = new Crawler[round%Config.THREADUNIT>=0?Config.TARGETCOUNT:round];
		int threadHolder = 0;
		
		while(true) {
			if(Config.TARGETCOUNT==0) {
				break;
			}else if(threadHolder>=Config.THREADCNT){
				try {
					for(int i=0;i<c.length;i++) {
						if(c[i]==null || c[i].threadEnd) {
							//쓰레드가 끝나면 while문을 다시 돌게 한다.
							continue;
						}else {
							throw new CrawlerException();
						}
					}
				}catch(CrawlerException e) {
					continue;
				}
			}
			if(threadHolder==c.length) {
				break;
			}
			c[threadHolder] = new Crawler(threadHolder);
			Crawler.round=threadHolder;
			c[threadHolder].start();
			threadHolder++;
		}
		
		int print =0;
		
		while(true) {
			try {
				Map<Integer,Boolean> trigger = new HashMap<Integer,Boolean>();
				Map<Integer,String> tNames = new HashMap<Integer,String>();
				for(int i =0; i<threadHolder;i++) {
					trigger.put(i,c[i].threadEnd);
					tNames.put(i,c[i].tName);
				}
				
				StringBuffer logger;
				
				if(print%500000==0) {
					logger = new StringBuffer();
					for(int i=0;i<trigger.size();i++) {
						logger.append(tNames.get(i) + " : " + (trigger.get(i)?"stop":"working..."));
						if(i+1<trigger.size()) {
							logger.append(" / ");
						}
					}
					log.info(logger.toString());
					print=0;
				}
				
				boolean tmp = trigger.get(0);
				
				for(int i=0;i<trigger.size();i++) {
					if(tmp && trigger.get(i)) {
						continue;
					}else {
						throw new CrawlerException();
					}
				}
				
				break;
			}catch(CrawlerException e) {
				print++;
				continue;
			}catch(Exception e) {
				log.error("unknown error : "+e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		long end = System.currentTimeMillis()-start;
		String crawlTime = (end/1000/60+":"+(end/1000)%60+":"+end%1000);
		log.info(String.format("Crawl Time : %s",crawlTime));
	}
}
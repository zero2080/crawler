package com.crawler.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.crawler.config.Config;
import com.crawler.db.Connector;
import com.crawler.model.CrawllingTarget;
import com.crawler.model.Product;

public class Crawler extends Thread{
	private static final Logger log = LogManager.getLogger(Crawler.class);
	
	private WebDriverWait wait;
	private ChromeDriver webDriver;
	private ChromeOptions options;
	private static Connector targetConn;
	private static Connector serviceConn;
	public boolean threadEnd = false;
	public String tName;

	public Crawler() {
		System.setProperty("webdriver.chrome.driver", Config.ROOTPATH+Config.BROWSERDRVIER);
		targetConn = new Connector("target");
		serviceConn = new Connector("service");
		
		options = new ChromeOptions();
		options.addArguments("headless");
		options.addArguments("start-maximized");
		options.addArguments("disable-dev-shm-usage");
		options.addArguments("no-sendbox");
		options.addArguments("disable-gpu");
	}
	
	public Crawler(int i) {
		this();
		if(round==0) {
			try {
				serviceConn.createTmpTable();
			}catch(Exception e) {
				log.info(e.getMessage());
				serviceConn.deleteTmpTable();
			}
		}
	}
	
	//크롤링
	public List<Product> getProductList(CrawllingTarget ct) {
		webDriver = new ChromeDriver(options);
		List<Product> pArr = new ArrayList<Product>();
		
		int round = 0;
		
		//scroll_type == 1 / 무한스크롤
		//scroll_type > 1 / 페이지 방식
		
		if(ct.getScroll_type()==1) {
			round=1;
		}else {
			/*
			 * 전체 페이지 가져오는 로직 
			 * CrawllingTarget객체에 다음페이지로 가는 공통 URL을 넣거나
			 *  페이지를 순차로 넘기면서 없는 페이지가 올때까지 반복한다.
			 */
			//임시로 넣은 값임
			round=2;
		}
		log.info(String.format("\t%s : start crawlling \n\t\t\t\t\t\t%s : total page - %d", Thread.currentThread().getName(),Thread.currentThread().getName(),round));
		
		for(int i = 1; i<=round;i++) {
			webDriver.get(ct.getShop_url()+ct.getPage_selector()+i);
			long height = 0;
			
			for(int j=1;true;j++) {
				
				height = (Long)webDriver.executeScript("return window.scrollY");
						
				wait = new WebDriverWait(webDriver,10);
				webDriver.executeScript("window.scrollTo(0,"+(j*500)+");");
				wait = new WebDriverWait(webDriver,10);
				
				long tmpheight = (Long)webDriver.executeScript("return window.scrollY");
				if(height==tmpheight) {
					break;
				}
			}
			//제품이름
     		List<WebElement> list = webDriver.findElements(By.cssSelector(ct.getProduct_name()));
     		//이미지 url
     		List<WebElement> imgList = webDriver.findElements(By.cssSelector(ct.getProduct_image()));
     		//기본가격
     		List<WebElement> priceList = webDriver.findElements(By.cssSelector(ct.getProduct_price()));
     		//제품 상세페이지 URL
     		List<WebElement> detailLink = webDriver.findElements(By.cssSelector(ct.getProduct_url()));
     		
     		if(list.size()!=imgList.size() 
 				|| imgList.size() != priceList.size()
 				|| priceList.size() != detailLink.size()){
     			log.info(String.format("Crawlling result miss match \n list size : %d \n img list size : %d \n price list size : %d \n detail urllink size : %d",list.size(),imgList.size(),priceList.size(),detailLink.size()));
     			return null;
     		}
     		
     		for(int j =0;j<list.size();j++) {
     			Product p = new Product(ct.getShop_name(),
										list.get(j).getText(),
										Integer.parseInt(priceList.get(j).getText().replaceAll("[^0-9]","")),
										0,
										detailLink.get(j).getAttribute("href"),
										imgList.get(j).getAttribute("src"));
     			pArr.add(p);
     		}
		}
		webDriver.quit();
		
		log.debug("getProductList() end");
		
		return pArr;
	}
	
	public static int round = 0;
	
	//Thread
	@Override
	public void run() {
		// TODO Auto-generated method stub
		tName=Thread.currentThread().getName();
		List<CrawllingTarget> list = targetConn.getTargetList(round);
		try {
			if(list.size()==0) {
				threadEnd=true;
				throw new Exception("CrawllingTarget List is empty");
			}
			
			log.info(String.format("===================  %s get TargetList OK  ====================",Thread.currentThread().getName()));
			if(list==null || list.size()==0) {
				//타켓 정보 수정 요청테이블에 정보 입력
				throw new Exception("CrawllingTarget List is empty");
			}
			boolean[] tmp = new boolean[list.size()];
			for(int i = 0;i<list.size();i++) {
				log.info(String.format("%s - Crawlling start - %s",Thread.currentThread().getName() , list.get(i).getShop_name()));
				
//				List<?> pList = getProductList(ct);	//리플렉션으로 어떤 객체도 올수있게
				
				log.info(String.format("Crawlling Target : %s / Crawl Description : %s", list.get(i).getShop_name(),list.get(i).getShop_description()));
				
				List<Product> pList = getProductList(list.get(i));
				tmp[i]=serviceConn.insertCrawllingResult(pList);
			}
			while(!threadEnd) {
				for(int i = 0; i< tmp.length;i++) {
					if(!tmp[i]) {
						continue;
					}
					threadEnd=true;
				}
			}
			Thread.sleep(10);
		} catch(Exception e){
			log.info(e.getMessage());
		}
		
		log.debug(String.format("%s : Crawler running",Thread.currentThread().getName()));
		if(threadEnd) {
			log.debug(Thread.currentThread().getName()+" Thread end ");
			
		}
	}
	
	public void close() {
		
		try {
			targetConn.connClose();
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		try {
			serviceConn.connClose();
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		
	}
}

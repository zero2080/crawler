package com.crawler.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.crawler.config.Config;
import com.crawler.config.CrawllingTarget;
import com.crawler.db.Connector;
import com.crawler.model.Product;

public class Crawler extends Thread{
	private static final Logger log = LogManager.getLogger(Crawler.class);
	
	@SuppressWarnings("unused")
	private WebDriverWait wait;
	private ChromeDriver webDriver;
	private ChromeOptions options;
	
	public Crawler() {
		System.setProperty("webdriver.chrome.driver", Config.ROOTPATH+Config.BROWSERDRVIER);
		
		options = new ChromeOptions();
		options.addArguments("headless");
		options.addArguments("start-maximized");
		options.addArguments("disable-dev-shm-usage");
		options.addArguments("no-sendbox");
		options.addArguments("disable-gpu");
	}
	
	public Crawler(int i) {
		this();
		
	}
	
	//크롤링
	public List<Product> getProductList(CrawllingTarget ct) throws Exception{
		webDriver = new ChromeDriver(options);
		List<Product> pArr = new ArrayList<Product>();
		
		int round = 0;
		
		//scroll_type==1 / 무한스크롤
		//scroll_type> 1 / 페이지 방식
		
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
		
		log.info("start crawlling");
		log.info("total page : "+round);
		
		for(int i = 0; i<round;i++) {
			webDriver.get(ct.getShop_url()+ct.getPage_selector()+i);
			
			long height = 0;
			while(true) {
				wait = new WebDriverWait(webDriver,10);
				webDriver.executeScript("window.scrollTo(0,"+(i+500)+");");
				wait = new WebDriverWait(webDriver,10);
				
				long tmpheight = (Long)webDriver.executeScript("return window.scrollY");
				if(tmpheight!=0 && height==tmpheight) {
					break;
				}
				height=tmpheight;
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
     				|| priceList.size() != detailLink.size()) {
     			log.info(String.format("Crawlling result miss match \n list size : %d \n img list size : %d \n price list size : %d \n detail urllink size : %d",list.size(),imgList.size(),priceList.size(),detailLink.size()));
     			return null;
     		}
     		for(int j =0;j<list.size();j++) {
     			pArr.add(new Product(ct.getShop_name(),
     								 list.get(j).getText(),
     								 Integer.parseInt(priceList.get(j).getText().replaceAll("[^0-9]","")),
     								 0,
     								 detailLink.get(j).getAttribute("href"),
     								 imgList.get(j).getAttribute("src")
 								 ));
     		}
		}
		return pArr;
	}
	
	
	public int round = 0;
	
	//Thread
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Map<Integer,List<CrawllingTarget>> result = new HashMap<Integer,List<CrawllingTarget>>();
		Connector targetConn = new Connector("target");
		Connector serviceConn = new Connector("service");
		try {
			List<CrawllingTarget> list = targetConn.getTargetList(round);
			
			if(list==null || list.size()==0) {
				throw new Exception("CrawllingTarget List is empty");
			}else if(list.size()>0) {
				result.put(round,list);
			}
			
			Thread.currentThread().setName(round+"_"+result.get(0).get(0).getShop_name());
			
			log.info(String.format("===================  %s get TargetList OK  ====================",Thread.currentThread().getName()));
			
			for(int j = 0;j<result.size();j++) {
				for(CrawllingTarget ct : result.get(j)) {
					log.info(String.format("Crawlling start - %s", ct.getShop_name()));
					
//					List<?> pList = getProductList(ct);	//리플렉션으로 어떤 객체도 올수있게
					
					List<Product> pList = getProductList(ct);
					serviceConn.insertCrawllingResult(pList);
				}
			}
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error(String.format(e.getMessage()));
			e.printStackTrace();
			return;
		} catch(Exception e){
			log.error(String.format(e.getMessage()));
			return;
		} finally {
			targetConn.connClose();
		}
	}
}

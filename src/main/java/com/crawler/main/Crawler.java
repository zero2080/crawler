package com.crawler.main;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.crawler.api.ProductList;
import com.crawler.config.Config;
import com.crawler.db.Connector;
import com.crawler.model.CrawllingTarget;
import com.crawler.model.Product;
import com.crawler.util.ImageDownloader;

public class Crawler extends Thread {
	private static final Logger log = LogManager.getLogger(Crawler.class);
	
	@SuppressWarnings("unused")
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
		
		if(System.getProperty("os.name").toLowerCase().indexOf("window")==-1) {
			//리눅스용 옵션
			options.addArguments("--no-sandbox"); // Bypass OS security model, MUST BE THE VERY FIRST OPTION
	        options.addArguments("--headless");
	        options.setExperimentalOption("useAutomationExtension", false);
	        options.addArguments("start-maximized"); // open Browser in maximized mode
	        options.addArguments("disable-infobars"); // disabling infobars
	        options.addArguments("--disable-extensions"); // disabling extensions
	        options.addArguments("--disable-gpu"); // applicable to windows os only
	        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
	        options.addArguments("--single-process");
		}else {
//			options.addArguments("headless");
//			options.addArguments("start-maximized");
//			options.addArguments("no-sendbox");
//			options.addArguments("disable-dev-shm-usage");
//			options.addArguments("disable-gpu");
		}
	}
	
	public Crawler(int i) {
		this();
		if(Config.TEST == null || !Config.TEST.equals("test")) {
			if(round==0) {
				try {
					serviceConn.createTmpTable();
				}catch(Exception e) {
					log.info(e.getMessage());
					serviceConn.deleteTmpTable();
				}
			}
		}
	}
	
	//크롤링
	public List<Product> getProductList(CrawllingTarget ct) {
		webDriver = new ChromeDriver(options);
		wait = new WebDriverWait(webDriver,10);
		List<Product> pArr = new ProductList<Product>();
		List<Product> page = new ProductList<Product>();
		
		log.info(String.format("%s : start crawlling ", Thread.currentThread().getName()));
		
		for(int i = 1; true;i++) {
			List<Product> tmp_page = new ProductList<Product>();
			
			String pageUrl = ct.getShop_url();
			
			if(ct.getScroll_type()==0) {
				log.debug("page type");
				if(ct.getPage_size()==0) {
					pageUrl+=ct.getPage_selector()+i;
				}else {
					pageUrl+=ct.getPage_selector()+i+ct.getPage_size_selector()+ct.getPage_size();
				}
				
				webDriver.get(pageUrl);
				long height = 0;
				
				for(int j=1;true;j++) {
					height = (Long)webDriver.executeScript("return window.scrollY");
					webDriver.executeScript("window.scrollTo(0,"+(j*500)+");");
					
					long tmpheight = (Long)webDriver.executeScript("return window.scrollY");
					
					if(height==tmpheight) {
						break;
					}
				}
			}else {
				log.debug("infinit scroll");
				webDriver.get(pageUrl);
				long height = 0;
				
				for(int j=1;true;j++) {
					height = (Long)webDriver.executeScript("return window.scrollY");
					webDriver.executeScript("window.scrollTo(0,"+(j*500)+");");
					
					long tmpheight = (Long)webDriver.executeScript("return window.scrollY");
					
					if(height==tmpheight) {
						break;
					}
				}
			}
			//제품
     		List<WebElement> list = webDriver.findElements(By.cssSelector(ct.getProduct()));
     		
     		if(list.size()==0) {
     			log.debug("list size : 0");
     			break;
     		}
//			스크린샷 코드
//     		TakesScreenshot screenshot = (TakesScreenshot)webDriver;
// 			byte[] bytes = screenshot.getScreenshotAs(OutputType.BYTES);
// 			try {
// 				FileOutputStream fos = new FileOutputStream("C:/Develop/shot.png");
// 				fos.write(bytes);
// 				fos.close();
// 			}catch(Exception e) {
// 				e.printStackTrace();
// 			}
// 			
     		for(int j =0;j<list.size();j++) {
     			WebElement elem = list.get(j);
     			String product_name = elem.findElement(By.cssSelector(ct.getProduct_name())).getText().trim();
					   product_name = product_name.replaceAll(",", "，");
					   product_name = product_name.replaceAll("\n", " ");
					   product_name = product_name.replaceAll("★사은품증정★ ", "");
					   product_name = product_name.replaceAll("'", "＇"); 
     			String price = "";
     			int parsePrice = 0;
     			String dis_price = "";
     			if(priceChecker(ct.getProduct_discount_price())) {
     				try {
     					String script = String.format("return document.querySelectorAll('"+ct.getProduct()+" "+ct.getProduct_price()+"')[%d].innerText.replace(/\\n/g,'').replace(/ +/g,'');",j);
     					String priceBox = (String)webDriver.executeScript(script);
     					
     					StringTokenizer st = new StringTokenizer(priceBox,"원");
	     				
	     				st.nextToken();
	     				price = st.nextToken().replaceAll("[^0-9]", "");
	     				parsePrice = Integer.parseInt(price);
	     				dis_price = st.nextToken().replaceAll("[^0-9]", "");
	     				
	     			}catch(NoSuchElementException e	) {
	     				String err_msg = e.getMessage();
	     				int next_line_idx = err_msg.indexOf("\n");
	     				if(next_line_idx>0) {
	     					err_msg = err_msg.substring(0,next_line_idx);
	     				}
	     				
	     				log.error(String.format("Price setting error: %s",err_msg));
	     			}
     			}else {
     				try {
	     				dis_price = new String(elem.findElement(By.cssSelector(ct.getProduct_discount_price())).getText());
	     				if(dis_price.indexOf("(")>=0) {
	     					dis_price=dis_price.substring(0,dis_price.indexOf("("));
	     				}
	     				dis_price = dis_price.replaceAll("[^0-9]","");
	     			}catch(NoSuchElementException e	) {
	     				String err_msg = e.getMessage();
	     				int next_line_idx = err_msg.indexOf("\n");
	     				if(next_line_idx>0) {
	     					err_msg = err_msg.substring(0,next_line_idx);
	     				}
	     				
	     				log.error(err_msg);
	     			}
	     			try {
	     				price = new String(elem.findElement(By.cssSelector(ct.getProduct_price())).getText());
	     				price = price.replaceAll("[^0-9]","");
	     				parsePrice = Integer.parseInt(price);
	     			}catch(NoSuchElementException e	) {
	     				String err_msg = e.getMessage();
	     				int next_line_idx = err_msg.indexOf("\n");
	     				if(next_line_idx>0) {
	     					err_msg = err_msg.substring(0,next_line_idx);
	     				}
	     				
	     				log.error(err_msg);
	     			}catch(NumberFormatException ne) {
	     				price = dis_price;
	     			}
     			}
				
				String detailLink = elem.findElement(By.cssSelector(ct.getProduct_url())).getAttribute("href");
				String img = elem.findElement(By.cssSelector(ct.getProduct_image())).getAttribute("src");
				
				//TODO 여기 이미지 다운로드
				if(ct.getShop_name().equals("나투라비타") 
						|| ct.getShop_name().equals("네러메몰(네이쳐러브메레)")) {
					ImageDownloader downloader = new ImageDownloader(ct.getShop_name(),product_name,img);
					downloader.start();
					String type = img.substring(img.lastIndexOf("."),img.lastIndexOf(".")+4);
					img = "http://babybaily.cdn3.cafe24.com/"+downloader.getFileName()+type;
					log.debug("file_name = "+img);
				}
				String soldoutChecker = ct.getSoldout_checker();
				int item_state = 0;
				
				if(soldoutChecker!=null && !soldoutChecker.trim().equals("")) {
					StringTokenizer st = new StringTokenizer(soldoutChecker,"$$");
					WebElement tmp =null;
					try {
						tmp = elem.findElement(By.cssSelector(st.nextToken()));
					}catch(Exception e) {
						String err_msg = e.getMessage();
	     				int next_line_idx = err_msg.indexOf("\n");
	     				if(next_line_idx>0) {
	     					err_msg = err_msg.substring(0,next_line_idx);
	     				}
						log.error("soldout checker error : "+err_msg);
					}
					if(tmp!=null) {
						String text;
						if(st.nextToken().equals("0")) {
							text = tmp.getAttribute("alt");
						}else {
							text = tmp.getAttribute("src");
						}
						item_state = text.indexOf(st.nextToken())>-1?1:0;
						log.info(String.format("this item soldout / shop : %s / item : %s /soldout : %s",ct.getShop_name(),product_name,item_state==0?"정상":"품절"));
					}
				}
				String resultPrice = dis_price==null?price:dis_price.trim().equals("")?price:dis_price;
     			Product p = new Product(ct.getCategory1(),
     									ct.getCategory2(),
     									ct.getShop_name(),
										product_name,
										ct.getShop_description(),
										ct.getTarget(),
										parsePrice,
										resultPrice.equals("")?0:Integer.parseInt(resultPrice),
										detailLink,
										img,
										resultPrice.equals("")?1:item_state,
										"{\"option1\":\"" + ct.getOption_selector_1() + "\", \"option2\": \""+(ct.getOption_selector_2()==null?"null":ct.getOption_selector_2().equals("")?"null":ct.getOption_selector_2())+"\", \"option3\":\""+(ct.getOption_selector_3()==null?"null":ct.getOption_selector_3().equals("")?"null":ct.getOption_selector_3())+"\"}"
										);
     			tmp_page.add(p);
     		}
     		
     		if(i>0) {
     			if(!page.equals(tmp_page)) {
     				page = tmp_page;
     			}else {
     				log.debug(String.format(" page : %s \n\t\t\t\t\t tmp_page : %s", page.toString(),tmp_page.toString()));
     				log.debug("same_page");
     				break;
     			}
     		}
     		
     		pArr.addAll(tmp_page);
		}
		webDriver.quit();
		
		log.debug("getProductList() end");
		
		return pArr;
	}
	
//	private int getTotalPageCount(CrawllingTarget ct) {
//		int total = 0;
//		while(true) {
//			webDriver.get(ct.getShop_url()+ct.getPage_selector()+total);
//			List<WebElement> list = webDriver.findElements(By.cssSelector(ct.getProduct_name()));
//			System.out.println(list.get(0).getText());
//			if(list.size()==0) {
//				break;
//			}
//			total++;
//		}
//		return total;
//	}
//	
	
	public static int round = 0;
	
	//Thread
	@Override
	public void run() {
		// TODO Auto-generated method stub
		tName=Thread.currentThread().getName();
		List<CrawllingTarget> list ;
		if(Config.TEST != null && Config.TEST.equals("test")) {
			list = targetConn.getTestRow();
		}else {
			log.info(String.format("start thread / round : %d", round));
			list = targetConn.getTargetList(round);
		}
		try {
			if(list==null || list.size()==0) {
				threadEnd=true;
				throw new Exception("CrawllingTarget List is empty");
			}
			
			log.info(String.format("===================  %s get TargetList OK  ====================",Thread.currentThread().getName()));
			
			log.debug(String.format("%s : Crawler running",tName));
			
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
					log.debug(tName+" : end ");
				}
			}
			Thread.sleep(10);
		} catch(Exception e){
			log.info(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean priceChecker(String priceChecker) {
		if(priceChecker.indexOf("script")==0) {
			return true;
		}else {
			return false;
		}
	}
	
	public static void cleanup() throws Exception{
		serviceConn.cleanUp();
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
	
	public void browserClose() {
		try {
			webDriver.close();
			webDriver.quit();
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}
}

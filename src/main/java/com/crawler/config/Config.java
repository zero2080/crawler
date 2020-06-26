package com.crawler.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
	private	static	final	Logger	log				= LogManager.getLogger(Config.class);
	public	static	final	String	OS				= System.getProperty("os.name").toLowerCase();
	
	private			final	String	TARGET			= "target";
	private			final	String	SERVICE			= "service";
	
	private			final	String	DRIVER			= ".db.driver";
	private			final	String	URL				= ".db.url";
	private			final	String	ID				= ".db.id";
	private			final	String	PW				= ".db.pw";
	private			final	String	VERIFYQUERY		= ".verifyQuery";
	
	public	static			String	ROOTPATH		= null;
	public	static			String	BROWSERDRVIER	= null;
	
	public	static			String	TARGETDRIVER	= null;
	public	static			String	TARGETURL		= null;
	public	static			String	TARGETID		= null;
	public	static			String	TARGETPW		= null;
	public	static			String	TARGETVERIFYQUERY= null;
	
	
	public	static			String	SERVICEDRIVER	= null;
	public	static			String	SERVICEURL		= null;
	public	static			String	SERVICEID		= null;
	public	static			String	SERVICEPW		= null;
	public	static			String	SERVICEVERIFYQUERY= null;
	public	static			String	SERVICETABLE	= null;
	
	public	static			int		THREADCNT		= 0;
	public	static			int		THREADUNIT		= 0;
	
	public 	static			int		TARGETCOUNT		= 0;
	public 	static			int		THREADROUND	= 0;
	private static Config instance;
	
	public static Config getInstance() {
		return instance;
	}
	public static void setConfig(String config) {
		if(instance == null) {
			instance =  new Config(config);
		}
	}
	
	private Config(String config) {
		File file = new File(config);

		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(config));
			
			Config.THREADCNT		= Integer.valueOf(prop.getProperty("crawler.parallel.degree"));
			Config.THREADUNIT		= Integer.valueOf(prop.getProperty("crawler.parallel.unit"));
			Config.BROWSERDRVIER	= prop.getProperty("crawler.browserdriver")+(OS.indexOf("window")>=0?".exe":"");
			Config.ROOTPATH			= System.getProperty("user.dir")+'/'+prop.getProperty("crawler.driverPath");
			
			Config.TARGETDRIVER		= prop.getProperty(TARGET+DRIVER);
			Config.TARGETURL		= prop.getProperty(TARGET+URL);
			Config.TARGETID			= prop.getProperty(TARGET+ID);
			Config.TARGETPW			= prop.getProperty(TARGET+PW);
			Config.TARGETVERIFYQUERY= prop.getProperty(TARGET+VERIFYQUERY);
			
			Config.SERVICEDRIVER	= prop.getProperty(SERVICE+DRIVER);
			Config.SERVICEURL		= prop.getProperty(SERVICE+URL);
			Config.SERVICEID		= prop.getProperty(SERVICE+ID);
			Config.SERVICEPW		= prop.getProperty(SERVICE+PW);
			Config.SERVICEVERIFYQUERY= prop.getProperty(SERVICE+VERIFYQUERY);
			Config.SERVICETABLE		= prop.getProperty(SERVICE+".table");
			
			com.crawler.db.Connector conn = new com.crawler.db.Connector(TARGET);
			Config.TARGETCOUNT		= conn.getTargetCnt();
//			conn.connClose();
			
			log.info(String.format("==============Config setting Ok==============="));
		}catch(Exception e) {
			e.printStackTrace();
			help();
		}
	};
	
	public static void help() {
		System.out.println("java -cp [jar filePath] -Dlog4j.configurationFile=./log4j2.xml -jar crawler-1.0-all.jar");
	}
	
}
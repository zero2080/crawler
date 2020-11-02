package com.crawler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUploader {
	FTPClient ftp = null;
	
	private static final String HOSTADDR = "iup.cdn3.cafe24.com";
	private static final String HOSTDIR = "/www/";
	private static final String HOSTID = "babybaily";
	private static final String HOSTPW = "ll110066";
	private static final String LOCALPATH = System.getProperty("os.name").indexOf("Window")>=0?"C:\\test\\":"/root/item_thumbnail/";;
	
    public FTPUploader() throws Exception{
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        if(System.getProperty("os.name").toLowerCase().indexOf("windows")>=0) {
        	ftp.setControlEncoding("EUC-KR");
        }else {
        	ftp.setControlEncoding("UTF-8");
        }
        
        int reply;
        ftp.connect(HOSTADDR);//호스트 연결
        reply = ftp.getReplyCode();
        
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        
        ftp.login(HOSTID,HOSTPW);//로그인
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    public void uploadFile() throws Exception {
    	File[] fileList = new File(LOCALPATH).listFiles();
    	
    	for(File file:fileList) {
	        try(InputStream input = new FileInputStream(file)){
	        	String host = HOSTDIR+file.toString().replace(LOCALPATH, "");
		        this.ftp.storeFile(host, input);
		        //storeFile() 메소드가 전송하는 메소드
	        }
    	}
    }
    
    public void disconnect(){
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
                
                File[] fileList = new File(LOCALPATH).listFiles();
                for(File file:fileList) {
                	file.delete();
                }
            } catch (IOException f) {
                f.printStackTrace();
            }
            
        }
    }
}

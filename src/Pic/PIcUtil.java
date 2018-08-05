package Pic;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Scanner;

import javax.swing.JFileChooser;

import sinaUtils.SinaStoreSDK;  

public class PIcUtil {
	
	private File[] openFile() {
		JFileChooser fd = new JFileChooser();  
		//fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  
		fd.setMultiSelectionEnabled(true);
		fd.showOpenDialog(null);  
		File[] fs = fd.getSelectedFiles();  
		return fs.length>0?fs:null;
	}
	
	public String[] uploadPics(int days) {
		String prefix = System.currentTimeMillis()+"";
		int minutes = days*24*60;
		File[] files = openFile();
		String[] paths = new String[files.length];
		SinaStoreSDK sss = new SinaStoreSDK();
		for(int i=0;i<files.length;i++) {
			String webPath = "markdownPic/ts"+prefix+files[i].getName();
			sss.putObject("music-store", webPath, files[i]);
			paths[i] = sss.generateUrl("music-store", webPath, minutes);
		}
		return paths;
	}
	
	public void saveFile() {
		JFileChooser jf = new JFileChooser();  
		jf.setFileSelectionMode(JFileChooser.SAVE_DIALOG | JFileChooser.DIRECTORIES_ONLY);  
		jf.showDialog(null,null);  
		File fi = jf.getSelectedFile();  
		String f = fi.getAbsolutePath()+"\\test.txt";  
		System.out.println("save: "+f);  
		try{  
		    FileWriter out = new FileWriter(f);  
		    out.write("successful!!!");  
		    out.close();  
		}  
		catch(Exception e){}  
	}
	
}

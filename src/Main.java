import java.util.Scanner;

import Pic.PIcUtil;

public class Main {
	
	public static void main(String args[]) {
		
		PIcUtil pIcUtil = new PIcUtil();
		Scanner scanner = new Scanner(System.in);
		System.out.println("0: exit\n1: next");
		
		while(Integer.parseInt(scanner.nextLine())!=0) {
			
			String[] paths = pIcUtil.uploadPics(10);
			for(String path:paths) {
				System.out.println(path);
			}	
			
			System.out.println("0: exit\n1: next");
		}
		
		scanner.close();
		System.out.println("end");
		
	}

}

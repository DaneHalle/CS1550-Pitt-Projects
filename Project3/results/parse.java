
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class parse
{
	public static void main(String args[])
	{
		File file=new File("hundred.txt");
		Scanner scan;
		try{
			scan=new Scanner(file);
		}catch(FileNotFoundException e){
			System.out.println("File doesn't exist!");
			return;
		}

		String trace=scan.nextLine();
		int prev=Integer.MAX_VALUE; 
		while(scan.hasNextLine()){
			String line=scan.nextLine();
			if(line.charAt(0)=='='){
				trace=line;
				prev=Integer.MAX_VALUE;
			}else{
				int frames=Integer.parseInt(line.substring(0,line.indexOf(":")));
				int faults=Integer.parseInt(line.substring(line.indexOf(":")+1));
				if(faults<=prev){
					prev=faults;
				}else{
					System.out.println("Anomoly in trace: "+trace+" at frame "+frames);
					prev=faults;
				}
			}

		}
	}
}
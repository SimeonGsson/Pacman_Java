import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HighScoreManager {
	
	public static void main(String[] args) {
		
		
	try {
		FileReader reader = new FileReader("highscores.txt");
		int data = reader.read();
		while(data != -1){
			System.out.print((char)data);
			data = reader.read();
		}
		reader.close();
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
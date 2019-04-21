import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static void main(String[] args) throws IOException{
		Socket s = new Socket("localhost", 5000);
		
		PrintWriter pr = new PrintWriter(s.getOutputStream());
		pr.println("Client message to server");
		pr.flush();
		
		
		InputStreamReader isr = new InputStreamReader(s.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String str = br.readLine();
		
		System.out.println("Server saying : " + str);
	}
	
}

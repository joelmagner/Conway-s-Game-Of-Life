import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) throws IOException{
		ServerSocket ss = new ServerSocket(5000);
		Socket s = ss.accept();
		
		System.out.println("Client connected");
		
		InputStreamReader isr = new InputStreamReader(s.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String str = br.readLine();
		
		System.out.println("Client saying : " + str);
	
		
		PrintWriter pr = new PrintWriter(s.getOutputStream());
		pr.println("Server message to client");
		pr.flush();
		
		if(str == "!close") {
			ss.close();
		}
	}
	
}

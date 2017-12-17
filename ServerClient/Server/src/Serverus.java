import java.net.*;
import java.io.*;

public class Serverus {
	public static void main(String[] args) throws IOException {
		int portNumber = 4040;

		while (true) {
			System.out.println("am pornit in pula mea de server pa pornNumberu " + portNumber);

			try (ServerSocket serverSocket = new ServerSocket(portNumber);
					Socket clientSocket = serverSocket.accept();

					PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
				System.out.println("Am primit cleent");
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out.println("Cleentu o zis " + inputLine);
					if (inputLine.toLowerCase().contains("close_all"))
						break;
					inputLine = "\"" + inputLine + "\" o zis cleentu";
					out.println(inputLine);
				}
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port " + portNumber
						+ " or listening for a connection");
				System.out.println(e.getMessage());
			}
			System.out.println("Closing server connection...");
		}
	}
}
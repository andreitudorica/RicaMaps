import java.net.*;
import java.io.*;
import java.lang.Thread;

public class Serverus {
	public static void main (String[] args) {
		System.out.println("Started multithreaded server on port 4040");

		try (ServerSocket serverSocket = new ServerSocket(4040);) {
			while (true) {
				Socket clientSocket = serverSocket.accept();

				ClientHandler handler = new ClientHandler(clientSocket);
				handler.start();
			}
		} catch (IOException e) {
			System.out.println(
					"Exception caught when trying to listen on port 4040 or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}

class ClientHandler extends Thread {
	Socket client;

	ClientHandler(Socket client) {
		this.client = client;
	}

	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
			System.out.println("Am primit cleent");
			while (true) {
				String line = reader.readLine();
				if (line.toLowerCase().equals("close")) //daca primeste close se inchide threadul cu conexiunea curenta
					break;
				
				//in line ai stringu de la client. faci aici interogarea si dai write.println cu mesajul pe care vrei sa il trimiti
				System.out.println("Cleentu o zis " + line);
				line = "\"" + line + "\" o zis cleentu";
				writer.println(line);
				//astea 3 linii le schimbi cu ce iti doreste tie inima
				
			}
		} catch (Exception e) {
			System.err.println("Exception caught: client disconnected.");
		} finally {
			try {
				client.close();
			} catch (Exception e) {
				;
			}
		}
	}
}
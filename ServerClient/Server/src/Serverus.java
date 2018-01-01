import java.net.*;
import java.io.*;
import java.lang.Thread;

public class Serverus {
	private static String path = new String("D:\\Andrei\\Scoala\\SE\\RicaMaps\\ServerClient\\Routes.txt");
	private static String[][][] answerStrings = new String[200][200][1500];
	private static double[][] mapNodes = new double[200][2]; // lat = [n][0],
																// long = [n][1]
	private static int nrOfNodes = 0;

	public static void main(String[] args) throws IOException {

		// read files and put in ram
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		line = br.readLine();
		System.out.println(line);
		String[] nodes = line.split("\\|");
		System.out.println(nodes[0]);
		System.out.println(nodes[1]);
		System.out.println(nodes[2]);
		System.out.println(nodes[3]);
		for (String coords : nodes) { // read nodes
			// lat long
			System.out.println(coords);
			coords = coords.substring(1, coords.length() - 1);
			String[] latLong = coords.split(",");
			mapNodes[nrOfNodes][0] = Double.parseDouble(latLong[0]);
			mapNodes[nrOfNodes][1] = Double.parseDouble(latLong[1]);
			nrOfNodes++;
		}

		while ((line = br.readLine()) != null) { // read routes
			String[] exploded = line.split("\\|"); // start index 0, end index
													// 1, moment in minutes 2,
													// string 3, -h-m 4
			int indexStart, indexEnd, time;
			indexStart = Integer.parseInt(exploded[0]);
			indexEnd = Integer.parseInt(exploded[1]);
			time = Integer.parseInt(exploded[2]);
			String[] coords = exploded[3].split(",");
			StringBuilder sb = new StringBuilder(
					"{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[");

			sb.append("[");
			sb.append(mapNodes[Integer.parseInt(coords[0])][1]);
			sb.append(",");
			sb.append(mapNodes[Integer.parseInt(coords[0])][0]);
			sb.append("]");
			int first = 0;
			for (String c : coords) {
				if (first != 0) {
					sb.append(",[");
					sb.append(mapNodes[Integer.parseInt(c)][1]);
					sb.append(",");
					sb.append(mapNodes[Integer.parseInt(c)][0]);
					sb.append("]");
				}
				first = 1;
			}
			
			sb.append("]}}]}");
			sb.append(exploded[4]);

			answerStrings[indexStart][indexEnd][time] = sb.toString();
		}
		br.close();

		System.out.println("Started multithreaded server on port 7900");

		try (ServerSocket serverSocket = new ServerSocket(7900);) {
			while (true) {
				Socket clientSocket = serverSocket.accept();

				ClientHandler handler = new ClientHandler(clientSocket);
				handler.start();
			}
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port 7900 or listening for a connection");
			System.out.println(e.getMessage());
		}
	}

	public static String getAnswer(String start, String end, int time) {
		int indexStart = getIndex(start);
		int indexEnd = getIndex(end);
		return answerStrings[indexStart][indexEnd][time];
	}

	private static int getIndex(String latlong) {

		String[] ll = latlong.split(",");
		double lati = Double.parseDouble(ll[0]);
		double longi = Double.parseDouble(ll[1]);
		double deltaMin = 100.0;
		int indexMin = -1;

		for (int i = 0; i < nrOfNodes; i++) {
			double currentDelta = Math.abs(mapNodes[i][0] - lati) + Math.abs(mapNodes[i][1] - longi);
			if (currentDelta < deltaMin) {
				deltaMin = currentDelta;
				indexMin = i;
			}
		}

		return indexMin;

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
				System.out.println("Cleentu o zis " + line);
				// line will be x1-y1-x2-y2-h-m

				String[] exploded = line.split("-");
				System.out.println(exploded[0]);
				System.out.println(exploded[1]);
				System.out.println(exploded[2]);
				System.out.println(exploded[3]);
				System.out.println(exploded[4]);
				System.out.println(exploded[5]);
				int time = Integer.parseInt(exploded[4]) * 60 + Integer.parseInt(exploded[5]);

				String ans = Serverus.getAnswer(exploded[0] + "," + exploded[1], exploded[2] + "," + exploded[3], time);

				if (line.toLowerCase().equals("close")) // daca primeste close
														// se inchide threadul
														// cu conexiunea curenta
					break;

				System.out.println(ans);
				writer.println(ans);
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
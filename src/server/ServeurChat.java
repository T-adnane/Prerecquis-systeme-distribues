package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServeurChat extends Thread {
	private boolean isActive=true;
	private int nombreClient=0;
	private List<Conversation> clients=new ArrayList<Conversation>();
	public static void main(String[] args) {
		new ServeurChat().start();

	}
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(1234);
			System.out.println("Démarrage de serveur ...");
			while(true) {
				Socket socket = ss.accept();
				++nombreClient;
				Conversation conversation = new Conversation(socket, nombreClient);
				clients.add(conversation);
				conversation.start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	class Conversation extends Thread {
		protected Socket socketClient;
		protected int numero;
		public Conversation(Socket s, int num) {
			this.socketClient = s;
			this.numero = num;
		}
		
		public void broadcastMessage(String message, Socket socket, int numClient) {
			try {
				for(Conversation client:clients) {
					if(client.socketClient != socket) {
						if (client.numero==numClient || numClient==-1) {
							PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(), true);
							printWriter.println(message);
						}	
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void run() {
			try {
				InputStream is = socketClient.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				OutputStream os = socketClient.getOutputStream();
				PrintWriter pw = new PrintWriter(os,true);
				String IP = socketClient.getRemoteSocketAddress().toString();
				System.out.println("Connextion du client numéro "+numero+" IP = "+IP);
				pw.println("Bien venue vous etes le client numéro "+numero);
				while(true) {
					String req = br.readLine();
					if (req.contains("=>")) {
						String[] requestParams=req.split("=>");
						if(requestParams.length==2);
						String message = requestParams[1];
						int numeroClient = Integer.parseInt(requestParams[0]);
						broadcastMessage(message,socketClient,numeroClient);
					}else {
						broadcastMessage(req,socketClient,-1);
					}
					
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

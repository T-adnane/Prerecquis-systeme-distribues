package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServeurJeu extends Thread {
	private int nombreClient;
	private int nombreSecret;
	private boolean fin;
	private String gagnant;
	public static void main(String[] args) {
		new ServeurJeu().start();

	}
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(1234);
			System.out.println("Démarrage de serveur ...");
			nombreSecret = new Random().nextInt(1000);
			while(true) {
				Socket socket = ss.accept();
				++nombreClient;
				new Conversation(socket, nombreClient).start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	class Conversation extends Thread {
		private Socket socket;
		private int numeroClient;
		public Conversation(Socket s, int num) {
			this.socket = s;
			this.numeroClient = num;
		}
		public void run() {
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os,true);
				String IP = socket.getRemoteSocketAddress().toString();
				System.out.println("Connextion du client numéro "+numeroClient+" IP = "+IP);
				pw.println("Bien venue vous etes le client numéro "+numeroClient);
				pw.println("Deviner le nombre Secret .......");
				while(true) {
					String req = br.readLine();
					int nombre = Integer.parseInt(req);
					System.out.println("Le Client "+IP+" Tentative avec le nombre "+nombre);
					if(fin == false) {
						if(nombre>nombreSecret) {
							pw.println("Votre nombre est sup au nombreSecret");
						}else if(nombre<nombreSecret) {
							pw.println("Votre nombre est inf au nombreSecret");}
						else {
							pw.println("Vous avez gagné");
							gagnant = IP;
							System.out.println("BRAVO au gagnant "+gagnant);
							fin = true;
						}
					}else {
						pw.println("Jeu terminé, le gagnant est "+gagnant);
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

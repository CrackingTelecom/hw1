/**
 * @file: Server.java
 * 
 * @author: Chinmay Kamat <chinmaykamat@cmu.edu>
 * 
 * @date: Feb 15, 2013 1:13:37 AM EST
 * 
 * 
 * @refined by Xintong Yu & Hao Wang 
 * at Feb 10, 2014
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket srvSock;

	public static void main(String args[]) {
		int port = 8080;
		String path="";


		/* Parse parameter and do args checking */
		if (args.length != 2) {
			System.err.println("Usage: java Server <port_number> <Absolute path to www directory>");
			System.exit(1);
		}

		try {
			port = Integer.parseInt(args[0]);
			path=args[1];
		} catch (Exception e) {
			System.err.println("Usage: java Server <port_number> <Absolute path to www directory>");
			System.exit(1);
		}

		if (port > 65535 || port < 1024) {
			System.err.println("Port number must be in between 1024 and 65535");
			System.exit(1);
		}

		try {
			/*
			 * Create a socket to accept() client connections. This combines
			 * socket(), bind() and listen() into one call. Any connection
			 * attempts before this are terminated with RST.
			 */
			srvSock = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Unable to listen on port " + port);
			System.exit(1);
		}

		while (true) {
			Socket clientSock;
			try {
				/*
				 * Get a sock for further communication with the client. This
				 * socket is sure for this client. Further connections are still
				 * accepted on srvSock
				 */
				clientSock = srvSock.accept();
				ServerThread serverThread=new ServerThread(clientSock,path);
				System.out.println("Accpeted new connection from "
						+ clientSock.getInetAddress() + ":"
						+ clientSock.getPort());
				Thread thread=new Thread(serverThread);
				thread.start();
			} catch (IOException e) {
				continue;
			}
		}
	}
}

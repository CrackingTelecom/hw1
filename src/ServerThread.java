/**
 * @file: ServerThread.java
 * 
 * @author: Xintong Yu & Hao Wang <@andrew.cmu.edu>
 * 
 * @date: Feb 10, 2014 
 * 
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread implements Runnable {

	private Socket socket;
	private String path="";
	private BufferedReader inStream = null;
	private DataOutputStream outStream = null;
	private String buffer = "";

	public ServerThread(Socket socket,String path) {
		this.path=path;
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			inStream = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outStream = new DataOutputStream(socket.getOutputStream());

			buffer = inStream.readLine();

			RespondRequest request = new RespondRequest();
			String respond = request.respond(buffer,path);
			outStream.writeBytes(respond);
			outStream.flush();

			//System.out.println(respond);

			socket.close();
		} catch (IOException e) {
			System.err.println(e);
			socket = null;
		}

		finally {
			try {
				if (outStream != null)
					outStream.close();
				if (inStream != null)
					inStream.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

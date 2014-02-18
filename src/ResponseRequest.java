/**
 * @file: ResponseRequest.java
 * 
 * @author: Xintong Yu & Hao Wang <@andrew.cmu.edu>
 * 
 * @date: Feb 10, 2014 
 * 
 */

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

class RespondRequest {
	private String method = null;
	private String url = null;
	private String protocol_version = null;

	private String respond_status = "";
	private String respond_header = "";
	private String respond_body = "";

	private String path = "";

	public String respond(String request_line, String absoluatePath) throws IOException {
		if (request_line == null) {
			System.out.println("null request line");
			return "";
		}
		this.path = absoluatePath;

		String[] request = request_line.split(" ");
		if (request.length != 3) {
			System.err.println("Error: improper request: " + request_line);
			buildResponse(404); // what to respond here??
			return respond_status;
		}

		method = request[0];
		url = request[1];
		if (url.equals("/"))
			url += "index.html";
		protocol_version = request[2];

		/*check if the method in request status is valid*/
		if (!(method.equals("GET") || method.equals("HEAD"))) {
			System.err.println("Error: improper request: " + request_line);
			buildResponse(501);
			return respond_status;
		}


		/* check if the protocol version in request status is valid */
		String[] strings = protocol_version.split("/");
		if (strings.length != 2) {
			buildResponse(404);
			return respond_status;
		}
		if (!strings[0].equalsIgnoreCase("http")) {
			buildResponse(404);
			return respond_status;
		}

		/*every thing is valid and respond with the required content */
		/*set status line*/
		buildResponse(200);
		
		/*set header*/
		respond_header += "Server: Simple/1.0\r\n";
		String type = GetMime.getMimeType(url);
		if (type == null)
			type = "text/css";
		respond_header += "Content-Type: " + type + " \r\n";
		respond_header += "\r\n";

		//if the request method is HEAD, then just return status line and the headers
		if(method.equalsIgnoreCase("HEAD"))
			return respond_status+respond_header;
		
		/*set body*/
		String fullPath = path + url;
		System.out.println("reading file : " + fullPath);

		//if the required content is an image, use the image package to read
		if (type.contains("image")) {
			BufferedImage image = ImageIO.read(new File(fullPath));
			ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
			
			//transfer the image to stream
			ImageIO.write(image, type.split("/")[1].toString(), arrayStream);
			arrayStream.flush();
			
			//transfer the stream to byte array
			byte[] bytes = arrayStream.toByteArray();
			arrayStream.close();
			//transfer the byte array to String
			respond_body = new String(bytes, "ISO-8859-1");
			
			
			
		} else {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					fullPath)));

			String line = "";
			while ((line = br.readLine()) != null) {
				respond_body += line + "\r\n";
			}
			br.close();

		}
		
		String respond = respond_status + respond_header + respond_body;
		return respond;

	}

	public void buildResponse(int response_code) {
		switch (response_code) {
		case 200:
			respond_status = "HTTP/1.0 200 OK\r\n";
			break;
		case 404:
			respond_status = "HTTP/1.0 404 Not Found\r\n";
			break;
		case 500:
			respond_status = "HTTP/1.0 500 Not Found\r\n";
			break;
		case 501:
			respond_status = "HTTP/1.0 501 Not implemented\r\n";
			break;
		case 505:
			respond_status = "HTTP/1.0 505 HTTP Version not supported\r\n";
			break;
		}
	}
}

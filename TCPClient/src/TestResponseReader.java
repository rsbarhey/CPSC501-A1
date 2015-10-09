import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TestResponseReader {
	
	@Test
	public void testHttp200() {
		try {
			String httpReq = "GET /~mghaderi/test/test.html HTTP/1.1 \r\n"
					+ "Host: people.ucalgary.ca:80 \r\n";
			Socket socket = new Socket("people.ucalgary.ca", 80);
			PrintWriter out = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
			out.println(httpReq+"\r\n");
			out.flush();
			ResponseReader responseReader = new ResponseReader(socket.getInputStream());
			
			assertEquals(200, responseReader.GetStatusCode());
			socket.shutdownOutput();
			socket.shutdownInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHttp304()
	{
		try {
			Socket socket = new Socket("static.ucalgary.ca", 80);
			PrintWriter out = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
			out.println("GET /2013-001/980/global/images/identity/vertical-crest.png HTTP/1.1\r\n"
					+ "host: static.ucalgary.ca\r\n"
					+ "If-Modified-Since: Fri, 02 Oct 2015 20:00:00 GMT\r\n\r\n");
			out.flush();
			ResponseReader responseReader = new ResponseReader(socket.getInputStream());
			
			assertEquals(304, responseReader.GetStatusCode());
			socket.shutdownOutput();
			socket.shutdownInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testHttp400()
	{
		try {
			Socket socket = new Socket("static.ucalgary.ca", 80);
			PrintWriter out = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
			out.println("GET + /2013-001/980/global/images/identity/vertical-crest.png HTTP/1.1\r\n"
					+ "host: static.ucalgary.ca\r\n"
					+ "If-Modified-Since: Fri, 02 Oct 2015 20:00:00 GMT\r\n\r\n");
			out.flush();
			ResponseReader responseReader = new ResponseReader(socket.getInputStream());
			
			assertEquals(400, responseReader.GetStatusCode());
			socket.shutdownOutput();
			socket.shutdownInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLastModified()
	{
		try {
			Socket socket = new Socket("static.ucalgary.ca", 80);
			PrintWriter out = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
			out.println("GET /2013-001/980/global/images/identity/vertical-crest.png HTTP/1.1\r\n"
					+ "host: static.ucalgary.ca\r\n\r\n");
			out.flush();
			ResponseReader responseReader = new ResponseReader(socket.getInputStream());
			
			assertEquals("Fri, 30 Aug 2013 18:43:48 GMT", responseReader.GetLastModified());
			socket.shutdownOutput();
			socket.shutdownInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

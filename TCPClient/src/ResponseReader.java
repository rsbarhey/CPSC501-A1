import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ragheb Barheyan
 * @version 1.0, Oct 09, 2015
 *
 */
public class ResponseReader 
{
	private int dataIndex;
	private int statusCode;
	private int contentLength;
	private String lastModified;
	byte[] bytesToWrite;
	List<Integer> byteList = new ArrayList<>();
	
	/**
	 * Takes an InputStream and parse the bytes provided that it's HTTP response
	 * @param in The socket input stream
	 */
	public ResponseReader(InputStream in)
	{
		try {
			int byteInt = 0;
			//read until no bytes left
			while((byteInt = in.read()) != -1)
			{
				byteList.add(byteInt);
			}
			//responseBody is an arry of byteList
			Integer[] responseBody = byteList.toArray(new Integer[0]);
			//Gets the index where the file data starts
			parseDataIndex(responseBody);
			//Gets status code content length and last modified
			parseHeaders(responseBody);
			
			bytesToWrite = new byte[contentLength];
			//Copy Integer array into byte array
			setBytesToWrite(responseBody);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return the status code of the http request
	 */
	public int GetStatusCode()
	{
		return statusCode;
	}
	
	/**
	 * 
	 * @return the time in which the file was last modified in the server 
	 */
	public String GetLastModified()
	{
		return lastModified;
	}
	
	public byte[] GetBytesToWrite()
	{
		return bytesToWrite;
	}
	
	/**
	 * take an Array of Integer and and gets where the file data starts in the body
	 * @param bytes The array to parse
	 */
	private void parseDataIndex(Integer[] bytes)
	{
		int i = 0;
		while(i<bytes.length && i+1 < bytes.length &&
				i+2 <bytes.length && i + 3 <bytes.length)
		{
			//if a sequence of "\r\n\r\n" then what's after is the file data index
			if(Character.toChars(bytes[i])[0]== '\r' && 
					Character.toChars(bytes[i+1])[0] == '\n' &&
						Character.toChars(bytes[i+2])[0]== '\r' && 
							Character.toChars(bytes[i+3])[0] == '\n')
			{
				break;
			}
			i++;
		}
		dataIndex = i + 4;
	}
	
	/**
	 * Take an array of Integer and gets Status code, content length, and last modified
	 * @param bytes The array to parse
	 */
	private void parseHeaders(Integer[] bytes)
	{
		String tmpHeader = "";
		for(int i = 0; i < bytes.length; i++)
		{
			//read line
			if(Character.toChars(bytes[i])[0] != '\n')
			{
				tmpHeader += Character.toChars(bytes[i])[0];
			}
			if(Character.toChars(bytes[i])[0] == '\r')
			{
				//check what the header is
				if(tmpHeader.startsWith("HTTP"))
				{
					String[] parsedHeader = tmpHeader.split(" ");
					if(parsedHeader.length > 1)
					{
						String number = parsedHeader[1].replace("\r", "");	// remove the \r from parsed header
						statusCode = Integer.parseInt(number);
						tmpHeader = "";
					}
				}
				else if(tmpHeader.startsWith("Last-Modified"))
				{
					String[] parsedHeader = tmpHeader.split(" ", 2);
					if(parsedHeader.length > 1)
					{
						lastModified = parsedHeader[1].replace("\r", "");	// remove the \r\n from parsed header
						tmpHeader = "";
					}
				}
				else if(tmpHeader.startsWith("Content-Length"))
				{
					String[] parsedHeader = tmpHeader.split(" ");
					if(parsedHeader.length > 1)
					{
						String number = parsedHeader[1].replace("\r", "");	// remove the \r from parsed header
						contentLength = Integer.parseInt(number);
						tmpHeader = "";
					}
				}
				else
				{
					tmpHeader = "";
				}
			}
		}
	}
	
	/**
	 * Copies an Integer array into byte array
	 * @param bytes The Integer array to copy from
	 */
	private void setBytesToWrite(Integer[]bytes)
	{
		for(int i = 0; i < contentLength; i++)
		{
			int byteInt1 = bytes[i+dataIndex];
			bytesToWrite[i] = (byte) byteInt1;
		}
	}
}

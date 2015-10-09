import java.io.IOException;
import java.io.InputStream;

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
	byte[] responseBody;
	byte[] bytesToWrite;
	
	public ResponseReader(InputStream in)
	{
		responseBody = new byte[10 * 2048];
		try {
			in.read(responseBody);
			dataIndex = setDataIndex(responseBody);
			contentLength = setContentLength(responseBody);
			statusCode = setStatusCode(responseBody);
			lastModified = setLastModified(responseBody);
			
			bytesToWrite = new byte[contentLength];
			setBytesToWrite();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return the data index in which the bytes of the file starts
	 */
	public int GetDataIndex()
	{
		return dataIndex;
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
	 * @return the content length of the file in the body of the http reponse
	 */
	public int GetContentLength()
	{
		return contentLength;
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
	
	private int setDataIndex(byte[] bytes)
	{
		int i = 0;
		while(i<bytes.length && i+1 < bytes.length &&
				i+2 <bytes.length && i + 3 <bytes.length)
		{
			if(Character.toChars(bytes[i])[0]== '\r' && 
					Character.toChars(bytes[i+1])[0] == '\n' &&
						Character.toChars(bytes[i+2])[0]== '\r' && 
							Character.toChars(bytes[i+3])[0] == '\n')
			{
				break;
			}
			i++;
		}
		return i + 4;
	}
	
	private int setContentLength(byte[] bytes)
	{
		int contentLength = 0;
		String tmpHeader = "";
		for(int i = 0; i < bytes.length; i++)
		{
			if(Character.toChars(bytes[i])[0] != '\n')
			{
				tmpHeader += Character.toChars(bytes[i])[0];
			}
			if(Character.toChars(bytes[i])[0] == '\r')
			{
				if(tmpHeader.startsWith("Content-Length"))
				{
					String[] parsedHeader = tmpHeader.split(" ");
					if(parsedHeader.length > 1)
					{
						String number = parsedHeader[1].replace("\r", "");	// remove the \r from parsed header
						contentLength = Integer.parseInt(number);
						break;
					}
				}
				else
				{
					tmpHeader = "";
				}
			}
		}
		return contentLength;
	}
	
	private int setStatusCode(byte[] bytes)
	{
		int statusCode = 0;
		String tmpHeader = "";
		for(int i = 0; i < bytes.length; i++)
		{
			if(Character.toChars(bytes[i])[0] != '\n')
			{
				tmpHeader += Character.toChars(bytes[i])[0];
			}
			if(Character.toChars(bytes[i])[0] == '\r')
			{
				if(tmpHeader.startsWith("HTTP"))
				{
					String[] parsedHeader = tmpHeader.split(" ");
					if(parsedHeader.length > 1)
					{
						String number = parsedHeader[1].replace("\r", "");	// remove the \r from parsed header
						statusCode = Integer.parseInt(number);
						break;
					}
				}
				else
				{
					tmpHeader = "";
				}
			}
		}
		return statusCode;
	}
	
	private void setBytesToWrite()
	{
		for(int i = 0; i < contentLength; i++)
		{
			bytesToWrite[i] = responseBody[i+dataIndex];
		}
	}
	
	private String setLastModified(byte[] bytes)
	{
		String lastModified = "";
		String tmpHeader = "";
		for(int i = 0; i < bytes.length; i++)
		{
			if(Character.toChars(bytes[i])[0] != '\n')
			{
				tmpHeader += Character.toChars(bytes[i])[0];
			}
			if(Character.toChars(bytes[i])[0] == '\r')
			{
				if(tmpHeader.startsWith("Last-Modified"))
				{
					String[] parsedHeader = tmpHeader.split(" ", 2);
					if(parsedHeader.length > 1)
					{
						lastModified = parsedHeader[1].replace("\r", "");	// remove the \r\n from parsed header
						break;
					}
				}
				else
				{
					tmpHeader = "";
				}
			}
		}
		return lastModified;
	}
}

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
	
	public ResponseReader(InputStream in)
	{
		try {
			int byteInt = 0;
			while((byteInt = in.read()) != -1)
			{
				byteList.add(byteInt);
			}
			Integer[] responseBody = byteList.toArray(new Integer[0]);
			setDataIndex(responseBody);
			setContentLength(responseBody);
			setStatusCode(responseBody);
			setLastModified(responseBody);
			
			bytesToWrite = new byte[contentLength];
			setBytesToWrite(responseBody);
			
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
	
	private void setDataIndex(Integer[] bytes)
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
		dataIndex = i + 4;
	}
	
	private void setContentLength(Integer[] bytes)
	{
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
	}
	
	private void setStatusCode(Integer[] bytes)
	{
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
	}
	
	private void setBytesToWrite(Integer[]bytes)
	{
		for(int i = 0; i < contentLength; i++)
		{
			int byteInt1 = bytes[i+dataIndex];
			bytesToWrite[i] = (byte) byteInt1;
		}
	}
	
	private void setLastModified(Integer[] bytes)
	{
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
	}
}

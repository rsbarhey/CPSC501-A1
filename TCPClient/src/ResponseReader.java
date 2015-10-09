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
	
	public ResponseReader(InputStream in)
	{
		
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
}

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.net.*;
import java.io.*;
/**
 * UrlCache Class
 * 
 * @author 	Majid Ghaderi
 * @version	1.0, Sep 22, 2015
 *
 */
public class UrlCache {
	final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	private DateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.ENGLISH);
	private HashMap <String, Date> cacheHashMap = new HashMap<String, Date>();
    /**
     * Default constructor to initialize data structures used for caching/etc
	 * If the cache already exists then load it. If any errors then throw exception.
	 *
     * @throws UrlCacheException if encounters any errors/exceptions
     */
	public UrlCache() throws UrlCacheException
	{
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		File catalog = new File("catalog.txt");
		if(catalog.exists())
		{
			try 
			{
				BufferedReader reader = new BufferedReader(new FileReader(catalog));
				String line = "";
				while((line = reader.readLine()) != null)
				{
					String[] parsedLine = line.split("\t");
					if(parsedLine.length > 1)
					{
						try {
							Date lastModified = dateFormatter.parse(parsedLine[1]);
							cacheHashMap.put(parsedLine[0], lastModified);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    /**
     * Downloads the object specified by the parameter url if the local copy is out of date.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws UrlCacheException if encounters any errors/exceptions
     */
	public void getObject(String url) throws UrlCacheException
	{
		try
		{
			UrlParser urlParser = new UrlParser(url);
			Socket socket = new Socket(urlParser.GetHostName(), urlParser.GetPort());
			PrintWriter out = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
			String httpReq = "GET /" + urlParser.GetPath() + " HTTP/1.1\r\n" +
					 "Host: " + urlParser.GetHostName() +":"+ Integer.toString(urlParser.GetPort()) +"\r\n";
			if(cacheHashMap.containsKey(urlParser.GetPath()))
			{
				Date lastModified = cacheHashMap.get(urlParser.GetPath());
				lastModified.setTime(lastModified.getTime());
				httpReq += "If-Modified-Since: " + dateFormatter.format(lastModified) + "\r\n";
			}
			httpReq += "\r\n\r\n";
			out.println(httpReq);
			out.flush();
			
			ResponseReader responseReader = new ResponseReader(socket.getInputStream());
			int dataIndex = responseReader.GetDataIndex();
			int contentLength = responseReader.GetContentLength();
			int statusCode = responseReader.GetStatusCode();
			
			if(statusCode == 200)
			{
				File file = new File(System.getProperty("user.dir") + "/cache/" + urlParser.GetPath());
				file.getParentFile().mkdirs();
				file.createNewFile();
			
				FileOutputStream write = new FileOutputStream(file);
				byte[] bytesToWrite = responseReader.GetBytesToWrite();
				write.write(bytesToWrite);
				
				String lastModified = responseReader.GetLastModified();
				try
				{
					Date date = dateFormatter.parse(lastModified);
					file.setLastModified(date.getTime());
					PrintWriter writer = new PrintWriter(new FileWriter("catalog.txt", true));
					if(!cacheHashMap.containsKey(urlParser.GetPath()))
					{
						cacheHashMap.put(urlParser.GetPath(), date);
						writer.println(urlParser.GetPath() + "\t" + dateFormatter.format(date));
					}
					writer.close();
				} 
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				
			}
		}
		catch(UnknownHostException e)
		{
			System.out.println("Unknown host: " + e.getMessage());
		}
		catch(IOException e)
		{
			System.out.println("IOException: " + e.getMessage());
		}
		catch(InvalidUrlException e)
		{
			System.out.println("InvalidUrlException: " + e.getMessage());
		}
	}
	
    /**
     * Returns the Last-Modified time associated with the object specified by the parameter url.
	 *
     * @param url 	URL of the object 
	 * @return the Last-Modified time in millisecond as in Date.getTime()
     * @throws UrlCacheException if the specified url is not in the cache, or there are other errors/exceptions
     */
	public long getLastModified(String url) throws UrlCacheException
	{
		UrlParser urlParser;
		try {
			urlParser = new UrlParser(url);
			if(cacheHashMap.containsKey(urlParser.GetPath()))
			{
				return cacheHashMap.get(urlParser.GetPath()).getTime();
			}
			else
			{
				throw new UrlCacheException();
			}
		} catch (InvalidUrlException e) {
			e.printStackTrace();
		}
		return -1;	
	}
}

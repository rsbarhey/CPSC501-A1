

public class UrlParser
{
	private String hostName = "";
	private String path = "";
	private int port = 80;
	
	public UrlParser(String url) throws InvalidUrlException
	{
		if(url.isEmpty())
		{
			throw new InvalidUrlException();
		}
		String [] parsedList = url.split("/", 2);
		hostName = parsedList[0];
		if(parsedList.length > 1)
		{
			path = parsedList[1];
		}
		if(hostName.contains(":"))
		{	
			try
			{
				port = Integer.parseInt(hostName.substring(hostName.indexOf(':')+ 1));
				hostName = hostName.substring(0, hostName.indexOf(':'));
			}
			
			catch (NumberFormatException e)
			{
				throw new InvalidUrlException();
			}
		}
	}
	
	public String GetHostName()
	{
		return hostName;
	}
	
	public String GetPath()
	{
		return path;
	}
	
	public int GetPort()
	{
		return port;
	}
}
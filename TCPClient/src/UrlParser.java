
/**
 * 
 * @author Ragheb Barheyan
 * @version 1.3, Oct 9, 2015
 */
public class UrlParser
{
	private String hostName = "";
	private String path = "";
	private int port = 80;

	/**
	 * This is constructor for UrlParser and it parses the passed string into
	 * Host Name, Path, and Port Number 
	 * @param url A string to be parsed
	 * @throws InvalidUrlException If url is empty or the port string cannot be converted into a number
	 */
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
	
	/**
	 * 
	 * @return the Host Name parsed by the constructor 
	 */
	public String GetHostName()
	{
		return hostName;
	}
	
	/**
	 * 
	 * @return the Path parsed by the constructor
	 */
	public String GetPath()
	{
		return path;
	}
	
	/**
	 * 
	 * @return the Port Number parsed by the constructor as int
	 */
	public int GetPort()
	{
		return port;
	}
}
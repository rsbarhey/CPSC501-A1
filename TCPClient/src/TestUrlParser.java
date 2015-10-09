import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUrlParser {

	@Test
	public void testValidUrl() throws InvalidUrlException 
	{
		//Testing with an empty path
		UrlParser urlParser = new UrlParser("people.ucalgary.ca:888");
		assertEquals("people.ucalgary.ca", urlParser.GetHostName());
		assertEquals("", urlParser.GetPath());
		assertEquals(888, urlParser.GetPort());
		
		//Testing with an with a full path, and without specifying a port number
		urlParser = new UrlParser("github.com/rsbarhey/");
		assertEquals("github.com", urlParser.GetHostName());
		assertTrue("rsbarhey/".equals(urlParser.GetPath()));
	}
	
	@Test (expected = InvalidUrlException.class)
	public void testEmptyUrl() throws InvalidUrlException
	{
		UrlParser urlParser = new UrlParser("");
	}
	
	@Test (expected = InvalidUrlException.class)
	public void testInvalidPortNumber() throws InvalidUrlException
	{
		UrlParser urlParser = new UrlParser("github.com:tmoe/rsbarhey/");
	}
}

import java.io.IOException;


public class Search
{
	public static void trySearch(String strInput)
	{
		System.out.println("Begin searching for [" + strInput + "]");
		long startTime = System.currentTimeMillis();
		try
		{
			new SearchProcess().search(strInput);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Time:" + (endTime - startTime) * 1.0 / 1000 + "s");
		System.out.println("Done Searching!");
		System.out.println(" ");
	}
}

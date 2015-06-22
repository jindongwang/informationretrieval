import java.io.IOException;
import java.util.Scanner;


public class Main
{
	public static void main(String[] args) throws IOException
	{
		showMenu();
		Scanner scan = new Scanner(System.in);
		while(scan.hasNext())
		{
			String strInput = scan.nextLine();
			processMenu(strInput);
		}
		scan.close();
	}
	public static void processMenu(String strInput) throws IOException
	{
		if (strInput.compareTo("-i") == 0)
		{
			Index.buildIndex();
		}
		else if (strInput.contains("-r"))
		{
			String strKey = strInput.replace("-r ", "");
			Search.trySearch(strKey);
		}
		else
		{
			System.out.println("wrong instruction!");
		}
	}
	public static void showMenu()
	{
		System.out.println("Information Retrieval");
		System.out.println("-i for build index [e.g:-i]");
		System.out.println("-r for search [e.g: -r you]");
		System.out.println(" ");
	}
}

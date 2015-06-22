import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileOperation
{
	private String strFileName;
	private String strDocID;
	private String strContent;
	private String strTitle;
	private String strSpeaker;

	public String getSpeaker()
	{
		return strSpeaker;
	}

	public void setSpeaker(String strSpeaker)
	{
		this.strSpeaker = strSpeaker;
	}

	public String getTitle()
	{
		return strTitle;
	}

	public void setTitle(String strTitle)
	{
		this.strTitle = strTitle;
	}

	public String getDocID()
	{
		return strDocID;
	}

	public void setDocID(String strDocID)
	{
		this.strDocID = strDocID;
	}

	public String getContent()
	{
		return strContent;
	}

	public void setContent(String strContent)
	{
		this.strContent = strContent;
	}
	
	public String getFileName()
	{
		return strFileName;
	}

	public void setFileName(String strFileName)
	{
		this.strFileName = strFileName;
	}
	
	public void addContent(String strContent)
	{
		String strC = this.getContent();
		strC += " " + strContent;
		this.setContent(strC);
	}
	public FileOperation()
	{
		
	}
	
	//¥¶¿ÌŒƒµµID”≥…‰
	public Map<String, Integer> handleDocID() throws IOException
	{
		Map<String, Integer> mapDocID = new TreeMap<String, Integer>();
		FileReader fr = new FileReader("./files/shakespeare-merchant.trec.1");
		FileReader fr2 = new FileReader("./files/shakespeare-merchant.trec.2");
		BufferedReader br = new BufferedReader(fr);
		String strLine = br.readLine();
		while(strLine != null)
		{
			if (strLine.contains("<DOCNO>"))
			{
				String strTemp = strLine.replace("<DOCNO>", "");
				strTemp = strTemp.replace("</DOCNO>", "");
				if (!mapDocID.containsKey(strTemp))
				{
					mapDocID.put(strTemp, mapDocID.size());
				}
			}
			strLine = br.readLine();
		}
		br = new BufferedReader(fr2);
		strLine = br.readLine();
		while(strLine != null)
		{
			if (strLine.contains("<DOCNO>"))
			{
				String strTemp = strLine.replace("<DOCNO>", "");
				strTemp = strTemp.replace("</DOCNO>", "");
				if (!mapDocID.containsKey(strTemp))
				{
					mapDocID.put(strTemp, mapDocID.size());
				}
			}
			strLine = br.readLine();
		}
		br.close();
		fr.close();
		fr2.close();
		return mapDocID;
	}
}

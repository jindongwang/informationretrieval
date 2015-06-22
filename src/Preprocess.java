import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Preprocess
{
	private int iTotalWord;
	private int iTotalTerm;
	private int iAvgLength;
	private int iTotalDoc;
	public int getiTotalWord()
	{
		return iTotalWord;
	}
	public void setiTotalWord(int iTotalWord)
	{
		this.iTotalWord = iTotalWord;
	}
	public int getiTotalTerm()
	{
		return iTotalTerm;
	}
	public void setiTotalTerm(int iTotalTerm)
	{
		this.iTotalTerm = iTotalTerm;
	}
	public int getiAvgLength()
	{
		return iAvgLength;
	}
	public void setiAvgLength(int iAvgLength)
	{
		this.iAvgLength = iAvgLength;
	}
	public int getiTotalDoc()
	{
		return iTotalDoc;
	}
	public void setiTotalDoc(int iTotalDoc)
	{
		this.iTotalDoc = iTotalDoc;
	}
	
	//构造函数，计数值初始化
	public Preprocess()
	{
		this.setiAvgLength(0);
		this.setiTotalDoc(0);
		this.setiTotalTerm(0);
		this.setiTotalWord(0);
	}
	
	//预处理文档ID，将其映射成数字方便操作
	public void processDocID() throws IOException
	{
		FileOperation fo = new FileOperation();
		Map<String, Integer> mapDoc = fo.handleDocID();
		FileWriter fw = new FileWriter("./files/docid.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		for (String key : mapDoc.keySet())
		{
			int iValue = mapDoc.get(key);		
			bw.write(String.format("%s %d\r\n", key,iValue));
		}
		bw.close();
		fw.close();
		this.setiTotalDoc(mapDoc.size());
	}
	
	//处理两个文件，做第一步的倒排记录表
	public void filePro() throws IOException
	{
		List<InverseTerm> listTerms = new ArrayList<InverseTerm>();
		Map<String, Integer> mapBig = new HashMap<String, Integer>();
		String[] strDocs = new String[]{"./files/shakespeare-merchant.trec.1","./files/shakespeare-merchant.trec.2"};
		for (String string : strDocs)
		{
			FileReader fr = new FileReader(string);
			BufferedReader br = new BufferedReader(fr);
			String strLine = br.readLine();
			String strDocID = "";
			while (strLine != null)
			{
				//System.out.println(strLine);
				if (strLine.contains("<DOCNO>"))
				{
					String strTemp = strLine.replace("<DOCNO>", "");
					strTemp = strTemp.replace("</DOCNO>", "");
					strDocID = getDocID(strTemp.trim());
					//System.out.println(strDocID);
				}
				else
				{
					String strTemp = "";
					if (strLine.contains("<title>"))
					{
						strTemp = strLine.replace("<title>", "");
						strTemp = strTemp.replace("</title>", "");
					}
					if (strLine.contains("<speaker>"))
					{
						strTemp = strLine.replace("<speaker>", "");
						strTemp = strTemp.replace("</speaker>", "");
					}
					strTemp = processLine(strTemp);
					processItem(strTemp, strDocID, listTerms, mapBig);
				}
				strLine = br.readLine();
			}
			br.close();
			fr.close();
		}
		Collections.sort(listTerms,new Comparator<InverseTerm>()	
		{
			public int compare(InverseTerm arg0,InverseTerm arg1)
			{
				return arg0.getTerm().compareTo(arg1.getTerm());
			}
		});
		this.writeToFile(listTerms);
		//System.out.println("Done!");
		this.printStats();
	}
	
	//处理读到的记录
	public void processItem(String strContent,String strDocID,List<InverseTerm> listTerms,Map<String, Integer> mapBig)
	{
		String[] strLines = strContent.split(" ");
		for (String item : strLines)
		{
			if (item == null || item.trim().length() == 0)
			{
				continue;
			}
			if(!mapBig.containsKey(item.toLowerCase()))
			{
				mapBig.put(item.toLowerCase(), mapBig.size());
				InverseTerm it = new InverseTerm();
				it.addDocIDToList(strDocID);
				it.setTerm(item.toLowerCase());
				it.setTermID(mapBig.size() - 1);
				it.addToList(Integer.parseInt(strDocID));
				listTerms.add(it);
				this.iTotalTerm++;
			}
			else
			{
				int iValue = mapBig.get(item.toLowerCase());
				listTerms.get(iValue).addDocIDToList(strDocID);
				listTerms.get(iValue).addToList(Integer.parseInt(strDocID));
			}
			this.iTotalWord++;
		}
	}
	
	//处理一行，去标点等使之格式化
	public String processLine(String strTerm)
	{
		char strs[] = strTerm.toCharArray();
		int j = 0;
		for(int i = 0;i < strTerm.length();i++)
		{
			if (strTerm.charAt(i) >= 'A' && strTerm.charAt(i) <= 'Z' || strTerm.charAt(i) >= 'a' && 				strTerm.charAt(i) <= 'z' || strTerm.charAt(i) >= '0' && strTerm.charAt(i) <= '9')
			{
				strs[j++] = strTerm.charAt(i);
				continue;
			}
			else
			{
				strs[j++] = ' ';
			}
		}
		String strTemp = new String(strs);
		return strTemp;
	}
	
	//获取文档ID
	public String getDocID(String strID) throws IOException
	{
		String iValue = "";
		FileReader fr = new FileReader("./files/docid.txt");
		BufferedReader br = new BufferedReader(fr);
		String strLine = br.readLine();
		while (strLine != null)
		{
			if (strLine.contains(strID))
			{
				iValue = strLine.replace(strID, "");
				break;
			}
			strLine = br.readLine();
		}
		br.close();
		fr.close();
		return iValue.trim();
	}
	
	//将倒排记录表写到文件中
	public void writeToFile(List<InverseTerm> listTerms) throws IOException
	{
		FileWriter fw = new FileWriter("./files/OriginalInverseTable.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		FileWriter fw2 = new FileWriter("./files/Dictionary.txt");
		BufferedWriter bw2 = new BufferedWriter(fw2);
		FileWriter fw3 = new FileWriter("./files/DocIDs.txt");
		BufferedWriter bw3 = new BufferedWriter(fw3);
		int iPointer = 0;
		int iInversePointer = 0;
		int iSize = listTerms.size();
		for(int i = 0;i < iSize;++i)
		{
			String strLine = String.format("%d", i);
			strLine += String.format(" %d", iPointer);
			bw2.write(listTerms.get(i).getTerm());
			iPointer += listTerms.get(i).getTerm().length();
			strLine += String.format(" %d", listTerms.get(i).getCount());
			strLine += String.format(" %d", iInversePointer);
			String strT = "";
			List<Integer> listIDs = new ArrayList<Integer>();
			for (String strItem : listTerms.get(i).getListDocID())
			{
				int iValue = Integer.parseInt(strItem);
				listIDs.add(iValue + 1);
				strT += String.format("%s ", iValue + 1);
			}
			strT = compressDocID(listIDs);
			iInversePointer += strT.length();
			bw3.write(strT);
			bw.write(strLine + "\r\n");
		}
		bw.close();
		fw.close();
		bw2.close();
		fw2.close();
		bw3.close();
		fw3.close();
	}
	
	//打印统计记录
	public void printStats()
	{
		System.out.println("Docs:" + this.iTotalDoc);
		System.out.println("Words:" + this.iTotalWord);
		System.out.println("Terms:" + this.iTotalTerm);
		System.out.println("Avg Doc Length:" + this.iTotalWord / this.iTotalDoc);
	}
	
	//压缩倒排记录表，返回压缩后的所有连接起来的字符串
	private String compressDocID(List<Integer> listIDs)
	{
		String str = GammaEncode.encode(listIDs.get(0) + 1);
		for(int i = 1;i < listIDs.size();i++)
		{
			str += GammaEncode.encode(listIDs.get(i) - listIDs.get(i - 1));
		}
		return str;
	}
	
}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class SearchProcess
{
	private static List<InverseNumber> mListPointers = null;   //保存各查询结果的静态表
	public SearchProcess() throws IOException
	{
		mListPointers = this.getAllWordPointer();
	}
	
	//处理查询，查询形式：
	//各关键词以空格分割，空格表示and：key1 key2 key3    
	public void search(String strInput) throws IOException
	{
		String strTemp = new Preprocess().processLine(strInput);
		strTemp = strTemp.trim();
		String[] strLines = strTemp.split(" ");
		strLines = this.processStopWord(strLines);
		List<List<Integer>> listBig = new ArrayList<List<Integer>>();
		for (String item : strLines)
		{
			if (item.trim().length() == 0)
			{
				continue;
			}
			listBig.add(this.searchKey(item));
		}
		List<Integer> listNum = this.mergeLists(listBig);
		this.remapDocID(listNum);
		return;
	}
	
	//将查询到的数字ID与原文档ID进行反映射并格式化输出结果
	public void remapDocID(List<Integer> mapBig) throws IOException
	{
		Map<Integer, String> mapDocID = new TreeMap<Integer, String>();
		List<String> listDoc = new ArrayList<String>();
		FileReader fr = new FileReader("./files/docid.txt");
		BufferedReader br = new BufferedReader(fr);
		String strLine = br.readLine();
		while(strLine != null)
		{
			String[] strLines = strLine.split(" ");
			mapDocID.put(Integer.parseInt(strLines[1]), strLines[0]);
			strLine = br.readLine();
		}
		br.close();
		fr.close();
		if (mapBig == null || mapBig.size() == 0)
		{
			System.out.println("No retrieval!");
			return;
		}
		for (Integer item : mapBig)
		{
			String strDoc = mapDocID.get(item);
			listDoc.add(strDoc);
		}
		if (listDoc.size() == 0)
		{
			System.out.println("No retrieval!");
			return;
		}
		else
		{
			System.out.println(listDoc.size() + " Docs retrieved:");
			for(int i = 0;i < listDoc.size();i++)
			{
				System.out.println(String.format("%4d", i+1) + ":" + listDoc.get(i));
			}
		}
	}
	
	//处理停用词
	private String[] processStopWord(String[] strLines) throws IOException
	{
		List<String> listStops = this.getStopWords();
		List<String> listWords = Arrays.asList(strLines);
		List<String> listStrs = this.diff(listWords, listStops);
		return listStrs.toArray(new String[0]);
	}
	
	//从文件中读取停用词到一个数组
	public List<String> getStopWords() throws IOException
	{
		List<String> listStops = new ArrayList<String>();
		FileReader fr = new FileReader("./Files/StopWords.txt");
		BufferedReader br = new BufferedReader(fr);
		String strLine = br.readLine();
		while (true)
		{
			strLine = br.readLine();
			if (strLine == null)
			{
				break;
			}
			listStops.add(strLine.trim());
		}
		br.close();
		fr.close();
		listStops.add(" ");
		return listStops;
	}
	
	//两个数组做差
	public List<String> diff(List<String> ls, List<String> ls2) 
	{   
		List<String> list = new ArrayList(Arrays.asList(new String[ls.size()]));   
        Collections.copy(list, ls);   
        list.removeAll(ls2);   
        if (list.size() == 0)
		{
			return ls;
		}
        return list;   
    }   

	//处理大表
	public List<Integer> mergeLists(List<List<Integer>> listBig)
	{
		Collections.sort(listBig, new Comparator<List<Integer>>()
		{
			public int compare(List<Integer> list1,List<Integer> list2)
			{
				return list1.size() - list2.size();
			}
		});
		if (listBig.size() == 1)
		{
			return listBig.get(0);
		}
		List<Integer> listNew = listBig.get(0);
		for (int i = 1; i < listBig.size(); i++)
		{
			listNew = mergeDocIDList(listBig.get(i),listNew);
		}
		return listNew;
	}
	
	//两个倒排记录表合并，返回合并后的倒排记录表
	public List<Integer> mergeDocIDList(List<Integer> list1,List<Integer> list2)
	{
		List<Integer> list = new ArrayList(Arrays.asList(new String[list1.size()]));   
        Collections.copy(list, list1);   
        list.removeAll(list2);   
        return list;   
	}
	
	//查找一个单词，返回其倒排记录表
	public List<Integer> searchKey(String strKey) throws IOException
	{
		InverseNumber in = this.binarySearch(strKey);
		if (in == null)
		{
			return null;
		}
		List<Integer> listDocID = this.getDocIDByIndex(in.getiDocID(), in.getiDocIDLength());
		List<Integer> listIDs = new ArrayList<Integer>();
		Integer iFirst = listDocID.get(0) - 1;
		listIDs.add(iFirst - 1);
		for(int i = 1;i < listDocID.size();i++)
		{
			listIDs.add(listDocID.get(i) + listIDs.get(i - 1));
		}
		return listIDs;
	}
	
	//二分查找一个词
	public InverseNumber binarySearch(String strKey) throws IOException
	{
		int low = 0;
		int high = mListPointers.size() - 1;
		while(low <= high)
		{
			int middle = (low + high) / 2;
			Integer middleIndex = mListPointers.get(middle).getiWordID();
			Integer iLength;
			if (middle + 1 >= mListPointers.size())
			{
				iLength = -1;
			}
			else
			{
				iLength = mListPointers.get(middle + 1).getiWordID() - mListPointers.get(middle).getiWordID();
			}
			String strWord = this.getWordByIndex(middleIndex,iLength);
			if (strWord.compareTo(strKey) == 0)
			{
				int iDocIDLength = middle + 1 >= mListPointers.size() ? -1 : (mListPointers.get(middle + 1).getiDocID() - mListPointers.get(middle).getiDocID());
				InverseNumber in = new InverseNumber(middle, mListPointers.get(middle).getiDocID(), mListPointers.get(middle).getiFrequency(), middleIndex,iDocIDLength);
				return in;
			}
			else if(strWord.compareTo(strKey) > 0)
			{
				high = middle - 1;
			}
			else
			{
			    low = middle + 1;	
			}
		}
		return null;
	}
	
	//获取所有词项的指针到一个list中
	public  List<InverseNumber> getAllWordPointer() throws IOException
	{
		FileReader fr = new FileReader("./files/OriginalInverseTable.txt");
		BufferedReader br = new BufferedReader(fr);
		List<InverseNumber> listPointers = new ArrayList<InverseNumber>();
		String strLine = br.readLine();
		while(strLine != null)
		{
			String[] strLines = strLine.split(" ");
			InverseNumber in = new InverseNumber(Integer.parseInt(strLines[0].trim()),Integer.parseInt(strLines[3].trim()),Integer.parseInt(strLines[2].trim()),Integer.parseInt(strLines[1].trim()));
			listPointers.add(in);
			strLine = br.readLine();
		}
		br.close();
		fr.close();
		return listPointers;
	}
	
	//根据起始位置和长度返回文件中这部分字符串，如果长度是-1表示读到最后
	public String getTermByPosition(int iIndex,int iLength,String strFileName) throws IOException
	{
		File f = new File(strFileName);
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		byte[] by = new byte[200];
		raf.seek(iIndex);
		if (iLength == -1)
		{
			raf.read(by);
			raf.close();
			String str = new String(by,"UTF-8");
			return str.trim();
		}
		int i = 0;
		int iSum = iIndex + iLength;
		while(iIndex < iSum)
		{
			by[i++] = raf.readByte();
			raf.seek(++iIndex);
		}
		raf.close();
		String str = new String(by,"UTF-8");
		return str.trim();
	}
	
	//根据起始位置和长度读一个单词的倒排记录表到一个list
	public List<Integer> getDocIDByIndex(int iIndex,int iLength) throws IOException
	{
		String strLine = this.getTermByPosition(iIndex, iLength, "./files/DocIDs.txt");
		List<Integer> listIDs = GammaEncode.decodeLine(strLine);
		return listIDs;
	}
	
	//根据起始位置和长度读字典里的单词
	public String getWordByIndex(int iIndex,int iLength) throws IOException
	{
		String strWord = this.getTermByPosition(iIndex, iLength, "./files/Dictionary.txt");
		return strWord;
	}
}

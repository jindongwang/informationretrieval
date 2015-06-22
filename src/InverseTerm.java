import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class InverseTerm
{
	private int iTermID;
	private String strTerm;
	private List<String> listDocID;
	private int iCount;
	private Map<Integer, Integer> mapCount;
	private int iPointer;
	private int iDF;

	private double dTFIDF;
	public int getiDF()
	{
		return iDF;
	}
	public void setiDF(int iDF)
	{
		this.iDF = iDF;
	}
	public double getdTFIDF()
	{
		return dTFIDF;
	}
	public void setdTFIDF(int N)
	{
		this.dTFIDF = getCount() * Math.log10(N * 1.0 / iDF);
	}
	public int getPointer()
	{
		return iPointer;
	}
	public void setPointer(int iPointer)
	{
		this.iPointer = iPointer;
	}
	public Map<Integer, Integer> getMapCount()
	{
		return mapCount;
	}
	public void setMapCount(Map<Integer, Integer> mapCount)
	{
		this.mapCount = mapCount;
	}
	public int getCount()
	{
		return iCount;
	}
	public void setCount(int iCount)
	{
		this.iCount = iCount;
	}
	public int getTermID()
	{
		return iTermID;
	}
	public void setTermID(int iTermID)
	{
		this.iTermID = iTermID;
	}
	public String getTerm()
	{
		return strTerm;
	}
	public void setTerm(String strTerm)
	{
		this.strTerm = strTerm;
	}
	public List<String> getListDocID()
	{
		return listDocID;
	}
	public void setListDocID(List<String> listDocID)
	{
		this.listDocID = listDocID;
	}
	
	public InverseTerm(int iTermID,String strTerm,List<String> listdocID)
	{
		this.setTermID(iTermID);
		this.setTerm(strTerm);
		this.setListDocID(listdocID);
	}
	public InverseTerm()
	{
		this.setCount(0);
		this.mapCount = new TreeMap<Integer, Integer>();
		this.setListDocID(new ArrayList<String>());
	}
	public void addDocIDToList(String strDocID)
	{
		this.setCount(this.getCount() + 1);
		List<String> list = this.getListDocID();
		if (list.contains(strDocID))
		{
			return;
		}
		list.add(strDocID);
		this.setListDocID(list);
	}
	
	//将一个DocID加入一个词项记录
	public void addToList(int iDocID)
	{
		if (mapCount.containsKey(iDocID))
		{
			int iCount = mapCount.get(iDocID);
			mapCount.put(iDocID, ++iCount);
		}
		else
		{
			mapCount.put(iDocID, 1);
		}
	}
}

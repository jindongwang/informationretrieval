
public class InverseNumber
{
	private Integer iTermID;
	private Integer iDocID;
	private Integer iFrequency;
	private Integer iWordID;
	private Integer iDocIDLength;
	public Integer getiWordID()
	{
		return iWordID;
	}
	public void setiWordID(Integer iWordID)
	{
		this.iWordID = iWordID;
	}
	public Integer getiTermID()
	{
		return iTermID;
	}
	public void setiTermID(Integer iTermID)
	{
		this.iTermID = iTermID;
	}
	public Integer getiDocID()
	{
		return iDocID;
	}
	public void setiDocID(Integer iDocID)
	{
		this.iDocID = iDocID;
	}
	public Integer getiFrequency()
	{
		return iFrequency;
	}
	public void setiFrequency(Integer iFrequency)
	{
		this.iFrequency = iFrequency;
	}
	public InverseNumber(Integer iTermID,Integer iDocID,Integer iFrequency,Integer iWordID)
	{
		this.setiDocID(iDocID);
		this.setiFrequency(iFrequency);
		this.setiTermID(iTermID);
		this.setiWordID(iWordID);
	}
	public InverseNumber(Integer iTermID,Integer iDocID,Integer iFrequency,Integer iWordID,Integer iDocIDLength)
	{
		this.setiDocID(iDocID);
		this.setiFrequency(iFrequency);
		this.setiTermID(iTermID);
		this.setiWordID(iWordID);
		this.setiDocIDLength(iDocIDLength);
	}
	public Integer getiDocIDLength()
	{
		return iDocIDLength;
	}
	public void setiDocIDLength(Integer iDocIDLength)
	{
		this.iDocIDLength = iDocIDLength;
	}
}

import java.io.IOException;


public class Index
{
	public static void buildIndex() throws IOException
	{
		//1.文档ID预处理
		//2.建立倒排记录表
		long startTime = System.currentTimeMillis();
		System.out.println("Begin building inverted index for Shakespare Corpse...");
		System.out.println("Phase 1:Document ID preprocess...");
		Preprocess pp = new Preprocess();
		pp.processDocID();
		System.out.println("Phase 1 Done!");
		System.out.println("Phase 2:Build inverted index...");
		pp.filePro();
		System.out.println("Phase 2 Done!");
		long endTime = System.currentTimeMillis();
		System.out.println("Time:" + (endTime - startTime) * 1.0 / 1000 + "s");
		System.out.println("Done indexing!");
		System.out.println(" ");
	}
}

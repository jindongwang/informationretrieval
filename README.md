#信息检索建立索引并进行简单的查询
以莎士比亚《威尼斯商人》剧本为语料库，完成建立索引与查询两个功能。
系统语言为Java，在eclipse Luna版本上运行成功。
##系统结构
![系统结构](http://cl.ly/image/1G3p1s0X1T1F/Image%202015-06-22%20at%204.26.18%20%E4%B8%8B%E5%8D%88.png)

##运行界面
![运行界面](http://f.cl.ly/items/201I0N1b0Z2C0S2g2y3n/捕获14.PNG)

##建立索引
![建立索引](http://cl.ly/image/1O1U3X3Y1U3p/%E6%8D%95%E8%8E%B711.PNG)

##查询
![查询](http://cl.ly/image/2T1e0z3j0K2T/%E6%8D%95%E8%8E%B712.PNG)

![查询](http://cl.ly/image/2u1W1X0z141L/%E6%8D%95%E8%8E%B713.PNG)

##关键函数说明
要描述针对自由文本查询的数据传递路径，在图中，文档不断从左边流入到分析和语言学处理模块（语种识别、格式识别、词条化及词干还原等等），处理所输出的结果词条序列又会输送给后续模块：所有词条输入则到一系列索引器中，从而产生一系列索引结构，包括能够存储文档元数据的域索引及字段索引。自由文本查询（图上部中间）输送给索引器，并通过一个模块产生拼写校正后的候选查询。由于这一题是简单的布尔查询，索引直接将匹配的文档返回给结果页面，呈现给用户。

* public class Preprocess：预处理和构建倒排记录表
该类主要用于文档ID预处理和建立倒排记录表。
* 函数public void processDocID() throws IOException：预处理文档ID，将其映射成数字方便操作；
* 函数public String processLine(String strTerm)：处理一行，去标点等使之格式化；
* 函数public String getDocID(String strID) throws IOException：获取文档ID；
* 函数public void writeToFile(List<InverseTerm> listTerms) throws IOException：将倒排记录表写到文件中；
* 函数private String compressDocID(List<Integer> listIDs)：压缩倒排记录表，返回压缩后的所有连接起来的字符串，调用压缩类。
* public class GammaEncode：γ压缩
* public class InverseTerm：插入DocID
* 函数public void addToList(int iDocID)：将一个DocID加入一个词项记录
* public class SearchProcess：查询，验证索引器正确性
* 函数public void search(String strInput)：处理查询，查询形式，各关键词以空格分割，空格表示and：key1 key2 key3；
* 函数public void remapDocID(List<Integer> mapBig)：将查询到的数字ID与原文档ID进行反映射并格式化输出结果；
* 函数private String[] processStopWord(String[] strLines)：处理停用词；
* 函数public List<Integer> mergeLists(List<List<Integer>> listBig)：处理大表；
* 函数public List<Integer> mergeDocIDList(List<Integer> list1,List<Integer> list2)：两个倒排记录表合并，返回合并后的倒排记录表；
* 函数public List<Integer> searchKey(String strKey)：查找一个单词，返回其倒排记录表；
* 函数public InverseNumber binarySearch(String strKey)：二分查找一个词；
* 函数public  List<InverseNumber> getAllWordPointer()：获取所有词项的指针到一个list中；
* 函数public String getTermByPosition(int iIndex,int iLength,String strFileName)：根据起始位置和长度返回文件中这部分字符串，如果长度是-1表示读到最后；
* 函数public List<Integer> getDocIDByIndex(int iIndex,int iLength)：根据起始位置和长度读一个单词的倒排记录表到一个list；
* 函数public String getWordByIndex(int iIndex,int iLength)：根据起始位置和长度读字典里的单词；

##索引器实现方案
![索引器构造](http://cl.ly/image/1J3Z1z1s0A1b/0B8F867E-8130-46FF-81F2-025BC96D0A0F.jpeg)

###预处理
1. 收集需要建立索引的文档，如：Friends, Romans, Countrymen; So let it be with Caesar…
2. 将每篇文档转换成一个个词条（token），这个过程称为词条化。
3. 进行语言学预处理，产生归一化的词条来作为词项。

###排序
索引的构建相当于从正排表到倒排表的建立过程。当我们分析完文本语料后时当索引建立完成后 ,应得到倒排表 ,具体流程如图所示：
对每篇文档建立索引时的输入就是一个归一化的词条表，也可以看成二元组（词项，文档ID）的一个列表。对所有文档按照其中出现的词项来建立倒排索引，索引中包括一部词典和一个全体倒排记录表。每篇文档的所有词项加上文档ID，通过按照字母顺序排序，然后，同一词项进行合并。
###合并
将词项和文档ID 分开。词项存储在词典中，每个词项有一个指针指向倒排记录表。词典中往往还会存储一些其他的概要信息，如这里所存储的每个词项的文档频率。这个信息可以用于提高查询执行时的时间效率，也会应用于后面要讨论的结果排序中的权重计算方法。每个倒排记录表存储了词项出现的文档列表，也可以存储一些其他信息，比如词项频率（term frequency，即词项在文档中出现的次数）和词项在文档中出现的位置
###压缩并存入磁盘
在最终得到的倒排索引中，词典和倒排记录表都有存储开销。前者往往放在内存中，而后者由于规模大得多，通常放在磁盘上。因此，两部分的大小都非常重要。
进行压缩的一个优点显而易见：它能够节省磁盘空间。在本章我们即将看到，要达到1∶4的压缩比是非常容易的，也就是说可以降低75%的索引存储开销。
索引压缩还有两个隐含的优点。第一是能增加高速缓存（caching）技术的利用率。在搜索系统中，词典中某些条目及其索引往往比其他条目及其索引的使用更频繁。第二个隐含的优点是，压缩能够加快数据从磁盘到内存的传输速度。高效的解压缩算法在现代硬件上运行相当快，这样将压缩的数据块传输到内存并解压所需要的总时间往往会比将未压缩的数据块传输到内存要快。

	* 词典压缩：采用类似单一字符串方式的压缩
	* 倒排记录表压缩
	
基于位的编码能够在更细的位粒度上进行编码长度的自适应调整。最简单的位编码是一元编码（unary code）。数n 的一元编码为n 个1 后面加个0 组成的字符串。很显然，这种编码的效率不高，但是它会在后面用到。一个和最优编码长度差距在常数倍之内的方法是γ 编码。γ 编码将间距G表示成长度（length）和偏移（offset）两个部分进行变长编码。G的偏移实际上是G的二进制编码，但是前端的1 被去掉。比如，对13（二进制为1101）进行编码，其偏移为101。G的长度指的是偏移的长度，并采用一元编码。通过减少索引的磁盘存储空间、增加高速缓存中的信息存放量和加快从磁盘到内存的数据传输速度，索引压缩能够大大提高索引的时空效率。

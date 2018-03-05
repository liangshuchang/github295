package cn.itcast.lucene.first;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Lucene的维护
 *   添加索引
 *   删除索引
 *   修改索引
 *   查询索引
 */
public class LuceneManager {

//	抽取indexWriter
	public IndexWriter getIndexWriter() throws Exception{
		IKAnalyzer analyzer = new  IKAnalyzer();
		//索引生成的位置
		Directory directory = FSDirectory.open(new File("D:\\index"));
		//创建索引
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		return  new IndexWriter(directory, config);
		
	}
	
	
	
/**
 *  删除索引 
 */
	
	@Test
	public void testDelAll() throws Exception {
		IndexWriter indexWriter = this.getIndexWriter();
		//删除所有
		indexWriter.deleteAll();
		//回收资源
		indexWriter.close();
		
	}
	
	@Test
	public void testDelOne() throws Exception {
		IndexWriter indexWriter = this.getIndexWriter();

//		Query query = new TermQuery(new Term("name", "lucene"));
//		//删除单个
//		indexWriter.deleteDocuments(query);
		
		//删除单个
		indexWriter.deleteDocuments(new Term("name","java"));
		
		//回收资源
		indexWriter.close();
		
	}
	
	
	/**
	 * 修改    （ 是先删除在修改）
	 */
	@Test
	public void testUpdate() throws Exception {
		IndexWriter indexWriter = this.getIndexWriter();
		
		Document doc = new Document();
		doc.add(new StringField("id", "19", Store.YES));
		doc.add(new TextField("name","大华",Store.YES));
		//修改
		indexWriter.updateDocument(new Term("name","lucene"),doc);
		
		//回收资源
		indexWriter.close();
	}
	
	/**
	 * 查询
	 */
	
	//创建indexSearcher
	
	public  IndexSearcher  getIndexSearcher() throws Exception{
//		 2. 创建Directory流对象,声明索引库位置
		Directory directory = FSDirectory.open(new File("D:\\index"));
//		 3. 创建索引读取对象IndexReader
		IndexReader reader = DirectoryReader.open(directory);
//		 4. 创建索引搜索对象IndexSearcher
		return  new IndexSearcher(reader);
		
	}
	
	
	public void  getQuery(IndexSearcher indexSearcher , Query query) throws Exception{
//		 5. 使用索引搜索对象，执行搜索，返回结果集TopDocs
		TopDocs topDocs = indexSearcher.search(query, 5);
		System.out.println("查询到的数据总条数是："+topDocs.totalHits);
		//获取结果集
		ScoreDoc[] docs = topDocs.scoreDocs;
//		 6. 解析结果集
		for (ScoreDoc scoreDoc : docs) {
			System.out.println(scoreDoc);
		
			int doc1 = scoreDoc.doc;
	
			Document doc2 = indexSearcher.doc(doc1);
			System.out.println("=============================");
			System.out.println("docID:" + doc1);
			System.out.println("bookId:" + doc2.get("id"));
			System.out.println("name:" + doc2.get("name"));
			System.out.println("price:" + doc2.get("price"));
			System.out.println("pic:" + doc2.get("pic"));
			
		}
		
//		 7. 释放资源
		this.getIndexSearcher().getIndexReader().close();
		
	}
	
	/**
	 * 普通的查询语句
	 */
	@Test
	public void testQuery() throws Exception {
		IndexSearcher indexSearcher = this.getIndexSearcher();
		
		Query query = new TermQuery(new Term("name","java"));
		
		this.getQuery(indexSearcher, query);
		
	}
	
	/**
	 * NumericRangeQuery，指定数字范围查询.
	 */
	@Test
	public void testNumeric() throws Exception {
		 IndexSearcher indexSearcher = this.getIndexSearcher();
		 
		 NumericRangeQuery<Float> rangeQuery = 
				 NumericRangeQuery.newFloatRange("price", 70f, 80f, true, true);
		 
		 this.getQuery(indexSearcher, rangeQuery);
		
	}
	
	/**
	 *  BooleanQuery，布尔查询，实现组合条件查询 
	 */
	@Test
	public void testBooleanQuery() throws Exception {
		 IndexSearcher indexSearcher = this.getIndexSearcher();
		
		 BooleanQuery query = new BooleanQuery();
		 
		 //条件一
		 Query query1 = new TermQuery(new Term("name","java"));
		 //条件二。。。
		 Query query2 = new TermQuery(new Term("name","单身狗"));
		 //可以去详细了解SHOULD，MUST，MUST_NOT的区别
		 query.add(query1, Occur.SHOULD);
		 query.add(query2, Occur.SHOULD);
		
		 this.getQuery(indexSearcher, query);
	}
	
	/**
	 * QueryParser
	 * 创建搜索解析器，第一个参数：默认Field域，第二个参数：分词器
	 */
	@Test
	public void testQueryParser() throws Exception {
		IndexSearcher indexSearcher = this.getIndexSearcher();
		
		QueryParser queryParser = new QueryParser("name", new IKAnalyzer());
		
		Query query = queryParser.parse("name:java");
		
		this.getQuery(indexSearcher, query);
		
	}
	
	/**
	 * 通过MultiFieldQueryParse对多个域查询。
	 */
	@Test
	public void testMultiFieldQueryParser() throws Exception {
		IndexSearcher indexSearcher = this.getIndexSearcher();
		//MultiFieldQueryParser
		
		String[] fields= {"name","desc"};
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
		
		Query  query = queryParser.parse("lucene");
		
		this.getQuery(indexSearcher, query);
	}
	
	
	
}

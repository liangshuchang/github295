package cn.itcast.lucene.first;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.itcast.lucene.dao.BookDao;
import cn.itcast.lucene.dao.BookDaoImpl;
import cn.itcast.lucene.pojo.Book;

/**
 *  入门程序
 *   创建索引
 *   查询索引 
 */
public class LuceneFirst {

	/**
	 *  创建索引 
	 */
	@Test
	public void testAdd() throws Exception {
		
		//获得文档
		BookDao bookDao = new BookDaoImpl();
		List<Book> bookList = bookDao.queryBookList();
		//分词
//		Analyzer analyzer = new StandardAnalyzer();
		IKAnalyzer analyzer = new  IKAnalyzer();
		//索引生成的位置
		Directory directory = FSDirectory.open(new File("D:\\index"));
		//创建索引
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		for (Book book : bookList) {
			//创建文档对象
			Document doc = new Document();
			//创建域对象，下面是各式的域对象
			//ID 分析     不分词     不索引（也可索引）  存储
//			Field idField = new TextField("id",String.valueOf(book.getId()),Store.YES);
			Field idField = new StoredField("id", book.getId());//   不分词     不索引  存储
//			Field idField = new StringField("id", book.getId().toString(), Store.YES);//不分词     也可索引       存储
			//图书名称       分析       分词      索引    存储
			Field nameField = new TextField("name",book.getName() ,Store.YES);
			//图书价格      分析         分词       索引      存储   数字类型的需要分词
//			Field priceField = new TextField("price",String.valueOf(book.getPrice()),Store.YES);
			Field priceField = new    FloatField("price", book.getPrice(), Store.YES);
			//图书图片     分析     不分词     不索引    存储
//			Field picField = new TextField("pic",book.getPic(),Store.YES);
			Field picField = new   StoredField("pic", book.getPic());
			//图书描述
			Field descField = new TextField("desc",book.getDesc(),Store.YES);
			//将域的值放到文档对象中
			doc.add(idField);
			doc.add(nameField);
			doc.add(priceField);
			doc.add(picField);
			doc.add(descField);
			//保存索引及文档到索引库中
			indexWriter.addDocument(doc);
			
		}
		//关闭资源
		indexWriter.close();
		
	}
	
	/**
	 *  搜索索引
	 */
	@Test
	public void testQuery() throws Exception {
		
//		 1. 创建Query搜索对象
		// 创建分词器
		StandardAnalyzer analyzer = new StandardAnalyzer();
		// 创建搜索解析器，第一个参数：默认Field域，第二个参数：分词器
		QueryParser queryParser = new QueryParser("desc",analyzer);
		// 创建搜索对象
		Query query = queryParser.parse("desc:java AND lucene");
//		 2. 创建Directory流对象,声明索引库位置
		Directory directory = FSDirectory.open(new File("D:\\index"));
//		 3. 创建索引读取对象IndexReader
		IndexReader reader = DirectoryReader.open(directory);
//		 4. 创建索引搜索对象IndexSearcher
		IndexSearcher searcher = new IndexSearcher(reader);
//		 5. 使用索引搜索对象，执行搜索，返回结果集TopDocs
		TopDocs topDocs = searcher.search(query, 5);
		System.out.println("查询到的数据总条数是："+topDocs.totalHits);
		//获取结果集
		ScoreDoc[] docs = topDocs.scoreDocs;
//		 6. 解析结果集
		for (ScoreDoc scoreDoc : docs) {
			System.out.println(scoreDoc);
		
			int doc1 = scoreDoc.doc;
	
			Document doc2 = searcher.doc(doc1);
			System.out.println("=============================");
			System.out.println("docID:" + doc1);
			System.out.println("bookId:" + doc2.get("id"));
			System.out.println("name:" + doc2.get("name"));
			System.out.println("price:" + doc2.get("price"));
			System.out.println("pic:" + doc2.get("pic"));
			
		}
		
//		 7. 释放资源
	
		reader.close();
	}
	
	
	/**
	 * 相关度的计算
	 */
	@Test
	public void testBook() throws Exception {
		
		//获得文档
		BookDao bookDao = new BookDaoImpl();
		List<Book> bookList = bookDao.queryBookList();
		//分词
//		Analyzer analyzer = new StandardAnalyzer();
		IKAnalyzer analyzer = new  IKAnalyzer();
		//索引生成的位置
		Directory directory = FSDirectory.open(new File("D:\\index"));
		//创建索引
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		for (Book book : bookList) {
			//创建文档对象
			Document doc = new Document();
			//创建域对象，下面是各式的域对象
			//ID 分析     不分词     不索引（也可索引）  存储
//			Field idField = new TextField("id",String.valueOf(book.getId()),Store.YES);
			Field idField = new StoredField("id", book.getId());//   不分词     不索引  存储
//			Field idField = new StringField("id", book.getId().toString(), Store.YES);//不分词     也可索引       存储
			//图书名称       分析       分词      索引    存储
			Field nameField = new TextField("name",book.getName() ,Store.YES);
			//图书价格      分析         分词       索引      存储   数字类型的需要分词
//			Field priceField = new TextField("price",String.valueOf(book.getPrice()),Store.YES);
			Field priceField = new    FloatField("price", book.getPrice(), Store.YES);
			//图书图片     分析     不分词     不索引    存储
//			Field picField = new TextField("pic",book.getPic(),Store.YES);
			Field picField = new   StoredField("pic", book.getPic());
			//图书描述
			Field descField = new TextField("desc",book.getDesc(),Store.YES);
			//我们进行相关度的计算
			if (book.getId()==2) {
//				id=5的人给钱了
				descField.setBoost(100f);
			}
			
			
			//将域的值放到文档对象中
			doc.add(idField);
			doc.add(nameField);
			doc.add(priceField);
			doc.add(picField);
			doc.add(descField);
			//保存索引及文档到索引库中
			indexWriter.addDocument(doc);
			
		}
		//关闭资源
		indexWriter.close();
		
	}
	
	
}

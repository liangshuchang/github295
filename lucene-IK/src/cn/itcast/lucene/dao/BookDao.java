package cn.itcast.lucene.dao;

import java.util.List;

import cn.itcast.lucene.pojo.Book;

public interface BookDao {

	/**
	 * 查询所有的数据
	 */
	List<Book> queryBookList();
	
}

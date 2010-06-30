package ch.ralscha.extdirectspring.demo.book;

import java.util.List;
import javax.inject.Named;
import ch.ralscha.extdirectspring.annotation.ExtDirectStoreReadMethod;
import com.google.common.collect.ImmutableList;

@Named
public class BookService {

  @ExtDirectStoreReadMethod(group = "book")
  public List<Book> getBooks() {
    return BOOKS;
  }
  
  private static final ImmutableList<Book> BOOKS;

  static {
    ImmutableList.Builder<Book> builder = new ImmutableList.Builder<Book>();

    Book book = new Book();
    book.setId(1);
    book.setTitle("Learning Ext JS");
    book.setPublisher("Packt Publishing");
    book.setISBN10("1847195148");
    book.setISBN13("978-1847195142");
    book.setLink("https://www.packtpub.com/learning-ext-js/book");
    String descr = "The book provides plenty of fun example code and screenshots to guide you through the creation ";
    descr += "of examples to assist with learning. By taking a chapter-by-chapter look at each ";
    descr += "major aspect of the Ext JS framework, the book lets you digest the available features in small, ";
    descr += "easily understandable, chunks, allowing you to start using the library for your ";
    descr += "development needs immediately.";
    book.setDescription(descr);
    builder.add(book);

    book = new Book();
    book.setId(2);
    book.setTitle("Ext JS 3.0 Cookbook");
    book.setPublisher("Packt Publishing");
    book.setISBN10("1847198708");
    book.setISBN13("978-1847198709");
    book.setLink("https://www.packtpub.com/ext-js-3-0-cookbook/book");
    descr = "The Ext JS Cookbook contains step-by-step instructions for Ext JS users to build desktop-style ";
    descr += "interfaces in their own web applications. The book is designed so that you can refer to ";
    descr += "it chapter by chapter, or you can look at the list of recipes and read them in no particular order.";
    book.setDescription(descr);
    builder.add(book);

    book = new Book();
    book.setId(3);
    book.setTitle("Ext JS in Action");
    book.setPublisher("Manning Publications");
    book.setISBN10("1935182110");
    book.setISBN13("978-1935182115");
    book.setLink("http://www.manning.com/garcia/");
    descr = "Ext JS in Action teaches the reader about Ext from the ground up. By following the common design patterns ";
    descr += "demonstrated in the Ext source and in many commercial applications, the  ";
    descr += "book teaches you to achieve the same results you see in world-class commercial JavaScript applications. ";
    book.setDescription(descr);
    builder.add(book);

    BOOKS = builder.build();
  }



}

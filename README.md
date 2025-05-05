## ExtDirectSpring 
![Test Status](https://github.com/ralscha/extdirectspring/workflows/test/badge.svg)

ExtDirectSpring is a library that connects Ext JS 3.x, 4.x, 5.x, 6.x, 7.x and Sencha Touch 2.x applications with a Java/Spring back end, by implementing the [Ext Direct](https://docs.sencha.com/extjs/7.9.0/guides/backend_connectors/direct/specification.html) specification. 
The library supports all the features of Ext Direct:
  * Configuration with annotations
  * Simple remote calls
  * Named parameters
  * Method batching
  * Form post
  * Form post with file upload
  * Polling

## Maven
ExtDirectSpring is available from the Central Maven Repository. 
```
    <dependency>
      <groupId>ch.ralscha</groupId>
      <artifactId>extdirectspring</artifactId>
      <version>....</version>
    </dependency>
```


## Support
If you have a question about extdirectspring post it in our [Google Group forum](https://groups.google.com/forum/#!forum/extdirectspring).
The official Sencha [Ext.Direct forum](http://www.sencha.com/forum/forumdisplay.php?47-Ext.Direct) is a good place to look for an answer if you have a more general Ext.Direct question.  


## Minimum Requirements
  * 2.1.1: Spring 6.2.6, Servlet 6.1.0, Jackson 2.18.3, Java 17 (Jakarta)
  * 1.8.1: Spring 5.1.2, Servlet 3.0, Jackson 2.9, Java 1.8
  * 1.7.4: Spring 4.3.1, Servlet 3.0, Jackson 2.8, Java 1.6
  * 1.6.1: Spring 4.2.0, Servlet 3.0, Jackson 2.6, Java 1.6
  * 1.5.2: Spring 4.1.2, Servlet 3.0, Jackson 2.5, Java 1.6
  * 1.4.3: Spring 4.0.0, Servlet 3.0, Java 1.6
  * 1.3.9: Spring 3.2.1, Servlet 2.4, Java 1.6
  * 1.2.3: Spring 3.1.2, Servlet 2.4, Java 1.6
  * 1.1.3: Spring 3.0.7, Servlet 2.4, Java 1.5

The library supports Ext JS 3, 4, 5, 6 and 7 and Sencha Touch 2   


## Demo applications using extdirectspring
  * [eds-starter6-simple-jpa](https://github.com/ralscha/eds-starter6-simple-jpa)
  * [eds-starter6-simple-mongodb](https://github.com/ralscha/eds-starter6-simple-mongodb)
  * [eds-starter6-jpa](https://github.com/ralscha/eds-starter6-jpa)
  * [eds-starter6-mongodb](https://github.com/ralscha/eds-starter6-mongodb)
  * [musicsearch](https://github.com/ralscha/musicsearch)
  * [mycustomer](https://github.com/ralscha/mycustomer)

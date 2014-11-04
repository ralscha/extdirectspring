##ExtDirectSpring 
[![Build Status](https://api.travis-ci.org/ralscha/extdirectspring.png)](http://travis-ci.org/ralscha/extdirectspring)

ExtDirectSpring is a library that connects Ext JS 3.x, Ext JS 4.x and Sencha Touch 2.x applications with a Java/Spring 3 back end, by implementing the [Ext Direct](http://www.sencha.com/products/extjs/extdirect/) specification. 
The library supports all the features of Ext Direct:
  * Configuration with annotations
  * Simple remote calls
  * Named parameters
  * Method batching
  * Form post
  * Form post with file upload
  * Polling

###See the library in action: https://demo.rasc.ch/eds/

##Maven
ExtDirectSpring is available from the Central Maven Repository. 
```
    <dependency>
      <groupId>ch.ralscha</groupId>
      <artifactId>extdirectspring</artifactId>
      <version>1.5.0</version>
    </dependency>
```

For a quick start there are archetypes available. 
See the [Maven Setup](https://github.com/ralscha/extdirectspring/wiki/Setup-Maven#archetypes) wiki page for more information.


##Support
If you have a question about extdirectspring post it in our [Google Group forum](https://groups.google.com/forum/#!forum/extdirectspring).
The official Sencha [Ext.Direct forum](http://www.sencha.com/forum/forumdisplay.php?47-Ext.Direct) is a good place to look for an answer if you have a more general Ext.Direct question.  


##Minimal Requirements
 * Spring 3.0.7+ (Version 1.1.3), Spring 3.1.2+ (Version 1.2.3), Spring 3.2.1+ (Version 1.3.1), Spring 4.0.0+ (Version 1.4.3), Spring 4.1.2+ (Version 1.5.0)
 * Servlet 2.4+ Container (Version <= 1.3.9), Servlet 3.0+ (Version 1.4.3, 1.5.0)
 * Java 1.5 (Version 1.1.3), Java 1.6 (Version 1.2.3, 1.3.1, 1.4.3, 1.5.0)
 * Ext JS 3+, Ext JS 4+, Ext JS 5+, Sencha Touch 2 
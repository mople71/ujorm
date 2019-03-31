<p align="center">
	<a href="https://ujorm.org"><img src="https://mople71.cz/img/ujorm.png" alt="Ujorm"></a>
</p>
<p align="center">
	<a href="https://ujorm.org/">Homesite</a> ·
 <strong>JavaDoc</strong>: <a href="https://ujorm.org/javadoc/org/ujorm/orm/package-summary.html">ujo-orm</a> & <a href="https://pponec.github.io/ujorm/javadoc/1.88/ujo-tools/">ujo-tools</a> ·
	<a href="https://ujorm-cs.blogspot.com/">Blog</a>
</p>

<br>

Ujorm Framework is a tiny Java library providing nontraditional objects based on key-value architecture that aims to open up new and exciting possibilities for writing efficient code. Ujorm offers a unique ORM module designed for Java development with great performance and a small footprint. Its key features are typesafe database queries, relation mapping by Java code, zero entity states and memory overload protection cache.

## Why yet another ORM mapping?
The Ujorm (original name was UJO Framework) is designed primarily for the rapid Java development based on a relation database.
* java compiler can discover a syntax error of Ujorm database query similar like 4GL database languages
* easy to configure the ORM model by java source code, optionally by annotations and a XML file
* great performance, some types of SELECT query are very fast in comparison to its competitors
* lazy loading or the one request data loading of relations are supported optionally as a fetch strategy
* database tables, columns and indexes can be optionally updated according to Java meta-model in the run-time
* no confusing proxy or binary modified business objects
* very lightweight framework with no library dependencies in the run-time

## Some other features

* batch SQL statements for more rows like INSERT, UPDATE and DELETE are supported
* features LIMIT and OFFSET are available from the API
* nested transactions are supported using the partially implemented JTA
* resources for ORM mapping can be a database table, view or native SQL SELECT
* subset of table columns on SELECT can be specified for the SQL statement
* JDBC query parameters are passed by a 'question mark' notation to the PreparedStatement for a high security
* stored database procedures and functions are supported
* all persistent objects are based on the interface OrmUjo, namely on the implementation OrmTable
* internal object cache is based on the WeakHashMap class so that large transactions does not cause any OutOfMemoryException
* database indexes are created by the meta-model, added support for unique, non-unique indexes including the composed one

## Maven Repository

 ORM module:

    <dependency>
        <groupId>org.ujorm</groupId>
        <artifactId>ujo-orm</artifactId>
        <version>${ujorm.version}</version>
    </dependency>

 Module for [Apache Wicket](http://wicket.apache.org/) integration:

    <dependency>
        <groupId>org.ujorm</groupId>
        <artifactId>ujo-wicket</artifactId>
        <version>${ujorm.version}</version>
    </dependency>

## Running Ujorm

Java 7+ is required for compiling, Java 6+ is required for runtime.
You can use one of these preferred tools to open the project:
  *  NetBeans IDE
  *  InteliJ IDEA
  *  Eclipse with a Maven plugin
  *  Maven toolkit (if you like working in terminal)






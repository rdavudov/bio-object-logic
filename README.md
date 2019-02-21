# Bio Objects
Bio Objects is a library which can be used as a replacement for **Java Beans** only by extends BioObject class. Bio Dictionary is built based on @annotations or xml configuration and stores each field as a tag.

## Features
- Easily customizable by adding new keys to map, without changing your code.
- Fast and efficient serialization/deserialization
- Easy XML mapping from/to XML file
- Easy JSON mapping from/to JSON file
- Additional operations on Map such as trim(), clone(), format(), fill() (see below for details)
- Immutability support
- **Fast Expression Language evaluation (10x faster than SpEL)**

Bio Objects are easy solution where Model objects are changed frequently and developers don't want to change main code each time. Also it contains some additional functionalities which are very helpful.

```java
@BioObj
public class Vehicle extends BioObject {
  @BioTag(type="Integer")
  public static final String YEAR_OF_PRODUCTION = "year_of_production" ;
  @BioTag(type="String")
  public static final String PRODUCER = "producer" ;
  @BioTag(type="Integer")
  public static final String ENGINE = "engine" ;
  @BioTag(type="Integer")
  public static final String CYLINDERS = "cylinders" ;
  @BioTag(type="Double")
  public static final String FUEL_EFFICIENCY = "fuel_efficiency" ;
}
```
Here we annotate a class ```Vehicle```  which extends ```BioObject``` by annotation ```@BioObj``` and also we annotate its fields which will be used in serialization, xml/json mapping, formatting etc. by annotation ```@BioTag```.

First of all we need to create dictionary which is done by following code. Bio Dictionary finds all annotated classes and fields and give them an id(or code). This code is being used during serialization instead of serializing String names. 
```java
 new BioDictionaryBuilder().build(); 
 ```
 also you can specify which package root to travers for Bio Objects.
 ```java
 new BioDictionaryBuilder().addPackage("com.linkedlogics.bio.test").build();
 ```
 
 After dictiontary is created you can instantiate your classes either by default constructor
 ```java
 Vehicle v = new Vehicle() ;
 ```
 or by dictionary factory methods
 ```java
 Vehicle v = BioDictionary.getDictionary().getFactory().newBioObject(Vehicle.class) ;
 ```
Difference is in that second method will use latest concrete class in the classpath which extends Vehicle and adds some more tags. If you use first method you will get an instance of exact ```Vehicle``` class. 

## Setters and Getters
Bio Object provides default set and casted get methods already. For example:
```java
v.set(Vehicle.PRODUCER, "Ford") ;
v.set(Vehicle.YEAR_OF_PRODUCTION, 2019) ;
v.set(Vehicle.FUEL_EFFICIENCY, 17.8) ;
v.set("undefined tag", "Hello world") ;

v.getString(Vehicle.PRODUCER) ;
v.getInt(Vehicle.YEAR_OF_PRODUCTION) ;
v.getDouble(Vehicle.FUEL_EFFICIENCY) ;
v.get("undefined tag") ;
```
or you can still write standard setter/getters as following:
```java
@BioObj
public class Vehicle extends BioObject {
  @BioTag(type="Integer")
  public static final String YEAR_OF_PRODUCTION = "year_of_production" ;
  @BioTag(type="String")
  public static final String PRODUCER = "producer" ;
  @BioTag(type="Integer")
  public static final String ENGINE = "engine" ;
  @BioTag(type="Integer")
  public static final String CYLINDERS = "cylinders" ;
  @BioTag(type="Double")
  public static final String FUEL_EFFICIENCY = "fuel_efficiency" ;
  
  public void setProducer(String producer) {
    set(PRODUCER, producer) ;
  }

  public String getProducer() {
    return getString(Vehicle.PRODUCER) ;
  }

  public void setYearOfProduction(int yearOfProduction) {
    set(YEAR_OF_PRODUCTION, yearOfProduction) ;
  }
  
  public in getYearOfProduction() {
    return getInt(Vehicle.YEAR_OF_PRODUCTION) ;
  }
}
```


## Serialization/Deserialization
In order to serialize/deserialize Bio Objects you need to use to ```BioObjectBinaryParser``` class. Here is an example:
```java
 BioObjectBinaryParser parser = new BioObjectBinaryParser() ;
 
 byte[] encoded = parser.encode(v) ;
 
 Vehicle decoded = parser.decode(encoded) ;
 ```
 This serialization/deserialization is way to fast than standard Java serialization because it only serializes data and field codes. Bio Object codes must be unique within one dictionary (you can have multiple dictionaries at the same time) and tag codes must be unique within single Bio Object. By default Bio Dictionary uses standard hashing to assign default codes but it is possible that there can be duplicates. For that purpose you can also assign custom codes inside annotations ```@BioObj``` and ```@BioTag``` for each object and tag. For example:
 ```java
@BioObj(code=1)
public class Vehicle extends BioObject {
	  @BioTag(code=1, type="Integer")
	  public static final String YEAR_OF_PRODUCTION = "year_of_production" ;
	  @BioTag(code=2, type="String")
	  public static final String PRODUCER = "producer" ;
	  @BioTag(code=3, type="Integer")
	  public static final String ENGINE = "engine" ;
	  @BioTag(code=4, type="Integer")
	  public static final String CYLINDERS = "cylinders" ;
}
```
also by adding dictionary code to @BioObj you can differentiate Bio Objects based on whole dictionary.
```java
@BioObj(code=1, dictionary=3)
public class Vehicle extends BioObject { ... }
```

## XML parsing and export
It is very easy to export Bio Object to xml and also parse it from XML. You don't have to do anything additional.
For exporting you can use ```toXml()``` method in BioObject which is inherited.
```java
System.out.println(v.toXml()) ;
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<vehicle type="Vehicle" code="1">
    <fuel-efficiency type="Double">17.8</fuel-efficiency>
    <producer type="String">Ford</producer>
    <undefined tag type="String">Hello world</undefined tag>
    <year-of-production type="Integer">2019</year-of-production>
</vehicle>
```
Each object tag starts with name field and followed by attributes about its type and code. 
- **name** is by default snake case of class name it is commonly used in Bio Expressions
- **type** is by default class name it is commonly used in Bio Dictionary for dependencies between Bio Objects
- **code** is by default hash generated or can be given inside annotation, it is used in serialization/deserialization

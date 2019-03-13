# Bio Objects
Bio Objects is a library which can be used as a replacement for **Java Beans**. Bio Objects are based on Maps (Keys-Values) and you will only need to extend ```BioObject``` class and add necessary annotations. Bio Dictionary will be built based on @annotations or xml configuration and will contain all information required for serialization, xml/json parsing etc. 

**Bio Objects** are very useful in conjunction with **Bio Expressions** where you can write dynamic decision making mechanisms for your application. Such as :
- if		```car.year_of_production > 2015 and car.engine * 2 < 6000``` 		do this
- else if 	```car.fuel_efficiency.checkEUStandard()``` 				do this
- else if 	```car.calculateHP(car.cylinders, car.engine) > 200``` 			do this
- else if 	```4 < car.cylinders <= 6 and car.fuel_efficiency < 15.4```		do this
- else if 	```?car.producer and car.producer = ['BMW', 'Toyota']``` 		do this

if ```car``` object contains a tag ```producer``` and it is equal to any of the elements of array.

Bio Expressions are created and used as following:
```java
BioExpression expr = BioExpression.parse("car.year_of_production > 2015 and car.engine * 2 < 6000") ;
boolean result = (Boolean) expr.getValue(car) ;
```
you can use multiple Bio Objects in your expression:
```java
BioExpression expr = BioExpression.parse("car.year_of_production > 2015 and driver.license.category = ['B','E','D']") ;
boolean result = expr.getBooleanValue(car, driver) ;
```
Another example:
```java
BioExpression expr = BioExpression.parse("car.calculateHP(car.cylinders, car.engine)") ;
double result = (Double) expr.getValue(car) ;
```

**Note that** parsed Bio Expressions are cached, so parsing multiple times will not affect your application performance. If you parse exactly same expression again, it will return already parsed one from cache and since they are stateless objects there will be impact on your application logic.

## Cool Features
- Easily customizable by adding new keys to map, without changing your code.
- Fast and efficient serialization/deserialization
- Binary compression and encryption after serialization
- Easy XML mapping from/to XML file
- Easy JSON mapping from/to JSON file
- Additional operations on Map such as trim(), clone(), format(), fill() (see below for details)
- Immutability support
- **Fast Expression Language evaluation (10x faster than SpEL)**

Bio Objects are easy solution where Model objects are changed frequently and developers don't want to change main code each time. Also it contains some additional functionalities which are very helpful. 

## Quick Start
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
 also you can specify package root for Bio Objects traversal.
 ```java
 new BioDictionaryBuilder().addPackage("com.linkedlogics.bio.test").build();
 ```
 
 After dictionary is created you can instantiate your objects either by default constructor
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


## Serialization and Deserialization
In order to serialize/deserialize Bio Objects you need to use to ```BioObjectBinaryParser``` class. Here is an example:
```java
 BioObjectBinaryParser parser = new BioObjectBinaryParser() ;
 
 byte[] encoded = parser.encode(v) ;
 
 Vehicle decoded = (Vehicle) parser.decode(encoded) ;
 ```
 This serialization/deserialization is way too fast than standard Java serialization because it only serializes keys and values. Bio Object codes must be unique within one dictionary (you can have multiple dictionaries at the same time) and tag codes must be unique within single Bio Object. By default Bio Dictionary uses standard hashing to assign default codes but it is possible that there can be duplicates. For that purpose you can also assign custom codes inside annotations ```@BioObj``` and ```@BioTag``` for each object and tag. For example:
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
    <undefined-tag type="String">Hello world</undefined-tag>
    <year-of-production type="Integer">2019</year-of-production>
</vehicle>
```
Each root tag in XML starts with name (vehicle) field and followed by attributes about its type (Vehicle) and code (1). 
- **name** is by default snake case of class name it is commonly used in Bio Expressions
- **type** is by default class name it is commonly used in Bio Dictionary for dependencies between Bio Objects
- **code** is by default hash generated or can be given inside annotation, it is used in serialization/deserialization

In order to parse XML to Bio Object you have to use BioObjectXmlParser class in below way.
```java
BioObjectXmlParser xmlParser = new BioObjectXmlParser() ;
Vehicle v = (Vehicle) xmlParser.parse(new FileInputStream("vehicle.xml")) ;
```
Another way of parsing from XML is to use a static method ```fromXml(String xml)``` inside ```BioObject``` class. For example:
```java
Vehicle v = (Vehicle) BioObject.fromXml(xml) ;
```

## JSON parsing and export
For exporting to JSON you should use ```toJson()``` method and for parsing from JSON you can use static  ```BioObject.fromJson(String json)``` method. ```toJson()``` method returns a ```org.json.JSONObject``` instance
```java
System.out.println(v.toJson().toString(4)) ;
```
```json
{
    "fuel_efficiency": 17.8,
    "undefined tag": "Hello world",
    "producer": "Ford",
    "year_of_production": 2019
}
```
## Additional methods from BioObject
### trim()
```trim()``` method removes all keys which are not found in the dictionary. For example:
```java
v.set(Vehicle.PRODUCER, "Ford") ;
v.set(Vehicle.YEAR_OF_PRODUCTION, 2019) ;
v.set(Vehicle.FUEL_EFFICIENCY, 17.8) ;
v.set("undefined tag", "Hello world") ;
```
```"undefined tag"``` tag will be removed when you call ```v.trim()```

### trimAll()
```trimAll()``` method removes all keys which are not found in the dictionary recursively also from inner Bio Objects. 

### trim(String key)
```trim(key)``` method removes all keys which are annotated with specific trim key. It is useful during serialization to file or for network to have smaller sized objects. But if you want to trim some tags for file and trim other tags for network, you can specify different trim keys for each tag as following:
```java
	  @BioTag(type="String", trimKeys="key1")
	  public static final String PRODUCER = "producer" ;
	  @BioTag(type="Integer", trimKeys= {"key2", "keys3"})
	  public static final String ENGINE = "engine" ;
```
Here ```PRODUCER``` will be removed when we call ```v.trim("key1")``` and ```ENGINE``` will be removed when we call ```v.trim("key2")```

### format()
```format()``` method converts inappropriate values to correct types based on dictionary. If a tag is Integer but inside Bio Object it is a String ("42") it will be converted to int 42. It is applied to all primitive types and arrays of primitive types. For example:
```java
v.set(Vehicle.PRODUCER, "Ford") ;
v.set(Vehicle.YEAR_OF_PRODUCTION, "2019") ;
v.set(Vehicle.FUEL_EFFICIENCY, "17.8") ;
v.set("undefined tag", "Hello world") ;
```

```java
v.format() ;
System.out.println(v.toXml());
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<vehicle type="Vehicle" code="1">
    <fuel-efficiency type="Double">17.8</fuel-efficiency>
    <producer type="String">Ford</producer>
    <undefined-tag type="String">Hello world</undefined-tag>
    <year-of-production type="Integer">2019</year-of-production>
</vehicle>
```

### clone() and equals()
```clone()``` method returns completely new instance but with same tag values. ```equals()``` is comparing all tag values for equality.
```java
Vehicle clonned = (Vehicle) v.clone() ;
System.out.println(clonned.equals(v)) ;
```

### setImmutable()
```setImmutable()``` sets object to be immutable. No further **set**, **remove** or **clear** will be possible.


### fill()
Sometimes Bio Objects can contain dynamic expression values which can be filled afterwards using other Bio Objects.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<vehicle type="Vehicle" code="1">
    <fuel-efficiency type="Double">17.8</fuel-efficiency>
    <producer type="String">Ford</producer>
    <undefined-tag type="String">Hello world</undefined-tag>
    <year-of-production type="Integer">2019</year-of-production>
    <horse-power type="Dynamic">calculateHP(vehicle.engine, vehicle.cylinders)</horse-power>
    <is-driveable type="Dynamic">weather.celcius > -10 and !weather.is_windy</is-driveable>
</vehicle>
```
Once it is parsed by XML parser we can use following code to fill in dynamic values with actual ones:
```java
Weather w = new Weather() ;
w.setCelcius(11) ;
w.setWindy(false) ;

Vehicle v = (Vehicle) BioObject.fromXml(xml) ;
v.fill(v, w) ;
System.out.println(v) ;
```
And result will be as following
```xml
<?xml version="1.0" encoding="UTF-8"?>
<vehicle type="Vehicle" code="1">
    <fuel-efficiency type="Double">17.8</fuel-efficiency>
    <producer type="String">Ford</producer>
    <undefined-tag type="String">Hello world</undefined-tag>
    <year-of-production type="Integer">2019</year-of-production>
    <horse-power type="Double">220.0</horse-power>
    <is-driveable type="Boolean">true</is-driveable>
</vehicle>
```
# Bio Enums
Library also supports definition of Bio Enums. Which are also defined using annotations as following:
```java
@BioEnumObj
public class EngineType extends BioEnum {
    	public EngineType(int ordinal, String name) {
        	super(ordinal, name);
    	}
	
	public final static EngineType VEE = new EngineType(0, "vee");
	public final static EngineType INLINE = new EngineType(1, "inline");
	public final static EngineType ROTARY = new EngineType(2, "rotary");
}
```
We create a class by exteding ```BioEnum``` and annotate it using ```@BioEnumObj```. Then we can add enum values by creating ```public static final``` instances inside class. They will be automatically picked up and added to dictionary. 

In order to use Bio Enums with Bio Objects you must create another tag as in following example:
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
	  @BioTag(type="EngineType")
	  public static final String ENGINE_TYPE = "engine_type" ;
}
```
Inside ```@BioTag``` we specify type as class name of Bio Enum class. We can also provide initial value for tag as following:
```java
	  @BioTag(type="EngineType", initial="inline")
	  public static final String ENGINE_TYPE = "engine_type" ;
````
**Note that** in fact ```initial=""``` attribute can be used for all tags to provide initial value.

Now when we export object to xml we will get following:
```java
v.set(Vehicle.PRODUCER, "Ford") ;
v.set(Vehicle.YEAR_OF_PRODUCTION, 2019) ;
v.set(Vehicle.FUEL_EFFICIENCY, 17.8) ;
v.set(Vehicle.ENGINE, 2000) ;

v.set(Vehicle.ENGINE_TYPE, EngineType.INLINE) ;
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<vehicle type="Vehicle" code="32682">
    <engine type="Integer">2000</engine>
    <engine-type type="EngineType">inline</engine-type>
    <fuel-efficiency type="Double">17.8</fuel-efficiency>
    <producer type="String">Ford</producer>
    <year-of-production type="Integer">2019</year-of-production>
</vehicle>
```
Respectively all xml and json parsing will remain unchanged it will be handled by the library.

**Note that** Bio Enums are stored as Bio Enum instances but serialized as int. Since ```class BioEnum extends java.lang.Number``` enums also act as numbers. But during export to xml or json string representation is being used. 

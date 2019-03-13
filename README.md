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

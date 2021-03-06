# Bio Objects
Bio Objects is a library which can be used as a replacement for **Java Beans**. Bio Objects are based on Maps (Keys-Values) and you will only need to extend ```BioObject``` class and add necessary annotations. Bio Dictionary will be built based on @annotations or xml configuration and will contain all information required for serialization, xml/json parsing etc. 

[**Bio Objects**](https://github.com/rdavudov/bio-object-logic/wiki/Bio-Objects-Basics) are very useful in conjunction with [**Bio Expressions**](https://github.com/rdavudov/bio-object-logic/wiki/Bio-Expressions) where you can write dynamic decision making mechanisms for your application. Such as :
- if		```car.year_of_production > 2015 and car.engine * 2 < 6000``` 		do this
- if 	```car.fuel_efficiency.checkEUStandard()``` 				do this
- if 	```car.calculateHP(car.cylinders, car.engine) > 200``` 			do this
- if 	```4 <= car.engine.cylinders <= 6 and car.fuel_efficiency < 15.4```		do this
- if 	```?car.producer and car.producer = ['BMW', 'Toyota']``` 		do this (if ```car``` object contains a tag ```producer``` and it is equal to any of the elements of array.)


Here is simple definition of Bio Object
```java
@BioObj
public class Car extends BioObject {

  @BioTag(type="Integer")
  public static final String YEAR_OF_PRODUCTION = "year_of_production" ;
  
  @BioTag(type="String")
  public static final String PRODUCER = "producer" ;
  
  @BioTag(type="Double")
  public static final String FUEL_EFFICIENCY = "fuel_efficiency" ;
}
```

For more information please visit [wiki](https://github.com/rdavudov/bio-object-logic/wiki)

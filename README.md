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

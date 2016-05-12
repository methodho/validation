# Validation

Some sample to do the validating things

## JSR-303

`package tw.com.shihyu.validation.jsr303`

### Intergration with JEXL java expression language


```java
@AssertThat(value = "this.age >= 18 || (this.age < 18  && not empty(this.parent))",
    propertyNode = "age")
@Getter
@Setter
public class MyBean {
	private int age;
	private String parent;
	@AssertThat("this.isAfter(forName('java.time.LocalDate').now().plusDays(7))")
	private LocalDate expire;
	// ...
}
```

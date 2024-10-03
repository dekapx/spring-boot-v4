# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.2/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.2/maven-plugin/build-image.html)
* [Swagger] (http://localhost:8082/credit-rating-service/api/v1/info)
* http://localhost:8082/credit-rating-service/swagger-ui/index.html


## Credit Rating Service    [credit-rating-service]
```html
    - Request:
        - SSN
        - First Name
        - Last Name
        - Date of Birth 
    - Response
        - SSN
        - First Name
        - Last Name
        - Date of Birth
        - Credit Score
```
### Sample Request 
```html
{
  "ssn": "111-22-3344",
  "firstName": "Dummy",
  "lastName": "Person",
  "dateOfBirth": "1980-01-02"
}
```
# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

### Credit Rating Service URLs
* [Info URL](http://localhost:8082/api/v1/info)
* [Swagger](http://localhost:8082/swagger-ui/index.html)

```bash
$ mvn spring-boot:run
$ mvn spring-boot:run -DSERVER_PORT=8082
$ mvn spring-boot:run -DSERVER_PORT=8083
```

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
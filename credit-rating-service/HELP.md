# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

### Credit Rating Service URLs
* [Info URL](http://localhost:8082/credit-rating-service/api/v1/info)
* [Swagger](http://localhost:8082/credit-rating-service/swagger-ui/index.html)


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
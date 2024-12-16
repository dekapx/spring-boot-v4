# Getting Started

### Guides
* [Building a GraphQL service](https://spring.io/guides/gs/graphql-server/)

http://localhost:8085/spring-boot-graphql/api/v1/info
http://localhost:8085/spring-boot-graphql/graphiql?path=/spring-boot-graphql/graphql

## Use Postman
Postman: http://localhost:8085/spring-boot-graphql/graphql

```graphql

query FindAll {
    findAll {
        id
        username
        firstName
        lastName
        email
    }
}

query FindAll {
    findById(id: "1") {
        id
        username
        firstName
        lastName
        email
    }
}

mutation CreateUser {
    createUser(
        request: {
            username: "dummyuser"
            firstName: "Dummy"
            lastName: "User"
            email: "dummy.user@google.com"
        }
    ) {
        id
        username
        firstName
        lastName
        email
    }
}

```


```css
https://medium.com/javarevisited/building-a-crud-application-with-graphql-and-springboot-11dd719e4a5e
https://medium.com/@digvijay17july/building-a-robust-rest-api-with-graphql-and-spring-boot-using-h2-database-8935ceed264d
```
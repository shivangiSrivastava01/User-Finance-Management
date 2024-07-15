# User-Finance-Management-System

## Description
Manages Budgets and Expenses for personal finance management. Notifies In case expense exceeds allocated budget

## Dependency
Please add below mentioned jar files:
- jakarta.persistence-api-2.2.3-javadoc.jar
- javax.jar

## Setup
1. Clone the repository.
2. For each service Navigate to the respective project directory.
3. Run `mvn spring-boot:run`.
4. For the Complete system to run, each microservice should be in running state
5. Post starting services, please proceed with user creation, prior to budget and expense creation.


## API Documentation
Access the Swagger UI 
- User-Service: `http://localhost:8083/swagger-ui.html`
- Budget-Service: `http://localhost:8080/swagger-ui.html`
- Expense-service: `http://localhost:8081/swagger-ui.html`
- Notification-service: `http://localhost:8082/swagger-ui.html`

## Postman Collection
Import the `User-Finance-Management.postman_collection.json` file in Postman to test the APIs.

## Test user for notification mail
- Email: systemtest.email01@gmail.com
- Password: (Shared via Mail)

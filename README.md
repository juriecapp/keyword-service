Keyword-service created with Java 21 and Spring boot 

MSSQL used for local testing

1) We first install Docker MSSQL Server with this command on Windows 11 machine

docker run -d `
   --name mssql-keyworddb `
   -e ACCEPT_EULA=Y `
   -e MSSQL_SA_PASSWORD="SQLserver123!" `
   -p 1433:1433 `
   mcr.microsoft.com/mssql/server:2022-latest

Then we created a database KeywordDB from an SQL IDE with default schema dbo.

The other database object was build by Flyway when the service start up using the application-local.yml config.

2) Testing wise we use H2 DB for easy spinning up DB for testing the controller etc.

3) The Swagger page can be found at the http://localhost:8080/swagger-ui.html

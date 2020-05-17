# Senior Backend Java Developer Challenge

## Instruções para build

No arquivo application.properties está informado configuração para conexão com o banco de dados:

```
spring.datasource.url = jdbc:postgresql://localhost:5432/postgres
spring.datasource.username = postgres
spring.datasource.password = postgres
```

Caso seja necessário, altere para conectar onde desejar.

Realize o build do projeto utilizando Maven.

## Instruções para execução

Execute:
 
```
java -jar senior-challenge-0.0.1-SNAPSHOT.jar
```

## Documentação da API

Os serviços e funcionalidades estão descritos na própria API. Depois de executar acesse: http://localhost:8080/api/v1/swagger-ui.html
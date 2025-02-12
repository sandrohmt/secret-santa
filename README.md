# Secret Santa API

Esta aplicação foi desenvolvida para facilitar a organização de amigos ocultos, permitindo que os usuários cadastrem participantes, criem grupos e realizem sorteios automaticamente. Além disso, a API envia e-mails com os resultados do sorteio para cada participante de forma segura.

Funcionalidades

Autenticação de Usuários: Cadastro e login seguro utilizando Spring Security.

Gestão de Grupos: Criação, edição e remoção de grupos para o sorteio.

Sorteio Automático: Realiza o sorteio dos participantes dentro do grupo criado.

Envio de E-mails: Notificações automáticas por e-mail com os resultados do sorteio.

Controle de Acessos: Permissões baseadas em papéis (ADMIN, USER).

Tratamento de Erros: Respostas claras e detalhadas para requisições incorretas.

Monitoramento do Sistema: Endpoints para verificação de saúde e métricas.

Tecnologias Utilizadas

Java 17

Spring Boot 3.3

Spring Security 6.3

Spring Data JPA

Spring Mail (JavaMailSender)

JWT para autenticação

Maven

Docker

Banco de Dados MySQL

Swagger (a ser adicionado futuramente)

Testes de Integração (a serem adicionados futuramente)

Requisitos

Java 17

Docker (recomendado) ou MySQL instalado localmente

Configuração Inicial

Para executar a aplicação, inicie o banco de dados com Docker:

docker-compose up

Adicione as roles no banco de dados:

INSERT INTO roles (id, role) VALUES (1, 'ADMIN'), (2, 'USER');

A classe InitialConfig dentro do pacote config pode ser utilizada para popular o banco com dados de teste automaticamente ao iniciar a aplicação.

Execução dos Testes

Para rodar os testes unitários, utilize:

mvn test

Uso

Documentação da API (Swagger)

No futuro, a API contará com documentação interativa no Swagger. Para acessá-la, utilize:

http://localhost:8080/swagger-ui.html

Os endpoints contarão com descrições detalhadas e exemplos de uso.

Monitoramento e Métricas

A API expõe alguns endpoints para monitoramento do sistema:

/actuator/info - Informações básicas da aplicação.

/actuator/metrics - Métricas de desempenho.

/actuator/health - Status de saúde da aplicação.

Futuramente, a API será integrada com Prometheus e Grafana para visualização de métricas.

Contribuição

Sinta-se à vontade para contribuir para este projeto! Para sugerir melhorias ou reportar bugs, abra uma issue neste repositório.

Essa documentação será atualizada conforme novas funcionalidades forem implementadas. 🚀


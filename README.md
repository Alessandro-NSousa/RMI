# Java-RMI-Chat
Um aplicativo de bate-papo usando invocação de método remoto java

### Um projeto de terceiro ano da faculdade :mortar_board: :three:

Este é um projeto para construir um aplicativo de bate-papo cliente-servidor usando a tecnologia java RMI
Os diretórios incluídos neste repositório:
* Java-RMI-Chat: O diretório do projeto eclipse
- Cliente: Todo o código relevante para a GUI de bate-papo do cliente.
- Servidor: O código para o servidor central.
* Design: diagramas UML e design de esboço de wireframe

### Features:  
- O aplicativo segue uma topologia de hub e spoke, com o servidor como hub.
- Os clientes fazem login no sistema com um nome de usuário
- Os clientes podem enviar uma mensagem de chat normal (transmitir para todos os clientes)
- Os clientes podem enviar uma mensagem privada para um ou mais clientes
- O servidor mantém uma lista de usuários, que é exibida na GUI do cliente
- A lista de usuários online é atualizada em todos os clientes quando os usuários entram ou saem da sala de chat

### Instructionsll
- Inicie o servidor primeiro (método principal: ChatServer.java)
- Inicie um cliente (método principal: ClientRMIGUI.java)
- Digite um nome de usuário exclusivo para entrar no chat

### customization pela turma de sistemas de informação da UFPA 2019.4
- Adionado métodos para realizar criptografia das mensagens enviadas




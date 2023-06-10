# Java-RMI-Chat
A Chat application using java remote method invocation

### A third year college project :mortar_board: :three:  

This is a project to build a client-server chat application using java RMI technology
The directories included in this repo:  
* Java-RMI-Chat: The eclipse project directory  
	- Client: All relevant code for the client chat GUI.  
	- Server: The code for the central server.  
* Design: UML diagrams and wireframe sketch design 

### Features:  
- O aplicativo segue uma topologia de hub e spoke, com o servidor como hub.
- Os clientes fazem login no sistema com um nome de usuário
- Os clientes podem enviar uma mensagem de chat normal (transmitir para todos os clientes)
- Os clientes podem enviar uma mensagem privada para um ou mais clientes
- O servidor mantém uma lista de usuários, que é exibida na GUI do cliente
- A lista de usuários online é atualizada em todos os clientes quando os usuários entram ou saem da sala de chat

### Instructions
- Inicie o servidor primeiro (método principal: ChatServer.java)
- Inicie um cliente (método principal: ClientRMIGUI.java)
- Digite um nome de usuário exclusivo para entrar no chat



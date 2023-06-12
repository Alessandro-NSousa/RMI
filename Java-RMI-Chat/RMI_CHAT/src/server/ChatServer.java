package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

import client.ChatClient3IF;

/**
 * 
 * @author Daragh Walshe B00064428
 *         RMI Assignment 2 April 2015
 *
 */
public class ChatServer extends UnicastRemoteObject implements ChatServerIF {
	String line = "---------------------------------------------\n";
	private Vector<Chatter> chatters;
	private static final long serialVersionUID = 1L;

	// Constructor
	public ChatServer() throws RemoteException {
		super();
		chatters = new Vector<Chatter>(10, 1);
	}

	// -----------------------------------------------------------
	/**
	 * LOCAL METHODS
	 */
	public static void main(String[] args) {
		startRMIRegistry();
		String hostName = "localhost";
		String serviceName = "GroupChatService";

		if (args.length == 2) {
			hostName = args[0];
			serviceName = args[1];
		}

		try {
			ChatServerIF hello = new ChatServer();
			Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
			System.out.println("O servidor RMI de bate-papo em grupo está em execução...");
		} catch (Exception e) {
			System.out.println("O servidor teve problemas ao iniciar");
		}
	}

	/**
	 * Start the RMI Registry
	 */
	public static void startRMIRegistry() {
		try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("Servidor RMI pronto");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// -----------------------------------------------------------
	/*
	 * REMOTE METHODS
	 */

	/**
	 * Return a message to client
	 */
	public String sayHello(String ClientName) throws RemoteException {
		System.out.println(ClientName + " enviou uma mensagem");
		return "Olá " + ClientName + " do servidor de bate-papo em grupo";
	}

	/**
	 * Envie uma string (a última postagem, principalmente)
* para todos os clientes conectados
	 */
	public void updateChat(String name, String nextPost) throws RemoteException {
		String message = name + " : " + nextPost + "\n";
		sendToAll(message);
	}

	/**
	 * Receba uma nova referência remota de cliente
	 */
	@Override
	public void passIDentity(RemoteRef ref) throws RemoteException {
		System.out.println("\n" + ref.remoteToString() + "\n");
		try {
			System.out.println(line + ref.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receba um novo cliente e exiba detalhes no console
	 * enviar para o método de registro
	 */
	@Override
	public void registerListener(String[] details) throws RemoteException {
		System.out.println(new Date(System.currentTimeMillis()));
		System.out.println(details[0] + " entrou na sessão de chat");
		System.out.println(details[0] + "'nome do host : " + details[1]);
		System.out.println(details[0] + "'sRMI service : " + details[2]);
		registerChatter(details);
	}

	/** 
	 * @param details
	 */
	private void registerChatter(String[] details) {
		try {
			ChatClient3IF nextClient = (ChatClient3IF) Naming.lookup("rmi://" + details[1] + "/" + details[2]);

			chatters.addElement(new Chatter(details[0], nextClient));

			nextClient.messageFromServer("[Servidor] : Olá " + details[0] + " agora você pode conversar.\n");

			sendToAll("[Servidor] : " + details[0] + " entrou no grupo.\n");

			updateUserList();
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update all clients by remotely invoking their
	 * updateUserList RMI method
	 */
	private void updateUserList() {
		String[] currentUsers = getUserList();
		for (Chatter c : chatters) {
			try {
				c.getClient().updateUserList(currentUsers);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * generate a String array of current users
	 * 
	 * @return
	 */
	private String[] getUserList() {
		// generate an array of current users
		String[] allUsers = new String[chatters.size()];
		for (int i = 0; i < allUsers.length; i++) {
			allUsers[i] = chatters.elementAt(i).getName();
		}
		return allUsers;
	}

	/**
	 * Envie uma mensagem para todos os usuários
	 * 
	 * @param newMessage
	 */
	public void sendToAll(String newMessage) {
		for (Chatter c : chatters) {
			try {
				c.getClient().messageFromServer(newMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * remove a client from the list, notify everyone
	 */
	@Override
	public void leaveChat(String userName) throws RemoteException {

		for (Chatter c : chatters) {
			if (c.getName().equals(userName)) {
				System.out.println(line + userName + " left the chat session");
				System.out.println(new Date(System.currentTimeMillis()));
				chatters.remove(c);
				break;
			}
		}
		if (!chatters.isEmpty()) {
			updateUserList();
		}
	}

	/**
	 * A method to send a private message to selected clients
	 * The integer array holds the indexes (from the chatters vector)
	 * of the clients to send the message to
	 */
	@Override
	public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException {
		Chatter pc;
		for (int i : privateGroup) {
			pc = chatters.elementAt(i);
			pc.getClient().messageFromServer(privateMessage);
		}
	}

}

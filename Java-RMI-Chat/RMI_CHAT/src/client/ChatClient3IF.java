package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface remota para classes de cliente
  * Um método para receber uma string
  * Um método para atualizar as alterações na lista de usuários
 */
public interface ChatClient3IF extends Remote{
	//mensagem do servidor

	public void messageFromServer(String message) throws RemoteException;

	//atualizar lista de usuários

	public void updateUserList(String[] currentUsers) throws RemoteException;
	
}
/**
 * 
 * 
 * 
 *
 */
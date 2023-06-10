package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

//imports de criptografia
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * 
 * @author Daragh Walshe B00064428
 *         RMI Assignment 2 April 2015
 *
 */
public class ClientRMIGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel textPanel, inputPanel;
	private JTextField textField;
	private String name, message;
	private Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
	private Border blankBorder = BorderFactory.createEmptyBorder(10, 10, 20, 10);// top,r,b,l
	private ChatClient3 chatClient;
	private JList<String> list;
	private DefaultListModel<String> listModel;

	protected JTextArea textArea, userArea;
	protected JFrame frame;
	protected JButton privateMsgButton, startButton, sendButton;
	protected JPanel clientPanel, userPanel;

	
	private SecretKey generateSecretKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128); // Altere o tamanho da chave conforme necessário
			return keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * Main method to start client GUI app.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		// set the look and feel to 'Nimbus'
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}
		new ClientRMIGUI();
	}// end main

	/**
	 * GUI Constructor
	 */
	public ClientRMIGUI() {

		frame = new JFrame("Console de bate-papo do cliente");

		// -----------------------------------------
		/*
		 * intercept close method, inform server we are leaving
		 * then let the system exit.
		 */
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				if (chatClient != null) {
					try {
						sendMessage("Saiu da Conversa.");
						chatClient.serverIF.leaveChat(name);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
		// -----------------------------------------
		// remove window buttons and border frame
		// to force user to exit on a button
		// - one way to control the exit behaviour
		// frame.setUndecorated(true);
		// frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

		Container c = getContentPane();
		JPanel outerPanel = new JPanel(new BorderLayout());

		outerPanel.add(getInputPanel(), BorderLayout.CENTER);
		outerPanel.add(getTextPanel(), BorderLayout.NORTH);

		c.setLayout(new BorderLayout());
		c.add(outerPanel, BorderLayout.CENTER);
		c.add(getUsersPanel(), BorderLayout.WEST);

		frame.add(c);
		frame.pack();
		frame.setAlwaysOnTop(true);
		frame.setLocation(150, 150);
		textField.requestFocus();

		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Método para configurar o JPanel para exibir o texto do chat
	 * 
	 * @return
	 */
	public JPanel getTextPanel() {
		String welcome = "Bem-vindo, digite seu nome e pressione Iniciar para começar\n";
		textArea = new JTextArea(welcome, 14, 34);
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setFont(meiryoFont);

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textPanel = new JPanel();
		textPanel.add(scrollPane);

		textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
		return textPanel;
	}

	/**
	 * Método para construir o painel com campo de entrada
	 * 
	 * @return inputPanel
	 */
	public JPanel getInputPanel() {
		inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
		inputPanel.setBorder(blankBorder);
		textField = new JTextField();
		textField.setFont(meiryoFont);
		inputPanel.add(textField);
		return inputPanel;
	}

	/**
	 * Método para construir o painel exibindo os usuários conectados no momento
	 * com uma chamada para o método de construção do painel de botões
	 * 
	 * @return
	 */
	public JPanel getUsersPanel() {

		userPanel = new JPanel(new BorderLayout());
		String userStr = " Usuários ativos      ";

		JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
		userPanel.add(userLabel, BorderLayout.NORTH);
		userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));

		String[] noClientsYet = { "Nenhum outro usuário" };
		setClientPanel(noClientsYet);

		clientPanel.setFont(meiryoFont);
		userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);
		userPanel.setBorder(blankBorder);

		return userPanel;
	}

	/**
	 * Preencha o painel do usuário atual com um
* lista selecionável de usuários atualmente conectados
	 * 
	 * @param currClients
	 */
	public void setClientPanel(String[] currClients) {
		clientPanel = new JPanel(new BorderLayout());
		listModel = new DefaultListModel<String>();

		for (String s : currClients) {
			listModel.addElement(s);
		}
		if (currClients.length > 1) {
			privateMsgButton.setEnabled(true);
		}

		// Crie a lista e coloque-a em um painel de rolagem.
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(8);
		list.setFont(meiryoFont);
		JScrollPane listScrollPane = new JScrollPane(list);

		clientPanel.add(listScrollPane, BorderLayout.CENTER);
		userPanel.add(clientPanel, BorderLayout.CENTER);
	}

	/**
	 * Faça os botões e adicione o ouvinte
	 * 
	 * @return
	 */
	public JPanel makeButtonPanel() {
		sendButton = new JButton("Enviar ");
		sendButton.addActionListener(this);
		sendButton.setEnabled(false);

		privateMsgButton = new JButton("Enviar MP");
		privateMsgButton.addActionListener(this);
		privateMsgButton.setEnabled(false);

		startButton = new JButton("Iniciar ");
		startButton.addActionListener(this);

		JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
		buttonPanel.add(privateMsgButton);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(startButton);
		buttonPanel.add(sendButton);

		return buttonPanel;
	}

	/**
	 * Manipulação de ações nos botões
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			// get connected to chat service
			if (e.getSource() == startButton) {
				name = textField.getText();
				if (name.length() != 0) {
					frame.setTitle("Console de: " + name);
					textField.setText("");
					textArea.append("Usuário : " + name + " conectando ao chat...\n");
					getConnected(name);
					if (!chatClient.connectionProblem) {
						startButton.setEnabled(false);
						sendButton.setEnabled(true);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Digite seu nome para começar");
				}
			}

			// obter texto e limpar textField
			if (e.getSource() == sendButton) {
				message = textField.getText();
				textField.setText("");
				sendMessage(message);
				System.out.println("Enviando mensagem : " + message);
			}

			// enviar uma mensagem privada, para usuários selecionados
			if (e.getSource() == privateMsgButton) {
				int[] privateList = list.getSelectedIndices();

				for (int i = 0; i < privateList.length; i++) {
					System.out.println("índice selecionado :" + privateList[i]);
				}
				message = textField.getText();
				textField.setText("");
				sendPrivate(privateList);
			}

		} catch (RemoteException remoteExc) {
			remoteExc.printStackTrace();
		}

	}// end actionPerformed

	// --------------------------------------------------------------------

	/**
	 * Envie uma mensagem, para ser retransmitida a todos os participantes
	 * 
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendMessage(String chatMessage) throws RemoteException {
		try {
			SecretKey secretKey = generateSecretKey();
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedMessage = cipher.doFinal(chatMessage.getBytes());
	
			// Converte a mensagem criptografada em uma representação de string segura para transmissão
			String encryptedMessageStr = Base64.getEncoder().encodeToString(encryptedMessage);
	
			// Envie a mensagem criptografada para o servidor
			chatClient.serverIF.updateChat(name, encryptedMessageStr);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Envie uma mensagem, para ser retransmitida, apenas para participantes selecionados
	 * 
	 * @param chatMessage
	 * @throws RemoteException
	 */
	private void sendPrivate(int[] privateList) throws RemoteException {
		String privateMessage = "[Mensagem privada de " + name + "] :" + message + "\n";
		chatClient.serverIF.sendPM(privateList, privateMessage);
	}

	/**
	 * Faça a conexão com o servidor de chat
	 * 
	 * @param userName
	 * @throws RemoteException
	 */
	private void getConnected(String userName) throws RemoteException {
		// remova espaços em branco e caracteres não verbais para evitar url malformado
		String cleanedUserName = userName.replaceAll("\\s+", "_");
		cleanedUserName = userName.replaceAll("\\W+", "_");
		try {
			chatClient = new ChatClient3(this, cleanedUserName);
			chatClient.startClient();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}

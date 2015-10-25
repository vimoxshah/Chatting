package com.vimox.chatting;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;


public class Client extends JFrame {

	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message ="";
	private String serverIP;
	private Socket connection;
	
	
	public Client(String host) {
		super("Client Vimox");
		serverIP =host;
		userText = new JTextField();
		userText.setEditable(false);
		
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
						
					}
				}
				);
		add(userText,BorderLayout.NORTH);
		chatWindow= new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(400,400);
		setVisible(true);
		
	}
	public void startRunning(){
		try{
			connectToSever();
			setupStreams();
			whileChatting();
			
		}catch(EOFException eofException){
			showMessage("\n Client terminated connection !!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
			}
		finally{
			closeCrap();
		}
	}
	private void connectToSever() throws IOException{
		showMessage("Attempting connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP),6789);
		showMessage("Connected to:"+ connection.getInetAddress().getHostName());
	}

	
	
	private void setupStreams() throws IOException{
		output =new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Your Streams are now good to go... !! \n");
	}
	
	
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classnotfoundException){
				showMessage("\n I don't know that object type !");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	private void closeCrap(){
		showMessage("\n Closing connections...\n ");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT -"+ message);
			output.flush();
			showMessage("\n CLIENT-"  + message);
		}catch(IOException ioException){
			chatWindow.append("\n Something messed up sending message !!");
		}
		
	}
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(text);
						
					}
				}
				
				);
		}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
						}
				}
				);
	}
}

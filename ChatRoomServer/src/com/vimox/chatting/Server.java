package com.vimox.chatting;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;


public class Server extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	
	public Server() {
		super("Vimox's Instant Messenger");
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
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(400,400);
		setVisible(true);
		}
	
	public void startRunning(){
		try{
			server =new ServerSocket(6789,100);
			
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}
				catch(EOFException eofException){
					showMessage("\n Server ended Connection !!");
				}
				finally{
					closeCrap();
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void waitForConnection() throws IOException{
		showMessage("Waitig for someone to connect....\n");
		connection = server.accept();
		showMessage(" Now connected to " +connection.getInetAddress().getHostName());
	
	}
	
	private void setupStreams() throws IOException{
		output =new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup.. \n");
	}
	
	private void whileChatting() throws IOException{
		String message = "You are now connected !";
		sendMessage(message);
		ableToType(true);
		
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classnotfoundException){
				showMessage("\n idk wtf that user send !");
			}
		}while(!message.equals("CLIENT - END"));
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
			output.writeObject("SERVER -"+ message);
			output.flush();
			showMessage("\n SERVER-"  + message);
		}catch(IOException ioException){
			chatWindow.append("\n ERROR: Dude I can't Send this Message !!");
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
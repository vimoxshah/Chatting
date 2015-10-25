package com.vimox.chatting;
import javax.swing.JFrame;


public class ClientTest {

	public static void main(String[] args) {
		Client vimox;
		vimox=new Client("127.0.0.1");
		vimox.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		vimox.startRunning();

	}

}

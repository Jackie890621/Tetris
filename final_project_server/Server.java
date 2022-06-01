package com.tetris;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Platform;
import com.tetris.Shape.Tetrominoe;
import javafx.scene.layout.*;

public class Server{
	
	private ServerSocket serverSocket;
	private Socket socket;
	private InputStream inputStream;
	private ObjectInputStream objectInputStream;
	private OutputStream outputStream;
	private ObjectOutputStream objectOutputStream;
	
	public Server(ServerSocket serverSocket){
		System.out.println(2);
		try {
			this.serverSocket = serverSocket;
			System.out.println(3);
			this.socket = serverSocket.accept();
			System.out.println(4);
			this.inputStream = socket.getInputStream();
			//this.objectInputStream = new ObjectInputStream(inputStream);
			this.outputStream = socket.getOutputStream();
			//this.objectOutputStream = new ObjectOutputStream(outputStream);
		} catch (Exception e) {
			System.out.println("Error creating server");
			e.printStackTrace();
			closeEverything(socket, objectInputStream, objectOutputStream);
		}
	}
	
	public void sendMessageToClient(Pane temp) {
		try {
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(temp);
		} catch (IOException e) {
			System.out.println("Error creating server");
			e.printStackTrace();
			closeEverything(socket, objectInputStream, objectOutputStream);
		}
	}
	
	public void receiveMessageFromClient(Pane your) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (socket.isConnected()) {
					try {
						objectInputStream = new ObjectInputStream(inputStream);
						Pane temp = (Pane) objectInputStream.readObject();
						MultiController.display(temp, your);
					} catch (Exception e) {
						System.out.println("Error creating server");
						e.printStackTrace();
						closeEverything(socket, objectInputStream, objectOutputStream);
						break;
					}
				}
			}
		}).start();
	}
	
	public void closeEverything (Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
		try {
			if (socket != null) {
				socket.close();
			}
			if (objectInputStream != null) {
				objectInputStream.close();
			}
			if (objectOutputStream != null) {
				objectOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
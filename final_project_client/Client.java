package com.tetris;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Platform;
import com.tetris.Shape.Tetrominoe;
import javafx.scene.layout.*;

public class Client{
	
	private Socket socket;
	private InputStream inputStream;
	private ObjectInputStream objectInputStream;
	private OutputStream outputStream;
	private ObjectOutputStream objectOutputStream;
	
	public Client(Socket socket){
		System.out.println(2);
		try {
			this.socket = socket;			
			this.inputStream = socket.getInputStream();
			//this.objectInputStream = new ObjectInputStream(inputStream);
			this.outputStream = socket.getOutputStream();
			//this.objectOutputStream = new ObjectOutputStream(outputStream);
		} catch (Exception e) {
			System.out.println("Error creating client");
			e.printStackTrace();
			closeEverything(socket, objectInputStream, objectOutputStream);
		}
	}
	
	public void sendMessageToServer(Pane temp) {
		try {
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(temp);
		} catch (IOException e) {
			System.out.println("Error creating client");
			e.printStackTrace();
			closeEverything(socket, objectInputStream, objectOutputStream);
		}
	}
	
	public void receiveMessageFromServer(Pane your) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (socket.isConnected()) {
					try {
						objectInputStream = new ObjectInputStream(inputStream);
						Pane temp = (Pane) objectInputStream.readObject();
						MultiController.display(temp, your);
					} catch (Exception e) {
						System.out.println("Error creating client");
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
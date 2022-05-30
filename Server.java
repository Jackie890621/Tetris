package com.tetris;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Platform;
import com.tetris.Shape.Tetrominoe;
import com.tetris.Server;

public class Server{
	
	ServerSocket ss;
	DataInputStream input;
	DataOutputStream output;
	
	public boolean gameStarted = false;
	
	public int p1numLinesRemoved = 0;
	public int p2numLinesRemoved = 0;
	public Shape p1curPiece;
	public Shape p2curPiece;
	public Shape p1next1shape = new Shape();
	public Shape p2next1shape = new Shape();
	public Shape p1next2shape = new Shape();
	public Shape p2next2shape = new Shape();
	public Shape p1next3shape = new Shape();
	public Shape p2next3shape = new Shape();
	public Shape p1next4shape = new Shape();
	public Shape p2next4shape = new Shape();
	public Shape p1next5shape = new Shape();
	public Shape p2next5shape = new Shape();
	public Shape p1lock = new Shape();
	public Shape p2lock = new Shape();
    public Tetrominoe[] p1board;
    public Tetrominoe[] p2board;
	public boolean p1isFallingFinished = false;
	public boolean p2isFallingFinished = false;
	
	public Server(){
		public createserver(){
			new Thread( ()->{
				while(true){
					try{
						ss = new ServerSocket(8787);
						Platform.runLater(()-> System.out.println("Server started at " + new Date() + "\r\n"));
						Socket sck = ss.accept();
						input = new DataInputStream(sck.getInputStream());
						output = new DataOutputStream(sck.getOutputStream());
						while(true){
							//double radius = input.readDouble();
							//double area = radius * radius * Math.PI;
							//output.writeDouble(area);
							String messagetitle = input.readLine();
							switch(messagetitle){
								case "p1numLinesRemoved":
									p1numLinesRemoved++;
									break;
								case "p2numLinesRemoved":
									p1numLinesRemoved++;
									break;
								case "p1curPiece":
									p1curPiece.setShape(inttoshape(input.readInt()));
									break;
								case "p2curPiece":
									p2curPiece.setShape(inttoshape(input.readInt()));
									break;
								case "p1lock":
									store(1);
									output.writeInt(shapetoint(p1lock));
									sendnextshapes(1);
									break;
								case "p2lock":
									store(2);
									output.writeInt(shapetoint(p2lock));
									sendnextshapes(2);
									break;
								case "p1isFallingFinished":
									nextshapes(1);
									sendnextshapes(1);
									break;
								case "p2isFallingFinished":
									nextshapes(2);
									sendnextshapes(2);
									break;
							}
							Platform.runLater( ()->{
								System.out.println("Message received from client: " + messagetitle + "\r\n");
							});
						}
					}
					catch(IOException ex){
						System.err.println(ex);
					}
					try{
						ss.close();
						input.close();
						output.close();
					}
					catch(IOException ex){
						System.err.println(ex);
					}
				}
			}).start();
		}
	}
	
	private void start() {
		p1next1shape.setRandomShape();
		p1next2shape.setRandomShape();
		p1next3shape.setRandomShape();
		p1next4shape.setRandomShape();
		p1next5shape.setRandomShape();
		p2next1shape.setRandomShape();
		p2next2shape.setRandomShape();
		p2next3shape.setRandomShape();
		p2next4shape.setRandomShape();
		p2next5shape.setRandomShape();
		gameStarted = true;
		
        p1curPiece = new Shape();
		p2curPiece = new Shape();
        p1board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        p2board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

        //clearBoard();
        newPiece(1);
        newPiece(2);
		//gamecycle = new Timer();
		//gamecycle.schedule(new Gamecycle(), 0, PERIOD_INTERVAL);
	}
	
	private void newPiece() {
		if (i == 1){
			p1curPiece.setShape(p1next1shape.getShape());
			nextshapes(1);
		}
		else if(i == 2){
			p2curPiece.setShape(p2next1shape.getShape());
			nextshapes(2);
		}
	}
	
	private void nextshapes(int i) {
		if(gameStarted){
			if(i == 1){
				p1next1shape.setShape(p1next2shape.getShape());
				p1next2shape.setShape(p1next3shape.getShape());
				p1next3shape.setShape(p1next4shape.getShape());
				p1next4shape.setShape(p1next5shape.getShape());
				p1next5shape.setRandomShape();
			}
			else if(i == 2){
				p2next1shape.setShape(p2next2shape.getShape());
				p2next2shape.setShape(p2next3shape.getShape());
				p2next3shape.setShape(p2next4shape.getShape());
				p2next4shape.setShape(p2next5shape.getShape());
				p2next5shape.setRandomShape();
			}
		}
	}

	private Shape inttoshape(int i){
		switch(i){
			case 0:
				return Tetrominoe.NoShape;
				break;
			case 1:
				return Tetrominoe.ZShape;
				break;
			case 2:
				return Tetrominoe.SShape;
				break;
			case 3:
				return Tetrominoe.LineShape;
				break;
			case 4:
				return Tetrominoe.TShape;
				break;
			case 5:
				return Tetrominoe.SquareShape;
				break;
			case 6:
				return Tetrominoe.LShape;
				break;
			case 7:
				return Tetrominoe.MirroredLShape;
				break;
		}
	}
	
	private int shapetoint(Shape shape){
		switch(shape){
			case Tetrominoe.NoShape:
				return 0;
				break;
			case Tetrominoe.ZShape:
				return 1;
				break;
			case Tetrominoe.SShape:
				return 2;
				break;
			case Tetrominoe.LineShape:
				return 3;
				break;
			case Tetrominoe.TShape:
				return 4;
				break;
			case Tetrominoe.SquareShape:
				return 5;
				break;
			case Tetrominoe.LShape:
				return 6;
				break;
			case Tetrominoe.MirroredLShape:
				return 7;
				break;
		}
	}
	
	private void store(int i) {
		Shape temp = new Shape();
		if (i == 1){
			if (p1lock.getShape() == Tetrominoe.NoShape) {
				p1lock.setShape(p1curPiece.getShape());
				p1curPiece.setShape(p1next1shape.getShape());
				nextshapes(1);
			} 
			else {
				temp.setShape(p1curPiece.getShape());
				p1curPiece.setShape(p1lock.getShape());
				p1lock.setShape(temp.getShape());
			}
		}
		else if(i == 2){
			if (p2lock.getShape() == Tetrominoe.NoShape) {
				p2lock.setShape(p2curPiece.getShape());
				p2curPiece.setShape(p2next1shape.getShape());
				nextshapes(2);
			} 
			else {
				temp.setShape(p2curPiece.getShape());
				p2curPiece.setShape(p2lock.getShape());
				p2lock.setShape(temp.getShape());
			}
		}
	}
	
	private void sendnextshapes(int i){
		if(i == 1){
			output.writeInt(shapetoint(p1next1shape.getShape()));
			output.writeInt(shapetoint(p1next2shape.getShape()));
			output.writeInt(shapetoint(p1next3shape.getShape()));
			output.writeInt(shapetoint(p1next4shape.getShape()));
			output.writeInt(shapetoint(p1next5shape.getShape()));
		}
		else if(i == 2){
			output.writeInt(shapetoint(p2next1shape.getShape()));
			output.writeInt(shapetoint(p2next2shape.getShape()));
			output.writeInt(shapetoint(p2next3shape.getShape()));
			output.writeInt(shapetoint(p2next4shape.getShape()));
			output.writeInt(shapetoint(p2next5shape.getShape()));
		}
	}
}
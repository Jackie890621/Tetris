package com.tetris;

import java.io.*;
import java.net.*;
import com.tetris.Shape.Tetrominoe;
import javafx.fxml.FXML;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

public class MultiController {

    @FXML Button back;
    @FXML Pane myhold;
    @FXML public transient Pane mymain;
    @FXML Pane mynext1;
    @FXML Pane mynext2;
    @FXML Pane mynext3;
    @FXML Pane mynext4;
    @FXML Pane mynext5;
    @FXML Label myscore;
    @FXML Button mystart;
	@FXML Label top;
    @FXML Pane yourhold;
    @FXML public transient Pane yourmain;
    @FXML Pane yournext1;
    @FXML Pane yournext2;
    @FXML Pane yournext3;
    @FXML Pane yournext4;
    @FXML Pane yournext5;
    @FXML Label yourscore;
	
	private Server server;
	private Timer timer;
	private Timer gamecycle;
	
	private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
	private final int PERIOD_INTERVAL = 800;
	private int numLinesRemoved = 0;
	public Shape curPiece;
	public Shape ycurPiece = new Shape();
	public Shape next1shape = new Shape();
	public Shape next2shape = new Shape();
	public Shape next3shape = new Shape();
	public Shape next4shape = new Shape();
	public Shape next5shape = new Shape();
	public Shape mynext1shape = new Shape();
	public Shape mynext2shape = new Shape();
	public Shape mynext3shape = new Shape();
	public Shape mynext4shape = new Shape();
	public Shape mynext5shape = new Shape();
	public Shape yournext1shape = new Shape();
	public Shape yournext2shape = new Shape();
	public Shape yournext3shape = new Shape();
	public Shape yournext4shape = new Shape();
	public Shape yournext5shape = new Shape();
	private Shape lock = new Shape();
    public Tetrominoe[] board;
	public int[] boardint = new int[200];
	public Tetrominoe[] yourboard;
	public int[] yboardint = new int[200];
	private boolean isFallingFinished = false;
	private boolean isPaused = false;
	private boolean gameStarted = false;
	public int curX = 0;
    public int curY = 0;
	public int ycurX = 0;
    public int ycurY = 0;
	
	class Mymainclass{
		int curPieceINT;
		int mynext1INT;
		int mynext2INT;
		int mynext3INT;
		int mynext4INT;
		int mynext5INT;
		int curXINT;
		int curYINT;
	}
	
	class Yourmainclass{
		int ycurPieceINT;
		int yournext1INT;
		int yournext2INT;
		int yournext3INT;
		int yournext4INT;
		int yournext5INT;
		int ycurXINT;
		int ycurYINT;
	}
	
	private Shape inttoshape(int i){
		Shape s = new Shape();
		switch(i){
			case 0:
				s.setShape(Tetrominoe.NoShape);
				break;
			case 1:
				s.setShape(Tetrominoe.ZShape);
				break;
			case 2:
				s.setShape(Tetrominoe.SShape);
				break;
			case 3:
				s.setShape(Tetrominoe.LineShape);
				break;
			case 4:
				s.setShape(Tetrominoe.TShape);
				break;
			case 5:
				s.setShape(Tetrominoe.SquareShape);
				break;
			case 6:
				s.setShape(Tetrominoe.LShape);
				break;
			case 7:
				s.setShape(Tetrominoe.MirroredLShape);
				break;
		}
		return s;
	}
	
	private int shapetoint(Shape s){
		Tetrominoe shape = s.getShape();
		int shapenum = 0;
		switch(shape){
			case NoShape:
				shapenum =  0;
				break;
			case ZShape:
				shapenum =  1;
				break;
			case SShape:
				shapenum =  2;
				break;
			case LineShape:
				shapenum =  3;
				break;
			case TShape:
				shapenum =  4;
				break;
			case SquareShape:
				shapenum =  5;
				break;
			case LShape:
				shapenum =  6;
				break;
			case MirroredLShape:
				shapenum =  7;
				break;
		}
		return shapenum;
	}
	
	private void converttoint(Mymainclass m){
		m.curPieceINT = shapetoint(curPiece);
		mynext1shape.setShape(next1shape.getShape());
		mynext2shape.setShape(next2shape.getShape());
		mynext3shape.setShape(next3shape.getShape());
		mynext4shape.setShape(next4shape.getShape());
		mynext5shape.setShape(next5shape.getShape());
		m.mynext1INT = shapetoint(mynext1shape);
		m.mynext2INT = shapetoint(mynext2shape);
		m.mynext3INT = shapetoint(mynext3shape);
		m.mynext4INT = shapetoint(mynext4shape);
		m.mynext5INT = shapetoint(mynext5shape);
		m.curXINT = curX;
		m.curYINT = curY;
		boardint = boardInt();
	}
	
	private void converttoshape(Yourmainclass y){
		ycurPiece = inttoshape(y.ycurPieceINT);
		yournext1shape = inttoshape(y.yournext1INT);
		yournext2shape = inttoshape(y.yournext2INT);
		yournext3shape = inttoshape(y.yournext3INT);
		yournext4shape = inttoshape(y.yournext4INT);
		yournext5shape = inttoshape(y.yournext5INT);
		ycurX = y.ycurXINT;
		ycurY = y.ycurYINT;
		yourboard = boardTet(yboardint);
	}
	
	private void send(Mymainclass m){
		server.sendIntToClient(99);
		server.sendIntToClient(m.curPieceINT);
		server.sendIntToClient(m.mynext1INT);
		server.sendIntToClient(m.mynext2INT);
		server.sendIntToClient(m.mynext3INT);
		server.sendIntToClient(m.mynext4INT);
		server.sendIntToClient(m.mynext5INT);
		server.sendIntToClient(m.curXINT);
		server.sendIntToClient(m.curYINT);
	}
	
	private void receive(Yourmainclass y){
		y.ycurPieceINT = server.receiveIntFromClient();
		y.yournext1INT = server.receiveIntFromClient();
		y.yournext2INT = server.receiveIntFromClient();
		y.yournext3INT = server.receiveIntFromClient();
		y.yournext4INT = server.receiveIntFromClient();
		y.yournext5INT = server.receiveIntFromClient();
		y.ycurXINT = server.receiveIntFromClient();
		y.ycurYINT = server.receiveIntFromClient();
	}

	public void initialize() {
		System.out.println(1);
		try {
			server = new Server(new ServerSocket(1234));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error creating server");
		}
		
		
		
		top.setText("2:00");
		lock.setShape(Tetrominoe.NoShape);
		next1shape.setShape(Tetrominoe.NoShape);
		next2shape.setShape(Tetrominoe.NoShape);
		next3shape.setShape(Tetrominoe.NoShape);
		next4shape.setShape(Tetrominoe.NoShape);
		next5shape.setShape(Tetrominoe.NoShape);
		
		mymain.setStyle("-fx-background-color: " + "GAINSBORO");
		mynext1.setStyle("-fx-background-color: " + "GAINSBORO");
		mynext2.setStyle("-fx-background-color: " + "GAINSBORO");
		mynext3.setStyle("-fx-background-color: " + "GAINSBORO");
		mynext4.setStyle("-fx-background-color: " + "GAINSBORO");
		mynext5.setStyle("-fx-background-color: " + "GAINSBORO");
		myhold.setStyle("-fx-background-color: " + "GAINSBORO");
		
		yourmain.setStyle("-fx-background-color: " + "rgb(235, 235, 235)");
		yournext1.setStyle("-fx-background-color: " + "rgb(235, 235, 235)");
		yournext2.setStyle("-fx-background-color: " + "rgb(235, 235, 235)");
		yournext3.setStyle("-fx-background-color: " + "rgb(235, 235, 235)");
		yournext4.setStyle("-fx-background-color: " + "rgb(235, 235, 235)");
		yournext5.setStyle("-fx-background-color: " + "rgb(235, 235, 235)");
		yourhold.setStyle("-fx-background-color: " + "rgb(235, 235, 235)");
	}

    @FXML
    void backclicked(ActionEvent event) {
		Multi.st.close();
    }

    @FXML
    void startclicked(ActionEvent event) {
		if (server.receiveIntFromClient() == 123){
			mystart.setDisable(true);
			countdown();
			start();
			Multi.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {			
				
				@Override
				public void handle(KeyEvent e) {
					if (curPiece.getShape() == Tetrominoe.NoShape) {
						return;
					}
					if (e.getCode() == KeyCode.UP) {
						tryMove(curPiece.rotateRight(), curX, curY, true);
					} else if (e.getCode() == KeyCode.DOWN) {
						oneLineDown();
					} else if (e.getCode() == KeyCode.LEFT) {
						tryMove(curPiece, curX - 1, curY, true);
					} else if (e.getCode() == KeyCode.RIGHT) {
						tryMove(curPiece, curX + 1, curY, true);
					} else if (e.getCode() == KeyCode.CONTROL) {
						tryMove(curPiece.rotateLeft(), curX, curY, true);
					} else if (e.getCode() == KeyCode.SPACE) {
						dropDown();
					} else if (e.getCode() == KeyCode.P) {
						pause();
					} else if (e.getCode() == KeyCode.SHIFT) {
						store(curPiece);
					}
					
					
				}
			});
		}
    }
	
	private void countdown() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
            int i = 120;
			String time;
			
            public void run() {
				if (i % 60 < 10) {
					time = String.valueOf(i / 60) + ":0" + String.valueOf(i % 60);
				} else {
					time = String.valueOf(i / 60) + ":" + String.valueOf(i % 60);
				}
				
                if (!isPaused) {
					Platform.runLater(() -> top.setText(time));
					i--;
				}
                if (i < 0) {
                    timer.cancel();
					curPiece.setShape(Tetrominoe.NoShape);
					gamecycle.cancel();
                    Platform.runLater(() -> top.setText("Times Up!"));
					server.sendIntToClient(numLinesRemoved);
					server.sendIntToClient(numLinesRemoved);
					server.sendIntToClient(numLinesRemoved);
					int clientsocre = server.receiveIntFromClient();
					clientsocre = server.receiveIntFromClient();
					clientsocre = server.receiveIntFromClient();
					int x = clientsocre;
					Platform.runLater(() -> yourscore.setText(String.valueOf(x)));
					if(clientsocre == numLinesRemoved)
						Platform.runLater(() -> top.setText("Times Up!"));
					else if(clientsocre > numLinesRemoved)
						Platform.runLater(() -> top.setText("You Lose!"));
					else if(clientsocre < numLinesRemoved)
						Platform.runLater(() -> top.setText("You Win!"));
                }
            }
        }, 0, 1000);
	}
	
	private void start() {
		next1shape.setRandomShape();
		next2shape.setRandomShape();
		next3shape.setRandomShape();
		next4shape.setRandomShape();
		next5shape.setRandomShape();
		gameStarted = true;
		
        curPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
		yourboard = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            yourboard[i] = Tetrominoe.NoShape;
        }

        clearBoard();
        newPiece();
		gamecycle = new Timer();
		gamecycle.schedule(new Gamecycle(), 0, PERIOD_INTERVAL);
	}
	
	private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Tetrominoe.NoShape;
        }
    }
	
	private void newPiece() {
        curPiece.setShape(next1shape.getShape());
		nextshapes();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();
		
		if (!tryMove(curPiece, curX, curY, true)) {
            curPiece.setShape(Tetrominoe.NoShape);
            gamecycle.cancel();
			timer.cancel();
			Platform.runLater(() -> top.setText("You Lose!"));
			for(int i = 0; i < 100; i++)
				server.sendIntToClient(999);
        }
	}
	
	private void nextshapes() {
		Platform.runLater(() -> mynext1.getChildren().clear());
		Platform.runLater(() -> mynext2.getChildren().clear());
		Platform.runLater(() -> mynext3.getChildren().clear());
		Platform.runLater(() -> mynext4.getChildren().clear());
		Platform.runLater(() -> mynext5.getChildren().clear());
		if(gameStarted){
			next1shape.setShape(next2shape.getShape());
			next2shape.setShape(next3shape.getShape());
			next3shape.setShape(next4shape.getShape());
			next4shape.setShape(next5shape.getShape());
			next5shape.setRandomShape();
		}
		drawSmallSquares(mynext1, next1shape);
		drawSmallSquares(mynext2, next2shape);
		drawSmallSquares(mynext3, next3shape);
		drawSmallSquares(mynext4, next4shape);
		drawSmallSquares(mynext5, next5shape);
		drawSmallSquares(yournext1, yournext1shape);
		drawSmallSquares(yournext2, yournext2shape);
		drawSmallSquares(yournext3, yournext3shape);
		drawSmallSquares(yournext4, yournext4shape);
		drawSmallSquares(yournext5, yournext5shape);
	}

	private class Gamecycle extends TimerTask {   
		public void run() {   
			//System.out.println("________");
			doGameCycle();
		}	   
	}
	
	private void doGameCycle() {
		boardint = boardInt();
		server.sendIntToClient(-99);
		for(int i = 0; i < 200; i++){
			server.sendIntToClient(boardint[i]);
		}
		server.sendIntToClient(numLinesRemoved);
		if(server.receiveIntFromClient() == -99){
			for(int i = 0; i < 200; i++){
				yboardint[i] = server.receiveIntFromClient();
			}
			yourboard = boardTet(yboardint);
			int a = server.receiveIntFromClient();
			Platform.runLater(() -> yourscore.setText(String.valueOf(a)));
		}
		else if(server.receiveIntFromClient() == 999){
			curPiece.setShape(Tetrominoe.NoShape);
            gamecycle.cancel();
			timer.cancel();
			Platform.runLater(() -> top.setText("You Win!"));
		}
		
		Platform.runLater(() -> yournext1.getChildren().clear());
		Platform.runLater(() -> yournext2.getChildren().clear());
		Platform.runLater(() -> yournext3.getChildren().clear());
		Platform.runLater(() -> yournext4.getChildren().clear());
		Platform.runLater(() -> yournext5.getChildren().clear());
		drawSmallSquares(yournext1, yournext1shape);
		drawSmallSquares(yournext2, yournext2shape);
		drawSmallSquares(yournext3, yournext3shape);
		drawSmallSquares(yournext4, yournext4shape);
		drawSmallSquares(yournext5, yournext5shape);
		
        update();
        drawing();
    }
	
	private void update() {
		if (isPaused) {
            return;
        }
		if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }
	
	public void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1, true)) {
            pieceDropped();
        }
    }
	
	public boolean tryMove(Shape newPiece, int newX, int newY, boolean draw) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }

            if (shapeAt(x, y, 1) != Tetrominoe.NoShape) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
		
		if (draw) {
			curY = newY;
			drawing();
		}
		return true;
    }
	
	private int[] boardInt(){
		int[] intboard = new int[200];
		for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape1 = shapeAt(j, BOARD_HEIGHT - i - 1, 1);

                if (shape1 != Tetrominoe.NoShape) {
					Shape s = new Shape();
					s.setShape(shape1);
                    intboard[((BOARD_HEIGHT - i - 1) * BOARD_WIDTH) + j] = shapetoint(s);
                }
            }
        }
		return intboard;
	}
	
	private Tetrominoe[] boardTet(int[] t){
		Tetrominoe[] temp = new Tetrominoe[200];
		for (int i = 0; i < 200; i++) {
			temp[i] = inttoshape(t[i]).getShape();
        }
		return temp;
	}
	
	private Tetrominoe shapeAt(int x, int y, int i) {
		if(i == 1)
			return board[(y * BOARD_WIDTH) + x];
		else
			return yourboard[(y * BOARD_WIDTH) + x];
    }
	
	private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
            //yourboard[(y * BOARD_WIDTH) + x] = ycurPiece.getShape();
        }

        removeFullLines();
		//System.out.println(1);
        if (!isFallingFinished) {
            newPiece();
        }
		boardint = boardInt();
		server.sendIntToClient(-99);
		for(int i = 0; i < 200; i++){
			server.sendIntToClient(boardint[i]);
		}
		server.sendIntToClient(numLinesRemoved);
		
    }
	
	private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i, 1) == Tetrominoe.NoShape) {
					lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1, 1);
                        //yourboard[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1, 2);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;

            Platform.runLater(() -> myscore.setText(String.valueOf(numLinesRemoved)));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
		
    }
	
	private void drawing() {
		Platform.runLater(() -> mymain.getChildren().clear());
		Platform.runLater(() -> yourmain.getChildren().clear());
		
		drawline();
		
        int boardTop = 600 - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape1 = shapeAt(j, BOARD_HEIGHT - i - 1, 1);
                Tetrominoe shape2 = shapeAt(j, BOARD_HEIGHT - i - 1, 2);

                if (shape1 != Tetrominoe.NoShape) {
                    drawSquare(j * squareWidth(), boardTop + i * squareHeight(), shape1, 1.0, 1);
                }
				if (shape2 != Tetrominoe.NoShape) {
                    drawSquare(j * squareWidth(), boardTop + i * squareHeight(), shape2, 1.0, 2);
                }
				
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
				
				//System.out.println("old" + curY);
                drawSquare(x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape(), 1.0, 1);
				int newY = curY;

				while (newY > 0) {
					if (!tryMove(curPiece, curX, newY - 1, false)) {
						break;
					}
					newY--;
				}
				y = newY - curPiece.y(i);
				//System.out.println("new" + curY);
                drawSquare(x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape(), 0.2, 1);
            }
        }
		if (ycurPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; i++) {
                int x = ycurX + ycurPiece.x(i);
                int y = ycurY - ycurPiece.y(i);
				
				//System.out.println("old" + curY);
                drawSquare(x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), ycurPiece.getShape(), 1.0, 2);
				int newY = ycurY;

				/*while (newY > 0) {
					if (!tryMove(ycurPiece, ycurX, newY - 1, false)) {
						break;
					}
					newY--;
				}
				y = newY - ycurPiece.y(i);
				//System.out.println("new" + curY);
                drawSquare(x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), ycurPiece.getShape(), 0.2, 2);*/
            }
        }
	}
	
	private void drawline() {
		for (int i = 30; i < 300; i += 30) {
			Line line = new Line();
			line.setStartX((double)i);
			line.setStartY(0);
			line.setEndX((double)i);
			line.setStartY(600);
			line.setStroke(Color.rgb(255, 255, 255, 0.5));
			line.getStrokeDashArray().addAll(5d);

			Platform.runLater(() -> mymain.getChildren().addAll(line));
			Platform.runLater(() -> yourmain.getChildren().addAll(line));
		}
		for (int i = 30; i < 600; i += 30) {
			Line line = new Line();
			line.setStartY((double)i);
			line.setStartX(0);
			line.setEndY((double)i);
			line.setStartX(300);
			line.setStroke(Color.rgb(255, 255, 255, 0.5));
			line.getStrokeDashArray().addAll(5d);

			Platform.runLater(() -> mymain.getChildren().addAll(line));
			Platform.runLater(() -> yourmain.getChildren().addAll(line));
		}
	}
	
	private int squareWidth() {
        return 300 / BOARD_WIDTH;
    }

    private int squareHeight() {
        return 600 / BOARD_HEIGHT;
    }
	
	private void drawSquare(int x, int y, Tetrominoe shape, double alpha, int i) {

		Shape s = new Shape();
		s.setShape(shape);

        Color colors[] = {Color.rgb(0, 0, 0), Color.rgb(255, 0, 0),
						  Color.rgb(0, 255, 0), Color.rgb(0, 255, 255),
						  Color.rgb(255, 0, 255), Color.rgb(255, 255, 0),
						  Color.rgb(255, 128, 0), Color.rgb(0, 0, 255)
        };
		
		Rectangle in = new Rectangle();
		in.setX(x + 1);
		in.setY(y + 1);
		in.setWidth(squareWidth() - 2);
		in.setHeight(squareHeight() - 2);
		if (alpha > 0.5) {
			in.setFill(colors[shapetoint(s)]);
		} else {
			Color temp = colors[shapetoint(s)];
			in.setFill(Color.color(temp.getRed(), temp.getGreen(), temp.getBlue(), alpha));
		}
		if(i == 1)
			Platform.runLater(() -> mymain.getChildren().addAll(in));
		else if(i == 2)
			Platform.runLater(() -> yourmain.getChildren().addAll(in));
    }
	
	private void drawSmallSquares(Pane p, Shape s){
		
		Tetrominoe shape = s.getShape();
		Color colors[] = {Color.rgb(0, 0, 0), Color.rgb(255, 0, 0),
						  Color.rgb(0, 255, 0), Color.rgb(0, 255, 255),
						  Color.rgb(255, 0, 255), Color.rgb(255, 255, 0),
						  Color.rgb(255, 128, 0), Color.rgb(0, 0, 255)
        };
		if (shape != Tetrominoe.NoShape) {
            for (int i = 0; i < 4; i++) {
				int x = 40 + s.x(i) * 18;
                int y = 33 + s.y(i) * 18;
				Rectangle in = new Rectangle();
				in.setX(x + 1);
				in.setY(y + 1);
				in.setWidth(16);
				in.setHeight(16);
				in.setFill(colors[shapetoint(s)]);
				
				Platform.runLater(() -> p.getChildren().addAll(in));
                
            }
        }
	}
	
	public void dropDown() {
        int newY = curY;

        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1, true)) {
				break;
            }
            newY--;
        }
        pieceDropped();
    }
	
	private void pause() {
        isPaused = !isPaused;

        if (isPaused) {
            top.setText("paused");
        } else {
            myscore.setText(String.valueOf(numLinesRemoved));
        }
        drawing();
    }
	
	private void store(Shape curPiece) {
		Shape temp = new Shape();
		if (lock.getShape() == Tetrominoe.NoShape) {
			lock.setShape(curPiece.getShape());
			newPiece();
		} 
		else {
			temp.setShape(curPiece.getShape());
			curPiece.setShape(lock.getShape());
			lock.setShape(temp.getShape());
			curY = BOARD_HEIGHT - 1 + curPiece.minY();
		}
		myhold.getChildren().clear();
		drawSmallSquares(myhold, lock);
	}
	
	public static void display(Pane temp, Yourmainclass yourmain) {
		/*for (int i = 0; i < temp.getChildren().size(); i++) {
			yourmain.getChildren().add(temp.getChildren().get(i));
		}*/
	}
}
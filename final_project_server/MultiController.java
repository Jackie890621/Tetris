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
    @FXML public Pane mymain;
    @FXML Pane mynext1;
    @FXML Pane mynext2;
    @FXML Pane mynext3;
    @FXML Pane mynext4;
    @FXML Pane mynext5;
    @FXML Label myscore;
    @FXML Button mystart;
	@FXML Label top;
    @FXML Pane yourhold;
    @FXML public Pane yourmain;
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
	public Shape next1shape = new Shape();
	public Shape next2shape = new Shape();
	public Shape next3shape = new Shape();
	public Shape next4shape = new Shape();
	public Shape next5shape = new Shape();
	private Shape lock = new Shape();
    private Tetrominoe[] board;
	//private Tetrominoe[] yourboard;
	private boolean isFallingFinished = false;
	private boolean isPaused = false;
	private boolean gameStarted = false;
	public int curX = 0;
    public int curY = 0;
	

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
		server.receiveMessageFromClient(yourmain);
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
		//yourboard = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newPiece();
		gamecycle = new Timer();
		gamecycle.schedule(new Gamecycle(), 0, PERIOD_INTERVAL);
	}
	
	private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Tetrominoe.NoShape;
			//yourboard[i] = Tetrominoe.NoShape;
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
	}

	private class Gamecycle extends TimerTask {   
		public void run() {   
			//System.out.println("________");
			doGameCycle();
		}	   
	}
	
	private void doGameCycle() {
		server.sendMessageToClient(mymain);
		
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

            if (shapeAt(x, y) != Tetrominoe.NoShape) {
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
	
	private Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }
	
	private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }

        removeFullLines();
		//System.out.println(1);
        if (!isFallingFinished) {
            newPiece();
        }
    }
	
	private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Tetrominoe.NoShape) {
					lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
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
		
		drawline();
		
        int boardTop = 600 - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != Tetrominoe.NoShape) {
                    drawSquare(j * squareWidth(), boardTop + i * squareHeight(), shape, 1.0);
                }
				
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
				
				//System.out.println("old" + curY);
                drawSquare(x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape(), 1.0);
				int newY = curY;

				while (newY > 0) {
					if (!tryMove(curPiece, curX, newY - 1, false)) {
						break;
					}
					newY--;
				}
				y = newY - curPiece.y(i);
				//System.out.println("new" + curY);
                drawSquare(x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape(), 0.2);
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
	
	private void drawSquare(int x, int y, Tetrominoe shape, double alpha) {

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
			in.setFill(colors[shape.ordinal()]);
		} else {
			Color temp = colors[shape.ordinal()];
			in.setFill(Color.color(temp.getRed(), temp.getGreen(), temp.getBlue(), alpha));
		}
		Platform.runLater(() -> mymain.getChildren().addAll(in));
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
				in.setFill(colors[shape.ordinal()]);
				
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
	
	public static void display(Pane temp, Pane yourmain) {
		for (int i = 0; i < temp.getChildren().size(); i++) {
			yourmain.getChildren().add(temp.getChildren().get(i));
		}
	}
}
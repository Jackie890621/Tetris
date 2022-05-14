package com.tetris;

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

public class TetrisController {
	@FXML Label top;
	@FXML Label score;
	@FXML Pane main;
	@FXML Pane hold;
	@FXML Pane next1;
	@FXML Pane next2;
	@FXML Pane next3;
	@FXML Pane next4;
	@FXML Pane next5;
	@FXML Button single;
	@FXML Button multi;
	@FXML Button start;
	@FXML Button back;
	private Timer timer;
	private Timer gamecycle;
	
	private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
	private final int PERIOD_INTERVAL = 500;
	private int numLinesRemoved = 0;
	public Shape curPiece;
	private Shape lock = new Shape();
    private Tetrominoe[] board;
	private boolean isFallingFinished = false;
	private boolean isPaused = false;
	public int curX = 0;
    public int curY = 0;
	
	public void initialize() {
		single.setVisible(true);
		multi.setVisible(true);
		
		top.setText("2:00");
		top.setVisible(false);
		score.setVisible(false);
		back.setVisible(false);
		start.setVisible(false);
		start.setDisable(false);
		
		main.setStyle("");
		next1.setStyle("");
		next2.setStyle("");
		next3.setStyle("");
		next4.setStyle("");
		next5.setStyle("");
		hold.setStyle("");
		//addKeyListener(new TAdapter
		lock.setShape(Tetrominoe.NoShape);		
	}
	
	@FXML
	private void singleclicked(ActionEvent e) {
		single.setVisible(false);
		multi.setVisible(false);
				
		top.setVisible(true);
		score.setVisible(true);
		back.setVisible(true);
		start.setVisible(true);
		main.setStyle("-fx-background-color: " + "GAINSBORO");
		next1.setStyle("-fx-background-color: " + "GAINSBORO");
		next2.setStyle("-fx-background-color: " + "GAINSBORO");
		next3.setStyle("-fx-background-color: " + "GAINSBORO");
		next4.setStyle("-fx-background-color: " + "GAINSBORO");
		next5.setStyle("-fx-background-color: " + "GAINSBORO");
		hold.setStyle("-fx-background-color: " + "GAINSBORO");
		
	}
	
	@FXML
	private void backclicked(ActionEvent e) {
		this.timer.cancel();
		this.timer.purge();
		gamecycle.cancel();
		gamecycle.purge();
		clearBoard();
		main.getChildren().clear();
		main.getChildren().addAll(single, multi);
		initialize();
	}
	
	@FXML
	private void startclicked(ActionEvent ev) {
		start.setDisable(true);
		countdown();
		start();
		Tetris.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {			
			
			@Override
			public void handle(KeyEvent e) {
				
				if (curPiece.getShape() == Tetrominoe.NoShape) {
					return;
				}
				if (e.getCode() == KeyCode.UP) {
					tryMove(curPiece.rotateRight(), curX, curY);
				} else if (e.getCode() == KeyCode.DOWN) {
					oneLineDown();
				} else if (e.getCode() == KeyCode.LEFT) {
					tryMove(curPiece, curX - 1, curY);
				} else if (e.getCode() == KeyCode.RIGHT) {
					tryMove(curPiece, curX + 1, curY);
				} else if (e.getCode() == KeyCode.CONTROL) {
					tryMove(curPiece.rotateLeft(), curX, curY);
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
	
	private void countdown() {
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new TimerTask() {
            int i = 120;
			String time;
			
            public void run() {
				if (i % 60 < 10) {
					time = String.valueOf(i / 60) + ":0" + String.valueOf(i % 60);
				} else {
					time = String.valueOf(i / 60) + ":" + String.valueOf(i % 60);
				}
				Platform.runLater(() -> top.setText(time));
                if (!isPaused) {
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
        curPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

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
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();
		
		if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape);
            gamecycle.cancel();
			this.timer.cancel();
			Platform.runLater(() -> top.setText("You Lose!"));
        }
	}
	
	private class Gamecycle extends TimerTask {   
		public void run() {   
			//System.out.println("________");
			doGameCycle();
		}	   
	}
	
	private void doGameCycle() {
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
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }
	
	public boolean tryMove(Shape newPiece, int newX, int newY) {
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
        curY = newY;

        drawing();

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

            Platform.runLater(() -> score.setText(String.valueOf(numLinesRemoved)));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }
	
	private void drawing() {
		Platform.runLater(() -> main.getChildren().clear());
        int boardTop = 600 - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != Tetrominoe.NoShape) {
                    drawSquare(j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);

                drawSquare(x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
            }
        }
	}
	
	private int squareWidth() {
        return 300 / BOARD_WIDTH;
    }

    private int squareHeight() {
        return 600 / BOARD_HEIGHT;
    }
	
	private void drawSquare(int x, int y, Tetrominoe shape) {

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
		in.setFill(colors[shape.ordinal()]);
		
		Platform.runLater(() -> main.getChildren().addAll(in));
    }
	
	public void dropDown() {
        int newY = curY;

        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
				break;
            }
            newY--;
        }
        pieceDropped();
    }
	
	private void pause() {
        isPaused = !isPaused;

        if (isPaused) {
            score.setText("paused");
        } else {
            score.setText(String.valueOf(numLinesRemoved));
        }
        drawing();
    }
	
	private void store(Shape curPiece) {
		Shape temp = new Shape();
		if (lock.getShape() == Tetrominoe.NoShape) {
			lock = curPiece;
			newPiece();
		} else {
			temp = curPiece;
			curPiece = lock;
			lock = temp;
			curY = BOARD_HEIGHT - 1 + curPiece.minY();
		}
	}
}
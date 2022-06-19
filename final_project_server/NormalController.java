package com.tetris;

import com.tetris.Shape.Tetrominoe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

public class NormalController {
	@FXML Label top;
	@FXML Label score;
	@FXML Pane main;
	@FXML Pane hold;
	@FXML Pane next1;
	@FXML Pane next2;
	@FXML Pane next3;
	@FXML Pane next4;
	@FXML Pane next5;
	@FXML Button start;
	@FXML Button back;
	private Timer timer;
	private Timer gamecycle;
	
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
	private boolean isFallingFinished = false;
	private boolean isPaused = false;
	private boolean gameStarted = false;
	public int curX = 0;
    public int curY = 0;
	int combo = -1;
	int combo4 = -1;
	
	public void initialize() {
		
		top.setText("2:00");
		
		main.setStyle("");
		next1.setStyle("");
		next2.setStyle("");
		next3.setStyle("");
		next4.setStyle("");
		next5.setStyle("");
		hold.setStyle("");
		//addKeyListener(new TAdapter
		lock.setShape(Tetrominoe.NoShape);
		next1shape.setShape(Tetrominoe.NoShape);
		next2shape.setShape(Tetrominoe.NoShape);
		next3shape.setShape(Tetrominoe.NoShape);
		next4shape.setShape(Tetrominoe.NoShape);
		next5shape.setShape(Tetrominoe.NoShape);	
		
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
		timer.cancel();
		timer.purge();
		gamecycle.cancel();
		gamecycle.purge();
		Normal.st.close();
	}
	
	@FXML
	private void startclicked(ActionEvent ev) {
		start.setDisable(true);
		countdown();
		start();
		Normal.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {			
			
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
        board = new Tetrominoe[10 * 20];

        clearBoard();
        newPiece();
		gamecycle = new Timer();
		gamecycle.schedule(new Gamecycle(), 0, PERIOD_INTERVAL);
	}
	
	private void clearBoard() {
        for (int i = 0; i < 20 * 10; i++) {
            board[i] = Tetrominoe.NoShape;
        }
    }
	
	private void newPiece() {
        curPiece.setShape(next1shape.getShape());
		nextshapes();
        curX = 10 / 2;
        curY = 20 - 1 + curPiece.minY();
		
		if (!tryMove(curPiece, curX, curY, true)) {
            curPiece.setShape(Tetrominoe.NoShape);
            gamecycle.cancel();
			this.timer.cancel();
			Platform.runLater(() -> top.setText("You Lose!"));
        }
	}
	
	private void nextshapes() {
		Platform.runLater(() -> next1.getChildren().clear());
		Platform.runLater(() -> next2.getChildren().clear());
		Platform.runLater(() -> next3.getChildren().clear());
		Platform.runLater(() -> next4.getChildren().clear());
		Platform.runLater(() -> next5.getChildren().clear());
		if(gameStarted){
			next1shape.setShape(next2shape.getShape());
			next2shape.setShape(next3shape.getShape());
			next3shape.setShape(next4shape.getShape());
			next4shape.setShape(next5shape.getShape());
			next5shape.setRandomShape();
		}
		drawSmallSquares(next1, next1shape);
		drawSmallSquares(next2, next2shape);
		drawSmallSquares(next3, next3shape);
		drawSmallSquares(next4, next4shape);
		drawSmallSquares(next5, next5shape);
	}
	
	private class Gamecycle extends TimerTask {   
		public void run() {   
			update();
			drawing();
		}	   
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

            if (x < 0 || x >= 10 || y < 0 || y >= 20) {
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
        return board[(y * 10) + x];
    }
	
	private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
			board[(y * 10) + x] = curPiece.getShape();
        }
        removeFullLines();
		//System.out.println(1);
        if (!isFallingFinished) {
            newPiece();
        }
    }
	
	private void removeFullLines() {
        int numFullLines = 0;
		boolean flag = true;

        for (int i = 20 - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < 10; j++) {
                if (shapeAt(j, i) == Tetrominoe.NoShape) {
					lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
				flag = false;
                numFullLines++;
                for (int k = i; k < 20 - 1; k++) {
                    for (int j = 0; j < 10; j++) {
                        board[(k * 10) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }
		if (flag) {
			combo = -1;
		}
		//System.out.println(numFullLines);
        if (numFullLines > 0 && numFullLines < 4) {
			combo++;
			combo4 = -1;
			
			if (combo == 1 || combo == 2) {
				numLinesRemoved += numFullLines;
			} else if (combo == 3 || combo == 4) {
				numLinesRemoved += numFullLines * 2;
			} else if (combo == 5 || combo == 6) {
				numLinesRemoved += numFullLines * 3;
			} else if (combo > 6) {
				numLinesRemoved += numFullLines * 4;
			} else {
				numLinesRemoved += numFullLines - 1;
			}
			Platform.runLater(() -> score.setText(String.valueOf(numLinesRemoved)));
			isFallingFinished = true;
			curPiece.setShape(Tetrominoe.NoShape);
        } else if (numFullLines == 4) {
			combo4++;
			System.out.println(combo);
			if (combo4 == 0) {
				numLinesRemoved += numFullLines;
			} else {
				numLinesRemoved += 6;
			}
			Platform.runLater(() -> score.setText(String.valueOf(numLinesRemoved)));
			isFallingFinished = true;
			curPiece.setShape(Tetrominoe.NoShape);
		}
    }
	
	private void drawing() {
		Platform.runLater(() -> main.getChildren().clear());
		
		drawline();

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                Tetrominoe shape = shapeAt(j, 20 - i - 1);

                if (shape != Tetrominoe.NoShape) {
                    drawSquare(j * 30, i * 30, shape, 1.0);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(x * 30, (20 - y - 1) * 30, curPiece.getShape(), 1.0);
				
				int newY = curY;
				while (newY > 0) {
					if (!tryMove(curPiece, curX, newY - 1, false)) {
						break;
					}
					newY--;
				}
				y = newY - curPiece.y(i);
                drawSquare(x * 30, (20 - y - 1) * 30, curPiece.getShape(), 0.2);
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

			Platform.runLater(() -> main.getChildren().addAll(line));
		}
		for (int i = 30; i < 600; i += 30) {
			Line line = new Line();
			line.setStartY((double)i);
			line.setStartX(0);
			line.setEndY((double)i);
			line.setStartX(300);
			line.setStroke(Color.rgb(255, 255, 255, 0.5));
			line.getStrokeDashArray().addAll(5d);

			Platform.runLater(() -> main.getChildren().addAll(line));
		}
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
		in.setWidth(28);
		in.setHeight(28);
		if (alpha > 0.5) {
			in.setFill(colors[shape.ordinal()]);
		} else {
			Color temp = colors[shape.ordinal()];
			in.setFill(Color.color(temp.getRed(), temp.getGreen(), temp.getBlue(), alpha));
		}
		Platform.runLater(() -> main.getChildren().addAll(in));
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
            top.setText("pause");
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
			curY = 20 - 1 + curPiece.minY();
		}
		hold.getChildren().clear();
		drawSmallSquares(hold, lock);
	}
}
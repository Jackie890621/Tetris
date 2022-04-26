import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
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
	private void startclicked(ActionEvent e) {
		start.setDisable(true);
		countdown();
	}
	
	@FXML
	private void backclicked(ActionEvent e) {
		initialize();
	}
	
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
	}
	
	private void countdown() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
            int i = 120;
			String time;
			
            public void run() {
				if (i % 60 < 10) {
					time = String.valueOf(i / 60) + ":0" + String.valueOf(i % 60);
				} else {
					time = String.valueOf(i / 60) + ":" + String.valueOf(i % 60);
				}
				Platform.runLater(() -> top.setText(time));
                i--;
                if (i < 0) {
                    timer.cancel();
                    Platform.runLater(() -> top.setText("Times Up!"));
                }
            }
        }, 0, 1000);
	}
}
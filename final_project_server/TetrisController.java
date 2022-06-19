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

public class TetrisController {

	@FXML Pane main;
	@FXML Button single;
	@FXML Button multi;
	@FXML Button back;
	@FXML Button obstacle;
    @FXML Button challenge;
	@FXML Button marathon;
	@FXML Button normal;
    @FXML Button racing;
	
	public void initialize() {
		single.setVisible(true);
		multi.setVisible(true);
		back.setVisible(false);
		
		normal.setVisible(false);
		racing.setVisible(false);
		marathon.setVisible(false);
		obstacle.setVisible(false);
		challenge.setVisible(false);	
	}
	
	@FXML
	private void singleclicked(ActionEvent e) {
		single.setVisible(false);
		multi.setVisible(false);
		normal.setVisible(true);
		racing.setVisible(true);
		marathon.setVisible(true);
		obstacle.setVisible(true);
		challenge.setVisible(true);	
		back.setVisible(true);		
	}
	
	@FXML
    void backclicked(ActionEvent event) {
		initialize();
    }
	
	@FXML
	private void multiclicked(ActionEvent e) throws Exception {
		Multi mul = new Multi();
		mul.showWindow();
	}
	
	@FXML
    void normalclicked(ActionEvent event)  throws Exception {
		Normal nor = new Normal();
		nor.showWindow();
    }

    @FXML
    void racingclicked(ActionEvent event)  throws Exception {
		Racing rac = new Racing();
		rac.showWindow();
    }
	
	@FXML
    void marathonclicked(ActionEvent event)  throws Exception {
		Marathon mar = new Marathon();
		mar.showWindow();
    }
	
	@FXML
    void obstacleclicked(ActionEvent event)  throws Exception {
		Obstacle obs = new Obstacle();
		obs.showWindow();
    }

    @FXML
    void challengeclicked(ActionEvent event)  throws Exception {
		Challenge cha = new Challenge();
		cha.showWindow();
    }
}
	
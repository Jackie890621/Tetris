package com.tetris;

import com.tetris.Shape.Tetrominoe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.*;
import javafx.event.*;

public class Racing extends Application {
	public static Scene scene;
	public static Stage st = new Stage();
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("racing.fxml"));
		scene = new Scene(root);
		stage.setTitle("racing"); // displayed in window's title bar
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void showWindow() throws Exception {
		start(st);
	}
}
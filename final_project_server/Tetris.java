package com.tetris;

import com.tetris.Shape.Tetrominoe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.*;
import javafx.event.*;

public class Tetris extends Application {
	public static Scene scene;
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("Tetris.fxml"));
		scene = new Scene(root);
		stage.setTitle("Tetris"); // displayed in window's title bar
		stage.setScene(scene);
		stage.show();
	}
	
	Scene getScene() {
		return scene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
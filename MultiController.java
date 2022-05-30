package com.tetris;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.tetris.Shape.Tetrominoe;
import com.tetris.Server;

public class MultiController{
	
	ServerSocket ss;
	DataInputStream input;
	DataOutputStream output;

	@FXML
	private void createclicked(ActionEvent e) {
		 
	}
	
	@FXML
	private void joinclicked(ActionEvent e) {
		
	}
	
}
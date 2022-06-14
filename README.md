# Tetris
Java
## Compile
```
javac --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml -d . *.java
```
* 這樣東西就都會跑到com/tetris裡面

## Execution
```
java --module-path $env:PATH_TO_FX --add-modules=javafx.controls,javafx.fxml com.tetris.Tetris
```
* 檔名記得前面要加com.tetris.
* Tetris.fxml也要放在com/tetris裡面，而且controller的路徑也要改成com.tetris.TetrisController
* 玩的時候記得鍵盤要切成英文的

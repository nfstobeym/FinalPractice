import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberSliderAppV1 extends Application {
    private int[][] board = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private int rBlank = 2;
    private int cBlank = 2;
    private int moves = 0;
    private int limit = 20;
    private boolean over = false;
    private List<StateSnapshot> history = new ArrayList<>();
    private Button[][] btns = new Button[3][3];
    private Label statusLbl = new Label("Status: Ready");
    private Label moveLbl = new Label("Moves: 0 / 20");
    private TextField limitField = new TextField("20");

    private static class StateSnapshot {
        int[][] data;
        int r, c;
        StateSnapshot(int[][] m, int row, int col) {
            data = new int[3][3];
            for(int i=0; i<3; i++) System.arraycopy(m[i], 0, data[i], 0, 3);
            r = row;
            c = col;
        }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Slider Game V1");
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(10));
        Button playBtn = new Button("Play");
        Button resetBtn = new Button("Reset");
        Button undoBtn = new Button("Undo");
        Button setBtn = new Button("Set Max");
        limitField.setPrefWidth(50);
        top.getChildren().addAll(playBtn, resetBtn, undoBtn, new Label("Max:"), limitField, setBtn);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5);
        pane.setVgap(5);
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                Button b = new Button();
                b.setPrefSize(75, 75);
                final int row = i;
                final int col = j;
                b.setOnAction(e -> handleMove(row, col));
                btns[i][j] = b;
                pane.add(b, j, i);
            }
        }

        VBox bottom = new VBox(5);
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(statusLbl, moveLbl);

        playBtn.setOnAction(e -> playNew());
        resetBtn.setOnAction(e -> resetGame());
        undoBtn.setOnAction(e -> undoMove());
        setBtn.setOnAction(e -> {
            try {
                int val = Integer.parseInt(limitField.getText().trim());
                if(val > 0) { limit = val; refreshUI(); }
            } catch(Exception ignored) {}
        });

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(pane);
        root.setBottom(bottom);
        refreshUI();
        stage.setScene(new Scene(root, 330, 400));
        stage.show();
    }

    private void handleMove(int r, int c) {
        if(over) return;
        if(Math.abs(r - rBlank) + Math.abs(c - cBlank) == 1) {
            history.add(new StateSnapshot(board, rBlank, cBlank));
            board[rBlank][cBlank] = board[r][c];
            board[r][c] = 0;
            rBlank = r;
            cBlank = c;
            moves++;
            checkState();
            refreshUI();
        }
    }

    private void playNew() {
        List<Integer> nums = new ArrayList<>();
        for(int i=0; i<=8; i++) nums.add(i);
        do {
            Collections.shuffle(nums);
        } while(!isSolvable(nums) || isSorted(nums));
        int idx = 0;
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                board[i][j] = nums.get(idx++);
                if(board[i][j] == 0) { rBlank = i; cBlank = j; }
            }
        }
        moves = 0;
        history.clear();
        over = false;
        statusLbl.setText("Status: Playing");
        refreshUI();
    }

    private void resetGame() {
        int[][] def = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        for(int i=0; i<3; i++) System.arraycopy(def[i], 0, board[i], 0, 3);
        rBlank = 2; cBlank = 2; moves = 0; history.clear(); over = false;
        statusLbl.setText("Status: Reset");
        refreshUI();
    }

    private void undoMove() {
        if(over || history.isEmpty()) return;
        StateSnapshot last = history.remove(history.size() - 1);
        for(int i=0; i<3; i++) System.arraycopy(last.data[i], 0, board[i], 0, 3);
        rBlank = last.r; cBlank = last.c; moves--;
        statusLbl.setText("Status: Undone");
        refreshUI();
    }

    private boolean isSolvable(List<Integer> list) {
        int inv = 0;
        List<Integer> sub = new ArrayList<>(list);
        sub.remove(Integer.valueOf(0));
        for(int i=0; i<sub.size(); i++) {
            for(int j=i+1; j<sub.size(); j++) {
                if(sub.get(i) > sub.get(j)) inv++;
            }
        }
        return inv % 2 == 0;
    }

    private boolean isSorted(List<Integer> list) {
        return list.equals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 0));
    }

    private void checkState() {
        boolean win = true;
        int exp = 1;
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if(i == 2 && j == 2) { if(board[i][j] != 0) win = false; }
                else { if(board[i][j] != exp++) win = false; }
            }
        }
        if(win && moves < limit) { statusLbl.setText("Congrats, WINNER!!!"); over = true; }
        else if(moves >= limit) { statusLbl.setText("You FAILED!"); over = true; }
    }

    private void refreshUI() {
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if(board[i][j] == 0) btns[i][j].setText("");
                else btns[i][j].setText(String.valueOf(board[i][j]));
            }
        }
        moveLbl.setText("Moves: " + moves + " / " + limit);
    }
}

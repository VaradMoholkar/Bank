import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;
import java.time.*;


public class Deposit_Page {
    static int b =0;

    private static void showStyledPopup(String message) {
        Stage popup = new Stage();
        popup.setAlwaysOnTop(true);
        popup.setResizable(false);
        popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        Label msgLabel = new Label(message);
        msgLabel.setFont(new Font("Segoe UI", 16));
        msgLabel.setTextFill(Color.WHITE);
        msgLabel.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 10; -fx-padding: 15px;");

        VBox box = new VBox(msgLabel);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(box);
        scene.setFill(Color.TRANSPARENT);

        popup.setScene(scene);
        popup.setWidth(320);
        popup.setHeight(100);
        popup.setX(javafx.stage.Screen.getPrimary().getBounds().getWidth() / 2 - 160);
        popup.setY(javafx.stage.Screen.getPrimary().getBounds().getHeight() / 2 - 50);
        popup.show();

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                javafx.application.Platform.runLater(popup::close);
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    public static Scene getScene(Stage primaryStage, String rollno) {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #b3d9ff, #e6f0ff);");

        Label title = new Label("💵 Deposit Money");
        title.setFont(new Font("Segoe UI Semibold", 28));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setFont(new Font("Segoe UI", 16));
        amountField.setMaxWidth(240);

        Button depositBtn = createStyledButton("✅ Deposit");
        Button backBtn = createStyledButton("🔙 Back");

        depositBtn.setOnAction(e -> {
            String amountText = amountField.getText().trim();
            if (!amountText.isEmpty()) {
                try {
                    Connection conn = DBConnection.getConnection();
                    String selectsql = "SELECT * FROM user_table WHERE user_rollno = ?";
                    PreparedStatement prestmt = conn.prepareStatement(selectsql);
                    prestmt.setString(1,rollno);
                    ResultSet rs = prestmt.executeQuery();

                    if(rs.next()){
                        b = rs.getInt("user_balance");
                    } else{
                        System.out.println("User not found");
                    }

                } catch (Exception ex){
                    ex.printStackTrace();
                }

                try {
                    Connection conn = DBConnection.getConnection();

                    String updatesql = "UPDATE user_table SET user_balance = ? WHERE user_rollno = ?";
                    PreparedStatement updatestmt = conn.prepareStatement(updatesql);
                    updatestmt.setInt(1,b+Integer.parseInt(amountText));
                    updatestmt.setString(2, rollno);
                    updatestmt.executeUpdate();
                    updatestmt.close();

                    String p = rollno+ "_transaction_table";
                    String insertSql = "INSERT INTO "+p+" (transaction_id, balance_before_transaction, amount_deducted, balance_after_transaction,"+
                            " date_time, reciver_rollno) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertstmt = conn.prepareStatement(insertSql);
                    insertstmt.setLong(1, System.currentTimeMillis());
                    insertstmt.setInt(2, b);
                    insertstmt.setInt(3, -Integer.parseInt(amountText));
                    insertstmt.setInt(4, b+Integer.parseInt(amountText));
                    insertstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    insertstmt.setString(6, rollno);
                    insertstmt.executeUpdate();
                    insertstmt.close();
                    showStyledPopup("₹" + amountText + " deposited successfully!");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                amountField.clear();
            }
        });

        backBtn.setOnAction(e -> {

            new User(rollno).start(primaryStage);
        });

        root.getChildren().addAll(title, amountField, depositBtn, backBtn);

        Scene scene = new Scene(root, 550, 420);
        scene.getStylesheets().add(Deposit_Page.class.getResource("/style.css").toExternalForm());
        return scene;
    }

    private static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setFont(new Font("Segoe UI", 16));
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 8px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;"));
        return button;
    }
}

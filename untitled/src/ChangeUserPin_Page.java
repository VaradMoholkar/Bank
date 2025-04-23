import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;

import javafx.stage.Popup;

public class ChangeUserPin_Page {

    public static Scene getScene(Stage primaryStage, String rollno) {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f8ff, #e6f0ff);");

        Label title = new Label("🔐 Change PIN");
        title.setFont(new Font("Segoe UI Semibold", 28));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        PasswordField currentPinField = new PasswordField();
        currentPinField.setPromptText("Enter current PIN");
        currentPinField.setFont(new Font("Segoe UI", 16));
        currentPinField.setMaxWidth(240);

        PasswordField newPinField = new PasswordField();
        newPinField.setPromptText("Enter new PIN");
        newPinField.setFont(new Font("Segoe UI", 16));
        newPinField.setMaxWidth(240);

        Button changeBtn = createStyledButton("🔁 Change PIN");
        Button backBtn = createStyledButton("🔙 Back");

        changeBtn.setOnAction(e -> {
            String currentPin = currentPinField.getText().trim();
            String newPin = newPinField.getText().trim();

            if (!currentPin.isEmpty() && !newPin.isEmpty()) {
                try {
                    Connection conn = DBConnection.getConnection();
                    String checkSql = "SELECT * FROM user_table WHERE user_rollno = ? AND user_pin = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setString(1, rollno);
                    checkStmt.setString(2, currentPin);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        String updateSql = "UPDATE user_table SET user_pin = ? WHERE user_rollno = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setString(1, newPin);
                        updateStmt.setString(2, rollno);
                        updateStmt.executeUpdate();
                        updateStmt.close();

                        showPopup("PIN changed successfully.", primaryStage, backBtn);

                    } else {
                        showPopup("Incorrect current PIN.", primaryStage, backBtn);

                    }

                    rs.close();
                    checkStmt.close();
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                currentPinField.clear();
                newPinField.clear();
            }
        });

        backBtn.setOnAction(e -> {
            new User(rollno).start(primaryStage);
        });

        root.getChildren().addAll(title, currentPinField, newPinField, changeBtn, backBtn);
        Scene scene = new Scene(root, 550, 420);
        scene.getStylesheets().add(ChangeUserPin_Page.class.getResource("/style.css").toExternalForm());
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

    private static void showPopup(String message, Stage primaryStage, Button anchorButton) {
        Popup popup = new Popup();

        Label msgLabel = new Label(message);
        msgLabel.setFont(new Font("Segoe UI", 16));
        msgLabel.setTextFill(Color.WHITE);
        msgLabel.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 10; -fx-padding: 10px;");

        VBox content = new VBox(msgLabel);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: transparent;");

        popup.getContent().add(content);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        double x = primaryStage.getX() + anchorButton.localToScene(0, 0).getX() + anchorButton.getScene().getX();
        double y = primaryStage.getY() + anchorButton.localToScene(0, 0).getY() + anchorButton.getHeight() + anchorButton.getScene().getY() + 10;

        popup.show(primaryStage, x, y);

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                javafx.application.Platform.runLater(popup::hide);
            } catch (InterruptedException ignored) {}
        }).start();
    }


}


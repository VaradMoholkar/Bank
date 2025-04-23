import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;

public class User extends Application {

    private String userRollNo;
    double balance;

    public User(String rollno) {
        this.userRollNo = rollno;
    }
    @Override
    public void start(Stage stage) {
        stage.setTitle("User Dashboard");

        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #b3d9ff, #e6f0ff);");

        Label welcomeLabel = new Label("Welcome, "+userRollNo+" !");
        welcomeLabel.setFont(new Font("Segoe UI Semibold", 28));
        welcomeLabel.setTextFill(Color.web("#2C3E50"));
        welcomeLabel.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        try {
            Connection conn = DBConnection.getConnection();

            String selectSql = "SELECT * FROM user_table WHERE user_rollno = ?";
            PreparedStatement stmt = conn.prepareStatement(selectSql);
            stmt.setString(1, userRollNo);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                balance = rs.getDouble("user_balance");
            }

            stmt.close();
            rs.close();
            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Label balanceLabel = new Label("Current Balance: ₹"+balance);
        balanceLabel.setFont(new Font("Segoe UI", 20));
        balanceLabel.setTextFill(Color.web("#27AE60"));

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button transferBtn = createStyledButton("💸 Transfer Money");
        Button withdrawBtn = createStyledButton("💰 Withdraw Money");
        Button depositBtn = createStyledButton("💵 Deposit Money");
        Button pinBtn = createStyledButton("🔢 Change Pin");
        Button historyBtn = createStyledButton("📄 Transaction History");
        Button logoutBtn = createStyledButton("🚪 Logout");

        transferBtn.setOnAction(e -> {
            stage.setScene(TransferMoneyPage.getScene(stage, userRollNo));
        });

        depositBtn.setOnAction(e -> {
            stage.setScene(Deposit_Page.getScene(stage, userRollNo));
        });

        withdrawBtn.setOnAction(e -> {
            stage.setScene(Withdraw_Page.getScene(stage, userRollNo));
        });

        pinBtn.setOnAction(e -> {
            stage.setScene(ChangeUserPin_Page.getScene(stage, userRollNo));
        });

        historyBtn.setOnAction(e -> {
            stage.setScene(TransactionHistoryPage.getScene(stage, userRollNo));
        });

        logoutBtn.setOnAction(e -> {
            try {
                new Main().start((Stage) ((Button)e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        buttonBox.getChildren().addAll(transferBtn, withdrawBtn, depositBtn, historyBtn, pinBtn, logoutBtn);

        root.getChildren().addAll(welcomeLabel, balanceLabel, buttonBox);

        Scene scene = new Scene(root, 550, 420);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }


    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setFont(new Font("Segoe UI", 16));
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 8px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;"));
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

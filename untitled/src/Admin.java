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

public class Admin extends Application {

    private String adminRollNo;

    public Admin(String rollno) {
        this.adminRollNo = rollno;
    }
    @Override
    public void start(Stage stage) {
        stage.setTitle("Admin Dashboard");

        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #b3d9ff, #e6f0ff);");

        Label welcomeLabel = new Label("Welcome, Admin!");
        welcomeLabel.setFont(new Font("Segoe UI Semibold", 28));
        welcomeLabel.setTextFill(Color.web("#2C3E50"));
        welcomeLabel.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button showUserBtn = createStyledButton("👥 Show Users Table");
        Button AdminTableBtn = createStyledButton("🛠️ Show Admin Table");
        Button userBalBtn = createStyledButton("💰 Show User's Balance");
        Button pinBtn = createStyledButton("🔢 Change Pin");
        Button userHistoryBtn = createStyledButton("📄 Show Transaction History of User");
        Button logoutBtn = createStyledButton("🚪 Logout");

        showUserBtn.setOnAction(e -> {
            stage.setScene(ShowUsersPage.getScene(stage, adminRollNo));
        });

        AdminTableBtn.setOnAction(e -> {
            stage.setScene(ShowAdminsPage.getScene(stage, adminRollNo));
        });

        userBalBtn.setOnAction(e -> {
            stage.setScene(UserBalancePage.getScene(stage, adminRollNo));
        });

        pinBtn.setOnAction(e -> {
            stage.setScene(ChangeAdminPinPage.getScene(stage, adminRollNo));
        });

        userHistoryBtn.setOnAction(e -> {
            stage.setScene(AdminTransactionHistoryPage.getScene(stage, adminRollNo));
        });

        logoutBtn.setOnAction(e -> {
            try {
                new Main().start((Stage) ((Button)e.getSource()).getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        buttonBox.getChildren().addAll(showUserBtn, AdminTableBtn, userBalBtn, userHistoryBtn, pinBtn, logoutBtn);

        root.getChildren().addAll(welcomeLabel, buttonBox);

        Scene scene = new Scene(root, 550, 420);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(280);
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

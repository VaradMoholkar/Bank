import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🏦 Campus Banking Portal");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMinWidth(200);

        Tab loginTab = new Tab("🔐 Login", createLoginPane(primaryStage));
        Tab registerTab = new Tab("📝 Sign Up", createRegisterPane());
        Tab deleteTab = new Tab("❌ Delete Account", createDeletePane());

        tabPane.getTabs().addAll(loginTab, registerTab, deleteTab);

        Scene scene = new Scene(tabPane, 480, 450);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Pane createLoginPane(Stage primaryStage) {
        VBox loginBox = new VBox(20);
        loginBox.setPadding(new Insets(40));
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e9f0);");

        Label titleLabel = new Label("Welcome Back ✨");
        titleLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 24));

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "User");
        roleBox.setPromptText("👤 Select Role");
        roleBox.setMaxWidth(250);

        TextField rollField = new TextField();
        rollField.setPromptText("🎓 Roll Number");
        rollField.setMaxWidth(250);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("🔢 PIN");
        pinField.setMaxWidth(250);

        Label messageLabel = new Label();

        Button loginButton = new Button("🔓 Login");
        loginButton.setPrefWidth(250);
        loginButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
        loginButton.setOnAction(e -> {
            String role = roleBox.getValue();
            String roll = rollField.getText();
            String pin = pinField.getText();

            if (roll.isEmpty() || pin.isEmpty()) {
                messageLabel.setText("⚠️ Roll Number and PIN are required.");
                messageLabel.setTextFill(Color.CRIMSON);
            }
            else{
                if(role.equals("User")){
                    try {
                        Connection conn = DBConnection.getConnection();

                        String findSql_rollno = "SELECT * FROM user_table WHERE user_rollno = ?";
                        PreparedStatement findstmt_rollno = conn.prepareStatement(findSql_rollno);
                        findstmt_rollno.setString(1,roll);

                        ResultSet rslt_rollno = findstmt_rollno.executeQuery();

                        if(rslt_rollno.next()){
                            String findSql_pin = "SELECT * FROM user_table WHERE user_pin = ?";
                            PreparedStatement findstmt_pin = conn.prepareStatement(findSql_pin);
                            findstmt_pin.setDouble(1,Double.parseDouble(pin));

                            ResultSet rslt_pin = findstmt_pin.executeQuery();

                            if(rslt_pin.next()){
                                messageLabel.setText("✅ Logged in successfully !");
                                messageLabel.setTextFill(Color.GREEN);

                                primaryStage.close();

                                User userDashboard = new User(roll);
                                Stage userStage = new Stage();
                                userDashboard.start(userStage);
                            }
                            else {
                                messageLabel.setText("❌ Wrong Pin.");
                                messageLabel.setTextFill(Color.CRIMSON);
                            }

                            rslt_pin.close();
                        }
                        else {
                            messageLabel.setText("❌ User doesn't exist.");
                            messageLabel.setTextFill(Color.CRIMSON);
                        }

                        rslt_rollno.close();
                        findstmt_rollno.close();
                        conn.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                else{
                    try {
                        Connection conn = DBConnection.getConnection();

                        String findSql_rollno = "SELECT * FROM admin_table WHERE admin_rollno = ?";
                        PreparedStatement findstmt_rollno = conn.prepareStatement(findSql_rollno);
                        findstmt_rollno.setString(1,roll);

                        ResultSet rslt_rollno = findstmt_rollno.executeQuery();

                        if(rslt_rollno.next()){
                            String findSql_pin = "SELECT * FROM admin_table WHERE admin_pin = ?";
                            PreparedStatement findstmt_pin = conn.prepareStatement(findSql_pin);
                            findstmt_pin.setDouble(1,Double.parseDouble(pin));

                            ResultSet rslt_pin = findstmt_pin.executeQuery();

                            if(rslt_pin.next()){
                                messageLabel.setText("✅ Logged in successfully !");
                                messageLabel.setTextFill(Color.GREEN);

                                primaryStage.close();

                                Admin adminDashboard = new Admin(roll);
                                Stage adminStage = new Stage();
                                adminDashboard.start(adminStage);
                            }
                            else {
                                messageLabel.setText("❌ Wrong Pin.");
                                messageLabel.setTextFill(Color.CRIMSON);
                            }

                            rslt_pin.close();
                        }
                        else {
                            messageLabel.setText("❌ Admin doesn't exist.");
                            messageLabel.setTextFill(Color.CRIMSON);
                        }

                        rslt_rollno.close();
                        findstmt_rollno.close();
                        conn.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        loginBox.getChildren().addAll(titleLabel, roleBox, rollField, pinField, loginButton, messageLabel);
        return new StackPane(loginBox);
    }

    private Pane createRegisterPane() {
        VBox registerBox = new VBox(20);
        registerBox.setPadding(new Insets(40));
        registerBox.setAlignment(Pos.CENTER);
        registerBox.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e9f0);");

        Label titleLabel = new Label("Create Your Account 🗾");
        titleLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 24));

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "User");
        roleBox.setPromptText("👤 Select Role");
        roleBox.setMaxWidth(250);

        TextField nameField = new TextField();
        nameField.setPromptText("📝 Full Name");
        nameField.setMaxWidth(250);

        TextField phoneField = new TextField();
        phoneField.setPromptText("📞 Phone Number");
        phoneField.setMaxWidth(250);

        TextField rollField = new TextField();
        rollField.setPromptText("🎓 Roll Number");
        rollField.setMaxWidth(250);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("🔢 Choose PIN");
        pinField.setMaxWidth(250);

        TextField balanceField = new TextField();
        balanceField.setPromptText("💰 Initial Balance");
        balanceField.setMaxWidth(250);

        Label messageLabel = new Label();

        roleBox.setOnAction(e -> {
            String selectedRole = roleBox.getValue();
            if ("Admin".equals(selectedRole)) {
                balanceField.setVisible(false);
            } else {
                balanceField.setVisible(true);
            }
        });

        Button registerButton = new Button("🆕 Create Account");
        registerButton.setPrefWidth(250);
        registerButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
        registerButton.setOnAction(e -> {
            String role = roleBox.getValue();
            String name = nameField.getText();
            String phone = phoneField.getText();
            String roll = rollField.getText();
            String pin = pinField.getText();
            String balance = balanceField.getText();

            if (role == null || name.isEmpty() || phone.isEmpty() || roll.isEmpty() || pin.isEmpty() || ("User".equals(role) && balance.isEmpty())) {
                messageLabel.setText("⚠️ Please complete all fields.");
                messageLabel.setTextFill(Color.CRIMSON);
            } else {
                if(role.equals("User")){
                    try {
                        Connection conn = DBConnection.getConnection();

                        String insertSql = "INSERT INTO user_table (user_name, user_rollno, user_phone, user_pin, user_balance) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement insertstmt = conn.prepareStatement(insertSql);
                        insertstmt.setString(1, name);
                        insertstmt.setString(2, roll);
                        insertstmt.setLong(3, Long.parseLong(phone));
                        insertstmt.setDouble(4, Double.parseDouble(pin));
                        insertstmt.setInt(5, Integer.parseInt(balance));
                        insertstmt.executeUpdate();
                        insertstmt.close();
                        System.out.println("User info added to the list");

                        String temp_name = roll+"_transaction_table";
                        String createTableSql = "CREATE TABLE IF NOT EXISTS "+temp_name+" (id INT AUTO_INCREMENT PRIMARY KEY, "+
                                "transaction_id LONG, balance_before_transaction INT, amount_deducted INT, "+
                                "balance_after_transaction INT, date_time DATETIME, reciver_rollno VARCHAR(100))";
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate(createTableSql);
                        System.out.println("A seperate transaction history table is created for the user: "+name);

                        stmt.close();
                        conn.close();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                else{
                    try {
                        Connection conn = DBConnection.getConnection();

                        String insertSql = "INSERT INTO admin_table (admin_name, admin_rollno, admin_phone, admin_pin) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertstmt = conn.prepareStatement(insertSql);
                        insertstmt.setString(1, name);
                        insertstmt.setString(2, roll);
                        insertstmt.setLong(3, Long.parseLong(phone));
                        insertstmt.setDouble(4, Integer.parseInt(pin));
                        insertstmt.executeUpdate();
                        insertstmt.close();
                        System.out.println("Admin info added to the list");

                        conn.close();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                messageLabel.setText("✅ " + role + " account created for " + name + "!");
                messageLabel.setTextFill(Color.GREEN);
            }
        });

        registerBox.getChildren().addAll(
                titleLabel,
                roleBox,
                nameField,
                phoneField,
                rollField,
                pinField,
                balanceField,
                registerButton,
                messageLabel
        );

        return new StackPane(registerBox);
    }

    private Pane createDeletePane() {
        VBox deleteBox = new VBox(20);
        deleteBox.setPadding(new Insets(40));
        deleteBox.setAlignment(Pos.CENTER);
        deleteBox.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #e6e9f0);");

        Label titleLabel = new Label("Delete Your Account ❌");
        titleLabel.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 24));

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "User");
        roleBox.setPromptText("👤 Select Role");
        roleBox.setMaxWidth(250);

        TextField rollField = new TextField();
        rollField.setPromptText("🎓 Roll Number");
        rollField.setMaxWidth(250);

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("🔢 Enter PIN");
        pinField.setMaxWidth(250);

        Label messageLabel = new Label();

        Button deleteButton = new Button("❌ Delete Account");
        deleteButton.setPrefWidth(250);
        deleteButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
        deleteButton.setOnAction(e -> {
            String role = roleBox.getValue();
            String roll = rollField.getText();
            String pin = pinField.getText();

            if (roll.isEmpty() || pin.isEmpty()) {
                messageLabel.setText("⚠️ Roll Number and PIN are required.");
                messageLabel.setTextFill(Color.CRIMSON);
            }
            else{
                if(role.equals("User")){
                    try {
                        Connection conn = DBConnection.getConnection();

                        String findSql_rollno = "SELECT * FROM user_table WHERE user_rollno = ?";
                        PreparedStatement findstmt_rollno = conn.prepareStatement(findSql_rollno);
                        findstmt_rollno.setString(1,roll);

                        ResultSet rslt_rollno = findstmt_rollno.executeQuery();

                        if(rslt_rollno.next()){
                            String findSql_pin = "SELECT * FROM user_table WHERE user_pin = ?";
                            PreparedStatement findstmt_pin = conn.prepareStatement(findSql_pin);
                            findstmt_pin.setDouble(1,Double.parseDouble(pin));

                            ResultSet rslt_pin = findstmt_pin.executeQuery();

                            if(rslt_pin.next()){
                                String deleteSql = "DELETE FROM user_table WHERE user_rollno = ?";
                                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                                deleteStmt.setString(1,roll);

                                int rowsDeleted = deleteStmt.executeUpdate();
                                messageLabel.setText("✅ User account deleted!");
                                messageLabel.setTextFill(Color.GREEN);
                                deleteStmt.close();
                            }
                            else {
                                messageLabel.setText("❌ Wrong Pin.");
                                messageLabel.setTextFill(Color.CRIMSON);
                            }

                            rslt_pin.close();
                        }
                        else {
                            messageLabel.setText("❌ User doesn't exist.");
                            messageLabel.setTextFill(Color.CRIMSON);
                        }

                        rslt_rollno.close();
                        findstmt_rollno.close();
                        conn.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                else{
                    try {
                        Connection conn = DBConnection.getConnection();

                        String findSql_rollno = "SELECT * FROM admin_table WHERE admin_rollno = ?";
                        PreparedStatement findstmt_rollno = conn.prepareStatement(findSql_rollno);
                        findstmt_rollno.setString(1,roll);

                        ResultSet rslt_rollno = findstmt_rollno.executeQuery();

                        if(rslt_rollno.next()){
                            String findSql_pin = "SELECT * FROM admin_table WHERE admin_pin = ?";
                            PreparedStatement findstmt_pin = conn.prepareStatement(findSql_pin);
                            findstmt_pin.setDouble(1,Double.parseDouble(pin));

                            ResultSet rslt_pin = findstmt_pin.executeQuery();

                            if(rslt_pin.next()){
                                String deleteSql = "DELETE FROM admin_table WHERE admin_rollno = ?";
                                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                                deleteStmt.setString(1,roll);

                                int rowsDeleted = deleteStmt.executeUpdate();
                                messageLabel.setText("✅ Admin account deleted!");
                                messageLabel.setTextFill(Color.GREEN);
                                deleteStmt.close();
                            }
                            else {
                                messageLabel.setText("❌ Wrong Pin.");
                                messageLabel.setTextFill(Color.CRIMSON);
                            }

                            rslt_pin.close();
                        }
                        else {
                            messageLabel.setText("❌ Admin doesn't exist.");
                            messageLabel.setTextFill(Color.CRIMSON);
                        }

                        rslt_rollno.close();
                        findstmt_rollno.close();
                        conn.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        deleteBox.getChildren().addAll(titleLabel,roleBox, rollField, pinField, deleteButton, messageLabel);
        return new StackPane(deleteBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

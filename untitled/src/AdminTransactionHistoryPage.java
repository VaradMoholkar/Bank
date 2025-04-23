import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;

public class AdminTransactionHistoryPage {

    public static Scene getScene(Stage stage, String adminRollno) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("📄 Transaction History Lookup");
        title.setFont(new Font("Segoe UI", 24));

        TextField userRollField = new TextField();
        userRollField.setPromptText("Enter user's roll number");
        userRollField.setMaxWidth(250);

        TextArea transactionArea = new TextArea();
        transactionArea.setEditable(false);
        transactionArea.setWrapText(true);
        transactionArea.setPrefHeight(250);
        transactionArea.setMaxWidth(500);

        Button fetchBtn = new Button("🔍 Fetch History");
        Button backBtn = new Button("🔙 Back");

        fetchBtn.setOnAction(e -> {
            String rollno = userRollField.getText().trim();
            StringBuilder sb = new StringBuilder();

            try {
                String table = rollno + "_transaction_table";
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);

                while (rs.next()) {
                    sb.append("ID: ").append(rs.getString("transaction_id"))
                            .append(", Before: ").append(rs.getString("balance_before_transaction"))
                            .append(", Deducted: ").append(rs.getString("amount_deducted"))
                            .append(", After: ").append(rs.getString("balance_after_transaction"))
                            .append(", Time: ").append(rs.getString("date_time"))
                            .append(", Receiver: ").append(rs.getString("reciver_rollno"))
                            .append("\n\n");
                }

                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                sb.append("⚠ User doesn't exist");
            }

            transactionArea.setText(sb.toString());
        });

        backBtn.setOnAction(e -> {
            new Admin(adminRollno).start(stage);
        });

        root.getChildren().addAll(title, userRollField, fetchBtn, transactionArea, backBtn);
        return new Scene(root, 600, 500);
    }
}


# 🏦 Campus Banking Portal

This is a JavaFX-based GUI application that simulates a simple banking system for a campus. It supports both **Admin** and **User** roles and provides basic functionality like login, registration, balance management, PIN change, and transaction history. The backend is powered by **MySQL**.

---

## 🚀 Features

### For Users
- 💰 Check current balance
- 💸 Transfer money to another user
- 💵 Deposit and withdraw money
- 📄 View transaction history
- 🔢 Change PIN
- 🚪 Logout

### For Admins
- 👥 View users table
- 🛠️ View admins table
- 💰 View user's balance
- 📄 View user's transaction history
- 🔢 Change admin PIN
- 🚪 Logout

---

## 🧱 Tech Stack

- **JavaFX** - UI framework
- **MySQL** - Relational database
- **JDBC** - Java Database Connectivity
- **Java 11+**

---

## 🛠️ Project Structure

```
src/
│
├── Main.java              # Main application launcher with Login, Register, and Delete tabs
├── Admin.java             # Admin dashboard view
├── User.java              # User dashboard view
├── DBConnection.java      # MySQL database connection utility
└── style.css              # Application-wide styling (needs to be present in resources)
```

---

## 💾 Database Schema

The application expects a MySQL database named `oops_java_project` with at least two tables:

### `user_table`
```sql
CREATE TABLE user_table (
  user_name VARCHAR(100),
  user_rollno VARCHAR(100) PRIMARY KEY,
  user_phone BIGINT,
  user_pin DOUBLE,
  user_balance INT
);
```

### `admin_table`
```sql
CREATE TABLE admin_table (
  admin_name VARCHAR(100),
  admin_rollno VARCHAR(100) PRIMARY KEY,
  admin_phone BIGINT,
  admin_pin DOUBLE
);
```

### ⏱️ Each user also gets a custom transaction table created upon signup, e.g.:
```sql
CREATE TABLE 2001_transaction_table (
  id INT AUTO_INCREMENT PRIMARY KEY,
  transaction_id LONG,
  balance_before_transaction INT,
  amount_deducted INT,
  balance_after_transaction INT,
  date_time DATETIME,
  reciver_rollno VARCHAR(100)
);
```

---

## 🔧 Setup Instructions

1. **Clone or Download** the repository.
2. **Create MySQL Database** named `oops_java_project` and import the table schemas as shown above.
3. **Edit DBConnection.java**:
   ```java
   private static final String url = "jdbc:mysql://localhost:3306/oops_java_project";
   private static final String user = "root";
   private static final String pass = "user@123";
   ```
   Update `user` and `pass` as per your MySQL setup.

4. **Build and Run**:
   - Use **VS Code** or any Java IDE.
   - Make sure to add **JavaFX libraries** and **MySQL Connector/J** to the project’s classpath.

---

## 📸 UI Preview

*Login Window*
> Allows selection between Admin or User, with roll number and PIN fields.

*Admin & User Dashboards*
> Customized interfaces for each role, offering options as described above.

---

## ✍️ Author

Developed as part of a **Java OOPs course project**.


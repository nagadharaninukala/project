import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class DatabaseMenuApp extends Application {
    VBox contentBox = new VBox(10); // Dynamic content area
    TableView<List<String>> tableView = new TableView<>();
    // Replace these with your DB credentials
    final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    final String DB_USER = "root";
    final String DB_PASS = "dharani@naga93";
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Database Operations");
        Button btnCreate = new Button("Create");
        Button btnInsert = new Button("Insert");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnSelect = new Button("Select");
        HBox menuBox = new HBox(10, btnCreate, btnInsert, btnUpdate, btnDelete, btnSelect);
        menuBox.setPadding(new Insets(15));
        menuBox.setStyle("-fx-background-color: #444;");
        menuBox.getChildren().forEach(button -> {
            button.setStyle("-fx-font-size: 14px; -fx-background-color: #88f; -fx-text-fill: white;");
            button.setEffect(new DropShadow());
        });
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: #eee;");
        btnCreate.setOnAction(e -> showCreateForm());
        btnInsert.setOnAction(e -> showInsertForm());
        btnUpdate.setOnAction(e -> showUpdateForm());
        btnDelete.setOnAction(e -> showDeleteForm());
        btnSelect.setOnAction(e -> showSelectForm());
        VBox mainLayout = new VBox(menuBox, contentBox, tableView);
        Scene scene = new Scene(mainLayout, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void applyFadeEffect(Region node) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    private void showCreateForm() {
        contentBox.getChildren().clear();
        Label heading = new Label("Create Table");
        heading.setFont(new Font(18));
        TextField tableName = new TextField();
        tableName.setPromptText("Table Name");
        TextField fieldName = new TextField();
        fieldName.setPromptText("Field Name");
        TextField dataType = new TextField();
        dataType.setPromptText("Data Type (e.g., VARCHAR(50))");
        Button submitBtn = new Button("Create Table");
        submitBtn.setOnAction(e -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String query = "CREATE TABLE IF NOT EXISTS " + tableName.getText() +
                               " (" + fieldName.getText() + " " + dataType.getText() + ")";
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(query);
                fetchTableData(tableName.getText());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
        contentBox.getChildren().addAll(heading, tableName, fieldName, dataType, submitBtn);
        applyFadeEffect(contentBox);
    }
    private void showInsertForm() {
        contentBox.getChildren().clear();
        Label heading = new Label("Insert Into Table");
        heading.setFont(new Font(18));
        TextField tableName = new TextField();
        tableName.setPromptText("Table Name");
        TextField field = new TextField();
        field.setPromptText("Field Name");
        TextField value = new TextField();
        value.setPromptText("Value");
        Button submitBtn = new Button("Insert Record");
        submitBtn.setOnAction(e -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String query = "INSERT INTO " + tableName.getText() + 
                               " (" + field.getText() + ") VALUES (?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, value.getText());
                pstmt.executeUpdate();
                fetchTableData(tableName.getText());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
        contentBox.getChildren().addAll(heading, tableName, field, value, submitBtn);
        applyFadeEffect(contentBox);
    }
    private void showUpdateForm() {
        contentBox.getChildren().clear();
        Label heading = new Label("Update Record");
        heading.setFont(new Font(18));
        TextField table = new TextField();
        table.setPromptText("Table Name");
        TextField field = new TextField();
        field.setPromptText("Field to Update");
        TextField value = new TextField();
        value.setPromptText("New Value");
        TextField condition = new TextField();
        condition.setPromptText("Condition (e.g., id=1)");
        Button submitBtn = new Button("Update Record");
        submitBtn.setOnAction(e -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String query = "UPDATE " + table.getText() + " SET " + field.getText() + " = ? WHERE " + condition.getText();
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, value.getText());
                pstmt.executeUpdate();
                fetchTableData(table.getText());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
        contentBox.getChildren().addAll(heading, table, field, value, condition, submitBtn);
        applyFadeEffect(contentBox);
    }
    private void showDeleteForm() {
        contentBox.getChildren().clear();
        Label heading = new Label("Delete From Table");
        heading.setFont(new Font(18));
        TextField table = new TextField();
        table.setPromptText("Table Name");
        TextField condition = new TextField();
        condition.setPromptText("Condition (e.g., id=1)");
        Button submitBtn = new Button("Delete Record");
        submitBtn.setOnAction(e -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String query = "DELETE FROM " + table.getText() + " WHERE " + condition.getText();
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(query);
                fetchTableData(table.getText());
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
        contentBox.getChildren().addAll(heading, table, condition, submitBtn);
        applyFadeEffect(contentBox);
    }
    private void showSelectForm() {
        contentBox.getChildren().clear();
        Label heading = new Label("Select From Table");
        heading.setFont(new Font(18));
        TextField table = new TextField();
        table.setPromptText("Table Name");
        Button fetchBtn = new Button("Fetch Records");
        fetchBtn.setOnAction(e -> fetchTableData(table.getText()));
        contentBox.getChildren().addAll(heading, table, fetchBtn);
        applyFadeEffect(contentBox);
    }
    private void fetchTableData(String tableName) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                final int colIndex = i;
                TableColumn<List<String>, String> col = new TableColumn<>(meta.getColumnName(i));
                col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(colIndex - 1)));
                tableView.getColumns().add(col);
            }
            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                tableView.getItems().add(row);
            }
        } catch (Exception ex) {
            showAlert("Error", ex.getMessage());
        }
    }
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
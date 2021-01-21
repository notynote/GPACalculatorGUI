package GPAcalculator;

import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    public static Connection connection;

    @Override
    public void start(Stage primaryStage) throws Exception{

        this.connection = connect();

        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("GPA Calculator");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    private Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setServerTimezone("Asia/Bangkok");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("gpa_calculation");
        dataSource.setUser("pg2");
        dataSource.setPassword("Passw0rd");

        return dataSource.getConnection();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

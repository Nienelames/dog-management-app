import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/* 
<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<IMPORTANT READ FIRST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                            This app's database is hosted on Heroku
                            which periodically changes the database connection
                            string. In case this happens, you can contact
                            me and I can provide a new one.
                            If you're going to try and re-create this database
                            anyways, know that the "pgcrypto" and "uuid-oossp"
                            extentions are obligatory. You also will have to
                            make "share_code" in the "dog" table have 
                            an auto-generated UUID so that the share dog
                            feature works.

                            This app will throw errors if used with JRE ver 11
                            Make sure to have the latest Java installation
<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<IMPORTANT READ FIRST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
*/

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("/images/paw.png"));
        primaryStage.setTitle("Dog CRUD app");
        primaryStage.show();

        Account account = new Account(primaryStage);
        account.displayLogin();
    }
}
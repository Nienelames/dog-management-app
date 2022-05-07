import java.util.regex.Pattern;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class User {
    private int userId;
    private String email;
    private String password;

    private Stage userInfoStage; 
    private GridPane userInfoPane;
    private TextField emailField;
    private PasswordField newPasswordField;
    private PasswordField confrimNewPasswordField;
    private PasswordField oldPasswordField;

    //Constructor for new user
    protected User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    //Constructor for logged in user
    protected User(int userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    protected int getUserId() {
        return this.userId;
    }

    protected void setUserId(int userId) {
        this.userId = userId;
    }

    protected String getEmail() {
        return this.email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    protected String getPassword() {
        return this.password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected Boolean loginUser() {
        Database db = new Database();
        Boolean userExists = db.getUserByCredentials(this);
    
        if (userExists) 
            return true;
        
        Utils.displayAlert("Invalid authentication credentials!", "");

        return false;
    }

    protected Boolean registerUser() {
        Database db = new Database();
        Boolean userInsertSucceeded = db.insertNewUser(this);   
        
        if (userInsertSucceeded && db.getUserByCredentials(this)) 
            return true;
        
       
        return false;
     }

    protected void displayUserInfo() {
        this.userInfoStage = new Stage();

        //Popup conatier
        this.userInfoPane = new GridPane();
        
        this.userInfoPane.setVgap(10);
        this.userInfoPane.getStyleClass().add("popup-container");

        //Email area
        Label emailLabel = new Label("Your email:");
        this.emailField = new TextField(this.email);

        this.emailField.setMinWidth(200);
        this.emailField.setDisable(true);
        this.emailField.setStyle("-fx-text-fill: white");
        this.emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(this.email)) 
                this.emailField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.emailField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");

            //Restricting email length
            if (newValue.length() > 320)
                this.emailField.setText(oldValue);

            //Preventing spaces
            this.emailField.setText(this.emailField.getText().replaceAll("\s", ""));
        });

        this.userInfoPane.add(emailLabel, 0, 0);
        this.userInfoPane.add(this.emailField, 0, 1);

        //Bottom navigation bar
        HBox navBar = new HBox();

        navBar.setAlignment(Pos.CENTER);
        navBar.setSpacing(15);

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 30, 25, "white");

        goBackButtonIcon.setOnMouseClicked(e1 -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), userInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                userInfoStage.close();
            });

            this.userInfoPane.setDisable(true);
            fadeOut.play();
        });

        navBar.getChildren().add(goBackButtonIcon);

        Region editButtonIcon = Utils.getSVG(SVGIcons.EDIT_PENCIL, 25, 25, "white");

        editButtonIcon.setOnMouseClicked(e1 -> {
            navBar.getChildren().removeAll(goBackButtonIcon, editButtonIcon);

            this.emailField.setDisable(false);
            this.emailField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            Label newPasswordLabel = new Label("New Password:");

            newPasswordLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

            this.newPasswordField = new PasswordField();
            

            this.newPasswordField.setMinWidth(200);
            this.newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
                //Restricting password length
                if (newValue.length() > 72)
                    this.newPasswordField.setText(oldValue);

                //Preventing spaces
                this.newPasswordField.setText(this.newPasswordField.getText().replaceAll("\s", ""));
            });

            this.userInfoPane.add(newPasswordLabel, 0, 2);
            this.userInfoPane.add(this.newPasswordField, 0, 3);

            Label confirmNewPasswordLabel = new Label("Confirm New Password:");

            confirmNewPasswordLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

            this.confrimNewPasswordField = new PasswordField();

            this.confrimNewPasswordField.setMinWidth(200);
            this.confrimNewPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
                //Restricting password length
                if (newValue.length() > 72)
                    this.confrimNewPasswordField.setText(oldValue);

                //Preventing spaces
                this.confrimNewPasswordField.setText(this.confrimNewPasswordField.getText().replaceAll("\s", ""));
            });

            this.userInfoPane.add(confirmNewPasswordLabel, 0, 4);
            this.userInfoPane.add(this.confrimNewPasswordField, 0, 5);

            Label oldPasswordLabel = new Label("Old Password:");

            oldPasswordLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

            this.oldPasswordField = new PasswordField();

            this.oldPasswordField.setMinWidth(200);
            this.oldPasswordField.setOnMouseClicked(e -> this.oldPasswordField.setStyle("-fx-border-color: white"));
            this.oldPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
                //Restricting password length
                if (newValue.length() > 72)
                    this.oldPasswordField.setText(oldValue);

                //Preventing spaces
                this.oldPasswordField.setText(this.oldPasswordField.getText().replaceAll("\s", ""));
            });

            this.userInfoPane.add(oldPasswordLabel, 0, 6);
            this.userInfoPane.add(this.oldPasswordField, 0, 7);

            Button removeAccountButton = new Button("Remove account");

            removeAccountButton.setPrefWidth(200);
            removeAccountButton.getStyleClass().add("remove-button");
            removeAccountButton.setOnMouseClicked(e -> removeUser());

            this.userInfoPane.add(removeAccountButton, 0, 8);

            Region confirmEditButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

            confirmEditButtonIcon.setOnMouseClicked(e -> {
                editUser();
            });

            navBar.getChildren().add(confirmEditButtonIcon);
    
            Region cancelEditButtonIcon = Utils.getSVG(SVGIcons.FAILURE_X, 25, 25, "white");
    
            cancelEditButtonIcon.setOnMouseClicked(e -> {
                this.emailField.setText(this.email);
                this.emailField.setDisable(true);
                this.emailField.setStyle("-fx-text-fill: white");

                navBar.getChildren().addAll(goBackButtonIcon, editButtonIcon);
                navBar.getChildren().removeAll(confirmEditButtonIcon, cancelEditButtonIcon);
                this.userInfoPane.getChildren().removeAll(newPasswordLabel, this.newPasswordField, confirmNewPasswordLabel, this.confrimNewPasswordField, oldPasswordLabel, this.oldPasswordField, removeAccountButton);
            });

            navBar.getChildren().add(cancelEditButtonIcon);
        });

        navBar.getChildren().add(editButtonIcon);

        this.userInfoPane.add(navBar, 0, 9);

        //Setting window scene
        Scene userInfoScene = new Scene(userInfoPane, 290, 365);
        userInfoScene.setFill(Color.TRANSPARENT);
        userInfoScene.getStylesheets().add("style.css");

        userInfoStage.initStyle(StageStyle.TRANSPARENT);
        userInfoStage.setScene(userInfoScene);
        userInfoStage.showAndWait();  
    }

    private void editUser() {
        //Error for invalid email format
        if (!Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$").matcher(this.emailField.getText()).matches()) {
            this.emailField.setText("");
            this.emailField.setStyle("-fx-border-color: crimson");
            this.emailField.setPromptText("Invalid email format");

            return;
        }
    
        //Error for mismatched passwords
        if (!this.newPasswordField.getText().equals(this.confrimNewPasswordField.getText())) {
            Utils.displayAlert("Passowrds don't match!", "");

            return;
        }

        if (this.emailField.getText().equals(this.email) &&
            this.newPasswordField.getText() == "") 
                Utils.displayAlert("No changes detected!", "");
    

        Database db = new Database();
        User editedUser = new User(this.email, this.oldPasswordField.getText());

        //Checking user password
        if (!db.getUserByCredentials(editedUser)) {
            Utils.displayAlert("Invalid old password", "");
            
            return;
        }

        editedUser.setEmail(this.emailField.getText());

        if (this.newPasswordField.getText() != "")
            editedUser.setPassword(this.newPasswordField.getText());
        else
            editedUser.setPassword(this.oldPasswordField.getText());

        Boolean editSuccessful = db.commitUserEdit(editedUser);

        if (editSuccessful) {
            this.email = editedUser.getEmail();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.userInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e -> userInfoStage.close());

            this.userInfoPane.setDisable(true);
            fadeOut.play();
        }      
    }

    private void removeUser() {
        //Error for no password entered
        if (this.oldPasswordField.getText() == "") {
            this.oldPasswordField.setStyle("-fx-border-color: crimson");
            this.oldPasswordField.setPromptText("Enter old password");

            return;
        }

        Database db = new Database();

        //Checking password
        this.password = oldPasswordField.getText();

        if (!db.getUserByCredentials(this)) {
            Utils.displayAlert("Invalid old password!", "");

            return;
        }

        Boolean userRemovalSuccessfull = db.deleteUser(this.userId);

        if (userRemovalSuccessfull) {
            this.userId = -1;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.userInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e -> userInfoStage.close());

            this.userInfoPane.setDisable(true);
            fadeOut.play();
        } 
    }

}

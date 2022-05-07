import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Account {
    private User user;
    private ArrayList<Dog> userDogs;

    private Stage primaryStage;
    private GridPane primaryPane;
    private GridPane centerPane;

    //Constructor for logged out user
    protected Account(Stage primaryStage) {
        this.userDogs = new ArrayList<>();

        //Front-end
        this.primaryPane = new GridPane();
        this.primaryPane.setAlignment(Pos.CENTER);

        this.centerPane = new GridPane();
        this.centerPane.getStyleClass().add("content-container");
 
        Scene primaryScene = new Scene(primaryPane, 1000, 700);
        primaryScene.getStylesheets().add("style.css");

        this.primaryStage = primaryStage;
        this.primaryStage.setScene(primaryScene);
    }

    //Constructor for logged in user
    protected Account(Stage primaryStage, User user, ArrayList<Dog> userDogs) {
        this.user = user;
        this.userDogs = userDogs;
        
        //Front-end
        this.primaryPane = new GridPane(); 
        this.primaryPane.setAlignment(Pos.CENTER);

        this.centerPane = new GridPane();
        this.centerPane.getStyleClass().add("content-container");
 
        Scene primaryScene = new Scene(primaryPane, 1000, 700);
        primaryScene.getStylesheets().add("style.css");
        
        this.primaryStage = primaryStage;
        this.primaryStage.setScene(primaryScene);
    }

    protected void displayLogin() {
        this.primaryPane.getChildren().clear();

        this.centerPane = new GridPane();
        this.centerPane.getStyleClass().add("content-container");

        //Login Label
        Label loginLabel = new Label("Sign in to your account");

        loginLabel.setStyle("-fx-font-weight: bold;");
        GridPane.setMargin(loginLabel, (new Insets(0, 0, 20, 0)));
        GridPane.setHalignment(loginLabel, HPos.CENTER);

        this.centerPane.add(loginLabel, 1, 1);

        //Email Field
        Label emailLabel = new Label("Email address");
        TextField emailField = new TextField();

        emailLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");
        GridPane.setMargin(emailField, (new Insets(0, 0, 5, 0)));

        emailField.setOnMouseClicked(e -> {
            emailField.setStyle("-fx-border-color: white");
            emailField.setPromptText("");
        });
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting email length
            if (newValue.length() > 320) {
                emailField.setText(oldValue);

                return;
            }

            //Preventing spaces
            emailField.setText(emailField.getText().replaceAll("\s", ""));
        });

        this.centerPane.add(emailLabel, 1, 2);
        this.centerPane.add(emailField, 1, 3);

        //Password field
        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();

        passwordLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        passwordField.setOnMouseClicked(e -> {
            passwordField.setStyle("-fx-border-color: white;");
            passwordField.setPromptText("");
        });
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting password length
            if (newValue.length() > 72) {
                passwordField.setText(oldValue);

                return;
            }

            //Preventing spaces
            passwordField.setText(passwordField.getText().replaceAll("\s", ""));
        });

        this.centerPane.add(passwordLabel, 1, 4);
        this.centerPane.add(passwordField, 1, 5);
        
        //Form navigation
        Button loginButton = new Button("Login");
        Label switchToRegisterButton = new Label("Register");
        
        loginButton.setMinWidth(290);
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnMouseClicked(e1 -> {
            this.user = new User(emailField.getText(), passwordField.getText());
            Boolean loginSuccessfull = this.user.loginUser();

            if (loginSuccessfull) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> {
                    displayUserOptions();
                    this.centerPane.setDisable(false);
                });
                
                this.centerPane.setDisable(true);
                fadeOut.play();
            }
        });
        GridPane.setMargin(loginButton, (new Insets(20, 0, 20, 0)));
        GridPane.setHalignment(switchToRegisterButton, HPos.CENTER);
        switchToRegisterButton.getStyleClass().add("text-button");
        switchToRegisterButton.setOnMouseClicked(e1 -> { 
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> displayRegistry(loginLabel, loginButton, switchToRegisterButton, emailField, passwordField));

            this.centerPane.setDisable(true);
            fadeOut.play();
        });

        this.centerPane.add(loginButton, 1, 8);
        this.centerPane.add(switchToRegisterButton, 1, 9);

        this.primaryPane.add(this.centerPane, 0, 0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeIn.setFromValue(0);
        fadeIn.setFromValue(1);
        fadeIn.setCycleCount(1);
        fadeIn.setOnFinished(e -> this.centerPane.setDisable(false));

        fadeIn.play();
    }

    private void displayRegistry(Label loginLabel, Button loginButton, Label switchToRegisterButton ,TextField emailField, PasswordField passwordField) {
        Label backTologinButton = new Label("Login");
        Label confirmPasswordLabel = new Label("Confirm password");
        Button registerButton = new Button("Register");
        PasswordField confirmPasswordField = new PasswordField();

        //Confrim password field
        confirmPasswordLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");
        confirmPasswordField.getStyleClass().add("text-field");

        confirmPasswordField.setOnMouseClicked(e -> {
            confirmPasswordField.setStyle("-fx-border-color: white");
            confirmPasswordField.setPromptText("");
        });
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting password length
            if (newValue.length() > 72) {
                confirmPasswordField.setText(oldValue);

                return;
            }

            //Preventing spaces
            confirmPasswordField.setText(confirmPasswordField.getText().replaceAll("\s", ""));
        });

        //Altering login form content
        loginLabel.setText("Register your account");
        this.centerPane.add(confirmPasswordLabel, 1, 6);
        GridPane.setMargin(confirmPasswordLabel, new Insets(5, 0, 0, 0));
        this.centerPane.add(confirmPasswordField, 1, 7);
        this.centerPane.getChildren().remove(loginButton);
        this.centerPane.add(registerButton, 1, 8);
        registerButton.setMinWidth(290);
        this.centerPane.getChildren().remove(switchToRegisterButton);
        GridPane.setMargin(registerButton, new Insets(20, 0, 20, 0));
        backTologinButton.getStyleClass().add("text-button");
        this.centerPane.add(backTologinButton, 1, 9);
        GridPane.setHalignment(backTologinButton, HPos.CENTER);

        //Fading in new register form
        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        this.centerPane.setDisable(false);

        //Registry form submitted 
        registerButton.setOnMouseClicked(e1 -> {
            //Error for empty fields
            if (emailField.getText() == "" || passwordField.getText() == "" || confirmPasswordField.getText() == "") {
                Utils.displayAlert("All fields must be filled!", "");

                return;
            }

            //Error for invalid email format
            if (!Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$").matcher(emailField.getText()).matches()) {
                emailField.setText("");
                emailField.setStyle("-fx-border-color: crimson");
                emailField.setPromptText("Invalid email format");

                return;
            }

            //Error for mismatched passwords
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                Utils.displayAlert("Passowrds don't match!", "");

                return;
            }

            this.user = new User(emailField.getText(), passwordField.getText());
            Boolean registationSuccessfull = this.user.registerUser();

            if (registationSuccessfull) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> {
                    displayUserOptions();
                    this.centerPane.setDisable(false);
                });

                this.centerPane.setDisable(true);
                fadeOut.play();
            }
        });

        //Switching back to the login form
        backTologinButton.setOnMouseClicked(e1 -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.cycleCountProperty().add(1);
            
            //Preventing user intercation during fade
            this.centerPane.setDisable(true);
            fadeOut.play();

            //Fading out register form
            fadeOut.setOnFinished(e2 -> {
                //Changing register from back to login form
                loginLabel.setText("Sing in to your account");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
                this.centerPane.getChildren().removeAll(confirmPasswordLabel, confirmPasswordField, backTologinButton, registerButton);
                this.centerPane.add(loginButton, 1, 8);
                this.centerPane.add(switchToRegisterButton, 1, 9);

                //Fading login form back in
                fadeIn.play();
                fadeIn.setOnFinished(e3 -> this.centerPane.setDisable(false));
            });
        });          
    }

    protected void displayUserOptions() {
        this.centerPane.getChildren().clear();
        this.centerPane.getStyleClass().add("content-container");
        this.centerPane.setVgap(10);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> this.centerPane.setDisable(false));

        fadeIn.play();
    

        FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        Label userEmaiDescriptionLabel = new Label("Logged in with:");

        userEmaiDescriptionLabel.setStyle("-fx-font-weight: bold");
        GridPane.setHalignment(userEmaiDescriptionLabel, HPos.CENTER);

        Label userEmailLabel = new Label(this.user.getEmail());
        
        GridPane.setHalignment(userEmailLabel, HPos.CENTER);

        Button editAccountButton = new Button("Edit Account");

        editAccountButton.setMinWidth(200);
        editAccountButton.setOnMouseClicked(e1 -> {
            this.centerPane.setDisable(true);
            this.user.displayUserInfo();
            this.centerPane.setDisable(false);

            //If user id is -1, the user has been removed
            if (this.user.getUserId() == -1) {
                fadeOut.setOnFinished(e2 -> displayLogin());
                fadeOut.play();
            }

            //Refreshing data
            userEmailLabel.setText(this.user.getEmail());
        });

        Button viewDogsButton = new Button("View Dogs");
        
        viewDogsButton.setMinWidth(200);
        viewDogsButton.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> displayUserDogs());
            fadeOut.play();
        });

        Button logOutButton = new Button("Log out");

        logOutButton.setMinWidth(200);
        logOutButton.setId("logout-button");
        logOutButton.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> displayLogin());
            fadeOut.play();
        });        

        this.centerPane.add(userEmaiDescriptionLabel, 0, 0);
        this.centerPane.add(userEmailLabel, 0, 1);
        this.centerPane.add(editAccountButton, 0, 2);
        this.centerPane.add(viewDogsButton, 0, 3);
        this.centerPane.add(logOutButton, 0, 4);
    }

    protected void displayUserDogs() {
        this.primaryPane.getChildren().clear();

        this.centerPane = new GridPane();
        this.centerPane.setHgap(20);
        this.centerPane.setVgap(10);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        
        Database db = new Database();
        this.userDogs = db.getUserDogs(this.user.getUserId());

        //Generating Dog profile data cards
        int i = 0;
        for (Dog userDog : this.userDogs) {
            VBox dogProfileCard = new VBox();
            Region pawIcon = Utils.getSVG(SVGIcons.DOG_PAW, 140, 140, "white");
            VBox dogInfoContainer = new VBox();
            Label dogName = new Label(userDog.getName());
            Label dogBreed = new Label(userDog.getBreed());
            
            //Profile card content
            dogName.setStyle("-fx-font-weight: bold;");
            dogInfoContainer.getStyleClass().add("dog-info-container");
            dogInfoContainer.getChildren().addAll(dogName, dogBreed);
            
            dogProfileCard.getStyleClass().add("content-container");
            dogProfileCard.getStyleClass().add("dog-profile-card");

            if (userDog.getIsShared()) {
                Region dogIsSharedIcon = Utils.getSVG(SVGIcons.LINKED_CHAIN, 25, 25, "white");
                dogProfileCard.getChildren().add(dogIsSharedIcon);
            }

            dogProfileCard.getChildren().addAll(pawIcon, dogInfoContainer);
            dogProfileCard.setOnMouseClicked(e1 -> {
                Dog selectedDog = db.getDog(this.user.getUserId(), userDog.getDogId());
                ObservableList<Appointment> appointments = db.getDogAppointments(userDog.getDogId());
                ObservableList<Medication> medications = db.getDogMedications(userDog.getDogId());
                ObservableList<DogWeightRecord> dogWeightRecords = db.getDogWeightRecords(userDog.getDogId());

                FadeTransition fadeOutAll = new FadeTransition(Duration.millis(666), this.primaryPane);
                fadeOutAll.setFromValue(1);
                fadeOutAll.setToValue(0);
                fadeOutAll.setOnFinished(e -> {
                    DogProfile dogProfile = new DogProfile(this.primaryStage, selectedDog, appointments, medications, dogWeightRecords);
                    dogProfile.displayDogProfile();
                });

                if (selectedDog != null && appointments != null && medications != null && dogWeightRecords != null) {
                    this.centerPane.setDisable(true);
                    fadeOutAll.play();
                }
            });

            this.centerPane.addColumn(i, dogProfileCard);
            i++;
        }

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 25, 25, "white");

        GridPane.setHalignment(goBackButtonIcon, HPos.CENTER);
        GridPane.setMargin(goBackButtonIcon, new Insets(100, 0, 0, 0));
        goBackButtonIcon.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> {
                this.primaryPane.setDisable(false);
                displayUserOptions();
            });

            this.primaryPane.getChildren().remove(goBackButtonIcon);
            this.primaryPane.setDisable(true);
            fadeOut.play();

        });

        this.primaryPane.addRow(1, goBackButtonIcon);

        //Add new dog card
        VBox newDogProfileCard = new VBox();
        Region dogProfilePicPlaceholder = Utils.getSVG(SVGIcons.QUESTION_MARK, 140, 140, "white");
        VBox newDogInfoContainer = new VBox();
        Label firstHalf = new Label("Add");
        Label secondHalf = new Label("new dog");

        firstHalf.setStyle("-fx-font-weight: bold;");

        newDogProfileCard.getStyleClass().add("content-container");
        newDogProfileCard.getStyleClass().add("dog-profile-card");
        newDogProfileCard.getChildren().addAll(dogProfilePicPlaceholder, newDogInfoContainer);
        newDogProfileCard.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> {
                displayDogAdditionOptions();
                this.primaryPane.getChildren().remove(goBackButtonIcon);
                this.centerPane.setDisable(false);
            });

            this.centerPane.setDisable(true);
            fadeOut.play();
        });

        newDogInfoContainer.getStyleClass().add("dog-info-container");
        newDogInfoContainer.getChildren().addAll(firstHalf, secondHalf);

        this.centerPane.addColumn(i + 1, newDogProfileCard);
        this.primaryPane.add(this.centerPane, 0, 0);
    }

    private void displayDogAdditionOptions() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        this.centerPane.getChildren().clear();
        this.centerPane.getStyleClass().add("content-container");
        this.centerPane.setVgap(10);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        Button addNewDogButton = new Button("Add new");

        addNewDogButton.setMinWidth(150);
        addNewDogButton.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> {
                displayNewDogForm();
                this.centerPane.setDisable(false);
            });

            this.centerPane.setDisable(true);
            fadeOut.play();
        });

        this.centerPane.addRow(0, addNewDogButton);

        Button addExistingDogButton = new Button("Add existing");

        addExistingDogButton.setMinWidth(150);
        addExistingDogButton.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> {
                displayExistingDogForm();
                this.centerPane.setDisable(false);
            });

            this.centerPane.setDisable(true);
            fadeOut.play();
        });

        this.centerPane.addRow(1, addExistingDogButton);
    }

    private void displayExistingDogForm() {
        this.centerPane.getChildren().clear();
        this.centerPane.setVgap(5);

        Label shareCodeLabel = new Label("Enter share code:");
        TextField shareCodeField = new TextField();

        shareCodeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8");
        shareCodeField.setMinWidth(200);
        shareCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting code length
            if (newValue.length() > 36) {
                shareCodeField.setText(oldValue);

                return;
            }

            //Preventing spaces
            shareCodeField.setText(shareCodeField.getText().replaceAll("\s", ""));
        });

        this.centerPane.addRow(0, shareCodeLabel);
        this.centerPane.addRow(1, shareCodeField);

        HBox navBar = new HBox();
        
        navBar.setSpacing(15);
        navBar.setAlignment(Pos.CENTER);
        GridPane.setMargin(navBar, new Insets(10, 0, 0, 0));

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 30, 25, "white");

        goBackButtonIcon.setOnMouseClicked(e1 -> {
            Database db = new Database();
            this.userDogs = db.getUserDogs(this.user.getUserId());

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            fadeOut.setOnFinished(e2 -> {
                displayUserDogs();
                this.centerPane.setDisable(false);
            });

            this.centerPane.setDisable(true);
            fadeOut.play();
        });

        navBar.getChildren().add(goBackButtonIcon);

        Region confirmButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");
        confirmButtonIcon.setOnMouseClicked(e1 -> {
            if (shareCodeField.getText() == "") {
                Utils.displayAlert("No share code entered!", "");
    
                return;
            }

            Dog sharedDog = new Dog(this.user.getUserId(), UUID.fromString(shareCodeField.getText()));
            Boolean dogAdditionSuccessfull = sharedDog.addExistingDog();

            if (dogAdditionSuccessfull) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> {
                    displayUserDogs();
                    this.centerPane.setDisable(false);
                });

                this.centerPane.setDisable(true);
                fadeOut.play();
            }
        });

        navBar.getChildren().add(confirmButtonIcon);

        this.centerPane.addRow(2, navBar);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), centerPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();
    }

    private void displayNewDogForm() {
        this.centerPane.getChildren().clear();

        this.centerPane.setVgap(5);
        this.centerPane.setHgap(5);

        //Dog name area
        Label dogNameLabel = new Label ("Name: *");
        dogNameLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.centerPane.addRow(0, dogNameLabel);

        TextField dogNameField = new TextField();

        dogNameField.setMinWidth(100);
        dogNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting name length
            if (newValue.length() > 320)
                dogNameField.setText(oldValue);
        });

        this.centerPane.addRow(1, dogNameField);

        //Dog coat color area
        Label dogCoatColorLabel = new Label ("Coat Color: *");
        dogCoatColorLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.centerPane.addRow(2, dogCoatColorLabel);

        TextField dogCoatColorField = new TextField();

        dogCoatColorField.setMinWidth(100);
        dogCoatColorField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting coat color length
            if (newValue.length() > 50) {
                dogCoatColorField.setText(oldValue);

                return;
            }

            //Allowing only letters
            dogCoatColorField.setText(dogCoatColorField.getText().replaceAll("[^a-zA-Z],", ""));
        });
        
        this.centerPane.addRow(3, dogCoatColorField);

        //Dog breed area
        Label breedLabel = new Label("Breed: *");
        breedLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.centerPane.addRow(4, breedLabel);

        TextField breedField = new TextField();

        breedField.setMinWidth(100);
        breedField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting name length
            if (newValue.length() > 320) {
                breedField.setText(oldValue);

                return;
            }

            //Allowing only letters
            breedField.setText(breedField.getText().replaceAll("[^a-zA-Z]", ""));
        });

        this.centerPane.addRow(5, breedField);

        Label birthDateLabel = new Label("Birth date: *");
        birthDateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.centerPane.addRow(6, birthDateLabel);
        
        DatePicker birthDateField = new DatePicker(LocalDate.now());

        birthDateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            //Allowing only past and present
            if (newValue.isAfter(LocalDate.now())) {
                birthDateField.setValue(oldValue);
                birthDateField.getEditor().setText(oldValue.toString());
            }
        });

        this.centerPane.addRow(7, birthDateField);

        Label dogNoteLabel = new Label("Note:");
        dogNoteLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.centerPane.addRow(8, dogNoteLabel);

        TextArea dogNoteArea = new TextArea();

        dogNoteArea.setMinWidth(100);
        dogNoteArea.setMinHeight(200);
        dogNoteArea.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting note length
            if (newValue.length() > 4000)
                dogNoteArea.setText(oldValue);
        });

        this.centerPane.addRow(9, dogNoteArea);

        HBox navBar = new HBox();

        navBar.setSpacing(15);
        navBar.setAlignment(Pos.CENTER);

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 30, 25, "white");

        goBackButtonIcon.setOnMouseClicked(e1 -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            fadeOut.setOnFinished(e2 -> {
                displayUserDogs();
                this.centerPane.setDisable(false);
            });

            this.centerPane.setDisable(true);
            fadeOut.play();
        });

        navBar.getChildren().add(goBackButtonIcon);

        Region confirmButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

        confirmButtonIcon.setOnMouseClicked(e1 -> {
            if (dogNameField.getText() == "" ||
                breedField.getText() == "" ||
                dogCoatColorField.getText() == "")
            {
                Utils.displayAlert("Obligatory fiels are empty!", "");

                return;
            }

            //int userId, String name, String coatColor, String breed, String note, LocalDate birthDate
            Dog newDog = new Dog(this.user.getUserId(),
                                 dogNameField.getText(),
                                 dogCoatColorField.getText(),
                                 breedField.getText(),
                                 dogNoteArea.getText(),
                                 birthDateField.getValue());

            Boolean dogAdditionSuccessfull = newDog.addNewDog();

            if (dogAdditionSuccessfull) {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.centerPane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> {
                    displayUserDogs();
                    this.centerPane.setDisable(false);
                });

                this.centerPane.setDisable(true);
                fadeOut.play();
            }
        });

        navBar.getChildren().add(confirmButtonIcon);

        this.centerPane.addRow(10, navBar);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.centerPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();
    }
}

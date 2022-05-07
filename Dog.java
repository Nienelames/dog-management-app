import java.time.LocalDate;
import java.util.UUID;

import javafx.animation.FadeTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Dog {
    private int dogId;
    private int userId;
    private String name;
    private String breed;
    private String note;
    private String coatColor;
    private UUID shareCode;
    private Boolean isShared;
    private LocalDate birthDate;

    private Stage dogInfoStage;
    private GridPane dogInfoPane;
    private TextField dogNameField;
    private TextField dogCoatColorField;
    private TextField breedField;
    private DatePicker birthDateField;
    private TextArea dogNoteArea;

    //Constructor for new dog
    protected Dog(int userId, String name, String coatColor, String breed, String note, LocalDate birthDate) {
        this.userId = userId;
        this.name = name;
        this.breed = breed;
        this.coatColor = coatColor;
        this.note = note;
        this.birthDate = birthDate;
    }

    //Constructor for new shared dog
    protected Dog(int userId, UUID shareCode) {
        this.userId = userId;
        this.shareCode = shareCode;
    }

    //Constructor for dog reel display
    protected Dog(int dogId, String name, String breed, Boolean isShared) {
        this.dogId = dogId;
        this.name = name;
        this.breed = breed;
        this.isShared = isShared;
    }

    //Constructor for dog profile display
    protected Dog(int userId, int dogId, String name, String breed, String coatColor, String note, UUID shareCode, LocalDate birthDate) {
        this.userId = userId;
        this.dogId = dogId;
        this.name = name;
        this.breed = breed;
        this.note = note;
        this.coatColor = coatColor;
        this.shareCode = shareCode;
        this.birthDate = birthDate;
    }

    public int getDogId() {
        return this.dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return this.breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCoatColor() {
        return this.coatColor;
    }

    public void setCoatColor(String coatColor) {
        this.coatColor = coatColor;
    }

    public UUID getShareCode() {
        return this.shareCode;
    }

    public void setShareCode(UUID shareCode) {
        this.shareCode = shareCode;
    }

    public Boolean isIsShared() {
        return this.isShared;
    }

    public Boolean getIsShared() {
        return this.isShared;
    }

    public void setIsShared(Boolean isShared) {
        this.isShared = isShared;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    protected Boolean addNewDog() {
        Database db = new Database();
        Boolean dogAdditionSuccessfull = db.insertNewDog(this);

        if (dogAdditionSuccessfull) 
            return true;

        return false;
    }

    protected Boolean addExistingDog() {
        Database db = new Database();
        Boolean dogInsertSuccesfull = db.insertExistingDog(this);

        if (dogInsertSuccesfull)
            return true;
        
        return false;
    } 

    protected void displayDogInfo() {
        //int userId, String name, String coatColor, String breed, String note, LocalDate birthDate
        this.dogInfoStage = new Stage();
        this.dogInfoPane = new GridPane();

        this.dogInfoPane.setVgap(5);

        this.dogInfoPane.getStyleClass().add("popup-container");

        //Dog name area
        Label dogNameLabel = new Label ("Name:");
        dogNameLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.dogInfoPane.addRow(0, dogNameLabel);

        this.dogNameField = new TextField(this.name);

        this.dogNameField.setDisable(true);
        this.dogNameField.setMinWidth(100);
        this.dogNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting name length
            if (newValue.length() > 320) {
                this.dogNameField.setText(oldValue);
            
                return;
            }

            if (newValue.equals(this.name))
                this.dogNameField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.dogNameField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #86BBD8, derive(#9EE493, 60%))");
        });

        this.dogInfoPane.addRow(1, dogNameField);

        //Dog coat color area
        Label dogCoatColorLabel = new Label ("Coat Color:");
        dogCoatColorLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.dogInfoPane.addRow(2, dogCoatColorLabel);

        this.dogCoatColorField = new TextField(this.coatColor);

        this.dogCoatColorField.setDisable(true);
        this.dogCoatColorField.setMinWidth(100);
        this.dogCoatColorField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting coat color length
            if (newValue.length() > 50) {
                this.dogCoatColorField.setText(oldValue);

                return;
            }

            //Allowing only letters
            this.dogCoatColorField.setText(dogCoatColorField.getText().replaceAll("[^a-zA-Z],", ""));

            if (this.dogCoatColorField.getText().equals(this.coatColor))
                this.dogCoatColorField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.dogCoatColorField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #86BBD8, derive(#9EE493, 60%))");
        });
        
        this.dogInfoPane.addRow(3, dogCoatColorField);

        //Dog breed area
        Label breedLabel = new Label("Breed:");
        breedLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.dogInfoPane.addRow(4, breedLabel);

        this.breedField = new TextField(this.breed);

        this.breedField.setDisable(true);
        this.breedField.setMinWidth(100);
        this.breedField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting name length
            if (newValue.length() > 320) {
                this.breedField.setText(oldValue);

                return;
            }

            //Allowing only letters
            this.breedField.setText(this.breedField.getText().replaceAll("[^a-zA-Z]", ""));

            if (this.breedField.getText().equals(this.breed))
                this.breedField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.breedField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #86BBD8, derive(#9EE493, 60%))");
        });

        this.dogInfoPane.addRow(5, breedField);

        Label birthDateLabel = new Label("Birth date:");
        birthDateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.dogInfoPane.addRow(6, birthDateLabel);
        
        this.birthDateField = new DatePicker(this.birthDate);

        this.birthDateField.setDisable(true);
        this.birthDateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            //Allowing only past and present
            if (newValue.isAfter(LocalDate.now())) {
                this.birthDateField.setValue(oldValue);
                this.birthDateField.getEditor().setText(oldValue.toString());

                return;
            }

            if (newValue.equals(this.birthDate))
                this.birthDateField.getEditor().setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.birthDateField.getEditor().setStyle("-fx-text-fill: radial-gradient(radius 150%, #86BBD8, derive(#9EE493, 60%))");                
        });

        this.dogInfoPane.addRow(7, this.birthDateField);

        Label dogNoteLabel = new Label("Note:");
        dogNoteLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.dogInfoPane.addRow(8, dogNoteLabel);

        this.dogNoteArea = new TextArea(this.note);

        this.dogNoteArea.setDisable(true);
        this.dogNoteArea.setMinWidth(100);
        this.dogNoteArea.setMinHeight(100);
        this.dogNoteArea.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting note length
            if (newValue.length() > 4000) {
                this.dogNoteArea.setText(oldValue);

                return;
            }

            if (newValue.equals(this.note))
                this.dogNoteArea.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.dogNoteArea.setStyle("-fx-text-fill: radial-gradient(radius 150%, #86BBD8, derive(#9EE493, 60%))");                
        });

        this.dogInfoPane.addRow(9, this.dogNoteArea);

        HBox navBar = new HBox();

        navBar.setSpacing(15);
        navBar.setAlignment(Pos.CENTER);

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 30, 25, "white");

        goBackButtonIcon.setOnMouseClicked(e1 -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.dogInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                this.dogInfoPane.setDisable(true);
                this.dogInfoStage.close();
            });

            this.dogInfoPane.setDisable(true);
            fadeOut.play();
        });

        navBar.getChildren().add(goBackButtonIcon);

        Region editButtonIcon = Utils.getSVG(SVGIcons.EDIT_PENCIL, 25, 25, "white");

        editButtonIcon.setOnMouseClicked(e1 -> {
            navBar.getChildren().removeAll(goBackButtonIcon, editButtonIcon);

            this.dogNameField.setDisable(false);
            this.dogNameField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.dogCoatColorField.setDisable(false);
            this.dogCoatColorField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.breedField.setDisable(false);
            this.breedField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.birthDateField.setDisable(false);
            this.birthDateField.getEditor().setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.dogNoteArea.setDisable(false);
            this.dogNoteArea.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");     
            
            Button removeDogButton = new Button("Remove Dog");

            removeDogButton.setMaxWidth(200);
            GridPane.setHalignment(removeDogButton, HPos.CENTER);
            removeDogButton.getStyleClass().add("remove-button");
            removeDogButton.setOnMouseClicked(e2 -> {
                this.dogInfoPane.setDisable(true);
                removeDog();
                this.dogInfoPane.setDisable(false);
            });

            this.dogInfoPane.addRow(10, removeDogButton);

            Region confirmEditButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

            confirmEditButtonIcon.setOnMouseClicked(e3 -> {
                this.dogInfoPane.setDisable(true);
                editDog();
                this.dogInfoPane.setDisable(false);
            });

            Region cancelEditButtonIcon = Utils.getSVG(SVGIcons.FAILURE_X, 25, 25, "white");

            cancelEditButtonIcon.setOnMouseClicked(e4 -> {
                this.dogNameField.setDisable(true);
                this.dogNameField.setStyle("-fx-text-fill: white");
    
                this.dogCoatColorField.setDisable(true);
                this.dogCoatColorField.setStyle("-fx-text-fill: white");
    
                this.breedField.setDisable(true);
                this.breedField.setStyle("-fx-text-fill: white");
    
                this.birthDateField.setDisable(true);
                this.birthDateField.getEditor().setStyle("-fx-text-fill: white");
    
                this.dogNoteArea.setDisable(true);
                this.dogNoteArea.setStyle("-fx-text-fill: white");     

                navBar.getChildren().removeAll(confirmEditButtonIcon, cancelEditButtonIcon);
                this.dogInfoPane.getChildren().remove(removeDogButton);
                navBar.getChildren().addAll(goBackButtonIcon, editButtonIcon);
            });

            navBar.getChildren().addAll(confirmEditButtonIcon, cancelEditButtonIcon);
        });

        navBar.getChildren().add(editButtonIcon);
        this.dogInfoPane.addRow(11, navBar);

        Scene userInfoScene = new Scene(this.dogInfoPane, 300, 520);
        userInfoScene.setFill(Color.TRANSPARENT);
        userInfoScene.getStylesheets().add("style.css");

        dogInfoStage.initStyle(StageStyle.TRANSPARENT);
        dogInfoStage.setScene(userInfoScene);
        dogInfoStage.showAndWait(); 
    }

    private void removeDog() {
        Database db = new Database();
        Boolean dogRemovalSuccessfull = db.deleteDog(this.dogId);

        if (dogRemovalSuccessfull) {
            this.dogId = -1;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.dogInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e -> {
                this.dogInfoStage.close();
            });

            this.dogInfoPane.setDisable(true);
            fadeOut.play();
        }
    }

    private void editDog() {
        if (this.dogNameField.getText().equals(this.name) &&
            this.dogCoatColorField.getText().equals(this.coatColor) &&
            this.breedField.getText().equals(this.breed) &&
            this.birthDateField.getValue().equals(this.birthDate) &&
            this.dogNoteArea.getText().equals(this.note))
        {
            Utils.displayAlert("No changes were made!", "");

            return;
        }

        //int userId, int dogId, String name, String breed, String coatColor, String note, UUID shareCode, LocalDate birthDate
        Dog editedDog = new Dog(this.userId,
                                this.dogId,
                                this.dogNameField.getText(),
                                this.breedField.getText(),
                                this.dogCoatColorField.getText(),
                                this.dogNoteArea.getText(),
                                this.shareCode,
                                this.birthDateField.getValue());

        Database db = new Database();
        Boolean dogEditSuccessfull = db.commitDogEdit(editedDog);

        if (dogEditSuccessfull) {
            this.name = editedDog.getName();
            this.breed = editedDog.getBreed();
            this.coatColor = editedDog.getCoatColor();
            this.note = editedDog.getNote();
            this.birthDate = editedDog.getBirthDate();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.dogInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e -> this.dogInfoStage.close());

            fadeOut.play();
        }
    }
}

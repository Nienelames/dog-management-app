import java.time.LocalDate;

import javafx.animation.FadeTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DogWeightRecord {
    private int dogId;
    private int weightId;
    private double weight;
    private LocalDate dateTaken;

    private Stage weightInfoStage;
    private GridPane weightInfoPane;
    private TextField weightField;
    private DatePicker dateField;

    //Construcor for new dog weight record
    protected DogWeightRecord(int dogId, double weight, LocalDate dateTaken) {
        this.dogId = dogId;
        this.weight = weight;
        this.dateTaken = dateTaken;
    }

    //Construcor for existing dog weight record
    protected DogWeightRecord(int weightId, int dogId, double weight, LocalDate dateTaken) {
        this.weightId = weightId;
        this.dogId = dogId;
        this.weight = weight;
        this.dateTaken = dateTaken;
    }

    public int getWeightId() {
        return this.weightId;
    }

    public void setWeightId(int weightId) {
        this.weightId = weightId;
    }

    public int getDogId() {
        return this.dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public LocalDate getDateTaken() {
        return this.dateTaken;
    }

    public void setDateTaken(LocalDate dateTaken) {
        this.dateTaken = dateTaken;
    }

    protected Boolean addDogWeightRecord() {
        Database db = new Database();
        db.insertNewDogWeightRecord(this);

        if (db.insertNewDogWeightRecord(this))
            return true;

        return false;
    };

    protected void displayDogWeightInfo() {
        this.weightInfoStage = new Stage();

        this.weightInfoPane = new GridPane();

        this.weightInfoPane.setVgap(10);
        this.weightInfoPane.setHgap(10);
        this.weightInfoPane.getStyleClass().add("popup-container");

        //Weight area
        Label weightLabel = new Label("Weight:");
        weightLabel.setStyle("-fx-text-fill: rgb(255, 255, 255, 0.8)");

        this.weightInfoPane.addRow(0, weightLabel);

        this.weightField = new TextField(Double.toString(this.weight));

        this.weightField.setDisable(true);
        this.weightField.setMinWidth(200);
        this.weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting wieght size
            if (newValue.length() > 3) {
                this.weightField.setText(oldValue);

                return;
            }

            //Allowing only numbers
            this.weightField.setText(this.weightField.getText().replaceAll("[^0-9].", ""));

            if (this.weightField.getText() != "") {
                if (this.weightField.getText().equals(Double.toString(this.weight))) 
                    this.weightField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
                else
                    this.weightField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
            }
        });
        
        this.weightInfoPane.addRow(1, this.weightField);

        Label dateLabel = new Label("Date:");
        dateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.weightInfoPane.addRow(2, dateLabel);

        this.dateField = new DatePicker(this.dateTaken);

        this.dateField.setDisable(true);
        this.dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            //Allowing only past and present dates
            if (newValue.isAfter(LocalDate.now())) {
                this.dateField.setValue(oldValue);
                this.dateField.getEditor().setText(oldValue.toString());

                return;
            }

            if (this.dateField.getValue() == this.dateTaken)
                this.dateField.getEditor().setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.dateField.getEditor().setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
        });

        this.weightInfoPane.addRow(3, dateField);

        HBox navBar = new HBox();

        navBar.setAlignment(Pos.CENTER);
        navBar.setSpacing(15);

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 25, 25, "white");

        goBackButtonIcon.setOnMouseClicked(e1 -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.weightInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                weightInfoStage.close();
            });

            this.weightInfoPane.setDisable(true);
            fadeOut.play();
        });

        navBar.getChildren().add(goBackButtonIcon);

        Region editButtonIcon = Utils.getSVG(SVGIcons.EDIT_PENCIL, 25, 25, "white");

        editButtonIcon.setOnMouseClicked(e1 -> {
            navBar.getChildren().removeAll(goBackButtonIcon, editButtonIcon);

            this.weightField.setDisable(false);
            this.weightField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.dateField.setDisable(false);
            this.dateField.getEditor().setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            Button removeAppointmentButton = new Button("Remove Weight Record");

            removeAppointmentButton.setPrefWidth(200);
            removeAppointmentButton.getStyleClass().add("remove-button");
            removeAppointmentButton.setOnMouseClicked(e2 -> removeDogWeightRecord());

            this.weightInfoPane.addRow(4, removeAppointmentButton);
            GridPane.setHalignment(removeAppointmentButton, HPos.CENTER);

            Region confirmEditButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

            confirmEditButtonIcon.setOnMouseClicked(e -> {
                editDogWeightRecord();
            });

            navBar.getChildren().add(confirmEditButtonIcon);
    
            Region cancelEditButtonIcon = Utils.getSVG(SVGIcons.FAILURE_X, 25, 25, "white");
    
            cancelEditButtonIcon.setOnMouseClicked(e -> {
                this.weightField.setDisable(true);
                this.weightField.setStyle("-fx-text-fill: white");

                this.dateField.setDisable(true);
                this.dateField.getEditor().setStyle("-fx-text-fill: white");

                navBar.getChildren().addAll(goBackButtonIcon, editButtonIcon);
                navBar.getChildren().removeAll(confirmEditButtonIcon, cancelEditButtonIcon);

                this.weightInfoPane.getChildren().remove(removeAppointmentButton);
            });

            navBar.getChildren().add(cancelEditButtonIcon);
        });

        navBar.getChildren().add(editButtonIcon);

        this.weightInfoPane.addRow(5, navBar);

        Scene weightInfoScene = new Scene(this.weightInfoPane, 250, 300);
        weightInfoScene.setFill(Color.TRANSPARENT);
        weightInfoScene.getStylesheets().add("style.css");

        weightInfoStage.initStyle(StageStyle.TRANSPARENT);
        weightInfoStage.setScene(weightInfoScene);
        weightInfoStage.showAndWait(); 
    }

    private void editDogWeightRecord() {
        if (Double.parseDouble(this.weightField.getText()) == this.weight &&
            this.dateField.getValue() == this.dateTaken)
        {
            Utils.displayAlert("No changes were made!", "");

            return;
        }

        DogWeightRecord editedDogWeightRecord = new DogWeightRecord(this.dogId,
                                                                    Double.parseDouble(this.weightField.getText()), 
                                                                    this.dateField.getValue());

        Database db = new Database();
        Boolean dogWeightRecordEditSuccessfull = db.commitDogWeightRecordEdit(editedDogWeightRecord);

        if (dogWeightRecordEditSuccessfull) {
            this.weight = editedDogWeightRecord.getWeight();
            this.dateTaken = editedDogWeightRecord.getDateTaken();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.weightInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                this.weightInfoStage.close();
            });

            this.weightInfoPane.setDisable(true);
            fadeOut.play();
        }
    }

    private void removeDogWeightRecord() {
        Database db = new Database();
        Boolean dogWeightRecordRemovalSuccessfull = db.deleteDogWeightRecord(this.weightId);

        if (dogWeightRecordRemovalSuccessfull) {
            this.weightId = -1;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.weightInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                this.weightInfoStage.close();
            });

            this.weightInfoPane.setDisable(true);
            fadeOut.play();   
        }
    }
}

import java.time.LocalDateTime;
import java.time.LocalTime;

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

public class Medication {
    private int medicationId;
    private int dogId;
    private String name;
    private String purpose;
    private String measuringUnit;
    private String note;
    private double quantity;
    private LocalDateTime dateAndTime;

    private Stage medicationInfoStage; 
    private GridPane medicationInfoPane;
    private TextField nameField;
    private TextField measuringUnitField;
    private TextField quantityField;
    private TextField purposeField;
    private TextField hourField;
    private TextField minuteField;
    private TextArea noteArea;
    private DatePicker dateField;

    public int getMedicationId() {
        return this.medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public int getDogId() {
        return this.dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getMeasuringUnit() {
        return this.measuringUnit;
    }

    public void setMeasuringUnit(String measuringUnit) {
        this.measuringUnit = measuringUnit;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDateAndTime() {
        return this.dateAndTime;
    }

    public void setDateAndTime(LocalDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    //Constructor for new medication
    protected Medication(int dogId, String name, String purpose, String measuringUnit, String note, double quantity, LocalDateTime dateAndTime) {
        this.dogId = dogId;
        this.name = name;
        this.purpose = purpose;
        this.note = note;
        this.measuringUnit = measuringUnit;
        this.quantity = quantity;
        this.dateAndTime = dateAndTime;
    }

    //Conscturcor for existing medication
    protected Medication(int medicationId, int dogId, String name, String purpose, String measuringUnit, String note, double quantity, LocalDateTime dateAndTime) {
        this.medicationId = medicationId;
        this.dogId = dogId;
        this.name = name;
        this.purpose = purpose;
        this.note = note;
        this.measuringUnit = measuringUnit;
        this.quantity = quantity;
        this.dateAndTime = dateAndTime;
    }

    protected Boolean addMedication() {
        Database db = new Database();

        if (db.insertNewMedication(this))
            return true;

        return false;
    }

    protected void displayMedicationInfo() {
        this.medicationInfoStage = new Stage();

        this.medicationInfoPane = new GridPane();

        this.medicationInfoPane.setVgap(10);
        this.medicationInfoPane.setHgap(10);
        this.medicationInfoPane.getStyleClass().add("popup-container");

        //Name area
        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.medicationInfoPane.addRow(0, nameLabel);

        this.nameField = new TextField(this.name);

        this.nameField.setDisable(true);
        this.nameField.setMinWidth(200);
        this.nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting name length
            if (newValue.length() > 200) {
                this.nameField.setText(oldValue);

                return;
            }

            if (newValue.equals(this.name)) 
                this.nameField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.nameField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
        });

        this.medicationInfoPane.addRow(1, this.nameField);

        //Purpose area
        Label purposeLabel = new Label("Purpose:");
        purposeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.medicationInfoPane.addRow(2, purposeLabel);

        this.purposeField = new TextField(this.purpose);

        this.purposeField.setDisable(true);
        this.purposeField.setMinWidth(200);
        this.purposeField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting purpose length
            if (newValue.length() > 200) {
                this.purposeField.setText(oldValue);
            
                return;
            }

            if (newValue.equals(this.purpose)) 
                this.purposeField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.purposeField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
        });

        this.medicationInfoPane.addRow(3, this.purposeField);

        //Quantity area
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.medicationInfoPane.addRow(4, quantityLabel);

        this.quantityField = new TextField(Double.toString(this.quantity));

        this.quantityField.setDisable(true);
        this.quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting quantity size
            if (newValue.length() > 3) {
                this.quantityField.setText(oldValue);

                return;
            }
            
            //Allowing only numbers
            this.quantityField.setText(this.quantityField.getText().replaceAll("[^0-9].", ""));

            if (this.quantityField.getText() != "") {
                if (Double.parseDouble(this.quantityField.getText()) == this.quantity) 
                    this.quantityField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
                else
                    this.quantityField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
            }
        });

        this.medicationInfoPane.addRow(5, quantityField);

        //Measuring unit area
        Label measuringUnitLabel = new Label("Measuring unti (short):");
        measuringUnitLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.medicationInfoPane.addRow(6, measuringUnitLabel);

        this.measuringUnitField = new TextField(this.measuringUnit);

        this.measuringUnitField.setDisable(true);
        this.measuringUnitField.setMinWidth(200);
        this.measuringUnitField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting measuring unit length
            if (newValue.length() > 5) {
                this.measuringUnitField.setText(oldValue);

                return;
            }

            if (newValue.equals(this.measuringUnit)) 
                this.measuringUnitField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.measuringUnitField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
        });

        this.medicationInfoPane.addRow(7, measuringUnitField);

        //Date and time area 
        HBox dateAndTimeBox = new HBox();
        dateAndTimeBox.setSpacing(15);

        Label dateLabel = new Label("Date:");
        dateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.medicationInfoPane.addRow(8, dateLabel);

        this.dateField = new DatePicker(this.dateAndTime.toLocalDate());   

        this.dateField.setDisable(true);
        this.dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == this.dateAndTime.toLocalDate())
                this.dateField.getEditor().setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.dateField.getEditor().setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
        });
        
        dateAndTimeBox.getChildren().add(this.dateField);

        this.hourField = new TextField((this.dateAndTime.getHour() < 10) ? ("0" + Integer.toString(this.dateAndTime.getHour())) : (Integer.toString(this.dateAndTime.getHour())));

        this.hourField.setDisable(true);
        this.hourField.setMaxWidth(40);
        this.hourField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting time length
            if (newValue.length() > 2) {
                this.hourField.setText(oldValue);

                return;
            }

            //Allowing only numbers
            this.hourField.setText(this.hourField.getText().replaceAll("[^0-9]", ""));

            if (this.hourField.getText() != "") {
                if (Integer.parseInt(this.hourField.getText()) > 24) {
                    this.hourField.setText(oldValue);
                    
                    return;
                }

                if (Integer.parseInt(this.hourField.getText()) == this.dateAndTime.getHour()) 
                    this.hourField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
                else
                    this.hourField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
            }
        });

        dateAndTimeBox.getChildren().addAll(this.hourField, new Label(":"));
        
        this.minuteField = new TextField((this.dateAndTime.getMinute() < 10) ? ("0" + Integer.toString(this.dateAndTime.getMinute())) : (Integer.toString(this.dateAndTime.getMinute())));

        this.minuteField.setDisable(true);
        this.minuteField.setMaxWidth(40);
        this.minuteField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting time length
            if (newValue.length() > 2) {
                this.minuteField.setText(oldValue);

                return;
            }

            //Allowing only numbers
            this.minuteField.setText(this.minuteField.getText().replaceAll("[^0-9]", ""));

            if (this.minuteField.getText() != "") {
                if (Integer.parseInt(this.minuteField.getText()) > 59) {
                    this.minuteField.setText(oldValue);

                    return;
                }

                if (Integer.parseInt(this.minuteField.getText()) == this.dateAndTime.getMinute()) 
                    this.minuteField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
                else
                    this.minuteField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
            }
        });

        dateAndTimeBox.getChildren().add(this.minuteField);

        this.medicationInfoPane.addRow(9, dateAndTimeBox);

        //Note area
        Label noteLabel = new Label("Note:");
        noteLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8");

        this.medicationInfoPane.addRow(10, noteLabel);

        this.noteArea = new TextArea(this.note);

        this.noteArea.setDisable(true);
        this.noteArea.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting note length
            if (newValue.length() > 4000) {
                this.noteArea.setText(oldValue);

                return;
            }

            if (newValue.equals(this.note)) 
                this.noteArea.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.noteArea.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
        });

        this.medicationInfoPane.addRow(11, this.noteArea);

        HBox navBar = new HBox();

        navBar.setAlignment(Pos.CENTER);
        navBar.setSpacing(15);

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 25, 25, "white");

        goBackButtonIcon.setOnMouseClicked(e1 -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.medicationInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                medicationInfoStage.close();
            });

            this.medicationInfoPane.setDisable(true);
            fadeOut.play();
        });

        navBar.getChildren().add(goBackButtonIcon);

        Region editButtonIcon = Utils.getSVG(SVGIcons.EDIT_PENCIL, 25, 25, "white");

        editButtonIcon.setOnMouseClicked(e1 -> {
            navBar.getChildren().removeAll(goBackButtonIcon, editButtonIcon);

            this.nameField.setDisable(false);
            this.nameField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.measuringUnitField.setDisable(false);
            this.measuringUnitField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.quantityField.setDisable(false);
            this.quantityField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.purposeField.setDisable(false);
            this.purposeField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.dateField.setDisable(false);
            this.dateField.getEditor().setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.hourField.setDisable(false);
            this.hourField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.minuteField.setDisable(false);
            this.minuteField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.noteArea.setDisable(false);
            this.noteArea.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            Button removemedicationButton = new Button("Remove Medication");

            removemedicationButton.setPrefWidth(200);
            removemedicationButton.getStyleClass().add("remove-button");
            removemedicationButton.setOnMouseClicked(e2 -> removeMedication());

            this.medicationInfoPane.addRow(12, removemedicationButton);
            GridPane.setHalignment(removemedicationButton, HPos.CENTER);

            Region confirmEditButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

            confirmEditButtonIcon.setOnMouseClicked(e -> {
                editMedication();
            });

            navBar.getChildren().add(confirmEditButtonIcon);
    
            Region cancelEditButtonIcon = Utils.getSVG(SVGIcons.FAILURE_X, 25, 25, "white");
    
            cancelEditButtonIcon.setOnMouseClicked(e -> {
                this.nameField.setDisable(true);
                this.nameField.setStyle("-fx-text-fill: white");
                this.nameField.setText(this.name);

                this.measuringUnitField.setDisable(true);
                this.measuringUnitField.setStyle("-fx-text-fill: white");
                this.measuringUnitField.setText(this.measuringUnit);

                this.quantityField.setDisable(true);
                this.quantityField.setStyle("-fx-text-fill: white");
                this.quantityField.setText(Double.toString(this.quantity));

                this.purposeField.setDisable(true);
                this.purposeField.setStyle("-fx-text-fill: white");
                this.purposeField.setText(this.purpose);

                this.dateField.setDisable(true);
                this.dateField.getEditor().setStyle("-fx-text-fill: white");
                this.dateField.setValue(this.dateAndTime.toLocalDate());

                this.hourField.setDisable(true);
                this.hourField.setStyle("-fx-text-fill: white");
                this.hourField.setText((this.dateAndTime.getHour() < 10) ? ("0" + Integer.toString(this.dateAndTime.getHour())) : (Integer.toString(this.dateAndTime.getHour())));

                this.minuteField.setDisable(true);
                this.minuteField.setStyle("-fx-text-fill: white");
                this.minuteField.setText((this.dateAndTime.getMinute() < 10) ? ("0" + Integer.toString(this.dateAndTime.getMinute())) : (Integer.toString(this.dateAndTime.getMinute())));

                this.noteArea.setDisable(true);
                this.noteArea.setStyle("-fx-text-fill: white");
                this.noteArea.setText(this.note);

                navBar.getChildren().addAll(goBackButtonIcon, editButtonIcon);
                navBar.getChildren().removeAll(confirmEditButtonIcon, cancelEditButtonIcon);

                this.medicationInfoPane.getChildren().remove(removemedicationButton);
            });

            navBar.getChildren().add(cancelEditButtonIcon);
        });

        navBar.getChildren().add(editButtonIcon);

        this.medicationInfoPane.addRow(13, navBar);

        Scene medicationInfoScene = new Scene(this.medicationInfoPane, 550, 600);
        medicationInfoScene.setFill(Color.TRANSPARENT);
        medicationInfoScene.getStylesheets().add("style.css");

        medicationInfoStage.initStyle(StageStyle.TRANSPARENT);
        medicationInfoStage.setScene(medicationInfoScene);
        medicationInfoStage.showAndWait(); 
    }

    private void editMedication() {
        if (this.nameField.getText().equals(this.name) &&
            this.purposeField.getText().equals(this.purpose) &&
            this.measuringUnitField.getText().equals(this.measuringUnit) &&
            this.quantityField.getText().equals(Double.toString(this.quantity)) &&
            this.dateField.getValue() == this.dateAndTime.toLocalDate() &&
            Integer.parseInt(this.hourField.getText()) == this.dateAndTime.getHour() &&
            Integer.parseInt(this.minuteField.getText()) == this.dateAndTime.getMinute() &&
            this.noteArea.getText().equals(this.note))
        {
            Utils.displayAlert("No changes were made!", "");

            return;
        }

        Medication editedMedication = new Medication(this.medicationId,
                                                    this.dogId,
                                                    this.nameField.getText(),
                                                    this.purposeField.getText(),
                                                    this.measuringUnitField.getText(),
                                                    this.noteArea.getText(),
                                                    Double.parseDouble(this.quantityField.getText()),
                                                    LocalDateTime.of(this.dateField.getValue(),
                                                                     LocalTime.of(Integer.parseInt(this.hourField.getText()),
                                                                                  Integer.parseInt(this.minuteField.getText()))));
        Database db = new Database();
        Boolean medicationEditSuccessfull = db.commitDogMedicationEdit(editedMedication);

        if(medicationEditSuccessfull) {
            this.name = editedMedication.getName();
            this.purpose = editedMedication.getPurpose();
            this.measuringUnit = editedMedication.getMeasuringUnit();
            this.quantity = editedMedication.getQuantity();
            this.note = editedMedication.getNote();
            this.dateAndTime = editedMedication.getDateAndTime();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.medicationInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                medicationInfoStage.close();
            });

            this.medicationInfoPane.setDisable(true);
            fadeOut.play();
        }
    }

    private void removeMedication() {
        Database db = new Database();
        Boolean medicationRemovallSuccessfull = db.deleteDogMedication(this.dogId);

        if (medicationRemovallSuccessfull) {
            this.medicationId = -1;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.medicationInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                medicationInfoStage.close();
            });

            this.medicationInfoPane.setDisable(true);
            fadeOut.play(); 
        }
    }
}

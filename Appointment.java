import java.time.LocalDateTime;

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

public class Appointment {
    private int appointmentId;
    private int dogId;
    private String purpose;
    private String location;
    private String note;
    private LocalDateTime dateAndTime;

    private Stage appointmentInfoStage; 
    private GridPane appointmentInfoPane;
    private TextField purposeField;
    private TextField locationField;
    private TextField hourField;
    private TextField minuteField;
    private TextArea noteArea;
    private DatePicker dateField;

    //Constructor for new appointment
    protected Appointment(int dogId, String purpose, String locaiton, String note, LocalDateTime dateAndTime) {
        this.dogId = dogId;
        this.purpose = purpose;
        this.location = locaiton;
        this.dateAndTime = dateAndTime;
        this.note = note;
    }

    //Constructor for existing appointment
    protected Appointment(int appointmentId, int dogId, String purpose, String locaiton, String note, LocalDateTime dateAndTime) {
        this.appointmentId = appointmentId;
        this.dogId = dogId;
        this.purpose = purpose;
        this.location = locaiton;
        this.dateAndTime = dateAndTime;
        this.note = note;
    }

    public int getAppointmentId() {
        return this.appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getDogId() {
        return this.dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getDateAndTime() {
        return this.dateAndTime;
    }

    public void setDateAndTime(LocalDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    protected void displayAppointmentInfo() {
        this.appointmentInfoStage = new Stage();

        this.appointmentInfoPane = new GridPane();

        this.appointmentInfoPane.setVgap(10);
        this.appointmentInfoPane.setHgap(10);
        this.appointmentInfoPane.getStyleClass().add("popup-container");

        //Purpose area
        Label purposeLabel = new Label("Purpose:");
        purposeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.appointmentInfoPane.addRow(0, purposeLabel);

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

        this.appointmentInfoPane.addRow(1, this.purposeField);

        //Location area
        Label locaitonLabel = new Label("Location:");
        locaitonLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.appointmentInfoPane.addRow(2, locaitonLabel);

        this.locationField = new TextField(this.location);

        this.locationField.setDisable(true);
        this.locationField.setMinWidth(200);
        this.locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting location length
            if (newValue.length() > 200) {
                this.locationField.setText(oldValue);

                return;
            }

            if (newValue.equals(this.location)) 
                this.locationField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
            else
                this.locationField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
        });

        this.appointmentInfoPane.addRow(3, this.locationField);

        //Date and time area 
        HBox dateAndTimeBox = new HBox();
        dateAndTimeBox.setSpacing(15);

        Label dateLabel = new Label("Date:");
        dateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.appointmentInfoPane.addRow(4, dateLabel);

        this.dateField = new DatePicker(this.dateAndTime.toLocalDate());   

        this.dateField.setDisable(true);
        this.dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == this.dateAndTime.toLocalDate())
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

            if (this.hourField.getText() != "")
                if (Integer.parseInt(this.hourField.getText()) > 24) {
                    this.hourField.setText(oldValue);
                    
                    return;
                }

                if (Integer.parseInt(this.hourField.getText()) == this.dateAndTime.getHour()) 
                    this.hourField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");
                else
                    this.hourField.setStyle("-fx-text-fill: radial-gradient(radius 150%, #9EE493, derive(#86BBD8, 60%))");
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

        this.appointmentInfoPane.addRow(5, dateAndTimeBox);

        //Note area
        Label noteLabel = new Label("Note:");
        noteLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8");

        this.appointmentInfoPane.addRow(6, noteLabel);

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

        this.appointmentInfoPane.addRow(7, this.noteArea);

        HBox navBar = new HBox();

        navBar.setAlignment(Pos.CENTER);
        navBar.setSpacing(15);

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 25, 25, "white");

        goBackButtonIcon.setOnMouseClicked(e1 -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.appointmentInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                appointmentInfoStage.close();
            });

            this.appointmentInfoPane.setDisable(true);
            fadeOut.play();
        });

        navBar.getChildren().add(goBackButtonIcon);

        Region editButtonIcon = Utils.getSVG(SVGIcons.EDIT_PENCIL, 25, 25, "white");

        editButtonIcon.setOnMouseClicked(e1 -> {
            navBar.getChildren().removeAll(goBackButtonIcon, editButtonIcon);

            this.purposeField.setDisable(false);
            this.purposeField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.locationField.setDisable(false);
            this.locationField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.dateField.setDisable(false);
            this.dateField.getEditor().setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.hourField.setDisable(false);
            this.hourField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.minuteField.setDisable(false);
            this.minuteField.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            this.noteArea.setDisable(false);
            this.noteArea.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.6)");

            Button removeAppointmentButton = new Button("Remove appointment");

            removeAppointmentButton.setPrefWidth(200);
            removeAppointmentButton.getStyleClass().add("remove-button");
            removeAppointmentButton.setOnMouseClicked(e2 -> removeAppointment());

            this.appointmentInfoPane.addRow(8, removeAppointmentButton);
            GridPane.setHalignment(removeAppointmentButton, HPos.CENTER);

            Region confirmEditButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

            confirmEditButtonIcon.setOnMouseClicked(e -> {
                editAppointment();
            });

            navBar.getChildren().add(confirmEditButtonIcon);
    
            Region cancelEditButtonIcon = Utils.getSVG(SVGIcons.FAILURE_X, 25, 25, "white");
    
            cancelEditButtonIcon.setOnMouseClicked(e -> {
                this.purposeField.setDisable(true);
                this.purposeField.setStyle("-fx-text-fill: white");
                this.purposeField.setText(this.purpose);

                this.locationField.setDisable(true);
                this.locationField.setStyle("-fx-text-fill: white");
                this.locationField.setText(this.location);

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

                this.appointmentInfoPane.getChildren().remove(removeAppointmentButton);
            });

            navBar.getChildren().add(cancelEditButtonIcon);
        });

        navBar.getChildren().add(editButtonIcon);

        this.appointmentInfoPane.addRow(9, navBar);

        Scene appointmentInfoScene = new Scene(this.appointmentInfoPane, 500, 440);
        appointmentInfoScene.setFill(Color.TRANSPARENT);
        appointmentInfoScene.getStylesheets().add("style.css");

        appointmentInfoStage.initStyle(StageStyle.TRANSPARENT);
        appointmentInfoStage.setScene(appointmentInfoScene);
        appointmentInfoStage.showAndWait(); 
    }

    protected Boolean addAppointment() {
        Database db = new Database();
        
        if (db.insertNewAppointmet(this))
            return true;

        return false;
    }

    private void editAppointment() {
        if (this.purposeField.getText().equals(this.purpose) &&
            this.locationField.getText().equals(this.location) &&
            this.dateField.getValue() == this.dateAndTime.toLocalDate() &&
            Integer.parseInt(this.hourField.getText()) == this.dateAndTime.getHour() &&
            Integer.parseInt(this.minuteField.getText()) == this.dateAndTime.getMinute() &&
            this.noteArea.getText().equals(this.note))
        {
            Utils.displayAlert("No changes were made!", "");

            return;
        }

        Appointment editedAppointment = new Appointment(this.appointmentId,
                                                        this.dogId, 
                                                        this.purposeField.getText(), 
                                                        this.locationField.getText(), 
                                                        this.noteArea.getText(), 
                                                        this.dateField.getValue().atTime(Integer.parseInt(this.hourField.getText()), 
                                                                                         Integer.parseInt(this.minuteField.getText())));

        Database db = new Database();
        Boolean appointmentEditSuccessfull = db.commitDogAppointmentEdit(editedAppointment);

        if (appointmentEditSuccessfull) {
            this.purpose = editedAppointment.getPurpose();
            this.location = editedAppointment.getLocation();
            this.note = editedAppointment.getNote();
            this.dateAndTime = editedAppointment.getDateAndTime();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.appointmentInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                this.appointmentInfoStage.close();
            });

            this.appointmentInfoPane.setDisable(true);
            fadeOut.play();
        }
    }

    private void removeAppointment() {

        Database db = new Database();
        Boolean appointmentRemovalSuccessfull = db.deleteDogAppointmet(this.appointmentId);

        if (appointmentRemovalSuccessfull) {
            this.appointmentId = -1;

            FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.appointmentInfoPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);
            fadeOut.setOnFinished(e2 -> {
                this.appointmentInfoStage.close();
            });

            this.appointmentInfoPane.setDisable(true);
            fadeOut.play();

            return;
        }

    }
}

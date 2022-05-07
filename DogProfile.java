import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DogProfile {
    private Dog selectedDog;
    private ObservableList<Appointment> appointments;
    private ObservableList<Medication> medications;
    private ObservableList<DogWeightRecord> dogWeightRecords;

    private Stage primaryStage;
    private SplitPane primaryPane;
    private GridPane leftPane;
    private GridPane rightPane;
    private GridPane rightCenterPane;
    private Text appointmentInfo;
    private Text medsInfo;
    private LineChart<String, Number> weightChart;

    protected DogProfile(Stage primaryStage, Dog selectedDog, ObservableList<Appointment> appointments, ObservableList<Medication> medications, ObservableList<DogWeightRecord> dogWeightRecords) {
        this.selectedDog = selectedDog;
        this.appointments = appointments;
        this.medications = medications;
        this.dogWeightRecords = dogWeightRecords;
        
        this.primaryStage = primaryStage;
        this.primaryPane = new SplitPane();

        Scene mainScene = new Scene(this.primaryPane, 1200, 950);
        mainScene.getStylesheets().add("style.css");
        this.primaryStage.setScene(mainScene);

        this.leftPane  = new GridPane();
        this.rightPane = new GridPane();
        this.rightCenterPane = new GridPane();

        this.rightCenterPane.getStyleClass().add("content-container");
        this.rightCenterPane.setVgap(5);

        this.leftPane.getStyleClass().add("profile-container");
        this.leftPane.setAlignment(Pos.CENTER);
        
        this.rightPane.setAlignment(Pos.CENTER);
        this.rightPane.add(this.rightCenterPane, 0, 0);

        //Splitting scene into two sides
        this.primaryPane.getItems().addAll(this.leftPane, this.rightPane);
        this.primaryPane.setDividerPositions(0.35, 0.65);

        //Fixing sides
        this.leftPane.maxWidthProperty().bind(this.primaryPane.widthProperty().multiply(0.35));
        this.leftPane.minWidthProperty().bind(this.primaryPane.widthProperty().multiply(0.35));
        this.rightPane.maxWidthProperty().bind(this.primaryPane.widthProperty().multiply(0.65));
        this.rightPane.minWidthProperty().bind(this.primaryPane.widthProperty().multiply(0.65));
    }

    protected void displayDogProfile() {
        VBox leftCenterPane = new VBox();

        leftCenterPane.setSpacing(10);
        leftCenterPane.setAlignment(Pos.CENTER);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.primaryPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.rightCenterPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        //Dog data
        Region pawIcon = Utils.getSVG(SVGIcons.DOG_PAW, 140, 140, "white");
        Label dogNameLabel = new Label(this.selectedDog.getName());
        Text dogInfo = new Text();
        Period dogAgePeriod = Period.between(this.selectedDog.getBirthDate(), LocalDate.now());
        HBox dogNavBar = new HBox();
        Region editDogButtonIcon = Utils.getSVG(SVGIcons.EDIT_PENCIL, 20, 20, "white");
        Region shareDogButtonIcon = Utils.getSVG(SVGIcons.SHARE, 20, 20, "white");
        Tooltip shareDogTooltip = new Tooltip("Click to copy share code");

        if (dogAgePeriod.getYears() != 0) {
            dogInfo.setText(this.selectedDog.getCoatColor() + "\n" +
                            this.selectedDog.getBreed() + "\n" +
                            Integer.toString(dogAgePeriod.getYears()) + " Years old\n" +
                            this.selectedDog.getNote());
            
        } else if (dogAgePeriod.getMonths() != 0) {
            dogInfo.setText(this.selectedDog.getCoatColor() + "\n" +
                            this.selectedDog.getBreed() + "\n" +
                            Integer.toString(dogAgePeriod.getMonths()) + " Months old\n" +
                            this.selectedDog.getNote());
        } else {
            dogInfo.setText(this.selectedDog.getCoatColor() + "\n" +
                            this.selectedDog.getBreed() + "\n" +
                            Integer.toString(dogAgePeriod.getDays()) + " Days old\n" +
                            this.selectedDog.getNote());            
        }

        editDogButtonIcon.setOnMouseClicked(e -> {
            this.primaryPane.setDisable(true);
            this.selectedDog.displayDogInfo();
            this.primaryPane.setDisable(false);

            //If dog id has been set to -1, the dog as been removed
            if (this.selectedDog.getDogId() == -1) {
                Database db = new Database();
                User loggedInUser = db.getUserById(this.selectedDog.getUserId());
                ArrayList<Dog> userDogs = db.getUserDogs(this.selectedDog.getUserId());

                Account account = new Account(this.primaryStage, loggedInUser, userDogs);
                account.displayUserDogs();

                return;
            }

            if (dogAgePeriod.getYears() != 0) {
                dogInfo.setText(this.selectedDog.getCoatColor() + "\n" +
                                this.selectedDog.getBreed() + "\n" +
                                Integer.toString(dogAgePeriod.getYears()) + " Years old\n" +
                                this.selectedDog.getNote());
                
            } else if (dogAgePeriod.getMonths() != 0) {
                dogInfo.setText(this.selectedDog.getCoatColor() + "\n" +
                                this.selectedDog.getBreed() + "\n" +
                                Integer.toString(dogAgePeriod.getMonths()) + " Months old\n" +
                                this.selectedDog.getNote());
            } else {
                dogInfo.setText(this.selectedDog.getCoatColor() + "\n" +
                                this.selectedDog.getBreed() + "\n" +
                                Integer.toString(dogAgePeriod.getDays()) + " Days old\n" +
                                this.selectedDog.getNote());            
            }
        });

        shareDogTooltip.setShowDelay(Duration.millis(1));
        Tooltip.install(shareDogButtonIcon, shareDogTooltip);

        shareDogButtonIcon.setOnMouseExited(e -> shareDogTooltip.setText("Click to copy share code"));
        shareDogButtonIcon.setOnMouseClicked(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipBoardcontent = new ClipboardContent();

            clipBoardcontent.putString(this.selectedDog.getShareCode().toString());
            clipboard.setContent(clipBoardcontent);

            shareDogTooltip.setText("Share code copied!");
        });

        dogNameLabel.setStyle("-fx-font-weight: bold");

        dogInfo.setFill(Color.WHITE);
        dogInfo.setTextAlignment(TextAlignment.CENTER);
        dogInfo.setWrappingWidth(200);

        dogNavBar.setSpacing(5);
        dogNavBar.setAlignment(Pos.CENTER);
        dogNavBar.getChildren().addAll(editDogButtonIcon, shareDogButtonIcon);

        //Element seperation strip1
        Rectangle strip1 = new Rectangle();

        strip1.setWidth(200);
        strip1.setHeight(5);
        strip1.setArcWidth(10);
        strip1.setArcHeight(10);
        strip1.setFill(Color.TURQUOISE);

        //Nearest appointment box
        VBox appointmentBox = new VBox();
        HBox appointmentNavbar = new HBox();
        Label appointmentLabel = new Label("Next appointment ");
        Pane appointmentInfoContainer = new Pane();
        this.appointmentInfo = new Text("First vaccine * 11:00 AM\n47 Oldmarket road, CB1 6BE");
        Region addAppointmentButtonIcon = Utils.getSVG(SVGIcons.PLUS, 20, 20, "white");
        Region viewAppointmentsButtonIcon = Utils.getSVG(SVGIcons.LIST, 20, 20, "white");

        setNearestAppointmentData();

        appointmentLabel.setStyle("-fx-font-weight: bold");

        this.appointmentInfo.setFill(Color.WHITE);
        this.appointmentInfo.wrappingWidthProperty().set(200);

        addAppointmentButtonIcon.setOnMouseClicked(e -> {
            fadeOut.setOnFinished(e2 -> {
                displayNewAppointmentForm();
                this.leftPane.setDisable(false);
            });

            this.leftPane.setDisable(true);
            fadeOut.play();
        });

        viewAppointmentsButtonIcon.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> {
                displayAppointments();
                this.leftPane.setDisable(false);
            });

            this.leftPane.setDisable(true);
            fadeOut.play();
        });

        appointmentInfo.setWrappingWidth(200);

        appointmentInfoContainer.setMaxWidth(200);
        appointmentInfoContainer.setMaxHeight(200);
        appointmentInfoContainer.getChildren().add(appointmentInfo);

        appointmentNavbar.setSpacing(15);
        appointmentNavbar.getChildren().addAll(appointmentLabel, addAppointmentButtonIcon, viewAppointmentsButtonIcon);
        appointmentBox.setSpacing(5);
        appointmentBox.getChildren().addAll(appointmentNavbar, appointmentInfo);

        //Element seperation strip
        Rectangle strip2 = new Rectangle();
        strip2.setWidth(200);
        strip2.setHeight(5);
        strip2.setArcWidth(10);
        strip2.setArcHeight(10);
        strip2.setFill(Color.TURQUOISE);

        //Medication box
        VBox medsBox = new VBox();
        HBox medsNavbar = new HBox();
        Label medsLabel = new Label("Next medication: ");
        Pane medsInfoContainer = new Pane();
        this.medsInfo = new Text();
        Region addMedsButtonIcon = Utils.getSVG(SVGIcons.PLUS, 20, 20, "white");
        Region viewMedsButtonIcon = Utils.getSVG(SVGIcons.LIST, 20, 20, "white");

        setNearestMedicationData();

        medsLabel.setStyle("-fx-font-weight: bold");
        this.medsInfo.setFill(Color.WHITE);
        this.medsInfo.maxHeight(50);

        addMedsButtonIcon.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> {
                displayNewMedicationForm();
                this.leftPane.setDisable(false);
            });

            this.leftPane.setDisable(true);
            fadeOut.play();
        });

        viewMedsButtonIcon.setOnMouseClicked(e1 -> {
            fadeOut.setOnFinished(e2 -> {
                displayMedications();
                this.leftPane.setDisable(false);
            });

            this.leftPane.setDisable(true);
            fadeOut.play();
        });

        medsInfo.setWrappingWidth(200);

        medsInfoContainer.setMaxWidth(200);
        medsInfoContainer.setMaxHeight(200);
        medsInfoContainer.getChildren().add(medsInfo);

        medsNavbar.setSpacing(12);
        medsNavbar.getChildren().addAll(medsLabel, addMedsButtonIcon, viewMedsButtonIcon);
        medsNavbar.setAlignment(Pos.CENTER_LEFT);
        medsBox.setSpacing(5);
        medsBox.getChildren().addAll(medsNavbar, this.medsInfo);

        //Weight graph area
        VBox weightChartBox = new VBox();
        HBox weightNavbar = new HBox();
        Label wieghtChartLabel = new Label("  Weight chart");
        NumberAxis weightAxis = new NumberAxis();
        CategoryAxis dateAxis = new CategoryAxis();
        this.weightChart = new LineChart<>(dateAxis, weightAxis);
        Region addWeightRecordButtonIcon = Utils.getSVG(SVGIcons.PLUS, 20, 20, "white");
        Region viewWeightRecordsButtonIcon = Utils.getSVG(SVGIcons.LIST, 20, 20, "white");

        wieghtChartLabel.setStyle("-fx-font-weight: bold");

        weightChart.setMaxHeight(300);
        weightChart.setMaxWidth(300);
        weightChart.setLegendVisible(false);

        setWeightData();

        addWeightRecordButtonIcon.setOnMouseClicked(e -> {
            fadeOut.setOnFinished(e2 -> {
                displayNewWeightRecordForm();
                this.leftPane.setDisable(false);
            });

            this.leftPane.setDisable(true);
            fadeOut.play();            
        });

        viewWeightRecordsButtonIcon.setOnMouseClicked(e -> {
            fadeOut.setOnFinished(e2 -> {
                displayWeightRecrods();
                this.leftPane.setDisable(false);
            });

            this.leftPane.setDisable(true);
            fadeOut.play();            
        });

        weightNavbar.setSpacing(15);
        weightNavbar.setAlignment(Pos.CENTER);
        weightNavbar.getChildren().addAll(addWeightRecordButtonIcon, viewWeightRecordsButtonIcon);
        
        weightChartBox.setSpacing(5);
        weightChartBox.setAlignment(Pos.CENTER);
        weightChartBox.getChildren().addAll(wieghtChartLabel, weightChart, weightNavbar);

        Region goBackButtonIcon = Utils.getSVG(SVGIcons.BACK_ARROW, 25, 25, "white");
  
        goBackButtonIcon.setOnMouseClicked(e1 -> {
            Database db = new Database();

            User user = db.getUserById(selectedDog.getUserId());
            ArrayList<Dog> userDogs = db.getUserDogs(selectedDog.getUserId());

            if (user != null && userDogs != null) {
                FadeTransition fadeOutAll = new FadeTransition(Duration.millis(666), this.primaryPane);
                fadeOutAll.setFromValue(1);
                fadeOutAll.setToValue(0);
                fadeOutAll.setCycleCount(1);
                fadeOutAll.setOnFinished(e2 -> {
                    Account account = new Account(this.primaryStage, user, userDogs);
                    account.displayUserDogs();
                });

                this.primaryPane.setDisable(true);
                fadeOutAll.play();
            }
        });

        leftCenterPane.getChildren().addAll(pawIcon, dogNameLabel, dogInfo, dogNavBar, strip1, appointmentBox, strip2, medsBox, weightChartBox, goBackButtonIcon);
        this.leftPane.addRow(0, leftCenterPane);
    }

    private void setNearestAppointmentData() {
        if (this.appointments.isEmpty()) {
            this.appointmentInfo.setText("No appointments");

            return;
        }
            

        int nearestAppointmentIndex = 0;

        //Filtering out dates that are in the past
        for (int i = 0; i < this.appointments.size(); i++) {
            if (this.appointments.get(i).getDateAndTime().isBefore(LocalDateTime.now())) {
                i++;
                continue;
            }

            nearestAppointmentIndex = i;
            break;
        }

        //Calculating the time period between now and the next appointment in minutes
        LocalDateTime minDateTime = LocalDateTime.now();

        long years = minDateTime.until( this.appointments.get(nearestAppointmentIndex).getDateAndTime(), ChronoUnit.YEARS );
        minDateTime = minDateTime.plusYears( years );
        
        long months = minDateTime.until( this.appointments.get(nearestAppointmentIndex).getDateAndTime(), ChronoUnit.MONTHS );
        minDateTime = minDateTime.plusMonths( months );
        
        long days = minDateTime.until( this.appointments.get(nearestAppointmentIndex).getDateAndTime(), ChronoUnit.DAYS );
        minDateTime = minDateTime.plusDays( days );
        
        long hours = minDateTime.until( this.appointments.get(nearestAppointmentIndex).getDateAndTime(), ChronoUnit.HOURS );
        minDateTime = minDateTime.plusHours( hours );
        
        long minutes = minDateTime.until( this.appointments.get(nearestAppointmentIndex).getDateAndTime(), ChronoUnit.MINUTES );

        long minTimePeriod = (years * 525600) + (months * 43800) + (days * 1440) + (hours * 60) + minutes;
        
        for (int i = nearestAppointmentIndex; i < this.appointments.size(); i++) {
            //Filtering out dates that are in the past
            if (this.appointments.get(i).getDateAndTime().isBefore(LocalDateTime.now())) {
                i++;
                continue;
            }

            LocalDateTime tempDateTime = LocalDateTime.now();

            years = tempDateTime.until( this.appointments.get(i).getDateAndTime(), ChronoUnit.YEARS );
            tempDateTime = tempDateTime.plusYears( years );
            
            months = tempDateTime.until( this.appointments.get(i).getDateAndTime(), ChronoUnit.MONTHS );
            tempDateTime = tempDateTime.plusMonths( months );
            
            days = tempDateTime.until( this.appointments.get(i).getDateAndTime(), ChronoUnit.DAYS );
            tempDateTime = tempDateTime.plusDays( days );
            
            hours = tempDateTime.until( this.appointments.get(i).getDateAndTime(), ChronoUnit.HOURS );
            tempDateTime = tempDateTime.plusHours( hours );
            
            minutes = tempDateTime.until( this.appointments.get(i).getDateAndTime(), ChronoUnit.MINUTES );
            
            long tempTimePeriod = (years * 525600) + (months * 43800) + (days * 1440) + (hours * 60) + minutes;
               
            if (tempTimePeriod < minTimePeriod) {
                minTimePeriod = tempTimePeriod;
                nearestAppointmentIndex = i;
            }
        }

        this.appointmentInfo.setText(this.appointments.get(nearestAppointmentIndex).getPurpose() + "\n" + 
                                     this.appointments.get(nearestAppointmentIndex).getDateAndTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\n" + 
                                     this.appointments.get(nearestAppointmentIndex).getLocation() + "\nNote:\n" + 
                                     this.appointments.get(nearestAppointmentIndex).getNote());
  
    }

    private void setNearestMedicationData() {
        if (this.medications.isEmpty()) {
            this.medsInfo.setText("No appointments");

            return;
        }

        int nearestMedicationIndex = 0;

        //Filtering out dates that are in the past
        for (int i = 0; i < this.medications.size(); i++) {
            if (this.medications.get(i).getDateAndTime().isBefore(LocalDateTime.now())) {
                i++;
                continue;
            }

            nearestMedicationIndex = i;
            break;
        }

        //Calculating the time period between now and the next medication in minutes
        LocalDateTime minDateTime = LocalDateTime.now();

        long years = minDateTime.until( this.medications.get(nearestMedicationIndex).getDateAndTime(), ChronoUnit.YEARS );
        minDateTime = minDateTime.plusYears( years );
        
        long months = minDateTime.until( this.medications.get(nearestMedicationIndex).getDateAndTime(), ChronoUnit.MONTHS );
        minDateTime = minDateTime.plusMonths( months );
        
        long days = minDateTime.until( this.medications.get(nearestMedicationIndex).getDateAndTime(), ChronoUnit.DAYS );
        minDateTime = minDateTime.plusDays( days );
        
        long hours = minDateTime.until( this.medications.get(nearestMedicationIndex).getDateAndTime(), ChronoUnit.HOURS );
        minDateTime = minDateTime.plusHours( hours );
        
        long minutes = minDateTime.until( this.medications.get(nearestMedicationIndex).getDateAndTime(), ChronoUnit.MINUTES );

        long minTimePeriod = (years * 525600) + (months * 43800) + (days * 1440) + (hours * 60) + minutes;
        
        for (int i = nearestMedicationIndex; i < this.medications.size(); i++) {
            //Filtering out dates that are in the past
            if (this.medications.get(i).getDateAndTime().isBefore(LocalDateTime.now())) {
                i++;
                continue;
            }

            LocalDateTime tempDateTime = LocalDateTime.now();

            years = tempDateTime.until( this.medications.get(i).getDateAndTime(), ChronoUnit.YEARS );
            tempDateTime = tempDateTime.plusYears( years );
            
            months = tempDateTime.until( this.medications.get(i).getDateAndTime(), ChronoUnit.MONTHS );
            tempDateTime = tempDateTime.plusMonths( months );
            
            days = tempDateTime.until( this.medications.get(i).getDateAndTime(), ChronoUnit.DAYS );
            tempDateTime = tempDateTime.plusDays( days );
            
            hours = tempDateTime.until( this.medications.get(i).getDateAndTime(), ChronoUnit.HOURS );
            tempDateTime = tempDateTime.plusHours( hours );
            
            minutes = tempDateTime.until( this.medications.get(i).getDateAndTime(), ChronoUnit.MINUTES );
            
            long tempTimePeriod = (years * 525600) + (months * 43800) + (days * 1440) + (hours * 60) + minutes;
               
            if (tempTimePeriod < minTimePeriod) {
                minTimePeriod = tempTimePeriod;
                nearestMedicationIndex = i;
            }
        }

        this.medsInfo.setText(this.medications.get(nearestMedicationIndex).getName() + " " + medications.get(nearestMedicationIndex).getQuantity() + " " + this.medications.get(nearestMedicationIndex).getMeasuringUnit() + "\nFor " +
                                                   this.medications.get(nearestMedicationIndex).getPurpose() + "\n" + 
                                                   this.medications.get(nearestMedicationIndex).getDateAndTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\nNote:\n" + 
                                                   this.medications.get(nearestMedicationIndex).getNote());
    }

    private void setWeightData() {
        this.weightChart.getData().clear();

        if (this.dogWeightRecords.isEmpty()) {
            return;
        }

        this.dogWeightRecords.sort(Comparator.comparing(DogWeightRecord::getDateTaken));

        XYChart.Series<String, Number> weightData = new XYChart.Series<String, Number>();

        for (int i = 0; i < this.dogWeightRecords.size(); i++) {
            if (i > 4)
                break;

            weightData.getData().add(new XYChart.Data<>(this.dogWeightRecords.get(i).getDateTaken().toString(), this.dogWeightRecords.get(i).getWeight()));
        }

        this.weightChart.getData().add(weightData);
    }

    private void displayAppointments() {
        this.rightCenterPane.getChildren().clear();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.rightCenterPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        TableView<Appointment> appointmentTable = new TableView<>(this.appointments);

        TableColumn<Appointment, String> purposeColumn = new TableColumn<>("Purpose");
        purposeColumn.setMinWidth(200);
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        TableColumn<Appointment, LocalDateTime> dateAndTimeColumn = new TableColumn<Appointment, LocalDateTime>("Date");
        dateAndTimeColumn.setMinWidth(200);
        dateAndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));

        TableColumn<Appointment, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setMinWidth(200);
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        appointmentTable.getColumns().addAll(purposeColumn, dateAndTimeColumn, locationColumn);

        appointmentTable.setRowFactory(table -> {
            TableRow<Appointment> row = new TableRow<Appointment>();
        
            row.setOnMouseClicked(me -> {
                if (row.getIndex() >= appointments.size())
                    return;

                this.rightCenterPane.setDisable(true);
                this.appointments.get(row.getIndex()).displayAppointmentInfo();
                this.rightCenterPane.setDisable(false);

                //If appointment id has been set to -1, the appointment has been removed
                if (this.appointments.get(row.getIndex()).getAppointmentId() == -1) 
                    this.appointments.remove(row.getIndex());

                //Updating data after edits
                setNearestAppointmentData();                

                appointmentTable.refresh();
            });
        
            return row;
        });

        this.rightCenterPane.add(appointmentTable, 0, 0);

    }

    private void displayNewAppointmentForm() {
        this.rightCenterPane.getChildren().clear();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.rightCenterPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        //Purpose area
        Label purposeLabel = new Label("Purpose: *");
        purposeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(0, purposeLabel);

        TextField purposeField = new TextField();

        purposeField.setMinWidth(200);
        purposeField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting purpose length
            if (newValue.length() > 200)
                purposeField.setText(oldValue);
        });

        this.rightCenterPane.addRow(1, purposeField);

        //Location area
        Label locaitonLabel = new Label("Location: *");
        locaitonLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(2, locaitonLabel);

        TextField locationField = new TextField();

        locationField.setMinWidth(200);
        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting location length
            if (newValue.length() > 200)
                locationField.setText(oldValue);
        });

        this.rightCenterPane.addRow(3, locationField);

        //Date and time area 
        HBox dateAndTimeBox = new HBox();
        dateAndTimeBox.setSpacing(15);

        Label dateLabel = new Label("Date: *");
        dateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(4, dateLabel);

        //Date
        DatePicker dateField = new DatePicker();   
        
        dateAndTimeBox.getChildren().add(dateField);

        //Hour
        TextField hourField = new TextField();

        hourField.setMaxWidth(40);
        hourField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting time length
            if (newValue.length() > 2) {
                hourField.setText(oldValue);

                return;
            }

            //Allowing only numbers
            hourField.setText(hourField.getText().replaceAll("[^0-9]", ""));

            //Allowing only 24 hour format
            if (hourField.getText() != "") {
                if (Integer.parseInt(hourField.getText()) > 24) 
                    hourField.setText(oldValue);
            }
        });

        dateAndTimeBox.getChildren().addAll(hourField, new Label(":"));
        
        //Minute
        TextField minuteField = new TextField();

        minuteField.setMaxWidth(40);
        minuteField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting time length
            if (newValue.length() > 2) {
                minuteField.setText(oldValue);

                return;
            }

            //Allowing only numbers
            minuteField.setText(minuteField.getText().replaceAll("[^0-9]", ""));

            if (minuteField.getText() != "") {
                if (Integer.parseInt(minuteField.getText()) > 59)
                    minuteField.setText(oldValue);
            }
        });

        dateAndTimeBox.getChildren().add(minuteField);

        this.rightCenterPane.addRow(5, dateAndTimeBox);

        //Note area
        Label noteLabel = new Label("Note:");
        noteLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8");

        this.rightCenterPane.addRow(6, noteLabel);

        TextArea noteArea = new TextArea();

        noteArea.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting note length
            if (newValue.length() > 4000) 
                noteArea.setText(oldValue);
            
        });

        this.rightCenterPane.addRow(7, noteArea);

        Region confrimButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

        GridPane.setHalignment(confrimButtonIcon, HPos.CENTER);
        confrimButtonIcon.setOnMouseClicked(e1 -> {
            if (purposeField.getText() == "" ||
                locationField.getText() == "" || 
                hourField.getText() == "" ||
                minuteField.getText() == "" ||
                dateField.getValue() == null)
            {
                Utils.displayAlert("All obligatory field must be filled!", "");

                return;
            }

            Appointment newAppointment = new Appointment(this.selectedDog.getDogId(),
                                                         purposeField.getText(),
                                                         locationField.getText(),
                                                         noteArea.getText(),
                                                         LocalDateTime.of(dateField.getValue(), LocalTime.of(Integer.parseInt(hourField.getText()), Integer.parseInt(minuteField.getText()))));
            
            Boolean additionSuccessfull = newAppointment.addAppointment();

            if (additionSuccessfull) {
                this.appointments.add(newAppointment);

                //Refreshing nearset appointment
                setNearestAppointmentData();

                FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.rightCenterPane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> {
                    displayAppointments();
                    this.primaryPane.setDisable(false);
                });

                this.primaryPane.setDisable(true);
                fadeOut.play();
            }
        });

        this.rightCenterPane.addRow(8, confrimButtonIcon);
    }

    private void displayMedications() {
        this.rightCenterPane.getChildren().clear();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.rightCenterPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        TableView<Medication> medicationTable = new TableView<>(this.medications);

        TableColumn<Medication, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(120);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Medication, Double> quantityColumn = new TableColumn<>("Qty");
        quantityColumn.setMinWidth(50);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Medication, String> measuringUnitColumn = new TableColumn<>("MU");
        measuringUnitColumn.setMinWidth(50);
        measuringUnitColumn.setCellValueFactory(new PropertyValueFactory<>("measuringUnit"));

        TableColumn<Medication, String> purposeColumn = new TableColumn<>("Purpose");
        purposeColumn.setMinWidth(200);
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));

        TableColumn<Medication, LocalDateTime> dateAndTimeColumn = new TableColumn<Medication, LocalDateTime>("Date");
        dateAndTimeColumn.setMinWidth(200);
        dateAndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));

        medicationTable.getColumns().addAll(nameColumn, quantityColumn, measuringUnitColumn, purposeColumn, dateAndTimeColumn);

        medicationTable.setRowFactory(table -> {
            TableRow<Medication> row = new TableRow<Medication>();
        
            row.setOnMouseClicked(me -> {
                if (row.getIndex() >= this.medications.size())
                    return;

                this.rightCenterPane.setDisable(true);
                this.medications.get(row.getIndex()).displayMedicationInfo();
                this.rightCenterPane.setDisable(false);

                //If medication id has been set to -1, the medication has been removed
                if (this.medications.get(row.getIndex()).getMedicationId() == -1) 
                    this.medications.remove(row.getIndex());

                //Updating data after edits
                setNearestMedicationData();                

                medicationTable.refresh();
            });
        
            return row;
        });

        this.rightCenterPane.add(medicationTable, 0, 0);
    }

    private void displayNewMedicationForm() {
        this.rightCenterPane.getChildren().clear();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.rightCenterPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        //Name area
        Label nameLabel = new Label("Name: *");
        nameLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(0, nameLabel);

        TextField nameField = new TextField();

        nameField.setMinWidth(200);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting name length
            if (newValue.length() > 200)
                nameField.setText(oldValue);
        });

        this.rightCenterPane.addRow(1, nameField);

        //Purpose area
        Label purposeLabel = new Label("Purpose: *");
        purposeLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(2, purposeLabel);

        TextField purposeField = new TextField();

        purposeField.setMinWidth(200);
        purposeField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting purpose length
            if (newValue.length() > 200)
                purposeField.setText(oldValue);
        });

        this.rightCenterPane.addRow(3, purposeField);

        //Quantity area
        Label quantityLabel = new Label("Quantity *:");
        quantityLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(4, quantityLabel);

        TextField quantityField = new TextField();

        quantityField.setMinWidth(200);
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Allowing only numbers
            quantityField.setText(quantityField.getText().replaceAll("[^0-9.]", ""));

            //Restricting weight size
            if (quantityField.getText().length() > 3)
                quantityField.setText(oldValue);
        });

        this.rightCenterPane.addRow(5, quantityField);

        //Measuring unit area
        Label measuringUnitLabel = new Label("Measuring Unti (short) *:");
        measuringUnitLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(6, measuringUnitLabel);

        TextField measuringUnitField = new TextField();

        measuringUnitField.setMinWidth(200);
        measuringUnitField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting measuring unit length
            if (newValue.length() > 5)
                measuringUnitField.setText(oldValue);
        });

        this.rightCenterPane.addRow(7, measuringUnitField);

        //Date and time area 
        HBox dateAndTimeBox = new HBox();
        dateAndTimeBox.setSpacing(15);

        Label dateLabel = new Label("Date: *");
        dateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(8, dateLabel);

        //Date
        DatePicker dateField = new DatePicker(LocalDate.now());   

        dateField.valueProperty().addListener((observable, newValue, oldValue) -> {
            //Allowing only past and preset dates
            if (newValue.isAfter(LocalDate.now())) {
                 dateField.setValue(oldValue);
                 dateField.getEditor().setText(oldValue.toString());
            }
        });
        
        dateAndTimeBox.getChildren().add(dateField);

        //Hour
        TextField hourField = new TextField();

        hourField.setMaxWidth(40);
        hourField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting time length
            if (newValue.length() > 2) {
                hourField.setText(oldValue);

                return;
            }

            //Allowing only numbers
            hourField.setText(hourField.getText().replaceAll("[^0-9]", ""));

            //Allowing only 24 hour format
            if (hourField.getText() != "") {
                if (Integer.parseInt(hourField.getText()) > 24) 
                    hourField.setText(oldValue);
            }
        });

        dateAndTimeBox.getChildren().addAll(hourField, new Label(":"));
        
        //Minute
        TextField minuteField = new TextField();

        minuteField.setMaxWidth(40);
        minuteField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting time length
            if (newValue.length() > 2) {
                minuteField.setText(oldValue);

                return;
            }

            //Allowing only numbers
            minuteField.setText(minuteField.getText().replaceAll("[^0-9]", ""));

            if (minuteField.getText() != "") {
                if (Integer.parseInt(minuteField.getText()) > 59)
                    minuteField.setText(oldValue);
            }
        });

        dateAndTimeBox.getChildren().add(minuteField);

        this.rightCenterPane.addRow(9, dateAndTimeBox);

        //Note area
        Label noteLabel = new Label("Note:");
        noteLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8");

        this.rightCenterPane.addRow(10, noteLabel);

        TextArea noteArea = new TextArea();

        noteArea.textProperty().addListener((observable, oldValue, newValue) -> {
            //Restricting note length
            if (newValue.length() > 4000) 
                minuteField.setText(oldValue);
            
        });

        this.rightCenterPane.addRow(11, noteArea);

        Region confrimButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

        GridPane.setHalignment(confrimButtonIcon, HPos.CENTER);
        confrimButtonIcon.setOnMouseClicked(e1 -> {
            if (nameField.getText() == "" ||
                quantityField.getText() == "" ||
                measuringUnitField.getText() == "" ||
                purposeField.getText() == "" || 
                hourField.getText() == "" ||
                minuteField.getText() == "" ||
                dateField.getValue() == null)
            {
                Utils.displayAlert("All obligatory field must be filled!", "");

                return;
            }

            Medication newMedication = new Medication(this.selectedDog.getDogId(),
                                                      nameField.getText(),
                                                      purposeField.getText(), 
                                                      measuringUnitField.getText(),
                                                      noteArea.getText(),
                                                      Double.parseDouble(quantityField.getText()),
                                                      LocalDateTime.of(dateField.getValue(),
                                                                       LocalTime.of(Integer.parseInt(hourField.getText()),
                                                                                    Integer.parseInt(minuteField.getText()))));

            Boolean additionSuccessfull = newMedication.addMedication();

            if (additionSuccessfull) {
                this.medications.add(newMedication);

                //Refreshing nearset medication
                setNearestMedicationData();

                FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.rightCenterPane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> {
                    displayMedications();
                    this.primaryPane.setDisable(false);
                });

                this.primaryPane.setDisable(true);
                fadeOut.play();
            }
        });

        this.rightCenterPane.addRow(12, confrimButtonIcon);
    }

    private void displayWeightRecrods() {
        this.rightCenterPane.getChildren().clear();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.rightCenterPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        TableView<DogWeightRecord> weightTable = new TableView<>(this.dogWeightRecords);

        TableColumn<DogWeightRecord, Double> weightColumn = new TableColumn<>("Weight");
        weightColumn.setMinWidth(200);
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<DogWeightRecord, LocalDate> dateColumn = new TableColumn<>("Date Taken");
        dateColumn.setMinWidth(200);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTaken"));

        weightTable.getColumns().addAll(weightColumn, dateColumn);

        weightTable.setRowFactory(table -> {
            TableRow<DogWeightRecord> row = new TableRow<>();

            row.setOnMouseClicked(e ->{
                if (row.getIndex() >= this.dogWeightRecords.size())
                    return;

                this.dogWeightRecords.get(row.getIndex()).displayDogWeightInfo();

                //Weigt ID has been set o -1, the weight record has been removed
                if (this.dogWeightRecords.get(row.getIndex()).getWeightId() == -1)
                    this.dogWeightRecords.remove(row.getIndex());

                //Refreshing weight chart
                setWeightData();
            });

            return row;
        });

        this.rightCenterPane.addRow(0, weightTable);
    }

    private void displayNewWeightRecordForm() {
        this.rightCenterPane.getChildren().clear();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(666), this.rightCenterPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        fadeIn.play();

        //Weight area
        Label weightLabel = new Label("Weight: *");
        weightLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(0, weightLabel);

        TextField weightField = new TextField();

        weightField.setMinWidth(200);
        weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            //Allowing only numbers
            weightField.setText(weightField.getText().replaceAll("[^0-9.]", ""));

            //Restricting weight size
            if (newValue.length() > 3)
                weightField.setText(oldValue);
        });

        this.rightCenterPane.addRow(1, weightField);

        //Date area
        Label dateLabel = new Label("Date: *");
        dateLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.8)");

        this.rightCenterPane.addRow(2, dateLabel);

        DatePicker dateField = new DatePicker(LocalDate.now());

        dateField.valueProperty().addListener((observable, newValue, oldValue) -> {
            //Allowing only past and preset dates
            if (newValue.isAfter(LocalDate.now())) {
                dateField.setValue(oldValue);
                dateField.getEditor().setText(oldValue.toString());
            }
        });

        this.rightCenterPane.addRow(3, dateField);

        //Controls
        Region confirmButtonIcon = Utils.getSVG(SVGIcons.SUCCESS_CHECK_MARK, 25, 25, "white");

        GridPane.setHalignment(confirmButtonIcon, HPos.CENTER);
        confirmButtonIcon.setOnMouseClicked(e -> {
            if (weightField.getText() == "" || dateField.getValue() == null) {
                Utils.displayAlert("Obligatory fields must be filled!", "");

                return;
            }

            DogWeightRecord newWeightRecord = new DogWeightRecord(this.selectedDog.getDogId(),
                                                                  Double.parseDouble(weightField.getText()),
                                                                  dateField.getValue());

            Boolean weightRecordAdditionSuccessfull = newWeightRecord.addDogWeightRecord();
            
            if (weightRecordAdditionSuccessfull) {
                this.dogWeightRecords.add(newWeightRecord);

                //Refreshing wieght chart
                setWeightData();

                FadeTransition fadeOut = new FadeTransition(Duration.millis(666), this.rightCenterPane);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e2 -> {
                    displayWeightRecrods();
                    this.primaryPane.setDisable(false);
                });

                this.primaryPane.setDisable(true);
                fadeOut.play();
            }
        });

        this.rightCenterPane.addRow(4, confirmButtonIcon);
    }

}

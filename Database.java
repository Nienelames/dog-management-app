import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Database {
    private static Connection conn;

    protected Database() {}

    private void connectDatabase() {
        try {
            Properties props = new Properties();
            props.setProperty("user", "nedzuvwqyfmvrj");
            props.setProperty("password", "e9825aae75d826b679eed3dbbfb584e41829b9d6cc44128f272eded729ed9cb3");
            props.setProperty("ssl", "false");

            Database.conn = DriverManager.getConnection("jdbc:postgresql://ec2-34-255-225-151.eu-west-1.compute.amazonaws.com:5432/d7kjpaba3m1jg5", props);
        } catch (SQLException e) {
            Utils.displayAlert("Database connection failed", e.toString());
        }
    }

    protected Boolean insertNewUser(User newUser) {
        connectDatabase();

        String duplicateUserCheck = "SELECT email " +
                                    "FROM dog.user " + 
                                    "WHERE email = ?";
        String userInsert = "INSERT INTO dog.user (email, password) " +
                            "VALUES (?, crypt(?, gen_salt('bf')))";

        String sql = "INSERT INTO public.Owner (name, lastname, username, email, password, contact_number) VALUES (?, ?, ?, ?, crypt(?, gen_salt('bf')), ?)";                   

        //Checking for duplicate user email
        try (PreparedStatement duplicateUserCheckStmt = Database.conn.prepareStatement(duplicateUserCheck);
            PreparedStatement userInsertStmt = Database.conn.prepareStatement(userInsert)) {

            duplicateUserCheckStmt.setString(1, newUser.getEmail());

            ResultSet duplicateUserEmail = duplicateUserCheckStmt.executeQuery();

            if (duplicateUserEmail.next()) {
                Utils.displayAlert("This email is already registered!", "");
                
                return false;
            }

            //Inserting new user
            userInsertStmt.setString(1, newUser.getEmail());
            userInsertStmt.setString(2, newUser.getPassword());
            userInsertStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("User registration failed", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean getUserByCredentials(User user) {
        connectDatabase();

        String checkPassword = "SELECT user_id, email " +
                               "FROM dog.user " +
                               "WHERE email = ? AND password = crypt(?, password)";

        try (PreparedStatement getUserStmt = Database.conn.prepareStatement(checkPassword)) {
            getUserStmt.setString(1, user.getEmail());
            getUserStmt.setString(2, user.getPassword());

            ResultSet userData = getUserStmt.executeQuery();

            while(userData.next()) {
                user.setUserId(userData.getInt("user_id"));

                return true;
            }

            return false;
        } catch (SQLException e) {
            Utils.displayAlert("Password check failed", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected User getUserById(int userId) {
        connectDatabase();

        String userRequest = "SELECT email " +
                             "FROM dog.user " +
                             "WHERE user_id = ?";
        
        try (PreparedStatement userRequestStmt = Database.conn.prepareStatement(userRequest)) {
            userRequestStmt.setInt(1, userId);

            ResultSet userData = userRequestStmt.executeQuery();
            userData.next();

            return new User(userId, userData.getString("email"));
        } catch (SQLException e) {
            Utils.displayAlert("Failed to fetch user data", e.toString());
        } finally {
            closeConnection();
        }

        return null;
    }

    protected Boolean commitUserEdit(User editedUser) {
        connectDatabase();

        String duplicateEmailCheck = "SELECT user_id " +
                                     "FROM dog.user " +
                                     "WHERE email = ?";
        String userEdit = "UPDATE dog.user " +
                          "SET email = ?, password = crypt(?, gen_salt('bf')) " +
                          "WHERE user_id = ?";

        try (PreparedStatement duplicateUserCheckStmt = Database.conn.prepareStatement(duplicateEmailCheck);
            PreparedStatement userEditStmt = Database.conn.prepareStatement(userEdit)) {
            duplicateUserCheckStmt.setString(1, editedUser.getEmail());

            if (duplicateUserCheckStmt.executeQuery().next()) {
                Utils.displayAlert("This email is already in use!", "");

                return false;
            }
                    
            userEditStmt.setString(1, editedUser.getEmail());
            userEditStmt.setString(2, editedUser.getPassword());
            userEditStmt.setInt(3, editedUser.getUserId());

            userEditStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Account edit failed!", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean deleteUser(int userId) {
        connectDatabase();

        String userRemoveal = "DELETE FROM dog.user " +
                              "WHERE user_id = ?";
        //Removing only those dogs that are not shared with other users
        String userDogsRemoval = "DELETE FROM dog.dog " +
                                 "WHERE dog_id IN (SELECT dog_id " +
                                                  "FROM dog.user_dogs " +
                                                  "WHERE dog_id IN (SELECT dog_id " +
                                                                   "FROM dog.user_dogs " +
                                                                   "WHERE user_id = ?) " +
                                                  "GROUP BY dog_id " +
                                                  "HAVING COUNT(dog_id) = 1)";

        try (PreparedStatement userRemovalStmt = Database.conn.prepareStatement(userRemoveal);
            PreparedStatement userDogsRemovalStmt = Database.conn.prepareStatement(userDogsRemoval)) {
            Database.conn.setAutoCommit(false);

            userRemovalStmt.setInt(1, userId);
            userRemovalStmt.executeUpdate();

            userDogsRemovalStmt.setInt(1, userId);
            userDogsRemovalStmt.executeUpdate();

            Database.conn.commit();
            Database.conn.setAutoCommit(true);

            return true;
        } catch (SQLException e1) {
            Utils.displayAlert("Account removal failed", e1.toString());

            try {
                Database.conn.rollback();
            } catch (SQLException e2) {
                Utils.displayAlert("Internal database error", e2.toString());
            }
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean insertNewDog(Dog newDog) {
        connectDatabase();

        String insertDog = "INSERT INTO dog.dog (name, coat_color, birth_date, breed, note) " +
                           "VALUES (?, ?, ?, ?, ?) " +
                           "RETURNING dog_id";
        String insertDogRelation = "INSERT INTO dog.user_dogs (user_id, dog_id) " +
                                   "VALUES (?, ?)";

        try (PreparedStatement insertDogStmt = Database.conn.prepareStatement(insertDog);
            PreparedStatement insertDogRelationStmt = Database.conn.prepareStatement(insertDogRelation)) 
        {   
            Database.conn.setAutoCommit(false);

            insertDogStmt.setString(1, newDog.getName());
            insertDogStmt.setString(2, newDog.getCoatColor());
            insertDogStmt.setDate(3, Date.valueOf(newDog.getBirthDate()));
            insertDogStmt.setString(4, newDog.getBreed());
            insertDogStmt.setString(5, newDog.getNote());

            ResultSet dogId = insertDogStmt.executeQuery();
            dogId.next();

            Database.conn.commit();

            insertDogRelationStmt.setInt(1, newDog.getUserId());
            insertDogRelationStmt.setInt(2, dogId.getInt("dog_id"));

            insertDogRelationStmt.executeUpdate();

            Database.conn.commit();
            Database.conn.setAutoCommit(true);

            return true;
        } catch (SQLException e1) {
            Utils.displayAlert("Failed to add new dog", e1.toString());

            try {
                Database.conn.rollback();
            } catch (SQLException e2) {
                Utils.displayAlert("Internal database error", e2.toString());
            }
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean insertExistingDog(Dog sharedDog) {
        connectDatabase();

        String getDogId = "SELECT dog_id " +
                          "FROM dog.dog " +
                          "WHERE share_code = ?";
        String duplicateDogCheck = "SELECT user_id " +
                                   "FROM dog.user_dogs " +
                                   "WHERE user_id = ? AND dog_id = ?";
        String insertExistingDog = "INSERT INTO dog.user_dogs (user_id, dog_id) " +
                                   "VALUES (?, ?)";

        try (PreparedStatement getDogIdStmt = Database.conn.prepareStatement(getDogId);
            PreparedStatement duplicateDogCheckStmt = Database.conn.prepareStatement(duplicateDogCheck);
            PreparedStatement insertExistingDogStmt = Database.conn.prepareStatement(insertExistingDog)) 
        {
            getDogIdStmt.setObject(1, sharedDog.getShareCode());

            ResultSet dogId = getDogIdStmt.executeQuery();

            if (!dogId.next()) {
                Utils.displayAlert("Dog not found!", "");
                
                return false;
            }

            duplicateDogCheckStmt.setInt(1, sharedDog.getUserId());
            duplicateDogCheckStmt.setInt(2, dogId.getInt("dog_id"));

            ResultSet duplicateDog = duplicateDogCheckStmt.executeQuery();

            if (duplicateDog.next()) {
                Utils.displayAlert("Dog already added!", "");

                return false;
            }

            insertExistingDogStmt.setInt(1, sharedDog.getUserId());
            insertExistingDogStmt.setInt(2, dogId.getInt("dog_id"));

            insertExistingDogStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to add dog", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected ArrayList<Dog> getUserDogs(int userId) {
        connectDatabase();

        ArrayList<Dog> userDogs = new ArrayList<>();
        String dogsRequest = "SELECT dog.*, CASE " +
                                             "WHEN (SELECT COUNT(user_id) FROM dog.user_dogs WHERE dog_id = dog.dog_id) = 1 THEN FALSE " +
                                             "ELSE TRUE " +
                                            "END AS \"is_shared\" " +
                             "FROM dog.dog " + 
                             "WHERE dog.dog_id IN (SELECT dog_id " +
                                                  "FROM dog.user_dogs " +
                                                  "WHERE user_id = ?)";

        try (PreparedStatement stmt = Database.conn.prepareStatement(dogsRequest)) {
            stmt.setInt(1, userId);

            ResultSet userDogsData = stmt.executeQuery();

            while (userDogsData.next()) 
                userDogs.add(new Dog(userDogsData.getInt("dog_id"), userDogsData.getString("name"), userDogsData.getString("breed"), userDogsData.getBoolean("is_shared")));

            return userDogs;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to fech dogs from the database", e.getMessage());
        } finally {
            closeConnection();
        }

        return new ArrayList<>();
    }

    protected Dog getDog(int userId, int dogId) {
        connectDatabase();

        String dogRequest = "SELECT name, breed, birth_date, coat_color, note, share_code " +
                            "FROM dog.dog " +
                            "WHERE dog_id = ?";

        try (PreparedStatement stmt = Database.conn.prepareStatement(dogRequest)) {
            stmt.setInt(1, dogId);

            ResultSet dogData = stmt.executeQuery();
            dogData.next();

            return new Dog(userId, dogId, dogData.getString("name"), dogData.getString("breed"), dogData.getString("coat_color"), dogData.getString("note"), UUID.fromString(dogData.getString("share_code")), dogData.getDate("birth_date").toLocalDate());
        } catch (SQLException e) {
            Utils.displayAlert("Failed to fetch dog data from the database", e.toString());
        } finally {
            closeConnection();
        }

        return null;
    }

    protected Boolean commitDogEdit(Dog editedDog) {
        connectDatabase();

        String dogUpdate = "UPDATE dog.dog " +
                           "SET name = ?, breed = ?, coat_color = ?, birth_date = ?, note = ? " +
                           "WHERE dog_id = ?";

        try (PreparedStatement dogUpdateStmt = Database.conn.prepareStatement(dogUpdate)) {
            dogUpdateStmt.setString(1, editedDog.getName());
            dogUpdateStmt.setString(2, editedDog.getBreed());
            dogUpdateStmt.setString(3, editedDog.getCoatColor());
            dogUpdateStmt.setDate(4, Date.valueOf(editedDog.getBirthDate()));
            dogUpdateStmt.setString(5, editedDog.getNote());
            dogUpdateStmt.setInt(6, editedDog.getDogId());

            dogUpdateStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to update dog data", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean deleteDog(int dogId) {
        connectDatabase();

        String dogRemoval = "DELETE FROM dog.dog " +
                            "WHERE dog_id = ?";

        try (PreparedStatement dogDeleteStmt = Database.conn.prepareStatement(dogRemoval)) {
            dogDeleteStmt.setInt(1, dogId);

            dogDeleteStmt.executeQuery();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to remove dog from the database", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean insertNewDogWeightRecord(DogWeightRecord newDogWeightRecord) {
        connectDatabase();
        
        String insertWeight = "INSERT INTO dog.weight_record (dog_id, weight, date_taken)" +
                              "VALUES (?, ?, ?)";

        try (PreparedStatement insertWeightStmt = Database.conn.prepareStatement(insertWeight)) {
            insertWeightStmt.setInt(1, newDogWeightRecord.getDogId());
            insertWeightStmt.setDouble(2, newDogWeightRecord.getWeight());
            insertWeightStmt.setDate(3, Date.valueOf(newDogWeightRecord.getDateTaken()));

            insertWeightStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to add weight record", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected ObservableList<DogWeightRecord> getDogWeightRecords(int dogId) {
        connectDatabase();

        ObservableList<DogWeightRecord> dogWeightRecords = FXCollections.observableArrayList();
        String dogWeightRequest = "SELECT weight_id, weight, date_taken " + 
                                  "FROM dog.weight_record " + 
                                  "WHERE dog_id = ? ";

        try (PreparedStatement stmt = Database.conn.prepareStatement(dogWeightRequest)) {
            stmt.setInt(1, dogId);

            ResultSet dogWeightData = stmt.executeQuery();

            while (dogWeightData.next())
                dogWeightRecords.add(new DogWeightRecord(dogWeightData.getInt("weight_id"), dogId, dogWeightData.getDouble("weight"), dogWeightData.getDate("date_taken").toLocalDate()));
            
            return dogWeightRecords;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to fetch weight data", e.getMessage());
        } finally {
            closeConnection();
        }

        return null;
    }

    protected Boolean commitDogWeightRecordEdit(DogWeightRecord editedDogWeightRecord) {
        connectDatabase();

        String weightRecordUpdate = "UPDATE dog.weight_record " +
                                    "SET weight = ?, date_taken = ? " +
                                    "WHERE weight_id = ?";
                    
        try (PreparedStatement weightRecordUpdateStmt = Database.conn.prepareStatement(weightRecordUpdate)) {
            weightRecordUpdateStmt.setDouble(1, editedDogWeightRecord.getWeight());
            weightRecordUpdateStmt.setDate(2, Date.valueOf(editedDogWeightRecord.getDateTaken()));
            weightRecordUpdateStmt.setInt(3, editedDogWeightRecord.getWeightId());

            weightRecordUpdateStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to update weight record", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean deleteDogWeightRecord(int weightId) {
        connectDatabase();
        
        String weightRecordRemoval = "DELETE FROM dog.weight_record " +
                                     "WHERE weight_id = ?";

        try (PreparedStatement weightRecordDeleteStmt = Database.conn.prepareStatement(weightRecordRemoval)) {
            weightRecordDeleteStmt.setInt(1, weightId);

            weightRecordDeleteStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to remove weight record", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean insertNewAppointmet(Appointment newAppointment) {
        connectDatabase();

        String insertAppointment = "INSERT INTO dog.appointment (dog_id, purpose, location, date_and_time, note) " +
                                   "VALUES (?, ?, ?, ?, ?) " +
                                   "RETURNING appointment_id";

        try (PreparedStatement insertAppointmentStmt = Database.conn.prepareStatement(insertAppointment)) {   
            insertAppointmentStmt.setInt(1, newAppointment.getDogId());
            insertAppointmentStmt.setString(2, newAppointment.getPurpose());
            insertAppointmentStmt.setString(3, newAppointment.getLocation());
            insertAppointmentStmt.setTimestamp(4, Timestamp.valueOf(newAppointment.getDateAndTime()));
            insertAppointmentStmt.setString(5, newAppointment.getNote());

            ResultSet appointmentId = insertAppointmentStmt.executeQuery();
            
            appointmentId.next();
            newAppointment.setAppointmentId(appointmentId.getInt("appointment_id"));

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to add new dog", e.toString());
        } finally {
            closeConnection();
        }

        return false;        
    }

    protected ObservableList<Appointment> getDogAppointments(int dogId) {
        connectDatabase();
        
        ObservableList<Appointment> dogAppointments = FXCollections.observableArrayList();
        String appointmenRequest = "SELECT appointment_id, purpose, location, date_and_time, note " +
                                   "FROM dog.appointment " +
                                   "WHERE dog_id = ?";

        try (PreparedStatement stmt = Database.conn.prepareStatement(appointmenRequest)) {
            stmt.setInt(1, dogId);

            ResultSet dogAppointmentData = stmt.executeQuery();

            //int appointmentId, int dogId, String purpose, String locaiton, String note, LocalDateTime dateAndTime
            while (dogAppointmentData.next())
                dogAppointments.add(new Appointment(dogAppointmentData.getInt("appointment_id"), dogId, dogAppointmentData.getString("purpose"), dogAppointmentData.getString("location"), dogAppointmentData.getString("note"), dogAppointmentData.getTimestamp("date_and_time").toLocalDateTime()));
            
                return dogAppointments;
        } catch (SQLException e) {
            Utils.displayAlert("A database error has occured", e.getMessage());
        } finally {
            closeConnection();
        }

        return null;
    }

    protected Boolean commitDogAppointmentEdit(Appointment editedAppointment) {
        connectDatabase();
        
        String appointmentEdit = "UPDATE dog.appointment " +
                                 "SET purpose = ?, location = ?, date_and_time = ?, note = ? " +
                                 "WHERE appointment_id = ?";

        try (PreparedStatement dogUpdateStmt = Database.conn.prepareStatement(appointmentEdit)) {
            dogUpdateStmt.setString(1, editedAppointment.getPurpose());
            dogUpdateStmt.setString(2, editedAppointment.getLocation());
            dogUpdateStmt.setTimestamp(3, Timestamp.valueOf(editedAppointment.getDateAndTime()));
            dogUpdateStmt.setString(4, editedAppointment.getNote());
            dogUpdateStmt.setInt(5, editedAppointment.getAppointmentId());

            dogUpdateStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to update appointmet data", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean deleteDogAppointmet(int appointmentId) {
        connectDatabase();
        
        String appointmetRemoval = "DELETE FROM dog.appointment " +
                                   "WHERE appointment_id = ?";

        try (PreparedStatement appointmetDeleteStmt = Database.conn.prepareStatement(appointmetRemoval)) {
            appointmetDeleteStmt.setInt(1, appointmentId);

            appointmetDeleteStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to remove appointment", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean insertNewMedication(Medication newMedication) {
        connectDatabase();

        //int dogId, String name, String purpose, String measuringUnit, String note, double quantity, LocalDateTime dateAndTime
        String insertMedication = "INSERT INTO dog.medication (dog_id, name, purpose, measuring_unit, note, quantity, date_and_time)" +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                  "RETURNING medication_id";

        try (PreparedStatement insertMedicationStmt = Database.conn.prepareStatement(insertMedication)) {
            insertMedicationStmt.setInt(1, newMedication.getDogId());
            insertMedicationStmt.setString(2, newMedication.getName());
            insertMedicationStmt.setString(3, newMedication.getPurpose());
            insertMedicationStmt.setString(4, newMedication.getMeasuringUnit());
            insertMedicationStmt.setString(5, newMedication.getNote());
            insertMedicationStmt.setDouble(6, newMedication.getQuantity());
            insertMedicationStmt.setTimestamp(7, Timestamp.valueOf(newMedication.getDateAndTime()));

            ResultSet medicationId = insertMedicationStmt.executeQuery();
            medicationId.next();

            newMedication.setMedicationId(medicationId.getInt("medication_id"));

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to add medication", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected ObservableList<Medication> getDogMedications(int dogId) {
        connectDatabase();
        
        ObservableList<Medication> dogMedicaions = FXCollections.observableArrayList();
        String medicationsRequest = "SELECT medication_id, name, purpose, quantity, measuring_unit, date_and_time, note " +
                                    "FROM dog.medication " +
                                    "WHERE dog_id = ?";

        try (PreparedStatement medicationsRequestStmt = Database.conn.prepareStatement(medicationsRequest)) {
            medicationsRequestStmt.setInt(1, dogId);

            ResultSet dogMedicationData = medicationsRequestStmt.executeQuery();

            while (dogMedicationData.next())
                dogMedicaions.add(new Medication(dogMedicationData.getInt("medication_id"), dogId, dogMedicationData.getString("name"), dogMedicationData.getString("purpose"), dogMedicationData.getString("measuring_unit"), dogMedicationData.getString("note"), dogMedicationData.getDouble("quantity"), dogMedicationData.getTimestamp("date_and_time").toLocalDateTime()));

            return dogMedicaions;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to fetch medication data from the database", e.toString());
        } finally {
            closeConnection();
        }

        return null;
    }

    protected Boolean commitDogMedicationEdit(Medication editedMedication) {
        connectDatabase();
        
        String medicationEdit = "UPDATE dog.medication " +
                                "SET name = ?, quantity = ?, measuring_unit = ?, date_and_time = ?, note = ?" +
                                "WHERE medication_id = ?";

        try (PreparedStatement medicationUpdateStmt = Database.conn.prepareStatement(medicationEdit)) {
            medicationUpdateStmt.setString(1, editedMedication.getName());
            medicationUpdateStmt.setDouble(2, editedMedication.getQuantity());
            medicationUpdateStmt.setString(3, editedMedication.getMeasuringUnit());
            medicationUpdateStmt.setTimestamp(4, Timestamp.valueOf(editedMedication.getDateAndTime()));
            medicationUpdateStmt.setString(5, editedMedication.getNote());
            medicationUpdateStmt.setInt(6, editedMedication.getMedicationId());
            
            medicationUpdateStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to update medicaiton data", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    protected Boolean deleteDogMedication(int medicationId) {
        connectDatabase();
        
        String medicationRemoval = "DELETE FROM dog.medication " +
                                   "WHERE medication_id = ?";

        try (PreparedStatement medicationRemovalStmt = Database.conn.prepareStatement(medicationRemoval)) {
            medicationRemovalStmt.setInt(1, medicationId);

            medicationRemovalStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            Utils.displayAlert("Failed to remove medicaiton", e.toString());
        } finally {
            closeConnection();
        }

        return false;
    }

    private void closeConnection() {
        try {
            Database.conn.close();
        } catch (SQLException e) {
            Utils.displayAlert("Failed to close database connection", e.getMessage());
        }
    }
}

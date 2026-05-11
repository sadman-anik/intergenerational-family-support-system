package com.example.igfss_client;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class MainController {

    @FXML private VBox homePage;
    @FXML private VBox olderCouplePage;
    @FXML private VBox youngFamilyPage;
    @FXML private VBox memberMenuPage;
    @FXML private VBox listPage;
    @FXML private VBox detailsPage;
    @FXML private VBox createEventPage;
    @FXML private VBox viewEventsPage;

    @FXML private TextField txtHusbandName;
    @FXML private TextField txtWifeName;
    @FXML private TextField txtHusbandPhone;
    @FXML private TextField txtWifePhone;
    @FXML private TextField txtHusbandEmail;
    @FXML private TextField txtWifeEmail;
    @FXML private TextField txtOlderAddress;
    @FXML private TextField txtYearsMarried;
    @FXML private PasswordField txtOlderPassword;

    @FXML private TextField txtParent1Name;
    @FXML private TextField txtParent2Name;
    @FXML private TextField txtParent1Phone;
    @FXML private TextField txtParent2Phone;
    @FXML private TextField txtParent1Email;
    @FXML private TextField txtParent2Email;
    @FXML private TextField txtFamilyAddress;
    @FXML private PasswordField txtFamilyPassword;
    @FXML private TextField txtChild1Gender;
    @FXML private TextField txtChild1Age;
    @FXML private TextField txtChild2Gender;
    @FXML private TextField txtChild2Age;
    @FXML private TextField txtChild3Gender;
    @FXML private TextField txtChild3Age;
    @FXML private TextField txtChild4Gender;
    @FXML private TextField txtChild4Age;

    @FXML private TextField txtLoginEmail;
    @FXML private PasswordField txtLoginPassword;

    @FXML private ListView<String> listMembers;
    @FXML private TextField txtSearchFidn;
    @FXML private TextArea txtOutput;

    @FXML private TextField txtEventType;
    @FXML private TextField txtEventDuration;
    @FXML private TextField txtEventVenue;
    @FXML private TextField txtEventCost;
    @FXML private ListView<String> listEvents;

    @FXML private DatePicker datePickerEvent;
    @FXML private ComboBox<String> comboHour;
    @FXML private ComboBox<String> comboMinute;
    @FXML private ComboBox<String> comboAmPm;

    private IGFSSClient client;
    private int loginAttempts;
    private int loggedInFidn;

    public void initialize() {
        client = new IGFSSClient();
        loginAttempts = 0;
        loggedInFidn = -1;
        
        comboHour.getItems().addAll(
        "01","02","03","04","05","06",
        "07","08","09","10","11","12"
        );

        comboMinute.getItems().addAll(
                "00","05","10","15","20","25",
                "30","35","40","45","50","55"
        );

        comboAmPm.getItems().addAll("AM", "PM");
        showHomePage();
    }

    @FXML
    private void showHomePage() {
        setVisiblePage(homePage);
    }

    @FXML
    private void showOlderCouplePage() {
        setVisiblePage(olderCouplePage);
    }

    @FXML
    private void showYoungFamilyPage() {
        setVisiblePage(youngFamilyPage);
    }

    @FXML
    private void showMemberMenuPage() {
        if (!isLoggedIn()) {
            showError("Please login first.");
            showHomePage();
            return;
        }
        setVisiblePage(memberMenuPage);
    }

    @FXML
    private void showListPage() {
        if (!isLoggedIn()) {
            showError("Please login first.");
            showHomePage();
            return;
        }
        setVisiblePage(listPage);
        handleViewMemberSummaries();
    }

    @FXML
    private void showDetailsPage() {
        if (!isLoggedIn()) {
            showError("Please login first.");
            showHomePage();
            return;
        }
        setVisiblePage(detailsPage);
        txtOutput.clear();
        txtSearchFidn.clear();
    }

    @FXML
    private void showCreateEventPage() {
        if (!isLoggedIn()) {
            showError("Please login first.");
            showHomePage();
            return;
        }
        setVisiblePage(createEventPage);
    }

    @FXML
    private void showViewEventsPage() {
        if (!isLoggedIn()) {
            showError("Please login first.");
            showHomePage();
            return;
        }
        setVisiblePage(viewEventsPage);
        handleViewEvents();
    }

    private void setVisiblePage(VBox pageToShow) {
        VBox[] pages = {
                homePage, olderCouplePage, youngFamilyPage, memberMenuPage,
                listPage, detailsPage, createEventPage, viewEventsPage
        };

        for (VBox page : pages) {
            boolean show = page == pageToShow;
            page.setVisible(show);
            page.setManaged(show);
        }
    }

    @FXML
    private void handleRegisterOlderCouple() {
        try {
            OlderCouple olderCouple = new OlderCouple();
            olderCouple.setSpouse1Name(requireText(txtHusbandName.getText(), "Husband name"));
            olderCouple.setSpouse2Name(requireText(txtWifeName.getText(), "Wife name"));
            olderCouple.setSpouse1Phone(requireText(txtHusbandPhone.getText(), "Husband phone"));
            olderCouple.setSpouse2Phone(requireText(txtWifePhone.getText(), "Wife phone"));
            olderCouple.setSpouse1Email(requireText(txtHusbandEmail.getText(), "Husband email"));
            olderCouple.setSpouse2Email(requireText(txtWifeEmail.getText(), "Wife email"));
            olderCouple.setAddress(requireText(txtOlderAddress.getText(), "Address"));
            olderCouple.setYearsMarried(parseRequiredInt(txtYearsMarried.getText(), "Years married"));

            if (olderCouple.getYearsMarried() < 20) {
                throw new IllegalArgumentException("Years married must be greater than or equal to 20.");
            }

            String password = requireText(txtOlderPassword.getText(), "Password");
            validatePassword(password);
            olderCouple.setPassword(password);

            String response = client.registerOlderCouple(olderCouple);
            showInfo(response);
            clearOlderCoupleFields();
            showHomePage();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterYoungFamily() {
        try {
            YoungFamily youngFamily = new YoungFamily();
            youngFamily.setSpouse1Name(requireText(txtParent1Name.getText(), "Parent 1 name"));
            youngFamily.setSpouse2Name(requireText(txtParent2Name.getText(), "Parent 2 name"));
            youngFamily.setSpouse1Phone(requireText(txtParent1Phone.getText(), "Parent 1 phone"));
            youngFamily.setSpouse2Phone(requireText(txtParent2Phone.getText(), "Parent 2 phone"));
            youngFamily.setSpouse1Email(requireText(txtParent1Email.getText(), "Parent 1 email"));
            youngFamily.setSpouse2Email(requireText(txtParent2Email.getText(), "Parent 2 email"));
            youngFamily.setAddress(requireText(txtFamilyAddress.getText(), "Address"));

            String password = requireText(txtFamilyPassword.getText(), "Password");
            validatePassword(password);
            youngFamily.setPassword(password);

            addChildren(youngFamily);

            String response = client.registerYoungFamily(youngFamily);
            showInfo(response);
            clearYoungFamilyFields();
            showHomePage();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        try {
            if (loginAttempts >= 3) {
                showError("Three incorrect login attempts reached. Please restart the client to try again.");
                return;
            }

            String email = requireText(txtLoginEmail.getText(), "Email");
            String password = requireText(txtLoginPassword.getText(), "Password");

            String response = client.login(email, password);

            if (response.startsWith("LOGIN_SUCCESS")) {
                String[] parts = response.split(":");
                loggedInFidn = Integer.parseInt(parts[1]);
                loginAttempts = 0;
                showInfo("Login successful. Your FIDN is " + loggedInFidn + ".");
                showMemberMenuPage();
            } else {
                loginAttempts++;
                int remaining = 3 - loginAttempts;
                showError("Incorrect login details. Remaining attempts: " + remaining);
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        loggedInFidn = -1;
        loginAttempts = 0;
        showInfo("Logged out successfully.");
        showHomePage();
    }

    @FXML
    private void handleViewMemberSummaries() {
        try {
            ArrayList<String> summaries = client.getAllMemberSummaries();
            listMembers.setItems(FXCollections.observableArrayList(summaries));
        } catch (IOException | ClassNotFoundException e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewDetailsByFidn() {
        try {
            int fidn = parseRequiredInt(txtSearchFidn.getText(), "FIDN");
            Member member = client.getMemberByFidn(fidn);

            if (member == null) {
                txtOutput.setText("No registered member found for FIDN " + fidn + ".");
                return;
            }

            txtOutput.setText(formatMemberDetails(member));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateEvent() {
        try {
            Event event = new Event();
            event.setFidn(loggedInFidn);
            event.setEventType(requireText(txtEventType.getText(), "Event type"));
            event.setEventDate(datePickerEvent.getValue().toString());

            String eventTime =
                    comboHour.getValue() + ":"
                    + comboMinute.getValue() + " "
                    + comboAmPm.getValue();

            event.setEventTime(eventTime);
            event.setDuration(requireText(txtEventDuration.getText(), "Event duration"));
            event.setVenue(requireText(txtEventVenue.getText(), "Event venue"));
            event.setCost(parseRequiredDouble(txtEventCost.getText(), "Event cost"));

            String response = client.createEvent(event);
            showInfo(response);
            clearEventFields();
            showMemberMenuPage();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewEvents() {
        try {
            ArrayList<Event> events = client.getAllEvents();
            ArrayList<String> eventStrings = new ArrayList<>();

            for (Event event : events) {
                eventStrings.add(event.toString());
            }

            listEvents.setItems(FXCollections.observableArrayList(eventStrings));
        } catch (IOException | ClassNotFoundException e) {
            showError("Error: " + e.getMessage());
        }
    }

    private boolean isLoggedIn() {
        return loggedInFidn > 0;
    }

    private void addChildren(YoungFamily youngFamily) {
        addChildIfPresent(youngFamily, txtChild1Gender.getText(), txtChild1Age.getText());
        addChildIfPresent(youngFamily, txtChild2Gender.getText(), txtChild2Age.getText());
        addChildIfPresent(youngFamily, txtChild3Gender.getText(), txtChild3Age.getText());
        addChildIfPresent(youngFamily, txtChild4Gender.getText(), txtChild4Age.getText());
    }

    private void addChildIfPresent(YoungFamily youngFamily, String genderText, String ageText) {
        String gender = genderText == null ? "" : genderText.trim();
        String age = ageText == null ? "" : ageText.trim();

        if (gender.isEmpty() && age.isEmpty()) {
            return;
        }

        if (gender.isEmpty() || age.isEmpty()) {
            throw new IllegalArgumentException("Each child needs both gender and age.");
        }

        Child child = new Child();
        child.setGender(gender);
        child.setAge(parseRequiredInt(age, "Child age"));
        youngFamily.addChild(child);
    }

    private String formatMemberDetails(Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append("FIDN: ").append(member.getFidn()).append("\n");
        sb.append("Address: ").append(member.getAddress()).append("\n\n");

        if (member instanceof OlderCouple) {
            OlderCouple olderCouple = (OlderCouple) member;
            sb.append("Member Type: Older Couple\n");
            sb.append("Husband Name: ").append(member.getSpouse1Name()).append("\n");
            sb.append("Wife Name: ").append(member.getSpouse2Name()).append("\n");
            sb.append("Husband Phone: ").append(member.getSpouse1Phone()).append("\n");
            sb.append("Wife Phone: ").append(member.getSpouse2Phone()).append("\n");
            sb.append("Husband Email: ").append(member.getSpouse1Email()).append("\n");
            sb.append("Wife Email: ").append(member.getSpouse2Email()).append("\n");
            sb.append("Years Married: ").append(olderCouple.getYearsMarried()).append("\n");
        } else if (member instanceof YoungFamily) {
            YoungFamily youngFamily = (YoungFamily) member;
            sb.append("Member Type: Young Family\n");
            sb.append("Parent 1 Name: ").append(member.getSpouse1Name()).append("\n");
            sb.append("Parent 2 Name: ").append(member.getSpouse2Name()).append("\n");
            sb.append("Parent 1 Phone: ").append(member.getSpouse1Phone()).append("\n");
            sb.append("Parent 2 Phone: ").append(member.getSpouse2Phone()).append("\n");
            sb.append("Parent 1 Email: ").append(member.getSpouse1Email()).append("\n");
            sb.append("Parent 2 Email: ").append(member.getSpouse2Email()).append("\n");
            sb.append("Children:\n");

            if (youngFamily.getChildren().isEmpty()) {
                sb.append("- None\n");
            } else {
                for (int i = 0; i < youngFamily.getChildren().size(); i++) {
                    Child child = youngFamily.getChildren().get(i);
                    sb.append("- Child ").append(i + 1).append(": ")
                            .append(child.getGender()).append(", ")
                            .append(child.getAge()).append(" years\n");
                }
            }
        }

        return sb.toString();
    }

    private String requireText(String text, String fieldName) {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value;
    }

    private int parseRequiredInt(String text, String fieldName) {
        String value = requireText(text, fieldName);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    private double parseRequiredDouble(String text, String fieldName) {
        String value = requireText(text, fieldName);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }
    }

    private void validatePassword(String password) {
        if (password.length() != 10) {
            throw new IllegalArgumentException("Password must be exactly 10 characters long.");
        }

        boolean hasLetter = false;
        boolean hasNumber = false;

        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            }
            if (Character.isDigit(ch)) {
                hasNumber = true;
            }
        }

        if (!hasLetter || !hasNumber) {
            throw new IllegalArgumentException("Password must contain both letters and numbers.");
        }
    }

    private void clearOlderCoupleFields() {
        txtHusbandName.clear();
        txtWifeName.clear();
        txtHusbandPhone.clear();
        txtWifePhone.clear();
        txtHusbandEmail.clear();
        txtWifeEmail.clear();
        txtOlderAddress.clear();
        txtYearsMarried.clear();
        txtOlderPassword.clear();
    }

    private void clearYoungFamilyFields() {
        txtParent1Name.clear();
        txtParent2Name.clear();
        txtParent1Phone.clear();
        txtParent2Phone.clear();
        txtParent1Email.clear();
        txtParent2Email.clear();
        txtFamilyAddress.clear();
        txtFamilyPassword.clear();
        txtChild1Gender.clear();
        txtChild1Age.clear();
        txtChild2Gender.clear();
        txtChild2Age.clear();
        txtChild3Gender.clear();
        txtChild3Age.clear();
        txtChild4Gender.clear();
        txtChild4Age.clear();
    }

    private void clearEventFields() {
        txtEventType.clear();
        datePickerEvent.setValue(null);
        comboHour.setValue(null);
        comboMinute.setValue(null);
        comboAmPm.setValue(null);
        txtEventDuration.clear();
        txtEventVenue.clear();
        txtEventCost.clear();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

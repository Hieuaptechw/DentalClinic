package com.dentalclinic.views.pages.manage;

import com.dentalclinic.controllers.DatabaseController;
import com.dentalclinic.controllers.FinancialController;
import com.dentalclinic.controllers.PatientController;
import com.dentalclinic.entities.*;
import com.dentalclinic.views.pages.AbstractPage;
import com.dentalclinic.views.pages.Page;
import com.dentalclinic.views.pages.form.FinancialFormController;
import com.dentalclinic.views.pages.form.PatientFormController;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Page(name="Tài chính", icon="images/finance.png", fxml="manage/finance.fxml")
public class FinancePage extends AbstractPage {
    @FXML private TableView<Financial> tableViewIncome;
    @FXML private TableView<Financial> tableViewExpenses;
    @FXML private TableColumn<Financial, String> nameColumnInvoice;
    @FXML private TableColumn<Financial, String> amountColumnInvoice;
    @FXML private TableColumn<Financial, String> paymentColumnInvoice;
    @FXML private TableColumn<Financial, String> descriptionInvoice;
    @FXML private TableColumn<Financial, String>  statusColumnInvoice;
    @FXML private TableColumn<Financial, Void> actionColumnInvoice;
    @FXML private TableColumn<Financial, String> staffColumnExpenses;
    @FXML private TableColumn<Financial, String> amountColumnExpenses;
    @FXML private TableColumn<Financial, String> descriptionExpenses;
    @FXML private TableColumn<Financial, String> paymentColumnExpenses;
    @FXML private TableColumn<Financial, String>  statusColumnExpenses;
    @FXML private TableColumn<Financial, Void> actionColumnExpenses;
    @FXML private ComboBox<String> paymentMethodCombobox;
    @FXML private ComboBox<String> statusCombobox;
    @FXML private RadioButton radioIncome;
    @FXML private RadioButton radioExpenses;
    @FXML private ToggleGroup transactionTypeGroup;
    @FXML private TextField amountField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField emailStaffField;
    @FXML private TextField phonePatientField;
    @FXML private  TextField searchField;
    @FXML private Label revenueLabel;
    @FXML private Label expensesLabel;
    @FXML private Label profitLabel;
    private FinancialController financialController;
    private List<Patient> patientList;
    private List<Staff> staffList;
    private ObservableList<Financial> financialListIncome = FXCollections.observableArrayList();
    private ObservableList<Financial> financialListExpenses = FXCollections.observableArrayList();
    public void initialize() {
        DatabaseController.init();
        EntityManager em = DatabaseController.getEntityManager();
        financialController = new FinancialController(em);
        transactionTypeGroup = new ToggleGroup();
        radioIncome.setToggleGroup(transactionTypeGroup);
        radioExpenses.setToggleGroup(transactionTypeGroup);
        fixComboBox(paymentMethodCombobox);
        fixComboBox(statusCombobox);
        setupTableColumns();
        loadFinancial();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
        setupComboBoxListeners();
    }
    private void setupLable(double revenue,double expenses,double profit) {
        revenueLabel.setText(revenue+"$");
        expensesLabel.setText(expenses+"$");
        profitLabel.setText(profit+"$");
    }
    private void setupTableColumns() {
        nameColumnInvoice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatient().getName()));
        amountColumnInvoice.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentColumnInvoice.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        descriptionInvoice.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumnInvoice.setCellValueFactory(new PropertyValueFactory<>("status"));
        loadActionColumn(actionColumnInvoice);
        staffColumnExpenses.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpdatedBy().getName()));
        amountColumnExpenses.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionExpenses.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumnExpenses.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentColumnExpenses.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        loadActionColumn(actionColumnExpenses);
    }
    public void loadFinancial() {
        List<Financial> financialList = financialController.getAllPatients();
        financialListIncome.setAll(financialList.stream().filter(f->f.getTransactionType()== TransactionType.INCOME).sorted(Comparator.comparing(Financial::getCreatedAt).reversed()).toList());
        financialListExpenses.setAll(financialList.stream().filter(f->f.getTransactionType()== TransactionType.EXPENSE).sorted(Comparator.comparing(Financial::getCreatedAt).reversed()).toList());
        List<String> paymentMethods = List.of("Cash", "Credit Card", "Debit Card", "Insurance", "Bank Transfer");
        List<String> status = List.of("Pending", "Completed", "Cancelled", "Refunded");
        statusCombobox.setItems(FXCollections.observableArrayList(status));
        paymentMethodCombobox.setItems(FXCollections.observableArrayList(paymentMethods));
        tableViewIncome.setItems(financialListIncome);
        tableViewExpenses.setItems(financialListExpenses);
        staffList = financialController.getAllStaff();
        patientList = financialController.getAllPatient();
        double totalRevenue = financialListIncome.stream().mapToDouble(Financial::getAmount).sum()/25.000;
        double totalExpenses = financialListExpenses.stream().mapToDouble(Financial::getAmount).sum()/25.000;
        double profit = totalRevenue - totalExpenses;
        setupLable(totalRevenue,totalExpenses,profit);
    }
    private void loadActionColumn(TableColumn<Financial, Void> colNameActio) {
        colNameActio.setCellFactory(param -> new TableCell<>() {
            private final Button editButton;
            private final Button deleteButton;
            private final Button printButton;


            {
                ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/edit.png")));
                editIcon.setFitHeight(22);
                editIcon.setFitWidth(22);

                ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/delete_1.png")));
                deleteIcon.setFitHeight(22);
                deleteIcon.setFitWidth(22);

                ImageView printIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dentalclinic/images/printer.png")));
                printIcon.setFitHeight(22);
                printIcon.setFitWidth(22);


                editButton = new Button("", editIcon);
                deleteButton = new Button("", deleteIcon);
                printButton = new Button("", printIcon);
                editButton.getStyleClass().add("button-icon");
                deleteButton.getStyleClass().add("button-icon");
                printButton.getStyleClass().add("button-icon");

                editButton.setOnAction(event -> {
                    Financial financial = getTableView().getItems().get(getIndex());
                    System.out.println(financial);
                    handleEditFinancial(financial);
                });

                deleteButton.setOnAction(event -> {
                   Financial financial = getTableView().getItems().get(getIndex());
                    handleDeleteFinancial(financial);
                });

                printButton.setOnAction(event -> {
                    Financial financial = getTableView().getItems().get(getIndex());
                    showInvoiceAlert(financial,financial.getInvoiceNumber());
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, printButton,editButton,deleteButton));
                }
            }

        });
    }
    private void handleDeleteFinancial(Financial financial) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this invoice?");
        alert.setContentText("Invoice Number: " + financial.getInvoiceNumber());

        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                financialController.deleteFinancial(financial.getFinanceId());
                loadFinancial();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Invoice deleted successfully!", ButtonType.OK);
                alert1.showAndWait();
            }
        });
    }
    @FXML
    private void handleSearch(String keyword) {
        String trimmedKeyword = keyword.toLowerCase().trim();

        if (trimmedKeyword.isEmpty()) {
            tableViewExpenses.setItems(financialListExpenses);
            tableViewIncome.setItems(financialListIncome);
        } else {
            List<Financial> filteredListIncome = financialListIncome.stream()
                    .filter(f -> matchesSearch(f, trimmedKeyword))
                    .toList();
            List<Financial> filteredListExpense = financialListIncome.stream()
                    .filter(f -> matchesSearch(f, trimmedKeyword))
                    .toList();
            tableViewIncome.setItems(FXCollections.observableArrayList(filteredListIncome));
            tableViewExpenses.setItems(FXCollections.observableArrayList(filteredListExpense));

        }

        loadActionColumn(actionColumnInvoice);
        loadActionColumn(actionColumnExpenses);
    }

    private boolean matchesSearch(Financial financial, String keyword) {
        String[] keywords = keyword.split("\\s+");

        String combinedData = (financial.getAmount() + " " +
                financial.getPatient().getName() + " " +
                financial.getUpdatedAt().getNano() + " " +
                financial.getStatus() + " " +
                financial.getDescription() + " " +
                financial.getPaymentMethod()).toLowerCase();

        return Arrays.stream(keywords).allMatch(combinedData::contains);
    }

    private <T> void fixComboBox(ComboBox<T> cb) {
        cb.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty || item == null) {
                    setText(cb.getPromptText());
                } else {
                    setText(item.toString());
                }
            }
        });
    }


    @FXML
    private void handleAddFinancial() {
        String phonePatient = phonePatientField.getText();
        String emailStaff = emailStaffField.getText();
        String paymentMethod = paymentMethodCombobox.getValue();
        String status = statusCombobox.getValue();
        String description = descriptionArea.getText();
        TransactionType transactionType = null;
        if (radioExpenses.isSelected()) {
            transactionType = TransactionType.EXPENSE;
        } else if (radioIncome.isSelected()) {
            transactionType = TransactionType.INCOME;
        }

        if (transactionType == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a transaction type!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid amount format!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        Optional<Patient> selectedPatient = Optional.empty();
        if (transactionType == TransactionType.INCOME) {
            selectedPatient = patientList.stream()
                    .filter(p -> p.getPhone().equals(phonePatient))
                    .findFirst();

            if (selectedPatient.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Patient not found with phone: " + phonePatient, ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }
        Optional<Staff> selectedStaff = staffList.stream()
                .filter(staff -> staff.getEmail().equals(emailStaff))
                .findFirst();
        Patient patient = selectedPatient.orElse(null);
        if (selectedStaff.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Staff not found with email: " + emailStaff, ButtonType.OK);
            alert.showAndWait();
            System.out.println();
            return;
        }
        FinancialStatus financialStatus = null;
        switch (status){
            case "Pending":
                financialStatus= (FinancialStatus.PENDING);
                break;
            case "Completed":
                financialStatus=(FinancialStatus.COMPLETED);
                break;
            case "Cancelled":
                financialStatus =(FinancialStatus.CANCELLED);
                break;
            case "Refunded":
                financialStatus= (FinancialStatus.REFUNDED);
                break;
            default:
                System.out.println("Error");

        }
        LocalDateTime now = LocalDateTime.now();
        String invoiceNumber = generateCode();
        Financial financial = new Financial(patient,amount,description,now,now,transactionType,paymentMethod,invoiceNumber,financialStatus,selectedStaff.get());
        System.out.println(financial);
        financialController.addFinancial(financial);
        showInvoiceAlert(financial,financial.getInvoiceNumber());

        handleClear();
        loadFinancial();


    }

    private void handleEditFinancial(Financial financial) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dentalclinic/views/pages/form/financialform.fxml"));
            VBox root = (VBox) loader.load();
            FinancialFormController financialFormController = loader.getController();
            financialFormController.setFinancial(financial,staffList,patientList);
            Stage stage = new Stage();
            stage.setTitle("Edit Financial");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
//            loadPatients();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void setupComboBoxListeners() {
        paymentMethodCombobox.setOnAction(event -> {
           String paymentMethod = paymentMethodCombobox.getSelectionModel().getSelectedItem();
            if (paymentMethod != null) {
                System.out.println("Payment method được chọn: " +paymentMethod);
            }
        });
        statusCombobox.setOnAction(event -> {
            String status = statusCombobox.getSelectionModel().getSelectedItem();
            if (status != null) {
                System.out.println("Status được chọn :" +status);
            }
        });

    }
    public static String generateCode() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char letter = letters.charAt(random.nextInt(letters.length()));
            code.append(letter);
        }
        for (int i = 0; i < 3; i++) {
            int number = random.nextInt(10); // 0-9
            code.append(number);
        }

        return code.toString();
    }
    @FXML
    public void handleClear() {
        transactionTypeGroup.selectToggle(null);
        amountField.clear();
        descriptionArea.clear();
        phonePatientField.clear();
        emailStaffField.clear();
        statusCombobox.setValue(null);
        paymentMethodCombobox.setValue(null);
    }

    public void showInvoiceAlert(Financial financial, String invoiceNumber) {
        String text ="";
        if(financial.getTransactionType() ==TransactionType.INCOME){
            text = "Patient name: " + financial.getPatient().getName() + "\n";
        }
        Image qrImage = new Image(getClass().getResourceAsStream("/com/dentalclinic/images/qr.jpg"));
        ImageView qrImageView = new ImageView(qrImage);
        qrImageView.setFitWidth(200);
        qrImageView.setPreserveRatio(true);
        qrImageView.getStyleClass().add("image-view");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invoice Details");
        alert.setHeaderText("Billing Information #"+financial.getInvoiceNumber());
        alert.getButtonTypes().clear();
        Label textLabel = new Label(
                    "----------------------------------\n" +
                        "       DENTAL CLINIC RECEIPT       \n" +
                        "----------------------------------\n" +
                        "Date: " + financial.getCreatedAt() + "\n" +
                        text+
                        "Amount: $" + financial.getAmount() + "\n" +
                        "Payment: " + financial.getPaymentMethod() + "\n" +
                        "Status: " + financial.getStatus() + "\n" +
                        "Description: " + financial.getDescription() + "\n" +
                        "Staff: " + financial.getUpdatedBy().getName() + "\n" +
                        "----------------------------------"
        );
        Label bankLabel = new Label(
                "----------------------------------\n" +
                        "Bank Information:\n" +
                        "Bank Name: VietComBank\n" +
                        "Account Holder: Phi Van Hieu\n" +
                        "Account Number: 9334982576"
        );
        Label footerLabel = new Label(
                "----------------------------------\n" +
                        "Thank you for your visit!\n" +
                        "For any inquiries, contact us at: 033-498-2576"
        );
        bankLabel.setWrapText(true);
        textLabel.setWrapText(true);
        footerLabel.setWrapText(true);
        footerLabel.setTextAlignment(TextAlignment.CENTER);
        bankLabel.setTextAlignment(TextAlignment.CENTER);
        bankLabel.getStyleClass().add("label");
        textLabel.getStyleClass().add("label");
        footerLabel.getStyleClass().add("label");
        VBox vbox;
        if (financial.getTransactionType() == TransactionType.INCOME){
             vbox = new VBox(10, textLabel, bankLabel,qrImageView,footerLabel);
        }else {
             vbox = new VBox(10, textLabel,footerLabel);
        }
        vbox.setAlignment(Pos.CENTER);
        ButtonType printButton = new ButtonType("Print", ButtonBar.ButtonData.RIGHT);
        alert.getButtonTypes().add(printButton);

        vbox.getStyleClass().add("vbox");
        alert.getDialogPane().setContent(vbox);
        alert.getDialogPane().getStyleClass().add("invoice-alert");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/dentalclinic/css/invoice.css").toExternalForm());

        alert.showAndWait().ifPresent(response -> {
            if (response == printButton) {
                printInvoice(vbox);
            }
        });
    }
    private void printInvoice(VBox content) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean success = job.printPage(content);
            if (success) {
                job.endJob();
            }
        }
    }
}

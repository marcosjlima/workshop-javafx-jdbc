package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	private SellerService service;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	private Seller seller;
	@FXML
	private Label lblErrorName;
	@FXML
	private Label lblErrorEmail;
	@FXML
	private Label lblErrorBirthDate;
	@FXML
	private Label lblErrorBaseSalary;
	@FXML
	private Label lblErrorDepartment;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> cboDepartment;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;

	private ObservableList<Department> obsList;

	public void subscriberDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		try {
			validations();

			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			
			seller = new Seller(Utils.tryParseToInt(txtId.getText()), 
					txtName.getText(), txtEmail.getText(), 
					Date.from(instant), 
					Utils.tryParseToDouble(txtBaseSalary.getText()), 
					(Department) cboDepartment.getSelectionModel().getSelectedItem());
			
			service.saveOrUpdate(seller);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro saving department", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void validations() {
		ValidationException exception = new ValidationException("Validation error");

		if (txtName.getText() == null || txtName.getText().trim().equals(""))
			exception.addError("Name", "Field can't be empty");
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals(""))
			exception.addError("Email", "Field can't be empty");
		
		if (Utils.tryParseToDouble(txtBaseSalary.getText()) == null)
			exception.addError("BaseSalary", "Field can't be empty");
		
		if (dpBirthDate.getValue() == null)
			exception.addError("BirthDate", "Field can't be empty");

		if (cboDepartment.getSelectionModel().getSelectedItem() == null)
			exception.addError("Department", "Field can't be empty");
		
		if (exception.getErrors().size() > 0)
			throw exception;
	}

	private void notifyDataChangeListeners() {
		dataChangeListeners.stream().forEach(DataChangeListener::onDataChanged);
	}

	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		initializeCboDepartment();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 50);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
	}

	private void initializeCboDepartment() {

		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};

		cboDepartment.setCellFactory(factory);
		cboDepartment.setButtonCell(factory.call(null));
	}

	public void loadAssociateObjects() {
		if (departmentService == null)
			throw new IllegalStateException("Department Service was null");

		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		cboDepartment.setItems(obsList);
	}

	public void updateFormData() {
		if (seller == null)
			throw new IllegalStateException("Seller was null");

		txtId.setText(String.valueOf(seller.getId()));
		txtName.setText(seller.getName());
		txtEmail.setText(seller.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
		if (seller.getBirthDate() != null)
			dpBirthDate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
		if (seller.getDepartment() != null)
			cboDepartment.setValue(seller.getDepartment());
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		lblErrorName.setText("");
		lblErrorEmail.setText("");
		lblErrorBirthDate.setText("");
		lblErrorBaseSalary.setText("");
		lblErrorDepartment.setText("");
		
		if (fields.contains("Name"))
			lblErrorName.setText(errors.get("Name"));
		
		if (fields.contains("Email"))
			lblErrorEmail.setText(errors.get("Email"));
		
		if (fields.contains("BirthDate"))
			lblErrorBirthDate.setText(errors.get("BirthDate"));
		
		if (fields.contains("BaseSalary"))
			lblErrorBaseSalary.setText(errors.get("BaseSalary"));

		if (fields.contains("Department"))
			lblErrorDepartment.setText(errors.get("Department"));
		
	}
}

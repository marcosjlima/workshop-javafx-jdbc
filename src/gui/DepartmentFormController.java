package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	private DepartmentService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	private Department department;
	@FXML
	private Label lblError;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;

	public void subscriberDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		try {
			department = new Department(Utils.tryParseToInt(txtId.getText()), txtName.getText());
			service.saveOrUpdate(department);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro saving department", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		//for (DataChangeListener listener : dataChangeListeners)
			//listener.onDataChanged();
		dataChangeListeners.stream().forEach(DataChangeListener::onDataChanged);
	}

	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 50);

	}

	public void updateFormData() {
		if (department == null)
			throw new IllegalStateException("Department was null");

		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(department.getName());
	}
}

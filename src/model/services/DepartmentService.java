package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {
	public List<Department> findAll() {
		List<Department> department = new ArrayList<>();
		department.add(new Department(1, "Books"));
		department.add(new Department(2, "Eletronics"));
		department.add(new Department(3, "Music"));
		department.add(new Department(4, "Fashion"));

		return department;
	}
}

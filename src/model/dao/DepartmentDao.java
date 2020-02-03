package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {
	void insert(Department department);
	Integer update(Department department);
	Integer deleteById(Integer id);
	Department findById(Integer id);
	List<Department> findAll();
}

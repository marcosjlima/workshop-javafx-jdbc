package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {
	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department department) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("INSERT INTO department(Name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
			st.setString(1, department.getName());

			Integer rowsAffected = st.executeUpdate();

			if (rowsAffected <= 0)
				throw new DbException("Unexpected error | No rows affected");
			else {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next())
					department.setId(rs.getInt(1));
				DB.closeResultSet(rs);
			}
		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Integer update(Department department) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");
			st.setString(1, department.getName());
			st.setInt(2, department.getId());

			Integer rowsAffected = st.executeUpdate();

			if (rowsAffected <= 0)
				throw new DbException("Unexpected error | No rows affected");
			else
				return rowsAffected;
		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Integer deleteById(Integer id) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			st.setInt(1, id);

			Integer rowsAffected = st.executeUpdate();

			/*
			 * if (rowsAffected <= 0) throw new
			 * DbException("Unexpected error | No rows affected"); else
			 */
			return rowsAffected;
		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT Id, Name FROM department WHERE Id=?");
			st.setInt(1, id);

			rs = st.executeQuery();

			if (rs.next()) {
				return instantiateDepartment(rs);
			}

			return null;

		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT Id, Name FROM department ");

			rs = st.executeQuery();

			return instantiateDepartments(rs);

		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("Id"), rs.getString("Name"));
	}
	
	private List<Department> instantiateDepartments(ResultSet rs) throws SQLException {
		List<Department> departments = new ArrayList<>();

		while (rs.next()) {
			departments.add(instantiateDepartment(rs));
		}

		return departments;
	}
}
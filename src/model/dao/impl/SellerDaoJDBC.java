package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement(
					"INSERT INTO seller(Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDepartment().getId());

			Integer rowsAffected = st.executeUpdate();

			if (rowsAffected <= 0)
				throw new DbException("Unexpected error | No rows affected");
			else {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next())
					seller.setId(rs.getInt(1));
				DB.closeResultSet(rs);
			}
		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Integer update(Seller seller) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?");
			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDepartment().getId());
			st.setInt(6, seller.getId());

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
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			st.setInt(1, id);

			Integer rowsAffected = st.executeUpdate();

			/*if (rowsAffected <= 0)
				throw new DbException("Unexpected error | No rows affected");
			else 
			 */
			return rowsAffected;
		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.id=?");

			st.setInt(1, id);

			rs = st.executeQuery();

			if (rs.next()) {
				return instantiateSeller(rs);
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
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department " + "ON seller.DepartmentId = department.Id ");

			rs = st.executeQuery();

			return instantiateSellers(rs);

		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.DepartmentId=?");

			st.setInt(1, department.getId());

			rs = st.executeQuery();

			return instantiateSellers(rs, department);

		} catch (SQLException ex) {
			throw new DbException(ex.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs) throws SQLException {
		Department department = instantiateDepartment(rs);

		return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), rs.getDate("BirthDate"),
				rs.getDouble("BaseSalary"), department);
	}

	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		return new Seller(rs.getInt("Id"), rs.getString("Name"), rs.getString("Email"), rs.getDate("BirthDate"),
				rs.getDouble("BaseSalary"), department);
	}

	private List<Seller> instantiateSellers(ResultSet rs, Department department) throws SQLException {
		List<Seller> sellers = new ArrayList<>();

		while (rs.next()) {
			sellers.add(instantiateSeller(rs, department));
		}

		return sellers;
	}

	private List<Seller> instantiateSellers(ResultSet rs) throws SQLException {
		List<Seller> sellers = new ArrayList<>();
		Map<Integer, Department> departments = new HashMap<>();

		while (rs.next()) {
			sellers.add(instantiateSeller(rs, getDepartment(rs, departments)));
		}

		return sellers;
	}

	private Department getDepartment(ResultSet rs, Map<Integer, Department> departments) throws SQLException {
		Department deparment = departments.get(rs.getInt("DepartmentId"));

		if (deparment == null) {
			deparment = instantiateDepartment(rs);
			departments.put(rs.getInt("DepartmentId"), deparment);
		}

		return deparment;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("DepartmentId"), rs.getString("DepName"));
	}
}

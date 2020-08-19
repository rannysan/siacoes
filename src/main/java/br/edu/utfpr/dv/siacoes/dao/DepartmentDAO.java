package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.Department;

public class DepartmentDAO {
  // inicializando as variavéis que são utilizadas em todos métodos
	Connection conn = null;
	PreparedStatement pStmt = null;
	ResultSet rs = null;
	Statement stmt = null;

	public Department findById(int id) throws SQLException{
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			pStmt = conn.prepareStatement(
				"SELECT department.*, campus.name AS campusName " +
				"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
				"WHERE idDepartment = ?");
		
			pStmt.setInt(1, id);
			
			rs = pStmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
      ConnectionClosePreparedStatement(conn, pStmt, rs);
		}
	}
	
	public List<Department> listAll(boolean onlyActive) throws SQLException{
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " + 
					(onlyActive ? " WHERE department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
      ConnectionCloseStatement(conn, stmt, rs);
		}
	}
	
	public List<Department> listByCampus(int idCampus, boolean onlyActive) throws SQLException{
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT department.*, campus.name AS campusName " +
					"FROM department INNER JOIN campus ON campus.idCampus=department.idCampus " +
					"WHERE department.idCampus=" + String.valueOf(idCampus) + (onlyActive ? " AND department.active=1" : "") + " ORDER BY department.name");
			
			List<Department> list = new ArrayList<Department>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
      ConnectionCloseStatement(conn, stmt, rs);
		}
	}
	
	public int save(int idUser, Department department) throws SQLException{
		boolean insert = (department.getIdDepartment() == 0);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				pStmt = conn.prepareStatement("INSERT INTO department(idCampus, name, logo, active, site, fullName, initials) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				pStmt = conn.prepareStatement("UPDATE department SET idCampus=?, name=?, logo=?, active=?, site=?, fullName=?, initials=? WHERE idDepartment=?");
			}
			
			pStmt.setInt(1, department.getCampus().getIdCampus());
			pStmt.setString(2, department.getName());
			if(department.getLogo() == null){
				pStmt.setNull(3, Types.BINARY);
			}else{
				pStmt.setBytes(3, department.getLogo());	
			}
			pStmt.setInt(4, department.isActive() ? 1 : 0);
			pStmt.setString(5, department.getSite());
			pStmt.setString(6, department.getFullName());
			pStmt.setString(7, department.getInitials());
			
			if(!insert){
				pStmt.setInt(8, department.getIdDepartment());
			}
			
			pStmt.execute();
			
			if(insert){
				rs = pStmt.getGeneratedKeys();
				
				if(rs.next()){
					department.setIdDepartment(rs.getInt(1));
				}

				new UpdateEvent(conn).registerInsert(idUser, department);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, department);
			}
			
			return department.getIdDepartment();
		}finally{
			ConnectionClosePreparedStatement(conn, pStmt, rs);
		}
	}
	
	private Department loadObject(ResultSet rs) throws SQLException{
		Department department = new Department();
		
		department.setIdDepartment(rs.getInt("idDepartment"));
		department.getCampus().setIdCampus(rs.getInt("idCampus"));
		department.setName(rs.getString("name"));
		department.setFullName(rs.getString("fullName"));
		department.setLogo(rs.getBytes("logo"));
		department.setActive(rs.getInt("active") == 1);
		department.setSite(rs.getString("site"));
		department.getCampus().setName(rs.getString("campusName"));
		department.setInitials(rs.getString("initials"));
		
		return department;
	}

//criado metódos privados com trechos de código repetidos
  private void ConnectionClosePreparedStatement(Connection conn, PreparedStatement stmt, ResultSet rs) {
		if((rs != null) && !rs.isClosed())
			rs.close();
		if((stmt != null) && !stmt.isClosed())
			stmt.close();
		if((conn != null) && !conn.isClosed())
			conn.close();
	}	

   private void ConnectionCloseStatement(Connection conn, Statement stmt, ResultSet rs) {
		if((rs != null) && !rs.isClosed())
			rs.close();
		if((stmt != null) && !stmt.isClosed())
			stmt.close();
		if((conn != null) && !conn.isClosed())
			conn.close();
	}	
}

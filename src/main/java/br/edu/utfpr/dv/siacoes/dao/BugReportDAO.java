package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.model.BugReport;
import br.edu.utfpr.dv.siacoes.model.BugReport.BugStatus;
import br.edu.utfpr.dv.siacoes.model.Module;
import br.edu.utfpr.dv.siacoes.model.User;

public class BugReportDAO {
  // inicializando as variavéis que são utilizadas em todos métodos
	Connection conn = null;
	PreparedStatement pStmt = null;
	ResultSet rs = null;
	Statement stmt = null;
	
	public BugReport findById(int id) throws SQLException{
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			pStmt = conn.prepareStatement("SELECT bugreport.*, \"user\".name " + 
				"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
				"WHERE idBugReport = ?");
		
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
	
	public List<BugReport> listAll() throws SQLException{		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT bugreport.*, \"user\".name " +
					"FROM bugreport INNER JOIN \"user\" ON \"user\".idUser=bugreport.idUser " +
					"ORDER BY status, reportdate");
			List<BugReport> list = new ArrayList<BugReport>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			ConnectionCloseStatement(conn, stmt, rs);
		}
	}
	
	public int save(BugReport bug) throws SQLException{
		boolean insert = (bug.getIdBugReport() == 0);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				pStmt = conn.prepareStatement("INSERT INTO bugreport(idUser, module, title, description, reportDate, type, status, statusDate, statusDescription) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				pStmt = conn.prepareStatement("UPDATE bugreport SET idUser=?, module=?, title=?, description=?, reportDate=?, type=?, status=?, statusDate=?, statusDescription=? WHERE idBugReport=?");
			}
			
			pStmt.setInt(1, bug.getUser().getIdUser());
			pStmt.setInt(2, bug.getModule().getValue());
			pStmt.setString(3, bug.getTitle());
			pStmt.setString(4, bug.getDescription());
			pStmt.setDate(5, new java.sql.Date(bug.getReportDate().getTime()));
			pStmt.setInt(6, bug.getType().getValue());
			pStmt.setInt(7, bug.getStatus().getValue());
			if(bug.getStatus() == BugStatus.REPORTED){
				pStmt.setNull(8, Types.DATE);
			}else{
				pStmt.setDate(8, new java.sql.Date(bug.getStatusDate().getTime()));
			}
			pStmt.setString(9, bug.getStatusDescription());
			
			if(!insert){
				pStmt.setInt(10, bug.getIdBugReport());
			}
			
			pStmt.execute();
			
			if(insert){
				rs = pStmt.getGeneratedKeys();
				
				if(rs.next()){
					bug.setIdBugReport(rs.getInt(1));
				}
			}
			
			return bug.getIdBugReport();
		}finally{
      ConnectionClosePreparedStatement(conn, pStmt, rs);
		}
	}
	
	private BugReport loadObject(ResultSet rs) throws SQLException{
		BugReport bug = new BugReport();
		
		bug.setIdBugReport(rs.getInt("idBugReport"));
		bug.setUser(new User());
		bug.getUser().setIdUser(rs.getInt("idUser"));
		bug.getUser().setName(rs.getString("name"));
		bug.setModule(Module.SystemModule.valueOf(rs.getInt("module")));
		bug.setTitle(rs.getString("title"));
		bug.setDescription(rs.getString("description"));
		bug.setReportDate(rs.getDate("reportDate"));
		bug.setType(BugReport.BugType.valueOf(rs.getInt("type")));
		bug.setStatus(BugReport.BugStatus.valueOf(rs.getInt("status")));
		bug.setStatusDate(rs.getDate("statusDate"));
		bug.setStatusDescription(rs.getString("statusDescription"));
		
		return bug;
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

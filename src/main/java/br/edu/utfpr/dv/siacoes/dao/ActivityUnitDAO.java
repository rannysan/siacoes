package br.edu.utfpr.dv.siacoes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.siacoes.log.UpdateEvent;
import br.edu.utfpr.dv.siacoes.model.ActivityUnit;

public class ActivityUnitDAO {
  // inicializando as variavéis que são utilizadas em todos métodos
	Connection conn = null;
	PreparedStatement pStmt = null;
	ResultSet rs = null;
	Statement stmt = null;
	
	public List<ActivityUnit> listAll() throws SQLException{		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.createStatement();
		
			rs = stmt.executeQuery("SELECT * FROM activityunit ORDER BY description");
			
			List<ActivityUnit> list = new ArrayList<ActivityUnit>();
			
			while(rs.next()){
				list.add(this.loadObject(rs));
			}
			
			return list;
		}finally{
			ConnectionCloseStatement(conn, stmt, rs);
		}
	}
	
	public ActivityUnit findById(int id) throws SQLException{		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM activityunit WHERE idActivityUnit=?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.loadObject(rs);
			}else{
				return null;
			}
		}finally{
      ConnectionClosePreparedStatement(conn, pStmt, rs);
		}
	}
	
	public int save(int idUser, ActivityUnit unit) throws SQLException{
		boolean insert = (unit.getIdActivityUnit() == 0);
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			
			if(insert){
				pStmt = conn.prepareStatement("INSERT INTO activityunit(description, fillAmount, amountDescription) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				pStmt = conn.prepareStatement("UPDATE activityunit SET description=?, fillAmount=?, amountDescription=? WHERE idActivityUnit=?");
			}
			
			pStmt.setString(1, unit.getDescription());
			pStmt.setInt(2, (unit.isFillAmount() ? 1 : 0));
			pStmt.setString(3, unit.getAmountDescription());
			
			if(!insert){
				pStmt.setInt(4, unit.getIdActivityUnit());
			}
			
			pStmt.execute();
			
			if(insert){
				rs = pStmt.getGeneratedKeys();
				
				if(rs.next()){
					unit.setIdActivityUnit(rs.getInt(1));
				}
				
				new UpdateEvent(conn).registerInsert(idUser, unit);
			} else {
				new UpdateEvent(conn).registerUpdate(idUser, unit);
			}
			
			return unit.getIdActivityUnit();
		}finally{
      ConnectionClosePreparedStatement(conn, pStmt, rs);
		}
	}
	
	private ActivityUnit loadObject(ResultSet rs) throws SQLException{
		ActivityUnit unit = new ActivityUnit();
		
		unit.setIdActivityUnit(rs.getInt("idActivityUnit"));
		unit.setDescription(rs.getString("Description"));
		unit.setFillAmount(rs.getInt("fillAmount") == 1);
		unit.setAmountDescription(rs.getString("amountDescription"));
		
		return unit;
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

package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.DatiGrafo;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors(Map<Integer, Constructor> idMap) {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				if(!idMap.containsKey(rs.getInt("constructorId"))) {
					Constructor c = new Constructor(rs.getInt("constructorId"), rs.getString("name"));
					idMap.put(rs.getInt("constructorId"), c);
					constructors.add(c);
				}else {
					constructors.add(idMap.get(rs.getInt("constructorId")));
				}
				
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}


	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

//		List<Constructor> constructors = dao.getAllConstructors();
//		System.out.println(constructors);
		
	}

	public List<DatiGrafo> getDatiGrafo(Map<Integer, Constructor> idMap, Circuit c) {
		final String sql=	"SELECT r1.constructorId AS idC1, r2.constructorId AS idC2, COUNT(*) AS peso " + 
							"FROM results AS r1, results AS r2, races AS r " + 
							"WHERE r.circuitId=? " + 
							"AND r.raceId=r1.raceId " + 
							"AND r.raceId=r2.raceId " + 
							"AND r1.constructorId > r2.constructorId " + 
							"AND r1.POSITION < r2.position " + 
							"AND (r1.positionText!='R' OR r1.positionText!='D') " + 
							"AND (r2.positionText!='R' OR r2.positionText!='D') " + 
							"GROUP BY idC1, idC2";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, c.getCircuitId());

			ResultSet rs = st.executeQuery();

			List<DatiGrafo> datiGrafo = new LinkedList<>();
			while (rs.next()) {
				Constructor c1 = idMap.get(rs.getInt("idC1"));
				Constructor c2 = idMap.get(rs.getInt("idC2"));
				if(c1!=null && c2!=null) {
					DatiGrafo dg = new DatiGrafo(c1, c2, rs.getDouble("peso"));
					datiGrafo.add(dg);
				}
			}

			conn.close();
			return datiGrafo;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
}

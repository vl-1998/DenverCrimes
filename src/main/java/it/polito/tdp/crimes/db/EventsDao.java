package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> categoriaReato (){
		String sql = "select distinct offense_category_id from events";
		Connection conn = DBConnect.getConnection();
		List <String> reati = new ArrayList<>();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				reati.add(res.getString("offense_category_id"));
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reati;
	}
	
	public List<Integer> mesi (){
		String sql = "select distinct Month(reported_date) as mese from events";
		Connection conn = DBConnect.getConnection();
		List <Integer> mesi = new ArrayList<>();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				mesi.add(res.getInt("mese"));
			}
			conn.close();
			Collections.sort(mesi);//li ordino
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mesi;
	}

	public List<Adiacenza> getAdiacenze(String tipoReato, Integer mese) {
		String sql = "select e1.offense_type_id, e2.offense_type_id, " + 
				"COUNT(distinct(e1.neighborhood_id)) as peso " + 
				"from events e1, events e2 " + 
				"where e1.offense_category_id=? " + 
				"and e2.offense_category_id=? " + 
				"and Month(e1.reported_date)=? " + 
				"and Month(e2.reported_date)=? " + 
				"and e1.offense_type_id != e2.offense_type_id " + 
				"and e1.neighborhood_id = e2.neighborhood_id " + 
				"group by e1.offense_type_id, e2.offense_type_id";
		
		Connection conn = DBConnect.getConnection();
		List <Adiacenza> adiacenze = new ArrayList<>();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, tipoReato);
			st.setString(2, tipoReato);
			st.setInt(3, mese);
			st.setInt(4, mese);
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				Adiacenza a = new Adiacenza (res.getString("e1.offense_type_id"), res.getString("e2.offense_type_id"), res.getDouble("peso"));
				adiacenze.add(a);
			}
			conn.close();
			return adiacenze;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

}

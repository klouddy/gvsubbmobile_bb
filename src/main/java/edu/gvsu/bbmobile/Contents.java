package edu.gvsu.bbmobile;

import blackboard.data.content.Content;
import blackboard.data.course.Course;
import blackboard.data.navigation.CourseToc;
import blackboard.db.BbDatabase;
import blackboard.db.ConnectionNotAvailableException;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.content.ContentDbLoader;
import blackboard.persist.navigation.CourseTocDbLoader;
import blackboard.platform.db.JdbcServiceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contents {

		private Courses myCourses;
		private ContentDbLoader cntLoader;
		private CourseTocDbLoader tocLoader;
		
		private BbDatabase db;
		private Connection conn;
		
		private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
	    private blackboard.platform.log.Log log = logService.getDefaultLog();
	    private String TAG = "BB CONTENTS";

		
		public Contents(){
			myCourses = new Courses();
			
			try {
				cntLoader = ContentDbLoader.Default.getInstance();
				tocLoader = CourseTocDbLoader.Default.getInstance();
				db = JdbcServiceFactory.getInstance().getDefaultDatabase();
				
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		public List<Map> getContents(String strUsername){
			List<Map> lstRet = new ArrayList<Map>();
			
			//build list of courses
			String sqlCrsIn = "(";
			List<Course> crs = myCourses.getAvailableCoursesForUserList(strUsername);
			int i = 0;
			for(Course c : crs){
				if(i >0 ){ sqlCrsIn += ", "; }
				String pId = getIntFromIdString(c.getId().getExternalString());
				sqlCrsIn += pId;
				i++;
			}
			sqlCrsIn += ")";
			
			String sql = "select cm.pk1 as crs_pk1, cm.course_id as crs_id, cm.course_name as crs_name, ";
				sql += "gbm.pk1 as gb_pk1, gbm.due_date, ";
				sql += "crscnts.pk1 as cnt_pk1, crscnts.cnthndlr_handle as cnt_handle, crscnts.position as position, "
						+ "crscnts.title as cnt_title, crscnts.start_date as cnt_start, crscnts.end_date as cnt_end ";
				sql += "FROM bblearn.gradebook_main gbm, bblearn.course_main cm, bblearn.course_contents crscnts ";
				sql += "WHERE cm.pk1 in " + sqlCrsIn + " ";
				sql += "and gbm.crsmain_pk1 = cm.pk1 and crscnts.available_ind like 'Y' "
						+ "and gbm.course_contents_pk1 = crscnts.pk1 "
						+ "and ((crscnts.start_date <= current_date and current_date <= crscnts.end_date) "
						+ "or (crscnts.start_date is null and crscnts.end_date >= current_date  ) "
						+ "or (crscnts.end_date is null and crscnts.start_date <= current_date) "
						+ "or (crscnts.end_date is null and crscnts.start_date is null))";
				
			
				PreparedStatement query = null;
				try {
					conn = db.getConnectionManager().getConnection();
					log.logError(TAG + " QUERY: " + sql);
					query = conn.prepareStatement(sql);
					query.execute();
					ResultSet rs = query.getResultSet();
					
					
					
					while(rs.next()){
						Map cMap = new HashMap();
						cMap.put("cnt_id", "_" + String.valueOf(rs.getInt("cnt_pk1") + "_1"));
						cMap.put("cnt_label", rs.getString("cnt_title"));
						cMap.put("cnt_pos", rs.getString("position"));
						
						BbObjectType obType = new BbObjectType();
						cMap.put("cnt_type", obType.getIdForHanlde(rs.getString("cnt_handle")));
						cMap.put("cnt_handle", rs.getString("cnt_handle"));
						cMap.put("cnt_start", rs.getString("cnt_start"));
						cMap.put("cnt_end", rs.getString("cnt_end"));
						cMap.put("crs_id", "_" + String.valueOf(rs.getInt("crs_pk1")) + "_1");
						cMap.put("crs_title", rs.getString("crs_name"));
						cMap.put("due_date", rs.getString("due_date"));
						
						log.logError(TAG + "map: " + cMap.toString());
						lstRet.add(cMap);
					}
					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
				
					log.logError(TAG, e);
					log.logError(TAG + e.getMessage());
					e.printStackTrace();
				} catch (ConnectionNotAvailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return lstRet;
		}
		
		private String getIntFromIdString(String strId){
			
			String parsedId = strId.substring(1, strId.indexOf("_", 1));
			return parsedId;
		}
}

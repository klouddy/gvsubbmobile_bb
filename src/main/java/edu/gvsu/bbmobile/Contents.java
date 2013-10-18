package edu.gvsu.bbmobile;

import blackboard.data.announcement.Announcement;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.gvsu.bbmobile.compare.CompareContentRecent;
import edu.gvsu.bbmobile.compare.CompareGradeableSoonest;

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
		
		/**
		 * Gets all gradeable content for usersname passed
		 * @param strUsername
		 * @return List of Map items for content info
		 */
		public List<Map> getGradedContent(String strUsername){
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
						+ "crscnts.title as cnt_title, crscnts.start_date as cnt_start, crscnts.end_date as cnt_end, "
						+ "crscnts.available_ind as cnt_available, crscnts.DTMODIFIED as cnt_date_mod ";
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
					query = conn.prepareStatement(sql);
					query.execute();
					ResultSet rs = query.getResultSet();
					
					while(rs.next()){
						Map cMap = new HashMap();
						cMap.put("cnt_id", "_" + String.valueOf(rs.getInt("cnt_pk1") + "_1"));
						cMap.put("cnt_label", rs.getString("cnt_title"));
						cMap.put("cnt_pos", rs.getString("position"));
						cMap.put("type", BbObjectType.GRADEABLE);
						cMap.put("cnt_handle", rs.getString("cnt_handle"));
						cMap.put("cnt_start", rs.getString("cnt_start"));
						cMap.put("cnt_end", rs.getString("cnt_end"));
						cMap.put("crs_id", "_" + String.valueOf(rs.getInt("crs_pk1")) + "_1");
						cMap.put("crs_title", rs.getString("crs_name"));
						cMap.put("due_date", rs.getString("due_date"));
						cMap.put("cnt_available", rs.getString("cnt_available"));
						cMap.put("cnt_date_mod", rs.getString("cnt_date_mod"));
						cMap.put("cnt_recent_date", getRecentDate(cMap));
						if(getCompDueDate(cMap) != null){
							cMap.put("cnt_comp_due", getCompDueDate(cMap));
						}

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
		
		/**
		 * Get recent gradeable items only return top most recent bassed on limit
		 * @param userName
		 * @param limit
		 * @return return List of Map items with details of gradeable content
		 */
		public List<Map> getRecentGradeableContents(String userName, Integer limit){
			List<Map> theRet = new ArrayList<Map>();
			List<Map> allCnts = getGradedContent(userName);
			
			if(allCnts != null && allCnts.size() > 0){
				Collections.sort(allCnts, new CompareGradeableSoonest());
			}
			if(limit == null){
				limit = 10;
			}
			if(limit > allCnts.size()){
				limit = allCnts.size();
			}
			
			for(int i = 0; i < limit; i++){
				allCnts.get(i).remove("cnts_recent_date");
				theRet.add(allCnts.get(i));
			}
			
			return theRet;
		}
		
		/**
		 * gets recent contents up to limit (default 10 if null passed)
		 * only passing along conent in course passed in crsId
		 * @param userName
		 * @param limit
		 * @param crsId
		 * @return
		 */
		public List<Map> getRecentGradeableContents(String userName, Integer limit, String crsId){
			List<Map> theRet = new ArrayList<Map>();
			List<Map> allCnts = getGradedContent(userName);
			
			if(allCnts != null && allCnts.size() > 0){
				Collections.sort(allCnts, new CompareGradeableSoonest());
			}
			if(limit == null){
				limit = 10;
			}
			int i = 0;
			int k = 0;
			while(i < limit && k < allCnts.size()){
				if(allCnts.get(k).get("crs_id").equals(crsId)){
					allCnts.get(k).remove("cnts_recent_date");
					theRet.add(allCnts.get(k));
					i++;
				}
				k++;
			}
			return theRet;
		}
		
		/**
		 * Gets contents for username passed		
		 * @param strUsername
		 * @return list of Map items with details for content item.
		 */
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
				sql += "crscnts.pk1 as cnt_pk1, crscnts.cnthndlr_handle as cnt_handle, crscnts.position as position, "
						+ "crscnts.title as cnt_title, crscnts.start_date as cnt_start, crscnts.end_date as cnt_end, "
						+ "crscnts.available_ind as cnt_available, crscnts.DTMODIFIED as cnt_date_mod ";
				sql += "FROM bblearn.course_main cm, bblearn.course_contents crscnts ";
				sql += "WHERE cm.pk1 in " + sqlCrsIn + " ";
				sql += "and crscnts.available_ind like 'Y' "
						+ "and crscnts.crsmain_pk1 = cm.pk1 "
						+ "and ((crscnts.start_date <= current_date and current_date <= crscnts.end_date) "
						+ "or (crscnts.start_date is null and crscnts.end_date >= current_date  ) "
						+ "or (crscnts.end_date is null and crscnts.start_date <= current_date) "
						+ "or (crscnts.end_date is null and crscnts.start_date is null))";
				
				PreparedStatement query = null;
				
				try {
					conn = db.getConnectionManager().getConnection();
					query = conn.prepareStatement(sql);
					query.execute();
					ResultSet rs = query.getResultSet();
					
					while(rs.next()){
						Map cMap = new HashMap();
						cMap.put("cnt_id", "_" + String.valueOf(rs.getInt("cnt_pk1") + "_1"));
						cMap.put("cnt_label", rs.getString("cnt_title"));
						cMap.put("cnt_pos", rs.getString("position"));
						cMap.put("cnt_type", BbObjectType.CONTENT_ITEM);
						cMap.put("type", BbObjectType.CONTENT_ITEM);
						cMap.put("cnt_handle", rs.getString("cnt_handle"));
						cMap.put("cnt_start", rs.getString("cnt_start"));
						cMap.put("cnt_end", rs.getString("cnt_end"));
						cMap.put("crs_id", "_" + String.valueOf(rs.getInt("crs_pk1")) + "_1");
						cMap.put("crs_title", rs.getString("crs_name"));
						cMap.put("cnt_available", rs.getString("cnt_available"));
						cMap.put("cnt_date_mod", rs.getString("cnt_date_mod"));
						cMap.put("cnt_recent_date", getRecentDate(cMap));
						
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
		
		/**
		 * This gets recent contents up to limit (default 10 if passed null)
		 * @param userName
		 * @param limit
		 * @return
		 */
		public List<Map> getRecentContents(String userName, Integer limit){
			List<Map> theRet = new ArrayList<Map>();
			List<Map> allCnts = getContents(userName);
			
			if(allCnts != null && allCnts.size() > 0){
				Collections.sort(allCnts, new CompareContentRecent());
			}
			if(limit == null){
				limit = 10;
			}
			if(limit > allCnts.size()){
				limit = allCnts.size();
			}
			
			for(int i = 0; i < limit; i++){
				allCnts.get(i).remove("cnts_recent_date");
				theRet.add(allCnts.get(i));
			}
			
			return theRet;
		}
		
		/**
		 * gets recent contents up to limit (default 10 if null passed)
		 * only passing along conent in course passed in crsId
		 * @param userName
		 * @param limit
		 * @param crsId
		 * @return
		 */
		public List<Map> getRecentContents(String userName, Integer limit, String crsId){
			List<Map> theRet = new ArrayList<Map>();
			List<Map> allCnts = getContents(userName);
			
			if(allCnts != null && allCnts.size() > 0){
				Collections.sort(allCnts, new CompareContentRecent());
			}
			if(limit == null){
				limit = 10;
			}
			int i = 0;
			int k = 0;
			while(i < limit && k < allCnts.size()){
				if(allCnts.get(k).get("crs_id").equals(crsId)){
					allCnts.get(k).remove("cnts_recent_date");
					theRet.add(allCnts.get(k));
					i++;
				}
				k++;
			}
			return theRet;
		}
		
		/**
		 * Gets gradeable item and return what makes the most sense for hte due date (due_date or if not then cnt_end_date)
		 * or if none exists than null
		 * @param item
		 * @return
		 */
		private Date getCompDueDate(Map item){
			
			if(item.get("due_date") != null){
				try {
					return new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse((String) item.get("due_date"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
			else if(item.get("cnt_end") != null){
				try{
					return new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse((String) item.get("cnt_end"));
				}catch (ParseException e){
					//TDO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
			return null;
		}
		
		private Date getRecentDate(Map item){
			
			Date date = new Date();
			String cntStart = (String) item.get("cnt_start");
			if(cntStart != null && !cntStart.equals("")){
				try {
					date = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse((String) item.get("cnt_start"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return date;
			}
			else{
				try {
					date = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH).parse((String) item.get("cnt_date_mod"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return date;
			
		}
		
		private String getIntFromIdString(String strId){
			
			String parsedId = strId.substring(1, strId.indexOf("_", 1));
			return parsedId;
		}
}

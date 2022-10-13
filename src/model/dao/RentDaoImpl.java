package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import model.RentDao;

//RentDaoImpl implements RentDao 의 뜻은
//인터페이스를 물려받게 해줘라 ~
//RentDao  틀대로 개발하려합니다 이뜻
public class RentDaoImpl implements RentDao{
	//빨간불떠서 add 해주면 인터페이스에서 만든함수들 자동으로 다 만들어줌

	final static String DRIVER 	="oracle.jdbc.driver.OracleDriver"; 
	final static String URL 	= "jdbc:oracle:thin:@192.168.0.48:1521:xe";
	final static String USER 	= "beak";
	final static String PASS 	= "1234";



	public RentDaoImpl() throws Exception{
		// 1. 드라이버로딩
		Class.forName(DRIVER);
		System.out.println("드라이버 로딩성공");
	}




	//1.
	@Override
	public void rentVideo(String tel, int vnum) throws Exception {
		// 2. Connection 연결객체 얻어오기
		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			// 3. sql 문장 만들기 - 만들기전에 시퀀스를 프라이머리키인 랜트넘버로 sql에서 설정하고하자
				//주의 tel은 멤버에서 연동된 tel임 , V_NUMBER는 비디오에서 연동된것 (각각의 프라이 머리키들)
			String sql = "INSERT INTO RENTAL(R_NUMBER, TEL, V_NUMBER, V_DAY, V_VAN) VALUES(seq_ren_num.nextval,?,?,sysdate+3,'N')";

			//4. 전송객체 
			ps = con.prepareStatement(sql);
			
			//실직적 1번은 R_NUMBER 렌트넘버는 시퀀스로해줘야함 시퀀스가 자동 랜트번호 나오게 쭉쭉
			//그다음 칸부터가 1번이니 1번 tel은 tel에서 받아온거니 ? 하고 tel
			//2번은 비디오 넘버 받아온거니 ?하고 vnum 넣어주고
			//3,4번은 sysdata, n으로 ? 대신 값을 픽시해서 넣어준다
			ps.setString(1, tel);
			ps.setInt(2, vnum);


			//5전송
			ps.executeUpdate();

			//6.닫기
		}finally{

			ps.close(); 
			con.close();
		}

	} //end of rentVideo();


	//1013 returnVideo(0
	//returnVideo 함수 인자로 vo같은 모든정보가 아닌 vnum만 받는다! 비디오 번호만 던졌으니까
	//Rentview의 returnClick 함수의 sql담당 부분함수임
	
	@Override
	public void returnVideo(int vnum) throws Exception {
		// 2. Connection 연결객체 얻어오기

		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			// 3.sql 문장
			String sql = "UPDATE RENTAL SET V_VAN = 'Y' WHERE V_NUMBER = ? AND V_VAN = 'N'";
			//AND V_VAN = 'N' 해주는 이유는 그전것까지 다 바꿔버리기 때문에 지금것만 바꾸기 위해서 
			//반납한걸 또 반납한다고 해버리는건 전부 모든걸 바꾸는거니까 앤드를 이용해 처리해준다 마지막만
			//4 .전송객체
			ps = con.prepareStatement(sql);
			ps.setInt(1, vnum);
			//5전송
			ps.executeUpdate();

			//6.닫기
		}finally{

			ps.close(); 
			con.close();
		}

	}// end of returnVideo
	
	
	//1013 rentSelectTel();
	//전화번호 입력하고 엔터치면 이름나오게 하기
	public String rentSelectTel(String tel) throws SQLException {
		Connection con =null; 
		PreparedStatement ps = null; 
		//리턴값이 스트링
		String name = null;
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			//3.sql 문장
			//아까 멤버에 저장되어있는 이름을 가져와야함  WHERE TEL = ? 번호를 받아서를 의미
			String sql = "SELECT name FROM MEMBER WHERE TEL = ?  ";
			//4 전송객체
			ps = con.prepareStatement(sql);
			ps.setString(1, tel);
			
			//5 전송
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				//getString 컬럼명 name을 넣어주고 그걸 name에 담아서 리턴해줘야함
				//리턴할 name변수에 저장을 해줘야 던져주지 리턴으로!
				name = rs.getString("name");
				
			}
			//6.닫기
		}finally{

			ps.close(); 
			con.close();
		}
		return name;
	}
	
	
	//1013 미납목록 크게 뜨게하는 함수의 sql부분
		//ArrayList 를 사용하는게 중점임
	@Override
	public ArrayList selectList() throws Exception {
		//비디오다오임풀에 똑같이 어레이리스트 내용있는거 1013일에 복사해 넣엇음
			//sql문장 수정해야함, String[] colNames 배열도 필요없음
		ArrayList data = new ArrayList();
		
		//2.연결객체 얻어오기
		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			//3.1012일 sql 문장 - 그냥 비디오찾기에서 엔터 치면 저장된 모든 비디오정보가 다뜨는
			//String sql = "SELECT V_NUMBER,V_NAME,V_DIRECTOR,V_ACTOR FROM VIDEO ";

		
			// 조인을 이용해야함 WHERE R.V_VAN = 'N' "; 의 이유는 이걸로 판단하기 위해
			String sql = "  SELECT V.V_NUMBER VNUM, V.V_NAME VNAME, C.NAME CNAME, C.TEL CTEL, R.V_DAY+3 VDAY, 'N' VAN   "
					+ "  FROM MEMBER C inner join RENTAL R "
					+ "  ON C.TEL = R.TEL  inner join VIDEO V "
					+ "  ON V.V_NUMBER = R.V_NUMBER  "
					+ "  WHERE R.V_VAN = 'N' ";
				

			//4.전송객체
			ps = con.prepareStatement(sql);
			//5.전송
			ResultSet rs = ps.executeQuery();

			//개수가 불정확하니 while문
			while(rs.next()) { 
				ArrayList temp = new ArrayList();
				//새로운 temp를 만들어서 넣어주는데 별칭을 꼭 넣어주자
				temp.add(rs.getInt("VNUM"));
				temp.add(rs.getString("VNAME"));
				temp.add(rs.getString("CNAME"));
				temp.add(rs.getString("CTEL"));
				temp.add(rs.getString("VDAY"));
				temp.add(rs.getString("VAN"));
				data.add(temp);
			}
			return data;
		}finally{

			ps.close(); 
			con.close();
		}//end try.fin
		
	} // end of selectList();


}//end of main

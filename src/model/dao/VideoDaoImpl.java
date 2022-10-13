package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.VideoDao;
import model.vo.VideoVO;

public class VideoDaoImpl implements VideoDao{

	final static String DRIVER 	="oracle.jdbc.driver.OracleDriver"; 
	final static String URL 	= "jdbc:oracle:thin:@192.168.0.48:1521:xe";
	final static String USER 	= "beak";
	final static String PASS 	= "1234";

	public VideoDaoImpl() throws Exception{


		// 1. 드라이버로딩
		Class.forName(DRIVER);
		System.out.println("드라이버 로딩성공");


	} //end of VideoDaoImpl()



	//1번행동함수 insertVideo() 
	//인자로 vo, count를 받는다
	//count 는 입고수량 때문에 설정한거임 맨아래쪽버튼에
	//VideoView 클래스안에있는 registVideo 의 내용을 sql로 정리한 함수다

	public void insertVideo(VideoVO vo, int count) throws Exception{
		// 2. Connection 연결객체 얻어오기
		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			// 3. sql 문장 만들기
			//일단 V_NUMBER는 장르가 여러개인 버튼이기에 sql에서 시퀀스설정을 먼저해준다꼭
			//넘버 1 2 3 4 5 6 자동으로 나오게 할라고 시퀀스만들기
			//그래서 6개의 컬럼은 모두적어두되 1번은 이미 시퀀스가 들어간다했으니 나머지 물음표 5개만
			//아래 전송객체 순서대로 넣어주면된다
			String sql = "INSERT INTO VIDEO(V_NUMBER,V_TYPE,V_NAME,V_DIRECTOR,V_ACTOR,V_EXP) VALUES(seq_v_num.nextval, ? ,?, ? ,? ,?)";
			// 4. sql 전송객체 (PreparedStatement)		
			ps = con.prepareStatement(sql);

			ps.setString(1, vo.getV_type());
			ps.setString(2, vo.getV_name());
			ps.setString(3, vo.getV_director());
			ps.setString(4, vo.getV_actor());
			ps.setString(5, vo.getV_exp());


			// 5. sql 전송
			for(int i = 0; i<count; i++) {
				ps.executeUpdate();
			}

			// 6. 닫기
		}finally{

			ps.close(); 
			con.close();
		}
	} // end of insertVideo



	//2번 selectVideo() 함수 1013
	//비디오 검색칸에서 엔터를그냥치거나 아니면 제목을 넣어 그비디오 정보들이 배열로 
	//출력되게만드는 함수의sql 부분
	//videoview 클래스 searchVideo 에서 이제는 내가 원하는 영화를 검색하기위해
	//구문들을 추가해 인덱스와 고객이입력한 값 변수2개를 설정했다 그래서 아래 매개변수칸에
	//매개변수로 던져주었다
	
	public ArrayList selectVideo(int idx, String word) throws Exception {
		//정보가 딱 몇개다 라고 정해진게 없으니 ArrayList이용
		ArrayList data = new ArrayList();
		
		//2.연결객체 얻어오기
		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			//3. sql문장 - 엔터치면 전체정보 or 내가원하는 비디오번호만 찾기
			
			//제목,감독버튼 배열을 위한 변수1개설정 
			String[] colNames = {"V_NAME","V_DIRECTOR"};
			//?쓰면 안됨 '' 까지 자동을 붙어버려서 그래서 이전에 설정해 놓았던 변수명으로 넣어줘야함
			//SELECT 전체정보나오게 , WHERE 에 이제 변수명으로 넣어준다
			
			String sql = "SELECT V_NUMBER,V_NAME,V_DIRECTOR,V_ACTOR FROM VIDEO "
					+ "  WHERE " + colNames[idx] + "  LIKE '%"+ word + "%' ";

			//4.전송객체
			ps = con.prepareStatement(sql);
			
			//5.전송
			//ResultSet rs 가 있으면 while 이나 if 가 꼭 따라붙는다
			//지금은 ArrayList로 정해진 갯수가 없으니 while문 으로 실행
			ResultSet rs = ps.executeQuery(); 
			
			while(rs.next()) { //갯수 모르니 while
				
				//한 비디오의 정보를 temp에 저장
				//여기도 몇개인지 모르니 ArrayList 데이터에 담는것
				//위에 ArrayList data 랑은 다르며 두개를 이용해 2중 배열로 정보를 나오게 하는것
				//2차원 배열느낌인거임 ArrayList 에 ArrayList
				ArrayList temp = new ArrayList();
				temp.add(rs.getInt("V_NUMBER"));
				temp.add(rs.getString("V_NAME"));
				temp.add(rs.getString("V_DIRECTOR"));
				temp.add(rs.getString("V_ACTOR"));
				data.add(temp);
			}
			return data;
		}finally{

			ps.close(); 
			con.close();
		}


	} //end of selectVideo();



	//3번 selectBynum() 함수
	//비디오검색에서 나온 저장된 비디오 정보들가운데 어디 눌렀을때 그 비디오 정보가 쭉
	//텍스트칸에 자동으로 채워지게 만드는 함수
	//인자 비디오 번호
	//리턴값 비디오 정보
	//역활 비디오 번호를 넘겨받아 해당 비디오 번호의 비디오 정보를 리턴
	public VideoVO selectBynum(int Vnum) throws Exception{

		VideoVO vo = new VideoVO();
		//2.연결객체 얻어오기
		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			//3.sql 문장
			String sql = "SELECT * FROM VIDEO WHERE V_NUMBER = ?  ";


			//4.전송객체
			ps = con.prepareStatement(sql);
			ps.setInt(1, Vnum);

			//5.전송
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				vo.setV_number(rs.getInt("V_NUMBER"));
				vo.setV_type(rs.getString("V_TYPE"));
				vo.setV_name(rs.getString("V_NAME"));
				vo.setV_director(rs.getString("V_DIRECTOR"));
				vo.setV_actor(rs.getString("V_ACTOR"));
				vo.setV_exp(rs.getString("V_EXP"));

			}
			return vo;
		}finally{

			ps.close(); 
			con.close();
		}
	}


	//10.13
	//4번 modifyVideo 수정을 위한메소드
	//비디오 넘버가 프라이머리키
	
	//인터페이스 만들고나면 맨위 클래스 빨간줄에서 클릭해서 add 플레이하면 아래 이함수들이 자동으로생김
	@Override 
	public int modifyVideo(VideoVO vo) throws Exception {
		//2.연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps = null; 
		//리턴값이 int니 int값 넘겨주기 함수선언
		//5번 전송에서 이 result 함수를 이용해 ps.executeUpdate();를 받을꺼임
		int result = 0;

		try {
			con = DriverManager.getConnection(URL, USER, PASS);

			// 3.sql 문장
			//프라이머리키인 V_NUMBER 만 where 구문에 넣어줌
			String sql = "UPDATE VIDEO SET V_TYPE = ?, V_NAME = ?, V_DIRECTOR = ?, V_ACTOR = ?, V_EXP = ? WHERE V_NUMBER = ? ";
			//4 .전송객체
			ps = con.prepareStatement(sql);
			
			ps.setString(1, vo.getV_type());
			ps.setString(2, vo.getV_name());
			ps.setString(3, vo.getV_director());
			ps.setString(4, vo.getV_actor());
			ps.setString(5, vo.getV_exp());
			
			ps.setInt(6, vo.getV_number());
			
			
			//5.전송
			//위에서 선언한 result변수에 전송 값을 담아준다
			//그리고 리턴을  result로 던져버린다
			result = ps.executeUpdate(); 
			
		}finally {
			// 6. 닫기 
			ps.close();
			con.close();  
		} // end of try
		
		return result;
		
	}//end of modifyVideo



	
	
	//5.deleteVideo 정보띄운다움 삭제누르면 정보 삭제되게 만드는 함수의 sql과 연결부분 함수
	//넘어온 매겨변수 model.deleteVideo(vNum); vNum 넘어옴
	//비디오 넘버가 프라이머리키
		@Override
		public int deleteVideo(int Vnum) throws Exception {
			//2.연결객체 얻어오기
			Connection con = null;
			PreparedStatement ps = null; 
			int result = 0;

			try {
				con = DriverManager.getConnection(URL, USER, PASS);
				//3.sql 문장
				//WHERE V_NUMBER = ? 프라이머리키인 비디오넘버를 웨얼로
				String sql ="DELETE  FROM VIDEO WHERE V_NUMBER = ?";
				//4.전송객체 얻어오기
				ps = con.prepareStatement(sql);
				ps.setInt(1, Vnum);
				
				
				//5.전송
				result = ps.executeUpdate(); // 변수선언한 리설트에 전송 그래야 리턴되니
			}finally{

				//6.닫기 
				ps.close(); 
				con.close();
			}
			return result;
			
		}
	} // end of selectBynum

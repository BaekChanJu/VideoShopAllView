//CustomerDaoImpl implements CustomerDao

package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.CustomerDao;
import model.vo.CustomerVO;

public class CustomerDaoImpl implements CustomerDao{

	// 필드에 기본 설정을 먼저해준다
	//final static 해주고 final static 해준 변수들은 대문자로선언 기억!
	final static String DRIVER 	="oracle.jdbc.driver.OracleDriver"; 
	final static String URL 	= "jdbc:oracle:thin:@192.168.0.48:1521:xe";
	final static String USER 	= "beak";
	final static String PASS 	= "1234";



	public CustomerDaoImpl() throws Exception{
		// 1. 드라이버로딩

		Class.forName(DRIVER);
		System.out.println("드라이버 로딩 성공");


	}


	//1번 - CustomerView 클래스에서 고객이 회원가입눌렀을때 함수는 이미 만들어져있고
	//여기서는 sql연동을 위한 함수
	public void insertCustomer(CustomerVO vo) throws Exception{
		// 2. Connection 연결객체 얻어오기
		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			// 3. sql 문장 만들기
			//인설트 인투로 고객정보를 저장하는것임
			String sql = " INSERT INTO MEMBER(tel, name, addr, email, addtel) VALUES(?,?,?,?,?) ";
			// 4. sql 전송객체 (PreparedStatement)		
			ps = con.prepareStatement(sql);

			ps.setString(1, vo.getTel());
			ps.setString(2, vo.getName());
			ps.setString(3, vo.getAddr());
			ps.setString(4, vo.getEmail());
			ps.setString(5, vo.getAddtel());
			// 5. sql 전송
			ps.executeUpdate();
		}finally{
			//6.닫기 
			ps.close(); 
			con.close();
		}

	} //end of insertCustomer();




	//2번 고객이 번호검색을 눌렀을때 고객정보를 표현하기위한 함수의 sql 부분을 맞은 함수
	//메소드명 : selectByTel
	//인자 : 검색할 전화번호
	//리턴값 : 전화번호 검색에 따른 고객정보
	//역활 : 사용자가 입력한 전화번호를 받아서 해당하는 고객 정보를 리턴하는 역활임
	public CustomerVO selectByTel(String tel) throws Exception{
		CustomerVO dao = new CustomerVO();


		//2.연결객체 얻어오기
		Connection con =null; 
		PreparedStatement ps = null; 
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			//3.sql문장만들기
			String sql ="SELECT * FROM MEMBER WHERE tel = ?";
			//4.전송객체
			ps = con.prepareStatement(sql);

			ps.setString(1, tel);
			//5.전송 -  셀렉트니까 exrcuteQuery 사용해야함
			// 결과를 CustomerVO dao 에 담기
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				dao.setName(rs.getString("NAME"));
				dao.setTel(rs.getString("TEL"));
				dao.setAddr(rs.getString("ADDR"));
				dao.setEmail(rs.getString("EMAIL"));
				dao.setAddtel(rs.getString("ADDTEL"));
			}
			rs.close();

		}finally{
			//6.닫기 
			ps.close();
			con.close();
		}
		return dao;

	}// end of selectByTel



	//3번 고객이 회원수정 눌렀을때의 역활을 하는 함수에서 sql부분을 담당하는함수
	public int updateCustomer(CustomerVO vo) throws Exception{
		//2.연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps = null; 
		int result = 0;

		try {
			con = DriverManager.getConnection(URL, USER, PASS);

			// 3.sql 문장
			//지금은 번호검색에서 그사람의 정보를 나오게 한걸 수정하기때문에
			//WHERE tel = ? 이걸 해줘야한다 SET에서 tel을 빼고
			//그리고 순서대로 입력을 받는다

			//그냥 예외로 그럼 똑같이 번호검색해서 그사람 정보띄운후 번호를 바꾸고 싶다면?
			//번호를 바꾸고 회원수정을 하면 바뀔까?
			//안바뀐다 우리는 지금 정보저장을 전화번호로 했기때문에 그 번호가 바뀌면 그사람 정보자체가 없기에 안된다
			String sql ="UPDATE MEMBER SET  name =?, addr =?, email=?, addtel=? WHERE tel = ? ";
			//4. 전송객체
			ps = con.prepareStatement(sql);


			ps.setString(1, vo.getName());
			ps.setString(2, vo.getAddr());
			ps.setString(3, vo.getEmail());
			ps.setString(4, vo.getAddtel());
			ps.setString(5, vo.getTel());

			//전송
			ps.executeUpdate();

		}finally {
			// 6. 닫기 무조건 finally 안에 넣어주기
			ps.close();
			con.close();  
		}
		return result;
	}
}

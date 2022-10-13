package model;

import model.vo.CustomerVO;

/** 고객관리 JDBC 연결 */
//인터페이스에서 먼저할 작업들을 체크해보자
public interface CustomerDao {
	public void insertCustomer(CustomerVO vo) throws Exception;		// 회원가입
	public CustomerVO selectByTel(String tel) throws Exception;			// 전화번호로 검색
	public int updateCustomer(CustomerVO vo) throws Exception;		// 고객정보 수정
}

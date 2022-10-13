package model;

import java.util.ArrayList;

import model.vo.VideoVO;


//앞으로 계속 여기에 추가해서 인터페이스를 사용해야한다
public interface VideoDao {
	public void insertVideo(VideoVO vo, int count) throws Exception;
	
	
	//비디오검색에서 엔터치가나 입력해서 검색했을때
	public ArrayList selectVideo(int idx, String word) throws Exception;
	
	
	//비디오 번호가 넘어와서  VideoVO(비디오 정보) 를 리턴한다
	public VideoVO selectBynum(int Vnum) throws Exception;
	
	
	//수정  - 뭐 삭제했는지 알려주려고 인트형 선언
		//전체정보를 뜨게 하기위해서 VideoVO vo 매개변수로 사용
	public	int modifyVideo(VideoVO vo) throws Exception;
	
	
	//삭제 프라이 머리키인  Vnum 번호로 삭제
	public int deleteVideo(int Vnum) throws Exception;
}

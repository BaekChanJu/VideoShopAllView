package model;

import java.util.ArrayList;
//이 틀을 물려받는 클래스가 있어야겠네 ~
public interface RentDao {
	
	//대여
	public void rentVideo(String tel, int vnum) throws Exception;
	//대여할때 필요한건 고객번호랑 비디오 번호가 필요하지? 매개변수로 넣어주자
	
	//반납
	//매개변수 vo 모든정보가 아니라 vnum 가져왔기에 정보가져올때 vnum만 가져오면됌
	public void returnVideo( int vnum) throws Exception;
	//반납할땐 뭐 비디오 번호만 알면되지
	
	//미납목록검색 목록이니 까 어레이리스트
	public ArrayList selectList() throws Exception;
	
	//전화번호치고 엔터를 치면 고객 이름이 나오게해야함
		//필요한건 고객번호뿐이니 매개변수로 넣어주자
	public String rentSelectTel(String tel) throws Exception;

}

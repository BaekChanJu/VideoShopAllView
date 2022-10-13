//vo가 있어야 겟세터 생성자를 쓸수있지
//여기는 customer를 위한 VO
package model.vo;

public class CustomerVO {
	String tel;		// 전화번호
	String name;			// 고객명
	String addr;			// 주소
	String email;		//이메일
	String addtel;		// 보조 전화번호
	
	
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddtel() {
		return addtel;
	}
	public void setAddtel(String addtel) {
		this.addtel = addtel;
	}
	
	
	
}


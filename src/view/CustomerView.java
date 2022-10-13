package	 view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.CustomerDao;
import model.dao.CustomerDaoImpl;
import model.vo.CustomerVO;


public class CustomerView extends JPanel 
{
	JFrame frm;
	JTextField	tfCustName, tfCustTel,  tfCustTelAid, tfCustAddr, tfCustEmail;
	JButton		bCustRegist, bCustModify;//회원가입버튼, 회원수정 버튼

	JTextField  tfCustNameSearch,  tfCustTelSearch;
	JButton		bCustNameSearch,  bCustTelSearch;//이름검색버튼, 번호검색버튼


	//비지니스 로직 만들어야 가져오징
	CustomerDao model; // CustomerDao 라고 선언해도 CustomerDaoImpl의 내용이다 라고 꼭 기억

	public CustomerView(){
		addLayout();
		connectDB();
		eventProc();
	}

	public void eventProc(){
		ButtonEventHandler btnHandler = new ButtonEventHandler();

		// 이벤트 등록
		bCustRegist.addActionListener(btnHandler);
		bCustModify.addActionListener(btnHandler);
		bCustNameSearch.addActionListener(btnHandler);
		bCustTelSearch.addActionListener(btnHandler);
	}

	// 버튼 이벤트 핸들러 만들기

	class ButtonEventHandler implements ActionListener{
		public void actionPerformed(ActionEvent ev){
			Object o = ev.getSource();

			if(o==bCustRegist){  
				registCustomer();  // 회원등록
			}
			else if(o==bCustModify){  
				updateCustomer();  // 회원정보수정
			}			
			else  if(o==bCustTelSearch){  // 이름검색
				searchByTel();      // 번호검색 버튼이 눌렸을경우
			}
			else if(o==bCustNameSearch){  // 이름검색
				System.out.println("이름검색");
			}
		}
	}

	/*registCustomer();
	 	회원가입 버튼이 눌러졌을떄의 이벤트 함수
	 		>> 입력창에 모든정보를 입력하고 회원가입을누르면
	 			정보가 저장이된다
	 */
	public void registCustomer(){

		// 1.고객이 개인정보 입력칸에 입력한 값을 얻어오는 행위 
			// 변수선언안해서 받으면 데이터가 날라감
		String name = tfCustName.getText();
		String tel = tfCustTel.getText();
		String telA = tfCustTelAid.getText();
		String add = tfCustAddr.getText();
		String email = tfCustEmail.getText();

		// 2. 1번에서 얻어온 값들을 덩어리로 만들어서 CustomerVO 클래스에 값으로 저장하기위해
			//CustomerVO 클래스를 객체로 만들어서 사용해주는것임!
		//생성자 쓰려면 CustomerVO vo = new CustomerVO(name ~~~);
		//여기서는 겟세터 이용
		
		CustomerVO vo = new CustomerVO();
		vo.setName(name);
		vo.setTel(tel);
		vo.setAddtel(telA);
		vo.setAddr(add);
		vo.setEmail(email);
		
	
		// 3. CustomerDaoImpl 클래스 안에(CustomerDao model; 쓸수있게 만들어둠)
		//insertCustomer() 메소드 호출하여 2번에서 만든 VO 객체를 넘김
		//CustomerDaoImpl 클래스안에있는 insertCustomer 함수를 모델을 이용해 
		//사용하는것 insertCustomer 함수 안에는 sql 2~6번 해야하는것들이 들어가있음
		//insertCustomer 함수에는 () 안에 CustomerVO vo 를 꼭 던져줘야함
		//인터페이스에  CustomerVO vo 를 받는다고 이미 선언이 되어져있음
		try {
			model.insertCustomer(vo);
			JOptionPane.showMessageDialog(null, "입력");
		} catch (Exception e) {
			System.out.println("입력실패");
			e.printStackTrace();
		}
		// 4. 화면 초기화
		clearText(); //화면 삭제를 위해 만든 함수
		
		//registCustomer 함수를 쓰기위해 sql문장도 필요하고 연결도 필요하니
			//CustomerDaoImpl 클래스로 돌아가서 insertCustomer의 내용들을 채워줘야한다
	}//end of registCustomer();
	
	//화면 삭제를 위해만듬
	void clearText() {
		tfCustName.setText(null);
		tfCustTel.setText(null);
		tfCustTelAid.setText(null);
		tfCustAddr.setText(null);
		tfCustEmail.setText(null);
	} //end of clearText();
	
	

	/*searchByTel();
 	위에서 정보저장했으니 전화번호(맨아래칸) 에 그저장된 전화번호 입력하고 번호검색을 
 	눌러주면 그사람의 모든정보가 나오게 해주는 함수
 		>> 입력창에 모든정보를 입력하고 회원가입을누르면
 			정보가 저장이된다
 			번호검색 버튼이 눌렸을때 전화번호는 프라이머리키
 			 CustomerDaoImpl searchByTel라는 함수가 있음 거기선 sql처리를 다해줄꺼임
 */
	public void searchByTel(){
		// 1. 입력한 전화번호 얻어오기
			//tfCustTelSearch 이필드안에 고객이 적은내용을 얻어오기
		String tel = tfCustTelSearch.getText();

		
		// 2. Model의 전화번호 검색메소드 selectByTel()  호출
		try {
			//메인에있는 selectByTel 를 모델아받아서 사용하는데
			//거기가 매겨변수 tel을 받으니 tel을 바로던져줌
			//그리고 생성자 함수를 사용해야하니 CustomerVO vo 를 선언하면서 바로 객체선언까지
			CustomerVO vo =	model.selectByTel(tel);
			//model.selectByTel(tel); - 이렇게 2개 나눠도 가능
			//CustomerVO vo = new CustomerVO();

			// 3. 2번의 넘겨받은 Customer의 각각의 값을 화면 텍스트 필드 지정
			tfCustName.setText(vo.getName());
			tfCustTel.setText(vo.getTel());
			tfCustTelAid.setText(vo.getAddtel());
			tfCustAddr.setText(vo.getAddr());
			tfCustEmail.setText(vo.getEmail());

		} catch (Exception e) {
			e.printStackTrace();
		}
		//CustomerDaoImpl 클래스로 돌아가서 selectByTel의 내용들을 채워줘야한다
	}//end of searchByTel();
	
	

	/*updateCustomer();
 	//회원의 정보를 수정하는 메소드
	//예를들어 번호검색해서 010-5164-2240 백찬주의 정보가 나오게한뒤에
	//그나온 정보에서 만약 출신지를 제주라고 바꾸고 수정을 누르면 sql에서 값이 수정되어 나온다
 */
	public void updateCustomer(){
		//수정을 위해 사용자가 입력한 값들을 다시 얻어온다
		//처음 회원가입처럼 고객 정보를 저장하기위헤

		String name = tfCustName.getText();
		String tel = tfCustTel.getText();
		String telA = tfCustTelAid.getText();
		String add = tfCustAddr.getText();
		String email = tfCustEmail.getText();

		CustomerVO vo = new CustomerVO();
		vo.setName(name);
		vo.setTel(tel);
		vo.setAddtel(telA);
		vo.setAddr(add);
		vo.setEmail(email);

		//2. 모델이용해 메인클래스에있는 updateCustomer 사용

		try {
			model.updateCustomer(vo);//메인클래스에 vo 받고있게에 vo던지기
			clearText();

		} catch (Exception e) {
			e.printStackTrace();
		} //end of try.catch
		
		//CustomerDaoImpl 클래스로 돌아가서 updateCustomer의 내용들을 채워줘야한다

	} //end of updateCustomer




//드라이버 로딩을 위한 connectDB 라고 알고있자
	public void connectDB(){

		try {
			//CustomerDaoImpl클래스에 CustomerDaoImpl생성자객체 호출
			model = new CustomerDaoImpl(); //드라이버로딩 불러온거임
		} catch (Exception e) {
			System.out.println("고객관리 드라이버 로딩 실패" + e.getMessage());
			e.printStackTrace();
		}
	} //end of connectDB


	public void addLayout(){

		tfCustName			= new JTextField(20);
		tfCustTel			= new JTextField(20);
		tfCustTelAid		= new JTextField(20);
		tfCustAddr			= new JTextField(20);
		tfCustEmail			= new JTextField(20);


		tfCustNameSearch	= new JTextField(20);
		tfCustTelSearch		= new JTextField(20);

		bCustRegist			= new JButton("회원가입");
		bCustModify			= new JButton("회원수정");
		bCustNameSearch		= new JButton("이름검색");
		bCustTelSearch		= new JButton("번호검색");

		// 회원가입 부분 붙이기 
		//		( 그 복잡하다던 GridBagLayout을 사용해서 복잡해 보임..다른 쉬운것으로...대치 가능 )
		JPanel			pRegist		= new JPanel();
		pRegist.setLayout( new GridBagLayout() );
		GridBagConstraints	cbc = new GridBagConstraints();
		cbc.weightx	= 1.0;
		cbc.weighty	 = 1.0;
		cbc.fill				= GridBagConstraints.BOTH;
		cbc.gridx	=	0;	 			cbc.gridy	=  0;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( new JLabel("	이	름	") ,	cbc );
		cbc.gridx	=	1;	 			cbc.gridy	=  0;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( tfCustName ,	cbc );
		cbc.gridx	=	2;	 			cbc.gridy	=  0;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( bCustModify,	cbc );
		cbc.gridx	=	3;	 			cbc.gridy	=  0;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( bCustRegist,	cbc );

		cbc.gridx	=	0;	 			cbc.gridy	=  1;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( new JLabel("	전	화	") ,	cbc );
		cbc.gridx	=	1;	 			cbc.gridy	=  1;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add(  tfCustTel ,	cbc );
		cbc.gridx	=	2;	 			cbc.gridy	=  1;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( new JLabel(" 추가전화  ") ,	cbc );
		cbc.gridx	=	3;	 			cbc.gridy	=  1;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( tfCustTelAid ,	cbc );

		cbc.gridx	=	0;	 			cbc.gridy	=  2;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( new JLabel("	주	소	") ,	cbc );
		cbc.gridx	=	1;	 			cbc.gridy	=  2;			cbc.gridwidth	=	3;			cbc.gridheight= 1;
		pRegist.add(  tfCustAddr  ,	cbc );

		cbc.gridx	=	0;	 			cbc.gridy	=  3;			cbc.gridwidth	=	1;			cbc.gridheight= 1;
		pRegist.add( new JLabel("	이메일	") ,	cbc );
		cbc.gridx	=	1;	 			cbc.gridy	=  3;			cbc.gridwidth	=	3;			cbc.gridheight= 1;
		pRegist.add( tfCustEmail ,	cbc );




		// 회원검색 부분 붙이기
		JPanel			pSearch		= new JPanel();
		pSearch.setLayout( new GridLayout(2, 1) );
		JPanel	pSearchName	= new JPanel();
		pSearchName.add(	new JLabel("		이 	름	"));
		pSearchName.add(	tfCustNameSearch );
		pSearchName.add(	bCustNameSearch );
		JPanel	pSearchTel	= new JPanel();
		pSearchTel.add(		new JLabel("	전화번호	"));
		pSearchTel.add(	tfCustTelSearch );
		pSearchTel.add(	bCustTelSearch );
		pSearch.add(	 pSearchName );
		pSearch.add( pSearchTel );

		// 전체 패널에 붙이기
		setLayout( new BorderLayout() );
		add("Center",		pRegist );
		add("South",		pSearch );

	}


}			 	


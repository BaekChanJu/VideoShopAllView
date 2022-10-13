package  view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import model.RentDao;
import model.dao.RentDaoImpl;



public class RentView extends JPanel 
{
	JTextField tfRentTel, tfRentCustName, tfRentVideoNum;
	JButton bRent;
	
	JTextField tfReturnVideoNum;
	JButton bReturn;
	
	JTable tableRecentList;
	
	RentTableModel rentTM;
	
	//인터페이스 명으로 해도 메인클래스와 연동이라 상관없음
	RentDao model; 

	
	//==============================================
	//	 생성자 함수
	
	public RentView(){
		addLayout();	//화면구성
		eventProc();  	
		connectDB();  //DB연결  / 이부분에 메인꺼가져오는 객체생성 해주기 꼭
		
		
		selectList(); //1013 생성자 함수에 하나 넣어줌
		//생성하자마자 보이게 하기위함임
		//그리고 자동인것처럼 보이기위해 매번 이벤트마다 해준다
		//대여하고 반납할때마다 뜨게 마치 자동인것처럼 그래서 이벤트헨들러 칸에 추가해줬다
	}
	
	
	
	// DB 연결 
		//connectDB 에는 model 객체 (메인꺼 쓸 수 있는) 를 생성해주자
		//그리고 생성자에 넘겨주자
	void connectDB(){
		
		try {
			model = new RentDaoImpl();
		} catch (Exception e) {
			System.out.println("대여관리 드라이브 로딩 실패 ");
			e.printStackTrace();
		}
	}
	
	
	
	/*	화면 구성   */
	void addLayout(){
		// 멤버변수 객체 생성
		tfRentTel = new JTextField(20);
		tfRentCustName = new JTextField(20);
		tfRentVideoNum = new JTextField(20);
		tfReturnVideoNum = new JTextField(10);
		
		bRent = new JButton("대여");
		bReturn = new JButton("반납");
		
		tableRecentList = new JTable();
		
		rentTM=new RentTableModel();
		tableRecentList = new JTable(rentTM);
		
		// ************* 화면구성 *****************
		// 화면의 윗쪽
		JPanel p_north = new JPanel();
		p_north.setLayout(new GridLayout(1,2));
		// 화면 윗쪽의 왼쪽
		JPanel p_north_1 = new JPanel();
		p_north_1.setBorder(new TitledBorder("대		여"));
		p_north_1.setLayout(new GridLayout(4,2));
		p_north_1.add(new JLabel("전 화 번 호"));
		p_north_1.add(tfRentTel);
		p_north_1.add(new JLabel("고 객 명"));
		p_north_1.add(tfRentCustName);
		p_north_1.add(new JLabel("비디오 번호"));
		p_north_1.add(tfRentVideoNum);
		p_north_1.add(bRent);
		
		
		
		// 화면 윗쪽의 오른쪽
		JPanel p_north_2 = new JPanel();	
		p_north_2.setBorder(new TitledBorder("반		납"));
		p_north_2.add(new JLabel("비디오 번호"));
		p_north_2.add(tfReturnVideoNum);
		p_north_2.add(bReturn);
		
		//
		setLayout(new BorderLayout());
		add(p_north, BorderLayout.NORTH);
		add(new JScrollPane(tableRecentList),BorderLayout.CENTER);
		
		
		p_north.add(p_north_1);
		p_north.add(p_north_2);
	}

	class RentTableModel extends AbstractTableModel { 
		  
		ArrayList data = new ArrayList();
		String [] columnNames = {"비디오번호","제목","고객명","전화번호","반납예정일","반납여부"};

		    public int getColumnCount() { 
		        return columnNames.length; 
		    } 
		     
		    public int getRowCount() { 
		        return data.size(); 
		    } 

		    public Object getValueAt(int row, int col) { 
				ArrayList temp = (ArrayList)data.get( row );
		        return temp.get( col ); 
		    }
		    
		    public String getColumnName(int col){
		    	return columnNames[col];
		    }
	}
	
	// 이벤트 등록
	public void eventProc(){
		ButtonEventHandler btnHandler = new ButtonEventHandler();
		
		tfRentTel.addActionListener(btnHandler);
		bRent.addActionListener(btnHandler);
		bReturn.addActionListener(btnHandler);
		
	
		tableRecentList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev){
				
				try{
					int row = tableRecentList.getSelectedRow();
					int col = 0;	// 검색한 열을 클릭했을 때 클릭한 열의 비디오번호
				
					Integer vNum = (Integer)(tableRecentList.getValueAt(row, col));
					// 그 열의 비디오번호를 tfReturnVideoNum 에 띄우기
					tfReturnVideoNum.setText(vNum.toString());
	
				}catch(Exception ex){
					System.out.println("실패 : "+ ex.getMessage());
				}
				
			}
		});
		                         
	}
	
	// 이벤트 핸들러
	class ButtonEventHandler implements ActionListener{
		public void actionPerformed(ActionEvent ev){
			Object o = ev.getSource();
			
			if(o==tfRentTel){  			// 전화번호 엔터 / 이름을 나오게 하면된다
				rentSelectTel();				
			}
			else if(o==bRent){  		// 대여 클릭
				rentClick();	
				selectList(); //대여하고 반납할때마다 뜨게 마치 자동인것처럼
			}
			else if(o==bReturn){  		// 반납 클릭
				returnClick();	
				selectList(); //대여하고 반납할때마다 뜨게 마치 자동인것처럼
			}
		}	
	} //end of ButtonEventHandler
	
	
	
	
	//3번 1013 returnClick()
	// 저장되어있던 비디오 번호를 입력하고 반납을누르면!
	// 반납키를 누르면 sql에 저장되었던 정보중 v_van이 N에서 반납했다라는 Y로 바뀌어야한다
	
	public void returnClick(){
		//수정을 위해 사용자가 입력한 값듷을 다시 얻어온다
		//tfReturnVideoNum 이게 반납에 비디오번호 넣는 텍스트라인의 변수명이다
		//고객이 그래서 비디오번호 반납칸에 넣은값을 겟으로 얻어오고 번호는 인트니 형변환해서 넣어줌
		int vnum =    Integer.parseInt(tfReturnVideoNum.getText());
		
		try {
			//그래서 모델쪽 함수에 위 변수를 던져줌
			model.returnVideo(vnum);
			clearText();
		} catch (Exception e) {
			System.out.println("반납처리오류 : " + e.getMessage());
			e.printStackTrace();
		}
		
		//여기 함수가 끝났으니 sql구문 완성하는함수로 넘어가자

	}//end of returnClick()
	
	//1번 1013 대여버튼이 눌렸을때의 함수임 rentDaolmpl 클래스의 rentVideo 와 연동된 함수
	//전화번호와 비디오번호를 치고 대여 버튼을 누르면 sql에 대여관련 정보가 저장됨
		//(전화번호 쓰고 엔터 누르면 고객이름 뜨게하는건 이후에 처리할꺼임)
	public void rentClick(){
			//고객이 적은 전화번호와 비디오번호를 얻어오기먼저 시작
			
		String tel = tfRentTel.getText();
		int vnum = Integer.parseInt(tfRentVideoNum.getText());
		
		//모델쪽꺼 가져와야하니 일단 모델(클래스) 선언해주기 위에서
		try {
			//위 변수 2개 날려주기 인터페이스에서 먼저 그렇게 설정을해놨음
			model.rentVideo(tel, vnum);
			clearText();
		} catch (Exception e) {
			System.out.println("대여 처리 오류 : " + e.getMessage());
			e.printStackTrace();
		}//end of try
		
	//여기가 다됬으면 sql처리 함수로 넘어가자
		
	} // end of rentClick();
	
	
	void clearText(){
		tfRentVideoNum.setText(null);
		tfRentCustName.setText(null);
		tfRentVideoNum.setText(null);
		tfRentTel.setText(null);
	} //clearText();
	
	
	
	
	//2번 rentSelectTel() 함수 1013
	// 전화번호입력후 엔터
		//전화번호란에 번호치고 엔터를 치면 일전에 저장되있던 고객이름이 튀어나온다
	public void rentSelectTel(){
		
		//tfRentTel 맨왼쪽위 전화번호 넣는 칸
			//전화번호 넣는칸에 고객전화번호를 얻어온다 그러고 변수에 담아준다
			//tel 인터페이스에서 String으로 받는다고 이미 인터페이스 만들어놨음
		String tel = tfRentTel.getText();

		
		try {
			//모델.rentSelectTel 함수에 tel을 던져주고 변수에 담아준다
			//그리고 고객명 란에 이름을 지정해줘야하니까 name이라는 변수에 담아주고
			//이름나오는칸의 변수이름이 tfRentCustName 이거인데 지정해줘야하니
			//tfRentCustName.setText(name); 셋으로 지정까지해준다
			
			String name = model.rentSelectTel(tel);
			tfRentCustName.setText(name);
		} catch (Exception e) {
			e.printStackTrace();
		}//end of try
		
		//여기가 끝났으면 sql처리 함수로 넘어가자
	} //end of  rentSelectTel
	
	
	
	
	
	
	//1013 selectList() 미납목록 뜨게 만드는 함수
		// tableRecentList에 고객의 정보를 배열느낌으로 출력하는데 반납여부로
		// 아직 반납이 안됬으면 정보와 반납여부에 n이라고 출력이되고
		//반납이 되면  tableRecentList란에서 사라지며 sql에는 Y로 바뀌어있게 만드는함수
	
		//그리고  selectList 함수는 대여하고 반납할때 자동처럼 칸에 실시간으로 띄우기위해
		//이벤트 헨들러에 클릭클릭 마다 넣어주면 마치 실시간처럼 활용이된다
		//그리고 생성하자마자 딱 보이게 하기위해 생성자에 넣어준다 RentView() 여기에
	void selectList() {
		
		try {
			//rentTM 란에 나와야하니까 담아준다
				//selectList 의 어레이 리스트를 rentTM.data에 심어주는거고
			rentTM.data = model.selectList();
			//위에꺼 신호를 전송해 줘야 뜬다 / 재깍재깍 바뀐 실시간 데이터 신호를
			rentTM.fireTableDataChanged();
			
			
		} catch (Exception e) {
			System.out.println("미납목록 검색실패");
			e.printStackTrace();
		}//end try
		
		//여기가 완성됬으면 sql 만드는 구문으로가고 배열형식으로 출력해야하니까 거긴
		//어레이 리스트겠지?
		
	}//end of  selectList
	
	
	
}//main class
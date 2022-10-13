package	 view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import model.VideoDao;
import model.dao.VideoDaoImpl;
import model.vo.VideoVO;


public class VideoView extends JPanel 
{	
	//	member field
	JTextField	tfVideoNum, tfVideoTitle, tfVideoDirector, tfVideoActor;
	JComboBox	comVideoJanre;
	JTextArea	taVideoContent;

	JCheckBox	cbMultiInsert;
	JTextField	tfInsertCount;

	JButton		bVideoInsert, bVideoModify, bVideoDelete;

	JComboBox	comVideoSearch;
	JTextField	tfVideoSearch;
	JTable		tableVideo;//눈에보이는 그냥 제일큰칸

	VideoTableModel tbModelVideo; // 화면에 테이블을 붙히기위한


	//비지니스 로직
	//인터페이스명 VideoDao 이걸로만들어줘도 사실은 VideoDaoImpl 이걸 쓰는것이다
	VideoDao model;


	//##############################################
	//	constructor method
	public VideoView(){
		addLayout(); 	// 화면설계
		initStyle();
		eventProc();
		connectDB();	// DB연결 / 이부분에 메인꺼가져오는 객체생성 해주기 꼭
	}

	public void connectDB(){	// DB연결
		try {
			//비지니스 로직은 VideoDao 지만 실제 객체는 VideoDaoImpl 로
			model = new VideoDaoImpl(); 
		} catch (Exception e) {
			System.out.println("드라이버 로딩 실패" + e.getMessage());
		}

	}

	public void eventProc(){
		// 체크박스가 눌렸을 때 tfInseftCount 가 쓸수있게됨
		cbMultiInsert.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				/*	if(cbMultiInsert.isSelected()){
					tfInsertCount.setEditable(true);
				}
				else
					tfInsertCount.setEditable(false);*/

				tfInsertCount.setEditable( cbMultiInsert.isSelected() );
			}						
		});	

		ButtonEventHandler btnHandler = new ButtonEventHandler();

		// 이벤트 등록
		bVideoInsert.addActionListener(btnHandler);
		bVideoModify.addActionListener(btnHandler);
		bVideoDelete.addActionListener(btnHandler);
		tfVideoSearch.addActionListener(btnHandler);
		// 검색한 열을 클릭했을 때


		//1013 - 비디오 번호 이제 출력한부분에서 그 열이나 행 클릭했을때
		//몇행의 몇열 이런식으로 ~
		//그 비디오 정보를 제목 배우 감독 설명 란에 자동으로 뜨게만들기 / 즉 비디오 정보가오기
		tableVideo.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev){

				try{
					int row = tableVideo.getSelectedRow();
					int col = 0;	// 검색한 열을 클릭했을 때 클릭한 열의 비디오번호
					// Object -> Integer -> int 형변환
					int vNum = ((Integer)tableVideo.getValueAt(row, col)).intValue();




					//selectBynum 함수 이제 VideoDaoImpl 클래스에서 만들어줘야함 
					//인터페이스랑 !
					VideoVO vo = model.selectBynum(vNum);
					//화면에 비디오정보의 값들을 각각출력해주자
					tfVideoNum.setText(String.valueOf(vo.getV_number()));
					//장르 변하게 하려면
					//comVideoJanre.setSelectedItem 가져오기
					//comVideoJanre.getSelectedItem() 지정하기
					comVideoJanre.setSelectedItem(vo.getV_type());
					tfVideoTitle.setText(vo.getV_name());
					tfVideoDirector.setText(vo.getV_director());
					tfVideoActor.setText(vo.getV_actor());
					taVideoContent.setText(vo.getV_exp());



				}catch(Exception ex){
					System.out.println("실패 : "+ ex.getMessage());
				}

			}
		});
	}		

	// 버튼 이벤트 핸들러 만들기
	class ButtonEventHandler implements ActionListener{
		public void actionPerformed(ActionEvent ev){
			Object o = ev.getSource();

			if(o==bVideoInsert){  
				registVideo();					// 비디오 등록
			}
			else if(o==bVideoModify){  
				modifyVideo();					// 비디오 정보 수정
			}
			else if(o==bVideoDelete){  
				deleteVideo();					// 비디오 정보 삭제
			}
			else if(o==tfVideoSearch){
				searchVideo();				// 비디오 검색
			}
		}
	}


	/*1번 - registVideo();
	  입고 클릭시 - 텍스트의 입력한 값이 비디오의 정보로 저장이된다!
	VideoDaoImpl 클래스의 insertVideo 함수는 이내용을 sql 부분으로 정리한것
	 */

	public void registVideo(){
		//1) 화면의 사용자 입력값 얻어오기
		//1-1 다른건 다 겟텍스트인데 콤보박스는 어캐 얻어오지? (장르 부분의 콤보박스)
		//comVideoJanre.getSelectedItem(); 함수를 사용하면 에러가남 이유를 보면
		//난 무슨형인지 몰라 오브젝트로 해놓을태니 너희가 알아서 형을 정해서 형변환해 사용해라 라고나옴
		//우리는 String 으로 선언했으니 (String)comVideoJanre.getSelectedItem(); 형변환후 변수에담자
		String v_type =(String)comVideoJanre.getSelectedItem();
		//1-2 나머지 얻어오기
		//나머지는 콤보박스 형식이 아니니 일반적으로 받아오자
		String v_name = tfVideoTitle.getText();
		String v_director = tfVideoDirector.getText();
		String v_actor = tfVideoActor.getText();
		String v_exp = taVideoContent.getText();
		//1-3 겟으로 얻어오면 무저건 스트링으로 들어오기때문에 카운트는 int니 형변환 해서사용
		//다중입고 버튼의 수량때문에 count가 필요한것
		int count =Integer.parseInt(tfInsertCount.getText()); 


		//2) 1번의 값들을 VideoVO 에 지정해줘야함
		VideoVO vo = new VideoVO();
		//넘버는 입력칸이없어 안받아옴
		vo.setV_type(v_type);
		vo.setV_name(v_name);
		vo.setV_director(v_director);
		vo.setV_actor(v_actor);
		vo.setV_exp(v_exp);


		//3) 모델에있는 insertVideo() 호출
		try {
			model.insertVideo(vo, count); // insertVideo 함수가 매개변수로 vo,count를 받음
		} catch (Exception e) {
			System.out.println("비디오 입고 실패 : " + e.getMessage());

		}

		//4)화면지우기
		clearText();

		//끝났으면  VideoDaoImpl 클래스의 insertVideo함수 로 가서 sql 구문 정리해주자

	} // end of registVideo

	void clearText(){
		tfVideoTitle.setText(null);
		tfVideoDirector.setText(null);
		tfVideoActor.setText(null);
		taVideoContent.setText(null);
	} //clearText();





	public void initStyle(){   
		tfVideoNum.setEditable(false); // 입력하지 못하게 만듬.
		tfInsertCount.setEditable(false);

		tfInsertCount.setHorizontalAlignment(JTextField.RIGHT);
	} // end of initStyle()



	//1013일 modifyVideo();
	//고객이 저장되어 출력된 입력값에서 무언가 수정하고 수정하기 눌렀을때
	//예시로 제목을 바꾸자
	// 수정 클릭시 - 비디오 정보 수정
	public void modifyVideo(){
		//설명 - 비디오 검색한에 비디오 이름을 쳐서 아래 큰칸에 나오게 되면
			//그 칸들중 아무거나 누르면 왼쪽 비디오정보입력칸에 비디오의 모든정보가 나오게된다
			//거기서 아무거나 입력값을 바꾸고 수정을 누르면 sql의 내용이 수정이 되게된다
		
		//수정을 위해서는 일단 사용자가 입력한 값들을 다시 다 얻어오기부터 시작해야한다
		
		
		//v_type 은 장르 장르가 박스눌러서 여러가지가 있기때문에 
			//comVideoJanre.getSelectedItem(); 함수를 사용해줘야하고 겟은 스트링만 받아오기에
			//string으로 형변환 까지 해줘야한다
		String v_type =(String)comVideoJanre.getSelectedItem();
		//비디오 번호가 int 형으로 선언되었기에 파스인트로 형변환이 필요하다
		int v_num =Integer.parseInt(tfVideoNum.getText()) ;
		String v_name = tfVideoTitle.getText();
		String v_director = tfVideoDirector.getText();
		String v_actor = tfVideoActor.getText();
		String v_exp = taVideoContent.getText();
		
		//전체 정보의 이동을 위해 vo 객체를 선언한다
		//vo 객체를 선언함으로써 VideoVO에 있는 겟세터 생성자등 사용이 자유로워진다
		VideoVO vo = new VideoVO(); 
		
		//말그대로 vo.지정해준다 V_type 에  v_type아까 위에서 얻어온값을
		vo.setV_type(v_type);
		vo.setV_name(v_name);
		vo.setV_director(v_director);
		vo.setV_actor(v_actor);
		vo.setV_exp(v_exp);
		vo.setV_number(v_num);

		//모델을 이용해 VideoDaoImpl 클래스의 함수를 사용할수있게 해줘야함
		try {
			//정보덩어리 vo를 당연히 매개변수로 던져준다
			model.modifyVideo(vo);
			clearText();
		} catch (Exception e) {
			e.printStackTrace();
			
			//완성이 됬으면 sql구문 만드는 함수로 넘어가자
		}//end of try.catch
	} // end of modifyVideo


	//1013일 deleteVideo()
	//비디오 정보 다 뜨게해놓고 삭제 누르면
		//해당비디오 정보가 없어지게 만드는 역활
		// 삭제 클릭시 - 비디오 정보 삭제
	//하지만 지금 조인으로 대여관리를 다만들어 놓은상황에서는 제약조건위반으로 삭제는 되지않는다 데이터가 
	//남아있기때문
	public void deleteVideo(){
		//삭제 프라이 머리키인  vNum 번호로 삭제
			//비디오 번호 기준으로 삭제해야하니 비디오 번호를 얻어오자

			//비디오 번호 얻어오려고 위에 선생님이 만들어 놓으신 구문 그냥 복붙해옴
			//비디오 번호는 쓰는칸이없어서 겟텍스트로 대려오는게 아니라 아레처럼 대려옴
		try{
			int row = tableVideo.getSelectedRow();
			int col = 0;	// 검색한 열을 클릭했을 때 클릭한 열의 비디오번호
			// Object -> Integer -> int 형변환
			int vNum = ((Integer)tableVideo.getValueAt(row, col)).intValue();
			
			model.deleteVideo(vNum);
			clearText();
		}catch(Exception e) {
			System.out.println("비디오삭제실패" + e.getMessage());
			e.printStackTrace();
		}
			
		//deleteVideo 함수 구문 완료 sql 문장 함수 만들러가기

	} //end of deleteVideo



	// searchVideo() 함수 - 말그대로 비디오를 검색한다
	//비디오 검색 란에 그냥 아무것도 입력하지않고
	//엔터를 치면 저장된 비디오정보가 쭉 출력되게 만든다
	//그런데 이제 내가 저장한 제목을 검색하면 딱 그값들이 나오게 만드는것도 1013일에 추가했다
	//VideoDaoImpl 클래스의 selectVideo() 함수는 이걸 이용한 sql정리구문함수이다
	public void searchVideo(){
		try {
			//비디오검색에는 2가지 버튼이있다 열어보면 제목과 감독 인덱스 순서로보면 0,1 번 순서이다
			//제목은 0번이고 감독은1번인 셈이다 
			//그래서 인덱스를 확인해서 플레이해주는 int idx  를 만들어준다 
			int idx =	comVideoSearch.getSelectedIndex();
			
			//비디오검색에 고객의 글자를 얻어오기위해 // tfVideoSearch.getText(); 도 해준다
			String word = 	tfVideoSearch.getText(); 

			
			//위 두변수 idx, word 를 당연히 매개변수로 넘겨줘야겠죠?
			//인터페이스에도 매개변수로 idx, word 를 받고있습니다!
			//model.selectVideo(idx, word); 이렇게 넘겨주면된다
			//sql구문 함수에도 동일하게 매개변수로 2개를 넣어줘야함
			//tbModelVideo.data 변수를 선언한 이유는 그 나온 비디오 정보들을 데이타화 해서
			//tbModelVideo 가장큰 테이블에 나오게 하기위해서이다 (어레이리스트 data인느낌)
			tbModelVideo.data =  model.selectVideo(idx, word);
			//데이터가 실시간으로 변동되는 신호를 전송하기위해 아래코드가 필요하다라고 기억
			tbModelVideo.fireTableDataChanged(); 
		}catch(Exception ex) {
			System.out.println("검색실패 :" +ex.getMessage());

		}//end try.catch
		
		//완성되었으면 sql구문을 만드는 함수로 넘어가보자

	} //end of searchVideo()


	//  화면설계 메소드
	public void addLayout(){
		//멤버변수의 객체 생성
		tfVideoNum = new JTextField();
		tfVideoTitle = new JTextField();
		tfVideoDirector = new JTextField();
		tfVideoActor = new JTextField();

		String []cbJanreStr = {"멜로","엑션","스릴","코미디"};
		comVideoJanre = new JComboBox(cbJanreStr);
		taVideoContent = new JTextArea();

		cbMultiInsert = new JCheckBox("다중입고");
		tfInsertCount = new JTextField("1",5);

		bVideoInsert = new JButton("입고");
		bVideoModify = new JButton("수정");
		bVideoDelete = new JButton("삭제");

		String []cbVideoSearch = {"제목","감독"};
		comVideoSearch = new JComboBox(cbVideoSearch);
		tfVideoSearch = new JTextField(15);

		tbModelVideo = new VideoTableModel();
		tableVideo = new JTable(tbModelVideo);
		// tableVideo.setModel(tbModelVideo);



		//************화면구성************
		//왼쪽영역
		JPanel p_west = new JPanel();
		p_west.setLayout(new BorderLayout());
		// 왼쪽 가운데
		JPanel p_west_center = new JPanel();	
		p_west_center.setLayout(new BorderLayout());
		// 왼쪽 가운데의 윗쪽
		JPanel p_west_center_north = new JPanel();
		p_west_center_north.setLayout(new GridLayout(5,2));
		p_west_center_north.add(new JLabel("비디오번호"));
		p_west_center_north.add(tfVideoNum);
		p_west_center_north.add(new JLabel("장르"));
		p_west_center_north.add(comVideoJanre);
		p_west_center_north.add(new JLabel("제목"));
		p_west_center_north.add(tfVideoTitle);
		p_west_center_north.add(new JLabel("감독"));
		p_west_center_north.add(tfVideoDirector);
		p_west_center_north.add(new JLabel("배우"));
		p_west_center_north.add(tfVideoActor);

		// 왼쪽 가운데의 가운데
		JPanel p_west_center_center = new JPanel();
		p_west_center_center.setLayout(new BorderLayout());
		// BorderLayout은 영역 설정도 해야함
		p_west_center_center.add(new JLabel("설명"),BorderLayout.WEST);
		p_west_center_center.add(taVideoContent,BorderLayout.CENTER);

		// 왼쪽 화면에 붙이기
		p_west_center.add(p_west_center_north,BorderLayout.NORTH);
		p_west_center.add(p_west_center_center,BorderLayout.CENTER);
		p_west_center.setBorder(new TitledBorder("비디오 정보입력"));

		// 왼쪽 아래
		JPanel p_west_south = new JPanel();		
		p_west_south.setLayout(new GridLayout(2,1));

		JPanel p_west_south_1 = new JPanel();
		p_west_south_1.setLayout(new FlowLayout());
		p_west_south_1.add(cbMultiInsert);
		p_west_south_1.add(tfInsertCount);
		p_west_south_1.add(new JLabel("개"));
		p_west_south_1.setBorder(new TitledBorder("다중입력시 선택하시오"));
		// 입력 수정 삭제 버튼 붙이기
		JPanel p_west_south_2 = new JPanel();
		p_west_south_2.setLayout(new GridLayout(1,3));
		p_west_south_2.add(bVideoInsert);
		p_west_south_2.add(bVideoModify);
		p_west_south_2.add(bVideoDelete);

		p_west_south.add(p_west_south_1);
		p_west_south.add(p_west_south_2);

		p_west.add(p_west_center,BorderLayout.CENTER);
		p_west.add(p_west_south, BorderLayout.SOUTH);   // 왼쪽부분완성

		//---------------------------------------------------------------------
		// 화면구성 - 오른쪽영역
		JPanel p_east = new JPanel();
		p_east.setLayout(new BorderLayout());

		JPanel p_east_north = new JPanel();
		p_east_north.add(comVideoSearch);
		p_east_north.add(tfVideoSearch);
		p_east_north.setBorder(new TitledBorder("비디오 검색"));

		p_east.add(p_east_north,BorderLayout.NORTH);
		p_east.add(new JScrollPane(tableVideo),BorderLayout.CENTER);
		// 테이블을 붙일때에는 반드시 JScrollPane() 이렇게 해야함 


		// 전체 화면에 왼쪽 오른쪽 붙이기
		setLayout(new GridLayout(1,2));

		add(p_west);
		add(p_east);

	}

	//화면에 테이블 붙이는 메소드 
	class VideoTableModel extends AbstractTableModel { 

		ArrayList data = new ArrayList();
		String [] columnNames = {"비디오번호","제목","감독","배우"};

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
}



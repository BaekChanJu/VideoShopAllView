

//여기가 매인함수를 가지고있음
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import view.*;

public class VideoShop extends JFrame 
{
	CustomerView	customer;
	VideoView			video;
	RentView			rent;

	public VideoShop(){
		//각각의 화면을 관리하는 클래스 객체 생성
			customer = new CustomerView(); //뷰 패키지에 다있음 아래3개
			video		= new VideoView();
			rent			= new RentView();

			JTabbedPane  pane = new JTabbedPane();
			pane.addTab("고객관리", customer );
			pane.addTab("비디오관리", video);
			pane.addTab("대여관리", rent );

			pane.setSelectedIndex(2);

			// 화면크기지정
			add("Center", pane );
			setSize(800,600);
			setVisible( true );

			setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );	
	}
	public static void main(String[] args) 
	{
			new VideoShop(); //생성자함수로 위로 올라감
	}
}

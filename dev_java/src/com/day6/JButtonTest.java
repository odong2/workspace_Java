package com.day6;

import javax.swing.JButton;
import javax.swing.JFrame;

public class JButtonTest {
	JFrame jf = new JFrame();
	// 선언과 생성을 나누어 처리하는 경우에는 객체 생성에 대한
	// 시점에 문제가 발생할 수 있다
	// 선언이 먼저. 사용이 나중이다
	// 선언만 되어 있어도 문법 에러는 발생하지 않는다.
	// 그러나 new 예약어를 통해서 실제 클래스가 메모리에 로딩이
	// 안되어 있다면 NullPointerException과 같은 런타임 에러가 발생할 수 있다.
	JButton jbtn_north = null;
	JButton jbtn_center = new JButton("중앙");
	
	public void initDisplay() {
		// 선언부와 생성부를 나누어서 인스턴스화 할 수 있다.
		jbtn_north = new JButton("조회");
		jf.add("North",jbtn_north); //  add의 인자값 ( 위치, JButton객체 ) / jf.add 화면에 버튼을 추가하느 JFrame의 메서드
		jf.add("Center", jbtn_center);
		jf.setSize(500, 400); // JFrame 창 크기 설정
		jf.setVisible(true); //JFrame 창 열기 
	}
	
	public static void main(String[] args) {
		JButtonTest jbt = new JButtonTest();
		jbt.initDisplay();
	}
	
}

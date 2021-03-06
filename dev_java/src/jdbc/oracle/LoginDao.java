package jdbc.oracle;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
// 회원가입 중복체크 예시
public class LoginDao extends JFrame implements ActionListener	{
	JPanel 	 jp_north  = new JPanel();
	JTextField jtf_id  = new JTextField("", 10);
	JButton jbtn_check = new JButton("ID중복검사");
	JButton jbtn_join = new JButton("회원가입");
	boolean isIDCheck = false;
	public LoginDao(){
		jbtn_check.addActionListener(this);
		// 화면을 그리는 메소드 호출
		//initDisplay();
		
	}
	
	public void initDisplay() {
		
		jp_north.setLayout(new BorderLayout());
		jp_north.add("Center", jtf_id);
		jp_north.add("East",jbtn_check);
		this.add("North",jp_north);
		jbtn_join.setEnabled(false); // 버튼 얼려놓기(회원가입)
		this.add("South",jbtn_join);
		this.setSize(500,300);
		this.setVisible(true);
		
	}
	/***********************************************************************************************
	 * 
	 * @param mem_id - 사용자가 입력한 아이디 받아오기
	 * @param mem_pw - 사용자가 입력한 비번 받아오기
	 * @return
	 */
	public String login(String mem_id, String mem_pw) {
		String mem_name= null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String result = "";
		StringBuilder sql = new StringBuilder(); // 아이디 중복검사시 String x StringBuileder사용하기
		sql.append("SELECT                                        ");
        sql.append("RESULT                                        ");
        sql.append("FROM (                                        ");
        sql.append("	SELECT                                    ");
        sql.append("		CASE WHEN MEM_ID =? THEN              ");
        sql.append("         CASE WHEN MEM_PW =? THEN MEM_NAME    ");
        sql.append("         	ELSE '0'                          ");
        sql.append("         END                                  ");
        sql.append("        ELSE '-1'                             ");
        sql.append("        END AS RESULT                         ");
        sql.append("       FROM MEMBER                            ");
        sql.append("      ORDER BY RESULT DESC                    ");
        sql.append("	)                                         ");
        sql.append("WHERE ROWNUM = 1                              ");
		DBConnectionMgr dbMgr = new DBConnectionMgr();
		try {
			con = dbMgr.getConnection();
			// ? 자리에 들어갈 아이디를 설정해야함
			pstmt = con.prepareStatement(sql.toString()); // sql stringBuilder를 사용 하였으므로 sql.toString()으로 읽어온다
			pstmt.setString(1, mem_id);
			pstmt.setString(2, mem_pw);
			// select처리시는 excuteQuery()호출
			// insert, update, delete 처리시 executeUpdate()호출
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getString("result");
			}
			System.out.println("login메소드 호출됨"); // 단위테스트
		} catch (Exception e) {
			System.out.println(e.toString());
		} dbMgr.freeConnection(rs,pstmt,con);
		
		return result;                                       
	}
	
	
	/*********************************************************************************************************
	 * 아이디 중복체크
	 * @param mem_id - 사용자가 입력한 아이디
	 * @return 1: 아이디 존재함, 0 : 아이디 사용가능함, -1:디폴트 -end of file
	 */
	public int idCheck(String mem_id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		StringBuilder sql = new StringBuilder(); // 아이디 중복검사시 String x StringBuileder사용하기
		sql.append("SELECT NVL((                             ");
		sql.append("		 SELECT 1                        ");
		sql.append("		 FROM DUAL                       ");
		sql.append("		 WHERE EXISTS ( SElECT mem_name  ");
		sql.append("		             FROM member         ");
		sql.append("		        WHERE mem_id = ?) ");
		sql.append("		         ),0)                    ");
		sql.append("FROM DUAL                                ");
		DBConnectionMgr dbMgr = new DBConnectionMgr();
		try {
			con = dbMgr.getConnection();
			// ? 자리에 들어갈 아이디를 설정해야함
			pstmt = con.prepareStatement(sql.toString()); // sql stringBuilder를 사용 하였으므로 sql.toString()으로 읽어온다
			pstmt.setString(1, mem_id);
			// select처리시는 excuteQuery()호출
			// insert, update, delete 처리시 executeUpdate()호출
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt(1);
			}
			System.out.println("result: " + result);
		} catch (Exception e) {
			System.out.println(e.toString());
		} dbMgr.freeConnection(rs,pstmt,con);
		
		return result;                                       
	}
	
	
	
	public static void main(String[] args) {
		LoginDao loginDao = new LoginDao();
		loginDao.initDisplay();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// ID 중복체크 버튼 눌린거야?
		if(obj == jbtn_check) {
			System.out.println("id중복체크 호출 성공");
			// 사용자가 입력한 아이디를 가져와 담기
			String user_id = jtf_id.getText();
			int result = idCheck(user_id);
			//입력한 아이디가 존재하는거야?
			if(result == 1) {
				// 입력한 아이디는 사용 못해
				// 다시 입력해야 된다.
				jtf_id.setText(""); // 다시 입력하도록 초기화
				JOptionPane.showMessageDialog(this
						, "입력한 아이디는 사용할 수 없습니다"
						, "EEROR", JOptionPane.ERROR_MESSAGE);
				return; // 함수를 빠져 나가는 용도로 return사용
				
			}// 입력한 아이디가 없는데?
			else if(result==0) {
				//입력한 아이디를 사용할 수 있다
				JOptionPane.showMessageDialog(this
						, "입력한 아이디는 사용할 수 있습니다"
						, "INFO", JOptionPane.INFORMATION_MESSAGE);
				jtf_id.setEditable(false); // 얼려버림
				jbtn_check.setEnabled(false);//버튼을 쓰지 못하게 얼려버린다
				isIDCheck = true;
				jbtn_join.setEnabled(isIDCheck);
			}
			
		}
		
	}

}

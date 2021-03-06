package jdbc.oracle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class EmpSumList extends JFrame implements ActionListener{
	JButton jbtn_select = new JButton("조회");
	String headers[] = {"부서명","CLERK","MANAGER","ETC","DEPT_SAL"};
	String data[][] = new String[0][5]; // 앞에 0 은 header 뒤에는 컬럼 5개
	DefaultTableModel dtm = new DefaultTableModel(data, headers); // 테이블의 셋
	JTable jtb = new JTable(dtm); 
	JScrollPane jsp = new JScrollPane(jtb); // JScrollPane 스크롤바 있는 속지
	// 물리적으로 떨어져 있는 오라클 서버에 연결통로 확보
	Connection con = null;
	// 개발자가 작성한 select문 전달하고 오라클 서버에 처리 요청
	PreparedStatement pstmt = null;
	// 오라클 서버에서 조회한 결과를 반환해 주면 커서 조작하기
	ResultSet rs = null;
	DBConnectionMgr dbMgr = new DBConnectionMgr();
	
	public EmpSumList() {
		jbtn_select.addActionListener(this);
		initDisplay();
	}
	
	public List<Map<String,Object>> getEmpSumList(){
		List<Map<String,Object>>list = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT                                                     ");
        sql.append("DECODE(B.RNO,'1',A.DNAME,'총계')                           ");
        sql.append(",SUM(CLERK) CLERK                                          ");
        sql.append(",SUM(MANAGER) MANAGER                                      ");
        sql.append(",SUM(ETC) ETC                                              ");
        sql.append(",SUM(DEPT_SAL) DETP_SAL                                    ");
        sql.append("FROM (                                                     ");
        sql.append("SELECT  DEPT.DNAME                                         ");
        sql.append("   , SUM(DECODE(JOB,'CLERK', SAL)) CLERK                   ");
        sql.append("   , SUM(DECODE(JOB,'MANAGER', SAL)) MANAGER               ");
        sql.append("   , SUM(DECODE(JOB,'CLERK',NULL,'MANAGER',NULL,SAL)) ETC  ");
        sql.append("   ,SUM(SAL) DEPT_SAL                                      ");
        sql.append("             FROM EMP, DEPT                                ");
        sql.append("         WHERE EMP.DEPTNO = DEPT.DEPTNO                    ");
        sql.append("    GROUP BY DEPT.DNAME                                    ");
        sql.append("            )A,                                            ");
        sql.append("               (                                           ");
        sql.append("SELECT ROWNUM RNO FROM DEPT                                ");
        sql.append("    WHERE ROWNUM < 3                                       ");
        sql.append(")B                                                         ");
        sql.append("GROUP BY DECODE(B.RNO,'1',A.DNAME,'총계')                  ");
        sql.append("ORDER BY DECODE(B.RNO,'1',A.DNAME,'총계')                  ");
		
		try {
			con = dbMgr.getConnection();
			pstmt=con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			Map<String, Object> rmap = null;
			while(rs.next()) {
				rmap = new HashMap<>();
				list.add(rmap);
				rmap.put("dname", rs.getString(1));
				rmap.put("clerk", rs.getDouble(2));
				rmap.put("manager", rs.getDouble(3));
				rmap.put("etc", rs.getDouble(4));
				rmap.put("dept_sal", rs.getDouble(5));				
			}
			System.out.println(list); // List, Map은 toString 오버라이딩 되어있다. 그러므로 주소 번지가 아닌 값이 나온다.
		} catch (Exception e) {
			System.out.println(e.toString()); // Exception 이름 출력. 어떤 예외인지 파악하기 위해서
		}
		
		return list;
	}
	
	public void initDisplay() {
		System.out.println("iniDisplay 호출 성공");
		this.add("North", jbtn_select);
		this.add("Center",jsp); // JScrollPane 스크롤바 속지 붙임
		this.setSize(500,400);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new EmpSumList();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 이벤트가 감지된 컴포넌트의 주소번지
		Object obj = e.getSource();
		if(jbtn_select == obj) {
			System.out.println("조회 버튼 누른 거야!");
			getEmpSumList();
		}
		
		
		
		
	}

}

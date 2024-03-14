package com.ssafy.board.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ssafy.board.model.BoardDto;
import com.ssafy.util.DBUtil;

/*
 * JDBC 작업 순서
 * 1. Driver Loading
 * 2. DB 연결 (Connection 생성)
 * 3. SQL 실행 준비
 * 	3-1. SQL 작성
 *  3-2. PreparedStatement 생성(위치 홀더 값 셋팅) 							******
 * 4. SQL 실행
 *  4-1. Insert, Update, Delete											******
 *  	pstmt.executeUpdate(sql문); ===> return 값이 int
 *  4-2. Search
 *  	pstmt.executeQuery() ===> return 값이 ResultSet
 * 5. DB 연결 종료: 연결 역순으로 종료 권장 (finally, AutoCloseable, try-with-resource 등 활용)
 */
public class BoardDaoImpl implements BoardDao {
	
	private final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String URL = "jdbc:mysql://localhost:3306/ssafydb?serverTimezone=UTC";
	// 중요! ?앞에 찾아가려는 스키마를 입력해야함 - ssafydb
	private final String DB_ID = "ssafy";
	private final String DB_PWD = "ssafy";
	
	
	
	// 1-1. 드라이버 로딩 - 생성자 속에 구현 (빌드 패스 꼭 확인)
	// 연결 전 반드시 한번은 로딩되어야 함
	// JDK 버전에 따라서 자동 로딩되는 경우도 있고, 직접 로딩해야하는 경우도 있음
	// 생성자에서 DB 연결까지 안하는 이유는 연결 효율성 때문, 옛 스마트폰의 Nate 버튼을 생각해보자
	private BoardDaoImpl() {
		try {
			Class.forName(DRIVER);
			// System.out.println("드라이버 로딩 성공");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 1-2. 객체 생성 (생성자에 의해서 드라이버 로딩)
	private static BoardDao boardDao = new BoardDaoImpl();
	public static BoardDao getBoardDao() {
		return boardDao;
	}

	@Override
	public void registerArticle(BoardDto boardDto) {
		Connection conn = null;
		PreparedStatement pstmt = null;
//		TODO : boardDto의 내용을 board table에 insert 하세요!!!
		try {
			// 2. DB 연결 - Connection 생성
			conn = DriverManager.getConnection(URL, DB_ID, DB_PWD);
			// System.out.println("DB 연결 성공");
			
			// 3-1. SQL문 작성
			StringBuilder sql = new StringBuilder("insert into board(subject, content, user_id) \n");
			sql.append("values (?, ?, ?)");
			// ?: 위치 홀더
			
			// 3-2. SQL문 PreparedStatement에 세팅
			pstmt = conn.prepareStatement(sql.toString());
			// 위치 홀더에 값 셋팅하기, setString(index, object)에서 index와 위치 홀더랑 대응
			int i = 0;
			pstmt.setString(++i, boardDto.getSubject());
			pstmt.setString(++i, boardDto.getContent());
			pstmt.setString(++i, boardDto.getUserId());
			
			// 4. SQL문 실행, cnt에는 몇 개의 결과가 되었는지 확인, mySQL에서 SQL문 실행시 1 row(s) affected에서 1을 의미
			int cnt = pstmt.executeUpdate();
			// 성공하면 1, 실패하면 0
			System.out.println(cnt + "개 등록 성공!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 5. Connection 끊기
			// - 하나씩 모두 끊기
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
//		END
	}

	@Override
	public List<BoardDto> searchListAll() {
		List<BoardDto> list = new ArrayList<BoardDto>();
//		TODO : board table의 모든 글정보를 글번호순으로 정렬하여 list에 담고 return 하세요!!!
		
		// 밖에 선언되어 있어야 finally에서 닫을 수 있다.
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getInstance().getConnection();
			String sql = "select * from board";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			// 받아온 데이터의 처음으로 이동했다가 옆으로 계속 이동하며 데이터를 출력
			// 이때 get 함수에 들어가는 것은 컬럼 명이며, alias 설정했으면 alias를 작성해줘야 한다.
			// 따라서 mySQL의 출력 테이블의 컬럼 명을 넣어야 한다.
			while(rs.next()) {
				BoardDto boardDto = new BoardDto();
				boardDto.setArticleNo(rs.getInt("article_no"));
				boardDto.setSubject(rs.getString("subject"));
				boardDto.setContent(rs.getString("content"));
				boardDto.setUserId(rs.getString("user_id"));
				boardDto.setRegisterTime(rs.getString("register_time"));
				list.add(boardDto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 앞에 있는 객체부터 닫는다.
			DBUtil.getInstance().close(rs, pstmt, conn);
		}
//		END
		return list;
	}

	@Override
	public List<BoardDto> searchListBySubject(String subject) {
		List<BoardDto> list = new ArrayList<BoardDto>();
//		TODO : board table에서 제목에 subject를 포함하고 있는 글정보를 list에 담고 return 하세요!!!
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getInstance().getConnection();
			
			StringBuilder sql = new StringBuilder("select * from board \n");
			sql.append("where subject = ");
			sql.append("'" + subject + "'");
			
//			System.out.println(sql);
			
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				BoardDto boardDto = new BoardDto();
				boardDto.setArticleNo(rs.getInt("article_no"));
				boardDto.setSubject(rs.getString("subject"));
				boardDto.setContent(rs.getString("content"));
				boardDto.setUserId(rs.getString("user_id"));
				boardDto.setRegisterTime(rs.getString("register_time"));
				list.add(boardDto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.getInstance().close(rs, pstmt, conn);
		}
		
//		END
		return list;
	}

	@Override
	public BoardDto viewArticle(int no) {
		BoardDto boardDto = null;
//		TODO : board table에서 글번호가 no인 글 한개를 return 하세요!!!
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBUtil.getInstance().getConnection();
			
			StringBuilder sql = new StringBuilder("select * from board \n");
			sql.append("where article_no = ");
			sql.append("'" + no + "'");
			
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			rs.next();
			boardDto = new BoardDto();
			boardDto.setArticleNo(rs.getInt("article_no"));
			boardDto.setSubject(rs.getString("subject"));
			boardDto.setContent(rs.getString("content"));
			boardDto.setUserId(rs.getString("user_id"));
			boardDto.setRegisterTime(rs.getString("register_time"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.getInstance().close(rs, pstmt, conn);
		}
//		END
		return boardDto;
	}

	@Override
	public void modifyArticle(BoardDto boardDto) {
		Connection conn = null;
		PreparedStatement pstmt = null;
//		TODO : boardDto의 내용을 이용하여 글번호에 해당하는 글제목과 내용을 수정하세요!!!
		try {
			conn = DBUtil.getInstance().getConnection();
			
			StringBuilder sql = new StringBuilder("update board\n");
			sql.append("set subject = " + "'" + boardDto.getSubject() + "'," + "content = " + "'" + boardDto.getContent() + "'\n");
			sql.append("where article_no = " + "'" + boardDto.getArticleNo() + "'");
			
			pstmt = conn.prepareStatement(sql.toString());
			int cnt = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.getInstance().close(pstmt, conn);
		}
//		END
	}

	@Override
	public void deleteArticle(int no) {
		Connection conn = null;
		PreparedStatement pstmt = null;
//		TODO : board table에서 글번호가 no인 글 정보를 삭제하세요!!!
		try {
			conn = DBUtil.getInstance().getConnection();
			
			StringBuilder sql = new StringBuilder("delete from board\n");
			sql.append("where article_no = " + "'" + no + "'");
			
			pstmt = conn.prepareStatement(sql.toString());
			
			int cnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.getInstance().close(pstmt, conn);
		}
//		END
	}

}

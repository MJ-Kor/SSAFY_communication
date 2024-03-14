package com.ssafy.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionTest {
	
	private final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String URL = "jdbc:mysql://localhost:3306/dbtest?serverTimezone=UTC";
	private final String DB_ID = "ssafy";
	private final String DB_PWD = "ssafy";
	
	public ConnectionTest() {
		try {
			Class.forName(DRIVER);
			System.out.println("드라이버 로딩 성공 !!!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// ConnectionTest()를 주석처리해도 DB 연결이 되는 이유
	// JDBC 1.6 이상 버전은 드라이브 로딩을 자동으로 해주기 때문이다.
	
	public static void main(String[] args) {
		ConnectionTest ct = new ConnectionTest();
		ct.connect();
	}

	private void connect() {
		try {
			Connection conn = DriverManager.getConnection(URL, DB_ID, DB_PWD);
			System.out.println("DB 연결 성공 !!!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

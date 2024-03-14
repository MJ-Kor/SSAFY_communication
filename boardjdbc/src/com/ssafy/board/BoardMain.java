package com.ssafy.board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.ssafy.board.model.BoardDto;
import com.ssafy.board.model.service.BoardServiceImpl;

public class BoardMain {

	BufferedReader in;

	public BoardMain() {
		in = new BufferedReader(new InputStreamReader(System.in));
		menu();
	}

	private void menu() {
		while (true) {
			System.out.println("---------- 게시판 메뉴 ----------");
			System.out.println("1. 글등록");
			System.out.println("2. 글목록(전체)");
			System.out.println("3. 글검색(제목)");
			System.out.println("4. 글보기");
			System.out.println("5. 글수정");
			System.out.println("6. 글삭제");
			System.out.println("-------------------------------------");
			System.out.println("0. 프로그램 종료");
			System.out.println("-------------------------------------");
			System.out.print("메뉴 선택 : ");
			try {
				int num = Integer.parseInt(in.readLine());
				switch (num) {
				case 1:
					registerArticle();
					break;
				case 2:
					searchListAll();
					break;
				case 3:
					searchListBySubject();
					break;
				case 4:
					viewArticle();
					break;
				case 5:
					modifyArticle();
					break;
				case 6:
					deleteArticle();
					break;
				default:
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void registerArticle() throws IOException {
		BoardDto boardDto = new BoardDto();
		System.out.println("=== 글 등록 ===");
		System.out.print("제목 : ");
		boardDto.setSubject(in.readLine());
		System.out.print("내용 : ");
		boardDto.setContent(in.readLine());
		boardDto.setUserId("ssafy");
//		TODO : Service의 registerArticle을 호출하세요!!!
		BoardServiceImpl.getBoardService().registerArticle(boardDto);
//		END
	}

	private void searchListAll() {
//		TODO : Service의 searchListAll을 호출하세요!!!
		List<BoardDto> list = BoardServiceImpl.getBoardService().searchListAll();
//		END
		System.out.println("********** 글목록(전체) **********");
		for(BoardDto article : list) {
			System.out.println(article);
		}
	}

	private void searchListBySubject() throws IOException {
		System.out.print("검색 할 제목 : ");
		String subject = in.readLine();
//		TODO : Service의 searchListBySubject을 호출하세요!!!
		List<BoardDto> list = BoardServiceImpl.getBoardService().searchListBySubject(subject);
//		END
		System.out.println("********** 글목록(제목검색) **********");
		for(BoardDto article : list) {
			System.out.println(article);
		}
	}

	private void viewArticle() throws IOException {
		System.out.print("글번호 : ");
		int no = Integer.parseInt(in.readLine());
//		TODO : Service의 viewArticle을 호출하세요!!!
		BoardDto boardDto = BoardServiceImpl.getBoardService().viewArticle(no);
//		END		
		System.out.println("********** " + no + "번 글정보 **********");
		System.out.println(boardDto);
	}

	private void modifyArticle() throws NumberFormatException, IOException {
		BoardDto boardDto = new BoardDto();
		System.out.print("수정 할 글번호 : ");
		boardDto.setArticleNo(Integer.parseInt(in.readLine()));
		System.out.print("수정 제목 : ");
		boardDto.setSubject(in.readLine());
		System.out.print("수정 내용 : ");
		boardDto.setContent(in.readLine());
//		TODO : Service의 modifyArticle을 호출하세요!!!
		BoardServiceImpl.getBoardService().modifyArticle(boardDto);
//		END
	}

	private void deleteArticle() throws IOException {
		System.out.print("삭제 할 글번호 : ");
		int no = Integer.parseInt(in.readLine());
//		TODO : Service의 deleteArticle을 호출하세요!!!
		BoardServiceImpl.getBoardService().deleteArticle(no);
//		END
	}

	public static void main(String[] args) {
		new BoardMain();
	}

}

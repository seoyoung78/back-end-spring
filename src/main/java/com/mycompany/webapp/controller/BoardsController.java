package com.mycompany.webapp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.webapp.dto.Board;
import com.mycompany.webapp.dto.Pager;
import com.mycompany.webapp.service.BoardsService;

@RestController		//REST 지원 컨트롤러
@RequestMapping("/boards")
public class BoardsController {
	private final Logger logger = LoggerFactory.getLogger(BoardsController.class);
	
	@Autowired
	private BoardsService boardsService;
	
	@GetMapping("")
	public List<Board> list(@RequestParam(defaultValue = "1") int pageNo) {
		int totalRows = boardsService.getCount();
		Pager pager = new Pager(5, 5, totalRows, pageNo);
		List<Board> list = boardsService.getList(pager);
		return list;		//JSON을 만들기 위한 데이터가 들어감(view 이름 x)
	}	
}

package com.mycompany.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	public Map<String, Object> list(@RequestParam(defaultValue = "1") int pageNo) {
		int totalRows = boardsService.getCount();
		Pager pager = new Pager(5, 5, totalRows, pageNo);
		List<Board> list = boardsService.getList(pager);
		
		//2개 이상의 데이터를 전달하기 위해 map에 저장
		Map<String, Object> map = new HashMap<>();
		map.put("pager", pager);
		map.put("boards", list);
		
		return map;		//JSON을 만들기 위한 데이터가 들어감(view 이름 x)
	}	
	
    @PostMapping("")	//리소스 생성
    public Board create(Board board) {	//특정하게 보낼 데이터는 없지만 자동적으로 200 응답데이터 보내짐
       logger.info(board.getBtitle());
       logger.info(board.getBcontent());
       logger.info(board.getBwriter());
       if(board.getBattach() != null && !board.getBattach().isEmpty()) {
          MultipartFile mf = board.getBattach();
          board.setBattachoname(mf.getOriginalFilename());
          board.setBattachsname(new Date().getTime() + "-" + mf.getOriginalFilename());
          board.setBattachtype(mf.getContentType());
          try {
             File file = new File("D:/Team6Projects/uploadfiles/" + board.getBattachsname());
             mf.transferTo(file);
          } catch (Exception e) {
             e.printStackTrace();
          }
       }
       boardsService.insert(board);
       board.setBattach(null);
       return board;
    }
    
    @GetMapping("/{bno}")	//경로 상에 bno를 전달
    public Board read(@PathVariable int bno) {	//경로 상의 bno를 매개변수로 받기 위해 PathVariable 사용
    	boardsService.addHitcount(bno);
    	Board board = boardsService.getBoard(bno);
    	return board;
    }
    
    @GetMapping("/battach/{bno}")
    public void download(@PathVariable int bno, HttpServletResponse response) {
       try {
          Board board = boardsService.getBoard(bno);
          String battachoname = board.getBattachoname();
          if(battachoname == null) return;
          battachoname = new String(battachoname.getBytes("UTF-8"),"ISO-8859-1");	//다시 인코딩
          String battachsname = board.getBattachsname();      
          String battachspath = "D:/Team6Projects/uploadfiles/" + battachsname;
          String battachtype = board.getBattachtype();
    
          response.setHeader("Content-Disposition", "attachment; filename=\""+battachoname+"\";");
          response.setContentType(battachtype);

          InputStream is = new FileInputStream(battachspath);
          OutputStream os = response.getOutputStream();
          FileCopyUtils.copy(is, os);	//body 본문에 데이터로 실어 보내주기 위함
          is.close();
          os.flush();
          os.close();
       } catch (Exception e) {
          e.printStackTrace();
       }
    }
    
    @PutMapping("")	//수정
    public Board update(Board board) {
       if(board.getBattach() != null  && !board.getBattach().isEmpty()) {
          MultipartFile mf = board.getBattach();
          board.setBattachoname(mf.getOriginalFilename());
          board.setBattachsname(new Date().getTime() + "-" + mf.getOriginalFilename());
          board.setBattachtype(mf.getContentType());
          try {
             File file = new File("D:/Team6Projects/uploadfiles/" + board.getBattachsname());
             mf.transferTo(file);
          } catch (Exception e) {
             e.printStackTrace();
          }
       }
       boardsService.update(board);
       board.setBattach(null);
       return board;
    }
    
    @DeleteMapping("/{bno}")
    public void delete(@PathVariable int bno) {
    	boardsService.delete(bno);
    }
}

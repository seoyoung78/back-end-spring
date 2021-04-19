package com.mycompany.webapp.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {
	//uid를 이용하여 사용자의 상세 정보를 가져오는 서비스 객체 - 사용자 이름, 사용자 ROLE 등
	private UserDetailsService userDetailsService;
	
	public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//Jwt 토큰이 요청 헤더로 전송된 경우
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String jwt = httpRequest.getHeader("authToken");
		if(jwt == null) {
			//JWT가 요청 파라미터로 전달된 경우
			//<img src="download?bno=3&authToken=xxxx"/> - GET 방식으로 요청 / AJAX 아님
			jwt = request.getParameter("authToken");
		}
		if(jwt != null) {
			if(JwtUtil.validateToken(jwt)) {
				//JWT에서 uid 얻기
				String uid = JwtUtil.getUid(jwt);
				//DB에서 uid에 해당하는 정보를 가져오기(이름, 권한(롤)들 등)
				UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
				//인증 성공 객체 생성
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
				//Spring Security에 인증 객체 등록
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		
		//다음 필터 실행
		chain.doFilter(request, response);
	}

}

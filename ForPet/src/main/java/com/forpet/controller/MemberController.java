package com.forpet.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.forpet.common.MailHandler;
import com.forpet.common.TempKey;
import com.forpet.model.vo.Member;
import com.forpet.service.MemberService;

@Controller
public class MemberController {

	private Logger logger=LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
	private MemberService service;
	
	@Autowired
	private BCryptPasswordEncoder bcEcoder;
	
	@Autowired
    private JavaMailSender mailSender;
    
	@RequestMapping("/member/memberMyInform.do")
	public String memberMyInform() {
		return "member/memberMyInform";
	}
	
	@RequestMapping("/member/memberEnroll.do")
	public String memberEnroll() {
		return "member/memberEnroll";
	}
	
	@RequestMapping("/member/memberUpdate.do")
	public ModelAndView memberUpdateEnd(HttpSession session,Model model) {
		
		Member re=service.selectOne((Member)session.getAttribute("loggedMember"));
		
		ModelAndView mv=new ModelAndView();
		mv.setViewName("myPage/member/memberUpdate");
		mv.addObject("member",re);
		return mv;
	}
	
	@RequestMapping("/member/memberUpdateEnd.do")
	public ModelAndView updateEnd(Member m, HttpSession session) {
		String rawPw=m.getMemberPassword();
		String enPw=bcEcoder.encode(rawPw);
		m.setMemberPassword(enPw);
		System.out.println(m);
		int result=service.update(m);
		m = service.selectOne(m);
		String msg="";
		String loc="/member/update.do?memberEmail="+m.getMemberEmail();
		if(result>0) {
			msg="수정완료";
			session.setAttribute("loggedMember",m );
		}else {
			msg="수정실패";
		}
		ModelAndView mv=new ModelAndView();
		mv.setViewName("common/msg");
		mv.addObject("msg",msg);
		return mv;
		
	}
	
	@RequestMapping("member/memberDel.do")
	public String memberDel(Member m) {
		int result=service.delete(m);
		
		return "redirect:/";
	}
	
	@RequestMapping("/member/memberEnrollEnd.do")
	public String memberEnrollEnd(Member m,String authKey,Model model) {
		if(authKey==null||!authKey.equals(service.selectCountUserAuth(m.getMemberEmail())))
		{
			model.addAttribute("msg","이메일 인증번호가 일치하지 않습니다");
			model.addAttribute("loc","/member/memberEnroll.do");
			return "common/msg";
		}
		
		
		String rawPw=m.getMemberPassword();
		String enPw=bcEcoder.encode(rawPw);
		m.setMemberPassword(enPw);
		
		int result=service.insertMember(m);
		String msg="";
		String loc="/";
		if(result>0) {
			msg="회원가입 완료";
		}else {
			msg="회원가입 실패";
		}
		model.addAttribute("msg",msg);
		model.addAttribute("loc",loc);
		return "member/memberEnrollEnd";
	}
	
	@RequestMapping("/member/memberLogin.do")
	public String login(Member m, Model model, HttpSession session) {
		Member result=service.selectOne(m);
		String msg="";
		String loc="/";
		
		try {
			throw new RuntimeException("test에러 ");
		}catch(RuntimeException e) {
			logger.error("로그인에러 ");
		}
		
		if(result!=null) {
			if(bcEcoder.matches(m.getMemberPassword(), result.getMemberPassword())) {
				msg="로그인 성공";
				session.setAttribute("loggedMember", result);
			}else {
				msg="비밀번호가 일치하지 않습니다";
			}
		}else {
			msg="아이디가 존재하지 않습니다";
		}
		
		model.addAttribute("msg",msg);
		model.addAttribute("loc",loc);
		return "common/msg";
	}
	
	@RequestMapping("/member/logOut.do")
	public String logOut(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	@RequestMapping("/member/checkEmail.do")
	public void checkEmail(String memberEmail, HttpServletResponse res)throws IOException{
		Member m=new Member();
		m.setMemberEmail(memberEmail);
		Member result=service.selectOne(m);
		boolean isOk=(result!=null)?false:true;
		res.getWriter().println(isOk);	
	}
	
	//메일 인증
	@RequestMapping("/member/emailAuth.do")
	public void emailAuth(String memberEmail) {
		String key = new TempKey().getKey(20,false);

		//메일 전송
        try {
        MailHandler sendMail = new MailHandler(mailSender);
        sendMail.setSubject("FAINT  서비스 이메일 인증]");
        sendMail.setText(new StringBuffer().append("<h1>메일인증</h1>"+key).append("<a href='http://localhost:9090/forpet/member/memberEnroll").append("' target='_blank'>인증한 번호로 가입하기</a>").toString());
        sendMail.setFrom("forpetAdmin@gmail.com", "포펫서비스센터 ");       
        	sendMail.setTo(memberEmail);
        	 sendMail.send();
	String result=service.selectCountUserAuth(memberEmail);
     	     	
             if(result==null/*&&result.length()>0*/) {
             	service.insertUserAuth(memberEmail,key); //인증키 db 저장
             }
             else {
             	service.updateUserAuth(memberEmail,key);
             }
        }catch(MessagingException | UnsupportedEncodingException e) {
        	e.printStackTrace();
        }
        
       
    }
	
	
	@RequestMapping("/member/checkNickname.do")
	public void checkNickname(String memberNickname,HttpServletResponse res)throws IOException {
		Member m=new Member();
		m.setMemberNickname(memberNickname);
		Member result=service.selectByNickname(m);
		boolean isOk=(result!=null)?false:true;
		res.getWriter().println(isOk);
	}

	@RequestMapping("/member/kakaoLogin.do")
	public ModelAndView kakaoApiLogin(String kakaoId,String kakaoNick,HttpSession session) {
		ModelAndView mv=new ModelAndView();
/*		String msg="";
		String loc="/";*/
		
		System.out.println(kakaoId);
		Member result=service.kakaoSelectOne(kakaoId);
		System.out.println(result);
		if(result!=null) {
			//추가정보 예전에 입력했던 사용자
			session.setAttribute("loggedMember", result);/*
			msg="카카오 로그인 성공";
			loc="/";*/
			mv.setViewName("/main");
			
		}else {
			//추가정보입력해야되는 사용자
			mv.addObject("kakaoNick", kakaoNick);
			mv.setViewName("/member/kakaoEnroll");
		}
		return mv;
	}
	
	
	
	
}

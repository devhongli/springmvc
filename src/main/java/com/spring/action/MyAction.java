package com.spring.action;


import com.spring.action.service.IDemoService;
import com.spring.annotation.Autowried;
import com.spring.annotation.Controller;
import com.spring.annotation.RequestMapping;

@Controller
public class MyAction {

		@Autowried
		IDemoService demoService;
	
		@RequestMapping("/index.html")
		public void query(){

		}
	
}

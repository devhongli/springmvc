package com.spring.action.service.impl;


import com.spring.action.service.IDemoService;
import com.spring.annotation.Service;

@Service
public class DemoService implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}

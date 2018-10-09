package com.qceda.module.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SinglePageAppController {
	@RequestMapping(value = { "/", "/blogs" })
	public ModelAndView index() {
		return new ModelAndView("index.html");
	}
}
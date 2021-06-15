package com.example.jsp.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.jsp.commons.exception.ProjectException;
import com.example.jsp.commons.model.Transporter;
import com.example.jsp.pojo.Deliver;
import com.example.jsp.pojo.User;
import com.example.jsp.service.DeliverService;
import com.example.jsp.service.OrderService;
import com.example.jsp.service.UserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author 橙鼠鼠
 */
@RestController
@RequestMapping("/deliver")
public class DeliverController {
	DeliverService deliverService;
	UserService userService;
	OrderService orderService;


	@Autowired
	public void setDeliverService (DeliverService deliverService) {
		this.deliverService = deliverService;
	}

	@Autowired
	public void setUserService (UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/enroll/{username}/{password}/{deliverName}/{telephone}")
	public Transporter enroll (@PathVariable("username") String username,
	                           @PathVariable("password") String password,
	                           @PathVariable("deliverName") String deliverName,
	                           @PathVariable("telephone") String telephone) throws ProjectException {
		var user = new User().setUsername(username).setPassword(password);
		userService.create(user);
		var deliver = new Deliver().setLoginUser(user).setTelephone(telephone).setName(deliverName);
		deliverService.create(deliver);
		return new Transporter().setMsg("注册成功");
	}

	/**
	 * 管理骑手页面
	 * 骑手信息显示
	 */
	//@SaCheckLogin
	@GetMapping("/show")
	public Transporter show() throws ProjectException{
		Transporter transporter = new Transporter();
		val select = deliverService.select();
		return transporter.addData("deliver",select).setMsg("查询成功");
	}

	/**
	 * 订单列表页
	 * 骑手接单
	 */
	@SaCheckRole("deliver")
	@GetMapping("/take/{orderId}")
	public Transporter takeOrder (@PathVariable("orderId") Integer orderId) throws ProjectException{
		Transporter transporter = new Transporter();
		val select = orderService.select(orderId);
		val status =select.getStatus();
		select.setStatus(status+1);
		transporter.addData("status",status+1)
				.setMsg("查询成功");
		return transporter;
	}

}

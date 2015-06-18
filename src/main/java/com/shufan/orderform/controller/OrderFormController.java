package com.shufan.orderform.controller;

import haiyan.bill.database.DBBill;
import haiyan.common.CloseUtil;
import haiyan.common.exception.Warning;
import haiyan.common.intf.database.IDBBill;
import haiyan.common.intf.database.orm.IDBRecord;
import haiyan.common.intf.database.orm.IDBResultSet;
import haiyan.common.intf.web.IWebContext;
import haiyan.web.orm.RequestRecord;
import haiyan.web.session.WebContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.shufan.orderform.common.OrderStatus;
import com.shufan.orderform.dao.OrderFormDao;
import com.shufan.orderform.dao.impl.OrderFormDaoImpl;

@Controller
public class OrderFormController {
	
	private RequestRecord createRequestRecord(HttpServletRequest req,
			HttpServletResponse res, OrderFormDao dao) throws Throwable {
		RequestRecord record = new RequestRecord(req, res, dao.getOrderFormTable());
		record.set("STATUS", OrderStatus.INIITIAL.toString());//将订单状态设置成初始状态
		String points = req.getParameter("TOTALPOINTS");//获取可用积分
		if(points != null){
			record.set("TOTALPOINTS", points);
		}
		return record;
	}
	/**
	 * 根据用户Id分页获取用户订单列表
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForms/{userID}/{pageIndex}", method = RequestMethod.GET)
	public ModelAndView orderFormList(HttpServletRequest req, HttpServletResponse res
			,@PathVariable("userID")String userId,@PathVariable("userID")int pageIndex){
		String sPageSize = req.getParameter("maxPageSize");
		int maxPageSize = sPageSize == null ? 20 : Integer.parseInt(sPageSize);
		
		IWebContext context = null; 
		OrderFormDao dao = null;
		IDBResultSet list = null;
		try {
			context = WebContextFactory.createDBContext(req, res);
			dao = new OrderFormDaoImpl(context);
			list = dao.getOrderList(userId, maxPageSize, pageIndex);
		} catch (Throwable e) {
			throw new Warning(500,e);
		} finally {
			CloseUtil.close(context);
		}
		ModelMap model = new ModelMap();
		model.put("list", list.getRecords());
		return new ModelAndView("orderList.vm",model);
	}
	/**
	 * 根据订单ID获取订单页面
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm/{orderFormId}", method = RequestMethod.GET)
	public ModelAndView orderForm(HttpServletRequest req, HttpServletResponse res
			,@PathVariable("orderFormId")String orderFormId){
		IWebContext context = null; 
		OrderFormDao dao = null;
		ModelMap model = new ModelMap();
		try {
			context = WebContextFactory.createDBContext(req, res);
			dao = new OrderFormDaoImpl(context);
			IDBBill bill = new DBBill(context.getUser(), dao.getSetMealBill());
			bill.setBillID(orderFormId);
			bill = dao.selectOrderForm(bill);
			IDBResultSet billHead = bill.getResultSet(0);
			IDBResultSet billDetail = bill.getResultSet(1);
			if(billHead.getPageRowCount()>0)
				model.put("head",billHead.getRecord(0));
			else
				return new ModelAndView("404.html");
			if(billDetail.getRecordCount()>0){
				model.put("detail", billDetail.getRecords());
			}
		} catch (Throwable e) {
			throw new Warning(500,e);
		} finally {
			CloseUtil.close(context);
		}
		return new ModelAndView("orderForm.vm",model);
	}
	/**
	 * 创建订单页面
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm/create", method = RequestMethod.POST)
	public ModelAndView createOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null; 
		OrderFormDao dao = null;
		IDBRecord record = null;
		try {
			context = WebContextFactory.createDBContext(req, res);
			dao = new OrderFormDaoImpl(context);
			IDBBill bill = new DBBill(context.getUser(), dao.getSetMealBill());
			
			record = createRequestRecord(req, res, dao);
		} catch (Throwable e) {
			throw new Warning(500,e);
		} finally {
			CloseUtil.close(context);
		}
		ModelMap model = new ModelMap();
		if(record != null)
			model.putAll(record.getDataMap());
		return new ModelAndView("orderForm.vm",model);
	}
	/**
	 * 确认下单,返回支付页面
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm/confirm", method = RequestMethod.POST)
	public ModelAndView saveOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null; 
		OrderFormDao dao = null;
		IDBRecord record = null;
		try {
			context = WebContextFactory.createDBContext(req, res);
			dao = new OrderFormDaoImpl(context);
			record = new RequestRecord(req, res, dao.getOrderFormTable());
			//TODO 必填项是填写，金额是否正确
			record.set("STATUS", OrderStatus.UNPAID.toString());//将订单状态设置成未支付状态
			record = dao.createOrderForm(record);
		} catch (Throwable e) {
			throw new Warning(500,e);
		}finally {
			CloseUtil.close(context);
		}
		ModelMap model = new ModelMap();
		model.putAll(record.getDataMap());
		return new ModelAndView("pay.vm",model);
	}
	/**
	 * 支付
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm/pay", method = RequestMethod.POST)
	public ModelMap payOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null; 
		OrderFormDao dao = null;
		IDBRecord record = null;
		try {
			context = WebContextFactory.createDBContext(req, res);
			dao = new OrderFormDaoImpl(context);
			record = new RequestRecord(req, res, dao.getOrderFormTable());
			//TODO 是否支付完成
			record.set("STATUS", OrderStatus.PAID.toString());//将订单状态设置成支付状态
			record = dao.updateOrderForm(record);
		} catch (Throwable e) {
			throw new Warning(500,e);
		}finally {
			CloseUtil.close(context);
		}
		ModelMap model = new ModelMap();
		model.put("success", true);
		return model;
	}
	/**
	 * 确认收货
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm/receipt", method = RequestMethod.POST)
	public ModelMap receiptOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null; 
		OrderFormDao dao = null;
		IDBRecord record = null;
		try {
			context = WebContextFactory.createDBContext(req, res);
			dao = new OrderFormDaoImpl(context);
			record = new RequestRecord(req, res, dao.getOrderFormTable());
			record.set("STATUS", OrderStatus.RECEIPT.toString());//将订单状态设置成已收货
			record = dao.updateOrderForm(record);
			ModelMap model = new ModelMap();
			model.put("STATUS",record.get("STATUS"));
			return model;
		} catch (Throwable e) {
			throw new Warning(500,e);
		}finally {
			CloseUtil.close(context);
		}
	}
	/**
	 * 取消订单
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm/cancel", method = RequestMethod.POST)
	public ModelMap cancelOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null; 
		OrderFormDao dao = null;
		IDBRecord record = null;
		try {
			context = WebContextFactory.createDBContext(req, res);
			dao = new OrderFormDaoImpl(context);
			record = new RequestRecord(req, res, dao.getOrderFormTable());
			OrderStatus status = OrderStatus.valueOf(record.get("STATUS").toString());
			ModelMap model = new ModelMap();
			switch (status){
				case REFUND:{
					//TODO 退款
					record.set("STATUS", OrderStatus.REFUND.toString());//将订单状态设置成退款中
					break;
				}case UNPAID:{
					record.set("STATUS", OrderStatus.CANCEL.toString());//将订单状态设置成已取消
				}default :{
					//不能取消
					return model;
				}
			}
			record = dao.updateOrderForm(record);
			model.put("STATUS",record.get("STATUS"));
			return model;
		} catch (Throwable e) {
			throw new Warning(500,e);
		} finally {
			CloseUtil.close(context);
		}
	}
	
}

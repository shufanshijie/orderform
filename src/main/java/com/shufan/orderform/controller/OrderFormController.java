package com.shufan.orderform.controller;

import haiyan.bill.database.DBBill;
import haiyan.common.CloseUtil;
import haiyan.common.exception.Warning;
import haiyan.common.intf.database.IDBBill;
import haiyan.common.intf.database.IDBFilter;
import haiyan.common.intf.database.orm.IDBRecord;
import haiyan.common.intf.database.orm.IDBResultSet;
import haiyan.common.intf.web.IWebContext;
import haiyan.orm.database.DBPage;
import haiyan.orm.database.sql.SQLDBFilter;
import haiyan.web.orm.RequestRecord;
import haiyan.web.session.WebContextFactory;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jgroups.util.UUID;
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
	/**
	 * 根据用户Id分页获取用户订单列表
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForms/{userID}", method = RequestMethod.GET)
	public ModelAndView orderFormList(HttpServletRequest req, HttpServletResponse res
			,@PathVariable("userID")String userId){
		IWebContext context = null;
		try{
			context= WebContextFactory.createDBContext(req, res);
			String sPageSize = req.getParameter("maxPageSize");
			int maxPageSize = sPageSize == null ? 20 : Integer.parseInt(sPageSize);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			IDBResultSet list = dao.getOrderList(userId, maxPageSize, 1);
			ModelMap model = new ModelMap();
			model.put("list", list.getRecords());
			return new ModelAndView("order_manager.vm",model);
		}finally{
			CloseUtil.close(context);
		}
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
		try{
			context = WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			IDBRecord record = dao.getOrderForm(orderFormId);
			ModelMap model = new ModelMap();
			if(record != null)
				model.putAll(record.getDataMap());
			return new ModelAndView("orderForm.vm",model);
		}finally{
			CloseUtil.close(context);
		}
	}	
	@RequestMapping(value = "orderForm_complete", method = RequestMethod.POST)
	public void completeOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null;
		PrintWriter writer = null;
		try{
			context = WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			String mealdetail = req.getParameter("mealdetail");
			String userId = req.getParameter("userid");
			String productIds = req.getParameter("productids");
			JSONArray datas = JSONArray.fromObject(mealdetail);
			DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			Template pdTemplate = Velocity.getTemplate("tradingscheme.vm","utf-8");
			VelocityContext ctx = new VelocityContext();
			ArrayList<JSONObject> comboList = new ArrayList<JSONObject>();
			ctx.put("comboList", comboList);
			Date createDate = new Date(System.currentTimeMillis());
			String lotOrderID = UUID.randomUUID().toString();
			BigDecimal allprice = new BigDecimal(0);
			for (int i = 0; i < datas.size(); i++) {
				JSONObject mealData = datas.getJSONObject(i);
				if(mealData == null || mealData.isNullObject())
					continue;
				BigDecimal price = new BigDecimal(mealData.getString("price"));
				price = price.multiply(new BigDecimal(mealData.getInt("COUNT")));
				allprice = price.add(allprice);
				mealData.accumulate("price", price);
				comboList.add(mealData);
				String sDate = mealData.getString("DISPATCHINGDATE");
				Date date = fmt.parse(sDate);
				IDBResultSet headSet = new DBPage(new ArrayList<IDBRecord>());
				IDBRecord headRecord = headSet.appendRow();
				headRecord.set("USERID", userId);
				headRecord.set("PRODUCTIDS", productIds);
				headRecord.set("NAME", mealData.getString("name"));
				headRecord.set("WEEK", mealData.getString("week"));
				headRecord.set("CREATEDATE", createDate);
				headRecord.set("DISPATCHINGDATE", date);
				headRecord.set("LOTNO", lotOrderID);
				headRecord.set("TOTALPRICE", price);
				IDBBill bill = new DBBill(null, dao.getSetMealBill());
				bill.setResultSet(0, headSet);
				dao.addOrderForm(bill);
				mealData.accumulate("billID", bill.getBillID());
			}
			req.getSession().setAttribute("currentSumPrice", allprice);
			ctx.put("allprice", allprice);
			req.getSession().setAttribute("userid", userId);
			ctx.put("userid", userId);
			req.getSession().setAttribute("lotNo", lotOrderID);
			ctx.put("lotNo", lotOrderID);
			StringWriter sw = new StringWriter();
			BufferedWriter bw = new BufferedWriter(sw);
			writer = res.getWriter();
			pdTemplate.merge(ctx, bw);
			bw.flush();
			String backData = sw.toString().replaceAll("\\n", "").replaceAll("\\t", "").replaceAll("\\r", "");
			writer.write(backData);
		} catch (Throwable e1) {
			throw new Warning(500,e1);
		}finally{
			CloseUtil.close(writer);
			CloseUtil.close(context);
		}
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
		try{
			context = WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			IDBRecord record = null;
			try {
				record = createRequestRecord(req, res, dao);
			} catch (Throwable e) {
				throw new Warning(500,e);
			}
			ModelMap model = new ModelMap();
			if(record != null)
				model.putAll(record.getDataMap());
			return new ModelAndView("orderForm.vm",model);
		}finally{
			CloseUtil.close(context);
		}
	}
	
	@RequestMapping(value = "orderForm_finish", method = RequestMethod.GET)
	public ModelAndView finishOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null;
		try{
			String id = req.getParameter("ID");
			context = WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			IDBRecord record = null;
			try {
				record = new RequestRecord(req, res, dao.getOrderFormTable());
				record.set("ID",id);
				//TODO 是否支付完成
				record.set("STATUS", OrderStatus.RECEIPT.toString());//将订单状态设置成收货状态
				record = dao.updateOrderForm(record,null);
			} catch (Throwable e) {
				throw new Warning(500,e);
			}
			ModelMap model = new ModelMap();
			return new ModelAndView("reserve_success.vm",model);
		}finally{
			CloseUtil.close(context);
		}
	}
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
	 * 确认下单,返回支付页面
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm/confirm", method = RequestMethod.POST)
	public ModelAndView saveOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null;
		try{
			context = WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			IDBRecord record = null;
			try {
				record = new RequestRecord(req, res, dao.getOrderFormTable());
				//TODO 必填项是填写，金额是否正确
				record.set("STATUS", OrderStatus.UNPAID.toString());//将订单状态设置成未支付状态
				record = dao.createOrderForm(record);
			} catch (Throwable e) {
				throw new Warning(500,e);
			}
			ModelMap model = new ModelMap();
			model.putAll(record.getDataMap());
			return new ModelAndView("pay.vm",model);
		}finally{
			CloseUtil.close(context);
		}
	}
	/**
	 * 支付
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "orderForm_pay", method = RequestMethod.POST)
	public ModelAndView payOrderForm(HttpServletRequest req, HttpServletResponse res){
		IWebContext context = null;
		try{
			context= WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			IDBRecord record = null;
			try {
				record = new RequestRecord(req, res, dao.getOrderFormTable());
				record.set("TOTALPRICE", req.getSession().getAttribute("currentSumPrice"));
				record.set("DISPATCHINGADDRESS", req.getParameter("DISPATCHINGADDRESS"));
				record.set("DISPATCHINGTIME", req.getParameter("DISPATCHINGTIME"));
//				record.set("DISPATCHINGTYPE", req.getParameter("dispatchingtype"));
				//TODO 是否支付完成
				record.set("STATUS", OrderStatus.PAID.toString());//将订单状态设置成支付状态
				IDBFilter filter = new SQLDBFilter(" and LOTNO = ?", new Object[]{req.getSession().getAttribute("lotNo")});
				record = dao.updateOrderForm(record,filter);//很不雅
			} catch (Throwable e) {
				throw new Warning(500,e);
			}
			String userID = req.getParameter("userid");
			ModelMap model = new ModelMap();
//			model.put("success", true);
			model.put("userID", userID);
//			model.putAll(record.getDataMap());
//			return new ModelAndView("todayCode.vm",model);
			return new ModelAndView("reserve_success.vm",model);
		}finally{
			CloseUtil.close(context);
		}
	}
	/**
	 * 查看自己的取餐码
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping(value = "showTodayCode/{userID}/{orderID}", method = RequestMethod.GET)
	public ModelAndView showTodayCode(HttpServletRequest req, HttpServletResponse res
			,@PathVariable("userID")String userId,@PathVariable("orderID")String orderId){
		IWebContext context = null;
		try {
			context = WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
//			String queryString = req.getQueryString();
//			String orderId = queryString.substring(queryString.indexOf("=")+1);
			IDBRecord orderForm = dao.getOrderForm(orderId);
			if(orderForm==null || !userId.equals(orderForm.getString("USERID"))){
				res.setStatus(400);
				return new ModelAndView("404.html");
			}
			ModelMap model = new ModelMap();
			model.put("success", true);
			model.putAll(orderForm.getDataMap());
			model.put("orderId", orderId);
			return new ModelAndView("todayCode.vm",model);
		}finally{
			CloseUtil.close(context);
		}
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
			try{
				context = WebContextFactory.createDBContext(req, res);
				OrderFormDao dao = new OrderFormDaoImpl(context);
				IDBRecord record = null;
				try {
					record = new RequestRecord(req, res, dao.getOrderFormTable());
					record.set("STATUS", OrderStatus.RECEIPT.toString());//将订单状态设置成已收货
					record = dao.updateOrderForm(record,null);
					ModelMap model = new ModelMap();
					model.put("STATUS",record.get("STATUS"));
					return model;
				} catch (Throwable e) {
					throw new Warning(500,e);
				}
			}finally{
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
		try {
			context = WebContextFactory.createDBContext(req, res);
			OrderFormDao dao = new OrderFormDaoImpl(context);
			IDBRecord record = null;
			try {
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
				record = dao.updateOrderForm(record,null);
				model.put("STATUS",record.get("STATUS"));
				return model;
			} catch (Throwable e) {
				throw new Warning(500,e);
			} 
		}finally{
			CloseUtil.close(context);
		}
	}
	
}

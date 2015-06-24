package com.shufan.orderform.dao;

import haiyan.common.intf.config.IBillConfig;
import haiyan.common.intf.database.IDBBill;
import haiyan.common.intf.database.orm.IDBRecord;
import haiyan.common.intf.database.orm.IDBResultSet;
import haiyan.config.castorgen.Table;

/**
 * 订单数据访问对象接口
 * @author 商杰
 *
 */
public interface OrderFormDao {
	/**
	 * 根据用户ID和年月取相应的订单头信息
	 * @param userId
	 * @param year
	 * @param month
	 * @return
	 */
	public abstract IDBResultSet getOrderList(String userId,int year,int month);
	/**
	 * 根据用户ID订单ID查询订单头信息
	 * @param userId
	 * @param orderId
	 * @return
	 */
	public abstract IDBRecord getOrderForm(String userId,String orderId);
	/**
	 * 根据订单ID修改订单头信息
	 * @param orderForm
	 * @return
	 */
	public abstract IDBRecord updateOrderForm(IDBRecord orderForm);
	/**
	 * 根据订单批号修改订单头信息
	 * @param orderForm
	 * @param lotNo
	 * @return
	 */
	public abstract IDBRecord updateOrderFormByLotNo(IDBRecord orderForm,String lotNo);
	/**
	 * 新增订单
	 * @param bill
	 * @return
	 * @throws Throwable
	 */
	public abstract IDBBill addOrderForm(IDBBill bill)throws Throwable;
	/**
	 * 根据订单ID修改订单
	 * @param bill
	 * @return
	 * @throws Throwable
	 */
	public abstract IDBBill updateOrderForm(IDBBill bill)throws Throwable;
	/**
	 * 根据订单ID查询订单详情
	 * @param bill
	 * @return
	 * @throws Throwable
	 */
	public abstract IDBBill selectOrderForm(IDBBill bill)throws Throwable;
	/**
	 * 获取订单头表信息
	 * @return
	 */
	public abstract Table getOrderFormTable();
	/**
	 * 获取订单明细表信息
	 * @return
	 */
	public abstract Table getOrderDetailTable();
	/**
	 * 获取订单单据表信息
	 * @return
	 */
	public abstract IBillConfig getSetMealBill();
}

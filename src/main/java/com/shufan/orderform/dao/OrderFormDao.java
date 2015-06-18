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
	
	public abstract IDBResultSet getOrderList(String userId,int maxPageSize,int page);
	public abstract IDBRecord getOrderForm(String orderId);
	public abstract IDBRecord createOrderForm(IDBRecord orderForm);
	public abstract IDBRecord updateOrderForm(IDBRecord orderForm);
	public abstract boolean deleteOrderForm(String[] orderFormId);
	public abstract IDBBill addOrderForm(IDBBill bill)throws Throwable;
	public abstract IDBBill updateOrderForm(IDBBill bill)throws Throwable;
	public abstract IDBBill selectOrderForm(IDBBill bill)throws Throwable;
	public abstract Table getOrderFormTable();
	public abstract Table getOrderDetailTable();
	public abstract IBillConfig getSetMealBill();
}

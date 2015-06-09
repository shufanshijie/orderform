package com.shufan.orderform.dao;

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
	public abstract Table getOrderFormTable();
}

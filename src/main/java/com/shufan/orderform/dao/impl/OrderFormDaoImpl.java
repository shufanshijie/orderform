package com.shufan.orderform.dao.impl;

import haiyan.common.CloseUtil;
import haiyan.common.exception.Warning;
import haiyan.common.intf.database.orm.IDBRecord;
import haiyan.common.intf.database.orm.IDBResultSet;
import haiyan.common.intf.session.IContext;
import haiyan.config.castorgen.Table;
import haiyan.config.intf.database.ITableDBManager;
import haiyan.config.intf.session.ITableDBContext;
import haiyan.config.util.ConfigUtil;
import haiyan.orm.database.DBContextFactory;

import com.shufan.orderform.dao.OrderFormDao;

public class OrderFormDaoImpl implements OrderFormDao {
	
	protected IContext parentContext;
	protected OrderFormDaoImpl() {
	}
	public OrderFormDaoImpl(IContext parentContext) {
		this.parentContext = parentContext;
	}
	public IContext getParentContext() {
		return parentContext;
	}
	private static Table orderFormTable;
	public Table getOrderFormTable() {
		if (orderFormTable==null)
			synchronized(OrderFormDaoImpl.class) {
				if (orderFormTable==null)
					orderFormTable = ConfigUtil.getTable("T_ORDERFORM_ORDER");
			}
		return orderFormTable;
	}

	@Override
	public IDBResultSet getOrderList(String userId, int maxPageSize, int page) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = DBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			IDBRecord record = dbm.createRecord();
			record.set("USERID", userId);
			IDBResultSet result = dbm.select(context, getOrderFormTable(), record, maxPageSize, page);
			return result;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}

	@Override
	public IDBRecord getOrderForm(String orderId) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = DBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			IDBRecord record = dbm.select(context, getOrderFormTable(), orderId);
			return record;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}

	@Override
	public IDBRecord createOrderForm(IDBRecord orderForm) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = DBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			IDBRecord record = dbm.insert(context, getOrderFormTable(), orderForm);
			return record;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}

	@Override
	public IDBRecord updateOrderForm(IDBRecord orderForm) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = DBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			IDBRecord record = dbm.update(context, getOrderFormTable(), orderForm);
			return record;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}

	@Override
	public boolean deleteOrderForm(String[] orderFormIds) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = DBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			boolean record = dbm.delete(context, getOrderFormTable(), orderFormIds);
			return record;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}

}

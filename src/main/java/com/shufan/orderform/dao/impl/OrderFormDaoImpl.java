package com.shufan.orderform.dao.impl;

import haiyan.bill.database.BillDBContextFactory;
import haiyan.bill.database.IBillDBManager;
import haiyan.bill.database.sql.IBillDBContext;
import haiyan.common.CloseUtil;
import haiyan.common.exception.Warning;
import haiyan.common.intf.config.IBillConfig;
import haiyan.common.intf.database.IDBBill;
import haiyan.common.intf.database.orm.IDBRecord;
import haiyan.common.intf.database.orm.IDBResultSet;
import haiyan.common.intf.session.IContext;
import haiyan.config.castorgen.Table;
import haiyan.config.util.ConfigUtil;
import haiyan.orm.database.TableDBContextFactory;
import haiyan.orm.intf.database.ITableDBManager;
import haiyan.orm.intf.session.ITableDBContext;

import java.util.Collection;

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
	private static Table orderDetailTable;
	public Table getOrderDetailTable() {
		if (orderDetailTable==null)
			synchronized(OrderFormDaoImpl.class) {
				if (orderDetailTable==null)
					orderDetailTable = ConfigUtil.getTable("T_ORDERFORM_ORDER_DETAIL");
			}
		return orderDetailTable;
	}
	private static IBillConfig orderFormBill;
	public IBillConfig getSetMealBill(){
		if (orderFormBill==null)
			synchronized(OrderFormDaoImpl.class) {
				if (orderFormBill==null)
					orderFormBill = ConfigUtil.getBill("B_ORDERFROM_ORDER");
			}
		return orderFormBill;
	}

	@Override
	public IDBResultSet getOrderList(String userId, int maxPageSize, int page) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = TableDBContextFactory.createDBContext(parentContext);
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
			context = TableDBContextFactory.createDBContext(parentContext);
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
			context = TableDBContextFactory.createDBContext(parentContext);
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
			context = TableDBContextFactory.createDBContext(parentContext);
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
			context = TableDBContextFactory.createDBContext(parentContext);
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
	@Override
	public IDBBill addOrderForm(IDBBill bill) throws Throwable {
		IBillDBContext context = null;
		IBillDBManager bbm = null;
		try {
			context = BillDBContextFactory.createBillDBContext(parentContext,getSetMealBill());
			bbm = context.getBBM();
			IDBResultSet[] rs = bill.getResultSets();
			for(IDBResultSet set : rs){
				Collection<IDBRecord> records = set.getRecords();
				for(IDBRecord record : records){
					record.setStatus(IDBRecord.INSERT);
				}
			}
			context.openTransaction();
			bbm.saveBill(context, bill);
			context.commit();
			return bill;
		} catch (Throwable e) {
			if(context != null)
				context.rollback();
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(bbm);
			CloseUtil.close(context);
		}
	}
	@Override
	public IDBBill updateOrderForm(IDBBill bill) throws Throwable{
		IBillDBContext context = null;
		IBillDBManager bbm = null;
		try {
			context = BillDBContextFactory.createBillDBContext(parentContext,getSetMealBill());
			bbm = context.getBBM();
			IDBResultSet[] rs = bill.getResultSets();
			for(IDBResultSet set : rs){
				Collection<IDBRecord> records = set.getRecords();
				for(IDBRecord record : records){
					record.setStatus(IDBRecord.UPDATE);
				}
			}
			context.openTransaction();
			bbm.saveBill(context, bill);
			context.commit();
			return bill;
		} catch (Throwable e) {
			if(context != null)
				context.rollback();
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(bbm);
			CloseUtil.close(context);
		}
	}
	@Override
	public IDBBill selectOrderForm(IDBBill bill) throws Throwable{
		IBillDBContext context = null;
		IBillDBManager bbm = null;
		try {
			context = BillDBContextFactory.createBillDBContext(parentContext,getSetMealBill());
			bbm = context.getBBM();
			bbm.loadBill(context, bill);
			return bill;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(bbm);
			CloseUtil.close(context);
		}
	}

}

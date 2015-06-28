package com.shufan.orderform.dao.impl;

import haiyan.bill.database.BillDBContextFactory;
import haiyan.bill.database.DBBill;
import haiyan.bill.database.IBillDBManager;
import haiyan.bill.database.sql.IBillDBContext;
import haiyan.common.CloseUtil;
import haiyan.common.exception.Warning;
import haiyan.common.intf.config.IBillConfig;
import haiyan.common.intf.config.IBillIDConfig;
import haiyan.common.intf.database.IDBBill;
import haiyan.common.intf.database.IDBFilter;
import haiyan.common.intf.database.orm.IDBRecord;
import haiyan.common.intf.database.orm.IDBResultSet;
import haiyan.common.intf.session.IContext;
import haiyan.config.castorgen.Table;
import haiyan.config.util.ConfigUtil;
import haiyan.orm.database.TableDBContextFactory;
import haiyan.orm.database.sql.SQLDBFilter;
import haiyan.orm.intf.database.ITableDBManager;
import haiyan.orm.intf.session.ITableDBContext;

import java.sql.Date;
import java.util.Calendar;
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
	public IDBResultSet getOrderListByMonth(String userId, int year, int month) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = TableDBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			Calendar cal = Calendar.getInstance();
			cal.set(year, month-1, 1,0,0,0);
			java.util.Date uStart = cal.getTime();
			Date start = new Date(uStart.getTime());
			cal.set(year, month, 1,0,0,0);
			java.util.Date uEnd = cal.getTime();
			Date end = new Date(uEnd.getTime());
			IDBFilter filter = new SQLDBFilter(" and USERID = ? and DISPATCHINGDATE >= ? and DISPATCHINGDATE < ? ", new Object[]{userId,start,end});
			IDBResultSet result = dbm.select(context, getOrderFormTable(), filter, 31, 1);
			return result;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}
	
	@Override
	public IDBResultSet getOrderListByDay(String userId, int year, int month,int day) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = TableDBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			Calendar cal = Calendar.getInstance();
			cal.set(year, month-1, day,0,0,0);
			java.util.Date uStart = cal.getTime();
			Date start = new Date(uStart.getTime());
			cal.set(year, month-1, day+1,0,0,0);
			java.util.Date uEnd = cal.getTime();
			Date end = new Date(uEnd.getTime());
			IDBFilter filter = new SQLDBFilter(" and USERID = ? and DISPATCHINGDATE >= ? and DISPATCHINGDATE < ? ", new Object[]{userId,start,end});
			IDBResultSet result = dbm.select(context, getOrderFormTable(), filter, 31, 1);
			return result;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}

	@Override
	public IDBRecord getOrderFormHead(String userId,String orderId) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = TableDBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			IDBFilter filter = new SQLDBFilter(" and ID = ? and USERID = ?", new Object[]{orderId,userId});
			IDBResultSet result = dbm.select(context, getOrderFormTable(), filter,1,1);
			if(result.getRecordCount()>0)
				return result.getRecord(0);
			else
				return null;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(dbm);
			CloseUtil.close(context);
		}
	}
	
	@Override
	public IDBBill getOrderForm(String userId, String orderId) {
		IBillDBContext context = null;
		IBillDBManager bbm = null;
		try {
			context = BillDBContextFactory.createBillDBContext(parentContext,getSetMealBill());
			bbm = context.getBBM();
			IDBBill bill = new DBBill(context.getUser(), getSetMealBill());
			bill.setBillID(orderId);
			IBillIDConfig idConf = ConfigUtil.getBillIDConfig(bill.getBillConfig(), 0);
			bill.setDBFilter(0, new SQLDBFilter(" and "+idConf.getDbName()+" = ? and USERID = ? ", new Object[]{orderId,userId}));
			bbm.loadBill(context, bill);;
			return bill;
		} catch (Throwable e) {
			throw Warning.wrapException(e);
		}finally{
			CloseUtil.close(bbm);
			CloseUtil.close(context);
		}
	}

	@Override
	public IDBRecord updateOrderFormHead(IDBRecord orderForm) {
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
	public IDBRecord updateOrderFormHeadByLotNo(IDBRecord orderForm,String lotNo) {
		ITableDBContext context = null;
		ITableDBManager dbm = null;
		try {
			context = TableDBContextFactory.createDBContext(parentContext);
			dbm = context.getDBM();
			IDBFilter filter = new SQLDBFilter(" and LOTNO = ?", new Object[]{lotNo});
			IDBRecord record = dbm.update(context, getOrderFormTable(), orderForm,filter);
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

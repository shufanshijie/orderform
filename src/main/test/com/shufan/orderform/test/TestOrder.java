package com.shufan.orderform.test;

import haiyan.bill.database.DBBill;
import haiyan.common.config.PathUtil;
import haiyan.common.intf.database.IDBBill;
import haiyan.common.intf.database.orm.IDBRecord;
import haiyan.common.intf.database.orm.IDBResultSet;
import haiyan.orm.database.DBPage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.shufan.orderform.common.ContextListener;
import com.shufan.orderform.dao.OrderFormDao;
import com.shufan.orderform.dao.impl.OrderFormDaoImpl;

public class TestOrder {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		String s = System.getProperty("user.dir");
		Properties p = PathUtil.getEnvVars();
		p.setProperty("HAIYAN_HOME", s+File.separator+"WebContent");
		ContextListener.init(s+File.separator+"WebContent"+File.separator+"WEB-INF");
		ContextListener.USE_ES=true; 
		
		testBill();
//		testSetMeal();
		
		System.exit(0);
	}

	private static void testBill() throws Throwable {
		IDBResultSet headSet = new DBPage(new ArrayList<IDBRecord>());
		IDBRecord headRecord = headSet.appendRow();
		headRecord.set("NAME", "订单列表名称2");
		headRecord.set("DISPATCHINGADDRESS", "地址1");
		headRecord.set("DISPATCHINGDATE", new Date(System.currentTimeMillis()));
		headRecord.set("DISPATCHINGTYPE", "0");
		headRecord.set("TOTALPRICE", 30);
		headRecord.set("USERID", "aaaaab");
		headRecord.set("CREATEDATE", new Date(System.currentTimeMillis()));
		
		IDBResultSet detailSet = new DBPage(new ArrayList<IDBRecord>());
		IDBRecord detailRecord = detailSet.appendRow();
		detailRecord.set("NAME", "套餐2");
		detailRecord.set("COUNT", 1);
		detailRecord.set("DISPATCHINGDATE", new Date(System.currentTimeMillis()));
		detailRecord.set("DISPATCHINGTYPE", "0");
		detailRecord.set("PRICE", 10);
		detailRecord.set("TOTALPRICE", 10);
		
		detailRecord = detailSet.appendRow();
		detailRecord.set("NAME", "套餐1");
		detailRecord.set("COUNT", 2);
		detailRecord.set("DISPATCHINGDATE", new Date(System.currentTimeMillis()));
		detailRecord.set("DISPATCHINGTYPE", "0");
		detailRecord.set("PRICE", 10);
		detailRecord.set("TOTALPRICE", 20);
		
		detailRecord = detailSet.appendRow();
		detailRecord.set("NAME", "套餐1");
		detailRecord.set("COUNT", 2);
		detailRecord.set("DISPATCHINGDATE", new Date(System.currentTimeMillis()));
		detailRecord.set("DISPATCHINGTYPE", "0");
		detailRecord.set("PRICE", 10);
		detailRecord.set("TOTALPRICE", 20);
		
		OrderFormDao dao = new OrderFormDaoImpl(null);
		IDBBill bill = new DBBill(null, dao.getSetMealBill());
		bill.setResultSet(0, headSet);
		bill.setResultSet(1, detailSet);
		bill = dao.addOrderForm(bill);
		System.out.println(bill.getBillID());
		
	}

}

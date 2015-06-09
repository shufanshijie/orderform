package com.shufan.orderform.test;

import com.shufan.orderform.common.OrderStatus;

public class TestEnum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(OrderStatus.INIITIAL.name()+" : "+OrderStatus.INIITIAL.ordinal());
		System.out.println(OrderStatus.PAID.name()+" : "+OrderStatus.PAID.ordinal());
	}

}

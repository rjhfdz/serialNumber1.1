package com.boray.ui;

import javax.comm.SerialPort;
import java.util.ArrayList;
import java.util.List;

public class Data {

	public static SerialPort serialPort;
	public static String comKou;
	public static Thread thread;
	public static List<String> list = new ArrayList<>();
	static {
		list.add("04");
		list.add("08");
		list.add("0A");
		list.add("0C");
		list.add("0D");
		list.add("10");
		list.add("11");
	}
}

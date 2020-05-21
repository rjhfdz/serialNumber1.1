package com.boray.comListern;

import io.netty.buffer.ByteBufUtil;

import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.boray.EncryPwd.TeaUtil;
import com.boray.ui.Data;
import com.boray.ui.mainUI;


public class SerialPortListern implements Runnable{
	public void run() {
		try {
			InputStream is = Data.serialPort.getInputStream();
			byte[] temp = new byte[1024];
			int len = -1;
			while (true) {
				len = is.read(temp);
				if (len > 4 && Byte.toUnsignedInt(temp[3])==2) {
					if (mainUI.writeTimer!=null) {
						mainUI.writeTimer.cancel();
						mainUI.writeTimer = null;
					}
					JTextField label = (JTextField)mainUI.map.get("xiLieHaoLabel");
					String str = "";
					int a = 0;
					byte[] en = new byte[17];
					for (int i = 0; i < 17; i++) {
						en[i] = temp[4+i];
						//a = Byte.toUnsignedInt(temp[4+i]);
						//str = str + (Integer.toHexString(a).length()==1?"0"+Integer.toHexString(a).toUpperCase():Integer.toHexString(a).toUpperCase());
					}
					str = ByteBufUtil.hexDump(TeaUtil.decryptByHexKey("424F53444F4E4C6F636B4C6F6E767838", en)).toUpperCase();
					label.setText(str);
				} else if (len > 4 && Byte.toUnsignedInt(temp[3])==3) {
					if (mainUI.writeTimer!=null) {
						mainUI.writeTimer.cancel();
						mainUI.writeTimer = null;
					}
					if (Byte.toUnsignedInt(temp[5])==1) {
						JLabel label3 = (JLabel)mainUI.map.get("successLabel");
						int cnt = Integer.valueOf(label3.getText()).intValue()+1;
						label3.setText(cnt+"");
					} else {
						JLabel label3 = (JLabel)mainUI.map.get("failLabel");
						int cnt = Integer.valueOf(label3.getText()).intValue()+1;
						label3.setText(cnt+"");
						JFrame frame = (JFrame)mainUI.map.get("frame");
						JOptionPane.showMessageDialog(frame, "Ð´ÈëÊ§°Ü£¡", "ÌáÊ¾", JOptionPane.ERROR_MESSAGE);
					}
				} else if (len > 4 && Byte.toUnsignedInt(temp[3])==4) {
					if (mainUI.writeTimer!=null) {
						mainUI.writeTimer.cancel();
						mainUI.writeTimer = null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

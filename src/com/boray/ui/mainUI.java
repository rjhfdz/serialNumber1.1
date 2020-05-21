package com.boray.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.boray.EncryPwd.TeaUtil;
import com.boray.comListern.SerialPortListern;
import com.sun.org.apache.bcel.internal.generic.AllocationInstruction;


public class mainUI implements ActionListener {
    private JFrame frame;
    private JComboBox box8;
    private JToggleButton button, button2;
    private String tp = null;
    private int week = 0;
    public static HashMap map = new HashMap();
    public static Timer writeTimer;
    private String[] s2, s3, s4;

    public mainUI() {
    }

    public void show() {
        try {
            //JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel());
	         /*UIManager.put("FileChooser.cancelButtonText", "取消");
	         UIManager.put("FileChooser.saveButtonText", "保存");
	         UIManager.put("FileChooser.openButtonText", "打开");
	         UIManager.put("FileChooser.newFolderButtonText", "新建文件夹");
	         UIManager.put("menuText.Copy", "是");*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new JFrame("智能锁序列号读写工具V1.2");
        map.put("frame", frame);
        int screenWidth = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
        int screenHeight = ((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
        int frameWidth = 460;
        int frameHeight = 415;
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(screenWidth / 2 - frameWidth / 2, screenHeight / 2 - frameHeight / 2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);
        flowLayout.setVgap(10);
        frame.setLayout(flowLayout);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (Data.serialPort != null) {
                    try {
                        OutputStream os = Data.serialPort.getOutputStream();
                        byte[] b = new byte[7];
                        //0x5353	0x07	0xFF	0x00	0x06	0x45
                        b[0] = 0x53;
                        b[1] = 0x53;
                        b[2] = 0x07;
                        b[3] = (byte) 0xFF;
                        b[5] = 0x06;
                        b[6] = 0x45;
                        os.write(b);
                        os.flush();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    Data.thread.stop();
                    Data.serialPort.close();
                    Data.serialPort = null;
                    box8.setEnabled(true);
                    JTextField label = (JTextField) mainUI.map.get("xiLieHaoLabel");
                    label.setText("");
                }

                JComboBox boxa = (JComboBox) mainUI.map.get("zhufenlei");
                JComboBox boxa1 = (JComboBox) mainUI.map.get("chanpingfenlei");
                JComboBox boxa2 = (JComboBox) mainUI.map.get("xinghaofenlei");
                JTextField jt0 = (JTextField) mainUI.map.get("banbenhao");
                //(this.getClass().getResource("/").getPath());
                //String courseFile = (this.getClass().getResource("/").getPath());
                //System.out.println(courseFile);

                String path = System.getProperty("user.dir");
                try {
                    path = URLDecoder.decode(path, "utf-8");
                    File oa = new File(path, "config.ini");
                    FileOutputStream fos = new FileOutputStream(oa);
                    String s5 = boxa.getSelectedItem().toString();
                    String s6 = boxa1.getSelectedItem().toString();
                    String s7 = boxa2.getSelectedItem().toString();
                    String s8 = jt0.getText();
                    String all = s5 + "&&" + s6 + "&&" + s7 + "&&" + s8;
                    fos.write(all.getBytes());
                    fos.flush();
                    fos.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            public void windowOpened(WindowEvent e) {
                JComboBox boxa = (JComboBox) mainUI.map.get("zhufenlei");
                JComboBox boxa1 = (JComboBox) mainUI.map.get("chanpingfenlei");
                JComboBox boxa2 = (JComboBox) mainUI.map.get("xinghaofenlei");
                JTextField jt0 = (JTextField) mainUI.map.get("banbenhao");
                String path = System.getProperty("user.dir");
                byte[] b0 = new byte[256];
                try {
                    path = URLDecoder.decode(path, "utf-8");
                    File a1 = new File(path, "config.ini");
                    if (a1.exists()) {
                        FileInputStream oa = new FileInputStream(a1);
                        int len = oa.read(b0);
                        String a = new String(b0, 0, len);
                        String[] ss = a.split("&&");
                        oa.close();
                        boxa.setSelectedItem(ss[0]);
                        boxa1.setSelectedItem(ss[1]);
                        boxa2.setSelectedItem(ss[2]);
                        jt0.setText(ss[3]);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        init();
        frame.setVisible(true);
    }

    private void init() {
        JPanel p1 = new JPanel();
        //p1.setBorder(new LineBorder(Color.black));
        p1.setPreferredSize(new Dimension(410, 40));
        //p1.add(new JLabel("                                                       "));
        FlowLayout f1 = new FlowLayout(FlowLayout.LEFT);
        f1.setVgap(10);
        p1.setLayout(f1);
        p1.add(new JLabel("串口号:     "));
        box8 = new JComboBox();
        box8.setPreferredSize(new Dimension(120, 32));
        p1.add(box8);
        CommPortIdentifier cpid;
        Enumeration enumeration = CommPortIdentifier.getPortIdentifiers();
        while (enumeration.hasMoreElements()) {
            cpid = (CommPortIdentifier) enumeration.nextElement();
            if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                box8.addItem(cpid.getName());
            }
        }
        button = new JToggleButton("连接");
        button2 = new JToggleButton("断开");
        button2.setSelected(true);
        button.addActionListener(this);
        button2.addActionListener(this);
        ButtonGroup group = new ButtonGroup();
        group.add(button);
        group.add(button2);
        p1.add(button);
        p1.add(button2);

        JPanel p2 = new JPanel();
        //p2.setBorder(new LineBorder(Color.black));
        p2.setPreferredSize(new Dimension(410, 40));
        p2.add(new JLabel("序列号:    "));
        JTextField label = new JTextField("", JLabel.CENTER);
        //label.removeMouseListener(label.getMouseListeners()[3]);
		/*for (int i = label.getMouseListeners().length-1; i >= 0; i--) {
			System.out.println("///"+label.getMouseListeners()[i]);
			label.removeMouseListener(label.getMouseListeners()[i]);
		}*/
        label.setEditable(false);
        map.put("xiLieHaoLabel", label);
        label.setPreferredSize(new Dimension(200, 34));
        label.setOpaque(true);
        label.setBackground(new Color(196, 196, 196));
        p2.add(label);
        JButton btn = new JButton("读取");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Data.serialPort != null) {
                        OutputStream os = Data.serialPort.getOutputStream();
                        byte[] b = new byte[7];
                        //0x5353	0x07	0x02	0x00	0x09	0x45
                        b[0] = 0x53;
                        b[1] = 0x53;
                        b[2] = 0x07;
                        b[3] = (byte) 0x02;
                        b[5] = 0x09;
                        b[6] = 0x45;
                        os.write(b);
                        os.flush();
                        writeTimer = new Timer();
                        writeTimer.schedule(new TimerTask() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame, "读取失败！", "提示", JOptionPane.ERROR_MESSAGE);
                            }
                        }, 800);
                    } else {
                        JOptionPane.showMessageDialog(frame, "串口未连接！", "提示", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
        btn.setPreferredSize(new Dimension(68, 32));
        p2.add(btn);

        JPanel p2_3 = new JPanel();
        //p2_3.setBorder(new LineBorder(Color.black));
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
        flowLayout.setVgap(10);
        p2_3.setLayout(flowLayout);
        p2_3.setPreferredSize(new Dimension(410, 120));
        //String[] s0_ = new String[2];
		/*String[] s0_1 = new String[16];
		String[] s0_2 = new String[16];
		String[] s0_3 = new String[16];
		*/
        //String[] s0_= {"保仕盾","优果"};


        String[] s = new String[15];
        s2 = new String[255];
        s3 = new String[255];
        s4 = new String[255];
        String[] sq = {"（智能锁）", "（网关类）", "（开关模块）", "（可调模块）", "（空调模块）", "（红外学习模块）",
                "（干接点模块）", "（控制面板）", "（网络模块）", "（传感器类）", "", "", "", "", "", ""};
        //（智能猫眼）
        String[] sq2 = {"（Z1）", "（Z1S）", "（Z1PRO）", "（Z1W）", "（Z1F）", "（Z2）", "（Z2S）", "（Z2W）"};
        String[] sq3 = {"（H1）", "（H1S）", "（H1PRO）", "（H1W）", "（H1F）", "（H2）", "（H2S）", "（H2W）"};
		/*for (int j = 0; j < 64; j++) {
			if (j < 9) {
				s[j] = "0"+(j+1);
			} else {
				s[j] = ""+(j+1);
			}
			if (j < 10) {
				s[j] = s[j] + sq[j];
			}
		}*/


        for (int i = 1; i < 16; i++) {
            s[i - 1] = (Integer.toHexString(i).length() == 1 ? (Integer.toHexString(i).toUpperCase()) : Integer.toHexString(i).toUpperCase()) + sq[i - 1];
        }
		
		/*for (int j = 0; j < 20; j++) {
			if (j < 9) {
				s2[j] = "0"+(j+1);
			} else {
				s2[j] = ""+(j+1);
			}
			if (j < 3) {
				s2[j] = s2[j] + sq2[j];
			}
		}*/
        for (int i = 1; i < 256; i++) {
            if (i < 16) {
                s2[i - 1] = (Integer.toHexString(i).length() == 1 ? ("0" + Integer.toHexString(i).toUpperCase()) : Integer.toHexString(i).toUpperCase());
                s3[i - 1] = s2[i - 1];
                s4[i - 1] = s2[i - 1];
            } else {
                s2[i - 1] = Integer.toHexString(i).toUpperCase();
                s3[i - 1] = s2[i - 1];
                s4[i - 1] = s2[i - 1];
            }
            if (i < 9) {
                s2[i - 1] = s2[i - 1] + sq2[i - 1];
                s4[i - 1] = s4[i - 1] + sq3[i - 1];
            }
        }
        String[] s0 = {"0（保仕盾）", "1（优果）", "2", "3", "4", "5", "6", "7", "8",
                "9", "A", "B", "C", "D", "E", "F"};
        final JComboBox box0 = new JComboBox(s0);
        box0.setPreferredSize(new Dimension(165, 30));
        final JComboBox box = new JComboBox(s);
        box.setPreferredSize(new Dimension(165, 30));
        final JComboBox box2 = new JComboBox(s2);
        box2.setPreferredSize(new Dimension(165, 30));
        map.put("zhufenlei", box0);
        map.put("chanpingfenlei", box);
        map.put("xinghaofenlei", box2);
        ItemListener itemlistener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    DefaultComboBoxModel model = null;
                    //System.out.println("vdfvcf");
					/*if("保仕盾".equals(e.getItem().toString())) {
					if ("01（智能锁）".equals(e.getItem().toString())) {
						model = new DefaultComboBoxModel(s2);
					} else if ("0B（优果智能锁）".equals(e.getItem().toString())) {
						model = new DefaultComboBoxModel(s4);
					} else {
						model = new DefaultComboBoxModel(s3);
					}
					}*/
					/*else if("优果".equals(e.getItem().toString())) {
						
					}*/
                    if ("0（保仕盾）".equals(e.getItem().toString()) && box.getSelectedItem().toString().equals("1（智能锁）")) {
                        model = new DefaultComboBoxModel(s2);
                    } else if ("1（优果）".equals(e.getItem().toString()) && box.getSelectedItem().toString().equals("1（智能锁）")) {
                        model = new DefaultComboBoxModel(s4);
                    } else {
                        model = new DefaultComboBoxModel(s3);
                    }
                    //box2.setModel(model);
                    box2.setModel(model);
					/*box2.setModel(model);
					box0.setModel(model);*/
                }
            }
        };
        ItemListener listener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                DefaultComboBoxModel model = null;
                if ("1（智能锁）".equals(e.getItem().toString()) && box0.getSelectedItem().toString().equals("0（保仕盾）")) {
                    model = new DefaultComboBoxModel(s2);
                } else if ("1（智能锁）".equals(e.getItem().toString()) && box0.getSelectedItem().toString().equals("1（优果）")) {
                    model = new DefaultComboBoxModel(s4);
                } else {
                    model = new DefaultComboBoxModel(s3);
                }
                box2.setModel(model);
            }
        };
        box0.addItemListener(itemlistener);
        box.addItemListener(listener);

        final JTextField field2 = new JTextField(3);
        map.put("banbenhao", field2);
        field2.setText("11");
        p2_3.add(new JLabel(" 品牌:       "));
        p2_3.add(box0);
        p2_3.add(new JLabel("                                   "));
        p2_3.add(new JLabel("产品分类:  "));
        p2_3.add(box);
        p2_3.add(new JLabel("                         "));
        p2_3.add(new JLabel("型号分类:  "));
        p2_3.add(box2);
        p2_3.add(new JLabel("硬件版本号:"));
        p2_3.add(field2);

        JPanel p3 = new JPanel();
        p3.setPreferredSize(new Dimension(410, 80));
        p3.add(new JLabel("当前时间："));
        final JLabel timeLabel = new JLabel("", JLabel.CENTER);
        timeLabel.setPreferredSize(new Dimension(200, 26));
        timeLabel.setOpaque(true);
        timeLabel.setBackground(new Color(196, 196, 196));
        p3.add(timeLabel);
        p3.add(new JLabel("          "));
        final JLabel label2 = new JLabel("BD01010013030608093B006A", JLabel.CENTER);
        p3.add(new JLabel("       "));
        p3.add(new JLabel("  序列号： "));
        new Timer().schedule(new TimerTask() {
            public void run() {
                Date date = new Date(System.currentTimeMillis());
                String s = date.toLocaleString();
                timeLabel.setText(s);
                int year = (date.getYear() + 1900) % 100;
                int month = date.getMonth() + 1;
                int day = date.getDate();
                int hour = date.getHours();
                int minute = date.getMinutes();
                int seconds = date.getSeconds();
                week = date.getDay();

                //主类号，分类号，版本号
                int zfl_int = Integer.valueOf(box0.getSelectedIndex()).intValue();
                int zl_int = Integer.valueOf(box.getSelectedIndex() + 1).intValue();
                int fl_int = Integer.valueOf(box2.getSelectedIndex() + 1).intValue();
                int version_int = 17;
                if (!"".equals(field2.getText())) {
                    try {
                        version_int = Integer.valueOf(field2.getText(), 16).intValue();
                    } catch (Exception e) {
                        // TODO: handle exception
                        version_int = 17;
                    }
                    if (version_int > 255) {
                        version_int = 255;
                        field2.setText("ff");
                    }
                }
                int check = year + month + day + hour + minute + seconds + zl_int + fl_int + version_int + zfl_int * 16;
                int hgt = check / 256;
                int low = check % 256;

                String y = Integer.toHexString(year).length() == 1 ? ("0" + Integer.toHexString(year).toUpperCase()) : Integer.toHexString(year).toUpperCase();
                String m = Integer.toHexString(month).length() == 1 ? ("0" + Integer.toHexString(month).toUpperCase()) : Integer.toHexString(month).toUpperCase();
                String d = Integer.toHexString(day).length() == 1 ? ("0" + Integer.toHexString(day).toUpperCase()) : Integer.toHexString(day).toUpperCase();
                String h = Integer.toHexString(hour).length() == 1 ? ("0" + Integer.toHexString(hour).toUpperCase()) : Integer.toHexString(hour).toUpperCase();
                String min = Integer.toHexString(minute).length() == 1 ? ("0" + Integer.toHexString(minute).toUpperCase()) : Integer.toHexString(minute).toUpperCase();
                String sec = Integer.toHexString(seconds).length() == 1 ? ("0" + Integer.toHexString(seconds).toUpperCase()) : Integer.toHexString(seconds).toUpperCase();


                String zl = Integer.toHexString(zfl_int).toUpperCase() + Integer.toHexString(zl_int).toUpperCase();
                String fl = fl_int < 16 ? ("0" + Integer.toHexString(fl_int).toUpperCase()) : Integer.toHexString(fl_int).toUpperCase();
                String version = version_int < 16 ? ("0" + Integer.toHexString(version_int).toUpperCase()) : Integer.toHexString(version_int).toUpperCase();

                String ht = Integer.toHexString(hgt).length() == 1 ? ("0" + Integer.toHexString(hgt).toUpperCase()) : Integer.toHexString(hgt).toUpperCase();
                String lw = Integer.toHexString(low).length() == 1 ? ("0" + Integer.toHexString(low).toUpperCase()) : Integer.toHexString(low).toUpperCase();
                String tmp = y + m + d + h + min + sec + ht + lw;

                label2.setText("BD" + zl + fl + version + tmp);
                label2.repaint();
            }
        }, 100, 1000);
        label2.setPreferredSize(new Dimension(200, 26));
        label2.setOpaque(true);
        label2.setBackground(new Color(196, 196, 196));
        p3.add(label2);
        JButton btn2 = new JButton("写入");
        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Data.serialPort != null) {
                    String s = label2.getText();
                    byte[] b = new byte[19];
                    for (int i = 8; i < s.length() - 4; i = i + 2) {
                        b[i / 2 - 4] = Integer.valueOf(s.substring(i, i + 2), 16).byteValue();
                    }
                    b[6] = (byte) week;
                    //b[7] = (byte)66;b[8] = (byte)68;
                    for (int i = 0; i < s.length(); i = i + 2) {
                        b[i / 2 + 7] = Integer.valueOf(s.substring(i, i + 2), 16).byteValue();
                    }
                    //System.out.println(ByteBufUtil.hexDump(b));
                    byte[] en = TeaUtil.encryptByHexKey("424F53444F4E4C6F636B4C6F6E767838", b);
                    // System.out.println(ByteBufUtil.hexDump(en));
                    //53	53	1F	03 03	CB	62	62	4A	16	AD	13	B0	F6	C4	8A	9E	E2	54	81 22	74	C4	83	05	3B	F2	B3	6B	4A	45
                    //0x5353	0x19	0x03	0xXX	0xXX	0x45
                    byte[] bt = new byte[25 + 6];
                    bt[0] = 0x53;
                    bt[1] = 0x53;
                    bt[2] = 0x1F;
                    bt[3] = 0x03;
                    for (int i = 0; i < en.length; i++) {
                        bt[4 + i] = en[i];
                    }
                    int tps = Byte.toUnsignedInt(checkByte(en)) + 31 + 3;
//					int tps = getJiaoYan(bt);
                    bt[29] = (byte) tps;
                    bt[30] = 0x45;

                    try {
                        OutputStream os = Data.serialPort.getOutputStream();
                        os.write(bt);
                        os.flush();
                        writeTimer = new Timer();
                        writeTimer.schedule(new TimerTask() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame, "写入失败！", "提示", JOptionPane.ERROR_MESSAGE);
                            }
                        }, 1000);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "串口未连接！", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btn2.setPreferredSize(new Dimension(68, 32));
        p3.add(btn2);

        JPanel p4 = new JPanel();
        FlowLayout flowLayout2 = new FlowLayout(FlowLayout.LEFT);
        flowLayout2.setHgap(0);
        p4.setLayout(flowLayout2);
        //19 03 13 03 0C 0A 02 26 02 BD 01 01 0B 13 03 0C 0A 02 26 01 1E
        //p4.setBorder(new LineBorder(Color.gray));
        p4.setPreferredSize(new Dimension(420, 80));
        p4.add(new JLabel("序列号:"));
        final JTextField field11 = new JTextField(3);
        final JTextField field22 = new JTextField(3);
        final JTextField field33 = new JTextField(3);
        final JTextField field44 = new JTextField(3);
        final JTextField field55 = new JTextField(3);
        final JTextField field66 = new JTextField(3);
        p4.add(field11);
        p4.add(field22);
        p4.add(field33);
        p4.add(field44);
        p4.add(field55);
        p4.add(field66);
        JButton btn3 = new JButton("强制写入");
        btn3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Data.serialPort != null) {
                    String s = field11.getText() + field22.getText() + field33.getText() +
                            field44.getText() + field55.getText() + field66.getText();
                    if (s != null) {
                        if (s.length() == 24) {
                            if (Integer.valueOf(s.substring(0, 2), 16).intValue() != 189) {
                                JOptionPane.showMessageDialog(frame, "数据不合法!", "提示", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            int cnt = 0, ct8 = 0;
                            for (int i = 2; i < s.length() - 4; i = i + 2) {
                                cnt = cnt + Integer.valueOf(s.substring(i, i + 2), 16).intValue();
                            }
                            ct8 = Integer.valueOf(s.substring(20, 22), 16).intValue() * 256;
                            ct8 = ct8 + Integer.valueOf(s.substring(22, 24), 16).intValue();

                            if (ct8 != cnt) {
                                JOptionPane.showMessageDialog(frame, "数据不合法!", "提示", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            byte[] b = new byte[19];
                            for (int i = 8; i < s.length() - 4; i = i + 2) {
                                b[i / 2 - 4] = Integer.valueOf(s.substring(i, i + 2), 16).byteValue();
                            }
                            b[6] = (byte) week;
                            //b[7] = (byte)66;b[8] = (byte)68;
                            for (int i = 0; i < s.length(); i = i + 2) {
                                b[i / 2 + 7] = Integer.valueOf(s.substring(i, i + 2), 16).byteValue();
                            }

                            byte[] en = TeaUtil.encryptByHexKey("424F53444F4E4C6F636B4C6F6E767838", b);

                            //0x5353	0x19	0x03	0xXX	0xXX	0x45
                            byte[] bt = new byte[25 + 6];
                            bt[0] = 0x53;
                            bt[1] = 0x53;
                            bt[2] = 0x1F;
                            bt[3] = 0x03;
                            for (int i = 0; i < en.length; i++) {
                                bt[4 + i] = en[i];
                            }
                            int tps = Byte.toUnsignedInt(checkByte(en)) + 31 + 3;
//							int tps = getJiaoYan(bt);
                            bt[29] = (byte) tps;
                            bt[30] = 0x45;

                            try {
                                OutputStream os = Data.serialPort.getOutputStream();
                                os.write(bt);
                                os.flush();
                                writeTimer = new Timer();
                                writeTimer.schedule(new TimerTask() {
                                    public void run() {
                                        JOptionPane.showMessageDialog(frame, "写入失败！", "提示", JOptionPane.ERROR_MESSAGE);
                                    }
                                }, 800);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "数据长度不对!", "提示", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "串口未连接！", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btn3.setPreferredSize(new Dimension(82, 32));
        JButton btn4 = new JButton("擦除");
        btn4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Data.serialPort != null) {
                    try {
                        OutputStream os = Data.serialPort.getOutputStream();
                        byte[] b = new byte[15];
                        //53 53 09 04 03 0A 21 3B 45
                        //53 53 0F 04 03 d1 2a 27 3f 1e 37 04 4e (1E) 45
                        b[0] = 0x53;
                        b[1] = 0x53;
                        b[2] = 0x0F;
                        b[3] = (byte) 0x04;
                        b[4] = 0x03;
                        b[5] = (byte) 0xD1;
                        b[6] = 0x2A;
                        b[7] = 0x27;
                        b[8] = 0x3F;
                        b[9] = 0x1E;
                        b[10] = 0x37;
                        b[11] = 0x04;
                        b[12] = 0x4E;
                        b[13] = 0x1E;
                        b[14] = 0x45;
                        os.write(b);
                        os.flush();
                        writeTimer = new Timer();
                        writeTimer.schedule(new TimerTask() {
                            public void run() {
                                JOptionPane.showMessageDialog(frame, "擦除失败！", "提示", JOptionPane.ERROR_MESSAGE);
                            }
                        }, 800);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "串口未连接！", "提示", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btn4.setPreferredSize(new Dimension(68, 32));
        JPanel nullPanel = new JPanel();
        nullPanel.setPreferredSize(new Dimension(88, 40));
        p4.add(nullPanel);
        p4.add(new JLabel("           "));
        p4.add(btn3);//p4.add(btn4);

        p2.add(btn4);

        JPanel p5 = new JPanel();
        p5.setPreferredSize(new Dimension(420, 30));
        //p5.setBorder(new LineBorder(Color.gray));
        //FlowLayout flowLayout1 = new FlowLayout();
        //flowLayout1.setVgap(5);
        //p5.setLayout(flowLayout1);
        p5.add(new JLabel("成功:"));
        JLabel label3 = new JLabel("0");
        map.put("successLabel", label3);
        p5.add(label3);
        p5.add(new JLabel("失败:"));
        JLabel label4 = new JLabel("0");
        map.put("failLabel", label4);
        p5.add(label4);

        FlowLayout f = new FlowLayout(FlowLayout.LEFT);
        p1.setLayout(f);
        p2.setLayout(f);
        p3.setLayout(f);//p4.setLayout(f);
        JPanel nullHeadPane = new JPanel();
        nullHeadPane.setPreferredSize(new Dimension(380, 3));
        frame.add(nullHeadPane);
        frame.add(p1);
        frame.add(p2);
        frame.add(p2_3);
        frame.add(p3);
        //frame.add(p4);
        frame.add(p5);
        //BD01011113030C0A1A04005D
    }

    public void actionPerformed(ActionEvent e) {
        if ("连接".equals(e.getActionCommand())) {
            if (Data.serialPort != null) {
                return;
            }
            try {
                tp = null;
                final String comsString = box8.getSelectedItem().toString();
                Data.comKou = comsString;
                CommPortIdentifier cpid = CommPortIdentifier.getPortIdentifier(comsString);
                Data.serialPort = (SerialPort) cpid.open(comsString, 5);
                Integer botelv = 9600;
                JComboBox box = (JComboBox) map.get("xinghaofenlei");
                if (box.getSelectedItem().toString().contains("04") || box.getSelectedItem().toString().contains("08")) {
                    botelv = 115200;
                }
                Data.serialPort.setSerialPortParams(botelv, 8, 1, 0);
                OutputStream os = Data.serialPort.getOutputStream();
                byte[] b = new byte[7];
                //0x5353	0x07	0x00	0x00	0x07	0x45
                b[0] = 0x53;
                b[1] = 0x53;
                b[2] = 0x07;
                b[5] = 0x07;
                b[6] = 0x45;
                os.write(b);
                os.flush();
                final Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            InputStream is = Data.serialPort.getInputStream();
                            byte[] b = new byte[16];
                            int len = is.read(b);
                            if (len == 7) {
                                tp = Integer.toHexString(b[3] & 0xFF);
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                });
                thread.start();
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        if (tp != null) {
                            (Data.thread = new Thread(new SerialPortListern())).start();
                            box8.setEnabled(false);
                            try {
                                Thread.sleep(150);
                                if (Data.serialPort != null) {
                                    OutputStream os = Data.serialPort.getOutputStream();
                                    byte[] b = new byte[7];
                                    //0x5353	0x07	0x02	0x00	0x09	0x45
                                    b[0] = 0x53;
                                    b[1] = 0x53;
                                    b[2] = 0x07;
                                    b[3] = (byte) 0x02;
                                    b[5] = 0x09;
                                    b[6] = 0x45;
                                    os.write(b);
                                    os.flush();
                                }
                            } catch (Exception e2) {
                            }
                        } else {
                            thread.stop();
                            Data.serialPort.close();
                            Data.serialPort = null;
                            button2.setSelected(true);
                            JOptionPane.showMessageDialog(frame, "连接失败!", "提示", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }, 300);
            } catch (javax.comm.PortInUseException ee) {
                button2.setSelected(true);
                JOptionPane.showMessageDialog(frame, "串口已被占用!", "提示", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else {
            if (Data.serialPort != null) {
                try {
                    OutputStream os = Data.serialPort.getOutputStream();
                    byte[] b = new byte[7];
                    //0x5353	0x07	0xFF	0x00	0x06	0x45
                    b[0] = 0x53;
                    b[1] = 0x53;
                    b[2] = 0x07;
                    b[3] = (byte) 0xFF;
                    b[5] = 0x06;
                    b[6] = 0x45;
                    os.write(b);
                    os.flush();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                Data.thread.stop();
                Data.serialPort.close();
                Data.serialPort = null;
                box8.setEnabled(true);
                JTextField label = (JTextField) mainUI.map.get("xiLieHaoLabel");
                label.setText("");
            }
        }
    }

    byte checkByte(byte[] tp) {
        int cnt = 0;
        for (int i = 0; i < tp.length; i++) {
            cnt = cnt + Byte.toUnsignedInt(tp[i]);
        }
        return Integer.valueOf(cnt).byteValue();
    }

    /*
     * 校验和
     */
    public byte getJiaoYan(byte[] b) {
        int all = 0;
        for (int i = 0; i < b.length - 1; i++) {
            all = all + Byte.toUnsignedInt(b[i]);
        }
        return (byte) all;
    }
}

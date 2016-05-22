package com.imoocXiaoYimain;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.imoocXiaoYi.service.DownloadFile;
import com.imoocXiaoYi.service.GetAttachFile;
import com.imoocXiaoYi.service.GetInfo;
import com.imoocXiaoYi.service.GetList;

public class LoadFrameUI extends JFrame{
	private JPanel contentPane;
	private JTextField textField;
	private JRadioButton radioButton;
	private JRadioButton radioButton_1;
	private JLabel label;
	private JLabel textField_1;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JTextArea textArea;
	
	static int videoNumDef;
	static int classNum;
	class loadThread extends Thread{
		public void run(){
			loadFile();
		}
	}
	loadThread load=new loadThread();
     
    public LoadFrameUI(){
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 641, 663);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(51, 46, 378, 34);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("这里输入慕课网课程号:");
		lblNewLabel.setBounds(51, 21, 365, 26);
		contentPane.add(lblNewLabel);
		
		final JRadioButton rdbtnNewRadioButton = new JRadioButton("普清");
		rdbtnNewRadioButton.setBounds(51, 92, 68, 23);
		contentPane.add(rdbtnNewRadioButton);
		
		radioButton = new JRadioButton("高清");
		radioButton.setBounds(119, 92, 68, 23);
		contentPane.add(radioButton);
		
		radioButton_1 = new JRadioButton("超清");
		radioButton_1.setBounds(190, 92, 74, 23);
		contentPane.add(radioButton_1);
		ButtonGroup bgroup1 = new ButtonGroup();
		bgroup1.add(rdbtnNewRadioButton);
		bgroup1.add(radioButton);
		bgroup1.add(radioButton_1);
		
		JButton button = new JButton("确认");
		button.setBounds(51, 128, 117, 29);
		button.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				classNum=Integer.parseInt(textField.getText()); 
				if(rdbtnNewRadioButton.isSelected()){
					videoNumDef=0;
				}else if(radioButton.isSelected()){
					videoNumDef=1;
				}else if(radioButton_1.isSelected()){
					videoNumDef=2;
				}
				load.start();
			}
			
		});
		JButton button1 = new JButton("停止");
		button1.setBounds(166, 127, 117, 29);
		
		button1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
			
				load.stop();
			}
			
		});
		
		
		contentPane.add(button);
		contentPane.add(button1);
		
		label = new JLabel("课程号：");
		label.setBounds(51, 169, 378, 26);
		contentPane.add(label);
		
		textField_1 = new JLabel();
		textField_1.setText("例如:http://www.imooc.com/learn/616");
		textField_1.setBounds(51, 192, 270, 26);
		contentPane.add(textField_1);
		//textField_1.setColumns(10);
		
		lblNewLabel_1 = new JLabel("那么课程号为:616");
		lblNewLabel_1.setBounds(51, 218, 378, 26);
		contentPane.add(lblNewLabel_1);
		
		lblNewLabel_2 = new JLabel("然后选择要下载的清晰度确认即可。author：xiaoyi");
		lblNewLabel_2.setBounds(51, 244, 341, 16);
		contentPane.add(lblNewLabel_2);
		
		textArea = new JTextArea();
		
		JScrollPane jp=new JScrollPane(textArea);
		textArea.setLineWrap(true);
		jp.setBounds(51, 287, 536, 318);
		contentPane.add(jp);
		
		JButton button_2 = new JButton("清除");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("");
			}
		});
		button_2.setBounds(430, 50, 117, 29);
		contentPane.add(button_2);
		
		JButton button_3 = new JButton("清除");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
		button_3.setBounds(471, 246, 117, 29);
		contentPane.add(button_3);
}
    
    public void loadFile(){
		int curruntCount;
		Document doc = null;

		// 一个课程下载一次即可，标记是否需要下载
		boolean flag;
		String title;
		String savePath;
		JSONObject jsonObject;
		String jsonData;
		JSONArray mpath;
		Document jsonDoc;
		String[] videoNos;
		String videoName;
		String videoNo;

		while (true) {
			curruntCount = 0;
			flag = true;
			int classNo = classNum;
			// 获得要解析的网页文档
			try {
				doc = Jsoup.connect("http://www.imooc.com/learn/" + classNo)
						.get();
			} catch (IOException e) {
				textArea.append("获取课程信息时网络异常！检查网络或者稍候再试 \r\n");
				continue;
			}
			// 获得课程标题：
			title = doc.getElementsByTag("h2").html();

			// 过滤文件夹非法字符
			title = title.replaceAll("[\\\\/:\\*\\?\"<>\\|]", "#");
			savePath = "./download/" + title + "/";
			File file = new File(savePath);

			Elements videos = doc.select(".video a");
			if (title.equals("") && videos.size() == 0) {
				textArea.append("抱歉，没有该课程！\r\n");
				continue;
			}

			// 先进行计算：
			int count = 0;
			for (Element video : videos) {
				videoNos = video.attr("href").split("/");

				// 如果该课程不是视频则不用下载
				if (!videoNos[1].equals("video")) {
					continue;
				}
				count++;
			}
			textArea.append("\r\n要下载的课程标题【" + title + "】，");
			textArea.append("共 " + videos.size() + " 节课程，其中视频课程有 " + count
					+ " 节\r\n");

			int videoDef = videoNumDef;

			textArea.append("\r\n正在下载，请稍候…\r\n");

			// 遍历所有视频
			for (Element video : videos) {
				curruntCount++;
				videoNos = video.attr("href").split("/");

				try {
					GetAttachFile.doGetFile(videoNos[2], title);
				} catch (IOException e) {
					textArea.append("下载课程资料附件时出现异常！\r\n");
				}

				// 控制课程相关信息只获取一次
				if (flag) {
					// 创建课程文件夹
					file.mkdirs();
					// 获得课程信息进行保存
					try {
						GetInfo.doGetInfo(classNo, title);

						textArea.append("course_info.txt   生成成功！\t");
					} catch (Exception e2) {
						textArea.append("生成course_info.txt时出现异常！");

					}

					// 生成course_list.html
					try {
						GetList.doGetList(videos, savePath);
						textArea.append("course_list.html   生成成功！\r\n");
					} catch (Exception e1) {
						textArea.append("生成course_list.html时出现异常！");
					}
					flag = false;
				}

				// 如果该课程不是视频则不用下载
				if (!videoNos[1].equals("video")) {
					continue;
				}

				// 获得视频课程名称并过滤特殊字符
				videoName = video.html()
						.substring(0, video.html().length() - 7).trim();
				videoName = videoName.replaceAll("[\\\\/:\\*\\?\"<>\\|]", "#");
				videoNo = videoNos[2];

				// 获取视频下载地址
				try {
					jsonDoc = Jsoup
							.connect(
									"http://www.imooc.com/course/ajaxmediainfo/?mid="
											+ videoNo + "&mode=flash")
							.timeout(10 * 1000).get();
				} catch (IOException e) {
					textArea.append("【" + curruntCount + "】" + videoName
							+ "\t网络异常，地址获取失败！");
					continue;
				}
				jsonData = jsonDoc.text();
				jsonObject = new JSONObject(jsonData);
				mpath = jsonObject.optJSONObject("data")
						.optJSONObject("result").optJSONArray("mpath");
				String downloadPath = mpath.getString(videoDef).trim();
				// 进行下载
				try {
					DownloadFile.downLoadFromUrl(downloadPath, videoName
							+ ".mp4", savePath);
					textArea.append("【" + curruntCount + "】" + videoName
							+ " \t下载成功！\r\n");
				} catch (IOException e) {
					textArea.append("【" + curruntCount + "】：\t" + videoName
							+ " \t网络异常，下载失败！");
				}

			}

			textArea.append("\n【"
							+ title
							+ "】课程的下载任务已完成！！！\n已下载到该JAR包所在目"
							+ "录download文件夹下。"
							+ "\n----------------------------------------------------------\r\n");
			load.stop();
		}
	}
    public static void main(String[] args){
        new LoadFrameUI().setVisible(true);;
    }
    }

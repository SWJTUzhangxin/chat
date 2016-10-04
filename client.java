import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame 
{
	Socket s = null;          											// 服务器将接收到的sockte传给该量
	
	DataOutputStream dos = null;										// 服务器向外输出的管道
															
	DataInputStream dis = null;											// 向服务器输入的管道			
	
	private boolean bConnected = false;							// 用于判断客户端和服务器是否建立连接

	TextField tfTxt = new TextField();							// 用于聊天的输入框

	TextArea taContent = new TextArea();						// 用于显示聊天内容的文本框
	
	Thread tRecv = new Thread(new RecvThread());    // 线程，用于接收客户端发来消息的线程
	
	/*主函数，主线程*/
	
	public static void main(String[] args) 
	{
		new ChatClient().launchFrame(); 
	}//end main()
	
	/*用于启动一个Frame窗口*/
	
	public void launchFrame() 
	{
		setLocation(400, 300);												// 设置窗口的坐标
		
		this.setSize(300, 300);												// 设置窗口的大小
		
		add(tfTxt, BorderLayout.SOUTH);								// 将聊天输入框添加到窗口中
		
		add(taContent, BorderLayout.NORTH);						// 将聊天显示框添加到布局窗口中
		
		pack();																				// 使布局充满整个Frame
		
		/* 添加对窗口关闭的监听，使点击右上角关闭做出相应，使用了内部类 */
		
		this.addWindowListener
		
		(new WindowAdapter()																					// 窗口适配器 
			{
					public void windowClosing(WindowEvent arg0) 						
					{
						disconnect();																					// 断开连接
						
						System.exit(0);																				// 客户端退出
					}
			
			}
		);
		
		tfTxt.addActionListener(new TFListener());		// 添加对输入字符按回车键的监听，当按回车键后				
		
		setVisible(true);															// 设置窗口为可见
		
		connect();																		// 建立连接
		
		tRecv.start();																// 开启一个线程，用于向服务器发送数据
	}// end launchFrame () 
	
	/* 建立服务器和客户端的连接 */
	
	public void connect() 
	{
		try 
		{
			s = new Socket("127.0.0.1", 6666);													// 建立一个Socket对象，并连接到相应服务器的某一端口
			
			dos = new DataOutputStream(s.getOutputStream());						// 得到该Socket的输出流管道，并用数据输出流进行包装
			
			dis = new DataInputStream(s.getInputStream());							// 得到该Socket的输入流管道，并用数据输入流进行包装
			
			System.out.println("connected!");														// 输出一个提示性内容，表示已经连接到服务器
			
			bConnected = true;																					// 表示连接到服务器
		} 
			catch (UnknownHostException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		
	}// end connect()
	
	/* 断开连接的方法，在windowsclosing()中调用,以确保在客户端退出时，所有管道和socket已经关闭 */
		
	public void disconnect()
	{
		try 
		{
			dos.close();															  // 输出端管道关闭
			
			dis.close();																// 输入端管道关闭
			
			s.close();																	// socket关闭
		} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
	}// end disconnect()
	
	/*对输入框中回车键的监听*/
	
	private class TFListener implements ActionListener 
	{
			public void actionPerformed(ActionEvent e) 
			{
			
				String str = tfTxt.getText().trim();				// 将聊天输入框中的字符串去掉空格存放到字符串中
			
				tfTxt.setText("");													// 将聊天输入框清空
			
				try 
				{
					dos.writeUTF(str);												//用客户端的输出管道向服务器端写字符
				
					dos.flush();															//写完后清空缓存
				
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();				
				}
			
			}
		
	}//end class TFListener 
	
	/* 该线程用于接收服务器端发来的数据 */
	
	private class RecvThread implements Runnable 
	{
			public void run() 
			{
				try 
				{  /*当连接上以后*/
					while(bConnected) 																			
					{
						String str = dis.readUTF();														// 输入管道读入从客户端发送过来的消息，并将字符存放到字符串中
					
						taContent.setText(taContent.getText() + str + '\n');	// 聊天显示窗口要显示之前的内容和新加入的内容被分行显示
					}
				} 
					catch (SocketException e) 
					{
						System.out.println("退出了，bye!");
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					} 
			
			}
		}// end class RecvThread
}// end class ChatClient

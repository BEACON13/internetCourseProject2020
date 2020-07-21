package src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;


public class ReceiveLetter {
	
	private int port=110;//POP3端口
	private String server;
	private String account;
	private String password;
	
	private Socket socket;
	private BufferedReader socketIn;
	private DataOutputStream socketOut;
	
	private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private static PrintWriter stdOut = new PrintWriter(System.out,true);
	private static PrintWriter stdErr = new PrintWriter(System.err,true);
	
	public ReceiveLetter(String server, String account, String password) 
	{
		this.server = server;
		this.account = account;
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	//初始化端口和输入输出流
	private boolean initialSocket()
	{
		boolean flag=true;
		
		if(server==null)
		{
			return false;
		}
		
		try 
		{
			socket=new Socket(server,port);
			socketIn=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketOut=new DataOutputStream(socket.getOutputStream());
		}
		catch(UnknownHostException e)
		{
			stdOut.println("未知主机错误");
			e.printStackTrace();
			flag=false;
		}
		catch (IOException e) {
			stdOut.println("IO错误");
			e.printStackTrace();
			flag=false;
		}
		return flag;
	}
	
	private boolean getMail()
	{
		try {
			String response;
			response=socketIn.readLine();
			
			stdOut.println(response);
			
			//输入用户名
			socketOut.writeBytes("user "+account+"\r\n");
			response=socketIn.readLine();
			//stdOut.println(response);
			
			//输入密码
			socketOut.writeBytes("pass "+password+"\r\n");
			response=socketIn.readLine();
			//stdOut.println(response);
			
			//要求列出邮件
			socketOut.writeBytes("stat\r\n");
			response=socketIn.readLine();
			//stdOut.println(response);
		
			//这里获得邮件总数
			StringTokenizer stringTokenizer=new StringTokenizer(response," ");
			stringTokenizer.nextToken();
			int num=Integer.parseInt(stringTokenizer.nextToken());
			
			//展示每一封邮件
			for (int i = 1; i <= num; i++) 
			{
				stdOut.println("第"+i+"封邮件内容");
				socketOut.writeBytes("retr "+i+"\r\n");
				
				while (true) 
				{
					String line=socketIn.readLine();
					stdOut.println(line);
					
					if(line.equals("."))
					{
						break;
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  true;
	}
	
	public boolean run()
	{
		initialSocket();
		getMail();
		return true;
	}

}

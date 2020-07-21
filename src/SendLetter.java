package src;

import java.net.*;
import java.util.*;
import java.io.*;

public class SendLetter {
	
	private String sender;//发送方账户
	private String receiver;//接收方账户
	private String password;//发送账户密码
	private int port = 25;//SMTP使用端口25
	private String mailServer;//邮箱服务器
	private String content;//信件内容
	
	//控制台输入输出
	private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private static PrintWriter stdOut = new PrintWriter(System.out,true);
	private static PrintWriter stdErr = new PrintWriter(System.err,true);
	
	//socket
	private Socket socket;
	private BufferedReader socketIn;
	private DataOutputStream socketOut;
	
	public SendLetter(String sender, String receiver, String password, String mailServer) {
		
		this.sender = sender;
		this.receiver = receiver;
		this.password = password;
		this.mailServer = mailServer;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMailServer() {
		return mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	//初始化端口和输入输出流
	private boolean initialSocket()
	{
		boolean flag=true;
		
		if(mailServer==null)
		{
			return false;
		}
		
		try 
		{
			socket=new Socket(mailServer,port);
			socketIn=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketOut=new DataOutputStream(socket.getOutputStream());
			
			String result=socketIn.readLine();
			if(!result.startsWith("220"))
			{
				flag=false;
				stdOut.println("连接出错"+result);
			}
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
	
	//发送指令、信息 返回返回值 将socket操作封装在这里
	private String sendAndGetResponse(String message)
	{
		String result=null;
		try 
		{
			socketOut.writeBytes(message);
			socketOut.flush();
			result=socketIn.readLine();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean sendMail()
	{
		//如果初始化socket失败
		if(!initialSocket())
		{
			stdErr.print("1");
			return false;
		}
		
		if(sender.isEmpty()||receiver.isEmpty()||password.isEmpty())
		{
			stdErr.println("邮件发送信息未填写");
			return false;
		}
		
		String response;
		
		//标识用户建立连接
		response=sendAndGetResponse("HELO "+mailServer+"\r\n");//向服务器标识用户身份，返回邮件服务器身份
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("2\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//如果是qq邮箱这里还会返回2行 都读掉
		//如果是163邮箱 这里不用读掉
		
		if (sender.contains("@qq.com")) 
		{
			try {
				response=socketIn.readLine();
				//stdErr.println("2-2\t"+response);
				response=socketIn.readLine();
				//stdErr.println("2-3\t"+response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		//接下来开始登录操作
		
		//auth login 
		
		response=sendAndGetResponse("AUTH LOGIN\r\n");//请求验证身份
		
		if(!response.startsWith("334"))//334：服务器响应验证Base64字符串 后面的字段表示请求用户名
		{
			stdErr.print("3\t");
			stdErr.println("Response:"+response);
			return false;
		}
		

		//获取base64 encoder 用户名和密码需要使用它来加密
		Base64.Encoder encoder=Base64.getEncoder();
		
		//发送用户名 采用base64加密
		response=sendAndGetResponse(encoder.encodeToString(sender.getBytes())+"\r\n");
		
		if(!response.startsWith("334"))//334：服务器响应验证Base64字符串 后面的字段表示请求密码
		{
			stdErr.print("4\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		
		//发送密码 采用base64加密
		response=sendAndGetResponse(encoder.encodeToString(password.getBytes())+"\r\n");
		
		if(!response.startsWith("235"))//235：验证成功
		{
			stdErr.print("5\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//发件人
		response=sendAndGetResponse("mail from:<"+sender+">"+"\r\n");
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("6\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//收件人
		response=sendAndGetResponse("rcpt to:<"+receiver+">\r\n");
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("7\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		response=sendAndGetResponse("data\r\n");
		
		if(!response.startsWith("354"))//354:开始邮件输入
		{
			stdErr.print("8\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//String jiema = URLEncoder.encode("中国"，"UTF-8");
		
		String data="";
		data+="From:<" + sender + ">\r\n";
		data+="To:<" + receiver + ">\r\n";
		data+="Subject:test\r\n";
		data+="Content-Type:text/plain;charset=\"UTF-8\"\r\n";
		data+="\r\n";
		//data+=jiema;
		data+=content;
		data+="\r\n.\r\n";
		
		response=sendAndGetResponse(data);
		
		if(!response.startsWith("250"))//250:要求的邮件操作完成
		{
			stdErr.print("9\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		response=sendAndGetResponse("QUIT \r\n");
		
		if(!response.startsWith("221"))//221:服务关闭传输信道
		{
			stdErr.print("10\t");
			stdErr.println("Response:"+response);
			return false;
		}
		
		//关闭socket
		try
		{
			socketIn.close();
			socketOut.close();
			socket.close();
			
		} catch (IOException e) 
		{
			stdErr.println("关闭socket出错");
			e.printStackTrace();
		}

		return true;
	}
	
	public boolean run ()
	{
		stdOut.println("请输入邮件内容：");
		try 
		{
			this.content=stdIn.readLine();
		} 
		catch (IOException e) 
		{
			stdErr.println("输入邮件内容出错");
			e.printStackTrace();
		}
		
		return sendMail();
	
	}
}


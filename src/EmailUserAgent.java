package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;


public class EmailUserAgent {
	
	private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private static PrintWriter stdOut = new PrintWriter(System.out,true);
	private static PrintWriter stdErr = new PrintWriter(System.err,true);
	
	public boolean send()
	{
		//String sender="courseprojectnz@163.com";
		//String password="ONAZIYOWBWDWQEBT";
				
		//String sender="2458452428@qq.com";
		//String password="uqcdtxwspkmgdiah";
		
		//String receiver="courseprojectnz@163.com";
		//String receiver="2458452428@qq.com";
		
		String sender=null;
		String password=null;
		String receiver=null;
		String mailServer=null;
		
		try
		{
			boolean flag=false;
			while(!flag)
			{
				stdOut.println("请输入您的邮箱账户：");
				sender=stdIn.readLine();
				
				//本程序支持的qq和163邮箱的smtp服务器均为smtp.xx.com形式
				//因此选择从sender中截取获得smtp服务器
				StringTokenizer st=new StringTokenizer(sender,"@");
				if (st.countTokens()==2) 
				{
					st.nextToken();
					mailServer="smtp."+st.nextToken();
					flag=true;
				}else {
					stdErr.println("输入的邮箱有误");
				}
			}
			
			stdOut.println("请输入您的密码（授权码）");
			password=stdIn.readLine();
			
			stdOut.println("请输入收信方的邮箱");
			receiver=stdIn.readLine();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		SendLetter letter=new SendLetter(sender, receiver, password, mailServer);
		if(letter.run())
		{
			stdOut.println("发送成功");
			return true;
		}else {
			stdErr.println("发送出错");
			return false;
		}	
	}
	
	public boolean receive()
	{
		//String account="courseprojectnz@163.com";
		//String password="ONAZIYOWBWDWQEBT";
		
		//String account="2458452428@qq.com";
		//String password="uqcdtxwspkmgdiah";
		
		String server=null;
		String account=null;
		String password=null;
		
		try {
			boolean flag=false;
			while(!flag)
			{
				stdOut.println("请输入您的邮箱账户：");
				account=stdIn.readLine();
				
				//本程序支持的qq和163邮箱的pop服务器均为pop.xx.com形式
				//因此选择从account中截取获得pop服务器
				StringTokenizer st=new StringTokenizer(account,"@");
				if (st.countTokens()==2) 
				{
					st.nextToken();
					server="pop."+st.nextToken();
					flag=true;
				}else {
					stdErr.println("输入的邮箱有误");
				}
			}
			
			stdOut.println("请输入您的密码（授权码）");
			password=stdIn.readLine();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ReceiveLetter r=new ReceiveLetter(server, account, password);
		stdOut.println();
		stdOut.println("读取完成");
		return r.run();
	}
	
	public static void main(String[] args)
	{
		
		EmailUserAgent emailUserAgent=new EmailUserAgent();
		
		String choice="1";		
		while(!choice.equals("0")) 
		{
			stdOut.println();
			stdOut.println("------------------------------");
			stdOut.println("         请选择服务");
			stdOut.println("         1.发送邮件");
			stdOut.println("         2.收取邮件");
			stdOut.println("         0.退出");
			stdOut.println("------------------------------");
		
			try {
				choice=stdIn.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (choice.equals("1")) 
			{
				emailUserAgent.send();
			}
			else if(choice.equals("2"))
			{
				emailUserAgent.receive();
			}
			
		}
	}
}

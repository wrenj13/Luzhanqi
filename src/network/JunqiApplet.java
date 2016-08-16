package network;

import javax.swing.JApplet;

public class JunqiApplet extends JApplet {
	
	public void init() 
	{
		JunqiClient client = new JunqiClient();
		client.run();
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void destroy()
	{
		
	}
	
	public static void main(String[] args)
	{
		JunqiApplet a = new JunqiApplet();
		a.init();
	}
}

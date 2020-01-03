package fr.devsylone.fallenkingdom.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.exception.FkLightException;

public abstract class ServerSocket extends Thread
{
	protected boolean started;
	protected int port;
	protected String pass;
	protected Fk plugin;
	protected PrintWriter writer;
	protected BufferedReader reader;

	protected Socket client;
	protected java.net.ServerSocket server;

	protected List<String> FkPIArray;

	protected boolean fkpiReady;

	public ServerSocket() {
		started = true;
		port = Fk.getInstance().getConfig().getInt("Application.Port");
		pass = Fk.getInstance().getConfig().getString("Application.Password");
	}
	
	@Override
	public void interrupt()
	{
		super.interrupt();
		started = false;
		try
		{
			if(client != null)
			{
				writer.println("disconnect");
				writer.flush();
				client.close();
			}

			if(server != null)
				server.close();

		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public List<String> getFkPIArray()
	{
		if(!fkpiReady)
			throw new FkLightException("Il n'y a pas de configuration récemment envoyée par le logiciel");

		return FkPIArray;
	}

	public void clearFkPIArray()
	{
		fkpiReady = false;
		FkPIArray = null;
	}
	
	protected void log(String msg)
	{
		Fk.getInstance().getLogger().info("[" + this.getClass().getSimpleName() + "] " + msg);
	}
}

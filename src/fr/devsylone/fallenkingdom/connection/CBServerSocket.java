package fr.devsylone.fallenkingdom.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import fr.devsylone.fallenkingdom.Fk;
import fr.devsylone.fallenkingdom.utils.ChatUtils;

public class CBServerSocket extends ServerSocket
{
	@Override
	public void run()
	{
		try
		{
			server = new java.net.ServerSocket(port);
			log("Server listen [Port=" + server.getLocalPort() + "]");
		}catch(IOException e1)
		{
			e1.printStackTrace();
			started = false;
		}

		while(started)
		{
			try
			{
				client = server.accept();

				log("Connection from " + client.getLocalAddress().getHostAddress());

				writer = new PrintWriter(client.getOutputStream());
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

				String line = reader.readLine();

				if(line.startsWith("PASSWORD/") && line.split("/")[1].equals(pass))
				{
					writer.println("ACCEPT_CONNECTION");
					writer.flush();
					log("Connection accepted");
				}

				else
				{
					writer.println("DENY_CONNECTION");
					writer.flush();
					client.close();
					Fk.getInstance().getLogger().warning("[ServerSocket] Connection denied (Wrong password)");
				}

				while(!client.isClosed())
				{
					line = reader.readLine();
					if(line == null)
						throw new Exception("Connection reset");
					if(FkPIArray != null)
					{
						if(line.equals("END"))
						{
							log("Finished");
							fkpiReady = true;

							String msg = "";
							msg += ChatUtils.PREFIX + ChatColor.RED + "§m-------------------------------------\n";
							msg += ChatUtils.PREFIX + ChatColor.GOLD + "Un logiciel de configuration de Fk tente\n";
							msg += ChatUtils.PREFIX + ChatColor.GOLD + "d'installer une configuration sur\n";
							msg += ChatUtils.PREFIX + ChatColor.GOLD + "ce serveur.\n";
							msg += ChatUtils.PREFIX + ChatColor.GOLD + "Cela stoppera la partie en cours et\n";
							msg += ChatUtils.PREFIX + ChatColor.GOLD + "écrasera toutes les options definies.\n";
							msg += ChatUtils.PREFIX + ChatColor.GOLD + "\n";
							msg += ChatUtils.PREFIX+ ChatColor.DARK_GREEN + "/fk game AcceptApp";
							msg += ChatUtils.PREFIX+ ChatColor.DARK_RED + "/fk game DenyApp";

							Bukkit.broadcastMessage(msg);

							client.close();
						}

						else
							FkPIArray.add(line);
					}

					else if(line.equals("START_FKPI"))
						if(FkPIArray != null)
						{
							writer.println("Already reading an FkPI !");
							writer.flush();
						}

						else
						{
							FkPIArray = new ArrayList<String>();
							log("Reading...");
						}
				}

			}catch(Exception e)
			{
				if(e.getLocalizedMessage() != null)
				{
					if(e.getLocalizedMessage().equals("Connection reset") || (client != null && client.isConnected()))
						log("Client (" + client.getLocalAddress().getHostAddress() + ") has been disconnected");

					else if(e.getLocalizedMessage().equals("Socket closed"))
						log("Server closed");
				}

				else
					e.printStackTrace();
			}
		}
	}

}

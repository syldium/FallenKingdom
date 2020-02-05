package fr.devsylone.fkpi.teams;

import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fallenkingdom.utils.XMaterial;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fkpi.util.Saveable;

/**
 * Cette classe Base reprèsente la base d'une Team.
 * @see Team
 * @author Etrenak, Fabulacraft
 */
public class Base implements Saveable
{
	/**
	 * Equipe à qui appartient la base.
	 */
	private Team team;

	/**
	 * Centre de la base.
	 */
	private Location center;

	/**
	 * Point de téléportation lors du démarrage de la partie.
	 */
	private Location tp;

	/**
	 * Rayon de la base.
	 */
	private int radius;

	/**
	 * <p>
	 * Matière dans laquelle sera construite la première couche de la muraille.
	 * </p>
	 * <b>Note : </b> Si le Material fourni dans le constructeur n'existe pas, alors celui-ci sera de l'air
	 */
	private Material material;

	/**
	 * <p>
	 * Data du bloc dont sera construite la première couche de la muraille.
	 * </p>
	 */
	private byte data;

	private ChestsRoom chestRoom;

	/**
	 * <b>Note : </b>Le champs {@link fr.devsylone.fkpi.teams.Base#tp} est instancié en fonction du centre donné.
	 * @param team L'équipe à qui appartient la base.
	 * @param center Centre de la base.
	 * @param radius Rayon de la base.
	 * @param material Matière dans laquelle sera construite la première couche de la muraille, si null ou n'existe pas <br>
	 *        il sera remplacé par de l'air.
	 */
	public Base(Team team, Location center, int radius, Material material, byte data)
	{
		this.center = adjustLoc(center);
		this.radius = radius;
		this.team = team;
		this.material = material;
		this.data = data;
		this.chestRoom = new ChestsRoom(this);

		/*
		 * Ajustement de la Tp sur l'axe Z
		 * Et PAS sur l'axe X
		 */
		if(center != null)
			tp = getCenter().add(0, 1, 1);
	}

	/**
	 * Repère si la Location se trouve dans la base ou non, sans prendre en compte l'axe y (hauteur).
	 * @param loc Location à vérifier.
	 * @param lag Le nombre à ajouter au rayon de la base
	 * @return
	 * 	- <b>true</b> Si la Location est à l'intérieur de la base.<br>
	 *         - <b>false</b> Dans le cas contraire.
	 */

	public boolean contains(Location loc, int lag)
	{
		return loc.getBlockX() >= center.getBlockX() - (radius + lag) && loc.getBlockX() <= (center.getBlockX() + radius + lag) && loc.getBlockZ() >= center.getBlockZ() - (radius + lag) && loc.getBlockZ() <= center.getBlockZ() + radius + lag && loc.getWorld() == center.getWorld();
	}

	public boolean contains(Location loc)
	{
		return contains(loc, 0);
	}

	/**
	 * Renvoie le point de téléportation de la base.
	 * @return Le point de téléportation de la base.
	 */

	public Location getTpPoint()
	{
		return tp.clone();
	}

	/**
	 * Renvoie le centre de la base.
	 * @return Le centre de la base.
	 */

	public Location getCenter()
	{
		return center.clone();
	}

	public ChestsRoom getChestsRoom()
	{
		return chestRoom;
	}
	
	public void resetChestoom()
	{
		chestRoom = new ChestsRoom(this);
	}
	
	public Team getTeam()
	{
		return team;
	}

	/**
	 * Construit la base en jeu.
	 */
	public void construct()
	{
		adjustLoc(center);

		Location loc = new Location(center.getWorld(), 0, center.getBlockY(), 0);

		for(int i = 0; i < 2 * radius; i++)
		{
			loc.setX(center.getBlockX() + radius - i);
			loc.setZ(center.getBlockZ() + radius);

			adjustLoc(loc);
			loc.getBlock().setType(material);
			XBlock.setData(loc.getBlock(), data);

			loc.setX(center.getBlockX() - radius + i);
			loc.setZ(center.getBlockZ() - radius);

			adjustLoc(loc);
			loc.getBlock().setType(material);
			XBlock.setData(loc.getBlock(), data);

			loc.setX(center.getBlockX() - radius);
			loc.setZ(center.getBlockZ() + radius - i);

			adjustLoc(loc);
			loc.getBlock().setType(material);
			XBlock.setData(loc.getBlock(), data);

			loc.setX(center.getBlockX() + radius);
			loc.setZ(center.getBlockZ() - radius + i);

			adjustLoc(loc);
			loc.getBlock().setType(material);
			XBlock.setData(loc.getBlock(), data);
		}

		/*
		 * Vus de face :
		 * - Sol
		 * Air
		 * I Barrière
		 * O Laine
		 * & Index
		 * <- désigne où se trouve l'index lorsqu'il est dans un bloc
		 */

		Location index = center.clone();

		for(int i = 0; i < 3; i++)
		{
			index.getBlock().setType(XMaterial.OAK_FENCE.parseMaterial());
			index.add(0, 1, 0);
		}

		/*
		 *       I <-
		 *       I
		 *       I
		 * - - - - - - -
		 */

		index.add(1, -2, 0);

		/*
		 *       I
		 *       I
		 *       I &
		 * - - - - - - -
		 */

		for(int i = 0; i < 2; i++)
		{
			for(int k = 0; k < 2; k++)
			{
				/*
				 * On place une laine et on change la couleur
				 * en fonction de la couleur de l'equipe.
				 */
				index.getBlock().setType(XMaterial.WHITE_WOOL.parseMaterial());
				XBlock.setColor(index.getBlock(), team.getColor().getDyeColor());

				index.add(0, 1, 0);
			}
			index.add(-2, -2, 0);
		}
		/*
		 *      O I O
		 * &   O I O
		 *          I
		 * - - - - - - -
		 */
	}

	/**
	 * <b><u>Méthode privée</u></b> <br>
	 * Modifie la Location de manière à ce quelle ne soit pas dans un bloc plein.
	 * @param loc La Location à modifier.
	 * @return La Location modifiée.
	 */
	@SuppressWarnings("deprecation")
	private Location adjustLoc(Location loc)
	{
	    List<Material> lsBlocks = new ArrayList<>();
	    lsBlocks.add(Material.AIR);
	    lsBlocks.add(Material.LEGACY_RED_ROSE);
	    lsBlocks.add(Material.LEGACY_YELLOW_FLOWER);
	    lsBlocks.add(Material.LEGACY_DOUBLE_PLANT);
	    lsBlocks.add(Material.LEGACY_LONG_GRASS);
	    lsBlocks.add(Material.SNOW);
	    lsBlocks.add(Material.OAK_FENCE);
	    while (lsBlocks.contains(loc.getBlock().getType()))
	      loc.add(0.0D, -1.0D, 0.0D); 
	    while (!lsBlocks.contains(loc.getBlock().getType()))
	      loc.add(0.0D, 1.0D, 0.0D); 
	    return loc;
	}

	@Override
	public void load(ConfigurationSection config)
	{
		center = new Location(Bukkit.getWorld(config.getString("Center.World")), config.getInt("Center.X"), config.getInt("Center.Y"), config.getInt("Center.Z"));
		tp = getCenter().clone().add(0, 1, 1);
		material = Material.matchMaterial(config.getString("Material"));
		radius = config.getInt("Radius");

		if(config.isConfigurationSection("ChestsRoom"))
			chestRoom.load(config.getConfigurationSection("ChestsRoom"));
	}

	@Override
	public void save(ConfigurationSection config)
	{
		config.set("Center.World", center.getWorld().getName());
		config.set("Center.X", center.getBlockX());
		config.set("Center.Y", center.getBlockY());
		config.set("Center.Z", center.getBlockZ());
		config.set("Material", material.name());
		config.set("Radius", radius);

		chestRoom.save(config.createSection("ChestsRoom"));
	}
}

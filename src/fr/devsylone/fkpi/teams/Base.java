package fr.devsylone.fkpi.teams;

import com.cryptomorin.xseries.XMaterial;
import fr.devsylone.fkpi.FkPI;
import fr.devsylone.fkpi.rules.Rule;
import fr.devsylone.fkpi.util.OutlineSquareIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import fr.devsylone.fallenkingdom.utils.XBlock;
import fr.devsylone.fkpi.util.Saveable;

import java.util.Iterator;
import java.util.Objects;

import static fr.devsylone.fallenkingdom.version.Environment.getMinHeight;

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

	private int minX, minZ, maxX, maxZ;

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

		updateMinMaxLoc();
	}

	private void updateMinMaxLoc()
	{
		if (center == null) return;
		this.minX = center.getBlockX() - radius;
		this.minZ = center.getBlockZ() - radius;
		this.maxX = center.getBlockX() + radius;
		this.maxZ = center.getBlockZ() + radius;
	}

	/**
	 * Teste si le point avec ces coordonnées se trouve dans la base.
	 *
	 * @param world Le monde où ce point est situé.
	 * @param x La coordonnée X.
	 * @param y La coordonnée Y.
	 * @param z La coordonnée Z.
	 * @param lag Le nombre à ajouter au rayon de la base.
	 * @return {@code true} Si le point est à l'intérieur de la base, {@code false} sinon.
	 */
	public boolean contains(World world, int x, int y, int z, int lag)
	{
		int verticalLimit = FkPI.getInstance().getRulesManager().getRule(Rule.VERTICAL_LIMIT);
		if (verticalLimit > 0 && Math.abs(y - center.getBlockY()) > verticalLimit) {
			return false;
		}
		return x >= minX - lag && x <= maxX + lag
				&& z >= minZ - lag && z <= maxZ + lag
				&& Objects.equals(world, center.getWorld());
	}

	public boolean contains(World world, int x, int y, int z)
	{
		return contains(world, x, y, z, 0);
	}

	/**
	 * Teste si cet endroit se trouve dans la base.
	 *
	 * @param loc L'endroit à tester.
	 * @param lag Le nombre à ajouter au rayon de la base.
	 * @return {@code true} Si l'endroit est à l'intérieur de la base, {@code false} sinon.
	 */
	public boolean contains(Location loc, int lag)
	{
		return contains(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), lag);
	}

	public boolean contains(Location loc)
	{
		return contains(loc, 0);
	}

	/**
	 * Teste si ce bloc se trouve dans la base.
	 *
	 * @param block Le bloc à tester.
	 * @param lag Le nombre à ajouter au rayon de la base.
	 * @return {@code true} Si le bloc est à l'intérieur de la base, {@code false} sinon.
	 */
	public boolean contains(Block block, int lag)
	{
		return contains(block.getWorld(), block.getX(), block.getY(), block.getZ(), lag);
	}

	public boolean contains(Block block)
	{
		return contains(block, 0);
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
	
	public void resetChestRoom()
	{
		chestRoom = new ChestsRoom(this);
	}
	
	public Team getTeam()
	{
		return team;
	}

	public int getRadius()
	{
		return radius;
	}

	public void constructBorder() {
		if (material == Material.AIR) {
			return;
		}

		Iterator<Location> iterator = outlineIterator();
		Location location;
		while (iterator.hasNext()) {
			location = iterator.next();
			adjustLoc(location);
			location.getBlock().setType(material);
			XBlock.setData(location.getBlock(), data);
		}
	}

	public OutlineSquareIterator outlineIterator() {
		return new OutlineSquareIterator(center.getBlockX() - radius, center.getBlockY(), center.getBlockZ() - radius, radius * 2, center.getWorld());
	}

	public boolean isLoaded() {
		World world = center.getWorld();
		if (world == null) {
			return false;
		}

		for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x += 16) {
			for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z += 16) {
				if (!world.isChunkLoaded(x >> 4, z >> 4)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Construit la base en jeu.
	 */
	public void construct()
	{
		constructFlag();
		constructBorder();
	}

	public void constructFlag() {
		adjustLoc(center);

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
	private Location adjustLoc(Location loc)
	{
		if(loc == null)
			return null;

		World world = loc.getWorld();
		if(world == null)
			return loc;

		while(XBlock.isReplaceable(loc.getBlock()) && loc.getBlockY() > getMinHeight(world) + 1)
			loc.add(0, -1, 0);
		
		while(!XBlock.isReplaceable(loc.getBlock()))
			loc.add(0, 1, 0);

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
		updateMinMaxLoc();
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

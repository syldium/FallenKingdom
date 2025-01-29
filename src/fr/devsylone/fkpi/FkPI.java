package fr.devsylone.fkpi;

import fr.devsylone.fkpi.managers.ChestsRoomsManager;
import fr.devsylone.fkpi.managers.LockedChestsManager;
import fr.devsylone.fkpi.managers.RulesManager;
import fr.devsylone.fkpi.managers.TeamManager;
import fr.devsylone.fkpi.teams.Team;
import fr.devsylone.fkpi.util.Saveable;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

public class FkPI implements Saveable
{
	@Getter private TeamManager teamManager = new TeamManager();
    @Getter private RulesManager rulesManager = new RulesManager();
    @Getter private LockedChestsManager lockedChestsManager = new LockedChestsManager();
    @Getter private ChestsRoomsManager chestsRoomsManager = new ChestsRoomsManager();

    private static FkPI instance;

    public static FkPI getInstance()
    {
        return instance;
    }

    public FkPI()
    {
        instance = this;
    }

    public void reset()
    {
        teardown();
        for(Team team : teamManager.getTeams())
        {
            if(team.getBase() != null)
                team.getBase().getNexus().remove();
        }
        teamManager = new TeamManager();
        rulesManager = new RulesManager();
        lockedChestsManager = new LockedChestsManager();
        chestsRoomsManager = new ChestsRoomsManager();
    }

    public void teardown()
    {
        teamManager.teardown();
    }

    @Override
    public void load(ConfigurationSection config)
    {
        chestsRoomsManager.loadNullable(config.getConfigurationSection("ChestsRoomsManager")); //AVANT TEAMMANAGER
        rulesManager.loadNullable(config.getConfigurationSection("RulesManager"));
        teamManager.loadNullable(config.getConfigurationSection("TeamManager"));
        lockedChestsManager.loadNullable(config.getConfigurationSection("LockedChestsManager"));
    }

    @Override
    public void loadNullable(ConfigurationSection config)
    {
        load(config);
    }

    @Override
    public void save(ConfigurationSection config)
    {
        rulesManager.save(config.createSection("RulesManager"));
        teamManager.save(config.createSection("TeamManager"));
        lockedChestsManager.save(config.createSection("LockedChestsManager"));
        chestsRoomsManager.save(config.createSection("ChestsRoomsManager"));
    }
}

package fr.devsylone.fallenkingdom;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import fr.devsylone.fkpi.teams.Team;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemStack;

public class MockUtils {

    private static PlayerMock playerMock;
    private static FkMock mockPlugin;
    private static Team blueTeam;

    public static ServerMock getServerMockSafe() {
        if (MockBukkit.isMocked()) {
            return MockBukkit.getMock();
        }
        ServerMock serverMock = MockBukkit.mock();
        serverMock.addSimpleWorld("normal");
        serverMock.addSimpleWorld("nether").setEnvironment(Environment.NETHER);
        return serverMock;
    }

    public static PlayerMock getDefaultPlayer() {
        return getServerMockSafe().addPlayer();
    }

    public static PlayerMock getConstantPlayer() {
        if (playerMock == null) {
            playerMock = getServerMockSafe().addPlayer(RandomStringUtils.random(6, true, true));
            playerMock.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }
        return playerMock;
    }

    public static FkMock getPluginMockSafe() {
        if (mockPlugin == null) {
            getServerMockSafe();
            MockUtils.mockPlugin = MockBukkit.load(FkMock.class);
        }
        return mockPlugin;
    }

    public static Team getBlueTeam() {
        if (blueTeam == null) {
            MockUtils.blueTeam = MockUtils.mockPlugin.fkPI.getTeamManager().getTeam("blue");
            if (blueTeam == null) {
                MockUtils.mockPlugin.fkPI.getTeamManager().createTeam("blue");
                return getBlueTeam();
            }
        }
        return blueTeam;
    }
}

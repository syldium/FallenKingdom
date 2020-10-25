package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArgumentParserTest {

    public ArgumentParserTest() {
        MockUtils.getPluginMockSafe();
    }

    @Test
    public void parseBoolean() {
        Assert.assertTrue(ArgumentParser.parseBoolean("TRUE", Messages.CMD_ERROR_BOOL_FORMAT));
        Assert.assertFalse(ArgumentParser.parseBoolean("fAlSe", Messages.CMD_ERROR_BOOL_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBoolean("", Messages.CMD_ERROR_BOOL_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBoolean("aRandomValue", Messages.CMD_ERROR_BOOL_FORMAT));
    }

    @Test
    public void parseBlock_ValidTypes() {
        Assert.assertEquals(Material.COBBLESTONE.name(), ArgumentParser.parseBlock("minecraft:COBBLESTONE").getBlockName());
        Assert.assertEquals(Material.MOSSY_COBBLESTONE_STAIRS.name(), ArgumentParser.parseBlock("mossy_cobblestone_stairs").getBlockName());
        MockUtils.getConstantPlayer().setItemInHand(new ItemStack(Material.STONE));
        Assert.assertEquals(Material.COAL_BLOCK.name(), ArgumentParser.parseBlock(0, Collections.singletonList("coal_block"), MockUtils.getConstantPlayer(), true).getBlockName());
        Assert.assertEquals(Material.STONE.name(), ArgumentParser.parseBlock(0, Collections.emptyList(), MockUtils.getConstantPlayer(), true).getBlockName());
        MockUtils.getConstantPlayer().setItemInHand(new ItemStack(Material.AIR));
        Assert.assertEquals(Material.AIR.name(), ArgumentParser.parseBlock(0, Collections.emptyList(), MockUtils.getConstantPlayer(), false).getBlockName());
    }

    @Test
    public void parseBlock_InvalidTypes() {
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBlock("obvious"));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBlock("coal"));
        MockUtils.getConstantPlayer().setItemInHand(new ItemStack(Material.AIR));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBlock(0, Collections.emptyList(), MockUtils.getConstantPlayer(), true));
    }

    @Test
    public void parseInt() {
        Assert.assertEquals(9, ArgumentParser.parseInt("9", Messages.CMD_ERROR_NAN));
        Assert.assertEquals(-37, ArgumentParser.parseInt("-37", Messages.CMD_ERROR_NAN));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBlock("two"));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBlock("1.6"));
    }

    @Test
    public void parsePositiveInt() {
        Assert.assertEquals(14, ArgumentParser.parsePositiveInt("14", true, Messages.CMD_ERROR_POSITIVE_INT));
        Assert.assertEquals(0, ArgumentParser.parsePositiveInt("0", true, Messages.CMD_ERROR_NAN));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePositiveInt("0", false, Messages.CMD_ERROR_POSITIVE_INT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePositiveInt("-6", false, Messages.CMD_ERROR_POSITIVE_INT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePositiveInt("wait", false, Messages.CMD_ERROR_POSITIVE_INT));
    }

    @Test
    public void parsePercentage() {
        Assert.assertEquals(100, ArgumentParser.parsePercentage("100", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        Assert.assertEquals(58, ArgumentParser.parsePercentage("58", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        Assert.assertEquals(0, ArgumentParser.parsePercentage("0", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePercentage("", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePercentage("101", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePercentage("-1", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
    }
}
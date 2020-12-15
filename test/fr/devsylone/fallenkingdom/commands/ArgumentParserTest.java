package fr.devsylone.fallenkingdom.commands;

import fr.devsylone.fallenkingdom.MockUtils;
import fr.devsylone.fallenkingdom.exception.ArgumentParseException;
import fr.devsylone.fallenkingdom.utils.Messages;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgumentParserTest {

    public ArgumentParserTest() {
        MockUtils.getPluginMockSafe();
    }

    @Test
    public void parseBoolean() {
        assertTrue(ArgumentParser.parseBoolean("TRUE", Messages.CMD_ERROR_BOOL_FORMAT));
        assertFalse(ArgumentParser.parseBoolean("fAlSe", Messages.CMD_ERROR_BOOL_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBoolean("", Messages.CMD_ERROR_BOOL_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBoolean("aRandomValue", Messages.CMD_ERROR_BOOL_FORMAT));
    }

    @Test
    public void parseBlock_ValidTypes() {
        assertEquals(Material.COBBLESTONE, ArgumentParser.parseBlock("minecraft:COBBLESTONE").getMaterial());
        assertEquals(Material.MOSSY_COBBLESTONE_STAIRS, ArgumentParser.parseBlock("mossy_cobblestone_stairs").getMaterial());
        MockUtils.getConstantPlayer().setItemInHand(new ItemStack(Material.STONE));
        assertEquals(Material.COAL_BLOCK, ArgumentParser.parseBlock(0, Collections.singletonList("coal_block"), MockUtils.getConstantPlayer(), true).getMaterial());
        assertEquals(Material.STONE, ArgumentParser.parseBlock(0, Collections.emptyList(), MockUtils.getConstantPlayer(), true).getMaterial());
        MockUtils.getConstantPlayer().setItemInHand(new ItemStack(Material.AIR));
        assertEquals(Material.AIR, ArgumentParser.parseBlock(0, Collections.emptyList(), MockUtils.getConstantPlayer(), false).getMaterial());
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
        assertEquals(9, ArgumentParser.parseInt("9", Messages.CMD_ERROR_NAN));
        assertEquals(-37, ArgumentParser.parseInt("-37", Messages.CMD_ERROR_NAN));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBlock("two"));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parseBlock("1.6"));
    }

    @Test
    public void parsePositiveInt() {
        assertEquals(14, ArgumentParser.parsePositiveInt("14", true, Messages.CMD_ERROR_POSITIVE_INT));
        assertEquals(0, ArgumentParser.parsePositiveInt("0", true, Messages.CMD_ERROR_NAN));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePositiveInt("0", false, Messages.CMD_ERROR_POSITIVE_INT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePositiveInt("-6", false, Messages.CMD_ERROR_POSITIVE_INT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePositiveInt("wait", false, Messages.CMD_ERROR_POSITIVE_INT));
    }

    @Test
    public void parsePercentage() {
        assertEquals(100, ArgumentParser.parsePercentage("100", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertEquals(58, ArgumentParser.parsePercentage("58", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertEquals(0, ArgumentParser.parsePercentage("0", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePercentage("", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePercentage("101", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
        assertThrows(ArgumentParseException.class, () -> ArgumentParser.parsePercentage("-1", Messages.CMD_ERROR_PERCENTAGE_FORMAT));
    }
}

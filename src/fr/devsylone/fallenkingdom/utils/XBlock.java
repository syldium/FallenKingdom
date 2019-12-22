/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Crypto Morin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package fr.devsylone.fallenkingdom.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.*;
import org.bukkit.material.*;

import java.util.EnumSet;

/*
 * References
 *
 * * * GitHub: https://github.com/CryptoMorin/XSeries/blob/master/XBlock.java
 * * XSeries: https://www.spigotmc.org/threads/378136/
 * BlockState: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockState.html
 * BlockData (New): https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/BlockData.html
 * MaterialData (Old): https://hub.spigotmc.org/javadocs/spigot/org/bukkit/material/MaterialData.html
 */

/**
 * <b>XBlock BETA</b> - MaterialData/BlockData Support<br>
 * Supports 1.8+ - Requires XMaterial
 * JavaDocs will be added soon.
 *
 * @author Crypto Morin
 * @version 0.2.0
 * @see Block
 * @see BlockData
 * @see BlockState
 * @see MaterialData
 * @see XMaterial
 */
@SuppressWarnings("deprecation")
public final class XBlock {
    public static final EnumSet<XMaterial> REPLACABLE = EnumSet.of(
            XMaterial.DANDELION, XMaterial.POPPY, XMaterial.BLUE_ORCHID, XMaterial.ALLIUM, XMaterial.AZURE_BLUET, XMaterial.RED_TULIP,
            XMaterial.ORANGE_TULIP, XMaterial.WHITE_TULIP, XMaterial.PINK_TULIP, XMaterial.OXEYE_DAISY, XMaterial.CORNFLOWER,
            XMaterial.LILY_OF_THE_VALLEY, XMaterial.WITHER_ROSE, XMaterial.SUNFLOWER, XMaterial.LILAC, XMaterial.ROSE_BUSH,
            XMaterial.PEONY, XMaterial.TALL_GRASS, XMaterial.LARGE_FERN, XMaterial.FERN, XMaterial.DEAD_BUSH,
            XMaterial.OAK_FENCE, XMaterial.AIR
    );
    private static final boolean ISFLAT = XMaterial.isNewVersion();

    public static Material grass() {
        // En 1.13+, grass désigne la plante, en 1.12.2-, grass désigne le bloc
        if (ISFLAT) {
            return Material.getMaterial("GRASS");
        } else {
            return Material.getMaterial("TALLGRASS");
        }
    }

    public static boolean isReplacable(Material material) {
        if (REPLACABLE.contains(XMaterial.matchXMaterial(material))) {
            return true;
        }
        return material.equals(grass());
    }

    public static boolean setColor(Block block, DyeColor color) {
        if (ISFLAT) {
            String type = block.getType().name();
            if (type.endsWith("WOOL")) block.setType(Material.getMaterial(color.name() + "_WOOL"));
            else if (type.endsWith("BED")) block.setType(Material.getMaterial(color.name() + "_BED"));
            else if (type.endsWith("STAINED_GLASS")) block.setType(Material.getMaterial(color.name() + "_STAINED_GLASS"));
            else if (type.endsWith("STAINED_GLASS_PANE")) block.setType(Material.getMaterial(color.name() + "_STAINED_GLASS_PANE"));
            else if (type.endsWith("TERRACOTTA")) block.setType(Material.getMaterial(color.name() + "_TERRACOTTA"));
            else if (type.endsWith("GLAZED_TERRACOTTA")) block.setType(Material.getMaterial(color.name() + "_GLAZED_TERRACOTTA"));
            else if (type.endsWith("BANNER")) block.setType(Material.getMaterial(color.name() + "_BANNER"));
            else if (type.endsWith("WALL_BANNER")) block.setType(Material.getMaterial(color.name() + "_WALL_BANNER"));
            else if (type.endsWith("CARPET")) block.setType(Material.getMaterial(color.name() + "_CARPET"));
            else if (type.endsWith("SHULKER_BOX")) block.setType(Material.getMaterial(color.name() + "_SHULKERBOX"));
            else if (type.endsWith("CONCRETE")) block.setType(Material.getMaterial(color.name() + "_CONCRETE"));
            else if (type.endsWith("CONCRETE_POWDER")) block.setType(Material.getMaterial(color.name() + "_CONCRETE_POWDER"));
            else return false;
            return true;
        }

        BlockState state = block.getState();
        MaterialData data = state.getData();

        if (data instanceof Colorable) {
            ((Colorable) data).setColor(color);
            state.update(true);
            return true;
        }
        return false;
    }
}
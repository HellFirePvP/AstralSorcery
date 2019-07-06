/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.base.BlockDynamicColor;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.marble.*;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;

import java.util.LinkedList;
import java.util.List;

import static hellfirepvp.astralsorcery.common.lib.BlocksAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBlocks
 * Created by HellFirePvP
 * Date: 31.05.2019 / 21:44
 */
public class RegistryBlocks {

    private static List<BlockDynamicColor> colorBlocks = Lists.newArrayList();
    static List<CustomItemBlock> defaultItemBlocks = new LinkedList<>();

    private RegistryBlocks() {}

    public static void registerBlocks() {
        MARBLE_ARCH     = registerBlock(new BlockMarbleArch());
        MARBLE_BRICKS   = registerBlock(new BlockMarbleBricks());
        MARBLE_CHISELED = registerBlock(new BlockMarbleChiseled());
        MARBLE_ENGRAVED = registerBlock(new BlockMarbleEngraved());
        MARBLE_PILLAR   = registerBlock(new BlockMarblePillar());
        MARBLE_RAW      = registerBlock(new BlockMarbleRaw());
        MARBLE_RUNED    = registerBlock(new BlockMarbleRuned());
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerColors(ColorHandlerEvent.Block blockColorEvent) {
        colorBlocks.forEach(block -> blockColorEvent.getBlockColors().register(block::getColor, (Block) block));
    }

    private static <T extends Block> T registerBlock(T block) {
        ResourceLocation name = createBlockName(block);
        block.setRegistryName(name);
        AstralSorcery.getProxy().getRegistryPrimer().register(block);
        if (block instanceof CustomItemBlock) {
            defaultItemBlocks.add((CustomItemBlock) block);
        }
        if (block instanceof BlockDynamicColor) {
            colorBlocks.add((BlockDynamicColor) block);
        }
        return block;
    }

    private static ResourceLocation createBlockName(Block block) {
        String name = block.getClass().getSimpleName();
        if (name.startsWith("Block")) {
            name = name.substring(5);
        }
        name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        return new ResourceLocation(AstralSorcery.MODID, name);
    }

}

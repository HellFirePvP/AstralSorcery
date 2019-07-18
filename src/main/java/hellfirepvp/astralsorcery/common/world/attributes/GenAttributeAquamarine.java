/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.attributes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.world.WorldGenAttribute;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeAquamarine
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:59
 */
public class GenAttributeAquamarine extends WorldGenAttribute {

    private List<IBlockState> replaceableStates = null;
    private List<String> replaceableStatesSerialized = new ArrayList<>(); //Delay resolving states to a later state...

    public GenAttributeAquamarine() {
        super(0);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.WORLDGEN, "aquamarine") {
            @Override
            public void loadFromConfig(Configuration cfg) {
                String[] applicableReplacements = cfg.getStringList("ReplacementStates", getConfigurationSection(), new String[] {
                        "minecraft:sand:0"
                }, "Defines the blockstates that may be replaced by aquamarine shale when trying to generate aquamarine shale. format: <modid>:<name>:<meta> - Use meta -1 for wildcard");
                replaceableStatesSerialized = Arrays.asList(applicableReplacements);
            }
        });
    }

    private void resolveReplaceableStates() {
        replaceableStates = new LinkedList<>();
        for (String stateStr : replaceableStatesSerialized) {
            String[] spl = stateStr.split(":");
            if(spl.length != 3) {
                AstralSorcery.log.info("Skipping invalid replacement state: " + stateStr);
                continue;
            }
            String strMeta = spl[2];
            Integer meta;
            try {
                meta = Integer.parseInt(strMeta);
            } catch (NumberFormatException exc) {
                AstralSorcery.log.error("Skipping invalid replacement state: " + stateStr + " - Its 'meta' is not a number!");
                continue;
            }
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(spl[0], spl[1]));
            if(b == null || b == Blocks.AIR) {
                AstralSorcery.log.error("Skipping invalid replacement state: " + stateStr + " - The block does not exist!");
                continue;
            }
            if(meta == -1) {
                replaceableStates.addAll(b.getBlockState().getValidStates());
            } else {
                replaceableStates.add(b.getStateFromMeta(meta));
            }
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if(replaceableStates == null) {
            resolveReplaceableStates();
        }

        for (int i = 0; i < Config.aquamarineAmount; i++) {
            int rX = (chunkX  * 16) + random.nextInt(16) + 8;
            int rY = 48 + random.nextInt(19);
            int rZ = (chunkZ  * 16) + random.nextInt(16) + 8;
            BlockPos pos = new BlockPos(rX, rY, rZ);
            IBlockState stateAt = world.getBlockState(pos);
            if (MiscUtils.getMatchingState(this.replaceableStates, stateAt) == null) {
                continue;
            }

            boolean foundWater = false;
            for (int yy = 0; yy < 2; yy++) {
                BlockPos check = pos.offset(EnumFacing.UP, yy);
                IBlockState bs = world.getBlockState(check);
                Block block = bs.getBlock();
                if((block instanceof BlockLiquid && bs.getMaterial() == Material.WATER) ||
                        block.equals(Blocks.ICE) || block.equals(Blocks.PACKED_ICE) || block.equals(Blocks.FROSTED_ICE)) {
                    foundWater = true;
                    break;
                }
            }
            if(!foundWater)
                continue;

            world.setBlockState(pos, BlocksAS.customSandOre.getDefaultState()
                    .withProperty(BlockCustomSandOre.ORE_TYPE, BlockCustomSandOre.OreType.AQUAMARINE));
        }
    }
}

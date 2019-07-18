/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.attributes;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.world.WorldGenAttribute;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeMarble
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:57
 */
public class GenAttributeMarble extends WorldGenAttribute {

    private WorldGenMinable marbleMineable = null;
    private List<IBlockState> replaceableStates = null;
    private List<String> replaceableStatesSerialized = new ArrayList<>(); //Delay resolving states to a later state...

    public GenAttributeMarble() {
        super(0);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.WORLDGEN, "marble") {
            @Override
            public void loadFromConfig(Configuration cfg) {
                String[] applicableReplacements = cfg.getStringList("ReplacementStates", getConfigurationSection(), new String[] {
                        "minecraft:stone:0"
                }, "Defines the blockstates that may be replaced by marble when trying to generate marble. format: <modid>:<name>:<meta> - Use meta -1 for wildcard");
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

    //WorldGenMinable has the offset built-in.
    //shifting it by 8 myself would just shift it 16 in total, not solving the problem.
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if(replaceableStates == null) {
            resolveReplaceableStates();
            marbleMineable = new WorldGenMinable(
                    BlocksAS.blockMarble.getDefaultState().
                            withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW),
                    Config.marbleVeinSize,
                    (s) -> MiscUtils.getMatchingState(this.replaceableStates, s) != null);
        }

        for (int i = 0; i < Config.marbleAmount; i++) {
            int rX = (chunkX  * 16) + random.nextInt(16);
            int rY = 50 + random.nextInt(10);
            int rZ = (chunkZ  * 16) + random.nextInt(16);
            BlockPos pos = new BlockPos(rX, rY, rZ);
            marbleMineable.generate(world, random, pos);
        }
    }
}

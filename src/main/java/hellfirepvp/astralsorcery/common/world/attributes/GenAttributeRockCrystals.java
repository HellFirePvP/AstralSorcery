/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.attributes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.world.WorldGenAttribute;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeRockCrystals
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:52
 */
public class GenAttributeRockCrystals extends WorldGenAttribute {

    private final ConfigEntry entry;

    private boolean doGenerate = false;
    private boolean doIgnoreBiomeSpecifications = true;
    private boolean doIgnoreDimensionSpecifications = true;
    private List<BiomeDictionary.Type> biomeTypes = new ArrayList<>();
    private List<Integer> applicableDimensions = new ArrayList<>();
    private int crystalDensity = 15;

    public GenAttributeRockCrystals() {
        super(0);
        this.entry = new ConfigEntry(ConfigEntry.Section.WORLDGEN, "rockcrystals") {
            @Override
            public void loadFromConfig(Configuration cfg) {
                doGenerate = cfg.getBoolean("Generate", getConfigurationSection(), true, "Generate " + getKey());
                doIgnoreBiomeSpecifications = cfg.getBoolean("IgnoreBiomeSpecification", getConfigurationSection(), doIgnoreBiomeSpecifications, "Ignore Biome specifications when trying to generate " + getKey());
                doIgnoreDimensionSpecifications = cfg.getBoolean("IgnoreDimensionSettings", getConfigurationSection(), doIgnoreDimensionSpecifications, "Ignore dimension-whitelist when trying to generate " + getKey());
                crystalDensity = cfg.getInt("CrystalDensity", getConfigurationSection(), crystalDensity, 1, 40, "Defines how rarely Rock crystal ores spawn. The higher, the more rare.");
                String[] strTypes = cfg.getStringList("BiomeTypes", getConfigurationSection(), new String[0], "Set the BiomeTypes (according to the BiomeDicitionary) this structure will spawn in.");
                List<BiomeDictionary.Type> resolvedTypes = new LinkedList<>();
                for (String s : strTypes) {
                    try {
                        resolvedTypes.add(BiomeDictionary.Type.getType(s));
                    } catch (Exception e) {
                        AstralSorcery.log.error("[AstralSorcery] Could not find BiomeType by name '" + s + "' - Ignoring BiomeType specification for structure " + getKey());
                    }
                }
                biomeTypes = Lists.newArrayList(resolvedTypes);
                String[] dimensionWhitelist = cfg.getStringList("DimensionWhitelist", getConfigurationSection(), new String[0], "Define an array of dimensionID's where the structure is allowed to spawn in.");
                applicableDimensions = new ArrayList<>();
                for (String s : dimensionWhitelist) {
                    try {
                        applicableDimensions.add(Integer.parseInt(s));
                    } catch (NumberFormatException exc) {
                        AstralSorcery.log.error("[AstralSorcery] Could not add " + s + " to dimension whitelist for " + getKey() + " - It is not a number!");
                    }
                }
            }

            @Override
            public String getConfigurationSection() {
                return super.getConfigurationSection() + "." + getKey();
            }
        };
        Config.addDynamicEntry(this.entry);
    }

    private boolean isApplicableWorld(World world) {
        if(this.doIgnoreDimensionSpecifications) return true;

        Integer dimId = world.provider.getDimension();
        if(this.applicableDimensions.isEmpty()) return false;
        for (Integer dim : this.applicableDimensions) {
            if(dim.equals(dimId)) return true;
        }
        return false;
    }

    private boolean fitsBiome(World world, BlockPos pos) {
        if(this.doIgnoreBiomeSpecifications) return true;

        Biome b = world.getBiome(pos);
        Collection<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
        if(types.isEmpty()) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (biomeTypes.contains(t)) applicable = true;
        }
        return applicable;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if (doGenerate && random.nextInt(crystalDensity) == 0) {
            int xPos = chunkX * 16 + random.nextInt(16) + 8;
            int zPos = chunkZ * 16 + random.nextInt(16) + 8;
            int yPos = 2 + random.nextInt(4);
            BlockPos pos = new BlockPos(xPos, yPos, zPos);
            if(!fitsBiome(world, pos) || !isApplicableWorld(world)) return;
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock().equals(Blocks.STONE)) {
                BlockStone.EnumType stoneType = state.getValue(BlockStone.VARIANT);
                if (stoneType.equals(BlockStone.EnumType.STONE)) {
                    IBlockState newState = BlocksAS.customOre.getDefaultState().withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.ROCK_CRYSTAL);
                    world.setBlockState(pos, newState);
                    RockCrystalBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
                    buf.addOre(pos);

                    if(random.nextInt(4) == 0) {
                        pos = pos.add(random.nextInt(2), random.nextInt(2), random.nextInt(2));
                        if (state.getBlock().equals(Blocks.STONE)) {
                            if (stoneType.equals(BlockStone.EnumType.STONE)) {
                                world.setBlockState(pos, newState);
                                buf.addOre(pos);
                            }
                        }
                    }
                }
            }
        }
    }
}

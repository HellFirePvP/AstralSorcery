/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.WorldStructureEntry;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import hellfirepvp.astralsorcery.common.world.WorldGenAttribute;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttributeStructure
 * Created by HellFirePvP
 * Date: 21.10.2016 / 13:24
 */
public abstract class WorldGenAttributeStructure extends WorldGenAttribute {

    protected final WorldStructureEntry cfgEntry;
    private StructureQuery query;

    public WorldGenAttributeStructure(int attributeVersion, String entry, StructureQuery query, BiomeDictionary.Type... types) {
        super(attributeVersion);
        this.cfgEntry = new WorldStructureEntry(entry, types);
        Config.addDynamicEntry(cfgEntry);
        this.query = query;
    }

    public StructureBlockArray getStructureTemplate() {
        return query.getStructure();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if(canGenerateAtAll(chunkX, chunkZ, world, random)) {
            BlockPos pos = getGenerationPosition(chunkX, chunkZ, world, random);
            if(fulfillsSpecificConditions(pos, world, random)) {
                generate(pos, world, random);
            }
        }
    }

    public abstract void generate(BlockPos pos, World world, Random rand);

    public abstract boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random);

    public abstract BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand);

    public boolean canGenerateAtAll(int chX, int chZ, World world, Random rand) {
        if(!DataWorldSkyHandlers.hasWorldHandler(world, Side.SERVER)) return false;
        return cfgEntry.shouldGenerate() && cfgEntry.tryGenerate(rand);
    }

    public static interface StructureQuery {

        public StructureBlockArray getStructure();

    }

}

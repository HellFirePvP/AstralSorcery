package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.WorldStructureEntry;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttributeStructure
 * Created by HellFirePvP
 * Date: 21.10.2016 / 13:24
 */
public abstract class WorldGenAttributeStructure {

    private final WorldStructureEntry cfgEntry;
    private StructureQuery query;

    public WorldGenAttributeStructure(String entry, StructureQuery query) {
        this.cfgEntry = new WorldStructureEntry(entry);
        Config.addDynamicEntry(cfgEntry);
        this.query = query;
    }

    public StructureBlockArray getStructureTemplate() {
        return query.getStructure();
    }

    public abstract void generate(BlockPos pos, World world, Random rand);

    public abstract boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random);

    public abstract BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand);

    public boolean canGenerate(int chX, int chZ, World world, Random rand) {
        if(!DataWorldSkyHandlers.hasWorldHandler(world, Side.SERVER)) return false;
        return cfgEntry.shouldGenerate() && cfgEntry.tryGenerate(rand);
    }

    public static interface StructureQuery {

        public StructureBlockArray getStructure();

    }

}

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.data.DirectionalLayerBlockDiscoverer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileIlluminator
 * Created by HellFirePvP
 * Date: 01.11.2016 / 16:01
 */
public class TileIlluminator extends TileSkybound {

    private static final Random rand = new Random();
    public static final LightCheck illuminatorCheck = new LightCheck();

    public static final int SEARCH_RADIUS = 48;
    public static final int STEP_WIDTH = 4;

    private LinkedList<BlockPos>[] validPositions = null;
    private boolean recalcRequested = false;
    private int ticksUntilNext = 180;

    @Override
    public void update() {
        super.update();

        if(!worldObj.isRemote && doesSeeSky()) {
            if(validPositions == null) recalculate();
            if(rand.nextInt(3) == 0 && placeFlares()) {
                recalcRequested = true;
            }
            ticksUntilNext--;
            if(ticksUntilNext <= 0) {
                ticksUntilNext = 180;
                if(recalcRequested) {
                    recalcRequested = false;
                    recalculate();
                }
            }
        }
    }

    private boolean placeFlares() {
        boolean needsRecalc = false;
        for (LinkedList<BlockPos> list : validPositions) {
            if(list.isEmpty()) {
                needsRecalc = true;
                continue;
            }
            int index = rand.nextInt(list.size());
            BlockPos at = list.remove(index);
            if(!needsRecalc && list.isEmpty()) needsRecalc = true;
            at.add(rand.nextInt(5) - 2, rand.nextInt(13) - 6, rand.nextInt(5) - 2);
            if(illuminatorCheck.isStateValid(worldObj, at, worldObj.getBlockState(at))) {
                worldObj.setBlockState(at, BlocksAS.blockVolatileLight.getDefaultState());
            }
        }
        return needsRecalc;
    }

    private void recalculate() {
        int parts = yPartsFromHeight();
        validPositions = new LinkedList[parts];
        for (int i = 1; i <= parts; i++) {
            int yLevel = (int) (((float) getPos().getY()) * (((float) i) / ((float) parts)));
            LinkedList<BlockPos> calcPositions = new DirectionalLayerBlockDiscoverer(worldObj, new BlockPos(getPos().getX(), yLevel, getPos().getZ()), SEARCH_RADIUS, STEP_WIDTH).discoverApplicableBlocks();
            validPositions[i - 1] = repeatList(calcPositions);
        }
    }

    private int yPartsFromHeight() {
        return Math.max(2, getPos().getY() / 8);
    }

    private LinkedList<BlockPos> repeatList(LinkedList<BlockPos> list) {
        LinkedList<BlockPos> rep =  new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            rep.addAll(list);
        }
        return rep;
    }

    @Override
    protected void onFirstTick() {
        recalculate();
    }

    public static class LightCheck implements BlockStateCheck {

        @Override
        public boolean isStateValid(World world, BlockPos pos, IBlockState state) {
            return world.isAirBlock(pos) && !world.canSeeSky(pos) && world.getLight(pos) < 8 && world.getLightFor(EnumSkyBlock.SKY, pos) < 6;
        }

    }

}

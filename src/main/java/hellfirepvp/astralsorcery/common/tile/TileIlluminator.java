/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.data.DirectionalLayerBlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
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

    public static final int SEARCH_RADIUS = 64;
    public static final int STEP_WIDTH = 4;

    private LinkedList<BlockPos>[] validPositions = null;
    private boolean recalcRequested = false;
    private int ticksUntilNext = 180;
    private boolean playerPlaced = false;

    @Override
    public void update() {
        super.update();

        if (!playerPlaced) return;

        if (!world.isRemote && doesSeeSky()) {
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
        if (world.isRemote) {
            playEffects();
        }
    }

    public void setPlayerPlaced() {
        playerPlaced = true;
        markForUpdate();
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        if(Minecraft.isFancyGraphicsEnabled() || rand.nextInt(5) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    getPos().getX() + 0.5,
                    getPos().getY() + 0.5,
                    getPos().getZ() + 0.5);
            p.motion((rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F);
            switch (rand.nextInt(3)) {
                case 0:
                    p.setColor(Color.WHITE);
                    break;
                case 1:
                    p.setColor(new Color(0xFEFF9E));
                    break;
                case 2:
                    p.setColor(new Color(0xFFE539));
                    break;
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
            at = at.add(rand.nextInt(5) - 2, rand.nextInt(13) - 6, rand.nextInt(5) - 2);
            if(world.isBlockLoaded(at) && illuminatorCheck.isStateValid(world, at, world.getBlockState(at))) {
                world.setBlockState(at, BlocksAS.blockVolatileLight.getDefaultState());
                if(rand.nextInt(4) == 0) {
                    EntityFlare.spawnAmbient(world, new Vector3(this).add(-1 + rand.nextFloat() * 3, 0.6, -1 + rand.nextFloat() * 3));
                }
            }
        }
        return needsRecalc;
    }

    private void recalculate() {
        int parts = yPartsFromHeight();
        validPositions = new LinkedList[parts];
        for (int i = 1; i <= parts; i++) {
            int yLevel = (int) (((float) getPos().getY()) * (((float) i) / ((float) parts)));
            LinkedList<BlockPos> calcPositions = new DirectionalLayerBlockDiscoverer(world, new BlockPos(getPos().getX(), yLevel, getPos().getZ()), SEARCH_RADIUS, STEP_WIDTH).discoverApplicableBlocks();
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
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("playerPlaced", this.playerPlaced);
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerPlaced = compound.getBoolean("playerPlaced");
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

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
import hellfirepvp.astralsorcery.common.block.BlockFlareLight;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.DirectionalLayerBlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
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
public class TileIlluminator extends TileEntityTick {

    private static final Random rand = new Random();
    public static final LightCheck illuminatorCheck = new LightCheck();

    public static final int SEARCH_RADIUS = 64;
    public static final int STEP_WIDTH = 4;

    private LinkedList<BlockPos>[] validPositions = null;
    private boolean recalcRequested = false;
    private int ticksUntilNext = 180;
    private boolean playerPlaced = false;

    private int boost = 0;
    private EnumDyeColor chosenColor = EnumDyeColor.YELLOW;

    @Override
    public void update() {
        super.update();

        if (!playerPlaced) return;

        if (!world.isRemote) {
            if(validPositions == null) recalculate();
            if(rand.nextInt(3) == 0 && placeFlares()) {
                recalcRequested = true;
            }
            boost--;
            ticksUntilNext--;
            if(ticksUntilNext <= 0) {
                ticksUntilNext = boost > 0 ? 30 : 180;
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
        this.playerPlaced = true;
        this.markForUpdate();
    }

    public void onWandUsed(EnumDyeColor color) {
        this.boost = 10 * 60 * 20;
        this.chosenColor = color;
        this.markForUpdate();
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

            Color col = MiscUtils.flareColorFromDye(EnumDyeColor.YELLOW);
            if (this.chosenColor != null) {
                col = MiscUtils.flareColorFromDye(this.chosenColor);
            }

            switch (rand.nextInt(3)) {
                case 0:
                    p.setColor(Color.WHITE);
                    break;
                case 1:
                    p.setColor(col.brighter().brighter());
                    break;
                case 2:
                    p.setColor(col);
                    break;
                default:
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
            if(world.isBlockLoaded(at) &&
                    at.getY() >= 0 &&
                    at.getY() <= 255 &&
                    illuminatorCheck.isStateValid(world, at, world.getBlockState(at))) {
                EnumDyeColor color = EnumDyeColor.YELLOW;
                if (this.chosenColor != null) {
                    color = this.chosenColor;
                }
                IBlockState lightState = BlocksAS.blockVolatileLight.getDefaultState().withProperty(BlockFlareLight.COLOR, color);
                if (world.setBlockState(at, lightState)) {
                    if(rand.nextInt(4) == 0) {
                        EntityFlare.spawnAmbient(world, new Vector3(this).add(-1 + rand.nextFloat() * 3, 0.6, -1 + rand.nextFloat() * 3));
                    }
                }
            }
        }
        return needsRecalc;
    }

    private void recalculate() {
        int parts = Math.max(0, getPos().getY() - 7);
        validPositions = new LinkedList[parts];
        for (int i = 1; i <= parts; i++) {
            float yPart = ((float) i) / ((float) parts);
            int yLevel = Math.round(yPart * (getPos().getY() - 7));
            LinkedList<BlockPos> calcPositions = new DirectionalLayerBlockDiscoverer(
                    new BlockPos(getPos().getX(), yLevel, getPos().getZ()), SEARCH_RADIUS, STEP_WIDTH)
                    .discoverApplicableBlocks();
            validPositions[i - 1] = repeatList(calcPositions);
        }
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
        compound.setInteger("boostTimeout", this.boost);
        if (chosenColor != null) {
            compound.setInteger("wandColor", this.chosenColor.getMetadata());
        } else {
            compound.setInteger("wandColor", EnumDyeColor.YELLOW.getMetadata());
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.playerPlaced = compound.getBoolean("playerPlaced");
        this.boost = compound.getInteger("boostTimeout");
        if (compound.hasKey("wandColor")) {
            this.chosenColor = EnumDyeColor.byMetadata(compound.getInteger("wandColor"));
        }
        if (this.chosenColor == null) {
            this.chosenColor = EnumDyeColor.YELLOW;
        }
    }

    @Override
    protected void onFirstTick() {
        recalculate();
    }

    public static class LightCheck implements BlockStateCheck.WorldSpecific {

        @Override
        public boolean isStateValid(World world, BlockPos pos, IBlockState state) {
            return world.isAirBlock(pos) &&
                    !MiscUtils.canSeeSky(world, pos, false, false) &&
                    world.getLight(pos) < 8 &&
                    world.getLightFor(EnumSkyBlock.SKY, pos) < 6;
        }

    }

}

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.BlockFlareLight;
import hellfirepvp.astralsorcery.common.entity.EntityFlare;
import hellfirepvp.astralsorcery.common.item.wand.ItemIlluminationWand;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileIlluminator
 * Created by HellFirePvP
 * Date: 31.08.2019 / 22:44
 */
public class TileIlluminator extends TileEntityTick {

    public static final LightCheck ILLUMINATOR_CHECK = new LightCheck();

    public static final int SEARCH_RADIUS = 64;
    public static final int STEP_WIDTH = 2;

    private List<List<BlockPos>> layerPositions = null;
    private boolean doRecalculation = false;
    private int ticksUntilNextPlacement = 180;
    private boolean playerPlaced = false;
    private int boostedTicks = 0;

    private DyeColor color = DyeColor.YELLOW;

    public TileIlluminator() {
        super(TileEntityTypesAS.ILLUMINATOR);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isPlayerPlaced()) {
            return; //Don't do anything if it's not specifically made as player-placed
        }

        if (!world.isRemote()) {
            if (layerPositions == null) {
                recalculate();
            }
            placeFlare();
            placeFlare();
            placeFlare();
            if (rand.nextInt(3) == 0 && placeFlare()) {
                doRecalculation = true;
            }
            if (boostedTicks > 0) {
                boostedTicks--;
            }
            ticksUntilNextPlacement--;
            if (ticksUntilNextPlacement <= 0) {
                ticksUntilNextPlacement = boostedTicks > 0 ? 30 : 180;
                if (doRecalculation) {
                    doRecalculation = false;
                    recalculate();
                }
            }
        }

        if (world.isRemote()) {
            this.tickEffects();
        }
    }

    private void recalculate() {
        int height = Math.max(0, getPos().getY() - 7);
        int parts = height / 7;
        layerPositions = new ArrayList<>(parts);
        for (int i = 0; i < parts; i++) {
            int yPart = 3 + i * 7;
            List<BlockPos> positions = new ArrayList<>();
            generatePositions(positions, new BlockPos(getPos().getX(), yPart, getPos().getZ()));
            layerPositions.add(positions);
        }
    }

    private void generatePositions(List<BlockPos> positions, BlockPos center) {
        int xPos = center.getX();
        int yPos = center.getY();
        int zPos = center.getZ();
        BlockPos currentPos = center;
        if (!positions.contains(currentPos)) {
            positions.add(currentPos);
        }

        Direction dir = Direction.NORTH;
        while (Math.abs(currentPos.getX() - xPos) <= SEARCH_RADIUS &&
                Math.abs(currentPos.getY() - yPos) <= SEARCH_RADIUS &&
                Math.abs(currentPos.getZ() - zPos) <= SEARCH_RADIUS) {
            currentPos = currentPos.offset(dir, STEP_WIDTH);
            if (!positions.contains(currentPos)) {
                positions.add(currentPos);
            }
            Direction tryDirNext = dir.rotateY();
            if (!positions.contains(currentPos.offset(tryDirNext, STEP_WIDTH))) {
                dir = tryDirNext;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffects() {
        if (!this.doesSeeSky() && this.boostedTicks <= 0) {
            return;
        }
        FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(new Vector3(this).add(0.5, 0.5, 0.5))
                .setScaleMultiplier(0.25F)
                .setMotion(new Vector3(rand.nextFloat() * 0.025F * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.025F * (rand.nextBoolean() ? 1 : -1),
                        rand.nextFloat() * 0.025F * (rand.nextBoolean() ? 1 : -1)));
        Color c = ColorUtils.flareColorFromDye(this.getColor());
        p.color(VFXColorFunction.constant(MiscUtils.eitherOf(rand,
                Color.WHITE, c.brighter().brighter(), c)));

        if (this.boostedTicks > 0) {
            if (this.ticksExisted % 4 == 0) {
                Collection<Vector3> positions = MiscUtils.getCirclePositions(
                        new Vector3(this).add(0.5, 0.5, 0.5),
                        Vector3.RotAxis.Y_AXIS, 0.8F + rand.nextFloat() * 0.1F, 20 + rand.nextInt(10));

                for (Vector3 v : positions) {
                    p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(v)
                            .setScaleMultiplier(0.15F)
                            .setMotion(new Vector3(0, (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.01, 0));
                    p.color(VFXColorFunction.constant(MiscUtils.eitherOf(rand,
                            Color.WHITE, c.brighter().brighter(), c)));
                }
            }
        }
    }

    private boolean placeFlare() {
        boolean recalc = false;
        for (List<BlockPos> list : layerPositions) {
            if (list.isEmpty()) {
                recalc = true;
                continue;
            }

            int index = rand.nextInt(list.size());
            BlockPos at = list.remove(index);
            if (!recalc && list.isEmpty()) {
                recalc = true;
            }
            at = at.add(rand.nextInt(5) - 2, rand.nextInt(13) - 6, rand.nextInt(5) - 2);
            MiscUtils.executeWithChunk(world, at, at, (pos) -> {
                if (this.doesSeeSky() && TileIlluminator.ILLUMINATOR_CHECK.test(world, pos, world.getBlockState(pos))) {
                    DyeColor color = this.getColor();
                    BlockState toPlace = BlocksAS.FLARE_LIGHT.getDefaultState().with(BlockFlareLight.COLOR, color);
                    if (world.setBlockState(pos, toPlace)) {
                        EntityFlare.spawnAmbientFlare(world, this.getPos());
                    }
                }
            });
        }
        return recalc;
    }

    public void setColor(DyeColor color) {
        this.color = color;
        this.markForUpdate();
    }

    public DyeColor getColor() {
        return color;
    }

    public void setPlayerPlaced(boolean playerPlaced) {
        this.playerPlaced = playerPlaced;
        this.markForUpdate();
    }

    public boolean isPlayerPlaced() {
        return playerPlaced;
    }

    public void onWandUsed(ItemStack stack) {
        this.boostedTicks = 10 * 60 * 20;
        this.setColor(ItemIlluminationWand.getConfiguredColor(stack));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putBoolean("playerPlaced", this.playerPlaced);
        compound.putInt("color", this.color.getId());
        compound.putInt("boostedTicks", this.boostedTicks);
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.playerPlaced = compound.getBoolean("playerPlaced");
        this.color = DyeColor.byId(compound.getInt("color"));
        this.boostedTicks = compound.getInt("boostedTicks");
    }

    public static class LightCheck implements BlockPredicate {

        @Override
        public boolean test(World world, BlockPos pos, BlockState state) {
            return world.isAirBlock(pos) &&
                    !MiscUtils.canSeeSky(world, pos, false, false) &&
                    world.getLight(pos) < 8 &&
                    world.getLightFor(LightType.SKY, pos) < 4;
        }

    }
}

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.block.tile.fountain.BlockFountainPrime;
import hellfirepvp.astralsorcery.common.crafting.nojson.FountainEffectRegistry;
import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffect;
import hellfirepvp.astralsorcery.common.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.tile.FluidTankAccess;
import hellfirepvp.astralsorcery.common.util.tile.SimpleSingleFluidTank;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileFountain
 * Created by HellFirePvP
 * Date: 29.10.2020 / 19:56
 */
public class TileFountain extends TileEntityTick {

    private static final int TANK_SIZE = 16 * FluidAttributes.BUCKET_VOLUME;
    private static final int LIQUID_STARLIGHT_TANK_SIZE = 16 * FluidAttributes.BUCKET_VOLUME;

    private FountainEffect<?> currentEffect;
    private FountainEffect.EffectContext effectContext;

    private int tickDrawLiquidStarlight = 0;
    private int tickActiveFountainEffect = 0;

    private int mbLiquidStarlight = 0;
    private final FluidTankAccess access;
    private final SimpleSingleFluidTank tank;

    public TileFountain() {
        super(TileEntityTypesAS.FOUNTAIN);

        this.tank = new SimpleSingleFluidTank(TANK_SIZE);
        this.tank.addUpdateFunction(this::markForUpdate);
        this.access = new FluidTankAccess();
        this.access.putTank(0, tank);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            if (this.hasMultiblock()) {
                this.updateFountainComponents();
                this.drawLiquidStarlight();

                FountainEffect effect = this.getCurrentEffect();
                if (effect != null) {
                    FountainEffect.EffectContext ctx = this.effectContext;
                    if (!this.consumeLiquidStarlight(1)) {
                        this.setCurrentEffect(null);
                        this.replaceCurrentEffect(effect, ctx, null);
                        return;
                    }

                    FountainEffect.OperationSegment segment = getSegment();
                    if (segment != FountainEffect.OperationSegment.RUNNING) {
                        this.tickActiveFountainEffect++;
                    }
                    FountainEffect.OperationSegment nextSegment = getSegment();

                    if (segment != nextSegment) {
                        effect.transition(this, ctx, LogicalSide.SERVER, segment, nextSegment);
                        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.FOUNTAIN_TRANSITION_SEGMENT).addData(buf -> {
                            ByteBufUtils.writePos(buf, pos);
                            ByteBufUtils.writeEnumValue(buf, segment);
                            ByteBufUtils.writeEnumValue(buf, nextSegment);
                        });
                        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, pos, 32));
                    }
                    effect.tick(this, ctx, this.tickActiveFountainEffect, LogicalSide.SERVER, this.getSegment());
                }
            }
        } else {
            if (this.hasMultiblock()) {
                FountainEffect effect = this.getCurrentEffect();
                if (effect != null) {
                    effect.tick(this, this.effectContext, this.tickActiveFountainEffect, LogicalSide.CLIENT, getSegment());
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void playTransitionEffect(PktPlayEffect pktPlayEffect) {
        BlockPos at = ByteBufUtils.readPos(pktPlayEffect.getExtraData());
        FountainEffect.OperationSegment segment = ByteBufUtils.readEnumValue(pktPlayEffect.getExtraData(), FountainEffect.OperationSegment.class);
        FountainEffect.OperationSegment nextSegment = ByteBufUtils.readEnumValue(pktPlayEffect.getExtraData(), FountainEffect.OperationSegment.class);

        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }
        TileFountain fountain = MiscUtils.getTileAt(world, at, TileFountain.class, false);
        if (fountain == null) {
            return;
        }
        FountainEffect effect = fountain.getCurrentEffect();
        if (effect == null) {
            return;
        }
        effect.transition(fountain, fountain.effectContext, LogicalSide.CLIENT, segment, nextSegment);
    }

    @OnlyIn(Dist.CLIENT)
    public static void replaceEffect(PktPlayEffect pktPlayEffect) {
        BlockPos at = ByteBufUtils.readPos(pktPlayEffect.getExtraData());
        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }
        TileFountain fountain = MiscUtils.getTileAt(world, at, TileFountain.class, false);
        if (fountain == null) {
            return;
        }
        FountainEffect effect = fountain.getCurrentEffect();
        if (effect == null) {
            return;
        }
        effect.onReplace(fountain, fountain.effectContext, null, LogicalSide.CLIENT);
    }

    private void drawLiquidStarlight() {
        this.tickDrawLiquidStarlight--;

        if (this.tickDrawLiquidStarlight <= 0) {
            this.tickDrawLiquidStarlight = 100;

            if (this.mbLiquidStarlight < (LIQUID_STARLIGHT_TANK_SIZE * 0.8F) && this.currentEffect != null) {
                TileChalice chalice = MiscUtils.getTileAt(world, pos.up(), TileChalice.class, false);
                if (chalice != null) {
                    FluidStack fluid = chalice.getTank().drain(400, IFluidHandler.FluidAction.SIMULATE);
                    if (!fluid.isEmpty() && fluid.getFluid() instanceof FluidLiquidStarlight) {
                        FluidStack drained = chalice.getTank().drain(new FluidStack(fluid, 400), IFluidHandler.FluidAction.EXECUTE);
                        this.mbLiquidStarlight += drained.getAmount();
                        this.markForUpdate();
                    }
                }
            }
        }
    }

    private void updateFountainComponents() {
        FountainEffect prevEffect = this.getCurrentEffect();
        FountainEffect.EffectContext prevContext = this.effectContext;

        BlockState primeState = world.getBlockState(pos.down());
        if (primeState.getBlock() instanceof BlockFountainPrime) {
            if (this.setCurrentEffect(((BlockFountainPrime) primeState.getBlock()).provideEffect()) && prevEffect != null) {
                this.replaceCurrentEffect(prevEffect, prevContext, this.getCurrentEffect());
            }
        } else {
            if (this.setCurrentEffect(null) && prevEffect != null) {
                this.replaceCurrentEffect(prevEffect, prevContext, null);
            }
        }
    }

    private void replaceCurrentEffect(FountainEffect prevEffect, FountainEffect.EffectContext prevContext, FountainEffect newEffect) {
        prevEffect.onReplace(this, prevContext, newEffect, LogicalSide.SERVER);
        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.FOUNTAIN_REPLACE_EFFECT)
                .addData(buf -> ByteBufUtils.writePos(buf, pos));
        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, pos, 32));

    }

    private boolean setCurrentEffect(@Nullable FountainEffect<?> effect) {
        if (!Objects.equals(this.currentEffect, effect)) {
            this.tickActiveFountainEffect = 0;
            this.currentEffect = effect;
            if (this.currentEffect == null) {
                this.effectContext = null;
            } else {
                this.effectContext = this.currentEffect.createContext(this);
            }
            this.markForUpdate();
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public StructureType getRequiredStructureType() {
        return StructureTypesAS.PTYPE_FOUNTAIN;
    }

    public boolean consumeLiquidStarlight(int amount) {
        if (amount <= 0) {
            return this.mbLiquidStarlight > 0;
        }
        if (this.mbLiquidStarlight >= amount) {
            this.mbLiquidStarlight -= amount;
            this.markForUpdate();
            return true;
        }
        this.mbLiquidStarlight = 0;
        this.markForUpdate();
        return false;
    }

    public FountainEffect.OperationSegment getSegment() {
        if (this.getCurrentEffect() == null || this.tickActiveFountainEffect == 0) {
            return FountainEffect.OperationSegment.INACTIVE;
        }
        if (this.getCurrentEffect().isInTick(FountainEffect.OperationSegment.STARTUP, this.tickActiveFountainEffect)) {
            return FountainEffect.OperationSegment.STARTUP;
        }
        if (this.getCurrentEffect().isInTick(FountainEffect.OperationSegment.PREPARATION, this.tickActiveFountainEffect)) {
            return FountainEffect.OperationSegment.PREPARATION;
        }
        return FountainEffect.OperationSegment.RUNNING;
    }

    @Nullable
    public FountainEffect<?> getCurrentEffect() {
        return currentEffect;
    }

    public int getTickActiveFountainEffect() {
        return tickActiveFountainEffect;
    }

    @Nonnull
    public SimpleSingleFluidTank getTank() {
        return tank;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.tickActiveFountainEffect = compound.getInt("tickActiveFountainEffect");
        this.mbLiquidStarlight = compound.getInt("mbLiquidStarlight");
        this.tank.readNBT(compound.getCompound("tank"));
        if (compound.contains("currentEffect")) {
            FountainEffect<?> prevEffect = this.getCurrentEffect();
            ResourceLocation key = new ResourceLocation(compound.getString("currentEffect"));
            this.currentEffect = FountainEffectRegistry.getEffect(key);

            if (this.currentEffect != null) {
                if (!Objects.equals(this.currentEffect, prevEffect)) {
                    this.effectContext = this.currentEffect.createContext(this);
                }
                this.effectContext.readFromNBT(compound.getCompound("currentEffectData"));
            } else {
                this.effectContext = null;
            }
        } else {
            this.currentEffect = null;
            this.effectContext = null;
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.putInt("tickActiveFountainEffect", this.tickActiveFountainEffect);
        compound.putInt("mbLiquidStarlight", this.mbLiquidStarlight);
        compound.put("tank", this.tank.writeNBT());
        if (this.currentEffect != null) {
            compound.putString("currentEffect", this.currentEffect.getId().toString());
            if (this.effectContext != null) {
                CompoundNBT tag = new CompoundNBT();
                this.effectContext.writeToNBT(tag);
                compound.put("currentEffectData", tag);
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.access.hasCapability(cap, side)) {
            return this.access.getCapability(side).cast();
        }
        return super.getCapability(cap, side);
    }
}

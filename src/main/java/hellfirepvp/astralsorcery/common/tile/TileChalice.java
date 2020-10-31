/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXScaleFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.ColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.auxiliary.ChaliceHelper;
import hellfirepvp.astralsorcery.common.block.tile.BlockChalice;
import hellfirepvp.astralsorcery.common.block.tile.BlockFountain;
import hellfirepvp.astralsorcery.common.block.tile.BlockWell;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteractionContext;
import hellfirepvp.astralsorcery.common.fluid.FluidLiquidStarlight;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RaytraceAssist;
import hellfirepvp.astralsorcery.common.util.block.BlockDiscoverer;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicates;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tile.FluidTankAccess;
import hellfirepvp.astralsorcery.common.util.tile.SimpleSingleFluidTank;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 09.11.2019 / 15:03
 */
public class TileChalice extends TileEntityTick {

    private static final int TANK_SIZE = 64 * FluidAttributes.BUCKET_VOLUME;

    private final SimpleSingleFluidTank tank;
    private final FluidTankAccess access;

    private int nextInteraction = -1;

    private Vector3 rotation = new Vector3();
    private Vector3 prevRotation = new Vector3();
    private Vector3 rotationVec = null;

    public TileChalice() {
        super(TileEntityTypesAS.CHALICE);

        this.tank = new SimpleSingleFluidTank(TANK_SIZE);
        this.tank.addUpdateFunction(this::markForUpdate);
        this.access = new FluidTankAccess();
        this.access.putTank(0, tank, Direction.DOWN);
    }

    @Override
    public void tick() {
        super.tick();

        if (getWorld().isRemote()) {
            if (this.rotationVec == null) {
                this.rotationVec = Vector3.random().normalize().multiply(1.5F);
            }

            this.prevRotation = this.rotation.clone();
            this.rotation.add(this.rotationVec);
        } else {
            if (nextInteraction == -1) {
                nextInteraction = ticksExisted + 20 + rand.nextInt(40);
            }
            if (ticksExisted < nextInteraction) {
                return;
            }
            nextInteraction = ticksExisted + 20 + rand.nextInt(40);

            if (!tickLightwellDraw()) {
                tickChaliceInteractions();
            }
        }
    }

    private void tickChaliceInteractions() {
        if (getWorld().isBlockPowered(pos) || getWorld().getBlockState(getPos().down()).getBlock() instanceof BlockFountain) {
            return;
        }
        FluidStack thisFluid = this.getTank().getFluid();
        if (thisFluid.isEmpty()) {
            return;
        }

        List<BlockPos> chalicePositions = ChaliceHelper.findNearbyChalices(getWorld(), getPos(), 16);
        Collections.shuffle(chalicePositions, rand);
        for (BlockPos otherChalicePos : chalicePositions) {
            TileChalice otherChalice = MiscUtils.getTileAt(getWorld(), otherChalicePos, TileChalice.class, false);
            if (otherChalice == null) {
                continue;
            }
            FluidStack otherFluid = otherChalice.getTank().getFluid();
            if (otherFluid.isEmpty()) {
                continue;
            }
            LiquidInteractionContext ctx = new LiquidInteractionContext(thisFluid, otherFluid);
            List<LiquidInteraction> recipes = RecipeTypesAS.TYPE_LIQUID_INTERACTION.findMatchingRecipes(ctx);
            LiquidInteraction recipe = LiquidInteraction.pickRecipe(recipes);
            while (recipe != null) {
                if (recipe.consumeInputs(this, otherChalice)) {

                    Vector3 thisChaliceV = new Vector3(this).add(0.5, 1.5, 0.5);
                    Vector3 otherChaliceV = new Vector3(otherChalicePos).add(0.5, 1.5, 0.5);
                    Vector3 target = thisChaliceV.getMidpoint(otherChaliceV);

                    recipe.getResult().doResult(getWorld(), target.clone());

                    PacketDistributor.TargetPoint point = PacketChannel.pointFromPos(getWorld(), target.toBlockPos(), 32);
                    PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.LIQUID_INTERACTION_LINE).addData(buf -> {
                        ByteBufUtils.writeVector(buf, thisChaliceV);
                        ByteBufUtils.writeVector(buf, target);
                        ByteBufUtils.writeFluidStack(buf, thisFluid);
                    });
                    PacketChannel.CHANNEL.sendToAllAround(pkt, point);
                    pkt = new PktPlayEffect(PktPlayEffect.Type.LIQUID_INTERACTION_LINE).addData(buf -> {
                        ByteBufUtils.writeVector(buf, otherChaliceV);
                        ByteBufUtils.writeVector(buf, target);
                        ByteBufUtils.writeFluidStack(buf, otherFluid);
                    });
                    PacketChannel.CHANNEL.sendToAllAround(pkt, point);
                    return;
                }
                recipes.remove(recipe);
                recipe = LiquidInteraction.pickRecipe(recipes);
            }
        }
    }

    private boolean tickLightwellDraw() {
        if (getWorld().isBlockPowered(pos)) {
            return false;
        }
        FluidStack thisFluid = this.getTank().getFluid();
        if (!thisFluid.isEmpty() && (!(thisFluid.getFluid() instanceof FluidLiquidStarlight) || thisFluid.getAmount() + 100 >= TANK_SIZE)) {
            return false;
        }

        Vector3 thisVector = new Vector3(this).add(0.5, 1.5, 0.5);
        List<BlockPos> wellPositions = BlockDiscoverer.searchForBlocksAround(world, pos, 16,
                BlockPredicates.isBlock(BlocksAS.WELL));
        wellPositions.removeIf(pos -> {
            Vector3 wellVec = new Vector3(pos).add(0.5, 0.5, 0.5);
            RaytraceAssist assist = new RaytraceAssist(thisVector, wellVec);
            return !assist.isClear(world);
        });
        Collections.shuffle(wellPositions, rand);

        for (BlockPos wellPos : wellPositions) {
            TileWell well = MiscUtils.getTileAt(world, wellPos, TileWell.class, true);
            if (well != null) {
                FluidStack drained = well.getTank().drain(400, IFluidHandler.FluidAction.SIMULATE);
                if (drained.getFluid() instanceof FluidLiquidStarlight && drained.getAmount() > 100) {
                    int maxFillable = this.getTank().getMaxAddable(drained.getAmount());
                    if (maxFillable > 0) {
                        FluidStack actual = well.getTank().drain(new FluidStack(drained, maxFillable), IFluidHandler.FluidAction.EXECUTE);
                        this.getTank().fill(actual, IFluidHandler.FluidAction.EXECUTE);

                        Vector3 wellVec = new Vector3(wellPos).add(0.5, 0.5, 0.5);

                        PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.LIQUID_INTERACTION_LINE).addData(buf -> {
                            ByteBufUtils.writeVector(buf, wellVec);
                            ByteBufUtils.writeVector(buf, thisVector);
                            ByteBufUtils.writeFluidStack(buf, actual);
                        });
                        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(getWorld(), wellVec.toBlockPos(), 32));
                        return true;
                    }
                    return false; //Cannot fill from any other either in this case.
                }
            }
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawLiquidLine(PktPlayEffect pktPlayEffect) {
        Vector3 from = ByteBufUtils.readVector(pktPlayEffect.getExtraData());
        Vector3 to = ByteBufUtils.readVector(pktPlayEffect.getExtraData());
        FluidStack fluid = ByteBufUtils.readFluidStack(pktPlayEffect.getExtraData());
        VFXColorFunction<?> colorFn = VFXColorFunction.constant(ColorizationHelper.getColor(fluid).orElse(Color.WHITE).brighter());

        playLineGenericParticles(from, to, 0.1F + rand.nextFloat() * 0.2F, colorFn);
        playLineGenericParticles(from, to, 0.1F + rand.nextFloat() * 0.2F, colorFn);
        playLineFluidParticles(from, to, 0.25F + rand.nextFloat() * 0.2F, fluid);
    }

    @OnlyIn(Dist.CLIENT)
    private static void playLineGenericParticles(Vector3 from, Vector3 to, float width, VFXColorFunction<?> colorFn) {
        playLineParticles(from, to, width, at -> EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(at)
                .color(colorFn)
                .alpha(VFXAlphaFunction.FADE_OUT)
                .setMotion(Vector3.random().multiply(0.01F))
                .setScaleMultiplier(0.15F + rand.nextFloat() * 0.25F)
                .setMaxAge(20 + rand.nextInt(35)));
    }

    @OnlyIn(Dist.CLIENT)
    private static void playLineFluidParticles(Vector3 from, Vector3 to, float width, FluidStack fluid) {
        Color c = new Color(fluid.getFluid().getAttributes().getColor(fluid));
        playLineParticles(from, to, width, at -> EffectHelper.of(EffectTemplatesAS.CUBE_TRANSLUCENT_ATLAS)
                .spawn(at)
                .setTextureAtlasSprite(RenderingUtils.getParticleTexture(fluid))
                .tumble()
                .color(VFXColorFunction.constant(c))
                .setMotion(Vector3.random().multiply(0.01F))
                .setScaleMultiplier(0.2F + rand.nextFloat() * 0.05F)
                .scale(VFXScaleFunction.SHRINK)
                .setMaxAge(10 + rand.nextInt(15)));
    }

    @OnlyIn(Dist.CLIENT)
    private static void playLineParticles(Vector3 from, Vector3 to, float width, Function<Vector3, EntityVisualFX> pCreator) {
        to.clone().subtract(from).stepAlongVector(width, v -> pCreator.apply(from.clone().add(v)));
    }

    @Nonnull
    public SimpleSingleFluidTank getTank() {
        return this.tank;
    }

    @Nonnull
    public IFluidHandler getTankAccess() {
        return this.access.getCapability(Direction.DOWN).orElse(null);
    }

    @Nonnull
    public Vector3 getRotation() {
        return rotation;
    }

    @Nonnull
    public Vector3 getPrevRotation() {
        return prevRotation;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.tank.readNBT(compound.getCompound("tank"));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("tank", this.tank.writeNBT());
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

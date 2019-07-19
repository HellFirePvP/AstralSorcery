/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefactionContext;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.base.network.TileReceiverBase;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverWell;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import hellfirepvp.astralsorcery.common.util.tile.PrecisionSingleFluidTank;
import hellfirepvp.astralsorcery.common.util.tile.TileInventoryFiltered;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileWell
 * Created by HellFirePvP
 * Date: 30.06.2019 / 21:53
 */
public class TileWell extends TileReceiverBase<StarlightReceiverWell> {

    private static final int MAX_CAPACITY = 2000;

    private WellLiquefaction runningRecipe = null;

    private PrecisionSingleFluidTank tank;
    private TileInventoryFiltered inventory;
    private double starlightBuffer = 0;
    private float posDistribution = -1;

    public TileWell() {
        super(TileEntityTypesAS.WELL);

        this.tank = new PrecisionSingleFluidTank(MAX_CAPACITY, Direction.DOWN);
        this.tank.setAllowInput(false);
        this.tank.setOnUpdate(this::markForUpdate);

        this.inventory = new TileInventoryFiltered(this, () -> 1, Direction.DOWN);
        this.inventory.canExtract((slot, amount, existing) -> false);
        this.inventory.canInsert((slot, toAdd, existing) -> {
            if (toAdd.isEmpty()) {
                return true;
            }
            return existing.isEmpty() && RecipeTypesAS.TYPE_WELL
                    .findRecipe(Dist.DEDICATED_SERVER, new WellLiquefactionContext(this)) != null;
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            if (this.doesSeeSky()) {
                this.collectStarlight();
            }

            ItemStack stack = this.inventory.getStackInSlot(0);
            if (!stack.isEmpty()) {
                if (!getWorld().isAirBlock(getPos().up())) {
                    breakCatalyst();
                } else {
                    if (runningRecipe == null) {
                        runningRecipe = RecipeTypesAS.TYPE_WELL.findRecipe(Dist.DEDICATED_SERVER, new WellLiquefactionContext(this));
                    }

                    if (runningRecipe != null) {
                        double gain = Math.sqrt(starlightBuffer) * runningRecipe.getProductionMultiplier();
                        if (gain > 0 && tank.getFluidAmount() <= MAX_CAPACITY) {
                            if (tank.getFluidAmount() <= MAX_CAPACITY) {
                                markForUpdate();
                            }
                            fillAndDiscardRest(runningRecipe, gain);
                            if (rand.nextInt(1500) == 0) {
                                // TODO entities
                                //EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
                            }
                        }
                        starlightBuffer = 0;
                        if (rand.nextInt(1 + (int) (1000 * runningRecipe.getShatterMultiplier())) == 0) {
                            breakCatalyst();
                            // TODO entities
                            //EntityFlare.spawnAmbient(world, new Vector3(this).add(-3 + rand.nextFloat() * 7, 0.6, -3 + rand.nextFloat() * 7));
                        }
                    } else {
                        breakCatalyst();
                    }
                }
            }
            this.starlightBuffer = 0;
        } else {
            doClientEffects();
        }
    }

    private void fillAndDiscardRest(WellLiquefaction recipe, double gain) {
        Fluid produced = recipe.getFluidOutput().getFluid();
        if (tank.getFluidAmount() < 10) {
            tank.setFluid(produced);
        }

        if (tank.getTankFluid() == null) {
            tank.setFluid(produced);
        } else if (!produced.equals(tank.getTankFluid())) {
            return;
        }
        tank.addAmount(gain);
    }

    public void breakCatalyst() {
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.runningRecipe = null;

        PktPlayEffect effect = new PktPlayEffect(PktPlayEffect.Type.WELL_CATALYST_BREAK)
                .setPos(new Vector3(this));
        PacketChannel.CHANNEL.sendToAllAround(effect, PacketChannel.pointFromPos(getWorld(), getPos(), 32));
        SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, getWorld(), getPos(), 1F, 1F);
        markForUpdate();
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientEffects() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        if(!stack.isEmpty()) {
            runningRecipe = RecipeTypesAS.TYPE_WELL.findRecipe(Dist.CLIENT, new WellLiquefactionContext(this));

            if (runningRecipe != null) {
                Color color = Color.WHITE;
                if (runningRecipe.getCatalystColor() != null) {
                    color = runningRecipe.getCatalystColor();
                }
                doCatalystEffect(color);
            }
        }
        // TODO fluids
        //if(tank.getFluidAmount() > 0 &&
        //        tank.getTankFluid() instanceof FluidLiquidStarlight) {
        //    doStarlightEffect();
        //}
    }

    @OnlyIn(Dist.CLIENT)
    private void doCatalystEffect(Color color) {
        if (rand.nextInt(6) == 0) {
            Vector3 at = new Vector3(this)
                    .add(0.5, 1.3, 0.5)
                    .add(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                            rand.nextFloat() * 0.1,
                            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(VFXColorFunction.constant(color));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void catalystBurst(PktPlayEffect event) {
        BatchRenderContext<? extends FXFacingParticle> ctx;
        switch (rand.nextInt(3)) {
            case 2:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_3;
                break;
            case 1:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_2;
                break;
            default:
            case 0:
                ctx = EffectTemplatesAS.CRYSTAL_BURST_1;
                break;
        }
        EffectHelper.of(ctx)
                .spawn(event.getPos().add(0.5, 1.3, 0.5))
                .setScaleMultiplier(1.5F);
    }

    private void collectStarlight() {
        double sbDayDistribution = DayTimeHelper.getCurrentDaytimeDistribution(world);
        sbDayDistribution = 0.3 + (0.7 * sbDayDistribution);
        int yLevel = getPos().getY();
        float dstr;
        if(yLevel > 120) {
            dstr = 1F;
        } else {
            dstr = yLevel / 120F;
        }
        if(posDistribution == -1) {
            posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, getPos());
        }

        sbDayDistribution *= dstr;
        sbDayDistribution *= 1 + (1.2 * posDistribution);
        starlightBuffer += Math.max(0.0001, sbDayDistribution);
    }

    public void receiveStarlight(double amount) {
        this.starlightBuffer += amount;
        this.markForUpdate();
    }

    @Nonnull
    public PrecisionSingleFluidTank getTank() {
        return tank;
    }

    @Nonnull
    public TileInventoryFiltered getInventory() {
        return inventory;
    }

    @Nonnull
    @Override
    public StarlightReceiverWell provideEndpoint(BlockPos at) {
        return new StarlightReceiverWell(at);
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.tank.readNBT(compound.getCompound("tank"));
        this.inventory = this.inventory.deserialize(compound.getCompound("inventory"));
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        compound.put("tank", this.tank.writeNBT());
        compound.put("inventory", this.inventory.serialize());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.tank.hasCapability(cap, side)) {
            return this.tank.getCapability().cast();
        }
        if (this.inventory.hasCapability(cap, side)) {
            return this.inventory.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void onFirstTick() {}
}

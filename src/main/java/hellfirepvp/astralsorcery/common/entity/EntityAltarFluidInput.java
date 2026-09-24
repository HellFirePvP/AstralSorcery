/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.ColorExtractUtil;
import hellfirepvp.astralsorcery.common.lib.EntityDataSerializersAS;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityAltarFluidInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityAltarFluidInput extends ThrowableProjectile {

    protected static final EntityDataAccessor<BlockPos> ALTAR = SynchedEntityData.defineId(EntityAltarFluidInput.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<Vector3> TARGET = SynchedEntityData.defineId(EntityAltarFluidInput.class, EntityDataSerializersAS.VECTOR.get());
    protected static final EntityDataAccessor<ActiveAltarRecipe.AdditionalInput> INPUT_REFERENCE = SynchedEntityData.defineId(EntityAltarFluidInput.class, EntityDataSerializersAS.ALTAR_INPUT_REFERENCE.get());
    protected static final EntityDataAccessor<FluidStack> FLUID = SynchedEntityData.defineId(EntityAltarFluidInput.class, EntityDataSerializersAS.FLUID_STACK.get());

    private Vector3 rotation = new Vector3();
    private Vector3 prevRotation = new Vector3();
    private Vector3 rotationVec;

    private final ClientObject<ColorWrapper> clientColor = new ClientObject<>();

    protected EntityAltarFluidInput(EntityType<? extends EntityAltarFluidInput> entityType, Level level) {
        super(entityType, level);
    }

    public static EntityType.EntityFactory<EntityAltarFluidInput> factory() {
        return EntityAltarFluidInput::new;
    }

    public void refreshReference(BlockPos altarPos, ActiveAltarRecipe.AdditionalInput input, FluidStack fluidStack) {
        this.getEntityData().set(ALTAR, altarPos);
        this.getEntityData().set(INPUT_REFERENCE, input, true);
        this.getEntityData().set(FLUID, fluidStack.copy());
    }

    public boolean hasValidAltarReference() {
        return MiscUtil.getTileAt(this.level(), this.getAltarPos(), TileAltar.class, true).map(altar -> {
            ActiveAltarRecipe.AdditionalInput inputReference = this.getInputReference();
            if (inputReference == null) return false;
            if (this.level() instanceof ServerLevel sLevel) {
                if (!inputReference.isValid(sLevel, altar)) return false;
            }
            return altar.getTileData().getActiveRecipe().map(activeRecipe -> {
                ActiveAltarRecipe.AdditionalInput input = activeRecipe.getAdditionalInput(inputReference.isItem(), inputReference.getInputIndex());
                if (input == null) return false;
                return input.getCapturedEntityUUID().map(id -> id.equals(this.getUUID())).orElse(false);
            }).orElse(false);
        }).orElse(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ALTAR, BlockPos.ZERO);
        builder.define(TARGET, new Vector3());
        builder.define(INPUT_REFERENCE, ActiveAltarRecipe.AdditionalInput.EMPTY);
        builder.define(FLUID, FluidStack.EMPTY);
    }

    public BlockPos getAltarPos() {
        return this.getEntityData().get(ALTAR);
    }

    public ActiveAltarRecipe.AdditionalInput getInputReference() {
        return this.getEntityData().get(INPUT_REFERENCE);
    }

    public FluidStack getFluid() {
        return this.getEntityData().get(FLUID);
    }

    public Vector3 getRotation() {
        return this.rotation;
    }

    public Vector3 getPrevRotation() {
        return this.prevRotation;
    }

    public Optional<FluidStack> getActuallyDrawnFluid() {
        return MiscUtil.getTileAt(this.level(), this.getAltarPos(), TileAltar.class, true)
                .map(TileAltar::getTileData)
                .flatMap(TileAltar.Data::getActiveRecipe)
                .map(activeRecipe -> {
                    return activeRecipe.getDrawnFluid().getOrDefault(this.getInputReference().getInputIndex(), FluidStack.EMPTY);
                });
    }

    public float getDrawnFilledPercentage() {
        return this.getActuallyDrawnFluid().map(filled -> {
            FluidStack required = this.getFluid();
            return (float) filled.getAmount() / required.getAmount();
        }).orElse(0F);
    }

    @Override
    public Component getName() {
        Component component = this.getCustomName();
        if (component != null) return component;
        FluidStack fluid = this.getFluid();
        if (!fluid.isEmpty()) return fluid.getHoverName();
        return super.getName();
    }

    @Override
    protected double getDefaultGravity() {
        return this.hasValidAltarReference() ? 0 : super.getDefaultGravity();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.playClientParticles();
        }

        if (!this.hasValidAltarReference()) {
            if (this.level() instanceof ServerLevel) {
                this.discard();
            }
            return;
        }

        Vector3 target = this.getInputReference().getTargetPosition();
        Vector3 targetDir = VectorUtil.getVortexMotion(new Vector3(this), target, 256, 0.6F * target.distance(this));
        this.setDeltaMovement(targetDir.toVector3d());

        if (this.level().isClientSide()) {
            if (this.rotationVec == null) {
                this.rotationVec = Vector3.random(this.random).normalize().multiply(2);
            }
            this.prevRotation = this.rotation.copy();
            this.rotation.add(this.rotationVec);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playClientParticles() {
        if (!this.hasValidAltarReference()) return;

        float filledPerc = this.getDrawnFilledPercentage();
        AABB entityBox = this.getDimensions(this.getPose()).makeBoundingBox(Vec3.ZERO).inflate(0.05F);
        Vector3 pos = new Vector3(this);
        for (int i = 0; i < 1; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos.copy().add(Vector3.randomInAABB(entityBox, random).multiply(filledPerc * 0.8F + 0.2F)))
                    .alpha(FXAlphaFunction.fadeIn(4).andThen(FXAlphaFunction.FADE_OUT))
                    .setMotion(Vector3.random(random).normalize().multiply(random.nextFloat() * 0.002F))
                    .setMaxAge(30 + random.nextInt(20));
        }
        for (int i = 0; i < 1; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos.copy().add(Vector3.randomInAABB(entityBox, random).multiply(filledPerc * 0.8F + 0.2F)))
                    .color(FXColorFunction.constant(this.resolveClientColor().orElse(ColorWrapper.WHITE)))
                    .alpha(FXAlphaFunction.fadeIn(4).andThen(FXAlphaFunction.FADE_OUT))
                    .setMotion(Vector3.random(random).normalize().multiply(0.003F + random.nextFloat() * 0.006F))
                    .setMaxAge(15 + random.nextInt(20));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Optional<ColorWrapper> resolveClientColor() {
        if (this.clientColor.isNull()) {
            ColorExtractUtil.getColor(this.getFluid()).ifPresent(this.clientColor::set);
        }
        return Optional.ofNullable(this.clientColor.get());
    }
}

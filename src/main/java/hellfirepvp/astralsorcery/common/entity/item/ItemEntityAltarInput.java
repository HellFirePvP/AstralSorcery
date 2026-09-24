/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity.item;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.ColorExtractUtil;
import hellfirepvp.astralsorcery.common.entity.ItemEntityHighlighted;
import hellfirepvp.astralsorcery.common.lib.EntityDataSerializersAS;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityAltarInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ItemEntityAltarInput extends ItemEntityHighlighted {

    protected static final EntityDataAccessor<BlockPos> ALTAR = SynchedEntityData.defineId(ItemEntityAltarInput.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<Vector3> TARGET = SynchedEntityData.defineId(ItemEntityAltarInput.class, EntityDataSerializersAS.VECTOR.get());
    protected static final EntityDataAccessor<ActiveAltarRecipe.AdditionalInput> INPUT_REFERENCE = SynchedEntityData.defineId(ItemEntityAltarInput.class, EntityDataSerializersAS.ALTAR_INPUT_REFERENCE.get());

    private final ClientObject<ColorWrapper> clientColor = new ClientObject<>();

    public ItemEntityAltarInput(EntityType<? extends ItemEntityAltarInput> entityType, Level level) {
        super(entityType, level);
        this.bobOffs = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.refreshDimensions();
        this.setUnlimitedLifetime();
    }

    public ItemEntityAltarInput(EntityType<? extends ItemEntityAltarInput> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        this(entityType, level, posX, posY, posZ, itemStack, level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntityAltarInput(EntityType<? extends ItemEntityAltarInput> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        this(entityType, level);
        this.setPos(posX, posY, posZ);
        this.setDeltaMovement(deltaX, deltaY, deltaZ);
        this.setItem(itemStack);
        this.setUnlimitedLifetime();
    }

    public static EntityType.EntityFactory<ItemEntityAltarInput> factory() {
        return ItemEntityAltarInput::new;
    }

    public void refreshReference(BlockPos altarPos, ActiveAltarRecipe.AdditionalInput input) {
        this.getEntityData().set(ALTAR, altarPos);
        this.getEntityData().set(INPUT_REFERENCE, input, true);
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
        super.defineSynchedData(builder);

        builder.define(ALTAR, BlockPos.ZERO);
        builder.define(TARGET, new Vector3());
        builder.define(INPUT_REFERENCE, ActiveAltarRecipe.AdditionalInput.EMPTY);
    }

    public BlockPos getAltarPos() {
        return this.getEntityData().get(ALTAR);
    }

    public ActiveAltarRecipe.AdditionalInput getInputReference() {
        return this.getEntityData().get(INPUT_REFERENCE);
    }

    @Override
    protected void moveTowardsClosestSpace(double x, double y, double z) {
        if (this.hasValidAltarReference()) return;
        super.moveTowardsClosestSpace(x, y, z);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance / 16F);
    }

    @Override
    public Optional<ColorWrapper> getColor() {
        if (!this.hasValidAltarReference()) return super.getColor();
        if (this.level().isClientSide()) {
            return this.resolveClientColor();
        } else {
            return Optional.of(ColorWrapper.WHITE);
        }
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
            this.setExtendedLifetime();
            return;
        }

        Vector3 target = this.getInputReference().getTargetPosition();
        Vector3 targetDir = VectorUtil.getVortexMotion(new Vector3(this), target, 256, 0.6F * target.distance(this));
        this.setDeltaMovement(targetDir.toVector3d());
    }

    @OnlyIn(Dist.CLIENT)
    private void playClientParticles() {
        if (!this.hasValidAltarReference()) return;

        AABB entityBox = this.getDimensions(this.getPose()).makeBoundingBox(Vec3.ZERO).inflate(0.05F);
        Vector3 pos = new Vector3(this).addY(0.2F);
        for (int i = 0; i < 1; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos.copy().add(Vector3.randomInAABB(entityBox, random)))
                    .alpha(FXAlphaFunction.fadeIn(4).andThen(FXAlphaFunction.FADE_OUT))
                    .setMotion(Vector3.random(this.random).normalize().multiply(random.nextFloat() * 0.002F))
                    .setMaxAge(30 + random.nextInt(20));
        }
        for (int i = 0; i < 1; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos.copy().add(Vector3.randomInAABB(entityBox, random)))
                    .color(FXColorFunction.constant(this.getColor().orElse(ColorWrapper.WHITE)))
                    .alpha(FXAlphaFunction.fadeIn(4).andThen(FXAlphaFunction.FADE_OUT))
                    .setMotion(Vector3.random(this.random).normalize().multiply(0.003F + random.nextFloat() * 0.006F))
                    .setMaxAge(15 + random.nextInt(20));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Optional<ColorWrapper> resolveClientColor() {
        if (this.clientColor.isNull()) {
            ColorExtractUtil.getColor(this.getItem()).ifPresent(this.clientColor::set);
        }
        return Optional.ofNullable(this.clientColor.get());
    }
}

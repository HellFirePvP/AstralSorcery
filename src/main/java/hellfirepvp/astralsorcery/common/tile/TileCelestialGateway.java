/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXRefreshFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXColorSphere;
import hellfirepvp.astralsorcery.client.helper.GatewayInterfaceRenderHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenStackList;
import hellfirepvp.astralsorcery.common.tile.base.TileDataOwned;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileCelestialGateway
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileCelestialGateway extends TileEntityTick<TileCelestialGateway.Data> implements Nameable {

    private final ClientObject<VFXColorSphere> colorSphereEffect = new ClientObject<>();
    private boolean isGatewayRegistered = false;

    public TileCelestialGateway(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.CELESTIAL_GATEWAY, pos, blockState);
    }

    protected TileCelestialGateway(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (this.hasStructure() && this.doesSeeSky()) {
            if (!this.isGatewayRegistered) {
                Data data = this.getTileData();
                DataAS.DOMAIN_AS.getData(level, DataAS.KEY_CELESTIAL_GATEWAY_DATA)
                        .addGateway(level, this.getBlockPos(), data.getCustomName(), data.getColor());
                this.isGatewayRegistered = true;
            }
        } else if (this.isGatewayRegistered) {
            DataAS.DOMAIN_AS.getData(level, DataAS.KEY_CELESTIAL_GATEWAY_DATA).removeGateway(level, this.getBlockPos());
            this.isGatewayRegistered = false;
        }
    }

    public void refreshGatewayRegistration() {
        this.isGatewayRegistered = false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        this.setupGatewayUIEffect();
        this.playGatewayEffects();
    }

    @OnlyIn(Dist.CLIENT)
    protected void setupGatewayUIEffect() {
        if (!this.hasStructure() || !this.doesSeeSky()) {
            this.colorSphereEffect.ifPresent(EntityFX::requestRemoval);
            this.colorSphereEffect.set(null);
            return;
        }

        Entity camera = Minecraft.getInstance().cameraEntity;
        if (camera == null) {
            this.colorSphereEffect.ifPresent(EntityFX::requestRemoval);
            this.colorSphereEffect.set(null);
            return;
        }

        float eyeHeight = camera.getDimensions(Pose.STANDING).eyeHeight();
        Vector3 effectPos = Vector3.atBottomCenter(this).addY(eyeHeight);
        double dst = new Vector3(camera).distance(effectPos);
        float cutoff = 4.5F;
        if (dst >= cutoff) {
            this.colorSphereEffect.ifPresent(EntityFX::requestRemoval);
            this.colorSphereEffect.set(null);
        }

        if (dst < cutoff) {
            if (this.colorSphereEffect.isNull()) {
                VFXColorSphere sphere = EffectHelper.of(EffectTemplatesAS.COLOR_SPHERE)
                        .spawn(effectPos)
                        .setup(Vector3.RotAxis.Y_AXIS, 6F)
                        .alpha(FXAlphaFunction.distance(1F, cutoff))
                        .color(FXColorFunction.BLACK)
                        .position((fx, pos, movement) -> Vector3.atBottomCenter(this).addY(eyeHeight))
                        .refresh(FXRefreshFunction.tileExistsAnd(this, (te, fx) -> te.doesSeeSky() && te.hasStructure()));
                this.colorSphereEffect.set(sphere);
            } else if (this.colorSphereEffect.get().isRemoved()) {
                EffectHelper.refresh(EffectTemplatesAS.COLOR_SPHERE, this.colorSphereEffect.get());
            }
        }

        if (dst < 5.5) {
            Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
        }
        if (dst < 2.5) {
            GatewayInterfaceRenderHelper.getInstance().tryCreateUI(level, this.getBlockPos(), effectPos, 5.5F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void playGatewayEffects() {
        if (!this.hasStructure() || !this.doesSeeSky()) return;

        ColorWrapper color = ColorsAS.DYE_COLORS[this.getTileData().getColor().getId()];
        for (int i = 0; i < 3; i++) {
            Vector3 at = new Vector3(this).add(-2, 0, -2);
            if (rand.nextBoolean()) {
                at.addZ(rand.nextFloat() * 5);
                if (rand.nextBoolean()) at.addX(5);
            } else {
                at.addX(rand.nextFloat() * 5);
                if (rand.nextBoolean()) at.addZ(5);
            }

            ColorWrapper display = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, color, color.brighter()).orElseThrow();
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(FXColorFunction.constant(display))
                    .setScale(0.2F + rand.nextFloat() * 0.1F)
                    .setGravity(Vector3.y(0.00006F))
                    .setMaxAge(30 + rand.nextInt(20));
        }

        for (int i = 0; i < 1; i++) {
            Vector3 at = VectorUtil.withRandomOffset(Vector3.atBottomCenter(this), rand, 2.5F);
            at.setY(this.getBlockPos().getY());

            ColorWrapper display = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, color, color.brighter()).orElseThrow();
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at)
                    .color(FXColorFunction.constant(display))
                    .setScale(0.15F + rand.nextFloat() * 0.1F)
                    .setGravity(Vector3.y(0.0001F))
                    .setMaxAge(20 + rand.nextInt(10));
        }

        if (this.getTileData().isLocked() && this.getTileData().hasOwner()) {
            Vector3 center = Vector3.atBottomCenter(this).addY(0.2F);
            for (int i = 0; i < 4; i++) {
                Vector3 offset = Vector3.random(rand).setY(0).normalize().multiply(1F);
                Vector3 at = VectorUtil.withRandomOffset(center, rand, 0.08F).add(offset);

                ColorWrapper display = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, ColorsAS.DYE_LIGHT_BLUE, ColorsAS.DYE_BLUE).orElseThrow();
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .color(FXColorFunction.constant(display))
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.08F + rand.nextFloat() * 0.04F)
                        .setMaxAge(20 + rand.nextInt(10));

            }

            for (int i = 0; i < 2; i++) {
                Vector3 offset = Vector3.random(rand).setY(0).normalize().multiply(0.6F).addY(0.1F);
                Vector3 at = VectorUtil.withRandomOffset(center, rand, 0.04F).add(offset);

                ColorWrapper display = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, ColorsAS.DYE_LIGHT_BLUE, ColorsAS.DYE_BLUE).orElseThrow();
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .color(FXColorFunction.constant(display))
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(0.08F + rand.nextFloat() * 0.04F)
                        .setMaxAge(20 + rand.nextInt(10));

            }
        }
    }

    @Override
    public boolean seesSkyInNoSkyWorlds() {
        return true;
    }

    @Nullable
    @Override
    public ObserverRegistryObject getRequiredObserver() {
        return ObserversAS.STRUCTURE_CELESTIAL_GATEWAY;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);

        Component customName = componentInput.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            this.getTileData().setCustomName(customName);
        }
        ColorComponent colorCmp = componentInput.get(DataComponentsAS.COLOR);
        if (colorCmp != null) {
            colorCmp.reference().asDyeColor().ifPresent(dyeColor -> {
                this.getTileData().setColor(dyeColor);
            });
        }

        this.getTileData().markForUpdate();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (this.hasCustomName()) {
            components.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        }
        if (this.getTileData().getColor() != null) {
            components.set(DataComponentsAS.COLOR, new ColorComponent(ColorReference.Dye.of(this.getTileData().getColor())));
        }
    }

    @Override
    public Component getName() {
        return ObjectUtils.firstNonNull(this.getCustomName(), BlocksAS.CELESTIAL_GATEWAY.get().getName());
    }

    @Override
    public boolean hasCustomName() {
        return this.getTileData().getCustomName() != null;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.getTileData().getCustomName();
    }

    @Override
    public void onTileEntityRemove(Level level, BlockPos pos) {
        super.onTileEntityRemove(level, pos);

        if (level instanceof ServerLevel sLevel) {
            DataAS.DOMAIN_AS.getData(sLevel, DataAS.KEY_CELESTIAL_GATEWAY_DATA).removeGateway(level, pos);
        }
    }

    public static class Data extends TileEntityTick.Data implements TileDataOwned.Lockable {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> gatewayFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P7<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Optional<UUID>, Boolean, Optional<Component>, DyeColor> gatewayFields(RecordCodecBuilder.Instance<T> instance) {
            return tickFields(instance)
                    .and(TileDataOwned.Lockable.ownedLockableFields(instance))
                    .and(instance.group(
                            CodecUtil.optional(ComponentSerialization.CODEC, "component", Data::getCustomName),
                            CodecUtil.defaulted(DyeColor.CODEC, "color", () -> DyeColor.YELLOW, Data::getColor)
                    ));
        }

        private Component customName;
        private DyeColor color;
        protected UUID ownerId;
        protected boolean locked;

        protected Data(long ticksExisted,
                       boolean hasStructure,
                       Map<BlockPos, Boolean> skyObstructions,
                       Optional<UUID> ownerId,
                       boolean locked,
                       Optional<Component> customName,
                       DyeColor color) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.ownerId = ownerId.orElse(null);
            this.locked = locked;
            this.customName = customName.orElse(null);
            this.color = color;
        }

        public DyeColor getColor() {
            return this.color;
        }

        public void setColor(DyeColor color) {
            this.color = color;
            this.refreshGatewayRegistration();
        }

        @Nullable
        public Component getCustomName() {
            return this.customName;
        }

        public void setCustomName(Component customName) {
            this.customName = customName;
            this.refreshGatewayRegistration();
        }

        @Nullable
        @Override
        public UUID getOwnerId() {
            return this.ownerId;
        }

        @Override
        public void setOwnerId(UUID ownerId) {
            this.ownerId = ownerId;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public boolean isLocked() {
            return this.locked;
        }

        private void refreshGatewayRegistration() {
            this.getOptionalTile(TileCelestialGateway.class).ifPresent(TileCelestialGateway::refreshGatewayRegistration);
        }
    }
}

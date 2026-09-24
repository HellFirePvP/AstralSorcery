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
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.source.FXOrbitalSource;
import hellfirepvp.astralsorcery.client.effect.source.orbital.FXFocusCrystalOrbitalSource;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.property.FocusCrystalProperty;
import hellfirepvp.astralsorcery.common.constellation.property.FocusCrystalSortProperty;
import hellfirepvp.astralsorcery.common.event.helper.TemporaryFlightHelper;
import hellfirepvp.astralsorcery.common.focal.FocusCrystalPlacementHelper;
import hellfirepvp.astralsorcery.common.focal.observer.FocusCrystalFilamentObserver;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.tile.base.TileDataConstellationContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileDataCrystalAttributeContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.tile.network.FocusCrystalSourceNode;
import hellfirepvp.astralsorcery.common.tile.network.provider.FocusCrystalSourceNodeProvider;
import hellfirepvp.astralsorcery.common.util.ClientObject;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.ObserverRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileStarlightFocusCrystal
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileStarlightFocusCrystal extends TileEntityNetwork<FocusCrystalSourceNode, TileStarlightFocusCrystal.Data> {

    private static final AABB PLAYER_BOX = AABB.ofSize(Vec3.atCenterOf(Vec3i.ZERO), 17, 17, 17);

    private final List<ClientObject<FXOrbitalSource>> orbitals = new ArrayList<>();

    public TileStarlightFocusCrystal(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.STARLIGHT_FOCUS_CRYSTAL, pos, blockState);
    }

    protected TileStarlightFocusCrystal(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        IntStream.range(0, 4).forEach(index -> this.orbitals.add(new ClientObject<>()));
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        Data data = this.getTileData();
        if (data.getTicksExisted() == 1) {
            DataAS.DOMAIN_AS.getData(level, DataAS.KEY_FOCAL_POINT_DATA)
                    .getNode(this.getBlockPos())
                    .ifPresent(node -> {
                        node.updateFocalPosition(level);
                        node.markDirty(level);
                        node.markForSync(level);
                    });
        }

        if (data.getTicksExisted() % 20 == 0 && data.getConstellation().isPresent()) {
            // Update states
            this.doesSeeSky();
            this.hasStructure();

            DataAS.DOMAIN_AS.getData(level, DataAS.KEY_FOCAL_POINT_DATA)
                    .getNode(this.getBlockPos())
                    .filter(focalPoint -> data.getConstellation().get().equals(focalPoint.getConstellation()))
                    .ifPresent(focalPoint -> {
                        this.getNetworkNode().ifPresent(node -> {
                            if (!node.getConstellation().equals(data.getConstellation())) {
                                node.setConstellation(data.getConstellation().orElse(null));
                                node.markDirty(level);
                            }
                            if (!node.getCrystalProperties().equals(data.getCrystalAttributes())) {
                                node.setCrystalProperties(data.getCrystalAttributes());
                                node.markDirty(level);
                            }
                        });
                        focalPoint.getFocalPosition().ifPresent(focusPos -> {
                            boolean isFocal = focusPos.equals(this.getBlockPos());

                            if (isFocal) {
                                level.getNearbyPlayers(TargetingConditions.forNonCombat(), null, PLAYER_BOX.move(this.getBlockPos()))
                                        .forEach(player -> TemporaryFlightHelper.allowFlight(player, 60));
                            }

                            if (this.getTileData().isOnFocalNode() != isFocal) {
                                this.getTileData().setIsOnFocalNode(isFocal);
                                this.getTileData().markForUpdate();
                            }
                        });
                    });
        }

        data.getConstellation()
                .flatMap(cst -> cst.getPropertyOpt(FocusCrystalProperty.KEY))
                .ifPresent(prop -> {
                    this.getStructureObserver().ifPresent(subscriber -> {
                        this.getNetworkNode().ifPresent(node -> {
                            if (subscriber.getObserver() instanceof FocusCrystalFilamentObserver relayObserver) {
                                Set<BlockPos> capturedFilaments = relayObserver.getPositions();
                                Set<Integer> layers = new HashSet<>(data.getValidLayers());
                                data.clearValidLayers();

                                int layerCount = 0;
                                if (FocusCrystalPlacementHelper.allPositionsFulfillGlobalRules(level, this.getBlockPos(), capturedFilaments)) {
                                    Collection<Set<BlockPos>> splitY = capturedFilaments.stream()
                                            .collect(Collectors.groupingBy(Vec3i::getY, Collectors.toSet()))
                                            .values();
                                    for (Set<BlockPos> layerSet : splitY) {
                                        int yLayer = layerSet.stream().findFirst().map(Vec3i::getY).orElse(-1);
                                        if (yLayer != -1 && prop.isValidLayer(level, this.worldPosition, layerSet)) {
                                            layerCount++;
                                            data.addValidLayer(yLayer - this.worldPosition.getY());
                                        }
                                    }
                                }

                                if (layerCount != node.getValidLayers()) {
                                    node.setValidLayers(layerCount);
                                    node.markDirty(level);
                                }
                                if (!data.getValidLayers().equals(layers)) {
                                    data.markForUpdate();
                                }
                            }
                        });
                    });
                });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        this.playStellarFilamentEffects();
        this.playFocalNodeEffects();
    }

    @OnlyIn(Dist.CLIENT)
    private void playStellarFilamentEffects() {
        Data data = this.getTileData();
        data.getConstellation().ifPresent(cst -> {
            cst.getPropertyOpt(FocusCrystalProperty.KEY).ifPresent(prop -> {
                List<BlockPos> filaments = FocusCrystalPlacementHelper.collectFocusCrystalFilaments(level, this.getBlockPos());
                if (!FocusCrystalPlacementHelper.allPositionsFulfillGlobalRules(level, this.getBlockPos(), new HashSet<>(filaments))) {
                    return;
                }
                FXAlphaFunction<?> buildingAlpha = new FXAlphaFunction<>() {
                    @Override
                    public float getAlpha(EntityVisualFX fx, float alphaIn, float pTicks) {
                        Player player = Minecraft.getInstance().player;
                        if (player == null) return alphaIn;
                        if (player.getMainHandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT) ||
                                player.getOffhandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT)) {
                            return alphaIn * 0.2F;
                        }
                        return alphaIn;
                    }
                };

                Collection<Set<BlockPos>> splitY = filaments.stream()
                        .collect(Collectors.groupingBy(Vec3i::getY, Collectors.toSet()))
                        .values();
                for (Set<BlockPos> layerSet : splitY) {
                    if (prop.isValidLayer(level, this.getBlockPos(), layerSet)) {
                        List<BlockPos> display = cst.getPropertyOpt(FocusCrystalSortProperty.KEY)
                                .map(FocusCrystalSortProperty::getSortFunction)
                                .orElse(FocusCrystalSortProperty.DEFAULT)
                                .apply(level, this.getBlockPos(), new ArrayList<>(layerSet));

                        if (ClientProxy.getClientTick() % 40 == 0) {
                            for (int i = 0; i < display.size() - 1; i++) {
                                Vector3 from = Vector3.atCenter(display.get(i));
                                Vector3 to   = Vector3.atCenter(display.get(i + 1));

                                EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                                        .spawn(from)
                                        .setup(to, 0.4F, 0.4F)
                                        .color(FXColorFunction.constant(cst.getConstellationColor()))
                                        .alpha(FXAlphaFunction.PYRAMID.andThen(buildingAlpha));
                                EffectHelper.of(EffectTemplatesAS.LIGHT_BEAM)
                                        .spawn(to)
                                        .setup(from, 0.4F, 0.4F)
                                        .color(FXColorFunction.constant(cst.getConstellationColor()))
                                        .alpha(FXAlphaFunction.PYRAMID.andThen(buildingAlpha));
                            }
                        }

                        for (BlockPos pos : display) {
                            Vector3 effectPos = VectorUtil.withRandomOffset(Vector3.atCenter(pos), rand, 0.1F);
                            Vector3 dir = Vector3.random(rand).normalize().multiply(0.01F + rand.nextFloat() * 0.01F);

                            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                    .spawn(effectPos)
                                    .alpha(FXAlphaFunction.FADE_OUT.andThen(buildingAlpha))
                                    .color(FXColorFunction.constant(cst.getConstellationColor()))
                                    .setScale(0.4F + rand.nextFloat() * 0.2F)
                                    .setMotion(dir)
                                    .setGravity(Vector3.y(0.0002F))
                                    .setMaxAge(20 + rand.nextInt(20));
                            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                    .spawn(effectPos)
                                    .alpha(FXAlphaFunction.FADE_OUT.andThen(buildingAlpha))
                                    .setScale(0.2F + rand.nextFloat() * 0.1F)
                                    .setMotion(dir)
                                    .setGravity(Vector3.y(0.0002F))
                                    .setMaxAge(20 + rand.nextInt(8));
                        }
                    }
                }
            });
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void playFocalNodeEffects() {
        if (!this.getTileData().isOnFocalNode() ||
                !this.getTileData().hasSky() ||
                this.getTileData().getConstellation().isEmpty()) {
            return;
        }

        ColorWrapper color = this.getTileData().getConstellation().get().getConstellationColor();

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(VectorUtil.withRandomOffset(Vector3.atCenter(this), this.rand, 0.2F))
                .color(FXColorFunction.constant(color))
                .setScale(0.2F + this.rand.nextFloat() * 0.1F)
                .setAlpha(0.8F)
                .setMaxAge(20 + this.rand.nextInt(10));

        for (ClientObject<FXOrbitalSource> orbitalObject : this.orbitals) {
            if (orbitalObject.isNull() || orbitalObject.get().isRemoved()) {
                FXOrbitalSource src = EffectHelper.source(
                        new FXFocusCrystalOrbitalSource(Vector3.atCenter(this), color)
                                .setOrbitAxis(new Vector3.RotAxis(Vector3.random(this.rand)))
                                .setOrbitRadius(1F + this.rand.nextFloat() * 0.8F)
                                .setMaxAge(35 + this.rand.nextInt(25)));
                orbitalObject.set(src);
            }
        }
    }

    @Nullable
    @Override
    public ObserverRegistryObject getRequiredObserver() {
        return ObserversAS.FOCUS_CRYSTAL_FILAMENTS;
    }

    @Override
    public DeferredHolder<TransmissionNodeProvider<?>, FocusCrystalSourceNodeProvider> getNodeProvider() {
        return StarlightNetworkNodesAS.FOCUS_CRYSTAL_SOURCE_NODE;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    @Override
    public void onTileEntityRemove(Level level, BlockPos pos) {
        super.onTileEntityRemove(level, pos);

        if (level instanceof ServerLevel sLevel) {
            DataAS.DOMAIN_AS.getData(sLevel, DataAS.KEY_FOCAL_POINT_DATA)
                    .getNode(this.getBlockPos())
                    .ifPresent(node -> {
                        node.updateFocalPosition(level);
                        node.markDirty(level);
                        node.markForSync(sLevel);
                    });
        }
    }

    public static class Data extends TileEntityNetwork.Data implements TileDataConstellationContainer, TileDataCrystalAttributeContainer {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> focusCrystalFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P8<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean, Optional<BaseConstellation>, CrystalAttributesComponent, Set<Integer>, Boolean> focusCrystalFields(RecordCodecBuilder.Instance<T> instance) {
            return netFields(instance).and(instance.group(
                    RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("constellation").forGetter(Data::getConstellation),
                    CodecUtil.defaulted(CrystalAttributesComponent.CODEC, "crystalAttributes", CrystalAttributesComponent::defaultEmpty, Data::getCrystalAttributes),
                    CodecUtil.defaulted(SetCodec.of(Codec.INT), "validLayers", Collections::emptySet, Data::getValidLayers),
                    CodecUtil.defaulted(Codec.BOOL, "isOnFocalNode", () -> false, Data::isOnFocalNode)
            ));
        }

        protected BaseConstellation constellation;
        protected CrystalAttributesComponent crystalAttributes;
        protected Set<Integer> validLayers = new HashSet<>();
        protected boolean isOnFocalNode;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions, boolean needsNetworkSync, Optional<BaseConstellation> constellation, CrystalAttributesComponent crystalAttributes, Set<Integer> validLayers, boolean isOnFocalNode) {
            super(ticksExisted, hasStructure, skyObstructions, needsNetworkSync);
            this.constellation = constellation.orElse(null);
            this.crystalAttributes = crystalAttributes;
            this.validLayers.addAll(validLayers);
            this.isOnFocalNode = isOnFocalNode;
        }

        @Override
        public <T extends TileEntitySynchronized.Data> T self() {
            return MiscUtil.cast(this);
        }

        @Override
        public Optional<BaseConstellation> getConstellation() {
            return Optional.ofNullable(this.constellation);
        }

        @Override
        public void setConstellation(@Nullable BaseConstellation cst) {
            this.constellation = cst;
        }

        @Nonnull
        @Override
        public CrystalAttributesComponent getCrystalAttributes() {
            return this.crystalAttributes;
        }

        @Override
        public void setCrystalAttributes(@Nonnull CrystalAttributesComponent attributes) {
            this.crystalAttributes = attributes;
        }

        public Set<Integer> getValidLayers() {
            return Collections.unmodifiableSet(this.validLayers);
        }

        protected void addValidLayer(int layer) {
            this.validLayers.add(layer);
        }

        protected void clearValidLayers() {
            this.validLayers.clear();
        }

        protected void setIsOnFocalNode(boolean onFocalNode) {
            this.isOnFocalNode = onFocalNode;
        }

        public boolean isOnFocalNode() {
            return this.isOnFocalNode;
        }
    }
}

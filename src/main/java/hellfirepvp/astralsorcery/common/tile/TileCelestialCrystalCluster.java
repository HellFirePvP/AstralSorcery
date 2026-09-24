/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.CelestialCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.tile.base.TileDataCrystalAttributeContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import hellfirepvp.astralsorcery.common.tile.network.ForwardingStarlightReceiverNode;
import hellfirepvp.astralsorcery.common.tile.network.provider.ForwardingStarlightReceiverNodeProvider;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileCelestialCrystalCluster
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileCelestialCrystalCluster extends TileEntityNetwork<ForwardingStarlightReceiverNode, TileCelestialCrystalCluster.Data> implements ForwardingStarlightReceiverNode.ReceiverTile {

    public TileCelestialCrystalCluster(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.CELESTIAL_CRYSTAL_CLUSTER, pos, blockState);
    }

    protected TileCelestialCrystalCluster(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);
        if (this.getGrowth(level) < 4 && this.doesSeeSky()) {
            this.tryGrow(level, 1F);
        }
    }

    @Override
    public void receiveStarlight(ServerLevel sLevel, StarlightTransmissionPacket packet) {
        this.tryGrow(sLevel, 1F / packet.amount());
    }

    @Override
    public DeferredHolder<TransmissionNodeProvider<?>, ForwardingStarlightReceiverNodeProvider> getNodeProvider() {
        return StarlightNetworkNodesAS.FORWARDING_RECEIVER_NODE;
    }

    private void tryGrow(Level level, float chanceMultiplier) {
        float chance = 20 * 60 * 15;

        if (level.getBlockState(this.getBlockPos().below()).is(BlocksAS.STARMETAL_ORE)) {
            chance /= 2;

            if (rand.nextInt(20 * 60 * 20) == 0) {
                level.setBlockAndUpdate(this.getBlockPos().below(), Blocks.IRON_ORE.defaultBlockState());
            }
        }

        float distr = DayTimeHelper.getCurrentDaytimeDistribution(level);
        chance *= (1F - (0.4F * distr));
        chance *= chanceMultiplier;

        this.grow(level, Mth.ceil(chance));
    }

    public void grow(Level level, int chance) {
        int stage = this.getGrowth(level);
        if (stage < 4) {
            if (rand.nextInt(Math.max(chance, 1)) == 0) {
                this.setGrowth(level, stage + 1);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        if (level.getBlockState(this.getBlockPos().below()).is(BlocksAS.STARMETAL_ORE)) {
            if (rand.nextInt(6) == 0) {
                Vector3 pos = Vector3.positiveRandom(rand).setY(0).add(this.getBlockPos());
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .color(FXColorFunction.constant(ColorsAS.STARMETAL))
                        .alpha(FXAlphaFunction.fadeIn(5).andThen(FXAlphaFunction.FADE_OUT))
                        .setScale(0.2F + rand.nextFloat() * 0.1F)
                        .setGravity(Vector3.y(0.0002F));
            }
        }
        if (this.getGrowth(level) == 4) {
            if (rand.nextInt(3) == 0) {
                AABB bounds = this.getBlockState().getShape(level, this.getBlockPos()).bounds();

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(new Vector3(this)
                                .add(Vector3.randomInAABB(bounds, rand)))
                        .alpha(FXAlphaFunction.fadeIn(5).andThen(FXAlphaFunction.FADE_OUT))
                        .setScale(0.1F + rand.nextFloat() * 0.1F)
                        .setGravity(Vector3.y(0.0001F));
            }
        }
    }

    public int getGrowth(Level level) {
        BlockState current = level.getBlockState(getBlockPos());
        return current.getValue(CelestialCrystalClusterBlock.STAGE);
    }

    public void setGrowth(Level level, int stage) {
        BlockState next = BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.get().defaultBlockState().setValue(CelestialCrystalClusterBlock.STAGE, stage);
        level.setBlockAndUpdate(getBlockPos(), next);
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityNetwork.Data implements TileDataCrystalAttributeContainer {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> netFields(inst).and(
                CodecUtil.defaulted(CrystalAttributesComponent.CODEC, "crystalAttributes", CrystalAttributesComponent::defaultEmpty, Data::getCrystalAttributes)
        ).apply(inst , Data::new));

        protected CrystalAttributesComponent crystalAttributes;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions, boolean needsNetworkSync, CrystalAttributesComponent crystalAttributes) {
            super(ticksExisted, hasStructure, skyObstructions, needsNetworkSync);
            this.crystalAttributes = crystalAttributes;
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

        @Override
        public CrystalAttributesComponent getEmptyCrystalAttributes() {
            return ItemsAS.CELESTIAL_CRYSTAL.asItem().components()
                    .getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES.get(), CrystalAttributesComponent.defaultEmpty());
        }
    }

}

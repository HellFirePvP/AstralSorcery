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
import hellfirepvp.astralsorcery.common.block.tile.LensBlock;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.tile.base.TileDataCrystalAttributeContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import hellfirepvp.astralsorcery.common.tile.network.SimpleSingleTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.network.provider.SimpleSingleTransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLens
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLens extends TileEntityNetwork<SimpleSingleTransmissionNode, TileLens.Data> {

    public TileLens(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.LENS, pos, blockState);
    }

    protected TileLens(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public Direction getPlacedAgainst() {
        if (!this.hasLevel()) return Direction.DOWN;
        BlockState state = this.getLevel().getBlockState(this.getBlockPos());
        return state.getOptionalValue(LensBlock.PLACED_AGAINST).orElse(Direction.DOWN);
    }

    @Override
    public DeferredHolder<TransmissionNodeProvider<?>, SimpleSingleTransmissionNodeProvider> getNodeProvider() {
        return StarlightNetworkNodesAS.SIMPLE_SINGLE_NODE;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityNetwork.Data implements TileDataCrystalAttributeContainer {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> lensFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P6<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean, CrystalAttributesComponent, Optional<BlockPos>> lensFields(RecordCodecBuilder.Instance<T> instance) {
            return netFields(instance).and(instance.group(
                    CodecUtil.defaulted(CrystalAttributesComponent.CODEC, "crystalAttributes", CrystalAttributesComponent::defaultEmpty, Data::getCrystalAttributes),
                    BlockPos.CODEC.optionalFieldOf("linkedPos").forGetter(Data::getLinkedPos)
            ));
        }

        protected CrystalAttributesComponent crystalAttributes;
        protected BlockPos linkedPos;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions, boolean needsNetworkSync, CrystalAttributesComponent crystalAttributes, Optional<BlockPos> linkedPos) {
            super(ticksExisted, hasStructure, skyObstructions, needsNetworkSync);
            this.crystalAttributes = crystalAttributes;
            this.linkedPos = linkedPos.orElse(null);
        }

        public Optional<BlockPos> getLinkedPos() {
            return Optional.ofNullable(this.linkedPos);
        }

        public void setLinkedPos(@Nullable BlockPos linkedPos) {
            this.linkedPos = linkedPos;
        }

        @Nonnull
        @Override
        public CrystalAttributesComponent getCrystalAttributes() {
            return this.crystalAttributes;
        }

        @Override
        public void setCrystalAttributes(@Nonnull CrystalAttributesComponent attributes) {
            this.crystalAttributes = attributes;
            this.setNeedsNetworkSync(true);
        }
    }

}

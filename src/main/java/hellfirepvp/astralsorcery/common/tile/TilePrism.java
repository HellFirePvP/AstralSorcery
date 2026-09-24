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
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.StarlightNetworkNodesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.tile.base.TileDataCrystalAttributeContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityNetwork;
import hellfirepvp.astralsorcery.common.tile.network.SimpleTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.network.provider.SimpleTransmissionNodeProvider;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TilePrism
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TilePrism extends TileEntityNetwork<SimpleTransmissionNode, TilePrism.Data> {

    public TilePrism(BlockPos pos, BlockState blockState) {
        this(TileEntitiesAS.PRISM, pos, blockState);
    }

    protected TilePrism(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public DeferredHolder<TransmissionNodeProvider<?>, SimpleTransmissionNodeProvider> getNodeProvider() {
        return StarlightNetworkNodesAS.SIMPLE_NODE;
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityNetwork.Data implements TileDataCrystalAttributeContainer {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> prismFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P5<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Boolean, CrystalAttributesComponent> prismFields(RecordCodecBuilder.Instance<T> instance) {
            return netFields(instance).and(
                    CodecUtil.defaulted(CrystalAttributesComponent.CODEC, "crystalAttributes", CrystalAttributesComponent::defaultEmpty, Data::getCrystalAttributes)
            );
        }

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
            this.setNeedsNetworkSync(true);
        }
    }
}

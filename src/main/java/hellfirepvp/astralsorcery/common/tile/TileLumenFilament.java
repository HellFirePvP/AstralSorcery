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
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNetworkHelper;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNode;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenTransferNotifiable;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenFilament
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenFilament extends TileEntityTick<TileLumenFilament.Data> implements LumenTransferNotifiable {

    private boolean nodeAdded = false;

    public TileLumenFilament(BlockPos pos, BlockState blockState) {
        super(TileEntitiesAS.LUMEN_FILAMENT, pos, blockState);
    }

    protected TileLumenFilament(TileRegistryObject<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (!this.nodeAdded) {
            this.nodeAdded = true;
            LumenNetworkHelper.createNode(level, this.getBlockPos(), this.getBlockPos().getCenter(), LumenNode.ConnectionType.TRANSMISSION);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);

        Lumen transmittedLumen = LumenAS.NONE.get();
        if (this.getTileData().getTransmittedLumenGameTime() + 3 * 20 > level.getGameTime()) {
            transmittedLumen = this.getTileData().getRecentlyTransmittedLumen();
        }

        for (int i = 0; i < 2; i++) {
            Vector3 pos = Vector3.atBottomCenter(this).addY(0.2F);
            pos = VectorUtil.withRandomOffset(pos, rand, 0.08F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(pos)
                    .color(FXColorFunction.constant(rand.nextInt(3) == 0 ? ColorWrapper.WHITE : transmittedLumen.getColor(level, pos)))
                    .setScale(0.12F + rand.nextFloat() * 0.2F)
                    .alpha(FXAlphaFunction.FADE_OUT)
                    .setGravity(Vector3.y(0.0012F))
                    .setMaxAge(24 + rand.nextInt(10));
        }

        if (transmittedLumen != LumenAS.NONE.get() && rand.nextInt(20) == 0) {
            Vector3 pos = VectorUtil.withRandomOffset(Vector3.atCenter(this), rand, 0.3F);

            EffectHelper.of(EffectTemplatesAS.LUMEN_PARTICLE)
                    .spawn(pos)
                    .setSprite(TexturesAS.ATLAS_LUMEN, RegistriesAS.REGISTRY_LUMEN.getKey(transmittedLumen))
                    .setAlpha(0.3F)
                    .color(FXColorFunction.constant(transmittedLumen.getColor(level, pos)))
                    .setGravity(Vector3.y(0.00007F));
        }
    }

    @Override
    public void onTransfer(ServerLevel sLevel, Lumen type) {
        this.getTileData().setTransmittedLumen(type, sLevel.getGameTime());
        this.getTileData().markForUpdate();
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityTick.Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> lumenFilamentFields(inst).apply(inst, Data::new));

        protected static <T extends TileLumenFilament.Data> Products.P5<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Lumen, Long> lumenFilamentFields(RecordCodecBuilder.Instance<T> instance) {
            return TileEntityTick.Data.tickFields(instance).and(instance.group(
                    CodecUtil.lenientDefaulted(RegistriesAS.REGISTRY_LUMEN.byNameCodec(), "recentlyTransmittedLumen", LumenAS.NONE, Data::getRecentlyTransmittedLumen),
                    CodecUtil.defaulted(Codec.LONG, "transmittedLumenGameTime", () -> 0L, Data::getTransmittedLumenGameTime)
            ));
        }

        protected Lumen recentlyTransmittedLumen;
        protected long transmittedLumenGameTime;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions, Lumen recentlyTransmittedLumen, long transmittedLumenGameTime) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.recentlyTransmittedLumen = recentlyTransmittedLumen;
            this.transmittedLumenGameTime = transmittedLumenGameTime;
        }

        public Lumen getRecentlyTransmittedLumen() {
            return this.recentlyTransmittedLumen;
        }

        public long getTransmittedLumenGameTime() {
            return this.transmittedLumenGameTime;
        }

        public void setTransmittedLumen(Lumen lumen, long gameTime) {
            this.recentlyTransmittedLumen = lumen;
            this.transmittedLumenGameTime = gameTime;
        }
    }
}

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
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.tile.base.TileDataLumenContainer;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenCrystalCluster
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenCrystalCluster extends TileEntityTick<TileLumenCrystalCluster.Data> {

    public TileLumenCrystalCluster(BlockPos pos, BlockState blockState) {
        super(TileEntitiesAS.LUMEN_CRYSTAL_CLUSTER, pos, blockState);
    }

    @Override
    public void clientTick(Level level) {
        super.clientTick(level);

        Lumen lumen = this.getTileData().getLumen();
        int count = LumenAS.PRISMATIC.get() == lumen ? 2 : 1;
        for (int i = 0; i < count; i++) {
            if (this.rand.nextInt(3) == 0) {
                AABB bounds = this.getBlockState().getShape(level, this.getBlockPos()).bounds().inflate(0.2F);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(new Vector3(this)
                                .add(Vector3.randomInAABB(bounds, rand)))
                        .alpha(FXAlphaFunction.fadeIn(3).andThen(FXAlphaFunction.FADE_OUT))
                        .color(FXColorFunction.constant(rand.nextInt(3) == 0 ? ColorWrapper.WHITE : lumen.getColor(rand.nextLong())))
                        .setScale(0.1F + rand.nextFloat() * 0.1F)
                        .setGravity(Vector3.y(0.0001F));
            }
        }
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }

    public static class Data extends TileEntityTick.Data implements TileDataLumenContainer {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> crystalClusterFields(inst).apply(inst, Data::new));

        protected static <T extends Data> Products.P4<RecordCodecBuilder.Mu<T>, Long, Boolean, Map<BlockPos, Boolean>, Lumen> crystalClusterFields(RecordCodecBuilder.Instance<T> instance) {
            return tickFields(instance).and(
                    CodecUtil.defaulted(RegistriesAS.REGISTRY_LUMEN.byNameCodec(), "lumen", LumenAS.NONE, Data::getLumen)
            );
        }

        private Lumen lumen;

        protected Data(long ticksExisted, boolean hasStructure, Map<BlockPos, Boolean> skyObstructions, Lumen lumen) {
            super(ticksExisted, hasStructure, skyObstructions);
            this.lumen = lumen;
        }

        @Override
        public Lumen getLumen() {
            return this.lumen;
        }

        @Override
        public void setLumen(Lumen lumen) {
            if (lumen == null) lumen = LumenAS.NONE.get();
            this.lumen = lumen;
        }
    }
}

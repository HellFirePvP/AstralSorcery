/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.GemCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileGemCrystalCluster
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileGemCrystalCluster extends TileEntityTick<TileEntityTick.Data> {

    public TileGemCrystalCluster(BlockPos pos, BlockState blockState) {
        super(TileEntitiesAS.GEM_CRYSTAL_CLUSTER, pos, blockState);
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        GemCrystalClusterBlock.GrowthStageType stage = this.getBlockState().getValue(GemCrystalClusterBlock.STAGE);
        if (stage.getGrowthStage() < 2 && this.doesSeeSky()) {
            int chance = 20 * 60 * 10;
            chance = (int) (chance * (1F - 0.3F * DayTimeHelper.getCurrentDaytimeDistribution(level)));

            if (this.rand.nextInt(chance) == 0) {
                level.setBlock(this.getBlockPos(), this.getBlockState().setValue(GemCrystalClusterBlock.STAGE, stage.grow(level)), Block.UPDATE_ALL);
            }
        } else if (stage.getGrowthStage() == 2) {
            if (this.rand.nextInt(20 * 60 * 6) == 0) {
                level.setBlock(this.getBlockPos(), this.getBlockState().setValue(GemCrystalClusterBlock.STAGE, stage.shrink()), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(Level level) {
        super.clientTick(level);
        if (this.rand.nextInt(5) == 0) {
            GemCrystalClusterBlock.GrowthStageType stage = this.getBlockState().getValue(GemCrystalClusterBlock.STAGE);
            AABB bounds = this.getBlockState().getShape(level, this.getBlockPos()).bounds().inflate(0.1F);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(this)
                            .add(Vector3.randomInAABB(bounds, rand)))
                    .alpha(FXAlphaFunction.fadeIn(3).andThen(FXAlphaFunction.FADE_OUT))
                    .color(FXColorFunction.constant(stage.getDisplayColor()))
                    .setScale(0.1F + rand.nextFloat() * 0.1F)
                    .setGravity(Vector3.y(0.0001F));
        }
    }

    @Override
    public Codec<Data> dataCodec() {
        return Data.CODEC;
    }
}

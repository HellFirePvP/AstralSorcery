/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.tile.BlockGemCrystalCluster;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileGemCrystals
 * Created by HellFirePvP
 * Date: 16.11.2019 / 16:45
 */
public class TileGemCrystals extends TileEntityTick {

    public static final int TICK_GROWTH_CHANCE = 20_000;

    public TileGemCrystals() {
        super(TileEntityTypesAS.GEM_CRYSTAL_CLUSTER);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            if (getGrowth().getGrowthStage() < 2 && doesSeeSky()) {
                this.tryGrowWithChance(TICK_GROWTH_CHANCE);
            } else if (getGrowth().getGrowthStage() == 2) {
                if (rand.nextInt(2400) == 0) {
                    this.setGrowth(getGrowth().shrink());
                }
            }
        } else {
            if (getGrowth().getGrowthStage() == 2) {
                playHarvestEffects();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playHarvestEffects() {
        Vector3 pos = new Vector3(this)
                .add(0.5, 0.5, 0.5)
                .add(this.getBlockState().getOffset(getWorld(), getPos()));
        MiscUtils.applyRandomOffset(pos, rand, 0.5F);

        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(pos)
                .color(VFXColorFunction.constant(getGrowth().getDisplayColor()))
                .setScaleMultiplier(0.1F + rand.nextFloat() * 0.05F)
                .setMaxAge(15 + rand.nextInt(5));
    }

    public void tryGrowWithChance(int growthChance) {
        float distribution = DayTimeHelper.getCurrentDaytimeDistribution(getWorld());
        growthChance *= (1F - (0.2F * distribution));

        this.grow(growthChance);
    }

    public void grow(int chance) {
        if (rand.nextInt(Math.max(chance, 1)) == 0) {
            setGrowth(getGrowth().grow(getWorld()));
        }
    }

    public BlockGemCrystalCluster.GrowthStageType getGrowth() {
        BlockState current = getWorld().getBlockState(getPos());
        return current.get(BlockGemCrystalCluster.STAGE);
    }

    public void setGrowth(BlockGemCrystalCluster.GrowthStageType stage) {
        BlockState next = BlocksAS.GEM_CRYSTAL_CLUSTER.getDefaultState().with(BlockGemCrystalCluster.STAGE, stage);
        getWorld().setBlockState(getPos(), next);
    }
}

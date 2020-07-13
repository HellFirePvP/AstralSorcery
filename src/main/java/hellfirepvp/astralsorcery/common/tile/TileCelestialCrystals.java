/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.block.ore.BlockStarmetalOre;
import hellfirepvp.astralsorcery.common.block.tile.BlockCelestialCrystalCluster;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributeTile;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialCrystals
 * Created by HellFirePvP
 * Date: 30.09.2019 / 18:17
 */
public class TileCelestialCrystals extends TileEntityTick implements CrystalAttributeTile {

    public static final int TICK_GROWTH_CHANCE = 24_000;

    private CrystalAttributes attributes = null;

    public TileCelestialCrystals() {
        super(TileEntityTypesAS.CELESTIAL_CRYSTAL_CLUSTER);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {
            if (getGrowth() < 4 && doesSeeSky()) {
                this.tryGrowWithChance(TICK_GROWTH_CHANCE);
            }
        } else {
            BlockState downState = getWorld().getBlockState(getPos().down());
            if (downState.getBlock() instanceof BlockStarmetalOre) {
                playStarmetalParticles();
            }
            if (getGrowth() == 4) {
                playFullyGrownParticles();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playStarmetalParticles() {
        if (rand.nextInt(9) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(this)
                            .add(0.1, 0, 0.1)
                            .add(rand.nextFloat() * 0.8, 0, rand.nextFloat() * 0.8))
                    .color(VFXColorFunction.constant(ColorsAS.DEFAULT_GENERIC_PARTICLE))
                    .setMotion(new Vector3(0, 0.02 + rand.nextFloat() * 0.05F, 0))
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.15F);
        }

        if (rand.nextInt(4) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(this)
                            .addY(0.05)
                            .add(rand.nextFloat(), 0, rand.nextFloat()))
                    .color(VFXColorFunction.constant(ColorsAS.DEFAULT_GENERIC_PARTICLE))
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .setScaleMultiplier(0.06F + rand.nextFloat() * 0.05F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playFullyGrownParticles() {
        if (rand.nextInt(4) == 0) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(this)
                            .add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()))
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.WHITE)
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.15F);
        }
    }

    public void tryGrowWithChance(int growthChance) {
        BlockState downState = getWorld().getBlockState(getPos().down());
        if (downState.getBlock() instanceof BlockStarmetalOre) {
            growthChance *= 0.6;

            if (rand.nextInt(400) == 0) {
                getWorld().setBlockState(getPos().down(), Blocks.IRON_ORE.getDefaultState());
            }
        }
        float distribution = DayTimeHelper.getCurrentDaytimeDistribution(getWorld());
        growthChance *= (1F - (0.5F * distribution));

        this.grow(growthChance);
    }

    public void grow(int chance) {
        if (rand.nextInt(Math.max(chance, 1)) == 0) {
            int stage = getGrowth();
            if (stage < 4) {
                setGrowth(stage + 1);
            }
        }
    }

    public int getGrowth() {
        BlockState current = getWorld().getBlockState(getPos());
        return current.get(BlockCelestialCrystalCluster.STAGE);
    }

    public void setGrowth(int stage) {
        BlockState next = BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.getDefaultState().with(BlockCelestialCrystalCluster.STAGE, stage);
        getWorld().setBlockState(getPos(), next);
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        if (this.attributes != null) {
            this.attributes.store(compound);
        } else {
            CrystalAttributes.storeNull(compound);
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.attributes = CrystalAttributes.getCrystalAttributes(compound);
    }

    @Nullable
    @Override
    public CrystalAttributes getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(@Nullable CrystalAttributes attributes) {
        this.attributes = attributes;
    }
}

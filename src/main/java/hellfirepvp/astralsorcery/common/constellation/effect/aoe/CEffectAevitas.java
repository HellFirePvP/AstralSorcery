/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.auxiliary.CropHelper;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.event.PlayerAffectionFlags;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectAevitas
 * Created by HellFirePvP
 * Date: 27.07.2019 / 21:54
 */
public class CEffectAevitas extends CEffectAbstractList<CropHelper.GrowablePlant> {

    public static PlayerAffectionFlags.AffectionFlag FLAG = makeAffectionFlag("aevitas");
    public static AevitasConfig CONFIG = new AevitasConfig();

    public CEffectAevitas(@Nonnull ILocatable origin) {
        super(origin, ConstellationsAS.aevitas, CONFIG.maxAmount.get(), (world, pos, state) -> CropHelper.wrapPlant(world, pos) != null);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        if (rand.nextBoolean()) {
            ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(
                            pos.getX() + rand.nextFloat() * (prop.getSize() / 2F) * (rand.nextBoolean() ? 1 : -1) + 0.5,
                            pos.getY() + rand.nextFloat() * (prop.getSize() / 4F) + 0.5,
                            pos.getZ() + rand.nextFloat() * (prop.getSize() / 2F) * (rand.nextBoolean() ? 1 : -1) + 0.5))
                    .setGravityStrength(-0.005F)
                    .setScaleMultiplier(0.45F)
                    .color(VFXColorFunction.constant(ColorsAS.RITUAL_CONSTELLATION_AEVITAS))
                    .setMaxAge(35);
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        boolean changed = false;
        CropHelper.GrowablePlant plant = getRandomElementChanced();
        if (plant != null) {
            changed = MiscUtils.executeWithChunk(world, plant.getPos(), changed, (changedFlag) -> {
                if (properties.isCorrupted()) {
                    if (world instanceof ServerWorld) {
                        CropHelper.HarvestablePlant harvestablePlant = CropHelper.wrapHarvestablePlant(world, plant.getPos());
                        if (harvestablePlant != null) {
                            NonNullList<ItemStack> drops = harvestablePlant.harvestDropsAndReplant((ServerWorld) world, rand, 1);
                            drops.forEach(drop -> ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                            changedFlag = !drops.isEmpty();
                        } else if (BlockUtils.breakBlockWithoutPlayer(((ServerWorld) world), plant.getPos())) {
                            changedFlag = true;
                        }
                    } else {
                        if (world.removeBlock(plant.getPos(), false)) {
                            changedFlag = true;
                        }
                    }
                } else {
                    if (!plant.isValid(world)) {
                        removeElement(plant.getPos());
                        changedFlag = true;
                    } else {
                        if (plant.tryGrow(world, rand)) {
                            PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.CROP_GROWTH)
                                    .addData(buf -> ByteBufUtils.writeVector(buf, new Vector3(plant.getPos())));
                            PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, plant.getPos(), 16));
                            changedFlag = true;
                        }
                    }
                }
                return changedFlag;
            }, false);
        }

        if (this.findNewPosition(world, pos, properties)
                .ifRight(attemptedPos -> sendConstellationPing(world, new Vector3(attemptedPos).add(0.5, 0.5, 0.5)))
                .left().isPresent()) changed = true;
        if (this.findNewPosition(world, pos, properties)
                .ifRight(attemptedPos -> sendConstellationPing(world, new Vector3(attemptedPos).add(0.5, 0.5, 0.5)))
                .left().isPresent()) changed = true;

        int amplifier = CONFIG.potionAmplifier.get();
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, BOX.offset(pos).grow(properties.getSize()));
        for (LivingEntity entity : entities) {
            if (entity.isAlive()) {
                if (properties.isCorrupted()) {
                    EntityUtils.applyPotionEffectAtHalf(entity, new EffectInstance(EffectsAS.EFFECT_BLEED, 120, amplifier * 2));
                    EntityUtils.applyPotionEffectAtHalf(entity, new EffectInstance(Effects.WEAKNESS, 120, amplifier * 3));
                    EntityUtils.applyPotionEffectAtHalf(entity, new EffectInstance(Effects.HUNGER, 120, amplifier * 4));
                    EntityUtils.applyPotionEffectAtHalf(entity, new EffectInstance(Effects.MINING_FATIGUE, 120, amplifier * 2));
                } else {
                    EntityUtils.applyPotionEffectAtHalf(entity, new EffectInstance(Effects.REGENERATION, 120, amplifier));
                }
                if (entity instanceof PlayerEntity) {
                    markPlayerAffected((PlayerEntity) entity);
                }
            }
        }

        return changed;
    }

    @Nullable
    @Override
    public CropHelper.GrowablePlant recreateElement(CompoundNBT tag, BlockPos pos) {
        return CropHelper.fromNBT(tag, pos);
    }

    @Nullable
    @Override
    public CropHelper.GrowablePlant createElement(World world, BlockPos pos) {
        return CropHelper.wrapPlant(world, pos);
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    public PlayerAffectionFlags.AffectionFlag getPlayerAffectionFlag() {
        return FLAG;
    }

    @OnlyIn(Dist.CLIENT)
    public static void playParticles(PktPlayEffect event) {
        Vector3 at = ByteBufUtils.readVector(event.getExtraData());
        for (int i = 0; i < 8; i++) {
            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(at.clone().add(rand.nextFloat(), 0.2, rand.nextFloat()))
                    .setMotion(new Vector3(0, 0.005 + rand.nextFloat() * 0.01, 0))
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.1F)
                    .color(VFXColorFunction.constant(Color.GREEN));
        }
    }

    private static class AevitasConfig extends CountConfig {

        private final int defaultPotionAmplifier = 1;

        public ForgeConfigSpec.IntValue potionAmplifier;

        public AevitasConfig() {
            super("aevitas", 10D, 4D, 200);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.potionAmplifier = cfgBuilder
                    .comment("Set the amplifier for the potion effects this ritual provides.")
                    .translation(translationKey("potionAmplifier"))
                    .defineInRange("potionAmplifier", this.defaultPotionAmplifier, 0, 10);
        }
    }
}

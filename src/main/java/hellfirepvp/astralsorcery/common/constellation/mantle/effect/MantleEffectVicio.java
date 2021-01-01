/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectVicio;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.PlayerAffectionFlags;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperTemporaryFlight;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyMantleFlight;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectVicio
 * Created by HellFirePvP
 * Date: 19.02.2020 / 21:05
 */
public class MantleEffectVicio extends MantleEffect {

    public static VicioConfig CONFIG = new VicioConfig();

    public MantleEffectVicio() {
        super(ConstellationsAS.vicio);
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        super.tickServer(player);

        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (prog.getPerkData().hasPerkEffect(p -> p instanceof KeyMantleFlight) &&
                AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCost.get(), true)) {
            boolean prev = player.abilities.allowFlying;
            player.abilities.allowFlying = true;
            if (!prev) {
                player.sendPlayerAbilities();
            }

            EventHelperTemporaryFlight.allowFlight(player, 10);
            if (player.abilities.isFlying && !player.isOnGround() && player.ticksExisted % 10 == 0) {
                if (!PlayerAffectionFlags.isPlayerAffected(player, CEffectVicio.FLAG)) {
                    AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCost.get(), false);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        if (player.isElytraFlying() || (!(player.isCreative() || player.isSpectator()) && player.abilities.isFlying)) {
            if (!Minecraft.getInstance().gameSettings.getPointOfView().func_243193_b()) {
                this.playCapeSparkles(player, 0.1F);
            } else {
                this.playCapeSparkles(player, 0.7F);
            }
        } else {
            this.playCapeSparkles(player, 0.15F);
        }
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    protected FXFacingParticle spawnFacingParticle(PlayerEntity player, Vector3 at) {
        if (player.isElytraFlying() || (!(player.isCreative() || player.isSpectator()) && player.abilities.isFlying)) {
            at.subtract(player.getMotion().mul(1.5, 1.5, 1.5));
        }
        return super.spawnFacingParticle(player, at);
    }

    public static boolean isUsableElytra(ItemStack elytraStack, PlayerEntity wearingEntity) {
        if (elytraStack.getItem() instanceof ItemMantle) {
            MantleEffect effect = ItemMantle.getEffect(wearingEntity, ConstellationsAS.vicio);
            PlayerProgress progress;
            if (wearingEntity.getEntityWorld().isRemote()) {
                progress = ResearchHelper.getClientProgress();
            } else {
                progress = ResearchHelper.getProgress(wearingEntity, LogicalSide.SERVER);
            }
            return effect != null && !progress.getPerkData().hasPerkEffect(p -> p instanceof KeyMantleFlight);
        }
        return false;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    private static class VicioConfig extends Config {

        private static final int defaultChargeCost = 60;

        private ForgeConfigSpec.IntValue chargeCost;

        public VicioConfig() {
            super("vicio");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.chargeCost = cfgBuilder
                    .comment("Defines the amount of starlight charge consumed per !second! during creative-flight with the vicio mantle.")
                    .translation(translationKey("chargeCost"))
                    .defineInRange("chargeCost", defaultChargeCost, 1, 500);
        }
    }
}

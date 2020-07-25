/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
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

    public static Config CONFIG = new Config("vicio");

    public MantleEffectVicio() {
        super(ConstellationsAS.vicio);
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        super.tickServer(player);

        PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (prog.hasPerkEffect(p -> p instanceof KeyMantleFlight) && prog.doPerkAbilities()) {
            boolean prev = player.abilities.allowFlying;
            player.abilities.allowFlying = true;
            if (!prev) {
                player.sendPlayerAbilities();
            }

            EventHelperTemporaryFlight.allowFlight(player);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        if (player.isElytraFlying() || (!player.isCreative() && player.abilities.isFlying)) {
            if (Minecraft.getInstance().gameSettings.thirdPersonView == 1) {
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
        if (player.isElytraFlying() || (!player.isCreative() && player.abilities.isFlying)) {
            at.subtract(player.getMotion().mul(1.5, 1.5, 1.5));
        }
        return super.spawnFacingParticle(player, at);
    }

    public static boolean isUsableElytra(ItemStack elytraStack, PlayerEntity wearingEntity) {
        if (elytraStack.getItem() instanceof ItemMantle) {
            MantleEffect effect = ItemMantle.getEffect(wearingEntity, ConstellationsAS.vicio);
            PlayerProgress progress = ResearchHelper.getProgress(wearingEntity, LogicalSide.SERVER);
            return effect != null && (!progress.hasPerkEffect(p -> p instanceof KeyMantleFlight) || !progress.doPerkAbilities());
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
}

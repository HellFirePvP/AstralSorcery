/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperTemporaryFlight;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyMantleFlight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

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
    protected void tickServer(PlayerEntity player, boolean hasMantle) {
        super.tickServer(player, hasMantle);

        if (hasMantle) {
            PlayerProgress prog = ResearchHelper.getProgress(player, LogicalSide.SERVER);
            if (prog.hasPerkEffect(p -> p instanceof KeyMantleFlight)) {
                boolean prev = player.abilities.allowFlying;
                player.abilities.allowFlying = true;
                if (!prev) {
                    player.sendPlayerAbilities();
                }

                EventHelperTemporaryFlight.allowFlight(player);
            }
        }
    }

    public static boolean isUsableElytra(ItemStack elytraStack, PlayerEntity wearingEntity) {
        if (elytraStack.getItem() instanceof ItemMantle) {
            MantleEffect effect = ItemMantle.getEffect(wearingEntity, ConstellationsAS.vicio);
            return effect != null && !ResearchHelper.getProgress(wearingEntity, LogicalSide.SERVER).hasPerkEffect(p -> p instanceof KeyMantleFlight);
        }
        return false;
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }
}

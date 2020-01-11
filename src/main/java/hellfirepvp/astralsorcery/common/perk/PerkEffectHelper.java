/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncPerkActivity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkEffectHelper
 * Created by HellFirePvP
 * Date: 25.08.2019 / 22:00
 */
public class PerkEffectHelper {

    private PerkEffectHelper() {}

    public static void onPlayerConnectEvent(ServerPlayerEntity player) {
        modifyAllPerks(player, LogicalSide.SERVER, Action.ADD);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(PktSyncPerkActivity.Type.UNLOCKALL));
    }

    public static void onPlayerDisconnectEvent(ServerPlayerEntity player) {
        modifyAllPerks(player, LogicalSide.SERVER, Action.REMOVE);
    }

    public static void onPlayerCloneEvent(ServerPlayerEntity original, ServerPlayerEntity newPlayer) {
        modifyAllPerks(original, LogicalSide.SERVER, Action.REMOVE);
        modifyAllPerks(newPlayer, LogicalSide.SERVER, Action.ADD);
        PerkCooldownHelper.removeAllCooldowns(original, LogicalSide.SERVER);

        PacketChannel.CHANNEL.sendToPlayer(newPlayer, new PktSyncPerkActivity(PktSyncPerkActivity.Type.UNLOCKALL));
    }

    //Know that if you apply global converters, you're also responsible for removing and syncing them at the appropriate time!
    public static void applyGlobalConverters(PlayerEntity player, LogicalSide side, PerkConverter... converters) {
        applyGlobalConverters(player, side, Arrays.asList(converters));
    }

    public static void applyGlobalConverters(PlayerEntity player, LogicalSide side, List<PerkConverter> converters) {
        batchApplyConverters(player, side, converters, null);
    }

    public static void removeGlobalConverters(PlayerEntity player, LogicalSide side, PerkConverter... converters) {
        removeGlobalConverters(player, side, Arrays.asList(converters));
    }

    public static void removeGlobalConverters(PlayerEntity player, LogicalSide side, List<PerkConverter> converters) {
        batchRemoveConverters(player, side, converters, null);
    }

    /* ****************************************************************************************************
                                       INTERNAL PERK SYNCHRONIZATION
     **************************************************************************************************** */

    @OnlyIn(Dist.CLIENT)
    public static void clientChangePerkData(AbstractPerk perk, CompoundNBT oldData, CompoundNBT newData) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.CLIENT);
        progress.applyPerk(perk, oldData);
        modifySinglePerk(player, LogicalSide.CLIENT, perk, Action.REMOVE);
        progress.applyPerk(perk, newData);
        modifySinglePerk(player, LogicalSide.CLIENT, perk, Action.ADD);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientClearAllPerks() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PerkAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.CLIENT);
        List<AbstractPerk> copyPerks = new ArrayList<>(attr.getCacheAppliedPerks());
        for (AbstractPerk perk : copyPerks) {
            handlePerkRemoval(perk, player, LogicalSide.CLIENT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientRefreshAllPerks() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        modifyAllPerks(player, LogicalSide.CLIENT, Action.ADD);
        PerkCooldownHelper.removeAllCooldowns(player, LogicalSide.CLIENT);
    }

    /* ****************************************************************************************************
                                      INTERNAL PERK APPLICATION LOGIC
     **************************************************************************************************** */

    public static void modifySinglePerk(PlayerEntity player, LogicalSide side, AbstractPerk perk, Action action) {
        PlayerProgress progress = ResearchHelper.getProgress(player, side);
        if (progress.isValid()) {
            if (action.isRemove()) {
                handlePerkRemoval(perk, player, side);
            } else {
                handlePerkApplication(perk, player, side);
            }
        }
    }

    private static void modifyAllPerks(PlayerEntity player, LogicalSide side, Action action) {
        PlayerProgress progress = ResearchHelper.getProgress(player, side);
        if (progress.isValid()) {
            for (AbstractPerk perk : progress.getAppliedPerks()) {
                if (action.isRemove()) {
                    handlePerkRemoval(perk, player, side);
                } else {
                    handlePerkApplication(perk, player, side);
                }
            }
        }
    }

    private static void handlePerkApplication(AbstractPerk perk, PlayerEntity player, LogicalSide side) {
        Collection<PerkConverter> converters = Lists.newArrayList();
        if (perk instanceof ConverterPerk) {
            converters = ((ConverterPerk) perk).provideConverters(player, side);
        }
        batchApplyConverters(player, side, converters, perk);
    }

    private static void handlePerkRemoval(AbstractPerk perk, PlayerEntity player, LogicalSide side) {
        Collection<PerkConverter> converters = Lists.newArrayList();
        if (perk instanceof ConverterPerk) {
            converters = ((ConverterPerk) perk).provideConverters(player, side);
        }
        batchRemoveConverters(player, side, converters, perk);
    }

    private static void batchApplyConverters(PlayerEntity player, LogicalSide side, Collection<PerkConverter> converters, @Nullable AbstractPerk onlyAdd) {
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.isValid()) {
            PerkAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
            List<AbstractPerk> perks = new LinkedList<>(prog.getAppliedPerks());
            perks = perks.stream().filter(attributeMap::isPerkApplied).collect(Collectors.toList());

            perks.forEach(perk -> perk.removePerk(player, side));

            if (onlyAdd == null || !prog.isPerkSealed(onlyAdd)) {
                converters.forEach((c) -> attributeMap.applyConverter(player, c));
            }

            if (onlyAdd != null && !prog.isPerkSealed(onlyAdd) && !perks.contains(onlyAdd)) {
               perks.add(onlyAdd);
            }

            perks.forEach(perk -> perk.applyPerk(player, side));
        }
    }

    private static void batchRemoveConverters(PlayerEntity player, LogicalSide side, Collection<PerkConverter> converters, @Nullable AbstractPerk onlyRemove) {
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.isValid()) {
            PerkAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
            List<AbstractPerk> perks = new ArrayList<>(attributeMap.getCacheAppliedPerks());

            perks.forEach(perk -> perk.removePerk(player, side));

            converters.forEach((c) -> attributeMap.removeConverter(player, c));

            if (onlyRemove != null) {
                perks.remove(onlyRemove);
            }

            perks.forEach(perk -> perk.applyPerk(player, side));
        }
    }

    public static enum Action {

        ADD,
        REMOVE;

        private boolean isRemove() {
            return this == REMOVE;
        }

    }
}

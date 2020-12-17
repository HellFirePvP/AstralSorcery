/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.data.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncPerkActivity;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeConverterProvider;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.Collection;

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
        PlayerPerkData perkData = progress.getPerkData();

        if (!perkData.hasPerkAllocation(perk)) {
            return;
        }
        perkData.updatePerkData(perk, oldData);
        modifySource(player, LogicalSide.CLIENT, perk, Action.REMOVE);
        perkData.updatePerkData(perk, newData);
        modifySource(player, LogicalSide.CLIENT, perk, Action.ADD);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientClearAllPerks() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.CLIENT);
        if (!progress.isValid()) {
            return;
        }

        PerkAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, LogicalSide.CLIENT);
        for (ModifierSource source : ModifierManager.getAppliedModifiers(player, LogicalSide.CLIENT)) {
            if (source instanceof AbstractPerk) {
                removeSource(attr, player, LogicalSide.CLIENT, source);
            }
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

    private static void modifyAllPerks(PlayerEntity player, LogicalSide side, Action action) {
        ResearchHelper.getProgress(player, side).getPerkData().getEffectGrantingPerks()
                .forEach(perk -> modifySource(player, side, perk, action));
    }

    public static <T extends ModifierSource> void modifySources(PlayerEntity player, LogicalSide side, Collection<T> sources, Action action) {
        PlayerProgress progress = ResearchHelper.getProgress(player, side);
        if (!progress.isValid()) {
            return;
        }

        PerkAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
        for (T src : sources) {
            if (action.isRemove()) {
                if (ModifierManager.isModifierApplied(player, side, src)) {
                    attributeMap.write(() -> removeSource(attributeMap, player, side, src));
                }
            } else {
                if (!ModifierManager.isModifierApplied(player, side, src) && src.canApplySource(player, side)) {
                    attributeMap.write(() -> applySource(attributeMap, player, side, src));
                }
            }
        }
    }

    public static void modifySource(PlayerEntity player, LogicalSide side, ModifierSource source, Action action) {
        PlayerProgress progress = ResearchHelper.getProgress(player, side);
        if (!progress.isValid()) {
            return;
        }

        PerkAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
        if (action.isRemove()) {
            if (ModifierManager.isModifierApplied(player, side, source)) {
                attributeMap.write(() -> removeSource(attributeMap, player, side, source));
            }
        } else {
            if (!ModifierManager.isModifierApplied(player, side, source) && source.canApplySource(player, side)) {
                attributeMap.write(() -> applySource(attributeMap, player, side, source));
            }
        }
    }

    private static void applySource(PerkAttributeMap attrMap, PlayerEntity player, LogicalSide side, ModifierSource add) {
        //The onlyAdd perk is already on the playerprogress (potentially with other, not-yet-added perks); filter it away.
        Collection<ModifierSource> sources = ModifierManager.getAppliedModifiers(player, side);
        //List<ModifierSource> sources = new LinkedList<>(prog.getAppliedPerks());
        //sources = sources.stream().filter(attrMap::isModifierApplied).collect(Collectors.toList());

        sources.forEach(source -> {
            removeModifiers(source, attrMap, player, side);
            ModifierManager.removeModifier(player, side, source);
        });

        if (add instanceof AttributeConverterProvider) {
            ((AttributeConverterProvider) add).getConverters(player, side, false)
                    .forEach((c) -> attrMap.applyConverter(player, c));
        }
        Collection<PerkAttributeModifier> newModifiers = applyModifiers(add, attrMap, player, side);

        sources.forEach(source -> {
            applyModifiers(source, attrMap, player, side);
            ModifierManager.addModifier(player, side, source);
        });
        //Add new source.
        ModifierManager.addModifier(player, side, add);
        newModifiers.forEach(mod -> mod.getAttributeType().onApply(player, side, add));
    }

    private static Collection<PerkAttributeModifier> applyModifiers(ModifierSource source, PerkAttributeMap attrMap, PlayerEntity player, LogicalSide side) {
        Collection<PerkAttributeModifier> addedModifiers = new ArrayList<>();
        if (source instanceof AttributeModifierProvider) {
            for (PerkAttributeModifier modifier : ((AttributeModifierProvider) source).getModifiers(player, side, false)) {
                addedModifiers.addAll(attrMap.applyModifier(player, modifier, source));
            }
        }
        return addedModifiers;
    }

    private static void removeSource(PerkAttributeMap attrMap, PlayerEntity player, LogicalSide side, ModifierSource remove) {
        //Drop the old source
        ModifierManager.removeModifier(player, side, remove);

        Collection<ModifierSource> sources = ModifierManager.getAppliedModifiers(player, side);
        sources.forEach(source -> {
            removeModifiers(source, attrMap, player, side);
            ModifierManager.removeModifier(player, side, source);
        });

        Collection<PerkAttributeModifier> removedModifiers = removeModifiers(remove, attrMap, player, side);
        if (remove instanceof AttributeConverterProvider) {
            ((AttributeConverterProvider) remove).getConverters(player, side, false)
                    .forEach((c) -> attrMap.removeConverter(player, c));
        }

        sources.forEach(source -> {
            applyModifiers(source, attrMap, player, side);
            ModifierManager.addModifier(player, side, source);
        });

        PerkAttributeMap map = PerkAttributeHelper.getOrCreateMap(player, side);
        removedModifiers.forEach(mod -> {
            mod.getAttributeType().onRemove(player, side, !map.hasModifiers(mod.getAttributeType()), remove);
        });
    }

    private static Collection<PerkAttributeModifier> removeModifiers(ModifierSource source, PerkAttributeMap attrMap, PlayerEntity player, LogicalSide side) {
        Collection<PerkAttributeModifier> removedModifiers = new ArrayList<>();
        if (source instanceof AttributeModifierProvider) {
            for (PerkAttributeModifier modifier : ((AttributeModifierProvider) source).getModifiers(player, side, false)) {
                removedModifiers.addAll(attrMap.removeModifier(player, modifier, source));
            }
        }
        return removedModifiers;
    }

    public static enum Action {

        ADD,
        REMOVE;

        private boolean isRemove() {
            return this == REMOVE;
        }

    }
}

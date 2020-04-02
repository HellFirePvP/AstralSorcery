/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.event.ASRegistryEvents;
import hellfirepvp.astralsorcery.common.perk.source.provider.PerkSourceProvider;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentSourceProvider;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModifierManager
 * Created by HellFirePvP
 * Date: 01.04.2020 / 17:27
 */
public class ModifierManager implements ITickHandler {

    public static final ResourceLocation PERK_PROVIDER_KEY = AstralSorcery.key("perks");
    public static final ResourceLocation EQUIPMENT_PROVIDER_KEY = AstralSorcery.key("equipment");

    public static final ModifierManager INSTANCE = new ModifierManager();

    private static Map<ResourceLocation, ModifierSourceProvider<?>> sourceProviders = null;

    private static Map<UUID, Set<ModifierSource>> modifierCache = new HashMap<>();
    private static Map<UUID, Set<ModifierSource>> modifierCacheClient = new HashMap<>();

    private ModifierManager() {}

    public static void init() {
        if (sourceProviders == null) {
            sourceProviders = new HashMap<>();

            //Special dummy handler for perks. They're handled differently.. (and i don't wanna refactor AGAIN)
            sourceProviders.put(PERK_PROVIDER_KEY, new PerkSourceProvider());

            sourceProviders.put(EQUIPMENT_PROVIDER_KEY, new EquipmentSourceProvider());

            MinecraftForge.EVENT_BUS.post(new ASRegistryEvents.ModifierSourceRegister(sourceProvider -> {
                sourceProviders.put(sourceProvider.getKey(), sourceProvider);
            }));
        }
    }

    @Nullable
    public static ModifierSourceProvider<?> getProvider(ResourceLocation key) {
        return sourceProviders.get(key);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        PlayerEntity player = (PlayerEntity) context[0];
        LogicalSide side = (LogicalSide) context[1];

        if (!side.isServer() || !(player instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        for (ModifierSourceProvider<?> sourceProvider : sourceProviders.values()) {
            sourceProvider.update(serverPlayer);
        }
    }

    @Nonnull
    private static Set<ModifierSource> getModifiers(PlayerEntity player, LogicalSide side) {
        if (side.isClient()) {
            return modifierCacheClient.computeIfAbsent(player.getUniqueID(), uuid -> new HashSet<>());
        } else {
            return modifierCache.computeIfAbsent(player.getUniqueID(), uuid -> new HashSet<>());
        }
    }

    @Nonnull
    public static Set<ModifierSource> getAppliedModifiers(PlayerEntity player, LogicalSide side) {
        return new HashSet<>(getModifiers(player, side));
    }

    public static void addModifier(PlayerEntity player, LogicalSide side, ModifierSource source) {
        Set<ModifierSource> modifiers = getModifiers(player, side);
        if (!modifiers.contains(source) && modifiers.add(source)) {
            source.onApply(player, side);
        }
    }

    public static void removeModifier(PlayerEntity player, LogicalSide side, ModifierSource source) {
        Set<ModifierSource> modifiers = getModifiers(player, side);
        if (modifiers.remove(source)) {
            source.onRemove(player, side);
        }
    }

    public static boolean isModifierApplied(PlayerEntity player, LogicalSide side, ModifierSource source) {
        return getModifiers(player, side).contains(source);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Modifier Source Manager";
    }
}

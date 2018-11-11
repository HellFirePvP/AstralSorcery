/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IConverterProvider;
import hellfirepvp.astralsorcery.common.constellation.perk.types.ICooldownPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncPerkActivity;
import hellfirepvp.astralsorcery.common.util.data.TimeoutListContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkEffectHelper
 * Created by HellFirePvP
 * Date: 30.06.2018 / 15:25
 */
public class PerkEffectHelper implements ITickHandler {

    public static TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> perkCooldowns = new TimeoutListContainer<>(new PerkTimeoutHandler(), TickEvent.Type.SERVER);
    public static TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> perkCooldownsClient = new TimeoutListContainer<>(new PerkTimeoutHandler(), TickEvent.Type.CLIENT);

    public static final PerkEffectHelper EVENT_INSTANCE = new PerkEffectHelper();

    private PerkEffectHelper() {}

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        AstralSorcery.proxy.scheduleDelayed(() -> handlePerkModification(((NetHandlerPlayServer) event.getHandler()).player, Side.SERVER, true));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @SideOnly(Side.CLIENT)
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        handlePerkModification(Minecraft.getMinecraft().player, Side.CLIENT, true);
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        AstralSorcery.proxy.scheduleDelayed(() -> handlePerkModification(((NetHandlerPlayServer) event.getHandler()).player, Side.SERVER, false));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        AstralSorcery.proxy.scheduleClientside(new Runnable() {
            @Override
            public void run() {
                if (Minecraft.getMinecraft().player != null && ResearchManager.clientInitialized) {
                    handlePerkModification(Minecraft.getMinecraft().player, Side.CLIENT, false);
                } else {
                    AstralSorcery.proxy.scheduleClientside(this);
                }
            }
        });
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();

        handlePerkModification(oldPlayer, oldPlayer.world.isRemote ? Side.CLIENT : Side.SERVER, true);
        handlePerkModification(newPlayer, newPlayer.world.isRemote ? Side.CLIENT : Side.SERVER, false);

        PlayerWrapperContainer container = new PlayerWrapperContainer(oldPlayer);
        if (perkCooldowns.hasList(container)) {
            perkCooldowns.removeList(container);
        }
        if (perkCooldownsClient.hasList(container)) {
            perkCooldownsClient.removeList(container);
        }

        if (newPlayer instanceof EntityPlayerMP) {
            PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(PktSyncPerkActivity.Type.UNLOCKALL), (EntityPlayerMP) newPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void expRemoval(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
            if (side != Side.SERVER) return;

            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog != null) {
                long exp = MathHelper.lfloor(prog.getPerkExp());
                int level = prog.getPerkLevel();
                long expThisLevel = PerkLevelManager.INSTANCE.getExpForLevel(level - 1);
                long expNextLevel = PerkLevelManager.INSTANCE.getExpForLevel(level);

                float removePerDeath = 0.25F;
                int remove = MathHelper.floor(((float) (expNextLevel - expThisLevel)) * removePerDeath);
                if (exp - remove < expThisLevel) {
                    exp = expThisLevel;
                } else {
                    exp -= remove;
                }

                ResearchManager.setExp(player, exp);
            }
        }
    }

    private void handlePerkModification(EntityPlayer player, Side side, boolean remove) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (progress != null) {
            for (AbstractPerk perk : progress.getAppliedPerks()) {
                if (remove) {
                    handlePerkRemoval(perk, player, side);
                } else {
                    handlePerkApplication(perk, player, side);
                }
            }
        }
    }

    public void notifyPerkChange(EntityPlayer player, Side side, AbstractPerk perk, boolean remove) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (progress != null) {
            if (remove) {
                handlePerkRemoval(perk, player, side);
            } else {
                handlePerkApplication(perk, player, side);
            }
        }
    }

    private void handlePerkApplication(AbstractPerk perk, EntityPlayer player, Side side) {
        Collection<PerkConverter> converters = Lists.newArrayList();
        if (perk instanceof IConverterProvider) {
            converters = ((IConverterProvider) perk).provideConverters(player, side);
        }
        batchApplyConverters(player, side, converters, perk);
    }

    private void handlePerkRemoval(AbstractPerk perk, EntityPlayer player, Side side) {
        Collection<PerkConverter> converters = Lists.newArrayList();
        if (perk instanceof IConverterProvider) {
            converters = ((IConverterProvider) perk).provideConverters(player, side);
        }
        batchRemoveConverters(player, side, converters, perk);
    }

    @SideOnly(Side.CLIENT)
    public void clearAllPerksClient(EntityPlayer player) {
        PlayerProgress prog = ResearchManager.getProgress(player, Side.CLIENT);
        if (prog != null) {
            PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, Side.CLIENT);
            List<AbstractPerk> copyPerks = new ArrayList<>(attr.getCacheAppliedPerks());
            for (AbstractPerk perk : copyPerks) {
                handlePerkRemoval(perk, player, Side.CLIENT);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void reapplyAllPerksClient(EntityPlayer player) {
        handlePerkModification(player, Side.CLIENT, false);

        PlayerWrapperContainer container = new PlayerWrapperContainer(player);
        if (perkCooldowns.hasList(container)) {
            perkCooldowns.removeList(container);
        }
        if (perkCooldownsClient.hasList(container)) {
            perkCooldownsClient.removeList(container);
        }
    }

    //Know that if you apply global converters, you're also responsible for removing them at the appropriate time...
    public void applyGlobalConverters(EntityPlayer player, Side side, PerkConverter... converters) {
        applyGlobalConverters(player, side, Arrays.asList(converters));
    }

    public void applyGlobalConverters(EntityPlayer player, Side side, List<PerkConverter> converters) {
        batchApplyConverters(player, side, converters, null);
    }

    public void removeGlobalConverters(EntityPlayer player, Side side, PerkConverter... converters) {
        removeGlobalConverters(player, side, Arrays.asList(converters));
    }

    public void removeGlobalConverters(EntityPlayer player, Side side, List<PerkConverter> converters) {
        batchRemoveConverters(player, side, converters, null);
    }

    private synchronized void batchApplyConverters(EntityPlayer player, Side side, Collection<PerkConverter> converters, @Nullable AbstractPerk onlyAdd) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null) {
            PlayerAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
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

    private synchronized void batchRemoveConverters(EntityPlayer player, Side side, Collection<PerkConverter> converters, @Nullable AbstractPerk onlyRemove) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null) {
            PlayerAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
            List<AbstractPerk> perks = new LinkedList<>(attributeMap.getCacheAppliedPerks());
            perks.forEach(perk -> perk.removePerk(player, side));

            converters.forEach((c) -> attributeMap.removeConverter(player, c));

            if (onlyRemove != null) {
                perks.remove(onlyRemove);
            }
            perks.forEach(perk -> perk.applyPerk(player, side));
        }
    }

    public final boolean isCooldownActiveForPlayer(EntityPlayer player, AbstractPerk perk) {
        if (!(perk instanceof ICooldownPerk)) return false;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        return container.hasList(ct) &&
                container.getOrCreateList(ct).contains(perk.getRegistryName());
    }

    public final void setCooldownActiveForPlayer(EntityPlayer player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof ICooldownPerk)) return;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        container.getOrCreateList(ct).setOrAddTimeout(cooldownTicks, perk.getRegistryName());
    }

    public final void forceSetCooldownForPlayer(EntityPlayer player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof ICooldownPerk)) return;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        if(!container.getOrCreateList(ct).setTimeout(cooldownTicks, perk.getRegistryName())) {
            setCooldownActiveForPlayer(player, perk, cooldownTicks);
        }
    }

    public final int getActiveCooldownForPlayer(EntityPlayer player, AbstractPerk perk) {
        if (!(perk instanceof ICooldownPerk)) return -1;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        if(!container.hasList(ct)) {
            return -1;
        }
        return container.getOrCreateList(ct).getTimeout(perk.getRegistryName());
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EntityPlayer ticked = (EntityPlayer) context[0];
        Side side = (Side) context[1];
        PlayerProgress prog = ResearchManager.getProgress(ticked, side);
        if(prog != null) {
            for (AbstractPerk perk : prog.getAppliedPerks()) {
                if (perk instanceof IPlayerTickPerk && prog.hasPerkEffect(perk)) {
                    ((IPlayerTickPerk) perk).onPlayerTick(ticked, side);
                }
            }
        }
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
        return "PlayerPerkHandler";
    }

    public static class PerkTimeoutHandler implements TimeoutListContainer.ContainerTimeoutDelegate<PlayerWrapperContainer, ResourceLocation> {

        @Override
        public void onContainerTimeout(PlayerWrapperContainer plWrapper, ResourceLocation key) {
            AbstractPerk perk = PerkTree.PERK_TREE.getPerk(key);
            if(perk != null && perk instanceof ICooldownPerk) {
                ((ICooldownPerk) perk).handleCooldownTimeout(plWrapper.player);
            }
        }
    }

    public static class PlayerWrapperContainer {

        @Nonnull
        public final EntityPlayer player;

        public PlayerWrapperContainer(@Nonnull EntityPlayer player) {
            this.player = player;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(obj == null) return false;
            if(!(obj instanceof PlayerWrapperContainer)) return false;
            return ((PlayerWrapperContainer) obj).player.getUniqueID().equals(player.getUniqueID());
        }

        @Override
        public int hashCode() {
            return player.getUniqueID().hashCode();
        }

    }

}

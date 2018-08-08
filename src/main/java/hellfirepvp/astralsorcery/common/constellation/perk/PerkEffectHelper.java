/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IConverterProvider;
import hellfirepvp.astralsorcery.common.constellation.perk.types.ICooldownPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.data.TimeoutListContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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
import java.util.*;

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
        MinecraftServer ms = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (ms != null) {
            ms.addScheduledTask(() -> handlePerkModification(((NetHandlerPlayServer) event.getHandler()).player, Side.SERVER, true));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Minecraft.getMinecraft().addScheduledTask(() -> handlePerkModification(Minecraft.getMinecraft().player, Side.CLIENT, true));
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        AstralSorcery.proxy.scheduleDelayed(() -> handlePerkModification(((NetHandlerPlayServer) event.getHandler()).player, Side.SERVER, false));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Minecraft.getMinecraft().addScheduledTask(() -> handlePerkModification(Minecraft.getMinecraft().player, Side.CLIENT, false));
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
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void expRemoval(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            Side side = event.getEntityLiving().world.isRemote ? Side.CLIENT : Side.SERVER;
            if (side != Side.SERVER) return;

            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog != null) {
                int exp = MathHelper.floor(prog.getPerkExp());
                int level = PerkLevelManager.INSTANCE.getLevel(exp);
                int expThisLevel = PerkLevelManager.INSTANCE.getExpForLevel(level - 1);
                int expNextLevel = PerkLevelManager.INSTANCE.getExpForLevel(level);

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

    public void clearAllPerks(EntityPlayer player, Side side) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null) {
            for (AbstractPerk perk : prog.getAppliedPerks()) {
                handlePerkRemoval(perk, player, side);
            }
        }
    }

    private void handlePerkApplication(AbstractPerk perk, EntityPlayer player, Side side) {
        if (perk instanceof IConverterProvider) {
            Collection<PerkConverter> converters = ((IConverterProvider) perk).provideConverters();
            if (!converters.isEmpty()) {
                batchApplyConverters(player, side, converters);
            }
        }
        perk.applyPerk(player, side);
    }

    private void handlePerkRemoval(AbstractPerk perk, EntityPlayer player, Side side) {
        if (perk instanceof IConverterProvider) {
            Collection<PerkConverter> converters = ((IConverterProvider) perk).provideConverters();
            if (!converters.isEmpty()) {
                batchRemoveConverters(player, side, converters);
            }
        }
        perk.removePerk(player, side);
    }

    public void applyGlobalConverters(EntityPlayer player, Side side, PerkConverter... converters) {
        applyGlobalConverters(player, side, Arrays.asList(converters));
    }

    public void applyGlobalConverters(EntityPlayer player, Side side, List<PerkConverter> converters) {
        batchApplyConverters(player, side, converters);
    }

    public void removeGlobalConverters(EntityPlayer player, Side side, PerkConverter... converters) {
        removeGlobalConverters(player, side, Arrays.asList(converters));
    }

    public void removeGlobalConverters(EntityPlayer player, Side side, List<PerkConverter> converters) {
        batchRemoveConverters(player, side, converters);
    }

    private void batchApplyConverters(EntityPlayer player, Side side, Collection<PerkConverter> converters) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null) {
            List<AbstractPerk> perks = new LinkedList<>(prog.getAppliedPerks());
            perks.forEach(perk -> perk.removePerk(player, side));

            PlayerAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
            converters.forEach((c) -> attributeMap.applyConverter(player, c));

            perks.forEach(perk -> perk.applyPerk(player, side));
        }
    }

    private void batchRemoveConverters(EntityPlayer player, Side side, Collection<PerkConverter> converters) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog != null) {
            List<AbstractPerk> perks = new LinkedList<>(prog.getAppliedPerks());
            perks.forEach(perk -> perk.removePerk(player, side));

            PlayerAttributeMap attributeMap = PerkAttributeHelper.getOrCreateMap(player, side);
            converters.forEach((c) -> attributeMap.removeConverter(player, c));

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
                if (perk instanceof IPlayerTickPerk) {
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

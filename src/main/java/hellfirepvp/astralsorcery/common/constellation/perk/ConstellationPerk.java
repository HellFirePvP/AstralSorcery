/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import hellfirepvp.astralsorcery.common.util.data.TimeoutListContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerk
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:03
 */
public abstract class ConstellationPerk extends ConfigEntry {

    protected static final Random rand = new Random();

    private final String unlocName;
    private final String unlocInfo;
    private final List<Target> perkEffectTargets;
    private int id = -1;

    protected ConstellationPerk(String name, Target... targets) {
        super(Section.PERKS, name);
        this.unlocName = "perk." + name;
        this.unlocInfo = unlocName + ".info";
        this.perkEffectTargets = Lists.newArrayList(targets);
    }

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public String getUnlocalizedDescription() {
        return unlocInfo;
    }

    public boolean mayExecute(Target target) {
        return perkEffectTargets.contains(target);
    }

    @SideOnly(Side.CLIENT)
    public void addLocalizedDescription(List<String> tooltip) {
        tooltip.add(I18n.format(getUnlocalizedName()));
        tooltip.add(I18n.format(getUnlocalizedDescription()));
    }

    public void onPlayerTick(EntityPlayer player, Side side) {}

    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        return dmgIn;
    }

    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        return dmgIn;
    }

    public void onEntityKnockback(EntityPlayer attacker, EntityLivingBase attacked) {}

    public void onEntityKilled(EntityPlayer attacker, EntityLivingBase killed) {}

    public float onHarvestSpeed(EntityPlayer harvester, IBlockState broken, @Nullable BlockPos at, float breakSpeedIn) {
        return breakSpeedIn;
    }

    public boolean onCanHarvest(EntityPlayer harvester, @Nonnull ItemStack playerMainHand, IBlockState tryHarvest, boolean prevSuccess) {
        return prevSuccess;
    }

    public void onTimeout(EntityPlayer player) {}

    public boolean hasConfigEntries() {
        return false;
    }

    public final boolean isCooldownActiveForPlayer(EntityPlayer player) {
        TimeoutListContainer<EventHandlerServer.PlayerWrapperContainer, Integer> container = player.getEntityWorld().isRemote ?
                EventHandlerServer.perkCooldownsClient : EventHandlerServer.perkCooldowns;
        EventHandlerServer.PlayerWrapperContainer ct = new EventHandlerServer.PlayerWrapperContainer(player);
        return container.hasList(ct) &&
                container.getOrCreateList(ct).contains(getId());
    }

    public final void setCooldownActiveForPlayer(EntityPlayer player, int cooldownTicks) {
        TimeoutListContainer<EventHandlerServer.PlayerWrapperContainer, Integer> container = player.getEntityWorld().isRemote ?
                EventHandlerServer.perkCooldownsClient : EventHandlerServer.perkCooldowns;
        EventHandlerServer.PlayerWrapperContainer ct = new EventHandlerServer.PlayerWrapperContainer(player);
        container.getOrCreateList(ct).setOrAddTimeout(cooldownTicks, getId());
    }

    public final void forceSetCooldownForPlayer(EntityPlayer player, int cooldownTicks) {
        TimeoutListContainer<EventHandlerServer.PlayerWrapperContainer, Integer> container = player.getEntityWorld().isRemote ?
                EventHandlerServer.perkCooldownsClient : EventHandlerServer.perkCooldowns;
        EventHandlerServer.PlayerWrapperContainer ct = new EventHandlerServer.PlayerWrapperContainer(player);
        if(!container.getOrCreateList(ct).setTimeout(cooldownTicks, getId())) {
            setCooldownActiveForPlayer(player, cooldownTicks);
        }
    }

    public final int getActiveCooldownForPlayer(EntityPlayer player) {
        TimeoutListContainer<EventHandlerServer.PlayerWrapperContainer, Integer> container = player.getEntityWorld().isRemote ?
                EventHandlerServer.perkCooldownsClient : EventHandlerServer.perkCooldowns;
        EventHandlerServer.PlayerWrapperContainer ct = new EventHandlerServer.PlayerWrapperContainer(player);
        if(!container.hasList(ct)) {
            return -1;
        }
        return container.getOrCreateList(ct).getTimeout(getId());
    }

    public final void addAlignmentCharge(EntityPlayer player, double charge) {
        if(!player.getEntityWorld().isRemote) {
            ResearchManager.modifyAlignmentCharge(player, charge);
        }
    }

    @Override
    public String getConfigurationSection() {
        return super.getConfigurationSection() + "." + getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstellationPerk that = (ConstellationPerk) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static enum Target {

        /**
         * Called when a Player attacks some LivingEntityBase.
         * Calls {@link #onEntityAttack(net.minecraft.entity.player.EntityPlayer, net.minecraft.entity.EntityLivingBase, float)}
         */
        ENTITY_ATTACK,

        /**
         * Called when a EntityLivingBase gets knockbacked by a player's attack.
         * Calls {@link #onEntityKnockback(net.minecraft.entity.player.EntityPlayer, net.minecraft.entity.EntityLivingBase)}
         */
        ENTITY_KNOCKBACK,

        /**
         * Called when a Player attack kills an EntityLivingBase.
         * Calls {@link #onEntityKilled(net.minecraft.entity.player.EntityPlayer, net.minecraft.entity.EntityLivingBase)}
         */
        ENTITY_KILL,

        /**
         * Called when a Player gets generally hurt.
         * Calls {@link #onEntityHurt(net.minecraft.entity.player.EntityPlayer, net.minecraft.util.DamageSource, float)}
         */
        ENTITY_HURT,

        /**
         * Gets called on each player's tick.
         * Calls {@link #onPlayerTick(net.minecraft.entity.player.EntityPlayer, net.minecraftforge.fml.relauncher.Side)}
         */
        PLAYER_TICK,

        /**
         * Gets called on a harvest-speed event
         * Calls {@link #onHarvestSpeed(EntityPlayer, IBlockState, BlockPos, float)}
         */
        PLAYER_HARVEST_SPEED,

        /**
         * Gets called on a harvest-check event
         * Calls {@link #onCanHarvest(EntityPlayer, ItemStack, IBlockState, boolean)}
         */
        PLAYER_HARVEST_TYPE

    }

}

package hellfirepvp.astralsorcery.common.constellation.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
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
        tooltip.add(I18n.format(getUnlocalizedDescription()));
    }

    public void onPlayerTick(EntityPlayer player) {}

    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        return dmgIn;
    }

    public void onEntityKnockback(EntityPlayer attacker, EntityLivingBase attacked) {}

    public void onEntityKilled(EntityPlayer attacker, EntityLivingBase killed) {}

    public boolean hasConfigEntries() {
        return false;
    }

    public final boolean isCooldownActiveForPlayer(EntityPlayer player) {
        return EventHandlerServer.perkCooldowns.hasList(player) &&
                EventHandlerServer.perkCooldowns.getOrCreateList(player).contains(getId());
    }

    public final void setCooldownActiveForPlayer(EntityPlayer player, int cooldownTicks) {
        EventHandlerServer.perkCooldowns.getOrCreateList(player).setOrAddTimeout(cooldownTicks, getId());
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

        ENTITY_ATTACK,
        ENTITY_KNOCKBACK,
        ENTITY_KILL,

        PLAYER_TICK;

    }

}

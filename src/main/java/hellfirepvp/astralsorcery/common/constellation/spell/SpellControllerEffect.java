/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleDataEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellControllerEffect
 * Created by HellFirePvP
 * Date: 07.07.2017 / 13:19
 */
public abstract class SpellControllerEffect implements Iterable<ISpellEffect> {

    protected final EntityLivingBase caster;
    protected Queue<ISpellEffect> spellEffects = Lists.newLinkedList();
    protected List<ISpellComponent> activeComponents = Lists.newLinkedList();
    private boolean started = false;
    private boolean finished = false;

    protected SpellControllerEffect(EntityLivingBase caster) {
        this.caster = caster;
    }

    public final void addEffect(ISpellEffect component) {
        if(started) return;
        this.spellEffects.add(component);
    }

    public final void addComponent(ISpellComponent component) {
        this.activeComponents.add(component);
    }

    final void onUpdate() {
        if(caster.isDead) {
            setFinished();
            return;
        }

        activeComponents.removeIf(component -> !component.isValid());
        activeComponents.stream().filter(ISpellComponent::requiresUpdatesFromController).forEach(ISpellComponent::onUpdateController);

        updateController();
    }

    @Override
    public final Iterator<ISpellEffect> iterator() {
        return spellEffects.iterator();
    }

    public final void castEffect() {
        if(started) return;
        this.started = true;
        SpellCastingManager.INSTANCE.addNewSpell(this);
        startCasting();
    }

    public void projectileImpact(SpellProjectile projectile, RayTraceResult result) {
        for (ISpellEffect effect : this)  {
            effect.impact(projectile, result);
        }
    }

    protected void particle(PktParticleDataEvent.ParticleType type, Vector3 pos, double... data) {
        PktParticleDataEvent pkt = new PktParticleDataEvent(type, pos.getX(), pos.getY(), pos.getZ(), data);
        PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(caster.getEntityWorld(), pos.toBlockPos(), 64));
    }

    public final void setFinished() {
        this.finished = true;
    }

    public final boolean isFinished() {
        return finished;
    }

    public abstract void startCasting();

    public abstract void updateController();

}

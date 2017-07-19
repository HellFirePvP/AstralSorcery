/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.spell.entity.SpellProjectile;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleDataEvent;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellControllerEffect
 * Created by HellFirePvP
 * Date: 07.07.2017 / 13:19
 */
public abstract class SpellControllerEffect implements Iterable<ISpellEffect> {

    protected static final Random rand = new Random();

    protected final EntityLivingBase caster;
    protected Queue<ISpellEffect> spellEffects = Lists.newLinkedList();
    protected Collection<ISpellEffect> removalQueue = Lists.newLinkedList();
    protected List<ISpellComponent> activeComponents = Lists.newLinkedList();
    private boolean started = false;
    private boolean finished = false;

    protected SpellControllerEffect(EntityLivingBase caster) {
        this.caster = caster;
    }

    public final void addEffect(ISpellEffect effect) {
        if(started) return;
        this.spellEffects.add(effect);
    }

    public final void queueRemoval(ISpellEffect effect) {
        removalQueue.add(effect);
    }

    public final void addComponent(ISpellComponent component) {
        this.activeComponents.add(component);
    }

    final void onUpdate() {
        if(caster.isDead) {
            setFinished();
            return;
        }
        spellEffects.removeIf(removalQueue::contains);
        removalQueue.clear();

        activeComponents.removeIf(component -> !component.isValid());
        activeComponents.stream().filter(ISpellComponent::requiresUpdatesFromController).forEach(ISpellComponent::onUpdateController);

        updateController();
    }

    public SpellProjectile newProjectile(float inaccuracy) {
        SpellProjectile projectile = new SpellProjectile(caster.world, this, inaccuracy);
        projectile.setTarget(SpellProjectile.getTarget(caster));
        caster.world.spawnEntity(projectile);
        addComponent(projectile);
        return projectile;
    }

    public Stream<SpellProjectile> splitProjectile(SpellProjectile origin, float splitAngle, int amount) {
        List<SpellProjectile> projectiles = new ArrayList<>(amount);
        Vector3 motionVec = new Vector3(origin.motionX, origin.motionY, origin.motionZ);
        Vector3 motionPerp = motionVec.clone().perpendicular();
        for (int i = 0; i < amount; i++) {
            Vector3 angleRot = motionVec.clone().rotate(Math.toRadians(splitAngle), motionPerp);
            Vector3 mot = angleRot.rotate(Math.toRadians(rand.nextFloat() * 360F), motionVec);
            SpellProjectile projectile = new SpellProjectile(caster.world, this, mot, (float) mot.length());
            projectile.setPosition(origin.posX, origin.posY, origin.posZ);
            projectile.setTarget(origin.getTarget());
            caster.world.spawnEntity(projectile);
            addComponent(projectile);
            projectiles.add(projectile);
        }
        origin.setDead();
        return projectiles.stream();
    }

    public EntityLivingBase getCaster() {
        return caster;
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

    public void projectileImpact(SpellProjectile projectile, RayTraceResult result, @Nullable EntityLivingBase nearbyOrDirect) {
        for (ISpellEffect effect : this)  {
            effect.impact(projectile, result, nearbyOrDirect);
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

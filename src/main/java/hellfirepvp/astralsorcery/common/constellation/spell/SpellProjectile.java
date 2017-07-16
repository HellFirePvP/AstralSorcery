/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.spell;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellProjectile
 * Created by HellFirePvP
 * Date: 07.07.2017 / 10:48
 */
public class SpellProjectile extends EntityThrowable implements IProjectile, ISpellComponent {

    private static final DataParameter<Integer> COLOR_TRAIL = EntityDataManager.createKey(SpellProjectile.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COLOR_SPARK = EntityDataManager.createKey(SpellProjectile.class, DataSerializers.VARINT);

    private int ticksNextSpellPulse = -1;

    private SpellControllerEffect spellController;

    public SpellProjectile(World worldIn) {
        super(worldIn);
    }

    public SpellProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public SpellProjectile(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public SpellProjectile(World worldIn, SpellControllerEffect controllerEffect) {
        super(worldIn, controllerEffect.caster);
        setSpellController(controllerEffect);
        float x = -MathHelper.sin(controllerEffect.caster.rotationYaw     * 0.017453292F)
                * MathHelper.cos(controllerEffect.caster.rotationPitch    * 0.017453292F);
        float y = -MathHelper.sin((controllerEffect.caster.rotationPitch) * 0.017453292F);
        float z =  MathHelper.cos(controllerEffect.caster.rotationYaw     * 0.017453292F)
                * MathHelper.cos(controllerEffect.caster.rotationPitch    * 0.017453292F);
        this.setThrowableHeading((double) x, (double) y, (double) z, 2.5F, 4F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(COLOR_TRAIL,   0xFFFFFFFF);
        this.dataManager.register(COLOR_SPARK, 0xFFFFFFFF);
    }

    public void setSpellController(SpellControllerEffect spellController) {
        this.spellController = spellController;
    }

    public void scheduleNextSpellPulse(int ticks) {
        this.ticksNextSpellPulse = ticks;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if(!world.isRemote) {
            if(this.spellController == null) {
                setDead();
                return;
            }
            ticksNextSpellPulse--;

        } else {

        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(!world.isRemote) {
            if(this.spellController == null) {
                setDead();
                return;
            }

            spellController.projectileImpact(this, result);
        }
    }

    @Override
    public void onUpdateController() {
        if(this.spellController == null) {
            setDead();
            return;
        }
        spellController.forEach(c -> c.affectProjectile(this));
    }

    public void setColors(int colorTrail, int colorSparks) {
        this.dataManager.set(COLOR_TRAIL, colorTrail);
        this.dataManager.set(COLOR_SPARK, colorSparks);
    }

    public Color getColorTrail() {
        return new Color(this.dataManager.get(COLOR_TRAIL));
    }

    public Color getColorSparks() {
        return new Color(this.dataManager.get(COLOR_SPARK));
    }

    @Override
    public boolean isValid() {
        return !isDead && !onGround;
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Nullable
    @Override
    public SpellControllerEffect getSpellController() {
        return this.spellController;
    }

    @Override
    public ISpellComponent copy() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        tag.removeTag("UUIDMost");
        tag.removeTag("UUIDLeast");
        Entity e = EntityList.newEntity(getClass(), world);
        if(e == null) {
            throw new IllegalStateException("Unknown or unregistered entity with class: " + getClass().getName());
        }
        if(!(e instanceof ISpellComponent)) {
            throw new IllegalStateException("Entity is not a ISpellComponent: " + getClass().getName());
        }
        e.readFromNBT(tag);
        return (ISpellComponent) e;
    }

}

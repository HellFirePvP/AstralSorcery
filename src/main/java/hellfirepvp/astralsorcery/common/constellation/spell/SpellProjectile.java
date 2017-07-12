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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpellProjectile
 * Created by HellFirePvP
 * Date: 07.07.2017 / 10:48
 */
public class SpellProjectile extends EntityThrowable implements IProjectile, ISpellComponent {

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

    public void setSpellController(SpellControllerEffect spellController) {
        this.spellController = spellController;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if(!world.isRemote && this.spellController == null) {
            setDead();
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

    @Override
    public boolean isValid() {
        return !isDead && !onGround;
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

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityAquamarine
 * Created by HellFirePvP
 * Date: 11.12.2016 / 16:25
 */
public class EntityAquamarine extends EntityItem implements EntityStarlightReacttant {

    private static final AxisAlignedBB boxCraft = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public static final int TOTAL_MERGE_TIME = 100 * 20;
    private int inertMergeTick = 0;

    public EntityAquamarine(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setNoDespawn();
    }

    public EntityAquamarine(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        setNoDespawn();
    }

    public EntityAquamarine(World worldIn) {
        super(worldIn);
        setNoDespawn();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Config.craftingLiqResoGem) {
            checkIncreaseConditions();
        }
    }

    private void checkIncreaseConditions() {
        if(world.isRemote) {
            if(canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if(canCraft()) {
                inertMergeTick++;
                if(inertMergeTick >= TOTAL_MERGE_TIME && rand.nextInt(300) == 0) {
                    craftCrystal();
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    private void craftCrystal() {
        world.setBlockToAir(getPosition());
        List<Entity> foundItems = world.getEntitiesInAABBexcluding(this, boxCraft.offset(posX, posY, posZ).expandXyz(0.1), EntityUtils.selectEntities(Entity.class));
        if(foundItems.size() <= 0) {
            if(!getEntityItem().isEmpty()) {
                if(getEntityItem().getCount() > 1) {
                    getEntityItem().setCount(getEntityItem().getCount() - 1);
                } else {
                    setDead();
                }
            } else {
                setDead();
            }
            EntityItem ei = ItemUtils.dropItemNaturally(world, posX, posY, posZ, ItemCraftingComponent.MetaType.RESO_GEM.asStack());
            ei.age = -32768;
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnCraftingParticles() {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        p.motion(rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.1  * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
        p.gravity(0.004);
        p.scale(0.3F).setColor(Color.CYAN);
    }

    private boolean canCraft() {
        if(!isInLiquidStarlight(this)) return false;

        List<Entity> foundEntities = world.getEntitiesInAABBexcluding(this, boxCraft.offset(getPosition()), EntityUtils.selectEntities(Entity.class));
        return foundEntities.size() <= 0;
    }

}

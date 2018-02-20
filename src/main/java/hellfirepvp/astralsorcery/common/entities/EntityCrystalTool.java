/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import com.google.common.base.Predicates;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalSword;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalToolBase;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCrystalTool
 * Created by HellFirePvP
 * Date: 10.05.2017 / 17:42
 */
public class EntityCrystalTool extends EntityItem implements EntityStarlightReacttant {

    private static final AxisAlignedBB boxCraft = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public static final int TOTAL_MERGE_TIME = 50 * 20;
    private int inertMergeTick = 0;


    public EntityCrystalTool(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCrystalTool(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    public EntityCrystalTool(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(age + 5 >= this.lifespan) {
            age = 0;
        }

        if (Config.craftingLiqCrystalToolGrowth) {
            checkIncreaseConditions();
        }
    }

    private void checkIncreaseConditions() {
        if(world.isRemote) {
            if(canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if(getProperties() == null) {
                setDead();
            }
            if(canCraft()) {
                inertMergeTick++;
                if(inertMergeTick >= TOTAL_MERGE_TIME && rand.nextInt(300) == 0) {
                    increaseSize();
                }
            } else {
                inertMergeTick = 0;
            }
        }
    }

    @Nullable
    private ToolCrystalProperties getProperties() {
        if(getItem().isEmpty()) return null;
        if(getItem().getItem() instanceof ItemCrystalToolBase) {
            return ItemCrystalToolBase.getToolProperties(getItem());
        }
        if(getItem().getItem() instanceof ItemCrystalSword) {
            return ItemCrystalSword.getToolProperties(getItem());
        }
        return null;
    }

    private void applyProperties(ToolCrystalProperties properties) {
        if(getItem().isEmpty()) return;
        if(getItem().getItem() instanceof ItemCrystalToolBase) {
            ItemCrystalToolBase.setToolProperties(getItem(), properties);
        }
        if(getItem().getItem() instanceof ItemCrystalSword) {
            ItemCrystalSword.setToolProperties(getItem(), properties);
        }
    }

    private int getMaxSize() {
        if(getItem().isEmpty()) return CrystalProperties.MAX_SIZE_CELESTIAL;
        if(getItem().getItem() instanceof ItemCrystalToolBase) {
            return ((ItemCrystalToolBase) getItem().getItem()).getCrystalCount() * CrystalProperties.MAX_SIZE_CELESTIAL;
        }
        if(getItem().getItem() instanceof ItemCrystalSword) {
            return 2 * CrystalProperties.MAX_SIZE_CELESTIAL;
        }
        return CrystalProperties.MAX_SIZE_CELESTIAL;
    }

    private void increaseSize() {
        world.setBlockToAir(getPosition());
        List<Entity> foundItems = world.getEntitiesInAABBexcluding(this, boxCraft.offset(posX, posY, posZ).grow(0.1),
                Predicates.or(EntityUtils.selectItemClassInstaceof(ItemCrystalToolBase.class), EntityUtils.selectItemClassInstaceof(ItemCrystalSword.class)));
        if(foundItems.size() <= 0) {
            CrystalProperties prop = getProperties();
            if(prop != null) {
                int max = getMaxSize();
                int grow = rand.nextInt(250) + 100;
                max = Math.min(prop.getSize() + grow, max);
                int cut = Math.max(0, prop.getCollectiveCapability() - (rand.nextInt(10) + 10));
                applyProperties(new ToolCrystalProperties(max, prop.getPurity(), cut));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnCraftingParticles() {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                posX + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                posY + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1),
                posZ + rand.nextFloat() * 0.2 * (rand.nextBoolean() ? 1 : -1));
        p.motion(rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.04  * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1));
        p.gravity(0.01);
        p.scale(0.2F).setColor(BlockCollectorCrystal.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
    }

    private boolean canCraft() {
        if(!isInLiquidStarlight(this)) return false;

        List<Entity> foundEntities = world.getEntitiesInAABBexcluding(this, boxCraft.offset(getPosition()), EntityUtils.selectEntities(Entity.class));
        return foundEntities.size() <= 0;
    }
}

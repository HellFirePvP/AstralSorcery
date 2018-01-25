/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityCrystal
 * Created by HellFirePvP
 * Date: 08.12.2016 / 19:11
 */
public class EntityCrystal extends EntityItemHighlighted implements EntityStarlightReacttant {

    private static final AxisAlignedBB boxCraft = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public static final int TOTAL_MERGE_TIME = 60 * 20;
    private int inertMergeTick = 0;

    public EntityCrystal(World worldIn) {
        super(worldIn);
    }

    public EntityCrystal(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityCrystal(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        Item i = stack.getItem();
        if(i instanceof ItemHighlighted) {
            applyColor(((ItemHighlighted) i).getHightlightColor(stack));
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(age + 5 >= this.lifespan) {
            age = 0;
        }

        if (Config.craftingLiqCrystalGrowth) {
            checkIncreaseConditions();
        }
    }

    private void checkIncreaseConditions() {
        if(world.isRemote) {
            if(canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if(CrystalProperties.getCrystalProperties(getItem()) == null) {
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

    private void increaseSize() {
        world.setBlockToAir(getPosition());
        List<Entity> foundItems = world.getEntitiesInAABBexcluding(this, boxCraft.offset(posX, posY, posZ).grow(0.1), EntityUtils.selectItemClassInstaceof(ItemRockCrystalBase.class));
        if(foundItems.size() <= 0) {
            ItemStack stack = getItem();
            CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
            int max = (stack.getItem() instanceof ItemCelestialCrystal ||
                    stack.getItem() instanceof ItemTunedCelestialCrystal) ?
                    CrystalProperties.MAX_SIZE_CELESTIAL : CrystalProperties.MAX_SIZE_ROCK;
            if(Config.canCrystalGrowthYieldDuplicates && prop.getSize() >= max && rand.nextInt(6) == 0) {
                ItemStack newStack = (stack.getItem() instanceof ItemCelestialCrystal ||
                        stack.getItem() instanceof ItemTunedCelestialCrystal) ?
                        ItemRockCrystalBase.createRandomCelestialCrystal() : ItemRockCrystalBase.createRandomBaseCrystal();
                CrystalProperties newProp = new CrystalProperties(
                        rand.nextInt(100) + 20,
                        Math.min(prop.getPurity() + rand.nextInt(10), 100),
                        rand.nextInt(40) + 30);
                CrystalProperties.applyCrystalProperties(newStack, newProp);
                ItemUtils.dropItemNaturally(world, posX, posY, posZ, newStack);

                CrystalProperties.applyCrystalProperties(stack,
                        new CrystalProperties(rand.nextInt(300) + 100, prop.getPurity(), rand.nextInt(40) + 30));
            } else {
                int grow = rand.nextInt(90) + 40;
                max = Math.min(prop.getSize() + grow, max);
                CrystalProperties.applyCrystalProperties(stack,
                        new CrystalProperties(max, prop.getPurity(), prop.getCollectiveCapability()));
            }
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
        p.gravity(0.01);
        p.scale(0.2F).setColor(getHighlightColor());
    }

    private boolean canCraft() {
        if(!isInLiquidStarlight(this)) return false;

        List<Entity> foundEntities = world.getEntitiesInAABBexcluding(this, boxCraft.offset(getPosition()), EntityUtils.selectEntities(Entity.class));
        return foundEntities.size() <= 0;
    }

}

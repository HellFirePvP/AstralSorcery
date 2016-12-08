package hellfirepvp.astralsorcery.common.entities;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.fluid.FluidBlockLiquidStarlight;
import hellfirepvp.astralsorcery.common.item.base.ItemHighlighted;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
public class EntityCrystal extends EntityItemHighlighted {

    private static final AxisAlignedBB boxCraft = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    public static final int TOTAL_MERGE_TIME = 300 * 20;
    private int inertMergeTick = 0;

    public EntityCrystal(World worldIn) {
        super(worldIn);
        setNoDespawn();
    }

    public EntityCrystal(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setNoDespawn();
    }

    public EntityCrystal(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        Item i = stack.getItem();
        if(i != null && i instanceof ItemHighlighted) {
            applyColor(((ItemHighlighted) i).getHightlightColor(stack));
        }
        setNoDespawn();
        if(CrystalProperties.getCrystalProperties(stack) == null) {
            setDead();
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        checkIncreaseConditions();
    }

    private void checkIncreaseConditions() {
        if(worldObj.isRemote) {
            if(canCraft()) {
                spawnCraftingParticles();
            }
        } else {
            if(CrystalProperties.getCrystalProperties(getEntityItem()) == null) {
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
        worldObj.setBlockToAir(getPosition());
        List<Entity> foundItems = worldObj.getEntitiesInAABBexcluding(this, boxCraft.offset(posX, posY, posZ).expandXyz(0.1), EntityUtils.selectItemClassInstaceof(ItemRockCrystalBase.class));
        if(foundItems.size() <= 0) {
            ItemStack stack = getEntityItem();
            CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
            int max = (stack.getItem() instanceof ItemCelestialCrystal ||
                    stack.getItem() instanceof ItemTunedCelestialCrystal) ?
                    CrystalProperties.MAX_SIZE_CELESTIAL : CrystalProperties.MAX_SIZE_ROCK;
            int grow = rand.nextInt(20) + 5;
            max = Math.min(prop.getSize() + grow, max);
            CrystalProperties.applyCrystalProperties(stack,
                    new CrystalProperties(max, prop.getPurity(), prop.getCollectiveCapability()));
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
        BlockPos at = getPosition();
        IBlockState state = worldObj.getBlockState(at);
        if(!(state.getBlock() instanceof FluidBlockLiquidStarlight)) {
            return false;
        }
        if(!((FluidBlockLiquidStarlight) state.getBlock()).isSourceBlock(worldObj, at)) {
            return false;
        }
        state = worldObj.getBlockState(at.down());
        if(!state.isSideSolid(worldObj, at.down(), EnumFacing.UP)) {
            return false;
        }
        List<Entity> foundEntities = worldObj.getEntitiesInAABBexcluding(this, boxCraft.offset(getPosition()), EntityUtils.selectEntities(Entity.class));
        return foundEntities.size() <= 0;
    }

}

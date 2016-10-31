package hellfirepvp.astralsorcery.common.block.fluid;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidBlockLiquidStarlight
 * Created by HellFirePvP
 * Date: 14.09.2016 / 11:38
 */
public class FluidBlockLiquidStarlight extends BlockFluidClassic {

    public FluidBlockLiquidStarlight() {
        super(BlocksAS.fluidLiquidStarlight, new MaterialLiquid(MapColor.SILVER));
        setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        Integer level = stateIn.getValue(LEVEL);
        double percHeight = 1D - (((double) level + 1) / 8D);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        p.offset(0, percHeight, 0);
        p.offset(rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1), 0, rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.2F).gravity(0.006).setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        if (rand.nextInt(3) == 0) {
            p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            p.offset(0, percHeight, 0);
            p.offset(rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1), 0, rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.2F).gravity(0.006).setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        }
    }

    @Override
    protected boolean canFlowInto(IBlockAccess world, BlockPos pos) {
        return super.canFlowInto(world, pos);
    }

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        AxisAlignedBB box = iblockstate.getBoundingBox(world, blockpos).offset(blockpos);
        AxisAlignedBB entityBox = entity.getEntityBoundingBox();//.offset(entity.posX, entity.posY, entity.posZ);
        return box.intersectsWith(entityBox) && materialIn.isLiquid();
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

        if (entityIn instanceof EntityPlayer) {
            ((EntityPlayer) entityIn).addPotionEffect(new PotionEffect(Potion.getPotionById(16), 300, 0, true, true));
        }
    }
}

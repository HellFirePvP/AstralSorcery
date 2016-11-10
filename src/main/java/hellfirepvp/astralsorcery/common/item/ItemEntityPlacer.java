package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemEntityPlacer
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:04
 */
public class ItemEntityPlacer extends Item {

    public ItemEntityPlacer() {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (PlacerType type : PlacerType.values()) {
            subItems.add(new ItemStack(itemIn, 1, type.getMeta()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Item i = stack.getItem();
        if(i instanceof ItemEntityPlacer) {
            PlacerType type = PlacerType.values()[stack.getItemDamage()];
            return super.getUnlocalizedName(stack) + "." + type.getUnlocalizedName();
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand == null || hand == EnumHand.OFF_HAND)
            return EnumActionResult.PASS; //LUL rekt 1.9 features

        if (worldIn.isRemote) return EnumActionResult.PASS;
        BlockPos positionSuggested = pos.offset(facing);
        if (worldIn.isAirBlock(positionSuggested)) {
            int meta = stack.getItemDamage();
            if(meta < 0 || meta >= PlacerType.values().length)
                return EnumActionResult.PASS;
            PlacerType type = PlacerType.values()[meta];
            Entity toSpawn = type.provideEntity(worldIn);
            if(entityFits(worldIn, positionSuggested, toSpawn)) {
                toSpawn.setPositionAndRotation(
                        positionSuggested.getX() + 0.5, positionSuggested.getY(), positionSuggested.getZ() + 0.5,
                        0.5F, 0.5F);
                worldIn.spawnEntityInWorld(toSpawn);

                if (!playerIn.isCreative()) {
                    stack.stackSize -= 1;
                }
            }
        }
        return EnumActionResult.PASS;
    }

    private boolean entityFits(World worldIn, BlockPos pos, Entity toSpawn) {
        BlockPos.PooledMutableBlockPos mut = BlockPos.PooledMutableBlockPos.retain();
        for (int xx = MathHelper.floor_double(-toSpawn.width); xx <= MathHelper.ceiling_double_int(toSpawn.width); xx++) {
            for (int zz = MathHelper.floor_double(-toSpawn.width); zz <= MathHelper.ceiling_double_int(toSpawn.width); zz++) {
                for (int yy = 0; yy <= MathHelper.ceiling_double_int(toSpawn.height); yy++) {
                    mut.setPos(pos).add(xx, yy, zz);
                    if(!worldIn.isAirBlock(mut)) {
                        mut.release();
                        return false;
                    }
                }
            }
        }
        mut.release();
        return true;
    }

    public static enum PlacerType {

        TELESCOPE(EntityTelescope::new),
        GRINDSTONE(EntityGrindstone::new);

        private final EntityProvider<? extends Entity> provider;

        private PlacerType(EntityProvider<? extends Entity> provider) {
            this.provider = provider;
        }

        public String getUnlocalizedName() {
            return name().toLowerCase();
        }

        public ItemStack asStack() {
            return new ItemStack(ItemsAS.entityPlacer, 1, getMeta());
        }

        public int getMeta() {
            return ordinal();
        }

        public Entity provideEntity(World world) {
            return provider.provideNewEntity(world);
        }

    }

    public static interface EntityProvider<T extends Entity> {

        public T provideNewEntity(World world);

    }

}

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.ItemInfusedGlass;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMapDrawingTable
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:32
 */
public class BlockMapDrawingTable extends BlockContainer {

    private static final AxisAlignedBB drawingTableBox = new AxisAlignedBB(-6.0 / 16.0, 0, -4.0 / 16.0, 22.0 / 16.0, 24.0 / 16.0, 20.0 / 16.0);

    public BlockMapDrawingTable() {
        super(Material.ROCK);
        setHardness(2F);
        setSoundType(SoundType.WOOD);
        setResistance(15F);
        setHarvestLevel("axe", 1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i < rand.nextInt(2) + 2; i++) {
            Vector3 offset = new Vector3(-5.0 / 16.0, 1.505, -3.0 / 16.0);
            int random = rand.nextInt(12);
            if(random > 5) {
                offset.addX(24.0 / 16.0);
            }
            offset.addZ((random % 6) * (4.0 / 16.0));
            offset.add(rand.nextFloat() * 0.1, 0, rand.nextFloat() * 0.1).add(pos);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(offset.getX(), offset.getY(), offset.getZ());
            p.scale(rand.nextFloat() * 0.1F + 0.15F).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.gravity(0.004F).setMaxAge(rand.nextInt(30) + 35);
            switch (random) {
                case 0:
                    p.setColor(new Color(0xFF0800));
                    break;
                case 1:
                    p.setColor(new Color(0xFFCC00));
                    break;
                case 2:
                    p.setColor(new Color(0x6FFF00));
                    break;
                case 3:
                    p.setColor(new Color(0x00FCFF));
                    break;
                case 4:
                    p.setColor(new Color(0x0028FF));
                    break;
                case 5:
                    p.setColor(new Color(0xFF00FE));
                    break;
                case 6:
                    p.setColor(new Color(0xF07800));
                    break;
                case 7:
                    p.setColor(new Color(0xB4F000));
                    break;
                case 8:
                    p.setColor(new Color(0x01F000));
                    break;
                case 9:
                    p.setColor(new Color(0x007AF0));
                    break;
                case 10:
                    p.setColor(new Color(0x3900F0));
                    break;
                case 11:
                    p.setColor(new Color(0xf0007B));
                    break;
            }
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            TileMapDrawingTable tm = MiscUtils.getTileAt(worldIn, pos, TileMapDrawingTable.class, true);
            if (tm != null) {
                if(playerIn.isSneaking()) {
                    if(!tm.getSlotIn().isEmpty()) {
                        playerIn.inventory.placeItemBackInInventory(worldIn, tm.getSlotIn());
                        tm.putSlotIn(ItemStack.EMPTY);
                        return true;
                    }
                    if(!tm.getSlotGlassLens().isEmpty()) {
                        playerIn.inventory.placeItemBackInInventory(worldIn, tm.getSlotGlassLens());
                        tm.putGlassLens(ItemStack.EMPTY);
                        return true;
                    }
                } else {
                    if(!held.isEmpty()) {
                        if(held.getItem() instanceof ItemCraftingComponent) {
                            if (held.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()) {
                                int remaining = tm.addParchment(held.getCount());
                                if (remaining < held.getCount()) {
                                    worldIn.playSound(null, pos, Sounds.bookFlip, Sounds.bookFlip.getCategory(), 1F, 1F);
                                    if (!playerIn.isCreative()) {
                                        held.setCount(remaining);
                                        if (held.getCount() <= 0) {
                                            playerIn.setHeldItem(hand, ItemStack.EMPTY);
                                        } else {
                                            playerIn.setHeldItem(hand, held);
                                        }
                                    }
                                }
                            }
                        } else if(held.getItem() instanceof ItemInfusedGlass) {
                            if (tm.getSlotGlassLens().isEmpty()) {
                                tm.putGlassLens(held);
                                if (!playerIn.isCreative()) {
                                    held.shrink(1);
                                    if (held.getCount() <= 0) {
                                        playerIn.setHeldItem(hand, ItemStack.EMPTY);
                                    } else {
                                        playerIn.setHeldItem(hand, held);
                                    }
                                }
                            }
                        } else if((held.getItem() instanceof ItemBook || held.isItemEnchantable()) && tm.getSlotIn().isEmpty()) {
                            tm.putSlotIn(ItemUtils.copyStackWithSize(held, 1));
                            if(!playerIn.isCreative()) {
                                held.shrink(1);
                                if(held.getCount() <= 0) {
                                    playerIn.setHeldItem(hand, ItemStack.EMPTY);
                                } else {
                                    playerIn.setHeldItem(hand, held);
                                }
                            }
                        } else if(held.getItem() instanceof ItemPotion && PotionUtils.getEffectsFromStack(held).isEmpty() && tm.getSlotIn().isEmpty()) {
                            tm.putSlotIn(held);
                            if(!playerIn.isCreative()) {
                                held.shrink(1);
                                if(held.getCount() <= 0) {
                                    playerIn.setHeldItem(hand, ItemStack.EMPTY);
                                } else {
                                    playerIn.setHeldItem(hand, held);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if(!playerIn.isSneaking()) {
                ItemStack held = playerIn.getHeldItem(hand);
                if (!held.isEmpty()) {
                    if((held.getItem() instanceof ItemCraftingComponent &&
                            (held.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()))
                            || held.getItem() instanceof ItemInfusedGlass
                            || held.isItemEnchantable()
                            || held.getItem() instanceof ItemBook
                            || (held.getItem() instanceof ItemPotion && PotionUtils.getEffectsFromStack(held).isEmpty())) {
                        return true;
                    }
                }
                AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.MAP_DRAWING, playerIn, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileMapDrawingTable tm = MiscUtils.getTileAt(worldIn, pos, TileMapDrawingTable.class, true);
        if(tm != null) {
            tm.dropContents();
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return drawingTableBox;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMapDrawingTable();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMapDrawingTable();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

}

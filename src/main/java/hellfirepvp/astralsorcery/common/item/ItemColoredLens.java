/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.network.packet.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemColoredLens
 * Created by HellFirePvP
 * Date: 29.11.2016 / 12:35
 */
public class ItemColoredLens extends Item implements ItemDynamicColor {

    public ItemColoredLens() {
        setMaxStackSize(16);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if(this.isInCreativeTab(tab)) {
            for (ColorType ct : ColorType.values()) {
                subItems.add(new ItemStack(this, 1, ct.getMeta()));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(!stack.isEmpty() && stack.getItem() instanceof ItemColoredLens) {
            int dmg = stack.getItemDamage();
            if(dmg >= 0 && dmg < ColorType.values().length) {
                tooltip.add(I18n.format("item.itemcoloredlens.effect." + ColorType.values()[dmg].name().toLowerCase() + ".name"));
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            ItemStack inHand = playerIn.getHeldItem(EnumHand.MAIN_HAND);
            ColorType type = null;
            if(!inHand.isEmpty() && inHand.getItem() instanceof ItemColoredLens) {
                int dmg = inHand.getItemDamage();
                if(dmg >= 0 && dmg < ColorType.values().length) {
                    type = ColorType.values()[dmg];
                }
            }
            if(type != null) {
                TileCrystalLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
                if(lens != null) {
                    ColorType oldType = lens.setLensColor(type);
                    if(!playerIn.isCreative()) {
                        inHand.setCount(inHand.getCount() - 1);
                        if(inHand.getCount() <= 0) {
                            playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                    SoundHelper.playSoundAround(Sounds.clipSwitch, worldIn, pos, 0.8F, 1.5F);
                    if(oldType != null) {
                        playerIn.inventory.placeItemBackInInventory(worldIn, oldType.asStack());
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        int dmg = stack.getItemDamage();
        if(dmg < 0 || dmg >= ColorType.values().length) return 0xFFFFFFFF;
        return ColorType.values()[dmg].colorRGB;
    }

    public static enum ColorType {

        FIRE    (TargetType.ENTITY, 0xff7f00, 0.07F),
        BREAK   (TargetType.BLOCK,  0xffdf00, 0.07F),
        GROW    (TargetType.BLOCK,  0x00df00, 0.07F),
        DAMAGE  (TargetType.ENTITY, 0xdf0000, 0.07F),
        REGEN   (TargetType.ENTITY, 0xff7fbf, 0.07F),
        PUSH    (TargetType.ENTITY, 0x00dfff, 0.07F),

        SPECTRAL(TargetType.NONE,   0x7f00bf, 0.25F);

        public final int colorRGB;
        public final Color wrappedColor;
        private final float flowReduction;
        private final TargetType type;

        private ColorType(TargetType type, int colorRGB, float flowReduction) {
            this.type = type;
            this.colorRGB = colorRGB;
            this.wrappedColor = new Color(colorRGB);
            this.flowReduction = flowReduction;
        }

        public TargetType getType() {
            return type;
        }

        public float getFlowReduction() {
            return flowReduction;
        }

        public String getUnlocalizedName() {
            return name().toLowerCase();
        }

        public ItemStack asStack() {
            return new ItemStack(ItemsAS.coloredLens, 1, getMeta());
        }

        public int getMeta() {
            return ordinal();
        }

        public void onEntityInBeam(Vector3 beamOrigin, Vector3 beamTarget, Entity entity, float percStrength) {
            switch (this) {
                case FIRE:
                    if(itemRand.nextFloat() > percStrength) return;
                    if(entity instanceof EntityItem) {
                        ItemStack current = ((EntityItem) entity).getItem();
                        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(current);
                        if(!result.isEmpty()) {
                            Vector3 entityPos = Vector3.atEntityCenter(entity);
                            ItemUtils.dropItemNaturally(entity.getEntityWorld(), entityPos.getX(), entityPos.getY(), entityPos.getZ(), ItemUtils.copyStackWithSize(result, 1));
                            if(current.getCount() > 1) {
                                current.shrink(1);
                                ((EntityItem) entity).setItem(current);
                            } else {
                                entity.setDead();
                            }
                        }
                    } else if(entity instanceof EntityLivingBase) {
                        entity.setFire(1);
                    }
                    break;
                case DAMAGE:
                    if(!(entity instanceof EntityLivingBase)) return;
                    if(itemRand.nextFloat() > percStrength) return;
                    if(entity instanceof EntityPlayer && entity.getServer() != null && entity.getServer().isPVPEnabled()) return;
                    entity.attackEntityFrom(CommonProxy.dmgSourceStellar, 6.5F);
                    break;
                case REGEN:
                    if(!(entity instanceof EntityLivingBase)) return;
                    if(itemRand.nextFloat() > percStrength) return;
                    ((EntityLivingBase) entity).heal(3.5F);
                    break;
                case PUSH:
                    if(entity instanceof EntityPlayer || itemRand.nextFloat() > percStrength) return;
                    Vector3 dir = beamTarget.clone().subtract(beamOrigin).normalize().multiply(0.5F);
                    entity.motionX = Math.min(1F, entity.motionZ + dir.getX());
                    entity.motionY = Math.min(1F, entity.motionY + dir.getY());
                    entity.motionZ = Math.min(1F, entity.motionZ + dir.getZ());
                    break;
            }
        }

        public void onBlockOccupyingBeam(World world, BlockPos at, IBlockState state, float percStrength) {
            switch (this) {
                case BREAK:
                    float hardness = state.getBlockHardness(world, at);
                    if(hardness < 0) return;
                    hardness *= 1.5F;
                    BlockBreakAssist.addProgress(world, at, hardness, percStrength * 4F);
                    PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.EffectType.BEAM_BREAK, at);
                    pkt.data = Block.getStateId(state);
                    PacketChannel.CHANNEL.sendToAllAround(pkt, PacketChannel.pointFromPos(world, at, 16));
                    break;
                case GROW:
                    if(world.rand.nextFloat() > percStrength) return;
                    CropHelper.GrowablePlant plant = CropHelper.wrapPlant(world, at);
                    if(plant != null) {
                        plant.tryGrow(world, world.rand);
                        PktParticleEvent packet = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, at);
                        PacketChannel.CHANNEL.sendToAllAround(packet, PacketChannel.pointFromPos(world, at, 16));
                    }
                    break;
                /*case HARVEST:
                    if(world.rand.nextFloat() > percStrength) return;
                    CropHelper.HarvestablePlant harvest = CropHelper.wrapHarvestablePlant(world, at);
                    if(harvest != null) {
                        harvest.tryGrow(world, world.rand);
                        if(harvest.canHarvest(world)) {
                            List<ItemStack> drops = harvest.harvestDropsAndReplant(world, world.rand, 4);
                            for (ItemStack st : drops) {
                                ItemUtils.dropItemNaturally(world, at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, st);
                            }
                        }
                        PktParticleEvent packet = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, at);
                        PacketChannel.CHANNEL.sendToAllAround(packet, PacketChannel.pointFromPos(world, at, 16));
                    }
                    break;*/
            }
        }

    }

    //Respectively only Entity-checks or only block-checks will be done.
    public static enum TargetType {

        ENTITY, BLOCK, NONE

    }

}

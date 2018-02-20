/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool.wand;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingDepthParticle;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.item.base.ISpecialInteractItem;
import hellfirepvp.astralsorcery.common.item.base.IWandInteract;
import hellfirepvp.astralsorcery.common.item.base.render.INBTModel;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.network.packet.server.PktShootEntity;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemWand
 * Created by HellFirePvP
 * Date: 23.09.2016 / 12:57
 */
public class ItemWand extends Item implements ISpecialInteractItem, INBTModel {

    public static final Color wandBlue = new Color(0x0C1576);

    private static final Random rand = new Random();

    public ItemWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return getAugment(stack) == WandAugment.ARMARA && entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            for (WandAugment wa : WandAugment.values()) {
                ItemStack wand = new ItemStack(this);
                setAugment(wand, wa);
                items.add(wand);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        WandAugment wa = getAugment(stack);
        if(wa != null) {
            tooltip.add(TextFormatting.BLUE + I18n.format(wa.getAssociatedConstellation().getUnlocalizedName()));
        }
    }

    @Override
    public List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation) {
        LinkedList<ResourceLocation> out = new LinkedList<>();
        out.add(defaultLocation);
        for (WandAugment wa : WandAugment.values()) {
            out.add(new ResourceLocation(defaultLocation.getResourceDomain(), defaultLocation.getResourcePath() + "_" + wa.name().toLowerCase()));
        }
        return out;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation suggestedDefaultLocation) {
        WandAugment wa = getAugment(stack);
        if(wa != null) {
            return new ModelResourceLocation(new ResourceLocation(suggestedDefaultLocation.getResourceDomain(),
                    suggestedDefaultLocation.getResourcePath() + "_" + wa.name().toLowerCase()),
                    suggestedDefaultLocation.getVariant());
        }
        return suggestedDefaultLocation;
    }

    @Nullable
    public static WandAugment getAugment(@Nonnull ItemStack stack) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if(!cmp.hasKey("AugmentName")) {
            return null;
        }
        IMajorConstellation cst = ConstellationRegistry.getMajorConstellationByName(cmp.getString("AugmentName"));
        if(cst == null) {
            return null;
        }
        return WandAugment.getByConstellation(cst);
    }

    public static ItemStack setAugment(@Nonnull ItemStack stack, @Nonnull WandAugment augment) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        cmp.setString("AugmentName", augment.getAssociatedConstellation().getUnlocalizedName());
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        WandAugment wa = getAugment(stack);
        if(wa != null) {
            if(wa.equals(WandAugment.ARMARA)) {
                return EnumAction.BLOCK;
            }
            if(wa.equals(WandAugment.VICIO)) {
                return EnumAction.BOW;
            }
        }
        return EnumAction.NONE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if(!itemstack.isEmpty()) {
            WandAugment wa = getAugment(itemstack);
            if(wa != null && (wa == WandAugment.ARMARA || wa == WandAugment.VICIO)) {
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        World world = player.getEntityWorld();
        if(stack.isEmpty()) return;

        WandAugment wa = getAugment(stack);
        if(wa != null) {
            if(!world.isRemote) {

            } else {
                if(wa == WandAugment.VICIO) {
                    playVicioEffect(stack, player, count);
                } else if(wa == WandAugment.ARMARA) {
                    playArmaraEffect(stack, player, count);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playArmaraEffect(ItemStack stack, EntityLivingBase player, int count) {
        if(player.ticksExisted % 2 == 0) {
            Collection<Vector3> positions = MiscUtils.getCirclePositions(
                    Vector3.atEntityCorner(player).addY(player.height / 2),
                    Vector3.RotAxis.Y_AXIS, 0.8F - rand.nextFloat() * 0.1F, 20 + rand.nextInt(10));
            for (Vector3 v : positions) {
                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                particle.gravity(0.004);
                particle.motion(0, (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.01, 0);
                if(rand.nextInt(3) == 0) {
                    particle.setColor(Color.WHITE);
                    particle.scale(0.1F + rand.nextFloat() * 0.05F);
                    particle.setMaxAge(15 + rand.nextInt(10));
                } else {
                    particle.setColor(wandBlue);
                    particle.scale(0.15F + rand.nextFloat() * 0.1F);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playVicioEffect(ItemStack stack, EntityLivingBase entity, int count) {
        int strTick = this.getMaxItemUseDuration(stack) - count;
        if(strTick <= 2) return;
        float mul = MathHelper.clamp(((float) strTick) / 30F, 0F, 1F);

        Vector3 look = new Vector3(entity.getLook(1F)).normalize().multiply(mul * 3);
        Vector3 motionReverse = look.clone().normalize().multiply(-0.4 * mul);
        Vector3 perp = look.clone().perpendicular();
        Vector3 origin = new Vector3(entity.posX + entity.width / 2F, entity.posY + entity.height, entity.posZ + entity.width / 2F);

        for (int i = 0; i < 4; i++) {
            if(rand.nextFloat() < mul) {
                Vector3 at = look.clone().multiply(0.2 + rand.nextFloat() * 2.5).add(perp.clone().rotate(rand.nextFloat() * 360, look).multiply(rand.nextFloat() * 0.5)).add(origin);

                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                p.scale(0.35F + rand.nextFloat() * 0.2F).setMaxAge(10 + rand.nextInt(10));
                p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).setAlphaMultiplier(1F);
                p.gravity(0.004);
                p.setColor(wandBlue);
                p.motion(motionReverse.getX(), motionReverse.getY(), motionReverse.getZ());
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        WandAugment wa = getAugment(stack);
        if(wa != null) {
            switch (wa) {
                case VICIO:
                    return 72000;
                case ARMARA:
                    return 72000;
                case EVORSIO:
                case AEVITAS:
                case DISCIDIA:
                    break;
            }
        }
        return super.getMaxItemUseDuration(stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if(worldIn.isRemote) return;

        WandAugment wa = getAugment(stack);
        if(wa != null) {
            if(wa.equals(WandAugment.VICIO)) {
                int strTick = this.getMaxItemUseDuration(stack) - timeLeft;
                if(strTick <= 2) return;
                float mul = MathHelper.clamp(((float) strTick) / 30F, 0F, 1F);
                Vec3d vec = entityLiving.getLook(1F);
                Vector3 motionApply = new Vector3(vec).normalize().multiply(mul * 3F);
                if(motionApply.getY() > 0) {
                    motionApply.setY(MathHelper.clamp(motionApply.getY() + (0.7F * mul), 0.7F * mul, Float.MAX_VALUE));
                }

                entityLiving.motionX = motionApply.getX();
                entityLiving.motionY = motionApply.getY();
                entityLiving.motionZ = motionApply.getZ();
                entityLiving.fallDistance = 0F;
                PktShootEntity pkt = new PktShootEntity(entityLiving.getEntityId(), motionApply);
                pkt.setEffectLength(mul);
                PacketChannel.CHANNEL.sendToDimension(pkt, worldIn.provider.getDimension());
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!isSelected) isSelected = (entityIn instanceof EntityPlayer) && ((EntityPlayer) entityIn).getHeldItemOffhand() == stack;

        if(!worldIn.isRemote) {
            if(isSelected) {
                if (worldIn.getTotalWorldTime() % 20 == 0 && entityIn instanceof EntityPlayerMP) {
                    double dstr = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(worldIn);
                    if(dstr <= 1E-4) return;

                    RockCrystalBuffer buf = WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
                    ChunkPos pos = new ChunkPos(entityIn.getPosition());
                    List<BlockPos> posList = buf.collectPositions(pos, 4);
                    for (BlockPos rPos : posList) {
                        IBlockState state = worldIn.getBlockState(rPos);
                        if (!(state.getBlock() instanceof BlockCustomOre) || state.getValue(BlockCustomOre.ORE_TYPE) != BlockCustomOre.OreType.ROCK_CRYSTAL) {
                            buf.removeOre(rPos);
                            continue;
                        }
                        BlockPos p = rPos.up();
                        PktParticleEvent pkt = new PktParticleEvent(PktParticleEvent.ParticleEventType.WAND_CRYSTAL_HIGHLIGHT, p.getX(), p.getY(), p.getZ());
                        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) entityIn);
                    }
                }
                WandAugment wa = getAugment(stack);
                if (wa != null) {
                    if (wa == WandAugment.AEVITAS) {
                        BlockPos playerPos = entityIn.getPosition();
                        for (int xx = -1; xx <= 1; xx++) {
                            for (int zz = -1; zz <= 1; zz++) {
                                BlockPos at = playerPos.add(xx, -1, zz);
                                if (MiscUtils.isChunkLoaded(worldIn, at) && !worldIn.isOutsideBuildHeight(at) && worldIn.getBlockState(at).getBlock().equals(Blocks.AIR)) {
                                    worldIn.setBlockState(at, BlocksAS.blockVanishing.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if(isSelected) {
                playClientEffects(stack, entityIn);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playClientEffects(ItemStack stack, Entity entityIn) {
        WandAugment wa = getAugment(stack);
        if(wa != null) {
            if(rand.nextFloat() <= 0.7F) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                        entityIn.posX - entityIn.width / 2 + rand.nextFloat() * entityIn.width,
                        entityIn.posY + rand.nextFloat() * (entityIn.height / 2),
                        entityIn.posZ - entityIn.width / 2 + rand.nextFloat() * entityIn.width);
                p.gravity(0.004).scale(0.3F + rand.nextFloat() * 0.2F).setMaxAge(45 + rand.nextInt(20));
                p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).setAlphaMultiplier(1F);
                p.motion(0, -rand.nextFloat() * 0.001, 0);
                if(rand.nextInt(4) == 0) {
                    p.setColor(Color.WHITE);
                    p.scale(0.1F + rand.nextFloat() * 0.1F);
                } else {
                    p.setColor(wandBlue);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void highlightEffects(PktParticleEvent event) {
        BlockPos orePos = event.getVec().toBlockPos();
        BlockPos pos = Minecraft.getMinecraft().world.getTopSolidOrLiquidBlock(orePos).up();
        Vector3 display = new Vector3(pos);
        MiscUtils.applyRandomOffset(display, rand, 2);
        double velX = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
        double velY = rand.nextFloat() * 0.3F;
        double velZ = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
        double dstr = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(Minecraft.getMinecraft().world);
        for (int i = 0; i < 10; i++) {
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(display.getX(), display.getY(), display.getZ());
            particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
            particle.motion(velX * (0.2 + 0.8 * rand.nextFloat()), velY * (0.4 + 0.6 * rand.nextFloat()), velZ * (0.2 + 0.8 * rand.nextFloat()));
            particle.scale(0.7F).setMaxAge(70);
            particle.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).setAlphaMultiplier((float) ((150 * dstr) / 255F));
        }
        dstr = Math.sqrt(dstr);
        dstr = Math.sqrt(dstr);
        Vector3 plVec = Vector3.atEntityCorner(Minecraft.getMinecraft().player);
        float dst = (float) event.getVec().distance(plVec);
        float dstMul = dst <= 25 ? 1F : (dst >= 50 ? 0F : (1F - (dst - 25F) / 25F));
        if(dstMul >= 1E-4) {
            EntityFXFacingDepthParticle p = EffectHelper.genericDepthIgnoringFlareParticle(
                    orePos.getX() - 1 + rand.nextFloat() * 3,
                    orePos.getY() - 1 + rand.nextFloat() * 3,
                    orePos.getZ() - 1 + rand.nextFloat() * 3);
            p.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
            p.gravity(0.004).scale(0.4F).setAlphaMultiplier((float) ((150 * dstr) / 255F) * dstMul);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT).setMaxAge(30 + rand.nextInt(10));
        }
    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if(b instanceof IWandInteract) {
            ((IWandInteract) b).onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
            return true;
        }
        IWandInteract wandTe = MiscUtils.getTileAt(world, pos, IWandInteract.class, true);
        if(wandTe != null) {
            wandTe.onInteract(world, pos, entityPlayer, side, entityPlayer.isSneaking());
            return true;
        }
        return false;
    }
}

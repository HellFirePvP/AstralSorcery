/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ExtendedChainingPlayerController
 * Created by HellFirePvP
 * Date: 23.12.2016 / 11:32
 */
public class ExtendedChainingPlayerController extends PlayerControllerMP {

    public static boolean call = false;

    private final PlayerControllerMP delegate;
    private float reachModifier = 0.0F;

    public ExtendedChainingPlayerController(PlayerControllerMP oldCtrl) {
        super(Minecraft.getMinecraft(), oldCtrl.connection);
        this.delegate = oldCtrl;
    }

    public void setReachModifier(float reachModifier) {
        this.reachModifier = reachModifier;
    }

    @Override
    public void setPlayerCapabilities(EntityPlayer player) {
        delegate.setPlayerCapabilities(player);
    }

    @Override
    public boolean isSpectator() {
        return delegate.isSpectator();
    }

    @Override
    public void setGameType(GameType type) {
        delegate.setGameType(type);
    }

    @Override
    public void flipPlayer(EntityPlayer playerIn) {
        delegate.flipPlayer(playerIn);
    }

    @Override
    public boolean shouldDrawHUD() {
        return delegate.shouldDrawHUD();
    }

    @Override
    public boolean onPlayerDestroyBlock(BlockPos pos) {
        return delegate.onPlayerDestroyBlock(pos);
    }

    @Override
    public boolean clickBlock(BlockPos loc, EnumFacing face) {
        return delegate.clickBlock(loc, face);
    }

    @Override
    public void resetBlockRemoving() {
        delegate.resetBlockRemoving();
    }

    @Override
    public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
        return delegate.onPlayerDamageBlock(posBlock, directionFacing);
    }

    @Override
    public float getBlockReachDistance() {
        call = true;
        return delegate.getBlockReachDistance() + reachModifier;
    }

    @Override
    public void updateController() {
        delegate.updateController();
    }

    @Override
    public boolean getIsHittingBlock() {
        return delegate.getIsHittingBlock();
    }

    @Override
    public EnumActionResult processRightClick(EntityPlayer player, World worldIn, EnumHand hand) {
        return delegate.processRightClick(player, worldIn, hand);
    }

    @Override
    public EnumActionResult processRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand) {
        return delegate.processRightClickBlock(player, worldIn, pos, direction, vec, hand);
    }

    @Override
    public EntityPlayerSP createPlayer(World p_192830_1_, StatisticsManager p_192830_2_, RecipeBook p_192830_3_) {
        return delegate.createPlayer(p_192830_1_, p_192830_2_, p_192830_3_);
    }

    @Override
    public void func_194338_a(int p_194338_1_, IRecipe p_194338_2_, boolean p_194338_3_, EntityPlayer p_194338_4_) {
        delegate.func_194338_a(p_194338_1_, p_194338_2_, p_194338_3_, p_194338_4_);
    }

    @Override
    public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
        delegate.attackEntity(playerIn, targetEntity);
    }

    @Override
    public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, EnumHand hand) {
        return delegate.interactWithEntity(player, target, hand);
    }

    @Override
    public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, RayTraceResult ray, EnumHand hand) {
        return delegate.interactWithEntity(player, target, ray, hand);
    }

    @Override
    public ItemStack windowClick(int windowId, int slotId, int mouseButton, ClickType type, EntityPlayer player) {
        return delegate.windowClick(windowId, slotId, mouseButton, type, player);
    }

    @Override
    public void sendEnchantPacket(int windowID, int button) {
        delegate.sendEnchantPacket(windowID, button);
    }

    @Override
    public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
        delegate.sendSlotPacket(itemStackIn, slotId);
    }

    @Override
    public void sendPacketDropItem(ItemStack itemStackIn) {
        delegate.sendPacketDropItem(itemStackIn);
    }

    @Override
    public void onStoppedUsingItem(EntityPlayer playerIn) {
        delegate.onStoppedUsingItem(playerIn);
    }

    @Override
    public boolean gameIsSurvivalOrAdventure() {
        return delegate.gameIsSurvivalOrAdventure();
    }

    @Override
    public boolean isInCreativeMode() {
        return delegate.isInCreativeMode();
    }

    @Override
    public boolean isNotCreative() {
        return delegate.isNotCreative();
    }

    @Override
    public boolean isRidingHorse() {
        return delegate.isRidingHorse();
    }

    @Override
    public boolean isSpectatorMode() {
        return delegate.isSpectatorMode();
    }

    @Override
    public boolean extendedReach() {
        return delegate.extendedReach();
    }

    @Override
    public GameType getCurrentGameType() {
        return delegate.getCurrentGameType();
    }

    @Override
    public void pickItem(int index) {
        delegate.pickItem(index);
    }
}

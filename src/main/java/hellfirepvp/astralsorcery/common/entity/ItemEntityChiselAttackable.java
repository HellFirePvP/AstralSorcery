/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entity;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ItemEntityChiselAttackable
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ItemEntityChiselAttackable extends ItemEntityHighlighted {

    public ItemEntityChiselAttackable(EntityType<? extends ItemEntityChiselAttackable> entityType, Level level) {
        super(entityType, level);
        this.bobOffs = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.refreshDimensions();
    }

    public ItemEntityChiselAttackable(EntityType<? extends ItemEntityChiselAttackable> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        this(entityType, level, posX, posY, posZ, itemStack, level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntityChiselAttackable(EntityType<? extends ItemEntityChiselAttackable> entityType, Level level, double posX, double posY, double posZ, ItemStack itemStack, double deltaX, double deltaY, double deltaZ) {
        this(entityType, level);
        this.setPos(posX, posY, posZ);
        this.setDeltaMovement(deltaX, deltaY, deltaZ);
        this.setItem(itemStack);
        this.lifespan = itemStack.getEntityLifespan(level);
    }

    @Override
    public boolean isPickable() {
        if (this.level().isClientSide()) {
            return this.clientHoldsChisel();
        }
        return true;
    }

    @Override
    public boolean isAttackable() {
        if (this.level().isClientSide()) {
            return this.clientHoldsChisel();
        }
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (!this.level().isClientSide() && entity instanceof ServerPlayer sPlayer) {
            ItemStack held = sPlayer.getMainHandItem();
            if (this.isValidAttackableStack(held) && !this.getItem().isEmpty()) {
                this.onAttack(sPlayer, held);
                this.playOnHitSound();
            }
        }
        return true;
    }

    public void playOnHitSound() {
        ServerSoundHelper.playSoundAround(SoundsAS.CHISEL_HIT, this.level(), this.position(),
                0.6F, 0.85F + random.nextFloat() * 0.3F);
    }

    public abstract void onAttack(ServerPlayer sPlayer, ItemStack chisel);

    @OnlyIn(Dist.CLIENT)
    private boolean clientHoldsChisel() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return this.isValidAttackableStack(player.getMainHandItem());
    }

    protected boolean isValidAttackableStack(ItemStack stack) {
        return stack.is(ItemsAS.CHISEL);
    }

    @Override
    public void tick() {
        boolean onGround = this.onGround();
        super.tick();
        if (this.onGround() != onGround) {
            this.refreshDimensions();
        }
    }

    @Override
    public void setOnGround(boolean onGround) {
        boolean updateSize = this.onGround() != onGround;
        super.setOnGround(onGround);
        if (updateSize) {
            this.refreshDimensions();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (!this.onGround()) {
            return EntityType.ITEM.getDimensions();
        }
        return this.getType().getDimensions();
    }
}

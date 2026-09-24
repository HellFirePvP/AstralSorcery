/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.item.base.InterceptInteractItem;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.linking.LinkResult;
import hellfirepvp.astralsorcery.common.linking.Linkable;
import hellfirepvp.astralsorcery.common.linking.session.ActiveLinkSession;
import hellfirepvp.astralsorcery.common.linking.session.LinkSessionHelper;
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkLevelHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.LogicalSide;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinkingToolItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LinkingToolItem extends ItemCustom implements InterceptInteractItem.Block, InterceptInteractItem.Entity<LivingEntity> {

    public LinkingToolItem() {
        super(new Properties()
                .stacksTo(1));
    }

    @Override
    public boolean shouldInterceptBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult, Direction blockFace) {
        return true;
    }

    @Override
    public boolean doBlockInteract(LogicalSide side, Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitResult, Direction blockFace) {
        if (side.isServer() && player instanceof ServerPlayer sPlayer) {
            ServerLevel sLevel = sPlayer.serverLevel();
            ActiveLinkSession session = LinkSessionHelper.getActiveSession(sPlayer).orElse(null);
            if (session == null) {
                this.tryStartSession(sPlayer, sLevel, pos);
            } else {
                session.resolve(sLevel).ifPresent(linkable -> {
                    if (!linkable.isLinkedTo(pos)) {
                        LinkResult attemptLink = this.attemptOperation(action -> linkable.tryLinkTo(sLevel, pos, action));
                        if (!attemptLink.isSuccess()) {
                            attemptLink.getErrorMessage().ifPresent(sPlayer::sendSystemMessage);
                            return;
                        }
                        linkable.updateLinkStateChange(sLevel, pos, true);
                        LinkSessionHelper.updateLinkSession(sPlayer, linkable, session);

                        MutableComponent name = MiscUtil.getBlockStateDisplayName(sLevel, sPlayer, pos, hitResult);
                        sPlayer.sendSystemMessage(Component.translatable("message.astralsorcery.linking.link",
                                linkable.getDisplayName(sLevel), name).withStyle(ChatFormatting.GREEN));
                    } else {
                        LinkResult attemptUnlink = this.attemptOperation(action -> linkable.tryUnlinkFrom(sLevel, pos, action));
                        if (!attemptUnlink.isSuccess()) {
                            attemptUnlink.getErrorMessage().ifPresent(sPlayer::sendSystemMessage);
                            return;
                        }
                        linkable.updateLinkStateChange(sLevel, pos, false);
                        LinkSessionHelper.updateLinkSession(sPlayer, linkable, session);

                        MutableComponent name = MiscUtil.getBlockStateDisplayName(sLevel, sPlayer, pos, hitResult);
                        sPlayer.sendSystemMessage(Component.translatable("message.astralsorcery.linking.unlink",
                                name, linkable.getDisplayName(sLevel)).withStyle(ChatFormatting.GREEN));
                    }
                });
            }
        } else {
            player.swing(hand);
        }
        return true;
    }

    private void tryStartSession(ServerPlayer sPlayer, ServerLevel sLevel, BlockPos pos) {
        Block block = sLevel.getBlockState(pos).getBlock();
        if (block instanceof Linkable linkable) {
            LinkSessionHelper.startBlockSession(sPlayer, pos, MiscUtil.cast(linkable));
        } else {
            Linkable linkableTile = MiscUtil.getTileAt(sLevel, pos, Linkable.class, true).orElse(null);
            if (linkableTile != null) {
                LinkSessionHelper.startBlockEntitySession(sPlayer, MiscUtil.cast(linkableTile));
            } else {
                StarlightNetworkLevelHelper.get(sLevel).getNode(pos).ifPresent(node -> {
                    if (node instanceof Linkable linkNode) {
                        LinkSessionHelper.startNetworkNodeSession(sPlayer, MiscUtil.cast(linkNode));
                    }
                });
            }
        }
    }

    @Override
    public Class<LivingEntity> getEntityFilterClass() {
        return LivingEntity.class;
    }

    @Override
    public boolean shouldInterceptEntityInteract(LogicalSide side, Player player, InteractionHand hand, LivingEntity interacted) {
        return interacted.isAlive();
    }

    @Override
    public boolean doEntityInteract(LogicalSide side, Player player, InteractionHand hand, LivingEntity interacted) {
        if (side.isServer() && player instanceof ServerPlayer sPlayer) {
            ServerLevel sLevel = sPlayer.serverLevel();
            ActiveLinkSession session = LinkSessionHelper.getActiveSession(sPlayer).orElse(null);
            if (session == null) {
                if (interacted instanceof Linkable) {
                    LinkSessionHelper.startEntitySession(sPlayer, MiscUtil.cast(interacted));
                }
            } else {
                session.resolve(sLevel).ifPresent(linkable -> {
                    if (!linkable.isLinkedTo(interacted)) {
                        LinkResult attemptLink = this.attemptOperation(action -> linkable.tryLinkToEntity(sLevel, interacted, action));
                        if (!attemptLink.isSuccess()) {
                            attemptLink.getErrorMessage().ifPresent(sPlayer::sendSystemMessage);
                            return;
                        }
                        linkable.updateLinkStateChange(sLevel, interacted, true);
                        LinkSessionHelper.updateLinkSession(sPlayer, linkable, session);

                        sPlayer.sendSystemMessage(Component.translatable("message.astralsorcery.linking.link",
                                linkable.getDisplayName(sLevel), interacted.getDisplayName()).withStyle(ChatFormatting.GREEN));
                    } else {
                        LinkResult attemptUnlink = this.attemptOperation(action -> linkable.tryUnlinkFromEntity(sLevel, interacted, action));
                        if (!attemptUnlink.isSuccess()) {
                            attemptUnlink.getErrorMessage().ifPresent(sPlayer::sendSystemMessage);
                            return;
                        }
                        linkable.updateLinkStateChange(sLevel, interacted, false);
                        LinkSessionHelper.updateLinkSession(sPlayer, linkable, session);

                        sPlayer.sendSystemMessage(Component.translatable("message.astralsorcery.linking.unlink",
                                interacted.getDisplayName(), linkable.getDisplayName(sLevel)).withStyle(ChatFormatting.GREEN));
                    }
                });
            }
        } else {
            player.swing(hand);
        }
        return true;
    }

    private LinkResult attemptOperation(Function<Linkable.LinkAction, LinkResult> attempt) {
        LinkResult simulate = attempt.apply(Linkable.LinkAction.SIMULATE);
        if (!simulate.isSuccess()) {
            return simulate;
        }
        return attempt.apply(Linkable.LinkAction.EXECUTE);
    }
}

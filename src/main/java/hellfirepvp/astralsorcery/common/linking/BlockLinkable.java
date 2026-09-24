/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking;

import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionLevelHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockLinkable
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface BlockLinkable extends Linkable {

    //Data storage for block links, same for entities next method. Return empty optional to disable that functionality.
    default Optional<LinkContainer> getLinkDataContainer() {
        return Optional.empty();
    }

    default Optional<EntityLinkContainer> getEntityLinkDataContainer() {
        return Optional.empty();
    }

    BlockPos getLinkablePos();

    @Override
    default Component getDisplayName(Level level) {
        return level.getBlockState(this.getLinkablePos()).getBlock().getName();
    }

    @Override
    default boolean startLinking(Player player) {
        return this.getLinkDataContainer().isPresent() || this.getEntityLinkDataContainer().isPresent();
    }

    @Override
    default LinkResult tryLinkTo(Level level, BlockPos to, LinkAction action) {
        LinkResult canLink = this.canLinkTo(level, to);
        if (!canLink.isSuccess()) return canLink;
        return this.getLinkDataContainer()
                .map(ct -> ct.link(level, this.getLinkablePos(), to, action))
                .orElse(LinkResult.failureInvalid());
    }

    default LinkResult canLinkTo(Level level, BlockPos to) {
        if (this.getLinkablePos().equals(to)) return LinkResult.failureInvalid();
        if (this.getMaxBlockLinkDistance()
                .map(dist -> this.getLinkablePos().distSqr(to) > dist * dist)
                .orElse(false)) {
            return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.distance",
                    this.getMaxBlockLinkDistance().orElse(0)).withStyle(ChatFormatting.RED));
        }

        return this.getLinkDataContainer().map(ct -> {
            if (this.getMaxBlockLinkCount()
                    .map(count -> ct.getLinkedToCount() >= count)
                    .orElse(false)) {
                int maxCount = this.getMaxBlockLinkCount().orElse(0);
                String key = maxCount == 1 ? "message.astralsorcery.linking.failure.max_count" :
                        "message.astralsorcery.linking.failure.max_count.plural";
                Component cmpFrom = this.getDisplayName(level);
                return LinkResult.failure(Component.translatable(key, cmpFrom, maxCount).withStyle(ChatFormatting.RED));
            }
            return ct.canLink(level, this.getLinkablePos(), to);
        }).orElse(LinkResult.failureInvalid());
    }

    @Override
    default LinkResult tryUnlinkFrom(Level level, BlockPos to, LinkAction action) {
        return this.getLinkDataContainer()
                .map(ct -> ct.unlink(level, this.getLinkablePos(), to, action))
                .orElse(LinkResult.failureInvalid());
    }

    @Override
    default LinkResult tryLinkToEntity(Level level, Entity entity, LinkAction action) {
        return this.getEntityLinkDataContainer().map(ct -> {
            LinkResult canLink = ct.canLink(level, this.getLinkablePos(), entity);
            if (!canLink.isSuccess()) return canLink;
            return ct.link(level, this.getLinkablePos(), entity, action);
        }).orElse(LinkResult.failureInvalid());
    }

    @Override
    default LinkResult tryUnlinkFromEntity(Level level, Entity entity, LinkAction action) {
        return this.getEntityLinkDataContainer()
                .map(ct -> ct.unlink(level, this.getLinkablePos(), entity, action))
                .orElse(LinkResult.failureInvalid());
    }

    default Collection<BlockLinkConnection> getLinkedPositions() {
        return this.getLinkDataContainer().map(LinkContainer::getLinkedTo).orElse(Collections.emptyList());
    }

    @Override
    default Collection<EntityLinkConnection> getLinkedEntities() {
        return this.getEntityLinkDataContainer().map(EntityLinkContainer::getLinkedTo).orElse(Collections.emptyList());
    }
}

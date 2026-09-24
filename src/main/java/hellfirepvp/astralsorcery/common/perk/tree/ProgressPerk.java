/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ProgressPerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ProgressPerk<D extends AbstractPerk.Data> extends AbstractPerk<D> {

    protected static <T extends ProgressPerk<?>> Products.P6<RecordCodecBuilder.Mu<T>, ResourceLocation, String, Float, Float, PerkCategory, Set<PerkRequirement>> progressFields(RecordCodecBuilder.Instance<T> instance) {
        return perkFields(instance).and(
                SetCodec.of(PerkRequirement.CODEC).fieldOf("requirements").forGetter(ProgressPerk::getRequirements)
        );
    }

    private Predicate<PlayerProgress> unlockFunction = progress -> true;

    private final Set<PerkRequirement> requirements = new HashSet<>();

    protected ProgressPerk(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements) {
        super(key, nameKey, x, y, category);
        this.requirements.addAll(requirements);
        this.requirements.forEach(req -> {
            this.unlockFunction = this.unlockFunction.and(req.createProgressCondition());
        });
        if (!this.requirements.isEmpty()) {
            this.disableTooltipCaching();
        }
    }

    public <P extends ProgressPerk<?>> P addRequirement(PerkRequirement requirement) {
        AstralSorcery.assertDataGeneration();
        if (this.requirements.add(requirement)) {
            this.unlockFunction = this.unlockFunction.and(requirement.createProgressCondition());
            this.disableTooltipCaching();
        }
        return MiscUtil.cast(this);
    }

    protected Set<PerkRequirement> getRequirements() {
        return Collections.unmodifiableSet(this.requirements);
    }

    @Override
    protected boolean addTooltip(Collection<MutableComponent> tooltip, PlayerProgress progress, @Nullable Player player, LogicalSide side) {
        if (!this.canSee(progress)) {
            tooltip.add(getInfoText("missing_progress").withStyle(ChatFormatting.RED));
            return true;
        }
        return super.addTooltip(tooltip, progress, player, side);
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, Player player) {
        if (!this.canSee(progress)) return false;
        return super.mayUnlockPerk(progress, player);
    }

    public final boolean canSee(Player player, LogicalSide side) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.isValid()) {
            return this.canSee(prog);
        }
        return false;
    }

    public final boolean canSee(PlayerProgress progress) {
        return this.unlockFunction.test(progress);
    }
}

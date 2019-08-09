/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProgressGatedPerk
 * Created by HellFirePvP
 * Date: 10.07.2019 / 21:38
 */
public class ProgressGatedPerk extends AbstractPerk {

    private BiFunction<PlayerEntity, PlayerProgress, Boolean> unlockFunction = (player, progress) -> true;

    public ProgressGatedPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    public void setRequireDiscoveredConstellation(IConstellation cst) {
        addResearchPreRequisite((player, progress) -> progress.hasConstellationDiscovered(cst));
    }

    public void addRequireProgress(ResearchProgression progression) {
        addResearchPreRequisite(((player, progress) -> progress.getResearchProgression().contains(progression)));
    }

    public void addRequireTier(ProgressionTier tier) {
        addResearchPreRequisite(((player, progress) -> progress.getTierReached().isThisLaterOrEqual(tier)));
    }

    public void addResearchPreRequisite(BiFunction<PlayerEntity, PlayerProgress, Boolean> unlockFunction) {
        BiFunction<PlayerEntity, PlayerProgress, Boolean> prev = this.unlockFunction;
        this.unlockFunction = (player, progress) -> prev.apply(player, progress) && unlockFunction.apply(player, progress);
        disableTooltipCaching(); //Cannot cache as it may change.
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        if (!canSee(player, progress)) {
            return false;
        }
        return super.mayUnlockPerk(progress, player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        if (!canSeeClient()) {
            tooltip.add(TextFormatting.RED + I18n.format("perk.info.missing_progress"));
            return false;
        }
        return super.addLocalizedTooltip(tooltip);
    }

    @OnlyIn(Dist.CLIENT)
    public final boolean canSeeClient() {
        return canSee(Minecraft.getInstance().player, Dist.CLIENT);
    }

    public final boolean canSee(PlayerEntity player, Dist side) {
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.isValid()) {
            return canSee(player, prog);
        }
        return false;
    }

    public final boolean canSee(PlayerEntity player, PlayerProgress progress) {
        return unlockFunction.apply(player, progress);
    }

    @Override
    protected void applyPerkLogic(PlayerEntity player, Dist side) {}

    @Override
    protected void removePerkLogic(PlayerEntity player, Dist side) {}

}
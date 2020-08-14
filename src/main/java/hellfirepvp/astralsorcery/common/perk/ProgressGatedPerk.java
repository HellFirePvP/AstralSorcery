/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProgressGatedPerk
 * Created by HellFirePvP
 * Date: 10.07.2019 / 21:38
 */
public class ProgressGatedPerk extends AbstractPerk {

    private BiPredicate<PlayerEntity, PlayerProgress> unlockFunction = (player, progress) -> true;

    private List<IConstellation> neededConstellations = new ArrayList<>();
    private List<ResearchProgression> neededResearch = new ArrayList<>();
    private List<ProgressionTier> neededProgression = new ArrayList<>();

    public ProgressGatedPerk(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    public void addRequireConstellation(IConstellation cst) {
        addResearchPreRequisite((player, progress) -> progress.hasConstellationDiscovered(cst));
        this.neededConstellations.add(cst);
    }

    public void addRequireProgress(ResearchProgression research) {
        addResearchPreRequisite(((player, progress) -> progress.getResearchProgression().contains(research)));
        this.neededResearch.add(research);
    }

    public void addRequireTier(ProgressionTier tier) {
        addResearchPreRequisite(((player, progress) -> progress.getTierReached().isThisLaterOrEqual(tier)));
        this.neededProgression.add(tier);
    }

    public void addResearchPreRequisite(BiPredicate<PlayerEntity, PlayerProgress> unlockFunction) {
        this.unlockFunction = this.unlockFunction.and(unlockFunction);
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
    public boolean addLocalizedTooltip(Collection<ITextComponent> tooltip) {
        if (!canSeeClient()) {
            tooltip.add(new TranslationTextComponent("perk.info.astralsorcery.missing_progress")
                    .applyTextStyle(TextFormatting.RED));
            return false;
        }
        return super.addLocalizedTooltip(tooltip);
    }

    @OnlyIn(Dist.CLIENT)
    public final boolean canSeeClient() {
        return canSee(Minecraft.getInstance().player, LogicalSide.CLIENT);
    }

    public final boolean canSee(PlayerEntity player, LogicalSide side) {
        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        if (prog.isValid()) {
            return this.canSee(player, prog);
        }
        return false;
    }

    public final boolean canSee(PlayerEntity player, PlayerProgress progress) {
        return this.unlockFunction.test(player, progress);
    }

    @Override
    public void deserializeData(JsonObject perkData) {
        super.deserializeData(perkData);

        this.neededConstellations.clear();
        this.neededResearch.clear();
        this.neededProgression.clear();

        if (JSONUtils.hasField(perkData, "neededConstellations")) {
            JsonArray array = JSONUtils.getJsonArray(perkData, "neededConstellations");
            for (int i = 0; i < array.size(); i++) {
                JsonElement el = array.get(i);
                String key = JSONUtils.getString(el, String.format("neededConstellations[%s]", i));
                IConstellation cst = ConstellationRegistry.getConstellation(new ResourceLocation(key));
                if (cst == null) {
                    throw new JsonParseException("Unknown constellation: " + key);
                }
                this.addRequireConstellation(cst);
            }
        }

        if (JSONUtils.hasField(perkData, "neededResearch")) {
            JsonArray array = JSONUtils.getJsonArray(perkData, "neededResearch");
            for (int i = 0; i < array.size(); i++) {
                JsonElement el = array.get(i);
                String key = JSONUtils.getString(el, String.format("neededResearch[%s]", i));
                try {
                    this.addRequireProgress(ResearchProgression.valueOf(key));
                } catch (Exception exc) {
                    throw new JsonParseException("Unknown research: " + key);
                }
            }
        }

        if (JSONUtils.hasField(perkData, "neededProgression")) {
            JsonArray array = JSONUtils.getJsonArray(perkData, "neededProgression");
            for (int i = 0; i < array.size(); i++) {
                JsonElement el = array.get(i);
                String key = JSONUtils.getString(el, String.format("neededProgression[%s]", i));
                try {
                    this.addRequireTier(ProgressionTier.valueOf(key));
                } catch (Exception exc) {
                    throw new JsonParseException("Unknown progress: " + key);
                }
            }
        }
    }

    @Override
    public void serializeData(JsonObject perkData) {
        super.serializeData(perkData);

        if (!this.neededConstellations.isEmpty()) {
            JsonArray array = new JsonArray();
            for (IConstellation cst : this.neededConstellations) {
                array.add(cst.getRegistryName().toString());
            }
            perkData.add("neededConstellations", array);
        }

        if (!this.neededResearch.isEmpty()) {
            JsonArray array = new JsonArray();
            for (ResearchProgression research : this.neededResearch) {
                array.add(research.name());
            }
            perkData.add("neededResearch", array);
        }

        if (!this.neededProgression.isEmpty()) {
            JsonArray array = new JsonArray();
            for (ProgressionTier progress : this.neededProgression) {
                array.add(progress.name());
            }
            perkData.add("neededProgression", array);
        }
    }
}
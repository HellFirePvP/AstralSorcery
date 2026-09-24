/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.data;

import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeCondition;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchNodeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchNodeBuilder {

    private final ResourceLocation key;
    private final ResearchTier tier;
    private final float posX, posY;
    private TextureQuery backgroundTexture = new TextureQuery(AssetLocation.SCREEN, "tome", "research_frame_wood");
    private final List<ItemStack> display = new ArrayList<>();
    private final List<TomePage> pages = new ArrayList<>();
    private final List<ResourceLocation> connectedNodes = new ArrayList<>();
    private final List<ResearchNodeCondition> conditions = new ArrayList<>();
    private final List<Item> lookupIndexItems = new ArrayList<>();

    private ResearchNodeBuilder(ResourceLocation key, ResearchTier tier, float posX, float posY) {
        this.key = key;
        this.tier = tier;
        this.posX = posX;
        this.posY = posY;
    }

    public static ResearchNodeBuilder create(ResourceLocation key, ResearchTier tier, float posX, float posY) {
        return new ResearchNodeBuilder(key, tier, posX, posY);
    }

    public ResearchNodeBuilder setBackgroundTexture(TextureQuery backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public ResearchNodeBuilder addDisplayItem(ItemLike... items) {
        Arrays.stream(items).map(ItemStack::new).forEach(this.display::add);
        return this;
    }

    public ResearchNodeBuilder addDisplayItem(ItemStack... stacks) {
        return this.addDisplayItem(Arrays.asList(stacks));
    }

    public ResearchNodeBuilder addDisplayItem(List<ItemStack> stacks) {
        this.display.addAll(stacks);
        return this;
    }

    public ResearchNodeBuilder addPage(TomePage page) {
        this.pages.add(page);
        return this;
    }

    public ResearchNodeBuilder addConnectedNode(ResourceLocation key) {
        this.connectedNodes.add(key);
        return this;
    }

    public ResearchNodeBuilder addConnectedNode(ResearchNode node) {
        this.connectedNodes.add(node.getKey());
        return this;
    }

    public ResearchNodeBuilder addCondition(ResearchNodeCondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public ResearchNodeBuilder addLookupIndexItem(ItemLike... items) {
        this.lookupIndexItems.addAll(Arrays.stream(items).map(ItemLike::asItem).toList());
        return this;
    }

    public ResearchNode build(Consumer<ResearchNode> consumerIn) {
        ResearchNode node = new ResearchNode(
                this.key, this.tier, this.posX, this.posY,
                this.backgroundTexture, this.display,
                this.pages, this.connectedNodes, this.conditions, this.lookupIndexItems
        );
        consumerIn.accept(node);
        return node;
    }
}

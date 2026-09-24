/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeCondition;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeVisibility;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchNode {

    public static final Codec<ResearchNode> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("key").forGetter(ResearchNode::getKey),
            ResearchTier.CODEC.fieldOf("tier").forGetter(ResearchNode::getTier),
            Codec.FLOAT.fieldOf("posX").forGetter(ResearchNode::getPosX),
            Codec.FLOAT.fieldOf("posY").forGetter(ResearchNode::getPosY),
            TextureQuery.CODEC.fieldOf("backgroundTexture").forGetter(ResearchNode::getBackgroundTexture),
            ItemStack.CODEC.listOf().fieldOf("renderItemStacks").forGetter(ResearchNode::getRenderItemStacks),
            TomePage.CODEC.listOf().fieldOf("pages").forGetter(ResearchNode::getPages),
            ResourceLocation.CODEC.listOf().fieldOf("connections").forGetter(ResearchNode::getConnections),
            ResearchNodeCondition.CODEC.listOf().fieldOf("conditions").forGetter(ResearchNode::getConditions),
            BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("lookupIndexItems").forGetter(ResearchNode::getLookupIndexItems)
    ).apply(inst, ResearchNode::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchNode> SYNC_CODEC = StreamCodec.of(ResearchNode::write, ResearchNode::read);

    private final ResourceLocation key;
    private final ResearchTier tier;
    private final float posX, posY;
    private final TextureQuery backgroundTexture;
    private final List<ItemStack> renderItemStacks;
    private final List<TomePage> pages = new ArrayList<>();
    private final List<ResourceLocation> connections = new ArrayList<>();
    private final List<ResearchNodeCondition> conditions = new ArrayList<>();
    private final List<Item> lookupIndexItems = new ArrayList<>();

    private String cacheDescriptionId;

    public ResearchNode(ResourceLocation key,
                        ResearchTier tier,
                        float posX,
                        float posY,
                        TextureQuery backgroundTexture,
                        List<ItemStack> renderItemStacks,
                        List<TomePage> pages,
                        List<ResourceLocation> connections,
                        List<ResearchNodeCondition> conditions,
                        List<Item> lookupIndexItems) {
        this.key = key;
        this.tier = tier;
        this.posX = posX;
        this.posY = posY;
        this.backgroundTexture = backgroundTexture;
        this.renderItemStacks = renderItemStacks;
        this.pages.addAll(pages);
        this.connections.addAll(connections);
        this.conditions.addAll(conditions);
        this.lookupIndexItems.addAll(lookupIndexItems);
    }

    public final ResourceLocation getKey() {
        return this.key;
    }

    public ResearchTier getTier() {
        return this.tier;
    }

    public float getPosX() {
        return this.posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public TextureQuery getBackgroundTexture() {
        return this.backgroundTexture;
    }

    public ResearchNode addConnection(ResearchNode node) {
        return this.addConnection(node.getKey());
    }

    public ResearchNode addConnection(ResourceLocation nodeKey) {
        AstralSorcery.assertDataGeneration();

        this.connections.add(nodeKey);
        return this;
    }

    public ResearchNodeVisibility getVisibility(PlayerProgress progress) {
        if (!progress.getTierReached().isThisLaterOrEqual(this.getTier())) {
            return ResearchNodeVisibility.hidden();
        }

        ResearchNodeVisibility.Type visibilityType = ResearchNodeVisibility.Type.VISIBLE;
        List<Component> conditionDescriptions = new ArrayList<>();
        for (ResearchNodeCondition condition : this.conditions) {
            if (!condition.canSee(this, progress)) {
                ResearchNodeVisibility.Type suggested = condition.getSuggestedVisibility(this, progress);
                if (suggested.ordinal() > visibilityType.ordinal()) visibilityType = suggested;
                conditionDescriptions.addAll(condition.getConditionDescription(this, progress));
            }
        }
        return new ResearchNodeVisibility(visibilityType, conditionDescriptions);
    }

    public ItemStack getRenderItemStack(long tick) {
        return this.renderItemStacks.get((int) ((tick / 40) % this.renderItemStacks.size()));
    }

    public List<ItemStack> getRenderItemStacks() {
        return Collections.unmodifiableList(this.renderItemStacks);
    }

    public List<TomePage> getPages() {
        return Collections.unmodifiableList(this.pages);
    }

    public List<ResourceLocation> getConnections() {
        return Collections.unmodifiableList(this.connections);
    }

    public List<ResearchNodeCondition> getConditions() {
        return Collections.unmodifiableList(this.conditions);
    }

    public List<Item> getLookupIndexItems() {
        return Collections.unmodifiableList(this.lookupIndexItems);
    }

    public Component getName() {
        if (this.cacheDescriptionId == null) {
            this.cacheDescriptionId = Util.makeDescriptionId("tome.research.node", this.getKey());
        }
        return Component.translatable(this.cacheDescriptionId);
    }

    private static void write(RegistryFriendlyByteBuf buf, ResearchNode node) {
        buf.writeResourceLocation(node.key);
        buf.writeEnum(node.tier);
        buf.writeFloat(node.posX);
        buf.writeFloat(node.posY);
        TextureQuery.STREAM_CODEC.encode(buf, node.backgroundTexture);
        ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, node.renderItemStacks);
        TomePage.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, node.pages);
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, node.connections);
        ResearchNodeCondition.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, node.conditions);
        ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list()).encode(buf, node.lookupIndexItems);
    }

    private static ResearchNode read(RegistryFriendlyByteBuf buf) {
        ResourceLocation key = buf.readResourceLocation();
        ResearchTier tier = buf.readEnum(ResearchTier.class);
        float posX = buf.readFloat();
        float posY = buf.readFloat();
        TextureQuery backgroundTexture = TextureQuery.STREAM_CODEC.decode(buf);
        List<ItemStack> stacks = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        List<TomePage> pages = TomePage.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        List<ResourceLocation> connections = ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        List<ResearchNodeCondition> conditions = ResearchNodeCondition.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        List<Item> lookupIndexItems = ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list()).decode(buf);
        return new ResearchNode(key, tier, posX, posY, backgroundTexture, stacks, pages, connections, conditions, lookupIndexItems);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResearchNode that = (ResearchNode) o;
        return this.key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}

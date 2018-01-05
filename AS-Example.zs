// Adds a new Infusion recipe to the starlight infusion
// Parameters:
// InputStack, OutputStack, consumeMultiple (true/false), consumptionChance, craftTickTime
// Example:
// mods.astralsorcery.StarlightInfusion.addInfusion(<astralsorcery:itemjournal>, <minecraft:bow>, false, 0.7, 200);

// Removes the first Infusion that matches the described ItemStack.
// Removes only the first it finds. Add multiple removeInfusion-calls to remove multiples, if present.
// Parameters:
// OutputStack
// Example:
// mods.astralsorcery.StarlightInfusion.removeInfusion(<minecraft:ice>);

// Adds a Ore that's possible to be spawned via the Mineralis ritual.
// Adds it via oreDict. To make it work, be sure the oreDict name has at least 1 ore *Block*
// Already-registered oreDict entries can be found here:
//    https://github.com/HellFirePvP/AstralSorcery/blob/master/src/main/java/hellfirepvp/astralsorcery/common/base/OreTypes.java#L35-L58
// GregTech ores are blacklisted for stability reasons.
// Parameters:
// OreDictName, spawnWeight
// Example:
// mods.astralsorcery.RitualMineralis.addOre("blockMarble", 6000);

// Removes an oreDict entry from the list of ores the mineralis ritual can possibly spawn.
// Parameters:
// OreDictName
// Example:
// mods.astralsorcery.RitualMineralis.removeOre("oreCoal");

// Adds a light transmutation recipe
// Each input & output item specified *has to have* a block representation or the recipe will be skipped.
// Metadata of the itemstacks is used to determine the blockstate.
// Parameters:
// InputBlock (as itemstack), OutputBlock (as itemstack), chargeUsed (until transmutation is complete)
// Example:
// mods.astralsorcery.LightTransmutation.addTransmutation(<minecraft:grass>, <minecraft:gold_ore>, 10);

// Removes a light transmutation recipe
// The inputstack can be an item or block, depending on that it'll try and find a recipe that matches.
// Removes only the first it finds. Add multiple removeTransmutation-calls to remove multiples, if present.
// Parameters:
// matchStack (itemstack that is matched against the output of the transmutation), true/false (respect metadata in the check, false ignores metadata and only looks for a block match)
// Example:
// mods.astralsorcery.LightTransmutation.removeTransmutation(<minecraft:end_stone>, false);

// Removes a lightwell recipe
// Tries to find the recipe to remove via the given ItemStack and optionally the fluid together with it.
// The fluid-parameter can be 'null' only search for the recipe to remove via the given itemstack.
// Removes only the first it finds. Add multiple removeLiquefaction-calls to remove multiples, if present.
// Parameters:
// matchStack (itemstack that is matched against the input of the liquefaction), liquidStack (as additional search parameter *or null* to only search via itemstack-input)
// Example:
// mods.astralsorcery.Lightwell.removeLiquefaction(<astralsorcery:itemcraftingcomponent:0>, null);

// Adds a lightwell recipe
// Only the fluid from the output-liquidstack counts, the amount will (like the default lightwell depend on day/night and stuff too..)
// outputMulitplier: Multiplier that is applied together with the collected starlight to calculate the output fluid amount. Usually 0.3 - 1.2 (aka: don't write like 200 here, if you want to stay reasonable :P)
// shatterMultiplier: The higher this multiplier, the lower the chance per-tick that the catalyst item will shatter.
// colorHEX: The colorcode used for the particles around the hovering item.
// Parameters:
// Input-ItemStack, Output-LiqudStack, (float) outputMulitplier, (float) shatterMultiplier, (int) colorHEX (use a hex-color picker or something if you don't know how to specify that manually)
// Example:
// mods.astralsorcery.Lightwell.addLiquefaction(<minecraft:dirt>, <liquid:water>, 1, 0.2, 0);

// Adds a grindstone recipe
// The 'Input' can be both an itemstack or a oredict name!
// Parameters:
// Input, Output-ItemStack
// Example:
// mods.astralsorcery.Grindstone.addRecipe(<minecraft:stone>, <minecraft:gravel>);

// Removes a single grindstone recipes
// Only 1 recipe will be removed, the first one matching the output-stack specified.
// Parameters:
// Output-ItemStack
// Example:
// mods.astralsorcery.Grindstone.removeReipce(<minecraft:redstone>);

// Removes a recipe from the altar recipe registry.
// Removes only the first it finds. Add multiple removeAltarRecipe-calls to remove multiples, if present.
// Parameters:
// OutputStack, AltarLevel to remove the recipe from (0=Luminous CrafingTable, 1=Starlight Crafting Altar, 2=Celestial Altar)
// Example:
// mods.astralsorcery.Altar.removeAltarRecipe(<astralsorcery:blockblackmarble>, 0);


// Adds a recipe to the discovery/tier1 altar recipes
// This cannot be shapeless.
// Can accept an ItemStack, OreDicitionary, LiquidStack or null in any slot.
// Formats (just as reminder): (ItemStack should be known), OreDicitionary: <ore:OreDictName>, Liquid: <liquid:LiquidName>
// Output has to be an ItemStack.
// Inputs have to have at least 1 non-null input.
// Arrays are indexed as follows:
//
// [ 0] [ 1] [ 2] 
// [ 3] [ 4] [ 5] 
// [ 6] [ 7] [ 7] 
//
// Parameters:
// OutputStack, (int) starlightRequired, (int) craftTickTime, Inputs-Array (*has* to be 9 elements)
// Example:
// mods.astralsorcery.Altar.addDiscoveryAltarRecipe(<minecraft:dirt>, 200, 200, [
// <minecraft:grass>, null, <ore:treeLeaves>,
// null, <minecraft:grass>, null,
// <liquid:astralsorcery.liquidstarlight>, null, <ore:treeLeaves>
// ]);


//Adds a recipe to the attunement/tier2 altar recipes
// This cannot be shapeless.
// Can accept an ItemStack, OreDicitionary, LiquidStack or null in any slot.
// Formats (just as reminder): (ItemStack should be known), OreDicitionary: <ore:OreDictName>, Liquid: <liquid:LiquidName>
// Output has to be an ItemStack.
// Inputs have to have at least 1 non-null input.
// Arrays are indexed as follows:
//
// [ 9]                [10] 
//      [ 0] [ 1] [ 2] 
//      [ 3] [ 4] [ 5] 
//      [ 6] [ 7] [ 8]
// [11]                [12] 
//
// Parameters:
// OutputStack, (int) starlightRequired, (int) craftTickTime, Inputs-Array (*has* to be 13 elements)
// Example:
// mods.astralsorcery.Altar.addAttunmentAltarRecipe(<minecraft:dirt>, 500, 300, [
// null, null, null,
// <ore:treeLeaves>, <astralsorcery:blockmarble:2>, <ore:treeLeaves>,
// null, <liquid:astralsorcery.liquidstarlight>, null,
// <ore:blockMarble>, <ore:blockMarble>, <ore:blockMarble>, <ore:blockMarble>
// ]);


//Adds a recipe to the constellation/tier3 altar recipes
// This cannot be shapeless.
// Can accept an ItemStack, OreDicitionary, LiquidStack or null in any slot.
// Formats (just as reminder): (ItemStack should be known), OreDicitionary: <ore:OreDictName>, Liquid: <liquid:LiquidName>
// Output has to be an ItemStack.
// Inputs have to have at least 1 non-null input.
// Arrays are indexed as follows:
//
// [ 9] [13]      [14] [10] 
// [15] [ 0] [ 1] [ 2] [16] 
//      [ 3] [ 4] [ 5] 
// [17] [ 6] [ 7] [ 8] [18] 
// [11] [19]      [20] [12] 
//
// Parameters:
// OutputStack, (int) starlightRequired, (int) craftTickTime, Inputs-Array (*has* to be 21 elements)
// Example:
// mods.astralsorcery.Altar.addConstellationAltarRecipe(<astralsorcery:itemcraftingcomponent:2>, 2000, 10, [
// <ore:blockMarble>, <astralsorcery:blocklens>, <ore:blockMarble>,
// <ore:blockMarble>, <astralsorcery:itemcraftingcomponent:2>, <ore:blockMarble>,
// <ore:blockMarble>, <minecraft:nether_star>, <ore:blockMarble>,
// null, null, <liquid:astralsorcery.liquidstarlight>, <liquid:astralsorcery.liquidstarlight>,
// <ore:blockMarble>, <ore:blockMarble>,
// <minecraft:nether_star>, <minecraft:nether_star>,
// <minecraft:nether_star>, <minecraft:nether_star>,
// <ore:blockMarble>, <ore:blockMarble>
// ]);








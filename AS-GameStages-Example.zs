// Sets the max-level a player can reach on the perk tree to the given level if the player has the game stage.
// Special cases:
// If the player has multiple stages that have level caps, the highest allowed cap will count.
// If the player has no stage that has a level cap, the highest allowed cap will be the configured total level cap.
mods.astralsorcery.GameStages.addLevelCap("someGameStage", 5);

// Sets the constellation to require the game stage to be discoverable or traceable.
// Special cases:
// If the player has no (relevant) stage, but the constellation requires stages, the player cannot trace/discover it.
// If the player has no (relevant) stage and the constellation doesn't require any stage, the player can trace or discover it.
// If the player has no (relevant) stage, but receives knowledge directly through knowledge-sharing paper, the gamestage will not prevent that from happening.
// If the player already discovered or traced the constellation, but doesn't have the game stage (anymore), the player will not lose the knowledge of that constellation.
mods.astralsorcery.GameStages.addConstellationDiscoveryStage("someGameStage", "astralsorcery.constellation.aevitas");

// If the constellation has multiple stages that "unlock" it, having one of them allows the player to discover or trace the constellation.
mods.astralsorcery.GameStages.addConstellationDiscoveryStage("anotherGameStage", "astralsorcery.constellation.aevitas");
mods.astralsorcery.GameStages.addConstellationDiscoveryStage("yetAnotherGameStage", "astralsorcery.constellation.aevitas");
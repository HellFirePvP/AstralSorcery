function initializeCoreMod() {
    return {
        'post_process_vanilla': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.item.ItemStack',
                'methodName': 'getTooltip',
                'methodDesc': '(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'add_missing_tag_enchantment_tooltip\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var methodEnchantmentTagList = ASMAPI.findFirstMethodCall(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/item/ItemStack',
                        'getEnchantmentTagList',
                        '()Lnet/minecraft/nbt/ListNBT;');

                var methodHasTag = ASMAPI.findFirstMethodCallBefore(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/item/ItemStack',
                        'hasTag',
                        '()Z',
                        method.instructions.indexOf(methodEnchantmentTagList));

                var prev = methodHasTag.getPrevious();
                method.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 0)); //thisStack
                method.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 3)); //tooltipList

                method.instructions.insertBefore(prev, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'addNoTagTooltip',
                    '(Lnet/minecraft/item/ItemStack;Ljava/util/List;)V',
                    ASMAPI.MethodType.STATIC));

                print('[AstralSorcery] Added \'add_missing_tag_enchantment_tooltip\' ASM patch!');
                return method;
            }
        }
    }
}
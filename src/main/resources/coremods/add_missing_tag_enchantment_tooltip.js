function initializeCoreMod() {
    return {
        'add_missing_tag_enchantment_tooltip': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.item.ItemStack',
                'methodName': 'func_82840_a',
                'methodDesc': '(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'add_missing_tag_enchantment_tooltip\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var callGetEnchantmentTagList = ASMAPI.mapMethod('func_77986_q');
                var callHasTag = ASMAPI.mapMethod('func_77942_o');

                var methodEnchantmentTagList = ASMAPI.findFirstMethodCall(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/item/ItemStack',
                        callGetEnchantmentTagList,
                        '()Lnet/minecraft/nbt/ListNBT;');

                var methodHasTag = ASMAPI.findFirstMethodCallBefore(method,
                        ASMAPI.MethodType.VIRTUAL,
                        'net/minecraft/item/ItemStack',
                        callHasTag,
                        '()Z',
                        method.instructions.indexOf(methodEnchantmentTagList));

                var prev = methodHasTag.getPrevious();
                method.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 0)); //thisStack
                method.instructions.insertBefore(prev, new VarInsnNode(Opcodes.ALOAD, 3)); //tooltipList

                method.instructions.insertBefore(prev, ASMAPI.buildMethodCall(
                    'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                    'addTooltipPreEnchantments',
                    '(Lnet/minecraft/item/ItemStack;Ljava/util/List;)V',
                    ASMAPI.MethodType.STATIC));

                ASMAPI.log('INFO', 'Added \'add_missing_tag_enchantment_tooltip\' ASM patch!');
                return method;
            }
        }
    }
}
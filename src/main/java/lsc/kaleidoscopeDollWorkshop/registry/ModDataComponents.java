package lsc.kaleidoscopeDollWorkshop.registry;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, KaleidoscopeDollWorkshop.MOD_ID);

    // 注册 "doll_owner" 组件
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResolvableProfile>> DOLL_OWNER =
            register("doll_owner", builder -> builder.persistent(ResolvableProfile.CODEC).networkSynchronized(ResolvableProfile.STREAM_CODEC));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }
}
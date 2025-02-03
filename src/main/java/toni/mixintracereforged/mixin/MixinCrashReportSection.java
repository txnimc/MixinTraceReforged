package toni.mixintracereforged.mixin;

import net.minecraft.CrashReportCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.mixintracereforged.MixinTraceReforged;

@Mixin(value = CrashReportCategory.class)
public abstract class MixinCrashReportSection {
    @Shadow
    private StackTraceElement[] stackTrace;

    @Inject(method = "getDetails", at = @At("TAIL"))
    private void addTrace(StringBuilder builder, CallbackInfo ci) {
        MixinTraceReforged.printTrace(stackTrace, builder);
    }
}
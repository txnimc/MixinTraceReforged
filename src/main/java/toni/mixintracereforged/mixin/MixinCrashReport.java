package toni.mixintracereforged.mixin;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.mixintracereforged.MixinTraceReforged;
import net.minecraft.CrashReport;

@Mixin(value = CrashReport.class)
public abstract class MixinCrashReport {
    @Shadow
    private StackTraceElement[] uncategorizedStackTrace;

    @Inject(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/CrashReport;details:Ljava/util/List;"))
    private void addTrace(StringBuilder builder, CallbackInfo ci) {
        int trailingNewlineCount = 0;
        if (builder.charAt(builder.length() - 1) == '\n') {
            builder.deleteCharAt(builder.length() - 1);
            trailingNewlineCount++;
        }
        if (builder.charAt(builder.length() - 1) == '\n') {
            builder.deleteCharAt(builder.length() - 1);
            trailingNewlineCount++;
        }
        MixinTraceReforged.printTrace(uncategorizedStackTrace, builder);
        builder.append(StringUtils.repeat("\n", trailingNewlineCount));
    }
}
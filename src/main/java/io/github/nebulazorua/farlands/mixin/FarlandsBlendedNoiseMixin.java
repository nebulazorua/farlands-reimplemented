package io.github.nebulazorua.farlands.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.nebulazorua.farlands.Config;
import io.github.nebulazorua.farlands.Farlands;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

@Mixin(BlendedNoise.class)
public class FarlandsBlendedNoiseMixin {
	@Shadow @Final private PerlinNoise minLimitNoise;
	@Shadow @Final private PerlinNoise maxLimitNoise;
	@Shadow @Final private PerlinNoise mainNoise;
	@Shadow @Final private double xzMultiplier;
	@Shadow @Final private double yMultiplier;
	@Shadow @Final private double xzFactor;
	@Shadow @Final private double yFactor;
	@Shadow @Final private double smearScaleMultiplier;
	@Shadow @Final private double maxValue;
	@Shadow @Final private double xzScale;
	@Shadow @Final private double yScale;

	@Inject(method = "compute", at = @At("HEAD"), cancellable = true)
	// Removes the Farlands patch and does dumbshit:tm: to make the farlands generate at a customizable distance

	public void compute(DensityFunction.FunctionContext ctx, CallbackInfoReturnable<Double> cir) {
		double d0 = (double)ctx.blockX() * this.xzMultiplier;
		double d1 = (double)ctx.blockY() * this.yMultiplier;
		double d2 = (double)ctx.blockZ() * this.xzMultiplier;

		// TODO: make this per-dimension perhaps
		boolean inXLands = Mth.abs(ctx.blockX()) >= Config.xDistance;
		boolean inZLands = Mth.abs(ctx.blockZ()) >= Config.zDistance;

		if(inXLands)
			d0 += Math.max(0, 12550800 - Config.xDistance) * this.xzMultiplier;

		if (inZLands)
			d2 += Math.max(0, 12550800 - Config.zDistance) * this.xzMultiplier;

		double d3 = d0 / this.xzFactor;
		double d4 = d1 / this.yFactor;
		double d5 = d2 / this.xzFactor;
		double d6 = this.yMultiplier * this.smearScaleMultiplier;
		double d7 = d6 / this.yFactor;
		double d8 = 0.0D;
		double d9 = 0.0D;
		double d10 = 0.0D;
		boolean flag = true;
		double d11 = 1.0D;

		for(int i = 0; i < 8; ++i) {
			ImprovedNoise improvednoise = this.mainNoise.getOctaveNoise(i);
			if (improvednoise != null) {
				double x = d3 * d11;
				double z = d5 * d11;
				if(!inXLands)
					x = PerlinNoise.wrap(x);

				if(!inZLands)
					z = PerlinNoise.wrap(z);

				d10 += improvednoise.noise(x, PerlinNoise.wrap(d4 * d11), z, d7 * d11, d4 * d11) / d11;
			}

			d11 /= 2.0D;
		}

		double d16 = (d10 / 10.0D + 1.0D) / 2.0D;
		boolean flag1 = d16 >= 1.0D;
		boolean flag2 = d16 <= 0.0D;
		d11 = 1.0D;

		for(int j = 0; j < 16; ++j) {
			double d12 = (d0 * d11);
			double d13 = PerlinNoise.wrap(d1 * d11);
			double d14 = (d2 * d11);

			if (!inXLands)
				d12 = PerlinNoise.wrap(d12);

			if (!inZLands)
				d14 = PerlinNoise.wrap(d14);

			double d15 = d6 * d11;
			if (!flag1) {
				ImprovedNoise improvednoise1 = this.minLimitNoise.getOctaveNoise(j);
				if (improvednoise1 != null) {
					d8 += improvednoise1.noise(d12, d13, d14, d15, d1 * d11) / d11;
				}
			}

			if (!flag2) {
				ImprovedNoise improvednoise2 = this.maxLimitNoise.getOctaveNoise(j);
				if (improvednoise2 != null) {
					d9 += improvednoise2.noise(d12, d13, d14, d15, d1 * d11) / d11;
				}
			}

			d11 /= 2.0D;
		}

		cir.setReturnValue(Mth.clampedLerp(d8 / 512.0D, d9 / 512.0D, d16) / 128.0D);
	}


}

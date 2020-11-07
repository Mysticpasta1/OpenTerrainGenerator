package com.pg85.otg.forge.gen;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.INoiseGenerator;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.OctavesNoiseGenerator;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.SimplexNoiseGenerator;
import net.minecraft.world.gen.settings.NoiseSettings;

// TODO: This class should be redundant, NewOTGChunkGenerator should replace this?
public class ForgeChunkGenNoise
{
	private static final float[] field_236081_j_ = Util.make(new float[25], (p_236092_0_) ->
	{
		for (int i = -2; i <= 2; ++i)
		{
			for (int j = -2; j <= 2; ++j)
			{
				float f = 10.0F / MathHelper.sqrt((float) (i * i + j * j) + 0.2F);
				p_236092_0_[i + 2 + (j + 2) * 5] = f;
			}
		}
	});
	
	private final OTGNoiseChunkGenerator otgNoiseChunkGenerator;	
	@Nullable
	private final SimplexNoiseGenerator field_236083_v_;
	
	private final int noiseSizeY;
	private final int verticalNoiseGranularity;
	private final int horizontalNoiseGranularity;
	private final OctavesNoiseGenerator field_222568_o;
	private final OctavesNoiseGenerator field_222569_p;
	private final OctavesNoiseGenerator field_222570_q;
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final SharedSeedRandom randomSeed;
	private final OctavesNoiseGenerator field_236082_u_;	
	private final INoiseGenerator surfaceDepthNoise;	
	
	public ForgeChunkGenNoise(OTGNoiseChunkGenerator otgNoiseChunkGenerator, NoiseSettings noiseSettings, long seed)
	{
		this.otgNoiseChunkGenerator = otgNoiseChunkGenerator;
		this.verticalNoiseGranularity = noiseSettings.func_236175_f_() * 4;
		this.horizontalNoiseGranularity = noiseSettings.func_236174_e_() * 4;
		this.noiseSizeY = noiseSettings.func_236169_a_() / this.verticalNoiseGranularity;
		this.randomSeed = new SharedSeedRandom(seed);
		this.field_222568_o = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
		this.field_222569_p = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
		this.field_222570_q = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-7, 0));		
		this.surfaceDepthNoise = noiseSettings.func_236178_i_() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0));
		this.randomSeed.skip(2620);
		this.field_236082_u_ = new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-15, 0));
		if (noiseSettings.func_236180_k_())
		{
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom(seed);
			sharedseedrandom.skip(17292);
			this.field_236083_v_ = new SimplexNoiseGenerator(sharedseedrandom);
		} else {
			this.field_236083_v_ = null;
		}
	}

	private void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ)
	{
		NoiseSettings noisesettings = this.otgNoiseChunkGenerator.dimensionSettings.get().func_236113_b_();
		double d0;
		double d1;
		if (this.field_236083_v_ != null)
		{
			d0 = EndBiomeProvider.func_235317_a_(this.field_236083_v_, noiseX, noiseZ) - 8.0F;
			if (d0 > 0.0D)
			{
				d1 = 0.25D;
			} else {
				d1 = 1.0D;
			}
		} else {
			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			int j = this.otgNoiseChunkGenerator.func_230356_f_();
			float f3 = this.otgNoiseChunkGenerator.getBiomeProvider1().getNoiseBiome(noiseX, j, noiseZ).getDepth();

			for (int k = -2; k <= 2; ++k)
			{
				for (int l = -2; l <= 2; ++l)
				{
					Biome biome = this.otgNoiseChunkGenerator.getBiomeProvider1().getNoiseBiome(noiseX + k, j, noiseZ + l);
					float f4 = biome.getDepth();
					float f5 = biome.getScale();
					float f6;
					float f7;
					if (noisesettings.func_236181_l_() && f4 > 0.0F)
					{
						f6 = 1.0F + f4 * 2.0F;
						f7 = 1.0F + f5 * 4.0F;
					} else {
						f6 = f4;
						f7 = f5;
					}

					float f8 = f4 > f3 ? 0.5F : 1.0F;
					float f9 = f8 * ForgeChunkGenNoise.field_236081_j_[k + 2 + (l + 2) * 5] / (f6 + 2.0F);
					f += f7 * f9;
					f1 += f6 * f9;
					f2 += f9;
				}
			}

			float f10 = f1 / f2;
			float f11 = f / f2;
			double d16 = f10 * 0.5F - 0.125F;
			double d18 = f11 * 0.9F + 0.1F;
			d0 = d16 * 0.265625D;
			d1 = 96.0D / d18;
		}

		double d12 = 684.412D * noisesettings.func_236171_b_().func_236151_a_();
		double d13 = 684.412D * noisesettings.func_236171_b_().func_236153_b_();
		double d14 = d12 / noisesettings.func_236171_b_().func_236154_c_();
		double d15 = d13 / noisesettings.func_236171_b_().func_236155_d_();
		double d17 = noisesettings.func_236172_c_().func_236186_a_();
		double d19 = noisesettings.func_236172_c_().func_236188_b_();
		double d20 = noisesettings.func_236172_c_().func_236189_c_();
		double d21 = noisesettings.func_236173_d_().func_236186_a_();
		double d2 = noisesettings.func_236173_d_().func_236188_b_();
		double d3 = noisesettings.func_236173_d_().func_236189_c_();
		double d4 = noisesettings.func_236179_j_() ? this.func_236095_c_(noiseX, noiseZ) : 0.0D;
		double d5 = noisesettings.func_236176_g_();
		double d6 = noisesettings.func_236177_h_();

		for (int i1 = 0; i1 <= this.noiseSizeY; ++i1)
		{
			double d7 = this.func_222552_a(noiseX, i1, noiseZ, d12, d13, d14, d15);
			double d8 = 1.0D - (double) i1 * 2.0D / (double) this.noiseSizeY + d4;
			double d9 = d8 * d5 + d6;
			double d10 = (d9 + d0) * d1;
			if (d10 > 0.0D)
			{
				d7 = d7 + d10 * 4.0D;
			} else {
				d7 = d7 + d10;
			}

			if (d19 > 0.0D)
			{
				double d11 = ((double) (this.noiseSizeY - i1) - d20) / d19;
				d7 = MathHelper.clampedLerp(d17, d7, d11);
			}

			if (d2 > 0.0D)
			{
				double d22 = ((double) i1 - d3) / d2;
				d7 = MathHelper.clampedLerp(d21, d7, d22);
			}

			noiseColumn[i1] = d7;
		}
	}
	
	private double func_222552_a(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_)
	{
		double d0 = 0.0D;
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 1.0D;

		for (int i = 0; i < 16; ++i)
		{
			double d4 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_1_ * p_222552_4_ * d3);
			double d5 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_2_ * p_222552_6_ * d3);
			double d6 = OctavesNoiseGenerator.maintainPrecision((double) p_222552_3_ * p_222552_4_ * d3);
			double d7 = p_222552_6_ * d3;
			ImprovedNoiseGenerator improvednoisegenerator = this.field_222568_o.getOctave(i);
			if (improvednoisegenerator != null)
			{
				d0 += improvednoisegenerator.func_215456_a(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
			}

			ImprovedNoiseGenerator improvednoisegenerator1 = this.field_222569_p.getOctave(i);
			if (improvednoisegenerator1 != null)
			{
				d1 += improvednoisegenerator1.func_215456_a(d4, d5, d6, d7, (double) p_222552_2_ * d7) / d3;
			}

			if (i < 8)
			{
				ImprovedNoiseGenerator improvednoisegenerator2 = this.field_222570_q.getOctave(i);
				if (improvednoisegenerator2 != null)
				{
					d2 += improvednoisegenerator2.func_215456_a(OctavesNoiseGenerator.maintainPrecision((double) p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.maintainPrecision((double) p_222552_2_ * p_222552_10_ * d3), OctavesNoiseGenerator.maintainPrecision((double) p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, (double) p_222552_2_ * p_222552_10_ * d3) / d3;
				}
			}

			d3 /= 2.0D;
		}

		return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
	}

	private double[] func_222547_b(int p_222547_1_, int p_222547_2_)
	{
		double[] adouble = new double[this.noiseSizeY + 1];
		this.fillNoiseColumn(adouble, p_222547_1_, p_222547_2_);
		return adouble;
	}
	
	private int func_236087_a_(int p_236087_1_, int p_236087_2_, @Nullable BlockState[] p_236087_3_, @Nullable Predicate<BlockState> p_236087_4_)
	{
		int i = Math.floorDiv(p_236087_1_, this.horizontalNoiseGranularity);
		int j = Math.floorDiv(p_236087_2_, this.horizontalNoiseGranularity);
		int k = Math.floorMod(p_236087_1_, this.horizontalNoiseGranularity);
		int l = Math.floorMod(p_236087_2_, this.horizontalNoiseGranularity);
		double d0 = (double) k / (double) this.horizontalNoiseGranularity;
		double d1 = (double) l / (double) this.horizontalNoiseGranularity;
		double[][] adouble = new double[][] {this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1)};

		for (int i1 = this.noiseSizeY - 1; i1 >= 0; --i1)
		{
			double d2 = adouble[0][i1];
			double d3 = adouble[1][i1];
			double d4 = adouble[2][i1];
			double d5 = adouble[3][i1];
			double d6 = adouble[0][i1 + 1];
			double d7 = adouble[1][i1 + 1];
			double d8 = adouble[2][i1 + 1];
			double d9 = adouble[3][i1 + 1];

			for (int j1 = this.verticalNoiseGranularity - 1; j1 >= 0; --j1)
			{
				double d10 = (double) j1 / (double) this.verticalNoiseGranularity;
				double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
				int k1 = i1 * this.verticalNoiseGranularity + j1;
				BlockState blockstate = this.func_236086_a_(d11, k1);
				if (p_236087_3_ != null)
				{
					p_236087_3_[k1] = blockstate;
				}

				if (p_236087_4_ != null && p_236087_4_.test(blockstate))
				{
					return k1 + 1;
				}
			}
		}

		return 0;
	}
	
	public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type heightmapType)
	{
		return this.func_236087_a_(p_222529_1_, p_222529_2_, null, heightmapType.getHeightLimitPredicate());
	}

	public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_)
	{
		BlockState[] ablockstate = new BlockState[this.noiseSizeY * this.verticalNoiseGranularity];
		this.func_236087_a_(p_230348_1_, p_230348_2_, ablockstate, null);
		return new Blockreader(ablockstate);
	}
	
	private double func_236095_c_(int p_236095_1_, int p_236095_2_)
	{
		double d0 = this.field_236082_u_.getValue(p_236095_1_ * 200, 10.0D, p_236095_2_ * 200, 1.0D, 0.0D, true);
		double d1;
		if (d0 < 0.0D)
		{
			d1 = -d0 * 0.3D;
		} else {
			d1 = d0;
		}

		double d2 = d1 * 24.575625D - 2.0D;
		return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
	}
	
	// Get stone block (corrected)?
	protected BlockState func_236086_a_(double p_236086_1_, int p_236086_3_)
	{
		BlockState blockstate;
		if (p_236086_1_ > 0.0D)
		{
			blockstate = this.otgNoiseChunkGenerator.defaultBlock;
		}
		else if (p_236086_3_ < this.otgNoiseChunkGenerator.func_230356_f_())
		{
			blockstate = this.otgNoiseChunkGenerator.defaultFluid;
		} else {
			blockstate = AIR;
		}

		return blockstate;
	}
	
	public INoiseGenerator getSurfaceDepthNoise()
	{
		return surfaceDepthNoise;
	}
	
	// TODO: Do we need these?
	
	private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_)
	{
		double d0 = p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_;
		double d1 = (double) p_222554_1_ + 0.5D;
		double d2 = d1 * d1;
		double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
		double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
		return d4 * d3;
	}	
}

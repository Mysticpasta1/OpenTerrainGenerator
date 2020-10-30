package com.pg85.otg.generator.biome;

import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.generator.biome.layers.legacy.Layer;
import com.pg85.otg.generator.biome.layers.legacy.OldBiomeLayers;

/**
 * Generates biomes from the image specified by the WorldConfig.
 *
 */
public class FromImageBiomeGenerator extends LayeredBiomeGenerator
{
    public FromImageBiomeGenerator(LocalWorld world)
    {
        super(world);
    }

    @Override
    protected Layer[] initLayers()
    {
    	return OldBiomeLayers.createFromImage(world);
    }
}

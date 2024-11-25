package io.yukkuric.hexparse.fabric.config;

import io.yukkuric.hexparse.config.HexParseConfig;

public class HexParseConfigFabric implements HexParseConfig.API {
    // TODO

    @Override
    public boolean canParseGreatPatterns() {
        return false;
    }

    @Override
    public boolean canParseGreatPattern(String patternId) {
        return false;
    }
}

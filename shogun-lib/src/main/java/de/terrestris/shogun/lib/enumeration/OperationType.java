package de.terrestris.shogun.lib.enumeration;

public enum OperationType {
    GET_MAP("GetMap"),
    GET_CAPABILITIES("GetCapabilities"),
    GET_FEATURE_INFO("GetFeatureInfo"),
    DESCRIBE_LAYER("DescribeLayer"),
    GET_LEGEND_GRAPHIC("GetLegendGraphic"),
    GET_STYLES("GetStyles"),
    DESCRIBE_FEATURE_TYPE("DescribeFeatureType"),
    GET_FEATURE("GetFeature"),
    LOCK_FEATURE("LockFeature"),
    TRANSACTION("Transaction"),
    DESCRIBE_COVERAGE("DescribeCoverage"),
    GET_COVERAGE("GetCoverage"),
    EXECUTE("Execute"),
    DESCRIBE_PROCESS("DescribeProcess"),
    GET_SCENE("GetScene"),
    GET_LAYER_INFO("GetLayerInfo"),
    GET_TILE("GetTile");

    private final String type;

    private OperationType(String type) {
        this.type = type;
    }
}

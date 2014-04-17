package dk.itu.groupe.parsing.krak;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public enum KrakRoadType
{   
    ROAD(5, KrakRoadType.secondFactor, 5),
    PROJ_SECONDARY_ROUTE(24, KrakRoadType.secondFactor, 4),
    PROJ_ROAD(25, KrakRoadType.secondFactor, 5),
    SECONDARY_ROUTE_EXIT(34, KrakRoadType.secondFactor, 24),
    
    OTHER_ROAD(6, KrakRoadType.thirdFactor, 6),
    PATH(8, KrakRoadType.thirdFactor, 11),
    DIRT_ROAD(10, KrakRoadType.thirdFactor, 9),
    PEDESTRIAN_ZONE(11, KrakRoadType.thirdFactor, 8),
    PROJ_OTHER_ROAD(26, KrakRoadType.thirdFactor, 6),
    PROJ_PATH(28, KrakRoadType.thirdFactor, 11),
    
    UNKNOWN(0, KrakRoadType.firstFactor, 10),
    HIGHWAY(1, KrakRoadType.firstFactor, 1),
    EXPRESSWAY(2, KrakRoadType.firstFactor, 2),
    PRIMARY_ROUTE(3, KrakRoadType.firstFactor, 3),
    SECONDARY_ROUTE(4, KrakRoadType.firstFactor, 4),
    PROJ_HIGHWAY(21, KrakRoadType.firstFactor, 1),
    PROJ_EXPRESSWAY(22, KrakRoadType.firstFactor, 2),
    PROJ_PRIMARY_ROUTE(23, KrakRoadType.firstFactor, 3),
    HIGHWAY_EXIT(31, KrakRoadType.firstFactor, 21),
    EXPRESSWAY_EXIT(32, KrakRoadType.firstFactor, 22),
    PRIMARY_ROUTE_EXIT(33, KrakRoadType.firstFactor, 23),
    HIGHWAY_TUNNEL(41, KrakRoadType.firstFactor, 12),
    EXPRESSWAY_TUNNEL(42, KrakRoadType.firstFactor, 12),
    FERRY(80, KrakRoadType.firstFactor, 14),
    ALSO_UNKNOWN(95, KrakRoadType.firstFactor, 10),
    
    EXACT_LOCATION_UNKNOWN(99, 10, 13);

    private final static int firstFactor = Integer.MAX_VALUE, secondFactor = 100, thirdFactor = 30;
    private final int typeNumber, newTypeNumber;
    private final int factorActivate;

    private KrakRoadType(int typeNumber, int factorActivate, int newTypeNumber)
    {
        this.typeNumber = typeNumber;
        this.factorActivate = factorActivate;
        this.newTypeNumber = newTypeNumber;
    }
    
    public int getNewTypeNumber()
    {
        return newTypeNumber;
    }

    public int getTypeNumber()
    {
        return typeNumber;
    }
    
    public boolean isEnabled(double factor)
    {
        return factorActivate >= factor;
    }
}

package dk.itu.groupe.parsing.krak;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public enum KrakRoadType
{   
    ROAD(5, 5),
    PROJ_SECONDARY_ROUTE(24, 4),
    PROJ_ROAD(25, 5),
    SECONDARY_ROUTE_EXIT(34, 24),
    
    OTHER_ROAD(6, 6),
    PATH(8, 11),
    DIRT_ROAD(10, 9),
    PEDESTRIAN_ZONE(11, 8),
    PROJ_OTHER_ROAD(26, 6),
    PROJ_PATH(28, 11),
    
    UNKNOWN(0, 10),
    HIGHWAY(1, 1),
    EXPRESSWAY(2, 2),
    PRIMARY_ROUTE(3, 3),
    SECONDARY_ROUTE(4, 4),
    PROJ_HIGHWAY(21, 1),
    PROJ_EXPRESSWAY(22, 2),
    PROJ_PRIMARY_ROUTE(23, 3),
    HIGHWAY_EXIT(31, 21),
    EXPRESSWAY_EXIT(32, 22),
    PRIMARY_ROUTE_EXIT(33, 23),
    HIGHWAY_TUNNEL(41, 12),
    EXPRESSWAY_TUNNEL(42, 12),
    FERRY(80, 14),
    ALSO_UNKNOWN(95, 10),
    
    EXACT_LOCATION_UNKNOWN(99, 13);

    private final int typeNumber, newTypeNumber;

    private KrakRoadType(int typeNumber, int newTypeNumber)
    {
        this.typeNumber = typeNumber;
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
}

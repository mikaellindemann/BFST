package dk.itu.groupe;

/**
 *
 * @author Peter Bindslev <plil@itu.dk>, Rune Henriksen <ruju@itu.dk> & Mikael
 * Jepsen <mlin@itu.dk>
 */
public enum RoadType
{

    UNKNOWN(0, RoadType.firstFactor),
    HIGHWAY(1, RoadType.firstFactor),
    EXPRESSWAY(2, RoadType.firstFactor),
    PRIMARY_ROUTE(3, RoadType.firstFactor),
    SECONDARY_ROUTE(4, RoadType.firstFactor),
    ROAD(5, RoadType.secondFactor),
    OTHER_ROAD(6, RoadType.thirdFactor),
    PATH(8, RoadType.thirdFactor),
    DIRT_ROAD(10, RoadType.thirdFactor),
    PEDESTRIAN_ZONE(11, RoadType.thirdFactor),
    PROJ_HIGHWAY(21, RoadType.firstFactor),
    PROJ_EXPRESSWAY(22, RoadType.firstFactor),
    PROJ_PRIMARY_ROUTE(23, RoadType.firstFactor),
    PROJ_SECONDARY_ROUTE(24, RoadType.secondFactor),
    PROJ_ROAD(25, RoadType.secondFactor),
    PROJ_OTHER_ROAD(26, RoadType.thirdFactor),
    PROJ_PATH(28, RoadType.thirdFactor),
    HIGHWAY_EXIT(31, RoadType.firstFactor),
    EXPRESSWAY_EXIT(32, RoadType.firstFactor),
    PRIMARY_ROUTE_EXIT(33, RoadType.firstFactor),
    SECOUNDARY_ROUTE_EXIT(34, RoadType.secondFactor),
    //OTHER_EXIT(35, RoadType.secondFactor),
    HIGHWAY_TUNNEL(41, RoadType.firstFactor),
    EXPRESSWAY_TUNNEL(42, RoadType.firstFactor),
    //PRIMARY_ROUTE_TUNNEL(43, RoadType.firstFactor),
    //SECONDARY_ROUTE_TUNNEL(44, RoadType.secondFactor),
    //OTHER_ROAD_TUNNEL(45, RoadType.secondFactor),
    //SMALL_ROAD_TUNNEL(46, RoadType.secondFactor),
    //PATH_TUNNEL(48, RoadType.secondFactor),
    FERRY(80, RoadType.firstFactor),
    ALSO_UNKNOWN(95, RoadType.firstFactor),
    EXACT_LOCATION_UNKNOWN(99, 10);

    private final static int firstFactor = 1000, secondFactor = 100, thirdFactor = 30;
    private final int typeNumber;
    private final int factorActivate;

    private RoadType(int typeNumber, int factorActivate)
    {
        this.typeNumber = typeNumber;
        this.factorActivate = factorActivate;
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

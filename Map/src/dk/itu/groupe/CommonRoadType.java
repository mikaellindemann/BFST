package dk.itu.groupe;

/**
 *
 * @author Mikael
 */
public enum CommonRoadType
{

    /**
     * OSM: Motorway.
     * KRAK: Highway(1) and Proj_Highway(21).
     */
    MOTORWAY(1, CommonRoadType.firstFactor),
    /**
     * OSM: Trunk.
     * KRAK: Expressway(2) and Proj_Expressway(22).
     */
    TRUNK(2, CommonRoadType.firstFactor),
    /**
     * OSM: Primary.
     * KRAK: PrimaryRoute(3) and Proj_PrimaryRoute(23).
     */
    PRIMARY(3, CommonRoadType.firstFactor),
    /**
     * OSM: Secondary.
     * KRAK: SecondaryRoute(4) and Proj_SecondaryRoute(24).
     */
    SECONDARY(4, CommonRoadType.secondFactor),
    /**
     * OSM: Tertiary.
     * KRAK: Road(5) and Proj_Road(25).
     */
    TERTIARY(5, CommonRoadType.secondFactor),
    /**
     * OSM: Unclassified and Byway.
     * KRAK: OtherRoad(6) and Proj_OtherRoad(26).
     */
    UNCLASSIFIED(6, CommonRoadType.thirdFactor),
    /**
     * OSM: Residential, Living_Street, Living_Street;Footway, Mini_Roundabout and service.
     * KRAK:.
     */
    RESIDENTIAL(7, CommonRoadType.thirdFactor),
    /**
     * OSM: Motorway_Link.
     * KRAK: HighwayExit(31).
     */
    MOTORWAY_LINK(21, CommonRoadType.firstFactor),
    /**
     * OSM: Trunk_Link.
     * KRAK: ExpresswayExit(32).
     */
    TRUNK_LINK(22, CommonRoadType.firstFactor),
    /**
     * OSM: Primary_Link.
     * KRAK: PrimaryRouteExit(33).
     */
    PRIMARY_LINK(23, CommonRoadType.firstFactor),
    /**
     * OSM: Secondary_Link.
     * KRAK: SecondaryRouteExit(34).
     */
    SECONDARY_LINK(24, CommonRoadType.secondFactor),
    /**
     * OSM: Tertiary_Link.
     * KRAK:.
     */
    TERTIARY_LINK(25, CommonRoadType.secondFactor),
    /**
     * OSM: Pedestrian.
     * KRAK: PedestrianZone(11).
     */
    PEDESTRIAN(8, CommonRoadType.thirdFactor),
    /**
     * OSM: Track.
     * KRAK: DirtRoad(10).
     */
    TRACK(9, CommonRoadType.thirdFactor),
    /**
     * OSM: Road, Yes, Tr, Rfe and Turning_Loop.
     * KRAK: Unknown(0) and AlsoUnknown(95).
     */
    ROAD(10, CommonRoadType.secondFactor),
    /**
     * OSM: Path and Path;Track.
     * KRAK: Path(8) and Proj_Path(28).
     */
    PATH(11, CommonRoadType.thirdFactor),
    /**
     * OSM: Tunnel.
     * KRAK: HighwayTunnel(41) and ExpresswayTunnel(42).
     */
    TUNNEL(12, CommonRoadType.firstFactor),
    /**
     * OSM:.
     * KRAK: ExactLocationUnknown(99).
     */
    PLACES(13, 3),
    /**
     * OSM:.
     * KRAK: Ferry(80).
     */
    FERRY(14, CommonRoadType.firstFactor),
    
    COASTLINE(-1, CommonRoadType.firstFactor);

    private final static int firstFactor = Integer.MAX_VALUE, secondFactor = 65, thirdFactor = 10;
    private final int type;
    private final int factorActivate;

    private CommonRoadType(int type, int factorActivate)
    {
        this.type = type;
        this.factorActivate = factorActivate;
    }

    public int getTypeNo()
    {
        return type;
    }
    
    public boolean isEnabled(double factor)
    {
        return factorActivate >= factor;
    }
}

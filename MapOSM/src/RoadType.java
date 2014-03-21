
public enum RoadType
{

    MOTORWAY("motorway"),
    MOTORWAY_LINK("motorway_link"),
    PRIMARY("primary"),
    PRIMARY_LINK("primary_link"),
    SECONDARY("secondary"),
    SECONDARY_LINK("secondary_link"),
    TERTIARY("tertiary"),
    TERTIARY_LINK("tertiary_link"),
    TRUNK("trunk"),
    TRUNK_LINK("trunk_link"),
    BRIDLEWAY("bridleway"),
    BUS_GUIDEWAY("bus_guideway"),
    BUS_STOP("bus_stop"),
    BYWAY("byway"),
    CONSTRUCTION("construction"),
    CROSSING("crossing"),
    CYCLEWAY("cycleway"),
    ELEVATOR("elevator"),
    FOOTWAY("footway"),
    LIVING_STREET("living_street"),
    LIVING_STREET_FOOTWAY("living_street;footway"),
    MINI_ROUNDABOUT("mini_roundabout"),
    PARKING_AISLE("parking_aisle"),
    PASSING_PLACE("passing_place"),
    PATH("path"),
    PATH_TRACK("path;track"),
    PEDESTRIAN("pedestrian"),
    PLATFORM("platform"),
    PROPOSED("proposed"),
    RACETRACK("racetrack"),
    RACEWAY("raceway"),
    RESIDENTIAL("residential"),
    REST_AREA("rest_area"),
    RFE("rfe"),
    ROAD("road"),
    SERVICE("service"),
    SERVICES("services"),
    STEPS("steps"),
    TR("tr"),
    TRACK("track"),
    TUNNEL("tunnel"),
    TURNING_LOOP("turning_loop"),
    UNCLASSIFIED("unclassified"),
    YES("yes"),
    NOTAG("notag");

    private final String type;
    private boolean enabled;

    private RoadType(String type)
    {
        this.type = type;
        enabled = false;
    }

    @Override
    public String toString()
    {
        return type;
    }

    public void enable()
    {
        enabled = true;
    }

    public void disable()
    {
        enabled = false;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}


public class Node
{

    private final long id;
    private final double x, y;

    private Node(long id, double x, double y)
    {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public long getId()
    {
        return id;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public static Node parseNode(String s)
    {
        String[] sub = s.split("\\, ");
        long id = Long.parseLong(sub[0]);
        double x = Double.parseDouble(sub[1]);
        double y = Double.parseDouble(sub[2]);
        return new Node(id, x, y);
    }
}

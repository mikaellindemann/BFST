package dk.itu.groupe.parsing.coastline;

import de.jotschi.geoconvert.GeoConvert;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;

/**
 * This class opens an ESRI-ShapeFile containing data about OpenStreetMap
 * polygons and parses them into commaseparated files where "nodes" contain
 * information about points and "edges" links these points together to form the
 * polygons.
 *
 * @author Mikael
 */
public class ShapeParser
{
    private final double xMin, yMin, xMax, yMax;
    private final java.awt.geom.Rectangle2D denmark;
    String fileName;

    /**
     * Creates and runs a ShapeParser. This can take some time depending on the
     * file.
     *
     * @param file The shapefile to parse. If null a popupwindow will ask the
     * user.
     * @param xMin The minimum x-coordinate in the shapefile (OSM: WSG84
     * lat/lon)
     * @param yMin The minimum y-coordinate in the shapefile (OSM: WSG84
     * lat/lon)
     * @param xMax The maximum x-coordinate in the shapefile (OSM: WSG84
     * lat/lon)
     * @param yMax The maximum y-coordinate in the shapefile (OSM: WSG84
     * lat/lon)
     * @throws java.io.IOException - if the file does not exist, is a directory
     * rather than a regular file, or for some other reason cannot be opened for
     * reading.
     * @throws InvalidShapeFileException If the given file contains other shapes
     * than Polygons.
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public ShapeParser(java.io.File file, double xMin, double yMin, double xMax, double yMax) throws java.io.IOException, InvalidShapeFileException
    {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        denmark = new java.awt.geom.Rectangle2D.Double(this.xMin, this.yMin, this.xMax - this.xMin, this.yMax - this.yMin);
        if (file == null) {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Polygon-Shapefile (.shp)", "shp"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showOpenDialog(null);
            file = fileChooser.getSelectedFile();
        }
        if (file == null) {
            return;
        }
        
        ValidationPreferences prefs = new ValidationPreferences();
        prefs.setAllowUnlimitedNumberOfPointsPerShape(true);
        ShapeFileReader shapeReader = new ShapeFileReader(new java.io.FileInputStream(file), prefs);
        new java.io.File("./res/data/coastline").mkdirs();
        DataOutputStream edgeWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/coastline/edges.bin")));
        DataOutputStream nodeWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/coastline/nodes.bin")));
        int nodenumber = 0;
        for (AbstractShape s = shapeReader.next(); s != null; s = shapeReader.next()) {
            switch (s.getShapeType()) {
                case POLYGON:
                    PolygonShape polygon = (PolygonShape) s;
                    double minx = polygon.getBoxMinX();
                    double miny = polygon.getBoxMinY();
                    if (!denmark.intersects(minx, miny, polygon.getBoxMaxX() - minx, polygon.getBoxMaxY() - miny)) {
                        break;
                    }
                    if (polygon.getNumberOfPoints() > 50000) {
                        System.out.println("This can take some time, number of nodes: " + polygon.getNumberOfPoints());
                    }
                    for (int j = 0; j < polygon.getNumberOfParts(); j++) {
                        PointData[] pds = polygon.getPointsOfPart(j);
                        edgeWriter.writeInt(pds.length - 1);
                        for (int i = 0; i < pds.length - 1; i++) {
                            PointData pd = pds[i];
                            double[] xy = new double[2];
                            assert 32 == GeoConvert.LatLonToUTMXY(GeoConvert.DegToRad(pd.getY()), GeoConvert.DegToRad(pd.getX()), 32, xy);
                            edgeWriter.writeInt(nodenumber);
                            nodeWriter.writeInt(nodenumber++);
                            nodeWriter.writeFloat((float)xy[0]);
                            nodeWriter.writeFloat((float)xy[1]);
                        }
                    }
                    break;
            }
        }
        DataOutputStream infoWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./res/data/coastline/info.bin")));
        infoWriter.writeInt(nodenumber);
        nodeWriter.close();
        edgeWriter.close();
        infoWriter.close();
    }

    /**
     * This method runs the ShapeParser with coordinates going a little around
     * Denmark.
     *
     * @param args not used.
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void main(String[] args)
    {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            new ShapeParser(null, 4.043, 54.699, 17.314, 57.868);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException | java.io.IOException | InvalidShapeFileException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
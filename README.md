BFST
====
First Year Project

##Nothing important yet!


## OpenStreetMap:
#Download:
Go to http://download.geofabrik.de/europe/denmark.html and download denmark-latest.osm.bz2. Unpack it at the root of OSMParsing.
Decompress and put at the root folder of OSMParsing.

#Converting:
Run OSM.java. 
You should now have a file nodes.csv, and a file ways.csv,
Create a folder "ways" if it isn't in the root of OSMParsing.
Run WayTag.java. Run WayTagSplitter.java.
Ways should now contain lots of smaller .csv-files.
Put the contents of ways (not the folder itself) and nodes.csv into the data-folder of MapOSM.
Run and magic will happen.. But it is slow, and has not been fine tuned with the correct corner-coordinates and stuff.
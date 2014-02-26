package dk.itu.groupe;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Parse Krak data files (kdv_node_unload.txt, kdv_unload.txt).
 *
 * Customize to your needs by overriding processNode and processEdge. 
 * See example in main. 
 *
 * Original author Peter Tiedemann petert@itu.dk; 
 * updates (2014) by Søren Debois, debois@itu.dk
 */
public abstract class KrakLoader {

	public abstract void processNode(NodeData nd);
	public abstract void processEdge(EdgeData ed);

	/**
	 * Load krak-data from given files, invoking 
	 * processNode and processEdge once for each node- and edge-
	 * specification in the input file, respectively. 
	 *
	 * @param nodeFile
	 * @param edgeFile
	 * @return
	 * @throws IOException if there is a problem reading data or the files dont exist
	 */
	public void load(String nodeFile, String edgeFile) throws IOException {
		/* Nodes. */
		BufferedReader br;
		br = new BufferedReader(new FileReader(nodeFile));
		br.readLine(); // First line is column names, not data.

		String line;
		while( (line = br.readLine()) != null ) {
			processNode(new NodeData(line)); 
		}
		br.close();

		/* Edges. */

		br = new BufferedReader(new FileReader(edgeFile));
		br.readLine(); // Again, first line is column names, not data.

		while( (line = br.readLine()) != null ) {
			processEdge(new EdgeData(line)); 
		}
		br.close();

		DataLine.resetInterner();
		System.gc();
	}

	/** 
	 * Example usage. You may need to adjust the java heap-size,
	 * i.e., -Xmx256M on the command-line. 
	 */
	public static void main(String[] args) throws IOException {
		String dir = "../data/";

		// For this example, we'll simply load the raw data into
		// ArrayLists.
		final ArrayList<NodeData> nodes = new ArrayList<NodeData>();
		final ArrayList<EdgeData> edges = new ArrayList<EdgeData>();

		// For that, we need to inherit from KrakLoader and override
		// processNode and processEdge. We do that with an 
		// anonymous class. 
		KrakLoader loader = new KrakLoader() {
			@Override public void 
				processNode(NodeData nd) { nodes.add(nd); } 
			@Override public void 
				processEdge(EdgeData ed) { edges.add(ed); } 
		};

		// If your machine slows to a crawl doing inputting, try
		// uncommenting this. 
		// Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		// Invoke the loader class.
		loader.load(dir + "kdv_node_unload.txt", 
				dir + "kdv_unload.txt");

		// Check the results.
		System.out.printf("Loaded %d nodes, %d edges\n", 
				nodes.size(), edges.size());
		MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
		System.out.printf("Heap memory usage: %d MB%n", 
				mxbean.getHeapMemoryUsage().getUsed()/(1000000));

		/*
		PrintStream out = new PrintStream(dir + "nodes.txt");
		for (NodeData node : nodes) out.println(node);
		out.close();
		out = new PrintStream(dir + "edges.txt");
		for (EdgeData edge : edges) out.println(edge);
		out.close();
		*/
	}
}

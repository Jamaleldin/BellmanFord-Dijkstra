package eg.edu.alexu.csd.filestructure.graphs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.management.RuntimeErrorException;

class Node {
	private int destination;
	private int weight;

	Node(int destination, int weight) {
		this.destination = destination;
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public int getDestination() {
		return destination;
	}
}

public class Graph implements IGraph {
	private ArrayList<ArrayList<Node>> adjList = new ArrayList<ArrayList<Node>>();
	private Integer V;
	private Integer E;
	private Integer Inf = Integer.MAX_VALUE / 2;
	private ArrayList<Integer> dijkstraOrder = new ArrayList<Integer>();

	@Override
	public void readGraph(File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			String lineSplited[] = line.split(" ");
			V = Integer.parseInt(lineSplited[0]);
			E = Integer.parseInt(lineSplited[1]);
			for (int i = 0; i < V; i++) {
				adjList.add(new ArrayList<Node>());
			}
			for (int i = 0; i < E; i++) {
				line = br.readLine();
				lineSplited = line.split(" ");
				Node node = new Node(Integer.parseInt(lineSplited[1]), Integer.parseInt(lineSplited[2]));
				adjList.get(Integer.parseInt(lineSplited[0])).add(node);
			}
			if (br.readLine() != null) {
				br.close();
				throw new RuntimeErrorException(null);
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeErrorException(null);
		}

	}

	@Override
	public int size() {
		return E;
	}

	@Override
	public ArrayList<Integer> getVertices() {
		ArrayList<Integer> vertices = new ArrayList<>();
		for (int i = 0; i < V; i++) {
			vertices.add(i);
		}
		return vertices;
	}

	@Override
	public ArrayList<Integer> getNeighbors(int v) {
		ArrayList<Integer> neighbors = new ArrayList<>();
		for (int i = 0; i < adjList.get(v).size(); i++) {
			neighbors.add(adjList.get(v).get(i).getDestination());
		}
		return neighbors;
	}

	@Override
	public void runDijkstra(int src, int[] distances) {
		dijkstraOrder.clear();
		boolean[] visited = new boolean[distances.length];
		for (int i = 0; i < distances.length; i++) {
			distances[i] = Inf;
			visited[i] = false;
		}
		distances[src] = 0;
		for (int i = 0; i < V - 1; i++) {
			int u = getMin(distances, visited);
			dijkstraOrder.add(u);
			visited[u] = true;
			for (int j = 0; j < adjList.get(u).size(); j++) {
				if (!visited[adjList.get(u).get(j).getDestination()]
						&& distances[adjList.get(u).get(j).getDestination()] > distances[u]
								+ adjList.get(u).get(j).getWeight()) {
					distances[adjList.get(u).get(j).getDestination()] = distances[u]
							+ adjList.get(u).get(j).getWeight();
				}
			}
		}

	}

	private int getMin(int[] distances, boolean[] visited) {
		int min = Inf;
		int minIndex = -1;
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] <= min && !visited[i]) {
				min = distances[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	@Override
	public ArrayList<Integer> getDijkstraProcessedOrder() {
		return dijkstraOrder;
	}

	@Override
	public boolean runBellmanFord(int src, int[] distances) {
		// initially all distances are inf except the source's distance which is zero
		Arrays.fill(distances, Inf);
		distances[src] = 0;

		// relax edges
		for (int i = 0; i < (V - 1); i++) {
			for (int j = 0; j < V; j++) {
				ArrayList<Node> temp = adjList.get(j);
				int source = j;
				for (int k = 0; k < temp.size(); k++) {
					int dest = temp.get(k).getDestination();
					int weight = temp.get(k).getWeight();
					if (distances[source] != Inf && distances[dest] > distances[source] + weight) {
						distances[dest] = distances[source] + weight;
					}
				}
			}
		}

		// check negative cycles
		for (int i = 0; i < V; i++) {
			ArrayList<Node> temp = adjList.get(i);
			int source = i;
			for (int j = 0; j < temp.size(); j++) {
				int dest = temp.get(j).getDestination();
				int weight = temp.get(j).getWeight();
				if (distances[source] != Inf && distances[dest] > distances[source] + weight) {
					return false; // graph contains negative cycles
				}
			}
		}
		return true;
	}

}
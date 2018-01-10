package nest.mdc.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

public class Dijkstra {
	public int verNum;
	Map<Integer, Set<Integer>> neigMap;
	public int dis[];
	public int[] prevArray;
	public int[] nextArray;

	public Dijkstra(int verNum, Map<Integer, Set<Integer>> neigMap) {
		// TODO Auto-generated constructor stub
		this.verNum = verNum;
		dis = new int[verNum];
		prevArray = new int[verNum];
		this.neigMap = neigMap;
	}

	/**
	 * Ѱ�ҵ�A����B�����·�� ʹ��getPathToDes��ȡ·��
	 * 
	 * @param souID
	 *            Դ�ڵ�A
	 * @param desID
	 *            Ŀ��ڵ�B
	 * @param vertexIdSet
	 *            �ɾ����Ľڵ㼯��
	 * @param costMatrix
	 *            cost����
	 * @return �ɹ��򷵻�true
	 */
	public boolean findShortestPathAtoB(final int souID, final int desID, Set<Integer> vertexIdSet,
			int[][] costMatrix) {
		if (!vertexIdSet.contains(desID)) {
			return false;
		}
		// ����prevArray
		for (int k = 0; k < verNum; k++) {
			prevArray[k] = -1;
			dis[k] = 0;
		}
		Set<Integer> minNodeSets = new HashSet<Integer>(); // �����ҵ������·���㼯�ϣ������������
		Set<Integer> waitSet = new HashSet<Integer>();
		int findedID = souID, minCost;
		while (minNodeSets.size() < (vertexIdSet.contains(souID) ? vertexIdSet.size() - 1 : vertexIdSet.size())) {
			for (int currNeig : neigMap.get(findedID)) {
				if (currNeig == souID || minNodeSets.contains(currNeig) || !vertexIdSet.contains(currNeig)) {
					continue;
				}
				if (costMatrix[findedID][currNeig] <= 0) {
					System.out.println("Error008!");
				}
				waitSet.add(currNeig);
				if (dis[currNeig] == 0 || dis[currNeig] > dis[findedID] + costMatrix[findedID][currNeig]) {
					dis[currNeig] = dis[findedID] + costMatrix[findedID][currNeig];
					prevArray[currNeig] = findedID;
				}
			}
			minCost = 10000;
			findedID = -1;
			for (int currVerID : waitSet) {
				if (dis[currVerID] < minCost) {
					if (dis[currVerID] <= 0) {
						System.out.println("Error009!");
					}
					minCost = dis[currVerID];
					findedID = currVerID;
				}
			}
			if (findedID < 0) {
				// System.out.println("Error010!");
				return false;
			}
			waitSet.remove(findedID);
			minNodeSets.add(findedID);
			if (findedID == desID) {
				return true;
			}
		}
		return false;
	}

	public int[] getPathToDes(int desID) {
		if (prevArray[desID] < 0) {
			return null;
		}
		int[] tem = new int[verNum];
		int length = 0, i;
		tem[0] = desID;
		length++;
		for (i = 1; i < tem.length; i++) {
			if (prevArray[tem[i - 1]] < 0) {
				break;
			}
			tem[i] = prevArray[tem[i - 1]];
			length++;
		}
		int[] path = new int[length];
		for (i = 0; i < path.length; i++) {
			path[i] = tem[path.length - i - 1];
		}
		if (length == 0) {
			return null;
		}
		return path;
	}

	public ArrayList<Integer> getPathToDesInList(int desID) {
		if (prevArray[desID] < 0) {
			return null;
		}
		int[] tem = new int[verNum];
		int length = 0, i;
		tem[0] = desID;
		length++;
		for (i = 1; i < tem.length; i++) {
			if (prevArray[tem[i - 1]] < 0) {
				break;
			}
			tem[i] = prevArray[tem[i - 1]];
			length++;
		}
		ArrayList<Integer> path = new ArrayList<>();
		for (i = 0; i < length; i++) {
			path.add(tem[length - i - 1]);
		}
		if (path.isEmpty()) {
			return null;
		}
		return path;
	}
}

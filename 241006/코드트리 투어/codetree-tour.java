import java.util.*;
import java.io.*;

public class Main {

    static class Edge implements Comparable<Edge> {
        int u;
        int w;

        Edge(int u, int w) {
            this.u = u;
            this.w = w;
        }

        @Override
        public int compareTo(Edge o) {
            return w - o.w;
        }

        public String toString() {
            return "연결된 노드는 " + u + " || 가중치는 " + w;
        }
    }

    static class Item implements Comparable<Item> {
        int id;
        int revenue;
        int dest;
        int cost;

        Item(int id, int revenue, int dest) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.cost = revenue - costArr[dest];
        }

        @Override
        public int compareTo(Item o) {
            if(o.cost < 0) {
                return -1;
            } else if(cost < 0) {
                return 1;
            } else if(o.cost - cost == 0) {
                return id - o.id;
            }

            return o.cost - cost;
        }
    }

    static class Cost {
        int cost;

        Cost(int cost) {
            this.cost = cost;
        }

        int getCost() {
            return cost;
        }

        void setCost(int cost) {
            this.cost = cost;
        }
    }

    static ArrayList<ArrayList<Edge>> g = new ArrayList<>();
    static Map<Integer, Item> itemMap = new HashMap<>();
    static PriorityQueue<Item> itemPq = new PriorityQueue<>();
    // 객체 참조 할 것
    static int[] costArr;

    static int n, m;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        int Q = Integer.parseInt(br.readLine());
        int v, u, w;
        int id, revenue, dest, s;
        while(Q-- > 0) {
            st = new StringTokenizer(br.readLine());
            int command = Integer.parseInt(st.nextToken());

            switch(command) {
                case 100 :
                    n = Integer.parseInt(st.nextToken());
                    m = Integer.parseInt(st.nextToken());

                    makeLand(n, m, st);
                    break;
                case 200 :
                    id = Integer.parseInt(st.nextToken());
                    revenue = Integer.parseInt(st.nextToken());
                    dest = Integer.parseInt(st.nextToken());

                    createItem(id, revenue, dest);
                    break;
                case 300 :
                    id = Integer.parseInt(st.nextToken());

                    cancelItem(id);
                    break;
                case 400 :
                    sell();
                    break;
                case 500:
                    id = Integer.parseInt(st.nextToken());

                    changeStart(id);
                    break;
            }
        }
    }

    static private void makeLand(int n, int m, StringTokenizer st) {
        for(int i = 0; i < n; i++) {
            g.add(new ArrayList<Edge>());
        }

        for(int i = 0; i < m; i++) {
            int u = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());

            g.get(u).add(new Edge(v, w));
            g.get(v).add(new Edge(u, w));
        }

        changeStart(0);
    }

    static private void createItem(int id, int revenue, int dest) {
        itemMap.put(id, new Item(id, revenue, dest));
        itemPq.add(itemMap.get(id));
    }

    static private void changeStart(int s) {
        // Dijkstra로 모든 여행 상품까지 COST 계산
        // 다익스트라 알고리즘 초기화
		costArr = new int[n]; // 최소 비용을 저장할 배열
		for (int i = 0; i < n; i++) {
			costArr[i] = Integer.MAX_VALUE;
		}

        PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
		pq.offer(new Edge(s, 0));
		costArr[s] = 0;
		while (!pq.isEmpty()) {
			Edge now = pq.poll();

			// 꺼낸 노드 = 현재 최소 비용을 갖는 노드.
			// 즉, 해당 노드의 비용이 현재 dist배열에 기록된 내용보다 크다면 고려할 필요가 없으므로 스킵한다.
			// 주의점 2 : 중복노드 방지1 : 만일, 이 코드를 생략한다면, 언급한 내용대로 이미 방문한 정점을 '중복하여 방문'하게 된다.
			// 만일 그렇다면, 큐에 있는 모든 다음 노드에대하여 인접노드에 대한 탐색을 다시 진행하게 된다.
			// 그래프 입력이 만일 완전 그래프의 형태로 주어진다면, 이 조건을 생략한 것 만으로 시간 복잡도가 E^2에 수렴할 가능성이 생긴다.
			if (costArr[now.u] < now.w) {
				continue;
			}

			// 선택된 노드의 모든 주변 노드를 고려한다.
			for (int i = 0; i < g.get(now.u).size(); i++) {
				Edge next = g.get(now.u).get(i);
				if (costArr[next.u] > now.w + next.w) {
					costArr[next.u] = now.w + next.w;
					pq.add(new Edge(next.u, costArr[next.u]));
				}
			}
		}

        PriorityQueue<Item> temp = new PriorityQueue<>();
        while(!itemPq.isEmpty()) {
            Item item = itemPq.poll();
            Item newItem = new Item(item.id, item.revenue, item.dest);
            temp.add(newItem);
        }
        itemPq = temp;
    }

    static private void cancelItem(int id) {
        if(itemMap.get(id) == null) {
            // System.out.println("cancel failed!!! : NONE ID :  " + id);
            return;
        }
        itemMap.remove(id);
        // System.out.println("canceled : ID : " + id);
    }

    static private void sell() {
        // System.out.println("sell -------- ");
        // for(Item item : itemPq) {
        //     System.out.print(item.id + " " + item.revenue + " - " + costArr[item.dest] + " = " + item.cost + " || ");
        // }
        // System.out.println();

        if(itemPq.isEmpty()) {
            System.out.println(-1);
            return;
        }

        Item item = itemPq.poll();
        while(!itemPq.isEmpty() && itemMap.get(item.id) == null) {
            item = itemPq.poll();
        }
        if(itemMap.get(item.id) == null) {
            System.out.println(-1);
            return;
        }
        
        // if(itemPq.isEmpty()) {
        //     System.out.println(-1);
        //     return;
        // }
        

        if(item.cost < 0) {
            System.out.println(-1);
            itemPq.add(item);
        }else {
            itemMap.remove(item.id);
            System.out.println(item.id);
        }
    }
}
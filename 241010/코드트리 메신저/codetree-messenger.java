import java.io.*;
import java.util.*;

public class Main {
    static class Node {
        int id;
        int authority;
        boolean alram = true;

        int parentId;
        Node parent;

        // 현재 노드가 전달할 수 있는 알람 수
        // arrOfArlam[3] = 4 라면 위로 3개의 노드에 전달할 수 있는 알람이 4
        int[] arrOfAlram = new int[21];

        Node rightChild;
        int rightMaxAuthority;

        Node leftChild;
        int leftMaxAuthority;

        Node(int id, int authority, int parentId) {
            this.id = id;
            this.authority = authority;
            this.parentId = parentId;
        }
    }

    static Map<Integer, Node> nodeMap = new HashMap<>();

    static int N, Q;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        StringBuilder sb = new StringBuilder();

        st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        int c1 = 0;
        int power = 0;
        int c2 = 0;
        while(Q-- > 0) {
            st = new StringTokenizer(br.readLine());

            int command = Integer.parseInt(st.nextToken());

            switch (command) {
                case 100: 
                    init(st);
                    break;
                case 200 :
                    c1 = Integer.parseInt(st.nextToken());
                    turn_alram(c1);
                    // printAll();
                    break;
                case 300 :
                    c1 = Integer.parseInt(st.nextToken());
                    power = Integer.parseInt(st.nextToken());
                    change_authority(c1, power);
                    // printAll();
                    break;
                case 400 :
                    c1 = Integer.parseInt(st.nextToken());
                    c2 = Integer.parseInt(st.nextToken());
                    change_parent(c1, c2);
                    // printAll();
                    break;
                case 500: 
                    c1 = Integer.parseInt(st.nextToken());
                    int count = getArlam(c1);
                    // System.out.println(count);
                    sb.append(count).append("\n");
            }
        }
        System.out.println(sb);
    }

    static private void init(StringTokenizer st) {
        // root 생성
        nodeMap.put(0, new Node(0, 0, -1));
        Node root = nodeMap.get(0);

        // 다른 노드 생성만
        for(int i= 1; i <= N; i++) {
            nodeMap.put(i, new Node(i, 0, -1));
        }

        // 노드 연결
        for(int i = 1; i <= N; i++) {
            int parentId = Integer.parseInt(st.nextToken());

            Node parent = nodeMap.get(parentId);
            Node child = nodeMap.get(i);

            // System.out.println(parent.id + " " + child.id);

            if(parent.leftChild == null) {
                parent.leftChild = child;
                child.parent = parent;
                child.parentId = parentId;
            } else {
                parent.rightChild = child;
                child.parent = parent;
                child.parentId = parentId;
            }
        }

        // 권한 설정
        for(int i = 1; i <= N; i++) {
            int authority = Integer.parseInt(st.nextToken());

            Node node = nodeMap.get(i);
            node.authority = authority;
        }

        // ArrOfArlam 초기화
        alramInit(root);
    }
    
    static private void alramInit(Node now) {
        if(now == null) {
            return;
        }

        now.arrOfAlram = new int[21];
        for(int i = 0; i <= now.authority && i <= 20; i++) {
            now.arrOfAlram[i]++;
        }
        
        alramInit(now.leftChild);
        alramInit(now.rightChild);
        
        if(now.leftChild != null) {
            // lefChilde의 n(1~20)번 알람 수가 현재 노드의 n - 1번 알람 수값에 반영
            for(int i = 1; i <= 20; i++) {
                now.arrOfAlram[i - 1] += now.leftChild.arrOfAlram[i];
            }
        }

        if(now.rightChild != null) {
            for(int i = 1; i <= 20; i++) {
                now.arrOfAlram[i - 1] += now.rightChild.arrOfAlram[i];
            }
        }
    }

    static private void recal(int id) {
        Node now = nodeMap.get(id);

        while(now != null && now.id != 0) {
            Arrays.fill(now.arrOfAlram, 0);
            for(int i = 0; i <= now.authority; i++) {
                now.arrOfAlram[i]++;
            }

            if(now.leftChild != null && now.leftChild.alram) {
            // leftChild의 n(1~20)번 알람 수가 현재 노드의 n - 1번 알람 수값에 반영
                for(int i = 1; i <= 20; i++) {
                    now.arrOfAlram[i - 1] += now.leftChild.arrOfAlram[i];
                }
            }

            if(now.rightChild != null && now.rightChild.alram) {
                for(int i = 1; i <= 20; i++) {
                    now.arrOfAlram[i - 1] += now.rightChild.arrOfAlram[i];
                }
            }

            now = now.parent;
        }

    }

    static private void turn_alram(int c) {
        Node node = nodeMap.get(c);
        node.alram = !node.alram;

        // node부터 root까지 거슬러 올라가면서 arrOfAlram 재계산
        recal(c);
    }

    static private void change_authority(int c, int power) {
        Node node = nodeMap.get(c);
        node.authority = power;

        recal(c);
    }

    static private void change_parent(int c1, int c2) {
        Node node1 = nodeMap.get(c1);
        Node node2 = nodeMap.get(c2);

        Node parent1 = nodeMap.get(node1.parentId);
        Node parent2 = nodeMap.get(node2.parentId);

        if(parent1.leftChild == node1) {
            parent1.leftChild = node2;
        } else if(parent1.rightChild == node1) {
            parent1.rightChild = node2;
        } 
        node2.parentId = parent1.id;
        node2.parent = parent1;

        if(parent2.leftChild == node2) {
            parent2.leftChild = node1;
            
        } else if(parent2.rightChild == node2) {
            parent2.rightChild = node1;
        }
        node1.parentId = parent2.id;
        node1.parent = parent2;

        recal(node1.id);
        recal(node2.id);
        // printAll();
    }

    static private void printAll() {
        for(int i = 1; i <= N; i++) {
            Node node = nodeMap.get(i);
            System.out.println(i + "번 노드의 부모 노드는 " + node.parent.id + " || 권한은 " + node.authority + " || 현재 알람 상태는 " + node.alram);
            System.out.println("현재 받을 수 있는 알람 수는 ? " + node.arrOfAlram[0]);

            // System.out.println(Arrays.toString(node.arrOfAlram));
            // System.out.println(node.leftChild == null ? "null" : node.leftChild.id);
            // System.out.println(node.rightChild == null ? "null" : node.rightChild.id);
        }
    }

    static private int getArlam(int c) {
        Node node = nodeMap.get(c);

        return node.arrOfAlram[0] - 1;
    }
}
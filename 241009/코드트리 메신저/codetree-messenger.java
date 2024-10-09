import java.io.*;
import java.util.*;

public class Main {
    static class Node {
        int id;
        int authority;
        boolean alram = true;

        int parentId;
        Node parent;

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
                    break;
                case 300 :
                    c1 = Integer.parseInt(st.nextToken());
                    power = Integer.parseInt(st.nextToken());
                    change_authority(c1, power);
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
    }

    static private void turn_alram(int c) {
        Node node = nodeMap.get(c);
        node.alram = !node.alram;

        // 디버깅
        // System.out.println(node.id + "번 노드 알림 " + !node.alram + "에서 " + node.alram + "으로 변경");
    }

    static private void change_authority(int c, int power) {
        Node node = nodeMap.get(c);

        // System.out.println(node.id + "번 노드 권한세기 " + node.authority + "에서 " + power + "로 변경");
        node.authority = power;
    }

    static private void change_parent(int c1, int c2) {
        Node node1 = nodeMap.get(c1);
        Node node2 = nodeMap.get(c2);

        Node parent1 = nodeMap.get(node1.parentId);
        Node parent2 = nodeMap.get(node2.parentId);

        if(parent1.leftChild == node1) {
            // System.out.println("111111");
            parent1.leftChild = node2;
        } else if(parent1.rightChild == node1) {
            // System.out.println("222222");
            parent1.rightChild = node2;
        } 
        node2.parentId = parent1.id;
        node2.parent = parent1;

        if(parent2.leftChild == node2) {
            // System.out.println("333333");
            parent2.leftChild = node1;
            
        } else if(parent2.rightChild == node2) {
            // System.out.println("444444");
            parent2.rightChild = node1;
        }
        node1.parentId = parent2.id;
        node1.parent = parent2;
    }

    static private void printAll() {
        for(int i = 1; i <= N; i++) {
            Node node = nodeMap.get(i);
            System.out.println(i + "번 노드의 부모 노드는 " + node.parent.id + " || 권한은 " + node.authority + " || 현재 알람 상태는 " + node.alram);
            System.out.println(node.leftChild == null ? "null" : node.leftChild.id);
            System.out.println(node.rightChild == null ? "null" : node.rightChild.id);
        }
    }

    static private int getArlam(int c) {
        // 트리 순회 & 백트래킹
        Node now = nodeMap.get(c);

        // int left = 0;
        // int right = 0;
        // if(now.leftChild != null) {
        //     left = traversal(now.leftChild, 1);
        // } 
        
        // if(now.rightChild != null) {
        //     right = traversal(now.rightChild, 1);
        // }

        int result = traversal2(now);
        // System.out.println(result);
        // System.out.println(now.id + "번 노드의 왼쪽에서 " + left + "개의 알람, 오른쪽에서 " + right + "개의 알람");
        
        // return left + right;
        return result;
    }

    static private int traversal(Node now, int depth) {
        // 현재 노드 알림이 꺼져있으면 이 상위 노드로는 알람이 안감
        if(!now.alram) {
            return 0;
        }
        
        int result = 0;  
        // c1 노드로부터 뎁스와 authority를 비교해서 본인의 알람이 전달되는지 확인
        if(depth <= now.authority) {
            result++;
        }

        // general case
        int left = 0;
        int right = 0;
        if(now.leftChild != null) {
            left = traversal(now.leftChild, depth + 1);
        }
        
        if(now.rightChild != null) {
            right = traversal(now.rightChild, depth + 1);
        }

        return result + left + right;
    }

    static private int traversal2(Node node) {
        Queue<Node> q = new ArrayDeque<>();
        Queue<Node> q2 = new ArrayDeque<>();

        q.add(node);
        int result = 0;
        int depth = 1;
        while(!q.isEmpty()) {
            Node now = q.poll();
            if(now.leftChild != null) {
                if(now.leftChild.alram) {
                    q2.add(now.leftChild);
                    if(now.leftChild.authority >= depth) {
                        result++;
                    }
                }
            }

            if(now.rightChild != null) {
                if(now.rightChild.alram) {
                    q2.add(now.rightChild);
                    if(now.rightChild.authority >= depth) {
                        result++;
                    }
                }
            }

            if(q.isEmpty() && !q2.isEmpty()) {
                q = q2;
                q2 = new ArrayDeque<>();
                depth++;
            }
        }

        return result;
    }
}
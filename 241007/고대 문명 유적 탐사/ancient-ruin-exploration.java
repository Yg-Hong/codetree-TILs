import java.util.*;
import java.io.*;

public class Main {

    static int[][] map = new int[5][5];
    static int[] pieceArr;
    static int pieceIdx = 0;
    static int[] dx = new int[] {1,0,-1,0};
    static int[] dy = new int[] {0,1,0,-1};

    static int K, M;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        StringBuilder sb = new StringBuilder();

        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());   

        for(int y = 0; y < 5; y++) {
            st = new StringTokenizer(br.readLine());
            for(int x = 0; x < 5; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }

        pieceArr = new int[M];
        st = new StringTokenizer(br.readLine());
        for(int i = 0 ; i < M; i++) {
            pieceArr[i] = Integer.parseInt(st.nextToken());
        }

        int result = 0;
        int maxScore = Integer.MIN_VALUE;
        int[][] maxMap = new int[5][5];

        int yyy = 0;
        int xxx = 0;
        int ddd = 0;
        while(K-- > 0) {
            // 회전각 0:90도, 1: 180도, 2: 270도
            for(int angle = 2; angle >= 0; angle--) {
                // 우선순위 때문에 순서 거꾸로
                for(int y = 3; y >= 1; y--) {
                    for(int x = 3; x >= 1; x--) {
                        int[][] temp = spin(y, x, angle);
                        int score = getItem(temp);
                        // score가 지금까지 최솟값보다 같거나 크다면 temp, maxScore 기억
                        if(score >= maxScore) {
                            maxScore = score;
                            maxMap = temp;
                            yyy = y;
                            xxx = x;
                            ddd = angle;
                        }
                    }
                }
            }
            result = chainReact(maxMap);
            maxScore = 0;
            if(result == 0) {
                break;
            }
            sb.append(result).append(" ");
            // System.out.println();
            // for(int[] line : maxMap) {
            //     System.out.println(Arrays.toString(line));
            // }
            // System.out.println(maxScore);
            // System.out.println(yyy + "   " + xxx + "   " + ddd);
            // System.out.println(result + " ----------- ");
        }
        System.out.println(sb);
    }

    static private int[][] spin (int y, int x, int angle) {
        int[][] temp = new int[5][5];
        for(int i = 0 ; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                temp[i][j] = map[i][j];
            }
        }

        Queue<Integer> q = new ArrayDeque();
        q.add(map[y - 1][x - 1]);
        q.add(map[y - 1][x]);
        q.add(map[y - 1][x + 1]);
        q.add(map[y][x + 1]);
        q.add(map[y + 1][x + 1]);
        q.add(map[y + 1][x]);
        q.add(map[y + 1][x - 1]);
        q.add(map[y][x - 1]);

        int nextY = 0, nextX = 0, d = 0;
        if(angle == 2) {
            nextY = y + 1;
            nextX = x - 1;
            d = 3;
        } else if(angle == 1) {
            nextY = y + 1;
            nextX = x + 1;
            d = 2;
        } else if(angle == 0) {
            nextY = y - 1;
            nextX = x + 1;
            d = 1;
        }

        temp[nextY][nextX] = q.poll();
        // System.out.println(nextY + "  " + nextX);
        // System.out.println(map[nextY][nextX]);
        int nowY = nextY;
        int nowX = nextX;
        while(!q.isEmpty()) {
            nextY = nowY + dy[d];
            nextX = nowX + dx[d];
            if(nextY < y - 1 || nextY > y + 1 || nextX < x - 1 || nextX > x + 1) {
                d = (d + 1) % 4;
                continue;
            }
            temp[nextY][nextX] = q.poll();
            // System.out.println(nextY + "  " + nextX);
            nowY = nextY;
            nowX = nextX;
        }

        // for(int[] line: temp) {
        //     System.out.println(Arrays.toString(line));
        // }

        return temp;
    }

    static private int getItem(int[][] temp) {
        boolean[][] visit = new boolean[5][5];

        int result = 0;
        for(int y = 0; y < 5; y++) {
            for(int x = 0; x < 5; x++) {
                if(!visit[y][x]) {
                    result += bfs(y, x, temp, visit);
                }
            }   
        }

        return result;
    }

    static private int bfs(int y, int x, int[][] temp, boolean[][] visit) {
        Queue<int[]> q = new ArrayDeque<>();
        Queue<int[]> q2 = new ArrayDeque<>();
        q.add(new int[] {y, x});
        q2.add(new int[] {y, x});
        visit[y][x] = true;
        int pivot = temp[y][x];

        while(!q.isEmpty()) {
            int[] now = q.poll();

            for(int d = 0; d < 4; d++) {
                int nextY = now[0] + dy[d];
                int nextX = now[1] + dx[d];

                if(nextY < 0 || nextY >=5 || nextX < 0 || nextX >= 5) {
                    continue;
                }
                if(visit[nextY][nextX]) {
                    continue;
                }
                if(temp[nextY][nextX] != pivot) {
                    continue;
                }

                q.add(new int[] {nextY, nextX});
                q2.add(new int[] {nextY, nextX});
                visit[nextY][nextX] = true;
            }
        }

        int result = q2.size(); 
        if(q2.size() >= 3) {
            while(!q2.isEmpty()) {
                int[] now = q2.poll();
                temp[now[0]][now[1]] = 0;
            }
        }
        
        return result >= 3 ? result : 0;
    }

    static private void init(int[][] maxMap) {
        for(int x = 0; x < 5; x++) {
            for(int y = 4; y >=0; y--) {
                if(maxMap[y][x] == 0) {
                    maxMap[y][x] = pieceArr[pieceIdx]; 
                    pieceIdx = (pieceIdx + 1) % M;
                }
            }
        }

        map = maxMap;
    }

    static private int chainReact(int[][] maxMap) {
        boolean flag = true;
        int total = 0;
        while(flag) {
            flag = false;

            int value = getItem(maxMap);
            // System.out.println("연쇄 작용 !!! - " + value);
            total += value;
            if(value != 0) {
                flag = true;
            }
            init(maxMap);
        }
        return total;
    }
}
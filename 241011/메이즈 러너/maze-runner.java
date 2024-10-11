import java.util.*;
import java.io.*;

public class Main {
    static int N, M, K;
    
    static class Runner {
        int r;
        int c;

        int run = 0;
        boolean out = false;

        Runner(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    static int[][] map;
    static Map<Integer, Runner> runnerMap = new HashMap<>();
    // static ArrayList<Integer> runnerArr = new ArrayList<>();
    static int Er, Ec;
    static int[] dr = {0, 0, 1, -1};
    static int[] dc = {-1, 1, 0, 0};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        map = new int[N + 1][N + 1];
        for(int y = 1; y <= N; y++) {
            st = new StringTokenizer(br.readLine());

            for(int x = 1; x <= N; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }
        
        // for(int[] line : map) {
        //     System.out.println(Arrays.toString(line));
        // }

        for(int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());

            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());

            runnerMap.put(i, new Runner(r, c));
        }

        // 출구 좌표
        st = new StringTokenizer(br.readLine());
        Er = Integer.parseInt(st.nextToken());
        Ec = Integer.parseInt(st.nextToken());

        map[Er][Ec] = -1;

        while(K-- > 0) {
            move_runner();
            rotate_maze();
        }
        
        int result = 0;
        for(int i = 1; i <= M; i++) {
            Runner runner = runnerMap.get(i);
            result += runner.run;
        }
        System.out.println(result);
        System.out.println(Er + " " + Ec);
    }

    static private int getDistance(int r1, int r2, int c1, int c2) {
        int rDist = r1 - r2;
        if(rDist < 0) {
            rDist *= -1;
        }

        int cDist = c1 - c2;
        if(cDist < 0) {
            cDist *= -1;
        }

        return rDist + cDist;
    }

    static private void move_runner() {
        for(int i = 1; i <= M; i++) {
            Runner runner = runnerMap.get(i);

            if(runner.out) {
                continue;
            }

            int min_dist = getDistance(Er, runner.r, Ec, runner.c);
            int min_direction = -1;
            for(int d = 0; d < 4; d++) {
                int nextR = runner.r + dr[d];
                int nextC = runner.c + dc[d];

                int dist = getDistance(Er, nextR, Ec, nextC);
                if(nextR <= 0 || nextR > N || nextC <= 0 || nextC > N) {
                    continue;
                }
                if(dist <= min_dist && map[nextR][nextC] <= 0) {
                    min_dist = dist;
                    min_direction = d;
                }
            }

            // static int[] dr = {0, 0, 1, -1};
            // static int[] dc = {-1, 1, 0, 0};
            // 0 : 왼쪽 1 : 오른쪽 2 : 아래쪽 3 : 위쪽
            // System.out.println(i + "번 러너의 다음 방향 : " + min_direction);

            if(min_direction == -1) {
                continue;
            }
            
            runner.r += dr[min_direction];
            runner.c += dc[min_direction];

            runner.run++;
            if(map[runner.r][runner.c] == -1) {
                runner.out = true;
            }
        }
    }

    static private void rotate_maze() {
        // 가장 작은 사각형 구하기
        // 출구로부터 가장 가까운 러너 구하기
        int i = 1;
        Runner min_runner = new Runner(-1, -1);
        int min_dist = 0;
        for(i = 1; i <= M; i++) {
            Runner runner = runnerMap.get(i);

            if(!runner.out) {
                min_runner = runner;
                min_dist = getDistance(Er, min_runner.r, Ec, min_runner.c);
                break;
            }
        }

        for(i = 2; i <= M; i++) {
            Runner runner = runnerMap.get(i);

            if(runner.out) {
                continue;
            }

            int dist = getDistance(Er, runner.r, Ec, runner.c);

            if(cmp(min_runner, runner)) {
                min_runner = runner;
                min_dist = dist;
            }
        }

        // System.out.println("현재 가장 가까운 러너 " + min_runner.r + ", " + min_runner.c + " | 거리는 " + min_dist);
        
        // 사각형 좌상단 꼭짓점 구하기
        int w = Er - min_runner.r > 0 ? Er - min_runner.r : (Er - min_runner.r) * -1;
        int l = Ec - min_runner.c > 0 ? Ec - min_runner.c : (Ec - min_runner.c) * -1;
        int len = Math.max(w, l);
        // System.out.println("한변의 길이 " + len);
        int r = 0, c = 0;

        a:for(int x = 1; x <= N - len; x++) {
            for(int y = 1; y <= N - len; y++) {
                if((Er >= y && Er <= y + len && Ec >= x && Ec <= x + len)
                    && (min_runner.r >= y && min_runner.r <= y + len 
                    && min_runner.c >= x && min_runner.c <= x + len)) {
                        r = y;
                        c = x;
                        break a;
                    }
            }
        }

        // System.out.println("좌상단 꼭짓점 좌표 : " + r + ", " + c);
        rotate(map, r, c, len);
    }

    static private boolean cmp(Runner r1, Runner r2) {
        int dist1 = getDistance(Er, r1.r, Ec, r1.c);
        int dist2 = getDistance(Er, r2.r, Ec, r2.c);

        if(dist1 > dist2) {
            return true;
        }
        if(dist1 == dist2 && r1.r > r2.r) {
            return true;
        }
        if(dist1 == dist2 && r1.r == r2.r && r1.c > r2.c) {
            return true;
        }

        return false;
    }

    static private void rotate(int[][] map, int r, int c, int l) {
        // 회전 시작 하기 전에 러너 map에 표시
        for(int i = 1; i <= M; i++) {
            Runner runner = runnerMap.get(i);
            if(runner.out) {
                continue;
            }
            map[runner.r][runner.c] = i + 100;
        }

        // for(int[] line : map) {
        //     System.out.println(Arrays.toString(line));
        // }
        // System.out.println();
        // reverse horizontal
        for(int y = 0; y <= l / 2; y++) {
            for(int x = c; x <= c + l; x++) {
                int temp = map[r + l - y][x];
                map[r + l - y][x] = map[r + y][x];
                map[r + y][x] = temp;
            }
        }

        // for(int[] line : map) {
        //     System.out.println(Arrays.toString(line));
        // }
        // System.out.println();
        // reverse diagonal
        for(int y = 0; y <= l; y++) {
            for(int x = 0; x <= l; x++) {
                if(x == y) {
                    continue;
                }
                if(x < y) {
                    continue;
                }

                int temp = map[r + y][c + x];
                map[r + y][c + x] = map[r + x][c + y];
                map[r + x][c + y] = temp;
                // System.out.println("Swap : (" + (r + y) + ", " + (c + x) + ")와 (" + (c + y) + ", " + (r + x)+")");
            }
        }
        // for(int[] line : map) {
        //     System.out.println(Arrays.toString(line));
        // }
        // System.out.println();

        for(int y = r; y <= r + l; y++) {
            for(int x = c; x <= c + l; x++) {
                if(map[y][x] > 0 && map[y][x] <= 10) {
                    map[y][x]--;
                }
                if(map[y][x] > 100) {
                    int id = map[y][x] - 100;
                    Runner runner = runnerMap.get(id);
                    runner.r = y;
                    runner.c = x;
                    runnerMap.put(id, runner);
                }
                if(map[y][x] == -1) {
                    Er = y;
                    Ec = x;
                }
            }
        }

        // map에서 선수 지우기
        for(int y = 1; y <= N; y++) {
            for(int x = 1; x <= N; x++) {
                if(map[y][x] > 100) {
                    map[y][x] = 0;
                }
            }
        }

        // for(int[] line : map) {
        //     System.out.println(Arrays.toString(line));
        // }
        // System.out.println();
    }
}
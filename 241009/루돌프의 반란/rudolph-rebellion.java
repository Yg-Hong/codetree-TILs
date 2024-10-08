import java.util.*;
import java.io.*;

public class Main {
    static int N, M, P, C, D;
    static int m = 0;
    static int Rr, Rc;
    
    static class Santa {
        int r;
        int c;
        int stunned; // 기절한 턴
        int score;
        boolean out = false;

        Santa(int r, int c) {
            this.r = r;
            this.c = c;
            stunned = -1;
            score = 0;
        }
    }
    static Map<Integer, Santa> santaMap = new HashMap<>();
    static Map<Integer, Santa> alive = new HashMap<>();
    static ArrayList<Integer> santaArr = new ArrayList<>();

    static int[][] map;

    static int[] dr_santa = {-1, 0, 1, 0};
    static int[] dc_santa = {0, 1, 0, -1};
    static int[] dr_dear = {-1, -1, 0, 1, 1, 1, 0, -1};
    static int[] dc_dear = {0, 1, 1, 1, 0, -1, -1, -1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        map = new int[N][N];

        st = new StringTokenizer(br.readLine());
        Rr = Integer.parseInt(st.nextToken()) - 1;
        Rc = Integer.parseInt(st.nextToken()) - 1;
        // -1이 루돌프
        map[Rr][Rc] = -1; 

        for(int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());

            int Pn = Integer.parseInt(st.nextToken());
            int Sr = Integer.parseInt(st.nextToken());
            int Sc = Integer.parseInt(st.nextToken());

            santaMap.put(Pn, new Santa(Sr - 1, Sc - 1));
            santaArr.add(Pn);
            
            map[Sr - 1][Sc - 1] = Pn;
        }

        Collections.sort(santaArr);
        boolean flag = true;
        while(flag && m++ < M) {
            move_dear();

            move_santa();

            flag = adjustment();
        }

        StringBuilder sb = new StringBuilder();
        for(int Pn : santaArr) {
            Santa santa = santaMap.get(Pn);

            sb.append(santa.score).append(" ");
        }

        System.out.println(sb);
    }

    static void move_dear() {
        Santa min_dist_santa = new Santa(-1, -1); // 아무값이나 초기화
        int min_dist = Integer.MAX_VALUE;

        for(int Pn : santaArr) {
            Santa santa = santaMap.get(Pn);

            if(santa.out) {
                continue;
            }

            int dist = getDistance(santa.r, Rr, santa.c, Rc);
            if(min_dist > dist) {
                min_dist = dist;
                min_dist_santa = santa;
            } 
            else if(min_dist == dist && cmp(santa, min_dist_santa)) {
                min_dist = dist;
                min_dist_santa = santa;
            }
        }
        
        min_dist = Integer.MAX_VALUE;
        int min_direction = -1;
        int nextR = 0, nextC = 0;
        for(int d = 0; d < 8; d++) {
            nextR = Rr + dr_dear[d];
            nextC = Rc + dc_dear[d];

            if(nextR < 0 || nextR >= N || nextC < 0 || nextC >= N) {
                continue;
            }

            int dist = getDistance(min_dist_santa.r, nextR, min_dist_santa.c, nextC);
            if(min_dist > dist) {
                min_dist = dist;
                min_direction = d;
            }
        }

        // 루돌프 이동
        map[Rr][Rc] = 0;
        nextR = Rr + dr_dear[min_direction];
        nextC = Rc + dc_dear[min_direction];

        // 산타와 충돌 시
        if(map[nextR][nextC] > 0) {
            // 루돌프가 밀고 들어온 방향으로 C 밀려나는 'map[nextR][nextC]'번 산타
            int Pn = map[nextR][nextC];

            // 산타 튕겨나감
            Santa santa = santaMap.get(Pn);
            santa.score += C;

            // 산타 기절
            santa.stunned = m + 1;
            int santaR = nextR + dr_dear[min_direction] * C;
            int santaC = nextC + dc_dear[min_direction] * C;

            interaction(map[nextR][nextC], santaR, santaC, dr_dear[min_direction], dc_dear[min_direction]);
        }

        map[nextR][nextC] = -1;
        Rr = nextR;
        Rc = nextC;
    }

    static boolean cmp(Santa q1, Santa q2) {
        if(q1.r == q2.r) {
            return q1.c > q2.c;
        }
        return q1.r > q2.r;
    }

    static int getDistance(int r1, int r2, int c1, int c2) {
        return (int)Math.pow(r1 - r2, 2) + (int)Math.pow(c1 - c2, 2);
    }

    static void move_santa() {

        for(int Pn : santaArr) {
            Santa santa = santaMap.get(Pn);

            if(santa.out) {
                continue;
            }

            if(santa.stunned >= m) {
                continue;
            }

            int min_dist = getDistance(santa.r, Rr ,santa.c, Rc);
            int min_direction = -1;
            int nextR = 0, nextC = 0;
            for(int d = 0; d < 4; d++) {
                nextR = santa.r + dr_santa[d];
                nextC = santa.c + dc_santa[d];

                if(nextR < 0 || nextR >= N || nextC < 0 || nextC >= N) {
                    continue;
                }
                // 이미 다른 산타가 있으면
                if(map[nextR][nextC] > 0) {
                    continue;
                }

                int dist = getDistance(nextR, Rr, nextC, Rc);
                if(min_dist > dist) {
                    min_dist = dist;
                    min_direction = d;
                }
            }

            if(min_direction == -1) {
                continue;
            }

            // 산타 이동
            map[santa.r][santa.c] = 0;
            nextR = santa.r + dr_santa[min_direction];
            nextC = santa.c + dc_santa[min_direction];

            // 루돌프와 충돌하면
            if(map[nextR][nextC] == -1) {
                santa.score += D;

                // min_direction 반대 방향으로 D만큼 튕겨나감
                int opposite_direction = (min_direction + 2) % 4;
                nextR += dr_santa[opposite_direction] * D;
                nextC += dc_santa[opposite_direction] * D;
                
                // 다음 라운드까지 santa 기절
                santa.stunned = m + 1;
                
                // nextR, nextC, direction에서 산타끼리 상호작용
                interaction(Pn, nextR, nextC, dr_santa[opposite_direction], dc_santa[opposite_direction]);
                
                // 맵 밖으로 벗어났다면 
                if(nextR < 0 || nextR >= N || nextC < 0 || nextC >= N) {
                    continue;
                }
            }

            santa.r = nextR;
            santa.c = nextC;
            map[nextR][nextC] = Pn;
        }
    }

    static private void interaction(int Pn, int nextR, int nextC, int dr, int dc) {
        // 맵 밖일 때
        if(nextR < 0 || nextR >= N || nextC < 0 || nextC >= N) {
            Santa santa = santaMap.get(Pn);
            santa.out = true;

            return;
        }
        // 빈칸 일때
        if(map[nextR][nextC] == 0) {
            map[nextR][nextC] = Pn;
            Santa santa = santaMap.get(Pn);
            santa.r = nextR;
            santa.c = nextC;
            
            return;
        }

        int nextPn = map[nextR][nextC];

        interaction(nextPn, nextR + dr, nextC + dc, dr, dc);

        map[nextR][nextC] = Pn;
        Santa santa = santaMap.get(Pn);
        santa.r = nextR;
        santa.c = nextC;
    }

    static private boolean adjustment() {
        boolean flag = false;

        for(int Pn : santaArr) {
            Santa santa = santaMap.get(Pn);

            if(!santa.out) {
                santa.score++;
                flag = true;
            }
        }
        return flag;
    }
}
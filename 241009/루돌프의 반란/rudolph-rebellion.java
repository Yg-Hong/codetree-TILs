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

            // for(int[] line : map) {
            //     System.out.println(Arrays.toString(line));
            // }
            // System.out.println();

            move_santa();
            
            // for(int[] line : map) {
            //     System.out.println(Arrays.toString(line));
            // }
            // System.out.println();

            flag = adjustment();
            // for(int Pn : santaArr) {
            //     Santa santa = santaMap.get(Pn);

            //     System.out.print(santa.score + " ");
            // }
            // System.out.println();
        }

        StringBuilder sb = new StringBuilder();
        for(int Pn : santaArr) {
            Santa santa = santaMap.get(Pn);

            sb.append(santa.score).append(" ");
        }

        System.out.println(sb);
    }

    static void move_dear() {
        // Santa min_dist_santa = santaMap.get(santaArr.get(0));
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
        // System.out.println("현재 루돌프와 가장 가까운 산타는 " + min_dist_santa.r + ", " + min_dist_santa.c + "에 위치");
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
        // System.out.println("루돌프 " + nextR + ", " + nextC + "로 이동");

        // 산타와 충돌 시
        if(map[nextR][nextC] > 0) {
            // 루돌프가 밀고 들어온 방향으로 C 밀려나는 'map[nextR][nextC]'번 산타
            int Pn = map[nextR][nextC];
            // System.out.println("날벼락!!!!! 루돌프의 이동으로 " + Pn + "번 산타 루돌프와 충돌!!!!");

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
                // System.out.println(Pn + "번 산타는 "+ santa.r + " " + santa.c + "에서 아파서 기절!");
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
                // System.out.println(Pn + "번 산타 " + dist + " " + nextR + " " + nextC);
                if(min_dist > dist) {
                    min_dist = dist;
                    min_direction = d;
                }
            }

            if(min_direction == -1) {
                // System.out.println(min_dist);
                // System.out.println(Pn + "번 산타 " + santa.r + ", " + santa.c + "에서 못움직임!!");
                continue;
            }

            // 산타 이동
            map[santa.r][santa.c] = 0;
            nextR = santa.r + dr_santa[min_direction];
            nextC = santa.c + dc_santa[min_direction];
            // System.out.println(Pn + "번 산타 " + nextR + ", " + nextC + "로 이동");

            // 루돌프와 충돌하면
            if(map[nextR][nextC] == -1) {
                santa.score += D;

                // min_direction 반대 방향으로 D만큼 튕겨나감
                int opposite_direction = (min_direction + 2) % 4;
                nextR += dr_santa[opposite_direction] * D;
                nextC += dc_santa[opposite_direction] * D;
                // System.out.println("쵸비상!!!! " + Pn + "번 산타 " + nextR + ", " + nextC + "로 이동");
                
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
            // System.out.println(Pn + "번 산타 아웃!!");
            // System.out.println(Pn +"번 산타 총 점수 " + santaMap.get(Pn).score + "로 경기 마무리!!!");
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
            // System.out.println("<>상호작용!!!! " + Pn + "번 산타 " + santa.r + ", " + santa.c + "로 이동");
            return;
        }

        int nextPn = map[nextR][nextC];
        
        // System.out.println("상호작용!!!! " + Pn + "번 산타 " + nextR + ", " + nextC + "로 이동");

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
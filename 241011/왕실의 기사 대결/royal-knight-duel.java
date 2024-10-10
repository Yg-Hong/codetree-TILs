import java.io.*;
import java.util.*;

public class Main {
    static int L, N, Q;
    static int[][] map;

    static class Knight {
        int r;
        int c;
        int h;
        int w;
        int k;

        boolean out = false;
        int damage = 0;

        Knight(int r, int c, int h, int w, int k) {
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
        }
    }

    static Map<Integer, Knight> knightMap = new HashMap<>();
    static ArrayList<Integer> knightArr = new ArrayList<>();

    static int[] dy = {-1,0,1,0};
    static int[] dx = {0,1,0,-1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        map = new int[L + 1][L + 1];
        for(int y = 1; y <= L; y++) {
            st = new StringTokenizer(br.readLine());
            for(int x = 1; x <= L; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }

        for(int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());

            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());

            knightMap.put(i, new Knight(r, c, h, w, k));
            knightArr.add(i);
        }

        while(Q-- > 0) {
            st = new StringTokenizer(br.readLine());

            int i = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());

            push(i , d);
            // printAll();
        }

        int result = 0;
        for(int id : knightArr) {
            Knight knight = knightMap.get(id);

            if(!knight.out) {
                result += knight.damage;
            }
        }
        System.out.println(result);
    }

    static void push(int id, int d) {
        int[][] temp = init();

        Knight knight = knightMap.get(id);
        if(knight.out) {
            return;
        }

        ArrayList<int[]> posArr = getPosArr(id, d);
        Queue<int[]> pos = new ArrayDeque<>();
        pos.addAll(posArr);

        // pos에서 하나씩 꺼내서 d 방향으로 한칸씩 이동할 거임.
        // 이 때 만나는 id를 Set에 저장
        // 벽을 만나면 움직일 수 없음
        Set<Integer> knightSet = new HashSet<>();
        boolean enableToMove = true;
        while(!pos.isEmpty()) {
            int[] now = pos.poll();
            int nextY = now[0] + dy[d];
            int nextX = now[1] + dx[d];
            
            // System.out.println("nextY : " + nextY + "|| nextX : " + nextX);

            // 벽을 만나면
            if(nextY <= 0 || nextY > L || nextX <= 0 || nextX > L) {
                // System.out.println("<>");
                enableToMove = false;
                break;
            }
            if(map[nextY][nextX] == 2) {
                // System.out.println("<><><>");
                enableToMove = false;
                break;
            }

            if(temp[nextY][nextX] > 0 && !knightSet.contains(temp[nextY][nextX])) {
                knightSet.add(temp[nextY][nextX]);
                posArr = getPosArr(temp[nextY][nextX], d);
                pos.addAll(posArr);
                // System.out.println("추가!!! " + posArr.size());
                // for(int[] yx : posArr) {
                //     System.out.println("   + " + Arrays.toString(yx));
                // }
            }
        }

        // System.out.println("knightSET : " + knightSet);
        // 움직일 수 없다면 바로 종료
        if(!enableToMove) {
            return;
        }

        moveKnights(knight, knightSet, d);
    }

    static ArrayList<int[]> getPosArr(int id, int d) {
        Knight knight = knightMap.get(id);

        // 움직일 방향의 벽면 좌표 가져오기
        ArrayList<int[]> pos = new ArrayList<>();
        if(d == 0) {
            for(int i = 0; i < knight.w; i++) {
                pos.add(new int[] {knight.r, knight.c + i});
            }
        } else if(d == 1) {
            for(int i = 0; i < knight.h; i++) {
                pos.add(new int[] {knight.r + i, knight.c + (knight.w - 1)});
            }
        } else if(d == 2) {
            for(int i = 0; i < knight.w; i++) {
                pos.add(new int[] {knight.r + (knight.h - 1), knight.c + i});
            }
        } else if(d == 3) {
            for(int i = 0; i < knight.h; i++) {
                pos.add(new int[] {knight.r + i, knight.c});
            }
        }

        return pos;
    }

    static int[][] init() {
        int[][] temp = new int[L + 1][L + 1];

        for(int id : knightArr) {
            Knight knight = knightMap.get(id);

            if(knight.out) {
                continue;
            }

            for(int y = 0; y < knight.h; y++) {
                for(int x = 0; x < knight.w; x++) {
                    temp[knight.r + y][knight.c + x] = id;
                }
            }
        }

        // for(int[] line : temp) {
        //     System.out.println(Arrays.toString(line));
        // }

        return temp;
    }

    static void moveKnights(Knight knight, Set<Integer> knightSet, int d) {
        knight.r += dy[d];
        knight.c += dx[d];

        for(int movedKnightid : knightSet) {
            Knight movedKnight = knightMap.get(movedKnightid);

            movedKnight.r += dy[d];
            movedKnight.c += dx[d];

            int damaged = 0;
            for(int y = 0; y < movedKnight.h; y++) {
                for(int x = 0; x < movedKnight.w; x++) {
                    // 밀려서 밟고 있는 함정의 수
                    if(map[movedKnight.r + y][movedKnight.c + x] == 1) {
                        damaged++;
                    }
                }
            }

            movedKnight.damage += damaged;
            movedKnight.k -= damaged;
            if(movedKnight.k <= 0) {
                movedKnight.out = true;
            }
        }
    }

    static void printAll() {
        for(int id : knightArr) {
            Knight knight = knightMap.get(id);

            System.out.println(id + "번 기사의 체력은 " + knight.k + ", 받은 데미지는 " + knight.damage + ", 현재 상태는 " + knight.out);
        }
    }
}
import java.util.*;
import java.io.*;

public class Main {

    static int L, Q;
    static class Query {
        public int cmd, t, x, n;
        public String name;

        public Query(int cmd, int t, int x, String name, int n) {
            this.cmd = cmd;
            this.t = t;
            this.x = x;
            this.name = name;
            this.n = n;
        }
    }

    // 명령 관리
    public static List<Query> queries = new ArrayList<>();

    // 사람 관리
    public static Set<String> names = new HashSet<>();

    // 각 사람마다 주어진 명령 관리
    public static Map<String, List<Query>> p_queries = new HashMap<>();

    // 각 사람마다 입장 시간 관리
    public static Map<String, Integer> entry_time = new HashMap<>();

    // 각 손님 위치 관리
    public static Map<String, Integer> position = new HashMap<>();

    // 퇴장 시간 관리
    public static Map<String, Integer> exit_time = new HashMap<>();

    public static boolean cmp(Query q1, Query q2) {
        if(q1.t != q2.t)
            return q1.t < q2.t;
        return q1.cmd < q2.cmd;
    }

    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        int command;
        while(Q-- > 0) {
            int t = -1;
            int x = -1;
            int n = -1;
            String name = "";
            st = new StringTokenizer(br.readLine());

            command = Integer.parseInt(st.nextToken());

            switch(command) {
                case 100:
                    t = Integer.parseInt(st.nextToken());
                    x = Integer.parseInt(st.nextToken());
                    name = st.nextToken();

                    break;
                case 200:
                    t = Integer.parseInt(st.nextToken());
                    x = Integer.parseInt(st.nextToken());
                    name = st.nextToken();
                    n = Integer.parseInt(st.nextToken());

                    break;
                case 300:
                    t = Integer.parseInt(st.nextToken());

                    break;
            }

            queries.add(new Query(command, t, x, name, n));

            // 사람별 주어진 초밥 목록 관리
            if(command == 100) {
                p_queries.computeIfAbsent(name, k -> new ArrayList<>()).add(new Query(command, t, x, name, n));
            } 
            // 손님 입장 시간 및 위치 관리
            else if(command == 200) {
                names.add(name);
                entry_time.put(name, t);
                position.put(name, x);
            }
        }

        // 각 사람마다 자신의 이름이 적힌 조합을 언제 먹게 되는지 계산
        // 해당 정보를 기존 Query에 추가
        for(String name : names) {
            // 해당 사람 퇴장시간 관리
            exit_time.put(name, 0);

            for(Query q : p_queries.get(name)) {
                // 만약 초밥이 사람이 등장하기 전에 미리 주어진 상황이면
                int time_to_removed = 0;
                if(q.t < entry_time.get(name)) {
                    // entry_time 때의 스시 위치를 계산
                    int t_sushi_x = (q.x + (entry_time.get(name) - q.t)) % L;
                    // 몇 초가 더 지나야 만나는지 계산
                    int additional_time = (position.get(name) - t_sushi_x + L) % L;

                    time_to_removed = entry_time.get(name) + additional_time;
                }
                // 초밥이 사람이 등장한 이후에 주어졌다면
                else {
                    // 몇 초가 더 지나야 만나는지 계산
                    int additional_time = (position.get(name) - q.x + L) % L;
                    time_to_removed = q.t + additional_time;
                }

                // 초밥이 사라지는 시간 중 가장 늦은 시간을 업데이트
                exit_time.put(name, Math.max(exit_time.get(name), time_to_removed));

                // 초밥이 사라지는 111번 쿼리를 추가
                queries.add(new Query(111, time_to_removed, -1, name, -1));
            }
        }

        // 사람마다 초밥을 마지막으로 먹은 시간 t를 계산하여 그 사람이 해당 t 떄 오마카세를 떠났음을 쿼리로 저장
        for(String name : names) {
            queries.add(new Query(222, exit_time.get(name), -1, name, -1));
        }

        // 전체 Query를 시간순으로 정렬하되 t가 일치한다면 문제 조건상 사진 촬영에 해당하는 300이 가장 늦게 실행
        // 이후 순서대로 보면서 사람, 초밥 수를 count
        queries.sort((q1, q2)-> cmp(q1, q2) ? -1 : 1);

        int people_num = 0;
        int sushi_num = 0;
        for(Query query: queries) {
            // System.out.println("query cmd : " + query.cmd + " | query t : " + query.t);
            if(query.cmd == 100)
                sushi_num++;
            else if(query.cmd == 111)
                sushi_num--;
            else if(query.cmd == 200)
                people_num++;
            else if(query.cmd == 222)
                people_num--;
            else
                sb.append(people_num).append(" ").append(sushi_num).append("\n");
        }

        System.out.println(sb);
    }
}
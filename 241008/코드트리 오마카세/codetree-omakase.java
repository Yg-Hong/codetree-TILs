import java.util.*;
import java.io.*;

public class Main {

    static class Customer {
        int t;
        int x;
        String name;
        int n;
        int eaten = 0;

        Customer(int t, int x, String name, int n) {
            this.t = t;
            this.x = x;
            this.name = name;
            this.n = n;
        }
    }

    static class Sushi implements Comparable<Sushi> {
        int t;
        int x;
        String name;

        Sushi(int t, int x, String name) {
            this.t = t;
            this.x = x;
            this.name = name;
        }

        @Override
        public int compareTo(Sushi o) {
            return t - o.t;
        }
    }

    static PriorityQueue<Sushi> belt = new PriorityQueue<>();
    static Map<String, Customer> customerMap = new HashMap<>();
    static int L, Q;

    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        int t, x, n;
        String name;
        int prevPhotoTime = 0;
        while(Q-- > 0) {
            st = new StringTokenizer(br.readLine());

            int command = Integer.parseInt(st.nextToken());

            switch(command) {
                case 100:
                    t = Integer.parseInt(st.nextToken());
                    x = Integer.parseInt(st.nextToken());
                    name = st.nextToken();

                    makeSushi(t, x, name);
                    break;
                case 200:
                    t = Integer.parseInt(st.nextToken());
                    x = Integer.parseInt(st.nextToken());
                    name = st.nextToken();
                    n = Integer.parseInt(st.nextToken());

                    customerEnter(t, x, name, n);
                    break;
                case 300:
                    t = Integer.parseInt(st.nextToken());

                    takePhoto(t);
                    prevPhotoTime = t;
                    break;
            }
        }

        System.out.println(sb);
    }

    static private void makeSushi(int t, int x, String name) {
        belt.add(new Sushi(t, x, name));
    }

    static private void customerEnter(int t, int x, String name, int n) {
        customerMap.put(name, new Customer(t, x, name, n));
    }

    static private void takePhoto(int t) {
        // prevPhotoTime ~ t 까지 정산
        PriorityQueue<Sushi> temp = new PriorityQueue<>();

        String name;
        while(!belt.isEmpty()) {
            Sushi now = belt.poll();

            name = now.name;
            if(customerMap.get(name) == null) {
                temp.add(now);
                continue;
            } 

            Customer customer = customerMap.get(name);
            // 손님 위치 기준으로 생각하자. 손님은 고정!!
            // 손님이 앉았을 때 초밥 위치
            int sushiPosWhenSit = (now.x + (customer.t - now.t)) % L;
            int sushiWhenSit = now.x + (customer.t - now.t);
            if (customer.t - now.t <= 0) {
                sushiPosWhenSit = now.x;
                sushiWhenSit = 0;
            }

            // 현재(t초) 초밥 위치
            int sushiPosWhenNow = (now.x + (t - now.t)) % L;
            int sushiWhenNow = now.x + (t - now.t);
            // general : 손님이 앉았을 때 초밥이 앞에 있고 지금(t초에) 뒤에 있으면 먹은 것.
            if(sushiPosWhenSit <= customer.x && sushiPosWhenNow >= customer.x) {
                eat(customer);
                continue;
            }
            // 자리가 겹치면 먹은 것.
            if(sushiPosWhenNow == customer.x || sushiPosWhenSit == customer.x) {
                eat(customer);
                continue;
            }
            // 한 바퀴 돌았으면 무조건 먹음
            if(sushiWhenNow - sushiWhenSit >= L) {
        
                eat(customer);
                continue;
            }

            // 단 한 바퀴 돌았을 떈 무조건 먹은 것
            // else if(now.x + now.t >= L) {
            //     eat(customer);
            //     continue;
            // }
            
            // 조건에 안맞았으면 다시 넣어야함.
            temp.add(now);
        }

        belt.addAll(temp);
        // System.out.println(customerMap.size() + " " + belt.size());
        sb.append(customerMap.size()).append(" ").append(belt.size()).append("\n");
    }

    static private void eat(Customer customer) {
        customer.eaten++;
        
        // System.out.println("Customer " + customer.name + "가 초밥을 먹음. 현재 n : " + customer.n + " | 현재 eaten : " + customer.eaten);

        if(customer.n <= customer.eaten) {
            customerMap.remove(customer.name);
        }
    }
}
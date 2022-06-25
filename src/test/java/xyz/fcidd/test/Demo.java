package xyz.fcidd.test;

public class Demo {
    public static void main(String[] args) {
        String config="!!";
        String message="!!qwq";
        int configLength = config.length();
        String substring = message.substring(0, configLength);
        if (substring.equals(config)){
            System.out.println("此消息已被放行");
        }
    }
}

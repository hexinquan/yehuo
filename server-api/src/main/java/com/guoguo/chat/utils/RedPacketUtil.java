package com.guoguo.chat.utils;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class RedPacketUtil {

    private static final int MIN_MONEY = 1;
    private static final int LESS = -1;
    private static final int OK = 1;
    private static final double TIMES = 1.1F;
    private int recursiveCount = 0;

    public List<Integer> splitRedPacket(int money, int count) {
        List<Integer> moneys = new LinkedList<>();
        int max = (int) ((money / count) * TIMES);
        for (int i = 0; i < count; i++) {
            int redPacket = randomRedPacket(money, MIN_MONEY, max, count - i);
            moneys.add(redPacket);
            money -= redPacket;
        }
        return moneys;
    }

    private int randomRedPacket(int totalMoney, int minMoney, int maxMoney, int count) {
        if (count == 1) {
            return totalMoney;
        }
        if (minMoney == maxMoney) {
            return minMoney;
        }
        maxMoney = maxMoney > totalMoney ? totalMoney : maxMoney;
        int redPacket = (int) (Math.random() * (maxMoney - minMoney) + minMoney);
        int lastMoney = totalMoney - redPacket;
        int status = checkMoney(lastMoney, count - 1);
        if (OK == status) {
            return redPacket;
        }
        if (LESS == status) {
            recursiveCount++;
            return randomRedPacket(totalMoney, minMoney, redPacket, count);
        }
        return redPacket;
    }

    /**
     * 校验剩余的金额的平均值是否在 最小值和最大值这个范围内
     *
     * @param lastMoney
     * @param count
     * @return
     */
    private int checkMoney(int lastMoney, int count) {
        double avg = lastMoney / count;
        if (avg < MIN_MONEY) {
            return LESS;
        }
        return OK;
    }


    public static void main(String[] args) {
        RedPacketUtil redPacket = new RedPacketUtil();
        List<Integer> redPackets = redPacket.splitRedPacket(400, 1);
        System.out.println("个数:".concat(String.valueOf(redPackets.size())));
        int sum = 0;
        for (Integer red : redPackets) {
            System.out.println(red);
            sum += red;
        }
        System.out.println("总额:"+sum);
    }

}

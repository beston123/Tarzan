package server;

import com.tongbanjie.tarzan.common.WheelTask;
import com.tongbanjie.tarzan.common.WheelTimer;
import com.tongbanjie.tarzan.common.util.DateUtils;

import java.util.Date;
import java.util.Random;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/5
 */
public class TestWheelTimer extends WheelTimer<String> {

    public static final int[] delayLevels = new int[18];

    static {
        delayLevels[0] = 1;
        delayLevels[1] = 5;
        delayLevels[2] = 10;
        delayLevels[3] = 30;
        delayLevels[4] = 1 * 60;
        delayLevels[5] = 2 * 60;
        delayLevels[6] = 3 * 60;
        delayLevels[7] = 4 * 60;
        delayLevels[8] = 5 * 60;
        delayLevels[9] = 6 * 60;
        delayLevels[10] = 7 * 60;
        delayLevels[11] = 8 * 60;
        delayLevels[12] = 9 * 60;
        delayLevels[13] = 10 * 60;
        delayLevels[14] = 20 * 60;
        delayLevels[15] = 30 * 60;
        delayLevels[16] = 60 * 60;
        delayLevels[17] = 120 * 60;
    }

    public TestWheelTimer(int slotNum) {
        super(slotNum);
    }

    @Override
    protected void doTask(WheelTask<String> task) {
        System.out.println(DateUtils.format(new Date(), "yyyyMMdd HH:mm:ss") + " >>> " + task);
        if(!new Random().nextBoolean()){
            addTask(task.getTask(), delayLevels[1]);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestWheelTimer wheelTimer = new TestWheelTimer(10);
        wheelTimer.start();

        System.out.println("start time : " + DateUtils.format(new Date(), "yyyyMMdd HH:mm:ss") + ", currentIndex:" + wheelTimer.getCurrentIndex());
        for(int i=1; i<=30; i++){
            Thread.sleep(1L);
            wheelTimer.addTask("x_"+i, delayLevels[0]*i);
        }

    }
}

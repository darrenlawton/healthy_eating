package healthyeating;

import java.util.Date;

/**
 * Created by darren on 4/6/17.
 */

public class DailyLog {
    //Private variables
    String logDate;
    int isHealthDay; //1 is healthy, 0 is otherwise
    String reasonNonHealth;
    int consecHealthDays;

    public DailyLog(String logDate, int isHealthDay, int ConsecDays, String reasonNonHealth){
        this.logDate = logDate;
        this.isHealthDay = isHealthDay;
        this.reasonNonHealth = reasonNonHealth;
        this.consecHealthDays = ConsecDays;
    }
}

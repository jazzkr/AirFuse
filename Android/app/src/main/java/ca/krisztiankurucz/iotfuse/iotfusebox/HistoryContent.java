package ca.krisztiankurucz.iotfuse.iotfusebox;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class HistoryContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<HistoryItem> ITEMS = new ArrayList<HistoryItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, HistoryItem> ITEM_MAP = new HashMap<>();

    //private static final int COUNT = 25;

    public static void addItem(HistoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.date, item);
    }

    public static void clear()
    {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class HistoryItem {
        public final String date;
        public final String message;
        public final String details;
        public final int fuse;
        public final Date d;

        public HistoryItem(String date, String message, String details, int fuse) throws Exception {

            String raw_date = date;
            String raw_action = message;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            Date date_obj = format.parse(raw_date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date_obj);
            cal.add(Calendar.HOUR_OF_DAY, -4);
            this.d = cal.getTime();
            DateFormat df = new SimpleDateFormat("dd/MM/yy h:mm:ss aa");

            this.date = df.format(d);

            FuseObject fo = MainActivity.getFuseById(fuse);

            if (raw_action.equals("trip"))
            {
                this.message = fo.name + " has been tripped by user.";
            }
            else if (raw_action.equals("reset"))
            {
                this.message = fo.name + " has been reset by user.";
            }
            else if (raw_action.equals("tripped"))
            {
                this.message = fo.name + " has tripped due to an overcurrent event!";
            }
            else if (raw_action.equals("good"))
            {
                this.message  = fo.name + " status is good.";
            }
            else {
                this.message = raw_action;
            }
            this.details = details;
            this.fuse = fuse;

        }

        @Override
        public String toString() {
            return message;
        }
    }
}

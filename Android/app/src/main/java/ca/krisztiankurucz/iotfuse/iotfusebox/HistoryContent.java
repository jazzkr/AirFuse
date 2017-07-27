package ca.krisztiankurucz.iotfuse.iotfusebox;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static Map<String, HistoryItem> ITEM_MAP = new HashMap<String, HistoryItem>();

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

        public HistoryItem(String date, String message, String details, int fuse) {
            String raw_date = date;
            String raw_action = message;

            //Todo: convert the raw date into human readable
            this.date = raw_date;
            this.message = raw_action;
            this.details = details;
            this.fuse = fuse;


        }

        @Override
        public String toString() {
            return message;
        }
    }
}

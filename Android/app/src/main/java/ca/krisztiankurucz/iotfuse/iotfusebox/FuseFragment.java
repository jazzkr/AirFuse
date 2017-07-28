package ca.krisztiankurucz.iotfuse.iotfusebox;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FuseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FuseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FuseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FuseObject fuse;

    private OnFragmentInteractionListener mListener;

    public FuseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FuseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FuseFragment newInstance(String param1, String param2) {
        FuseFragment fragment = new FuseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fuse, container, false);
        ((TextView)view.findViewById(R.id.fuse_chart_name)).setText(getArguments().getString("graph_title"));

        // in this example, a LineChart is initialized from xml
        LineChart chart = view.findViewById(R.id.fuse_chart);
        chart.setDrawGridBackground(true);
        chart.setDrawBorders(false);
        YAxis left = chart.getAxisLeft();
        left.setDrawGridLines(true);

        //Disable right axis
        YAxis right = chart.getAxisRight();
        right.setEnabled(false);

        //Disable description
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        //Disable top axis
        XAxis xaxis = chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawGridLines(false);
        xaxis.setLabelRotationAngle(-45);
        xaxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long epoch = (long) (value + 20000000) * 60 * 1000;
                //System.out.println("Value: " + value + " Epoch: " + epoch);
                Date date = new Date(epoch);
                DateFormat df = new SimpleDateFormat("dd/MM/yy H:mm");
                //System.out.println(value + " = " + df.format(date));

                return df.format(date);
            }
        });

        fuse = MainActivity.fuse_map.get(getArguments().getString("fuse_name"));
        System.out.println("Loaded fuse: " + fuse.name);

        // Get fuse information
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://django.utkarshsaini.com/AirFuse/fuseCurrentReading/" + Integer.toString(fuse.id);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("[HTTPGET] Response is: "+ response);
                        try
                        {
                            JSONArray fuse_readings = new JSONArray(response);
                            List<Long> times = new ArrayList<Long>();
                            List<Double> currents = new ArrayList<Double>();
                            for (int i = 0; i < fuse_readings.length(); i++)
                            {
                                JSONObject reading = fuse_readings.getJSONObject(i);
                                String raw_date = reading.getString("created_at");
                                double current = reading.getDouble("current");
                                //Parse the timestamp to convert to unix epoch
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                                Date date = format.parse(raw_date);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                cal.add(Calendar.HOUR_OF_DAY, -4);
                                date = cal.getTime();
                                long inMinutes = (date.getTime()/1000)/60;
                                times.add(inMinutes);
                                currents.add(current);
                                System.out.println(Long.toString(inMinutes) + ", " + Double.toString(current));
                            }
                            TreeMap<Long,ArrayList<Double>> collector = new TreeMap<>();
                            for (int i = 0; i < times.size(); i++)
                            {
                                long t = times.get(i);
                                double c = currents.get(i);
                                if (collector.containsKey(t))
                                {
                                    collector.get(t).add(currents.get(i));
                                } else
                                {
                                    ArrayList<Double> currList = new ArrayList<>();
                                    currList.add(c);
                                    collector.put(t, currList);
                                }
                            }
                            System.out.println(collector.toString());
                            //Go through hashmap, average, and add to entry
                            ArrayList<Entry> entries = new ArrayList<>();
                            for (long key: collector.keySet())
                            {
                                double avg_current = 0;
                                ArrayList<Double> currList = collector.get(key);
                                for (double c: currList)
                                {
                                    avg_current += c;
                                }
                                avg_current = avg_current / currList.size();
                                //HACKY FIX HERE
                                long passKey = key - 20000000;
                                entries.add(new Entry((float)passKey,(float)avg_current, key));
                            }

                            LineDataSet dataSet = new LineDataSet(entries, "Current (A)");
                            dataSet.setColor(Color.GREEN);
                            dataSet.setCircleColor(Color.DKGRAY);
                            dataSet.setDrawValues(false);
                            dataSet.setDrawCircles(false);
                            dataSet.setLineWidth(3);
                            LineData lineData = new LineData(dataSet);
                            LineChart chart = getView().findViewById(R.id.fuse_chart);
                            chart.setData(lineData);
                            chart.invalidate();

                        } catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
                //mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        String fuse_name = getArguments().getString("fuse_name");
        //Update fuse status as appropriate
        for (String sf: MainActivity.fuse_map.keySet())
        {
            System.out.println("Checking " + sf + " against " + fuse_name);
            if(getArguments().getString("fuse_name").equals(sf))
            {
                TextView status = view.findViewById(R.id.fuse_status);
                if (MainActivity.fuse_map.get(sf).status.equals("good"))
                {
                    status.setText("GOOD");
                    status.setTextColor(Color.GREEN);
                }
                else
                {
                    status.setText("TRIPPED");
                    status.setTextColor(Color.RED);
                }
            }
        }

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFuseFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFuseFragmentInteraction(Uri uri);
    }
}



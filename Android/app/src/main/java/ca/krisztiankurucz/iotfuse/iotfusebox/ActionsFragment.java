package ca.krisztiankurucz.iotfuse.iotfusebox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ActionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActionsFragment newInstance(String param1, String param2) {
        ActionsFragment fragment = new ActionsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_actions, container, false);

        final Button reset_1 = (Button) view.findViewById(R.id.reset_button_1);
        final Button reset_2 = (Button) view.findViewById(R.id.reset_button_2);
        final Button reset_3 = (Button) view.findViewById(R.id.reset_button_3);
        final Button trip_1 = (Button) view.findViewById(R.id.trip_button_1);
        final Button trip_2 = (Button) view.findViewById(R.id.trip_button_2);
        final Button trip_3 = (Button) view.findViewById(R.id.trip_button_3);

        reset_1.setEnabled(false);
        reset_2.setEnabled(false);
        reset_3.setEnabled(false);

        refreshActionFragment(view);

        reset_1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_1.setEnabled(false);
                trip_1.setEnabled(true);
                refreshActionFragment();
            }
        });
        reset_2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_2.setEnabled(false);
                trip_2.setEnabled(true);
                refreshActionFragment();
            }
        });
        reset_3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_3.setEnabled(false);
                trip_3.setEnabled(true);
                refreshActionFragment();
            }
        });
        trip_1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_1.setEnabled(true);
                trip_1.setEnabled(false);
                refreshActionFragment();
            }
        });
        trip_2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_2.setEnabled(true);
                trip_2.setEnabled(false);
                refreshActionFragment();
            }
        });
        trip_3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_3.setEnabled(true);
                trip_3.setEnabled(false);
                refreshActionFragment();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onActionsFragmentInteraction(uri);
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

    public void refreshActionFragment(View view)
    {
        // Get statuses of all three fuses, update UI accordingly
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        for (String s: MainActivity.fuse_map.keySet()) {
            FuseObject current_fuse = MainActivity.fuse_map.get(s);
            // Request a string response from the provided URL.
            String url = "http://django.utkarshsaini.com/AirFuse/fuseStatus/" + current_fuse.id;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("[HTTPGET] Response is: " + response);
                            try {
                                JSONArray user_actions = new JSONArray(response);
                                TreeMap<long, String> sorted_status = new TreeMap<>();
                                int fuse_id = 0;
                                for (int i = 0; i < user_actions.length(); i++) {
                                    JSONObject action = user_actions.getJSONObject(i);
                                    String raw_date = action.getString("created_at");
                                    String raw_status = action.getString("status");
                                    fuse = action.getInt("fuse");
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                                    Date date = format.parse(raw_date);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);
                                    cal.add(Calendar.HOUR_OF_DAY, -4);
                                    date = cal.getTime();
                                    sorted_status.put(date.getTime(),raw_status);
                                }

                                String fuse_status = sorted_status.lastEntry().getValue();

                                TextView status;
                                for (String key: MainActivity.fuse_map.keySet())
                                {
                                    FuseObject fo = MainActivity.fuse_map.get(key);
                                    if (fo.id == fuse_id)
                                    {
                                        //Some hardcoding here...
                                        if (fo.name.equals("Fuse 1")) {
;
                                        } else if(fo.name.equals("Fuse 2")) {

                                        }
                                    }
                                }

                                if (fuse_status.equals("good")) {

                                } else {

                                }

                            } catch (Exception e) {
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
        }
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
        // TODO: Update argument type and name
        void onActionsFragmentInteraction(Uri uri);
    }
}

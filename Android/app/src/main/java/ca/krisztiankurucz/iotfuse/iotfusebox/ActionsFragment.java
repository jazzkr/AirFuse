package ca.krisztiankurucz.iotfuse.iotfusebox;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;
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

        for (String sf: MainActivity.fuse_map.keySet())
        {
            FuseObject fo = MainActivity.fuse_map.get(sf);
           ((MainActivity) getActivity()).getFuseStatus(fo);
        }
        refreshActionFragment(view);

        reset_1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_1.setEnabled(false);
                trip_1.setEnabled(false);
                FuseObject fo = MainActivity.getFuseByName("Fuse 1");
                sendFuseAction(fo, "reset");
            }
        });
        reset_2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_2.setEnabled(false);
                trip_2.setEnabled(false);
                FuseObject fo = MainActivity.getFuseByName("Fuse 2");
                sendFuseAction(fo, "reset");
            }
        });
        reset_3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_3.setEnabled(false);
                trip_3.setEnabled(false);
                FuseObject fo = MainActivity.getFuseByName("Fuse 3");
                sendFuseAction(fo, "reset");
            }
        });
        trip_1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_1.setEnabled(false);
                trip_1.setEnabled(false);
                FuseObject fo = MainActivity.getFuseByName("Fuse 1");
                sendFuseAction(fo, "trip");
            }
        });
        trip_2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_2.setEnabled(false);
                trip_2.setEnabled(false);
                FuseObject fo = MainActivity.getFuseByName("Fuse 2");
                sendFuseAction(fo, "trip");
            }
        });
        trip_3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reset_3.setEnabled(false);
                trip_3.setEnabled(false);
                FuseObject fo = MainActivity.getFuseByName("Fuse 3");
                sendFuseAction(fo, "trip");
            }
        });

        return view;
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
        //Slightly hardcoded since the view is also hardcoded for now
        for (String sf: MainActivity.fuse_map.keySet())
        {
            FuseObject fo = MainActivity.fuse_map.get(sf);
            if (fo.name.equals("Fuse 1"))
            {
                Button trip_1 = view.findViewById(R.id.trip_button_1);
                Button reset_1 = view.findViewById(R.id.reset_button_1);
                TextView status_1 = view.findViewById(R.id.status_1);
                if(fo.status.equals("good"))
                {
                    trip_1.setEnabled(true);
                    reset_1.setEnabled(false);
                    status_1.setText("GOOD");
                    status_1.setTextColor(Color.GREEN);
                }
                else
                {
                    trip_1.setEnabled(false);
                    reset_1.setEnabled(true);
                    status_1.setText("TRIPPED");
                    status_1.setTextColor(Color.RED);
                }

            } else if (fo.name.equals("Fuse 2"))
            {
                Button trip_2 = view.findViewById(R.id.trip_button_2);
                Button reset_2 = view.findViewById(R.id.reset_button_2);
                TextView status_2 = view.findViewById(R.id.status_2);
                if(fo.status.equals("good"))
                {
                    trip_2.setEnabled(true);
                    reset_2.setEnabled(false);
                    status_2.setText("GOOD");
                    status_2.setTextColor(Color.GREEN);
                }
                else
                {
                    trip_2.setEnabled(false);
                    reset_2.setEnabled(true);
                    status_2.setText("TRIPPED");
                    status_2.setTextColor(Color.RED);
                }
            } else if (fo.name.equals("Fuse 3"))
            {
                Button trip_3 = view.findViewById(R.id.trip_button_3);
                Button reset_3 = view.findViewById(R.id.reset_button_3);
                TextView status_3 = view.findViewById(R.id.status_3);
                if(fo.status.equals("good"))
                {
                    trip_3.setEnabled(true);
                    reset_3.setEnabled(false);
                    status_3.setText("GOOD");
                    status_3.setTextColor(Color.GREEN);
                }
                else
                {
                    trip_3.setEnabled(false);
                    reset_3.setEnabled(true);
                    status_3.setText("TRIPPED");
                    status_3.setTextColor(Color.RED);
                }
            }
        }
    }

    public void sendFuseAction(FuseObject fo, final String action)
    {
        //First check if fuse action is already sent, but not executed yet
        final int fuseid = fo.id;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        //url to get only non-executed commands from API
        String url = "http://django.utkarshsaini.com/AirFuse/fuseUserActions2/";
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("[HTTPGET] " + response);

                        try
                        {
                            JSONArray unexecuted_actions = new JSONArray(response);
                            for(int i = 0; i < unexecuted_actions.length(); i++)
                            {
                                JSONObject action = unexecuted_actions.getJSONObject(i);
                                if (action.getInt("fuse") == fuseid && action.getString("action").equals(action))
                                {
                                    Context context = getContext();
                                    CharSequence text = "Action already queued!";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                    return;
                                }
                            }
                            //if we got here, we're good to post
                            postFuseAction(fuseid, action);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println("That didn't work!");
                        error.printStackTrace();
                    }
                }
        );
        queue.add(getRequest);
    }

    public void postFuseAction(final int fuseid, final String action)
    {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://django.utkarshsaini.com/AirFuse/fuseUserActions/";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println("Sent fuse action: " + action + " to " + fuseid);
                        System.out.println("[HTTPPOST] " + response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println("That didn't work!");
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("fuse", Integer.toString(fuseid));
                params.put("action", action);
                return params;
            }
        };
        queue.add(postRequest);
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

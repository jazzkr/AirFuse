package ca.krisztiankurucz.iotfuse.iotfusebox;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import ca.krisztiankurucz.iotfuse.iotfusebox.HistoryContent.HistoryItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistoryTestFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryTestFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HistoryTestFragment newInstance(int columnCount) {
        HistoryTestFragment fragment = new HistoryTestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        //Discard old history content
        HistoryContent.clear();

        // Load fuse information for later
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        for (String s: MainActivity.fuse_map.keySet()) {
            FuseObject current_fuse = MainActivity.fuse_map.get(s);
            String url_1 = "http://django.utkarshsaini.com/AirFuse/fuseUserActions/" + current_fuse.id;
            String url_2 = "http://django.utkarshsaini.com/AirFuse/fuseStatus/" + current_fuse.id;
            System.out.println(url_1);
            System.out.println(url_2);
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_1,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            System.out.println("[HTTPGET] Response is: "+ response);
                            try
                            {
                                JSONArray user_actions = new JSONArray(response);
                                for (int i = 0; i < user_actions.length(); i++)
                                {
                                    JSONObject action = user_actions.getJSONObject(i);
                                    String raw_date = action.getString("created_at");
                                    String raw_action = action.getString("action");
                                    int fuse = action.getInt("fuse");
                                    HistoryItem item = new HistoryItem(raw_date, raw_action, raw_action, fuse);
                                    HistoryContent.addItem(item);
                                }

                                Collections.sort(HistoryContent.ITEMS, new Comparator<HistoryItem>() {
                                    public int compare(HistoryItem o1, HistoryItem o2) {
                                        return o2.d.compareTo(o1.d);
                                    }
                                });
                                // Set the adapter
                                RecyclerView recyclerView = (RecyclerView) getView();
                                recyclerView.getAdapter().notifyDataSetChanged();

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
            // Request a string response from the provided URL.
            StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url_2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            System.out.println("[HTTPGET] Response is: "+ response);
                            try
                            {
                                JSONArray user_actions = new JSONArray(response);
                                for (int i = 0; i < user_actions.length(); i++)
                                {
                                    JSONObject action = user_actions.getJSONObject(i);
                                    String raw_date = action.getString("created_at");
                                    String raw_status = action.getString("status");
                                    int fuse = action.getInt("fuse");
                                    HistoryItem item = new HistoryItem(raw_date, raw_status, raw_status, fuse);
                                    HistoryContent.addItem(item);
                                }

                                Collections.sort(HistoryContent.ITEMS, new Comparator<HistoryItem>() {
                                    public int compare(HistoryItem o1, HistoryItem o2) {
                                        return o2.d.compareTo(o1.d);
                                    }
                                });
                                // Set the adapter
                                RecyclerView recyclerView = (RecyclerView) getView();
                                recyclerView.getAdapter().notifyDataSetChanged();

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
            queue.add(stringRequest2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        // Set the adapter

        System.out.println("Sorted:");
        System.out.println(HistoryContent.ITEMS);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyHistoryRecyclerViewAdapter(HistoryContent.ITEMS, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHistoryListFragmentInteraction(HistoryItem item);
    }
}

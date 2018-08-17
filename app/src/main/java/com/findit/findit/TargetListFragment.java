package com.findit.findit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class TargetListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;

    private TextView tvTargetList;

    private ListView lvTargetList;

    private ArrayList<Target> targetList;

    public TargetListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_target_list, container, false);

        this.tvTargetList = (TextView) view.findViewById(R.id.tvTargetList);

        try{
            FileInputStream fileInputStream = getActivity().openFileInput("hunt.txt");
            int read = -1;
            StringBuffer buffer = new StringBuffer();
            while((read=fileInputStream.read())!=-1)
            {
                buffer.append((char)read);
            }

            this.targetList = getTargetList(buffer.toString());

            this.lvTargetList = (ListView) view.findViewById(R.id.lvTargetList);
            lvTargetList.setAdapter(new TargetListAdapter(getActivity(), this.targetList));
            lvTargetList.setOnItemClickListener(this);

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return view;
    }

    public void updateTaskList(ArrayList<Target> targetList){
        this.targetList = targetList;
        TargetListAdapter adapter = (TargetListAdapter) lvTargetList.getAdapter();
        adapter.updateAdapter(this.targetList);
    }

    private ArrayList<Target> getTargetList(String string){
        try{
            String[] targets = string.split("\\|");

            ArrayList<Target> targetList = new ArrayList<>();

            for(String s : targets){
                String[] args = s.split(";");
                Target t = new Target(args[0],args[1],args[2],args[3]);
                targetList.add(t);
            }

            return targetList;
        } catch(ArrayIndexOutOfBoundsException e){
            return new ArrayList<>();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        long viewId = view.getId();

        if (viewId == R.id.btnDelete) {
            Toast.makeText(getActivity(), "Delete"+i, Toast.LENGTH_SHORT).show();
            this.targetList.remove(i);

            StringBuffer buffer = new StringBuffer();

            for(Target t : targetList){
                buffer.append(t.sTitle+';'+t.sDescription+';'+t.sTargetMsg+';'+t.sLocation+';'+t.isSolved+'|');
            }

            mListener.goDeleteTarget(buffer.toString());

        }

        if (viewId == R.id.tvTargetTitle){
            Target t = this.targetList.get(i);
            Toast.makeText(getActivity(), "TargetTitle"+i, Toast.LENGTH_SHORT).show();
            mListener.goBeginHunt(t.sTitle+';'+t.sDescription+';'+t.sTargetMsg+';'+t.sLocation);

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
        public void goDeleteTarget(String sTargetList);
        public void goBeginHunt(String sTargetInfo);
    }

    class TargetListAdapter extends BaseAdapter implements ListAdapter {

        private ArrayList<Target> targetList;
        private Context context;

        public TargetListAdapter(Context context, ArrayList<Target> taskList) {
            this.context = context;
            this.targetList = taskList;
        }

        public void updateAdapter(ArrayList<Target> taskList) {
            this.targetList = taskList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return targetList.size();
        }

        @Override
        public Object getItem(int i) {
            return targetList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, final ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                row = LayoutInflater.from(context).inflate(R.layout.target, parent, false);
                holder = new TargetListAdapter.ViewHolder(row);
                row.setTag(holder);

            } else {
                holder = (TargetListAdapter.ViewHolder) row.getTag();
            }

            Target target = targetList.get(i);
            String targetTitle = target.getsTitle();
            Boolean isSolved= target.getSolved();


            holder.tvTargetTitle.setText(targetTitle);

            if(isSolved) {
                holder.tvTargetTitle.setBackgroundColor(getResources().getColor(R.color.colorSolved));
            }
            holder.tvTargetTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, i, 0); // Let the event be handled in onItemClick()
                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, i, 0); // Let the event be handled in onItemClick()
                }
            });
            return row;
        }

        class ViewHolder {

            TextView tvTargetTitle;
            Button btnDelete;

            ViewHolder(View v) {
                tvTargetTitle = (TextView) v.findViewById(R.id.tvTargetTitle);
                btnDelete = (Button) v.findViewById(R.id.btnDelete);
            }
        }
    }
}


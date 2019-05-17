package com.example.Chat365.Adapter.UserAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.ProfileButton;
import com.example.Chat365.R;

import java.util.List;

public class ProfileButtonAdapter extends RecyclerView.Adapter<ProfileButtonAdapter.ViewHolder> {
    private List<ProfileButton> listButton;
    private Oncallback mListener;

    public ProfileButtonAdapter(Oncallback mListener, List<ProfileButton> listButton) {
        this.mListener = mListener;
        this.listButton = listButton;
    }

    @NonNull
    @Override
    public ProfileButtonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itembutton,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileButtonAdapter.ViewHolder holder, int position) {
        ProfileButton profileButton = listButton.get(position);
        holder.imHinh.setImageResource(profileButton.getId());
        holder.tvName.setText(profileButton.getName());
    }

    @Override
    public int getItemCount() {
        return listButton.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imHinh;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameButton);
            imHinh = itemView.findViewById(R.id.imHinh);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(getPosition());
                }
            });
        }
    }
    public interface Oncallback{
        void onItemClick(int position);
    }
}

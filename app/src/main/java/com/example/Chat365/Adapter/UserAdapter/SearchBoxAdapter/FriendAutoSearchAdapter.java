package com.example.Chat365.Adapter.UserAdapter.SearchBoxAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendAutoSearchAdapter extends ArrayAdapter<User> {
    public List<User> listUser;

    public FriendAutoSearchAdapter(@NonNull Context context, @NonNull List<User> objects) {
        super(context, 0, objects);
        listUser = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return UserFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.itemsearch, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.text_view_name);
        ImageView imageViewFlag = convertView.findViewById(R.id.image_view_flag);

        User user = getItem(position);

        if (user != null) {
            textViewName.setText(user.getName());
            if (!user.getLinkAvatar().equals("")) {
                Picasso.get().load(user.getLinkAvatar()).into(imageViewFlag);
            } else {
                imageViewFlag.setImageResource(R.drawable.noavt);
            }
        }

        return convertView;
    }

    Filter UserFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<User> suggestions = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                suggestions.addAll(listUser);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (User item : listUser) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((User) resultValue).getName();
        }
    };

}
package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CameraActivity;
import com.example.myapplication.Model.UserData;
import com.example.myapplication.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserData> userList;
    private Context context;

    public UserAdapter(Context context, List<UserData> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserData user = userList.get(position);
        holder.txtSchemeName.setText("Scheme: " + user.getSchemeName());
        holder.txtApplicationId.setText("App ID: " + user.getApplicationId());
        holder.txtEmail.setText("Email: " + user.getEmail());

        // Handle button click
        holder.buttonProceed.setOnClickListener(v -> {
            Intent intent = new Intent(context, CameraActivity.class);
            intent.putExtra("scheme_name", user.getSchemeName());
            intent.putExtra("application_id", user.getApplicationId());
            intent.putExtra("email", user.getEmail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSchemeName, txtApplicationId, txtEmail;
        ImageView buttonProceed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSchemeName = itemView.findViewById(R.id.txtSchemeName);
            txtApplicationId = itemView.findViewById(R.id.txtApplicationId);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            buttonProceed = itemView.findViewById(R.id.buttonProceed);
        }
    }
}
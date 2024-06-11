package com.example.diagnozepro.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diagnozepro.R;
import com.example.diagnozepro.model.AppointmentsModel;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {
    List<AppointmentsModel> list;
    Context context;
    String checkFrom;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    public AppointmentsAdapter(List<AppointmentsModel> list, Context context, String checkFrom, DatabaseReference databaseReference, ProgressDialog progressDialog) {
        this.list = list;
        this.context = context;
        this.checkFrom = checkFrom;
        this.databaseReference = databaseReference;
        this.progressDialog = progressDialog;
    }

    //Updates the list of appointments.
    public void setList(List<AppointmentsModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointments, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AppointmentsModel appointmentsModel = list.get(position);

        // Set appointment details to the corresponding views
        holder.tvTitle.setText("Title: " + appointmentsModel.getTitle());
        holder.tvDocName.setText("Doctor Name: " + appointmentsModel.getDoctorName());
        holder.tvPatientName.setText("Patient Name: " + appointmentsModel.getUserName());
        holder.tvPatientPhone.setText("Patient Phone: " + appointmentsModel.getUserPhone());
        holder.tvTime.setText("Time: " + appointmentsModel.getDateTime());
        holder.tvDetails.setText("Details: " + appointmentsModel.getDetails());
        holder.status.setText(appointmentsModel.getStatus());

        // Set text color based on the appointment status
        if (appointmentsModel.getStatus().equals("Pending")) {
            holder.status.setTextColor(context.getColor(R.color.pending));
        } else if (appointmentsModel.getStatus().equals("Rejected")) {
            holder.status.setTextColor(context.getColor(R.color.rejected));
        }

        // Customize views visibility based on the source
        if (checkFrom.contentEquals("Doctor")) {
            if (appointmentsModel.getStatus().equals("Pending")) {
                holder.llBottom.setVisibility(View.VISIBLE);
            } else {
                holder.llBottom.setVisibility(View.GONE);
            }
        } else if (checkFrom.contentEquals("User")) {
            holder.llBottom.setVisibility(View.GONE);
        }

        // Set click listeners for accept and reject buttons
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update appointment status to "Approved" and notify the user
                progressDialog.show();
                Map<String, Object> update = new HashMap<>();
                update.put("status", "Approved");
                databaseReference.child(appointmentsModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    appointmentsModel.setStatus("Approved");
                    holder.status.setText("Approved");
                    holder.status.setTextColor(context.getColor(R.color.main));
                    Toast.makeText(context, "Successfully Approved", Toast.LENGTH_SHORT).show();
                    holder.llBottom.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update appointment status to "Rejected" and notify the user
                progressDialog.show();
                Map<String, Object> update = new HashMap<>();
                update.put("status", "Rejected");
                databaseReference.child(appointmentsModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    appointmentsModel.setStatus("Rejected");
                    holder.status.setText("Rejected");
                    holder.status.setTextColor(context.getColor(R.color.rejected));
                    Toast.makeText(context, "Successfully Rejected", Toast.LENGTH_SHORT).show();
                    holder.llBottom.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //ViewHolder class for holding the views of each item in the RecyclerView.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDocName, tvPatientName, tvPatientPhone, tvTime, tvDetails, status;
        AppCompatButton btnReject, btnAccept;
        LinearLayoutCompat llBottom;

        //The view of the item in the RecyclerView
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDocName = itemView.findViewById(R.id.tvDocName);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvPatientPhone = itemView.findViewById(R.id.tvPatientPhone);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            status = itemView.findViewById(R.id.status);
            llBottom = itemView.findViewById(R.id.llBottom);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }
    }
}

package com.example.guardian.Contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.guardian.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Template> {

    Context context;
    List<Template> contacts;
    Template c;

    public CustomAdapter(@NonNull Context context, List<Template> contacts) {
        super(context, 0, contacts);
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // create a database helper object
        // to handle the database manipulations
        Database db = new Database(context);

        // Get the data item for this position
        Template c = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        LinearLayout linearLayout = convertView.findViewById(R.id.linear);

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPhone = convertView.findViewById(R.id.tvPhone);

        // Populate the data into the template
        // view using the data object
        tvName.setText(c.getName());
        tvPhone.setText(c.getPhoneNo());

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Dial Contact")
                        .setMessage("Do want to dial this contact?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String s = "tel:" + c.getPhoneNo();
                                Uri u = Uri.parse(s);
                                Intent intent = new Intent(Intent.ACTION_DIAL, u);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }

        });

        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // generate an MaterialAlertDialog Box
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Remove Contact")
                        .setMessage("Are you sure want to remove this contact?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // delete the specified contact from the database
                                db.deleteContact(c);
                                // remove the item from the list
                                contacts.remove(c);
                                // notify the listview that dataset has been changed
                                notifyDataSetChanged();
                                Toast.makeText(context, "Contact removed!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return false;
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    // this method will update the ListView
    public void refresh(List<Template> list) {
        contacts.clear();
        contacts.addAll(list);
        notifyDataSetChanged();
    }
}


package com.example.futsalgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditAkunMenu extends Fragment {
    public EditAkunMenu(){}
    LinearLayout view;
    Integer id;
    EditText etnama, etemail, ettelp, etpassword, etkonfirmasi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Edit Akun");

        view = (LinearLayout) inflater.inflate(R.layout.edit_akun_menu, container, false);
        etnama = view.findViewById(R.id.nama);
        etemail = view.findViewById(R.id.email);
        ettelp = view.findViewById(R.id.telp);
        etpassword = view.findViewById(R.id.password);
        etkonfirmasi = view.findViewById(R.id.konfirmasi_password);

        AndroidNetworking.initialize(getActivity());
        SharedPreferences user = getActivity().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
        id = user.getInt("id", 0);
        etnama.setText(user.getString("nama", "nama"));
        etemail.setText(user.getString("email", "email"));
        ettelp.setText(user.getString("telp", "telp"));

        Button btnsimpan = view.findViewById(R.id.simpan);
        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etpassword.getText().toString().length() != 0 && etpassword.getText().toString().length() < 5) {
                    Toast.makeText(getActivity(), "Password minimal 5 karakter!", Toast.LENGTH_LONG).show();
                } else {
                    if(!etpassword.getText().toString().equals(etkonfirmasi.getText().toString())) {
                        Toast.makeText(getActivity(), "Konfirmasi password salah!", Toast.LENGTH_LONG).show();
                    } else {
                        simpan();
                    }
                }
            }
        });
        return view;
    }

    private void simpan() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        AndroidNetworking.post(Konfigurasi.USER)
                .addBodyParameter("method", "update")
                .addBodyParameter("id", id.toString())
                .addBodyParameter("nama", etnama.getText().toString())
                .addBodyParameter("telp", ettelp.getText().toString())
                .addBodyParameter("email", etemail.getText().toString())
                .addBodyParameter("password", etpassword.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("status")) {
                                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("dataUser", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("nama", etnama.getText().toString());
                                editor.putString("telp", ettelp.getText().toString());
                                editor.commit();
                                Toast.makeText(getActivity(), "Data berhasil disimpan!", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getActivity(), "Gagal menyimpan data!", Toast.LENGTH_LONG).show();

                            }

                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                    }
                });
    }
}

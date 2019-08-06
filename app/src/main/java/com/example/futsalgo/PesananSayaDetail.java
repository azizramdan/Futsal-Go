package com.example.futsalgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.Constraints;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.futsalgo.data.PesananSayaAdapter;
import com.example.futsalgo.data.WaktuPilihJamAdapter;
import com.example.futsalgo.data.model.PesananSaya;
import com.example.futsalgo.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.google.android.gms.wearable.DataMap.TAG;
import static java.lang.Boolean.FALSE;

public class PesananSayaDetail extends Fragment {
    public PesananSayaDetail() {

    }

    LinearLayout view;
    String nama_lapangan, waktu_pilih_tanggal, waktu_pilih_jam, metode_bayar, status, harga, alamat;
    Integer id;
    TextView tvnama_lapangan, tvharga, tvwaktu_pilih_tanggal, tvwaktu_pilih_jam, tvstatus, tvmetode_bayar, tvalamat, tvbank, tvnama_rekening, tvno_rekening;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = (LinearLayout) inflater.inflate(R.layout.pesanan_saya_detail, container, false);
//        AndroidNetworking.initialize(getActivity());
        getActivity().setTitle("Detail Pemesanan");

        Bundle bundle = this.getArguments();
        id = bundle.getInt("id");
        nama_lapangan = bundle.getString("nama_lapangan");
        waktu_pilih_tanggal = bundle.getString("waktu_pilih_tanggal");
        waktu_pilih_jam = bundle.getString("waktu_pilih_jam");
        metode_bayar = bundle.getString("metode_bayar");
        status = bundle.getString("status");
        harga = bundle.getString("harga");
        alamat = bundle.getString("alamat");

        tvnama_lapangan = view.findViewById(R.id.nama_lapangan);
        tvharga = view.findViewById(R.id.harga);
        tvwaktu_pilih_tanggal = view.findViewById(R.id.waktu_pilih_tanggal);
        tvwaktu_pilih_jam = view.findViewById(R.id.waktu_pilih_jam);
        tvstatus = view.findViewById(R.id.status);
        tvmetode_bayar = view.findViewById(R.id.metode_bayar);
        tvalamat = view.findViewById(R.id.alamat);
        tvbank = view.findViewById(R.id.bank);
        tvnama_rekening = view.findViewById(R.id.nama_rekening);
        tvno_rekening = view.findViewById(R.id.no_rekening);

        if(metode_bayar.equals("COD")) {
            tvbank.setVisibility(View.GONE);
            tvnama_rekening.setVisibility(View.GONE);
            tvno_rekening.setVisibility(View.GONE);
        } else {
            tvbank.setText(bundle.getString("bank"));
            tvnama_rekening.setText("a.n. " + bundle.getString("nama_rekening"));
            tvno_rekening.setText(bundle.getString("no_rekening"));
        }

        String harga_idr = NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(Double.parseDouble(harga));

        tvnama_lapangan.setText(nama_lapangan);
        tvharga.setText("Harga: " + harga_idr);
        tvwaktu_pilih_tanggal.setText("Tanggal: " + waktu_pilih_tanggal);
        tvwaktu_pilih_jam.setText("Jam: " + waktu_pilih_jam);
        tvstatus.setText("Status: " + status);
        tvmetode_bayar.setText("Pembayaran melalui " + metode_bayar);
        tvalamat.setText("Alamat lapangan: " + alamat);

        Button batal = view.findViewById(R.id.batal);

        if(!status.equals("Belum bayar")) {
            batal.setEnabled(FALSE);
        } else {
            batal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    batalPesanan(view);
                }
            });
        }

        return view;
    }

    private void batalPesanan(View view) {
        final View v = view;
        new AlertDialog.Builder(getContext())
                .setTitle("Batalkan pesanan?")
                .setMessage(
                        "Apakah Anda yakin ingin membatalkan pesanan?")
                .setIcon(
                        android.R.drawable.ic_dialog_alert
                )
                .setPositiveButton(
                        "Ya",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("Sending...");
                                progressDialog.show();
                                AndroidNetworking.get(Konfigurasi.PESANAN)
                                        .addQueryParameter("method", "batal")
                                        .addQueryParameter("id", id.toString())
                                        .setPriority(Priority.LOW)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d(Constraints.TAG, "berhasil mang response " + response);

                                                try {
                                                    progressDialog.dismiss();
                                                        if(response.getBoolean("status")) {
                                                            Toast.makeText(getActivity(), "Pesanan berhasil dibatalkan!", Toast.LENGTH_LONG).show();

                                                            Fragment fragment = new PesananSayaMenu();
                                                            AppCompatActivity activity = (AppCompatActivity) v.getContext();

                                                            activity.getSupportFragmentManager()
                                                                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                            activity.getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .replace(R.id.frame_container, fragment)
                                                                    .commit();
                                                        } else {
                                                            Toast.makeText(getActivity(), "Pesanan gagal dibatalkan!", Toast.LENGTH_LONG).show();
                                                        }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Pesanan gagal dibatalkan!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            @Override
                                            public void onError(ANError error) {
                                                Log.d(Constraints.TAG, "error mang " + error);
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), "Pesanan gagal dibatalkan!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                .setNegativeButton(
                        "Tidak",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                            }
                        }).show();
    }
}
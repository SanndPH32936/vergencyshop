package com.example.vergencyshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vergencyshop.Adapter.ThanhToanAdapter;
import com.example.vergencyshop.models.NguoiDung;
import com.example.vergencyshop.models.SanPham;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ThanhToanSanPham extends AppCompatActivity {
    TextView tvTenSDtThanhToan , tvDiaChiThanhToan , tvTongThanhToanHoaDon ,btnMuaHang;
    RecyclerView rcSanPhamThanhToan ;
    RadioButton rdNhanHangThanhToan,  rdBankingThanhToan ;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<SanPham> list = new ArrayList<>();

    String  size ;
    String soLuong;
    ThanhToanAdapter thanhToanAdapter ;

    SanPham sanPham ;

    int tongTien = 0  ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan_san_pham);
        anhXa();
        setThongTin();

        Bundle bundle = getIntent().getExtras();
        sanPham = (SanPham) bundle.get("thanhtoancainay");
        soLuong = bundle.getString("soluongthanhtoan");
        size = bundle.getString("sizethanhtoan");

        list.add(sanPham);
        Toast.makeText(this, ""+size+soLuong, Toast.LENGTH_SHORT).show();

        rcSanPhamThanhToan.setLayoutManager(new LinearLayoutManager(this));
        thanhToanAdapter = new ThanhToanAdapter(list,this,soLuong,size);


        tongTien =  Integer.parseInt(soLuong) * Integer.parseInt(sanPham.getGiaSP()) ;
        if (tongTien < 200000){
           tongTien = tongTien + 15000;
        }

        tvTongThanhToanHoaDon.setText(String.valueOf(String.valueOf(tongTien) + "VNĐ"));
        rcSanPhamThanhToan.setAdapter(thanhToanAdapter);

    }

    private  void anhXa(){

        tvTenSDtThanhToan = findViewById(R.id.tvTenSDtThanhToan);
        tvDiaChiThanhToan = findViewById(R.id.tvDiaChiThanhToan);
        tvTongThanhToanHoaDon = findViewById(R.id.tvTongThanhToanHoaDon);
        btnMuaHang = findViewById(R.id.btnMuaHang);
        rcSanPhamThanhToan = findViewById(R.id.rcSanPhamThanhToan);
        rdBankingThanhToan = findViewById(R.id.rdBankingThanhToan);
        rdNhanHangThanhToan = findViewById(R.id.rdNhanHangThanhToan);
    }

    private void setThongTin (){




        reference.child("NguoiDung").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                NguoiDung nguoiDung = snapshot.getValue(NguoiDung.class);

                tvDiaChiThanhToan.setText(nguoiDung.getDiaChi());
                tvTenSDtThanhToan.setText(nguoiDung.getTen() +"-"+nguoiDung.getDiaChi());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }




}
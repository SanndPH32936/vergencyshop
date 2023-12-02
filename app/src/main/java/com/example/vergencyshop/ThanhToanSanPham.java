package com.example.vergencyshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vergencyshop.Adapter.ThanhToanAdapter;
import com.example.vergencyshop.models.GioHang;
import com.example.vergencyshop.models.HoaDon;
import com.example.vergencyshop.models.HoaDonChiTiet;
import com.example.vergencyshop.models.NguoiDung;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class ThanhToanSanPham extends AppCompatActivity {
    TextView tvTenSDtThanhToan , tvDiaChiThanhToan , tvTongThanhToanHoaDon ;
    LinearLayout btnMuaHang;
    RecyclerView rcSanPhamThanhToan ;
    RadioButton rdNhanHangThanhToan,  rdBankingThanhToan ;
    ThanhToanAdapter thanhToanAdapter;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();





    ArrayList<GioHang> list = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan_san_pham);

        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        Currency currency = Currency.getInstance(locale);
        anhXa();
        setList();
        setThongTin();




        rdBankingThanhToan.setChecked(true);



        rcSanPhamThanhToan.setLayoutManager(new LinearLayoutManager(this ));
        thanhToanAdapter = new ThanhToanAdapter(list,this);



        btnMuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMuaHang();
            }
        });


        rcSanPhamThanhToan.setAdapter(thanhToanAdapter);

    }



    private int tinhThanhTien (){
        int soHang = 0 ;

        for (GioHang i : list
             ) {

           soHang +=  (Integer.parseInt(i.getGiaSP()) * Integer.parseInt(i.getSoluongSP())) ;

        }
        return soHang ;
    }

    private void setMuaHang() {




        // Set giờ
        // Tạo đối tượng SimpleDateFormat với định dạng mong muốn
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Lấy thời gian hiện tại
        Date currentTime = new Date();

        // Định dạng thời gian hiện tại theo định dạng đã cho
        String formattedTime = dateFormat.format(currentTime);


        String idHD = reference.push().getKey();
        String idND = user.getUid();
        String thanhTien = String.valueOf(tinhThanhTien());
        String ngayMua = formattedTime;
        String phuongThuc = setPhuongThucThanhToan();
        String trangThai ="Chờ Xác Nhận";

        HoaDon hoaDon = new HoaDon(idHD,idND,thanhTien,ngayMua,phuongThuc,trangThai);

        reference.child("HoaDon").child(idHD)
                .setValue(hoaDon);


        for (GioHang hang: list){

            String idHDCT = hoaDon.getIdHD();
            String idSP =   hang.getIdSP() ;
            String soLuong = hang.getSoluongSP();
            String tongTien = String.valueOf(Integer.parseInt(hang.getSoluongSP()) * Integer.parseInt(hang.getSoluongSP())) ;
            String anhSP = hang.getAnhSP() ;
            String sizeSP = hang.getSizeSP();

            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet(idHDCT,idSP,soLuong,tongTien,anhSP,sizeSP);
            reference.child("HoaDonChiTiet").push().setValue(hoaDonChiTiet);
        }

        //Xóa giỏ hàng


        Intent intent = new Intent(ThanhToanSanPham.this , MainActivity.class);
        startActivity(intent);
        finishAffinity();

    }
    private  void anhXa(){

        tvTenSDtThanhToan = findViewById(R.id.tvTenSDtThanhToan);
        tvDiaChiThanhToan = findViewById(R.id.tvDiaChiThanhToan);
        tvTongThanhToanHoaDon = findViewById(R.id.tvTongThanhToanHoaDon);
        btnMuaHang = findViewById(R.id.btnMuaHangThanhToan);
        rcSanPhamThanhToan = findViewById(R.id.rcSanPhamThanhToan);
        rdBankingThanhToan = findViewById(R.id.rdBankingThanhToan);
        rdNhanHangThanhToan = findViewById(R.id.rdNhanHangThanhToan);
    }


    private String setPhuongThucThanhToan (){

        if (rdBankingThanhToan.isChecked()){
            return "bank";
        }else {
            return "tien";
        }
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


    private void setList(){




        Query query = reference.child("GioHang").orderByChild("idNguoiDung").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            GioHang hang = dataSnapshot.getValue(GioHang.class);
                            list.add(hang);

                        }
                        thanhToanAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
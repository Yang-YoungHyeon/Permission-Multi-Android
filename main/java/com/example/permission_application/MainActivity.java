package com.example.permission_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/** com.google.android.gms:play-services */
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.annotation.Target;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 94;
    private static final int LOCATION_ON_REQUEST = 95;
    private static final int BLUETOOTH_ON_REQUEST = 96;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check_Permission();
    }

    /** 현재 앱에서 필요한 권한에 대하여 사용권한이 있는지 확인하는 메소드 */
    private void check_Permission(){
        /** TIP : Developer.android.com 에서 반드시 위험권한 리스트 확인가능
         * 긴가민가 하거나, 추후에 위험권한으로 분류될 것 같으면, 미리 적어두는 것이 유지보수 측면에서 유리할 것으로 판단됨
         * 2019.12.19 기준, 블루투스는 위험권한으로 분류되어있지 않지만 일단 추가하였음. */
        //사용하는 권한 목록
        String [] permissions = {
                Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
        };

        //사용하는 권한 목록중 권한이 없는 목록 재생성
        ArrayList<String> request_permissions = new ArrayList<String>();
        for(String p : permissions){
            if(ContextCompat.checkSelfPermission(this,p)!=
            PackageManager.PERMISSION_GRANTED){
                request_permissions.add(p);
            }
        }

        //권한이 없는 목록에 대하여 권한 요청
        if(!request_permissions.isEmpty()){
            String[] request_permission_array = new String[request_permissions.size()];
            request_permission_array = request_permissions.toArray(request_permission_array);
            ActivityCompat.requestPermissions(this,request_permission_array,PERMISSIONS_REQUEST_CODE);
        }
    }
    /** Custom Activity or Permission Activity 에 대한 Request */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case BLUETOOTH_ON_REQUEST:
                break;
        }
    }
    /** 필수권한에 대한 Request */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i : grantResults){
            if(i == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "필요한 권한에 대해서 반드시 허용해야 합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    /** 위치 기능 키기 (설정화면으로 가기 싫은 경우) */
    private void createLocationRequest(){
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        //위치정보가 켜져있는가?
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });

        //위치정보가 꺼져있는가?
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    try{
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,LOCATION_ON_REQUEST);
                    }catch (IntentSender.SendIntentException e1){
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    /** 블루투스 기능 키기 */
    private void createBluetoothRequest(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled()){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),BLUETOOTH_ON_REQUEST);
        }
    }
    public void Button_Click(View view){
        switch (view.getId()){
            case R.id.BluetoothButton:
                createBluetoothRequest();
                break;
            case R.id.LocationButton:
                createLocationRequest();
                break;
            default:
                break;
        }
    }

}

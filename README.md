# Permission-Multi-Android
```
  1. You can mulitple permission request at one time.
  2. You can switch on location in an application.
  1. 여러개의 권한요청
  2. 위치기능 활성화시에 설정화면으로 이동하지 않아도 가능
```
# Using Permission
```
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    ...
```
  
# Check Permission
```
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
```

# onRequestPermissionsResult
```
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    for(int i : grantResults){
        if(i == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, "필요한 권한에 대해서 반드시 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
 ```
 #createLocationRequest
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

package natto.com.oreragorilla;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.taptappun.taku.kobayashi.runtimepermissionchecker.RuntimePermissionChecker;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        if(!RuntimePermissionChecker.existConfirmPermissions(this)){
            // write features you want to execute.
            start();
        }else {
            RuntimePermissionChecker.requestAllPermissions(this, REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(RuntimePermissionChecker.existConfirmPermissions(this)){
            // write features you want to execute.
            finish();
        }
        start();
    }
    void start(){
        setContentView(R.layout.activity_main);
    }
}

package natto.com.oreragorilla;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import natto.com.oreragorilla.databinding.FragmentCameraBinding;

public class CameraFragment extends Fragment {

    SurfaceView sv;
    SurfaceHolder sh;
    Camera cam;
    FragmentCameraBinding binding;

    FireBaseRepository firebase;
    private String mId;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = SystemRepository.getDeviceId(Objects.requireNonNull(getContext()));
        firebase = new FireBaseRepository(mId);
        firebase.setValueEventListener(new ValueEventListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(), dataSnapshot.child("imageUrl").getValue().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);

        sv = new SurfaceView(getContext());
        sh = sv.getHolder();
        sh.addCallback(new SurfaceHolderCallback());

        binding.picture.setOnClickListener(new TakePictureClickListener());

        binding.camera.addView(sv);
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //オートフォーカス(機能してるかわからん)
                cam.autoFocus(null);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            cam = Camera.open();
            Camera.Parameters param = cam.getParameters();
            List<Camera.Size> ss = param.getSupportedPictureSizes();
            Camera.Size pictSize = ss.get(0);

            param.setPictureSize(pictSize.width, pictSize.height);
            cam.setParameters(param);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
            try {
                cam.setDisplayOrientation(90);
                cam.setPreviewDisplay(sv.getHolder());

                // プレビュー画面のサイズ設定
                Camera.Parameters params = cam.getParameters();
                List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
                Camera.Size size = previewSizes.get(0);
                params.setPreviewSize(size.width, size.height);
                cam.setParameters(params);
                // プレビュー開始
                cam.startPreview();
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cam.stopPreview();
            cam.release();
        }
    }

    class TakePictureClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            cam.takePicture(null, null, new TakePictureCallback());
        }

        private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {

            }
        };
    }

    class TakePictureCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                //Cameraディレクトリ
                File dir = new File(
                        Environment.getExternalStorageDirectory(), "Camera");
                //なければ作る
                if (!dir.exists()) {
                    dir.mkdir();
                }
                //img.jpgファイルの作成
                File f = new File(dir, "img.jpg");
                FileOutputStream fos = new FileOutputStream(f);
                //撮影データの書き込み
                fos.write(data);
                Toast.makeText(getContext(),
                        "写真を送信しました", Toast.LENGTH_LONG).show();
                fos.close();
                cam.startPreview();

                //画像をbitmapに変換
                Bitmap bm = BitmapFactory.decodeFile("/storage/emulated/0/Camera/img.jpg");

                //bitmapをjpegに変換
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] jpgarr = baos.toByteArray();

                //jpeg変換後の値
                Log.d("tag", jpgarr.toString());

                //画像データを送信
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";

                HttpURLConnection con = null;
                URL url = new URL("");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setUseCaches(false);

                DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"filename\";" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes("fff.png");
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"upfile\";filename=\"upfile.png\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                for (int i = 0; i < jpgarr.length; i++) {
                    outputStream.writeByte(jpgarr[i]);
                }
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                con.connect();

                InputStream in = con.getInputStream();
                InputStreamReader objReader = new InputStreamReader(in);
                BufferedReader objBuf = new BufferedReader(objReader);
                StringBuilder objStr = new StringBuilder();
                String sLine;
                while ((sLine = objBuf.readLine()) != null) {
                    objStr.append(sLine);
                }
                //objStr.toString();//返り値
                in.close();
                objBuf.close();
            } catch (Exception e) {
            }
        }
    }
}

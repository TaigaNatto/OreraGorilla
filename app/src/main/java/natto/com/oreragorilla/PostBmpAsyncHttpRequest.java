package natto.com.oreragorilla;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostBmpAsyncHttpRequest extends AsyncTask<Param, Void, String> {
    private Activity mActivity;

    public PostBmpAsyncHttpRequest(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected String doInBackground(Param... params) {
        Param param = params[0];
        HttpURLConnection connection = null;
        StringBuilder sb = new StringBuilder();
        try {
            // 画像をjpeg形式でstreamに保存
            ByteArrayOutputStream jpg = new ByteArrayOutputStream();
            param.bmp.compress(Bitmap.CompressFormat.JPEG, 100, jpg);

            URL url = new URL(param.uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);//接続タイムアウトを設定する。
            connection.setReadTimeout(3000);//レスポンスデータ読み取りタイムアウトを設定する。
            connection.setRequestMethod("POST");//HTTPのメソッドをPOSTに設定する。
            //ヘッダーを設定する
            connection.setRequestProperty("User-Agent", "Android");
            connection.setRequestProperty("Content-Type","application/octet-stream");
            //connection.setRequestProperty("Content-Type","multipart/form-data");
            connection.setDoInput(true);//リクエストのボディ送信を許可する
            connection.setDoOutput(true);//レスポンスのボディ受信を許可する
            connection.setUseCaches(false);//キャッシュを使用しない
            connection.connect();

            String id=SystemRepository.getDeviceId(mActivity);
            byte[] idByte=id.getBytes(StandardCharsets.UTF_8);

            // データを投げる
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(jpg.toByteArray());
            out.write(idByte);
            out.flush();

            // データを受け取る
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null)
                sb.append(line);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            connection.disconnect();
        }
        return sb.toString();
    }

    public void onPostExecute(String string) {
        Log.d("IMAGE_SEND",string);
    }
}

package natto.com.oreragorilla;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import androidx.navigation.Navigation;
import natto.com.oreragorilla.databinding.FragmentResultBinding;

public class ResultFragment extends Fragment {

    public static String resultPictureURL;
    public static String resultText;

    FragmentResultBinding binding;

    public ResultFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false);
        float percent=getArguments().getFloat("percent");
        String message=" | ゴリラになるならオレラゴリラ";
        resultText = getReviewMessage(percent)+message;
        resultPictureURL = getArguments().getString("imageUrl");

        final String strUrl = resultPictureURL;
        Glide.with(this).load(strUrl).into(binding.resultPicture);

        binding.resultText.setText(resultText);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_resultFragment_to_cameraFragment);
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //結果をシェア
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, resultText + "\n" + strUrl);
                startActivity(Intent.createChooser(i, "結果をシェア！"));
            }
        });

        // Inflate the layout for this fragment
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

    public String getReviewMessage(float percent){
        if(percent>0.9){
            return "完全にゴリラ。野生に帰りましょう。";
        }else if(percent>0.75){
            return "ほぼゴリラ。かろうじて人間を保ってる。";
        } else if (percent > 0.5) {
            return "わりとゴリラ。わりと似てる。";
        }else if(percent>0.25){
            return "ちょいゴリラ。自覚はない。";
        }
        return "ゴリラではない。人の世で生きていきましょう。";
    }
}

package video.videoassistant.indexPage;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.azhon.basic.base.BaseFragment;
import com.jeremyliao.liveeventbus.LiveEventBus;

import video.videoassistant.R;
import video.videoassistant.databinding.FragmentIndexBinding;
import video.videoassistant.generated.callback.OnClickListener;
import video.videoassistant.mainPage.MainActivity;
import video.videoassistant.util.Constant;


public class IndexFragment extends BaseFragment<IndexModel, FragmentIndexBinding> {
    @Override
    protected IndexModel initViewModel() {
        return new ViewModelProvider(this).get(IndexModel.class);
    }

    @Override
    protected void showError(Object obj) {

    }

    @Override
    protected int onCreate() {
        return R.layout.fragment_index;
    }

    @Override
    protected void initView() {
        dataBinding.setModel(viewModel);
        dataBinding.setView(this);

        dataBinding.deleteUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataBinding.word.setText("");
            }
        });
    }

    @Override
    protected void initData() {
        viewModel.keyword.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (TextUtils.isEmpty(s)) {
                    dataBinding.deleteUsername.setVisibility(View.GONE);
                } else {
                    dataBinding.deleteUsername.setVisibility(View.VISIBLE);
                }
            }
        });

        dataBinding.word.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeybord(dataBinding.word);
                    return true;
                }
                return false;
            }
        });
    }

    public void so() {
        if (TextUtils.isEmpty(dataBinding.word.getText().toString())) {
            return;
        }
        LiveEventBus.get(Constant.soWord, String.class)
                .post(dataBinding.word.getText().toString().trim());
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectPage(1);
        }
    }

    public void closeKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}

package sdx.viewpagerindicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sdx on 2017/4/20.
 */

public class Tab1Fragment extends BaseFragment {
    private final String TAG = this.getClass().getSimpleName();
    private MyWebView webView;

    public static Tab1Fragment newInstance() {

        Bundle args = new Bundle();

        Tab1Fragment fragment = new Tab1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        webView = new MyWebView(getContext());
        webView.loadUrl("https://www.baidu.com");
        webView.setOnScrollListener(this);
        return webView;
    }

    @Override
    public View getScrollView() {
        return webView;
    }
}

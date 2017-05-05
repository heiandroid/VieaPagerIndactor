package sdx.viewpagerindicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by sdx on 2017/5/3.
 */

public abstract class BaseFragment extends Fragment implements OnScrollListener {
    private ThinkTitleView.Status current = ThinkTitleView.Status.LONG_STATUS;

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        final FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            if (t > 90) {
                current = ThinkTitleView.Status.SHORT_STATUS;
            } else {
                current = ThinkTitleView.Status.LONG_STATUS;
            }
            final ThinkTitleView.Status indicatorStatus = ((MainActivity) activity).getIndicatorStatus();
            if (current != indicatorStatus) {
                ((MainActivity) activity).startAnimation();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            final View scrollView = getScrollView();
            if (scrollView!=null){
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        onScrollChanged(scrollView.getLeft(), scrollView.getScrollY(), 0, 0);
                    }
                });
            }
        }
    }

    public abstract View getScrollView();

}

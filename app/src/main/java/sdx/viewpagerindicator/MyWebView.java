package sdx.viewpagerindicator;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sdx on 2017/5/3.
 */

public class MyWebView extends WebView {
    private final String TAG = this.getClass().getSimpleName();
    private WebSettings setting;

    public MyWebView(Context context) {
        this(context, null);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetting();
    }

    private void initSetting() {
        setting = getSettings();
        setting.setAllowFileAccess(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        /*Fit Phone ratio*/
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setSupportZoom(true);
        setting.setDomStorageEnabled(true);
        /*启用javascript支持*/
        setting.setJavaScriptEnabled(true);
        //只要有缓存就从缓存里边读取
        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setting.setLoadsImagesAutomatically(true); //
        setting.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        setting.setDatabaseEnabled(true);   //开启 database storage API 功能
        setting.setAppCacheEnabled(true);//开启 Application Caches 功能
        setOverScrollMode(View.OVER_SCROLL_NEVER); // 取消WebView中滚动或拖动到顶部、底部时的阴影
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // 取消滚动条白边效果
        //Https不允许加载Http资源,需要开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    view.stopLoading();
                }
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (listener != null) {
                    listener.onPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.e(TAG + "title", title);
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                return super.onJsBeforeUnload(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (listener != null) {
                    listener.onProgressChanged(view, newProgress);
                }
            }
        });

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.onScrollChanged(l, t, oldl, oldt);
        }
    }


    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface Listener {

        void onPageFinished(WebView view, String url);

        void onProgressChanged(WebView view, int newProgress);
    }

    private Listener listener;

    public void setLoadListener(Listener listener) {
        this.listener = listener;
    }

}

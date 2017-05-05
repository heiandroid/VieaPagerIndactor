package sdx.viewpagerindicator;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.indicator)
    ThinkTitleView indicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    List<Fragment> mList = new ArrayList<>();
    List<String> title = new ArrayList<>();
    String json="{\n" +
            "    \"api_status\": 1,\n" +
            "    \"data\": {\n" +
            "        \"item\": [\n" +
            "            {\n" +
            "                \"name\": \"哈哈哈哈\",\n" +
            "                \"engname\": \"adfa\",\n" +
            "                \"start_color\": \"#ffffff\",\n" +
            "                \"end_color\": \"#562362\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"啦啦啦啦啦\",\n" +
            "                \"engname\": \"aaaaaa\",\n" +
            "                \"start_color\": \"#236525\",\n" +
            "                \"end_color\": \"#125698\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"嗯\",\n" +
            "                \"engname\": \"123andian\",\n" +
            "                \"start_color\": \"#102365\",\n" +
            "                \"end_color\": \"#23698a\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"啧啧啧在什么地方\",\n" +
            "                \"engname\": \"haodehahaha\",\n" +
            "                \"start_color\": \"#fa2365\",\n" +
            "                \"end_color\": \"#ab1236\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Gson gson=new Gson();
        final Data data = gson.fromJson(json, Data.class);
        final List<Data.DataBean.ItemBean> item = data.data.item;
        for (int i = 0; i < data.data.item.size(); i++) {
            mList.add(Tab1Fragment.newInstance());
        }

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mList.get(position);
            }

            @Override
            public int getCount() {
                return mList.size();
            }
        });
        indicator.setViewPager(viewPager,item);
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RecyclerView recyclerView = (RecyclerView) View.inflate(MainActivity.this,
                        R.layout.pop_menu_think, null);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(new RecyclerView.Adapter() {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        TextView textView = new TextView(parent.getContext());
                        return new RecyclerView.ViewHolder(textView) {
                            @Override
                            public String toString() {
                                return super.toString();
                            }
                        };
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                        TextView textView = (TextView) holder.itemView;
                        textView.setTextColor(Color.RED);
                        textView.setText("position" + position);
                    }

                    @Override
                    public int getItemCount() {
                        return 10;
                    }
                });
                PopupWindow popupWindow = new PopupWindow(recyclerView, -2, -2);
                popupWindow.setOutsideTouchable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    popupWindow.showAsDropDown(indicator, indicator.getMeasuredWidth() / 2, 0, Gravity.CENTER);
                } else {
                    popupWindow.showAsDropDown(indicator, indicator.getMeasuredWidth()/ 2, 0);
                }
                indicator.animationTriangle();
            }
        });
        indicator.setOnclickBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ThinkTitleView.Status getIndicatorStatus() {
        return indicator.getStatus();
    }

    public void startAnimation() {
        if (indicator != null) {
            indicator.startAnimation();
        }
    }

}

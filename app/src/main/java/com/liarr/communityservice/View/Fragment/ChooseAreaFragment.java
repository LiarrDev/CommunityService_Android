package com.liarr.communityservice.View.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liarr.communityservice.Database.City;
import com.liarr.communityservice.Database.County;
import com.liarr.communityservice.Database.Province;
import com.liarr.communityservice.R;
import com.liarr.communityservice.Util.AlertDialogUtil;
import com.liarr.communityservice.Util.HttpRequestUrlUtil;
import com.liarr.communityservice.Util.LogUtil;
import com.liarr.communityservice.Util.ParseJsonUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ChooseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    public static final String SETTING_GLOBAL_LOCATION = "setting_global_location";
    public static final String SETTING_ADD_EVENT_LOCATION = "setting_add_event_location";

    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;        // 省列表

    private List<City> cityList;                // 市列表

    private List<County> countyList;            // 县列表

    private Province selectedProvince;          // 选中的省份

    private City selectedCity;                  // 选中的城市

    private County selectedCounty;              // 选中的县

    private int currentLevel;                   // 当前选中的级别

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.location_list_view);
        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 获取 Activity 中通过 Intent 传过来的 Action
        String action = getActivity().getIntent().getAction();
        LogUtil.e("==FragmentAction==", action);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                if (action.equals(SETTING_GLOBAL_LOCATION)) {       // 如果是在 Drawer 中进来就只查询到市并保存
                    LogUtil.e("==DrawerAction==", "INSIDE");
                    // 用 SharedPreferences 保存选中的位置
                    SharedPreferences.Editor editor = Objects.requireNonNull(getContext()).getSharedPreferences("defaultUser", MODE_PRIVATE).edit();
                    editor.putString("location", selectedCity.getCityName());
                    editor.apply();
                    Objects.requireNonNull(getActivity()).finish();
                } else if (action.equals(SETTING_ADD_EVENT_LOCATION)) {     // 如果是在添加 Event 中进来就查询到县并回传到 Activity 中
                    queryCounties();

                }
            } else if (currentLevel == LEVEL_COUNTY) {
                selectedCounty = countyList.get(position);
                // 把选中的省市县存在 SharedPreference 中
                SharedPreferences.Editor editor = Objects.requireNonNull(getContext()).getSharedPreferences("add_event_location", MODE_PRIVATE).edit();
                editor.putString("province", selectedProvince.getProvinceName());
                editor.putString("city", selectedCity.getCityName());
                editor.putString("county", selectedCounty.getCountyName());
                LogUtil.e("==AddEventLocation==", selectedProvince.getProvinceName() + " " + selectedCity.getCityName() + " " + selectedCounty.getCountyName());
                editor.apply();
                getActivity().finish();
            }
        });
        backButton.setOnClickListener(v -> {
            if (currentLevel == LEVEL_PROVINCE) {
                Objects.requireNonNull(getActivity()).finish();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            } else if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，有限从数据库查询，如果没有查询到再去服务器查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.VISIBLE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            LogUtil.e("==QueryProvinces==", "CLEAR");
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            LogUtil.e("==QueryProvinces==", "Add List");
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryLocationFromServer(HttpRequestUrlUtil.locationUrl, "province");
            LogUtil.e("==QueryProvinces==", "QueryFormServer");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String url = HttpRequestUrlUtil.locationUrl + "/" + provinceCode;
            queryLocationFromServer(url, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String url = HttpRequestUrlUtil.locationUrl + "/" + provinceCode + "/" + cityCode;
            queryLocationFromServer(url, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     *
     * @param url  查询的地址
     * @param type 查询的类型
     */
    private void queryLocationFromServer(String url, final String type) {
        AlertDialogUtil.showProgressDialog(getContext());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.e("==HttpNewCall==", "Failed");
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    AlertDialogUtil.dismissProgressDialog();
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                LogUtil.e("==HttpNewCall==", "Response");
                String responseContent = response.body().string();
                boolean result = false;
                switch (type) {
                    case "province":
                        result = ParseJsonUtil.handleProvinceJson(responseContent);
                        break;
                    case "city":
                        result = ParseJsonUtil.handleCityJson(responseContent, selectedProvince.getId());
                        break;
                    case "county":
                        result = ParseJsonUtil.handleCountyJson(responseContent, selectedCity.getId());
                        break;
                }
                if (result) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        AlertDialogUtil.dismissProgressDialog();
                        switch (type) {
                            case "province":
                                queryProvinces();
                                break;
                            case "city":
                                queryCities();
                                break;
                            case "county":
                                queryCounties();
                                break;
                        }
                    });
                }
            }
        });
    }
}
package ru.espepe.bubuka.player.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.MainActivity;
import ru.espepe.bubuka.player.R;
import ru.espepe.bubuka.player.log.Logger;
import ru.espepe.bubuka.player.log.LoggerFactory;

/**
 * Created by wolong on 17/09/14.
 */
public class RegisterActivity extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(RegisterActivity.class);

    @InjectView(R.id.register_email_field)
    protected EditText emailField;

    @InjectView(R.id.register_region_field)
    protected Spinner regionField;

    @InjectView(R.id.register_city_field)
    protected Spinner cityField;

    @InjectView(R.id.register_type_field)
    protected Spinner typeField;

    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
        client = new AsyncHttpClient();
        setupUi();
    }

    @Override
    protected void onDestroy() {
        try { client.cancelAllRequests(true); } catch (Exception e) { /* ignore */ }
        super.onDestroy();
    }

    private static final Pattern regionPattern = Pattern.compile("<option value=\"(\\d+)\">(.+?)</option>");
    private static final Charset charset = Charset.forName("CP1251");

    private void setupUi() {
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentEmail = s.toString();
            }
        });

        final CharSequence[] types = new CharSequence[]{
                "Общепит",
                "Торговля",
                "Услуги",
                "Другое"
        };

        typeField.setAdapter(new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, types));
        typeField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCurrentType(types[position].toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setCurrentType(null);
            }
        });

        client.get(this, "http://bubuka.espepe.ru/users/demoplayer/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200) {
                    String response = new String(responseBody, charset);
                    Matcher matcher = regionPattern.matcher(response);
                    List<Region> regions = new ArrayList<Region>();
                    while(matcher.find()) {
                        int code = Integer.parseInt(matcher.group(1));
                        String name = matcher.group(2);
                        regions.add(new Region(name, code));
                    }

                    final RegionAdapter adapter = new RegionAdapter(RegisterActivity.this, regions);

                    regionField.setAdapter(adapter);
                    regionField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            setCurrentRegion(adapter.getRegion(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            setCurrentRegion(null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(RegisterActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private City currentCity = null;
    private Region currentRegion = null;
    private String currentType = null;
    private String currentEmail = null;

    private void setCurrentCity(City city) {
        this.currentCity = city;
    }

    private void setCurrentType(String currentType) {
        this.currentType = currentType;
    }

    private void setCurrentRegion(final Region region) {
        this.currentRegion = region;
        client.get(this, "http://bubuka.espepe.ru/users/?action=admin-side&subaction=get_city_list&region_id=" + region.getCode(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        JSONArray result = jsonObject.getJSONArray("result");
                        List<City> cities = new ArrayList<City>(result.length());
                        for(int i = 0; i < result.length(); i++) {
                            JSONObject cityObject = result.getJSONObject(i);
                            String name = cityObject.getString("name");
                            int id = cityObject.getInt("id");

                            cities.add(new City(name, id));
                        }

                        setCities(cities);
                    } catch (JSONException e) {
                        logger.warn("failed to parse json", e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void setCities(List<City> cities) {
        final CityAdapter adapter = new CityAdapter(this, cities);
        cityField.setAdapter(adapter);
        cityField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCurrentCity(adapter.getCity(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setCurrentCity(null);
            }
        });
    }

    private static class Region {
        private final String name;
        private final int code;

        private Region(String name, int code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }
    }

    private static class City {
        private final String name;
        private final int code;

        private City(String name, int code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }
    }

    public static class RegionAdapter extends ArrayAdapter<CharSequence> {
        private final List<Region> regions;
        public RegionAdapter(Context context, List<Region> objects) {
            super(context, android.R.layout.simple_spinner_dropdown_item, 0, toNames(objects));
            this.regions = objects;
        }

        private static List<CharSequence> toNames(List<Region> regions) {
            List<CharSequence> regionNames = new ArrayList<CharSequence>(regions.size());
            for(Region region : regions) {
                regionNames.add(region.getName());
            }
            return regionNames;
        }

        public Region getRegion(int position) {
            return regions.get(position);
        }
    }

    public static class CityAdapter extends ArrayAdapter<CharSequence> {
        private final List<City> cities;
        public CityAdapter(Context context, List<City> objects) {
            super(context, android.R.layout.simple_spinner_dropdown_item, 0, toNames(objects));
            this.cities = objects;
        }

        private static List<CharSequence> toNames(List<City> cities) {
            List<CharSequence> regionNames = new ArrayList<CharSequence>(cities.size());
            for(City city : cities) {
                regionNames.add(city.getName());
            }
            return regionNames;
        }

        public City getCity(int position) {
            return cities.get(position);
        }
    }

    @OnClick(R.id.register_startwork_button)
    public void startwork() {
        /*
        String text = "Email: " + currentEmail + "\n" +
                "Region: " + currentRegion.getName() + "\n" +
                "City: " + currentCity.getName() + "\n" +
                "Type: " + currentType;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        */

        if(
                   currentEmail == null
                || !currentEmail.contains("@")
                || currentRegion == null
                || currentCity == null
                || currentType == null) {
            Toast.makeText(this, "Необходимо заполнить все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.add("demoMail", currentEmail);
        params.add("demoReg", Integer.toString(currentRegion.getCode()));
        params.add("demoTown", Integer.toString(currentCity.getCode()));
        params.add("demoOrg", currentType);
        params.add("create_demo", "1");

        params.setContentEncoding("CP1251");

        client.post(this, "http://bubuka.espepe.ru/users/demoplayer/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Pattern responsePattern = Pattern.compile("<Code>(.+?)</Code>", Pattern.CASE_INSENSITIVE);
                Matcher matcher = responsePattern.matcher(response);
                if(matcher.find()) {
                    registerSuccess(matcher.group(1));
                } else {
                    failed();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                failed();
            }

            private void failed() {
                Toast.makeText(RegisterActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerSuccess(String objectCode) {
        BubukaApplication.getInstance().setObjectCode(objectCode);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

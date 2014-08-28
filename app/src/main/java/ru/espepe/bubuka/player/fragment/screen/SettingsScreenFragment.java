package ru.espepe.bubuka.player.fragment.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import ru.espepe.bubuka.player.BubukaApplication;
import ru.espepe.bubuka.player.R;

/**
 * Created by wolong on 28/08/14.
 */
public class SettingsScreenFragment extends Fragment {

    @InjectView(R.id.settings_refresh_period_field)
    protected EditText refreshPeriodField;

    @InjectView(R.id.settings_domain_field)
    protected EditText domainField;

    @InjectView(R.id.settings_objectcode_field)
    protected EditText objectCodeField;

    @OnTextChanged(R.id.settings_refresh_period_field)
    public void refreshPeriodChanged(CharSequence text, int start, int before, int count) {
        try {
            BubukaApplication.getInstance().getPreferences().setRefreshPeriod(Integer.parseInt(text.toString()));
        } catch (Exception e) {

        }
    }

    @OnTextChanged(R.id.settings_domain_field)
    public void domainChanged(CharSequence text, int start, int before, int count) {
        BubukaApplication.getInstance().setBubukaDomain(text.toString());
    }

    @OnTextChanged(R.id.settings_objectcode_field)
    public void objectCodeChanged(CharSequence text, int start, int before, int count) {
        BubukaApplication.getInstance().setObjectCode(text.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_settings, null);
        ButterKnife.inject(this, view);
        setupUi();
        return view;
    }

    private void setupUi() {
        refreshPeriodField.setText(Integer.toString(BubukaApplication.getInstance().getPreferences().getRefreshPeriod()));
        objectCodeField.setText(BubukaApplication.getInstance().getObjectCode());
        domainField.setText(BubukaApplication.getInstance().getBubukaDomain());
    }
}

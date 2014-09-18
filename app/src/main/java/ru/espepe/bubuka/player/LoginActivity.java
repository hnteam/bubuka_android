package ru.espepe.bubuka.player;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ru.espepe.bubuka.player.activity.RegisterActivity;

/**
 * Created by wolong on 26/08/14.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @InjectView(R.id.login_logo)
    protected ImageView loginLogo;

    @OnClick(R.id.login_goto_register_demo)
    public void registerDemo() {

        //WebView webView = new WebView(this);


        /*
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Регистрация демо-объекта");
        alert.setView(webView);
        alert.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alert.set
        alert.show();
        */

        /*
        Dialog dialog = new Dialog(this);
        dialog.setTitle("Регистрация демо-объекта");
        dialog.setContentView(R.layout.dialog_register_demo);
        dialog.setCancelable(true);


        WebView webView = (WebView) dialog.findViewById(R.id.login_register_demo_webview);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        webView.loadUrl("http://bubuka.espepe.ru/users/demoplayer/");

        dialog.show()
        */

        startActivity(new Intent(this, RegisterActivity.class));

    }

    @InjectView(R.id.login_objectcode_field)
    protected EditText objectCodeField;

    @OnClick(R.id.login_startwork_button)
    public void login() {
        String currentObjectCode = objectCodeField.getText().toString();
        if(currentObjectCode.isEmpty()) {
            Toast.makeText(this, "Необходим заполнить поле", Toast.LENGTH_SHORT).show();
            return;
        }

        BubukaApplication.getInstance().setObjectCode(currentObjectCode);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

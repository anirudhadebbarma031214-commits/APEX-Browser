package com.apex.browser;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

public class MainActivity extends Activity {

    WebView webView;
    EditText addressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(5, 7, 13));

        LinearLayout topBar = new LinearLayout(this);
        topBar.setPadding(12, 12, 12, 12);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        addressBar = new EditText(this);
        addressBar.setHint("Search or enter website");
        addressBar.setSingleLine(true);
        addressBar.setTextColor(Color.WHITE);
        addressBar.setHintTextColor(Color.GRAY);
        addressBar.setBackgroundColor(Color.rgb(20, 25, 35));

        Button goButton = new Button(this);
        goButton.setText("GO");

        topBar.addView(addressBar,
                new LinearLayout.LayoutParams(0, 60, 1));
        topBar.addView(goButton,
                new LinearLayout.LayoutParams(100, 60));

        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        root.addView(topBar);
        root.addView(webView,
                new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);

        webView.loadUrl("https://www.google.com");

        goButton.setOnClickListener(v -> openWebsite());

        addressBar.setOnEditorActionListener((v, actionId, event) -> {
            openWebsite();
            return true;
        });
    }

    private void openWebsite() {
        String text = addressBar.getText().toString().trim();

        if (text.isEmpty()) return;

        if (text.startsWith("http://") ||
            text.startsWith("https://")) {
            webView.loadUrl(text);
        } else if (text.contains(".")) {
            webView.loadUrl("https://" + text);
        } else {
            webView.loadUrl(
                "https://www.google.com/search?q=" +
                android.net.Uri.encode(text)
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

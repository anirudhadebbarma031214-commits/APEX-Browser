package com.anistudio.neonchat;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(9, 10, 22);
    private final int panel = Color.rgb(20, 22, 42);
    private final int cyan = Color.rgb(0, 229, 255);
    private final int purple = Color.rgb(163, 82, 255);
    private LinearLayout messages;
    private EditText input;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(12));
        root.setBackgroundColor(bg);
        setContentView(root);

        root.addView(label("NEON CHAT", 25, cyan, true));
        TextView tagline = label("YOUR SQUAD. YOUR SPACE.", 11, Color.LTGRAY, true);
        LinearLayout.LayoutParams tl = new LinearLayout.LayoutParams(-1, -2);
        tl.topMargin = dp(4);
        root.addView(tagline, tl);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(14), dp(12), dp(14), dp(12));
        header.setBackground(shape(panel, purple, 1, 18));
        LinearLayout.LayoutParams hl = new LinearLayout.LayoutParams(-1, -2);
        hl.topMargin = dp(20);
        root.addView(header, hl);

        TextView avatar = label("N", 22, Color.WHITE, true);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(shape(purple, purple, 0, 50));
        header.addView(avatar, new LinearLayout.LayoutParams(dp(44), dp(44)));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);
        header.addView(details, new LinearLayout.LayoutParams(0, -2, 1));
        details.addView(label("Neon Lounge", 17, Color.WHITE, true));
        details.addView(label("Demo conversation · Offline", 12, cyan, false));

        messages = new LinearLayout(this);
        messages.setOrientation(LinearLayout.VERTICAL);
        messages.setPadding(dp(4), dp(16), dp(4), dp(12));
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.addView(messages);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, 0, 1);
        sp.topMargin = dp(10);
        root.addView(scroll, sp);

        addMessage("SYSTEM", "Welcome to NEON CHAT. Demo messages stay on this device screen.", false);
        addMessage("YOU", "Ready to squad up? 🎮", true);
        addMessage("NEON BOT", "Neon systems online. Real-time chat can be added next.", false);

        LinearLayout composer = new LinearLayout(this);
        composer.setGravity(Gravity.CENTER_VERTICAL);
        composer.setPadding(dp(8), dp(6), dp(6), dp(6));
        composer.setBackground(shape(panel, cyan, 1, 24));
        root.addView(composer, new LinearLayout.LayoutParams(-1, -2));

        input = new EditText(this);
        input.setSingleLine(true);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);
        input.setHint("Type a message...");
        input.setTextSize(14);
        input.setPadding(dp(10), dp(8), dp(8), dp(8));
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setImeOptions(EditorInfo.IME_ACTION_SEND);
        composer.addView(input, new LinearLayout.LayoutParams(0, -2, 1));

        Button send = new Button(this);
        send.setText("SEND");
        send.setTextColor(bg);
        send.setTextSize(12);
        send.setBackground(shape(cyan, cyan, 0, 18));
        composer.addView(send, new LinearLayout.LayoutParams(dp(76), dp(44)));
        send.setOnClickListener(v -> sendMessage());
        input.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_SEND) { sendMessage(); return true; }
            return false;
        });
    }

    private void sendMessage() {
        String value = input.getText().toString().trim();
        if (!value.isEmpty()) {
            addMessage("YOU", value, true);
            input.setText("");
        }
    }

    private void addMessage(String sender, String body, boolean mine) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(13), dp(10), dp(13), dp(10));
        card.setBackground(shape(mine ? Color.rgb(36, 27, 66) : panel,
                mine ? purple : cyan, 1, 16));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                mine ? dp(280) : dp(300), -2);
        cp.gravity = mine ? Gravity.RIGHT : Gravity.LEFT;
        cp.bottomMargin = dp(12);
        card.addView(label(sender, 10, mine ? purple : cyan, true));
        TextView bodyView = label(body, 14, Color.WHITE, false);
        bodyView.setPadding(0, dp(5), 0, 0);
        card.addView(bodyView);
        messages.addView(card, cp);
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private GradientDrawable shape(int color, int stroke, int width, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (width > 0) d.setStroke(dp(width), stroke);
        return d;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}

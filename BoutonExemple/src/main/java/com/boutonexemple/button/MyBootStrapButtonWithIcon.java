package com.boutonexemple.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boutonexemple.R;

/**
 * Created by Anthony on 15/10/2014.
 */
public class MyBootStrapButtonWithIcon extends LinearLayout implements View.OnTouchListener {

    private TextView tv;
    private ImageView iv;
    private View root;

    private int textColorHighlight, textColor, backGroundColor, backGroundColorHighlight;

    public MyBootStrapButtonWithIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        setOnTouchListener(this);
    }

    // ce constructeur ne devrait jamais être appelé, car il n'a pas d'AttributeSet en paramètre.
    public MyBootStrapButtonWithIcon(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public MyBootStrapButtonWithIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setOnTouchListener(this);
    }

    private void init(Context ctx, AttributeSet attrs) {

        // inflation du modèle "customtitle", et initialisation des composants Button et ImageView
        // on cherche le service Android pour instancier des vues
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // on instancie notre vue customisée (celle créée dans l'étape 1, qui se trouve dans res/layout/customtitle)
        View v = li.inflate(R.layout.bootstrap_button_with_icon, null);
        root = v.findViewById(R.id.root);
        tv = (TextView) root.findViewById(R.id.tv);
        iv = (ImageView) root.findViewById(R.id.iv);
        addView(v);

        // Le modèle est chargé, on a plus qu'à l'initialiser avec les attributs qu'on a reçus en paramètre

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.stylableButton);

        // on obtient un TypedArray, une classe qui a plein de méthodes getString(int index),
        // getInteger(int index) (...) pour obtenir la valeur String, Integer (...) d'un attribut.

        // on vérifie que l'attribut "txt" n'est pas null
        if (a.getString(R.styleable.stylableButton_android_text) != null) {
            tv.setText(a.getString(R.styleable.stylableButton_android_text));
        }

        // et on recommence pour l'attribut "drawable"
        if (a.getDrawable(R.styleable.stylableButton_android_src) != null) {
            iv.setImageDrawable(a.getDrawable(R.styleable.stylableButton_android_src));
        }

        //les couleurs
        textColorHighlight = a.getInt(R.styleable.stylableButton_android_textColorHighlight, Color.BLACK);
        textColor = a.getInt(R.styleable.stylableButton_android_textColor, Color.BLACK);
        backGroundColor = a.getInt(R.styleable.stylableButton_backGroundColor, Color.BLACK);
        backGroundColorHighlight = a.getInt(R.styleable.stylableButton_backGroundColorHighlight, Color.BLACK);

        updateComposant();

        // on recycle, c'est pour sauver mère nature
        a.recycle();
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            root.getBackground().setColorFilter(backGroundColorHighlight, PorterDuff.Mode.MULTIPLY);
            iv.setColorFilter(textColorHighlight, PorterDuff.Mode.MULTIPLY);
            tv.setTextColor(textColorHighlight);
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            root.getBackground().setColorFilter(backGroundColor, PorterDuff.Mode.MULTIPLY);
            iv.setColorFilter(textColor, PorterDuff.Mode.MULTIPLY);
            tv.setTextColor(textColor);
        }
        return false;
    }

    /**
     * Permet de gerer les changements de couleur sur le ontouch
     */
    public void setColorFilter(final int textColor, final int textColorHighlight, int backGroundColor, int backGroundColorHighlight) {
        this.textColor = textColor;
        this.textColorHighlight = textColorHighlight;
        this.backGroundColor = backGroundColor;
        this.backGroundColorHighlight = backGroundColorHighlight;
        updateComposant();

    }

    private void updateComposant() {
        root.getBackground().setColorFilter(backGroundColor, PorterDuff.Mode.MULTIPLY);
        iv.setColorFilter(textColor, PorterDuff.Mode.MULTIPLY);
        tv.setTextColor(textColor);
    }

    public TextView getTv() {
        return tv;
    }

    public ImageView getIv() {
        return iv;
    }

}

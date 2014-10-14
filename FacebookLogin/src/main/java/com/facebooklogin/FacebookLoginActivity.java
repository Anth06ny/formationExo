/**
 * (C)opyright 2014 - UrbanPulse - All rights Reserved
 * Released by CARDIWEB
 * File : FacebookLoginActivity.java
 * @date 9 avr. 2014
 * @author Anthony
 */
package com.facebooklogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.facebooklogin.utils.ButtonHighlight;
import com.facebooklogin.utils.ToastUtils;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Anthony
 * Affiche une fenetre de popup facebook et de connexion Urbanpulse.
 */
public class FacebookLoginActivity extends Activity implements OnClickListener, OnLoginListener {

    public static final String PENDING_ACTION = "PENDING_ACTION";
    public static final String FRIENDS_ID = "FRIENDS_ID";
    public static final String MESSAGE_KEY = "MESSAGE_KEY";

    //compoosant graphique
    private View progressView;
    private ButtonHighlight bt_facebook_connect, bt_retry;
    private View rl_retry;
    private TextView tv_message;
    private TextView tv_facebook;

    //facebook
    private SimpleFacebook mSimpleFacebook;
    private PendingAction pendingAction;

    //ressources
    private int textColor, textColorErreur;

    //les action facebook
    public enum PendingAction {
        NONE(0), LOGIN(1), SHARE_UP(2), FIND_FRIEND(3), POST_FRIEND_WALL(4), POST_USER_WALL(5), LOG_OUT(6);

        public int value;

        private PendingAction(final int value) {
            this.value = value;
        }

        public static PendingAction getPendingAction(final int value) {
            if (value == LOGIN.value) {
                return LOGIN;
            }
            else if (value == SHARE_UP.value) {
                return SHARE_UP;
            }
            else if (value == FIND_FRIEND.value) {
                return FIND_FRIEND;
            }
            else if (value == POST_FRIEND_WALL.value) {
                return POST_FRIEND_WALL;
            }
            else if (value == POST_USER_WALL.value) {
                return POST_USER_WALL;
            }
            else if (value == LOG_OUT.value) {
                return LOG_OUT;
            }
            else {
                return NONE;
            }
        }
    }

    /*-----------------------------
    // view
    //-----------------------------*/

    /** @see android.app.Activity#onCreate(android.os.Bundle) */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_login_inscription);

        mSimpleFacebook = SimpleFacebook.getInstance(this);
        bt_facebook_connect = (ButtonHighlight) findViewById(R.id.bt_facebook_connect);
        bt_retry = (ButtonHighlight) findViewById(R.id.bt_retry);
        tv_message = (TextView) findViewById(R.id.tv_message);
        progressView = findViewById(R.id.progress);
        rl_retry = findViewById(R.id.rl_retry);
        tv_facebook = (TextView) findViewById(R.id.tv_facebook);

        textColor = getResources().getColor(R.color.black);
        textColorErreur = getResources().getColor(R.color.red);

        bt_facebook_connect.setColorFilter(getResources(), R.color.facebook_blue, R.color.facebook_blue_light);
        bt_retry.setColorFilter(getResources(), R.color.facebook_blue, R.color.facebook_blue_light);

        //quelle action on veut faire
        pendingAction = PendingAction.getPendingAction(getIntent().getIntExtra(PENDING_ACTION, PendingAction.NONE.value));
        if (pendingAction == null) {
            pendingAction = PendingAction.NONE;
        }

        //sinon on met la progress bar
        if (mSimpleFacebook.isLogin()) {
            showProgress(true);
            //on fait l'action en param
            pendingAction();
        }
        else if (pendingAction == PendingAction.LOGIN) {
            //on a deja validé qu'on voulait se loguer
            showProgress(true);
            //on fait l'action en param
            pendingAction();
        }
        else if (pendingAction == PendingAction.LOG_OUT) {
            //on est déjà deco
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        mSimpleFacebook.eventAppLaunched();
        updateButton();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        showProgress(false);
        super.onActivityResult(requestCode, resultCode, data);
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
    }

    /** @see android.app.Activity#onBackPressed() */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* --------------------------
    // click touch
    //-------------------------- */

    /** @see android.view.View.OnClickListener#onClick(android.view.View) */
    @Override
    public void onClick(final View v) {
        setMessage("", 0);

        //connexion avec facebook
        if (v.getId() == R.id.bt_facebook_connect) {
            if (mSimpleFacebook.isLogin()) {
                logOut();
            }
            else {
                showProgress(true);
                mSimpleFacebook.login(this);
            }
        }
        //relance la dernière action
        else if (v.getId() == R.id.bt_retry) {
            showProgress(true);
            pendingAction();
        }
    }

    /*----------------------
    // traitement de retour des appel fb
    //---------------------- */
    /** @see com.sromku.simple.fb.listeners.OnLoginListener#onLogin() */
    @Override
    public void onLogin() {
        setMessage(getResources().getString(R.string.com_facebook_loginview_logged_in_using_facebook), textColor);
        //pour que le bus ait le temps de traiter le message

        if (pendingAction == PendingAction.LOGIN) {
            pendingAction = PendingAction.NONE;
        }

        pendingAction();
    }

    @Override
    public void onFail(final String reason) {
        setMessage(reason, getResources().getColor(R.color.red));
        showProgress(false);
        updateButton();
    }

    @Override
    public void onException(final Throwable throwable) {
        String message = FacebookLoginActivity.this.getResources().getString(R.string.error_try_later);
        Log.e("FacebookLogin", Log.getStackTraceString(throwable));
        message += "\nException : " + throwable.getMessage();

        setMessage(message, textColorErreur);
        showProgress(false);
        updateButton();
    }

    @Override
    public void onThinking() {
        setMessage(getResources().getString(R.string.may_take_several_minutes), textColor);
        showProgress(true);
    }

    /** @see com.sromku.simple.fb.listeners.OnLoginListener#onNotAcceptingPermissions(com.sromku.simple.fb.Permission.Type) */
    @Override
    public void onNotAcceptingPermissions(final Type type) {
        setMessage(getResources().getString(R.string.com_facebook_requesterror_permissions), textColorErreur);
        showProgress(false);
        updateButton();
    }

    /* ------------------------
    //FaceBookAction
    //------------------------ */
    private void pendingAction() {

        switch (pendingAction) {
            case LOGIN:
                if (mSimpleFacebook.isLogin()) {
                    mSimpleFacebook.logout(null);
                }
                mSimpleFacebook.login(this);
                break;
            case SHARE_UP:
                share_up_action();
                break;
            case FIND_FRIEND:
                findFriendsInFacebook();
                break;
            case POST_FRIEND_WALL:
                postOnFriendWall();
                break;
            case POST_USER_WALL:
                postOnUserWall();
                break;
            case LOG_OUT:
                logOut();
                break;
            case NONE:
                showProgress(false);
                break;
        }

    }

    private void share_up_action() {

        final Feed feed = new Feed.Builder().setDescription(getString(R.string.tab_friends_invitations_fb_text))
                .setName(getString(R.string.app_name)).setPicture("http://www.amonteiro.fr/images/entreprise/cv-recto.png")
                .setLink("http://www.amonteiro.fr").build();

        mSimpleFacebook.publish(feed, true, new OnPublishListener() {
            @Override
            public void onFail(final String reason) {
                FacebookLoginActivity.this.onFail(reason);
            }

            @Override
            public void onException(final Throwable throwable) {
                FacebookLoginActivity.this.onException(throwable);
            }

            @Override
            public void onThinking() {
                setMessage(getResources().getString(R.string.facebook_post_progress_body), textColor);
                showProgress(true);
            }

            @Override
            public void onComplete(final String postId) {
                ToastUtils.showToastOnUIThread(FacebookLoginActivity.this, R.string.facebook_stub_post_title);
                updateButton();
            }
        });
    }

    private void findFriendsInFacebook() {

        mSimpleFacebook.getFriends(new OnFriendsListener() {

            @Override
            public void onComplete(final List<Profile> friends) {
                //on transmet à la classe qui appelle les amis.
                updateButton();
            }

            /** @see com.sromku.simple.fb.listeners.OnActionListener#onException(Throwable) */
            @Override
            public void onException(final Throwable throwable) {
                FacebookLoginActivity.this.onException(throwable);
                setBt_retry(true);
            }

            /** @see com.sromku.simple.fb.listeners.OnActionListener#onThinking() */
            @Override
            public void onThinking() {
                FacebookLoginActivity.this.onThinking();
            }

            @Override
            public void onFail(final String reason) {
                FacebookLoginActivity.this.onFail(reason);
                bt_retry.setVisibility(View.VISIBLE);
            }

        });
    }

    private void postOnFriendWall() {

        final String friendId = getIntent().getStringExtra(FRIENDS_ID);
        final String message = getIntent().getStringExtra(MESSAGE_KEY);

        if (StringUtils.isBlank(friendId)) {
            ToastUtils.showToastOnUIThread(FacebookLoginActivity.this, "(Dev)Le friends Id n'a pas été transmit");
        }
        else if (StringUtils.isBlank(message)) {
            ToastUtils.showToastOnUIThread(FacebookLoginActivity.this, "(Dev)Le message est vide");
        }
        else {
            Log.d("facebook", "### postOnFriendWall #####");

            try {
                final Bundle params = new Bundle();
                params.putString("name", getString(R.string.app_name));//title
                params.putString("description", getResources().getString(R.string.tab_friends_invitations_fb_text));
                params.putString("to", friendId);
                params.putString("link", "http://www.amonteiro.fr");
                params.putString("picture", "http://www.amonteiro.fr/images/entreprise/cv-recto.png");

                final WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), params)).setOnCompleteListener(
                        new OnCompleteListener() {

                            @Override
                            public void onComplete(final Bundle values, final FacebookException error) {
                                ToastUtils.showToastOnUIThread(FacebookLoginActivity.this, R.string.facebook_stub_post_friend_title);
                            }

                        }).build();
                feedDialog.show();
            }
            catch (final Exception e) {
                onException(e);
                setBt_retry(true);
            }

        }
    }

    private void postOnUserWall() {

        final String message = getIntent().getStringExtra(MESSAGE_KEY);

        if (StringUtils.isBlank(message)) {
            ToastUtils.showToastOnUIThread(FacebookLoginActivity.this, "(Dev)Le message est vide");
        }
        else {

            final Feed feed = new Feed.Builder().setDescription(message).setName(getString(R.string.app_name)).setPicture("http://www.amonteiro.fr")
                    .setLink("http://www.amonteiro.fr").build();

            Log.d("facebook", " ### postOnUserWall #####");

            mSimpleFacebook.publish(feed, true, new OnPublishListener() {
                @Override
                public void onFail(final String reason) {
                    FacebookLoginActivity.this.onFail(reason);
                }

                @Override
                public void onException(final Throwable throwable) {
                    FacebookLoginActivity.this.onException(throwable);
                }

                @Override
                public void onThinking() {
                    setMessage(getResources().getString(R.string.facebook_post_progress_body), textColor);
                    showProgress(true);
                }

                @Override
                public void onComplete(final String postId) {
                    ToastUtils.showToastOnUIThread(FacebookLoginActivity.this, R.string.facebook_stub_post_title);
                    updateButton();
                }
            });
        }
    }

    private void logOut() {

        mSimpleFacebook.logout(new OnLogoutListener() {

            @Override
            public void onFail(final String reason) {
                FacebookLoginActivity.this.onFail(reason);
            }

            @Override
            public void onException(final Throwable throwable) {
                FacebookLoginActivity.this.onException(throwable);
            }

            @Override
            public void onThinking() {
                FacebookLoginActivity.this.onThinking();
            }

            @Override
            public void onLogout() {
                updateButton();
            }
        });

    }

    /* ------------------------
    //Update interface graphique
    //------------------------ */
    private void updateButton() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tv_facebook.setText(mSimpleFacebook.isLogin() ? getString(R.string.com_facebook_loginview_log_out_button)
                        : getString(R.string.com_facebook_loginview_log_in_button));
            }
        });
    }

    private void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                bt_facebook_connect.setEnabled(!show);
                if (show) {
                    tv_message.setText(getResources().getString(R.string.may_take_several_minutes));
                    tv_message.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
    }

    private void setMessage(final String message, final int colorId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tv_message.setText(message);
                tv_message.setTextColor(colorId);
            }
        });
    }

    private void setBt_retry(final boolean show) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                rl_retry.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}

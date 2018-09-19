package com.mysoft.debug_tools.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mysoft.debug_tools.R;
import com.mysoft.debug_tools.ui.connector.SimpleAnimationListener;
import com.mysoft.debug_tools.ui.connector.UIStateCallback;
import com.mysoft.debug_tools.ui.view.SwipeBackLayout;


/**
 * Created by linjiang on 31/05/2018.
 */
abstract class BaseFragment extends Fragment implements SwipeBackLayout.DismissCallback {
    protected static final String PARAM1 = "param1";
    protected static final String PARAM2 = "param2";
    protected static final String PARAM3 = "param3";
    protected static final String PARAM4 = "param4";
    protected static final String PARAM_TITLE = "param_title";

    public BaseFragment() {
        setArguments(new Bundle());
    }

    protected final void launch(Class<? extends BaseFragment> target, Bundle extra) {
        launch(target, null, extra);
    }

    protected final void launch(Class<? extends BaseFragment> target, String title) {
        launch(target, title, new Bundle());
    }

    protected final void launch(Class<? extends BaseFragment> target, String title, Bundle extra) {
        if (getActivity() == null) {
            return;
        }
        closeSoftInput();
        if (extra == null) {
            extra = new Bundle();
        }
        extra.putString(PARAM_TITLE, title);
        try {
            Fragment fragment = target.newInstance();
            fragment.setArguments(extra);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right_, 0, 0, R.anim.slide_out_right_)
                    .add(R.id.fragment_container_id, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private Toolbar toolbar;
    private UIStateCallback uiState;
    private TextView tvError;
    protected Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UIStateCallback) {
            uiState = (UIStateCallback) context;
        }
        activity = getActivity();
        // BUG FIX
        // setArguments is not allowed when fragment is active,
        // it is related to the diff in the version of the fragment.
    }

    @Override
    public void onDetach() {
        super.onDetach();
        uiState = null;
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutView();
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);
        }
        view = afterInflateAndBeforeAny(view);
        View finalView = installToolbar(view);
        finalView = installSwipe(finalView);
        // Because we does not hide any Fragment,
        // this prevents triggering of non-stack Fragment View events
        finalView.setClickable(true);
        return finalView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (uiState != null) {
            // Do not remove but choose to go out,
            // otherwise it will crash during animation
            uiState.hideHint();
        }
    }

    @Override
    public final Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0 || !enter) {
            if (enter) {
                if (getView() != null) {
                    onViewEnterAnimEnd(getView());
                }
            }
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
        Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        anim.setAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                // Closes the fragment when the animation has not finished yet,
                // causing getView() is null
                if (getView() != null) {
                    onViewEnterAnimEnd(getView());
                }
            }
        });
        return anim;
    }

    @Override
    public void onDismiss() {
        if (!isDetached()) {
            // 当滑动到满足滑动关闭阈值的地方，无论是否松手，此时同时又点击了物理back键，
            // 就会导致先后两次pop，back先pop，导致后一次pop即这里的getFragmentManager为null
            if (getFragmentManager() != null) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    try {
                        getFragmentManager().popBackStackImmediate();
                    } catch (Throwable ignore) {
                    }
                } else {
                    onBackPressed();
                }
            }
        }
    }

    protected abstract @LayoutRes
    int getLayoutId();

    protected View getLayoutView() {
        return null;
    }

    protected boolean enableToolbar() {
        return true;
    }

    protected boolean enableSwipeBack() {
        return true;
    }

    /**
     * Provide an opportunity to start asynchronous tasks<p>
     * If we perform an asynchronous task in onViewCreated and the task is completed before
     * the animation completes. At this point, the padding data triggers the re-measure
     * and causes an animation exception.<p>
     * <p>
     * Callback when the fragment animation ends, later than onViewCreated
     */
    protected void onViewEnterAnimEnd(View container) {

    }

    private View installSwipe(View content) {
        SwipeBackLayout swipeBackLayout = new SwipeBackLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        swipeBackLayout.setLayoutParams(params);
        swipeBackLayout.setBackgroundColor(Color.TRANSPARENT);
        swipeBackLayout.attach(this, content);
        swipeBackLayout.enableGesture(enableSwipeBack());
        return swipeBackLayout;
    }

    private View installToolbar(View view) {
        if (!enableToolbar() || getArguments() == null) {
            return view;
        }
        toolbar = (Toolbar) View.inflate(activity, R.layout.layout_toolbar, null);
        toolbar.inflateMenu(R.menu.menu_common);
        toolbar.setTitle(getArguments().getString(PARAM_TITLE, "TITLE"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RelativeLayout layout = new RelativeLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        ViewGroup.LayoutParams toolbarParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(toolbar, toolbarParams);

        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlParams.addRule(RelativeLayout.BELOW, toolbar.getId());
        layout.addView(view, rlParams);

        return layout;
    }

    protected View afterInflateAndBeforeAny(View view) {
        return view;
    }

    protected final Toolbar getToolbar() {
        return toolbar;
    }

    protected final void onBackPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    protected final void openSoftInput() {
        if (getContext() == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        } catch (Throwable ignore) {

        }
    }

    protected final void closeSoftInput() {
        if (getContext() == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        } catch (Throwable ignore) {
        }
    }

    protected final void showLoading() {
        if (uiState != null) {
            uiState.showHint();
        }
    }

    protected final void hideLoading() {
        if (uiState != null) {
            uiState.hideHint();
        }
    }

    protected final void showError(String msg) {
        hideLoading();
        if (tvError == null) {
            tvError = new TextView(getContext());
            tvError.setGravity(Gravity.CENTER);
            tvError.setTextSize(16);
            tvError.setTextColor(Color.parseColor("#6D6D72"));
            tvError.setBackgroundColor(ContextCompat.getColor(activity, R.color.main_bg));
            tvError.setClickable(true);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.BELOW, toolbar.getId());
            ((RelativeLayout) toolbar.getParent()).addView(tvError, rlParams);
        }
        if (tvError.getVisibility() != View.VISIBLE) {
            tvError.setVisibility(View.VISIBLE);
        }
        tvError.setText(TextUtils.isEmpty(msg) ? "EMPTY" : msg);
    }

    protected final void hideError() {
        if (tvError != null) {
            tvError.setVisibility(View.GONE);
        }
    }
}

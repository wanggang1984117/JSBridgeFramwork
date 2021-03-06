package com.xiaoying.h5core.view;

import com.xiaoying.h5api.api.H5Page;
import com.xiaoying.h5api.util.H5Environment;
import com.xiaoying.h5api.util.H5Log;
import com.xiaoying.h5api.util.H5Utils;
import com.xiaoying.h5core.R;
import com.xiaoying.h5core.env.H5Container;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

public class H5NavMenu extends H5PopMenu {

    public static final String TAG = "H5NavMenu";
    private int xOffset, yOffset;

    public H5NavMenu(H5Page page) {
        super(page);
    }

    public void initMenu() {
        Resources resources = H5Environment.getResources();
        menuList = new ArrayList<MenuItem>();

        menuList.add(new MenuItem(resources.getString(R.string.menu_font),
                H5Container.MENU_FONT, resources
                .getDrawable(R.drawable.h5_nav_font), false));
        menuList.add(new MenuItem(resources.getString(R.string.menu_open_in_browser),
                H5Container.MENU_OPEN_IN_BROWSER, resources.getDrawable(R.drawable.h5_nav_browse_enable), false));
        menuList.add(new MenuItem(resources.getString(R.string.menu_copy),
                H5Container.MENU_COPY, resources
                .getDrawable(R.drawable.h5_nav_copy), false));
        menuList.add(new MenuItem(resources.getString(R.string.menu_refresh),
                H5Container.MENU_REFRESH, resources
                .getDrawable(R.drawable.h5_nav_refresh), false));
    }

    public void showMenu(View anchor) {
        if (popupWindow != null && popupWindow.isShowing()) {
            H5Log.d(TAG, "menu is showing!");
            return;
        }

        if (menuUpdated || popupWindow == null) {
            Context context = anchor.getContext();
            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setVerticalScrollBarEnabled(true);
            container.setOnClickListener(onClickListener);

            int containerWidth = 0;
            final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            for (int index = 0; index < menuList.size(); index++) {
                if (index != 0) {
                    View divider = new View(container.getContext());
                    divider.setBackgroundResource(R.color.h5_nav_menu_divider);
                    container.addView(divider, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                }
                View item = getItemView(index, container);
                item.measure(widthMeasureSpec, heightMeasureSpec);
                int width = item.getMeasuredWidth();
                containerWidth = containerWidth > width ? containerWidth : width;
                container.addView(item);
            }

            Resources resources = H5Environment.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            int screenWidth = dm.widthPixels;

            Drawable background = resources.getDrawable(R.drawable.h5_nav_menu_bg);
            Rect padding = new Rect();
            background.getPadding(padding);

            int width = containerWidth;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            xOffset = screenWidth - width - H5Utils.dip2px(8);
            yOffset = -padding.top;

            container.setBackgroundResource(R.drawable.h5_nav_menu_bg);
            container.setPadding(0, padding.top, 0, 0);
            popupWindow = new PopupWindow(container, width, height, true);
            // if background not set, back key will not work
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);

            popupWindow.setClippingEnabled(false);
        }
        Activity activity = null;
        if (popupWindow.getContentView() != null
                && popupWindow.getContentView().getContext() != null) {
            activity = (Activity) popupWindow.getContentView().getContext();
            if (!activity.isFinishing()) {
                popupWindow.showAsDropDown(anchor, xOffset, yOffset);
            } else {
                H5Log.w(TAG, "Activity is finishing, PopupWindow.showAsDropDown() is ignored!");
            }
        }
    }

    private View getItemView(int position, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View content = inflater.inflate(R.layout.h5_nav_menu_item, parent,
                false);
        content.setTag(position);
        content.setOnClickListener(onClickListener);

        MenuItem item = menuList.get(position);
        TextView tvTitle = (TextView) content.findViewById(R.id.tv_title);

        tvTitle.setText(item.name);

        if (item.icon != null) {
            ImageView ivIcon = (ImageView) content.findViewById(R.id.iv_icon);
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageDrawable(item.icon);
        }
        return content;
    }

}

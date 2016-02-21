package com.example.qingunext.app.page_home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.example.qingunext.app.QingUApp;
import com.example.qingunext.app.R;
import com.example.qingunext.app.page_board.BoardActivity;

/**
 * Created by Voyager on 9/20/2015.
 */
public class SectionListFragment extends Fragment {
    private ExpandableListView mExpandableListView;

    public static SectionListFragment newInstance() {
        return new SectionListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mExpandableListView = new ExpandableListView(getActivity());
        return mExpandableListView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mExpandableListView.setOnGroupExpandListener(groupPosition -> {
            for (int i = 0; i < mExpandableListView.getAdapter().getCount(); i++) {
                if (i == groupPosition) continue;
                mExpandableListView.collapseGroup(i);
            }
        });
        mExpandableListView.setPadding(30, 0, 30, 0);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setChildDivider(null);
        mExpandableListView.setDividerHeight(0);
        mExpandableListView.setAdapter(new GenericBaseExpandableListAdapter<String, String>() {
            String[] stringArray = getResources().getStringArray(R.array.sections2);

            @Override
            public int getGroupCount() {
                return stringArray.length;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                String arrayResName = "section_" + groupPosition;
                int identifier = getResources().getIdentifier(arrayResName, "array", "com.example.qingunext.app");
                return getResources().getStringArray(identifier).length;
            }

            @Override
            public String getGroup(int groupPosition) {
                return stringArray[groupPosition];
            }

            @Override
            public String getChild(int groupPosition, int childPosition) {
                String arrayResName = "section_" + groupPosition;
                int identifier = getResources().getIdentifier(arrayResName, "array", "com.example.qingunext.app");
                return getResources().getStringArray(identifier)[childPosition];
            }

            @Override
            public long getGroupId(int groupPosition) {
                return super.getGroupId(groupPosition);
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return super.getChildId(groupPosition, childPosition);
            }

            @Override
            public boolean hasStableIds() {
                return super.hasStableIds();
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                // todo viewHolder
                TextView textView;
                if (convertView == null) {
                    textView = ((TextView) getLayoutInflater(null).inflate(R.layout.item_exp_group, parent, false));
                } else {
                    textView = ((TextView) convertView);
                }
                textView.setText(getGroup(groupPosition));
                textView.setPadding(20, 20, 20, 20);
                textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
                return textView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    textView = new QuoteTextView(getActivity());
                } else {
                    textView = (TextView) convertView;
                }
                textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Large);
                textView.setTextSize(20f);
                textView.setText(QingUApp.getInstance().getBoardFullDescription(getChild(groupPosition, childPosition)));
                textView.setOnClickListener(v -> BoardActivity.from(getActivity(), getChild(groupPosition, childPosition)));
                return textView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return false;
            }
        });

    }


    public static class QuoteTextView extends TextView {
        Paint mPaint = new Paint();
        Paint mPaint2 = new Paint();

        {
            mPaint.setStrokeWidth(8);
            mPaint.setColor(Color.BLUE);
            setPadding(100 + getPaddingLeft(), getPaddingTop() + 20, getPaddingRight() + 10, getPaddingBottom() + 20);
            mPaint2.setColor(Color.LTGRAY);
            mPaint2.setStrokeWidth(2);
        }

        public QuoteTextView(Context context) {
            super(context);
        }

        public QuoteTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public QuoteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawLine(25, 0, 25, getHeight(), mPaint);
            canvas.drawLine(100, getHeight() - 2, 100 + getText().length() * getTextSize(), getHeight() - 2, mPaint2);
        }
    }
}

package com.example.qingunext.app.pages_other;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qingunext.app.R;
import com.example.qingunext.app.datamodel.User;

/**
 * Created by Rye on 2/22/2015.
 */
public class UserFragment extends Fragment {
    private UserSupplier supplier;
    private User user;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        supplier = (UserSupplier) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_user, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = supplier.getUser();
        TextView textView = (TextView) view.findViewById(R.id.tvUserPageInfo);
        textView.setText(user.toString().replace(",", "\n"));
        ImageView imageView = (ImageView) view.findViewById(R.id.ivUserPageFace);
      /*  if (user.getFace() == null) {
            imageView.setImageResource(R.drawable.ic_launcher);
        }*///todo
        Button button = (Button) view.findViewById(R.id.bnUserPageSend);
        button.setOnClickListener(v -> InputActivity.InputIntent.startSendMailIntent(getActivity(), user.getId()));
    }

    public interface UserSupplier {
        User getUser();
    }

}

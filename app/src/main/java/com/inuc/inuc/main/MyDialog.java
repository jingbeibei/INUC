package com.inuc.inuc.main;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inuc.inuc.R;

/**
 * Created by 景贝贝 on 2016/9/21.
 */
public class MyDialog extends Dialog {
    private Context context;
    public MyDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    protected MyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog);


    }

}

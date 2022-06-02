package winapp.hajikadir.customer.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import winapp.hajikadir.customer.R;

/**
 * Created by user on 14-Jul-16.
 */
public class UIHelper {
    private Context context;
    public UIHelper( Context context) {
        this.context = context;
    }
    public void showErrorDialog(String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_error);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        dialog.setCancelable(true);
        TextView text = (TextView) dialog.findViewById(R.id.message);
        text.setText(message);
        ImageView close = (ImageView) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();

    }
    public void showShortToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public void showShortToast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
